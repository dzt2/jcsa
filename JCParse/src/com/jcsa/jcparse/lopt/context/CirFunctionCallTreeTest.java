package com.jcsa.jcparse.lopt.context;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.parse.CTranslate;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceEdge;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

public class CirFunctionCallTreeTest {
	
	protected static final String prefix = "D:\\SourceCode\\MyData\\CODE2\\ifiles";
	protected static final String postfx = "result/";
	
	public static void main(String[] args) throws Exception {
		for(File file : new File(prefix).listFiles()) {
			try {
				testing(file);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private static void testing(File file) throws Exception {
		System.out.println("Testing " + file.getName());
		
		CirTree tree = parse(file);
		System.out.println("\t1. parse the program file");
		
		CirCallContextInstanceGraph graph = translate(tree);
		System.out.println("\t2. translate to function tree");
		
		output(graph, new File(postfx + "fun/" + file.getName() + ".txt"));
		System.out.println("\t3. output function call tree");
		
		System.out.print("\t4. HEAD nodes in graph are: { ");
		for(CirInstanceNode head : graph.get_heads()) {
			System.out.print(head.get_execution().toString() + "; ");
		}
		System.out.println("}");
		System.out.print("\t5. TAIL nodes in graph are: { ");
		for(CirInstanceNode tail : graph.get_tails()) {
			System.out.print(tail.get_execution().toString() + "; ");
		}
		System.out.println("}");
		
		System.out.println();
	}
	private static CirTree parse(File file) throws Exception {
		AstTree ast_tree = CTranslate.parse(file, ClangStandard.gnu_c89);
		return CTranslate.parse(ast_tree);
	}
	private static CirCallContextInstanceGraph translate(CirTree tree) throws Exception {
		return CirCallContextInstanceGraph.graph(
				tree.get_function_call_graph().get_function("main"), 
				CirFunctionCallPathType.simple_path, -1);
	}
	private static void output(CirInstanceNode node, FileWriter writer) throws Exception {
		CirExecution execution = node.get_execution();
		
		writer.write("\t" + execution.toString());
		writer.write("\t" + execution.get_type());
		if(execution.get_statement() == null)
			writer.write("\t#NULL");
		else writer.write("\t" + execution.
				get_statement().generate_code());
		writer.write("\n");
		
		Iterable<CirInstanceEdge> edges = node.get_ou_edges();
		for(CirInstanceEdge edge : edges) {
			CirInstanceNode target = edge.get_target();
			CirExecution target_execution = target.get_execution();
			CirFunctionCallTreeNode target_context = 
					(CirFunctionCallTreeNode) target.get_context();
			
			writer.write("\t");
			switch(edge.get_type()) {
			case call_flow:
				writer.write("+==>\t");
				writer.write(edge.get_type().toString());
				writer.write("\t");
				writer.write(target_execution.toString());
				writer.write("\t{");
				writer.write(target_context.toString());
				writer.write("}");
				break;
			case retr_flow:
				writer.write("-==>\t");
				writer.write(edge.get_type().toString());
				writer.write("\t");
				writer.write(target_execution.toString());
				writer.write("\t{");
				writer.write(target_context.toString());
				writer.write("}");
				break;
			default:
				writer.write("===>\t");
				writer.write(edge.get_type().toString());
				writer.write("\t");
				writer.write(target_execution.toString());
				break;
			}
			writer.write("\n");
		}
	}
	private static void output(CirCallContextInstanceGraph graph, 
			CirFunctionCallTreeNode context, FileWriter writer) throws Exception {
		CirExecutionFlowGraph flow_graph = context.get_function().get_flow_graph();
		
		writer.write("FUNCTION\t");
		writer.write(context.get_function().get_name() + "\t");
		writer.write("{ ");
		writer.write(context.toString());
		writer.write(" }\n");
		
		for(int k = 1; k <= flow_graph.size(); k++) {
			CirExecution execution = flow_graph.get_execution(k%flow_graph.size());
			if(execution.is_reachable()) {
				CirInstanceNode node = graph.get_execution(context, execution);
				output(node, writer); writer.write("\n");
			}
		}
	}
	private static void output(CirCallContextInstanceGraph graph, File output) throws Exception {
		Iterable<CirFunctionCallTreeNode> contexts = graph.get_call_contexts();
		FileWriter writer = new FileWriter(output);
		for(CirFunctionCallTreeNode context : contexts) {
			output(graph, context, writer);
		}
		writer.close();
	}
	
}
