package test;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.parse.CTranslate;
import com.jcsa.jcparse.lopt.analysis.flow.CDependenceControlEdge;
import com.jcsa.jcparse.lopt.analysis.flow.CDependenceDataEdge;
import com.jcsa.jcparse.lopt.analysis.flow.CDependenceEdge;
import com.jcsa.jcparse.lopt.analysis.flow.CDependenceGraph;
import com.jcsa.jcparse.lopt.analysis.flow.CDependenceNode;
import com.jcsa.jcparse.lopt.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.lopt.context.CirFunctionCallPathType;
import com.jcsa.jcparse.lopt.context.CirFunctionCallTreeNode;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

public class CDependenceTest {
	
	protected static final String prefix = "D:/SourceCode/MyData/CODE2/gfiles/";
	protected static final String postfx = "result/";
	
	public static void main(String[] args) {
		for(File file : new File(prefix).listFiles()) {
			try {
				testing(file);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/* basic methods */
	private static AstTree parse(File file) throws Exception {
		return CTranslate.parse(file, ClangStandard.gnu_c89);
	}
	private static CirTree parse(AstTree ast_tree) throws Exception {
		return CTranslate.parse(ast_tree);
	}
	private static CirCallContextInstanceGraph translate(CirTree cir_tree) throws Exception {
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		return CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
	}
	private static CDependenceGraph generate(CirInstanceGraph program_graph) throws Exception {
		return CDependenceGraph.graph(program_graph);
	}
	private static void output(CirInstanceGraph program_graph, CDependenceGraph dependence_graph, 
			CirFunctionCallTreeNode context, FileWriter writer) throws Exception {
		writer.write("FUNCTION\t");
		writer.write(context.toString());
		writer.write("\n");
		
		CirExecutionFlowGraph flow_graph = context.get_function().get_flow_graph();
		for(int k = 1; k <= flow_graph.size(); k++) {
			CirExecution statement = flow_graph.get_execution(k % flow_graph.size());
			if(program_graph.has_instance(context, statement)) {
				CirInstanceNode instance = program_graph.get_instance(context, statement);
				
				writer.write("\t");
				writer.write(instance.get_execution().toString());
				writer.write("\t");
				writer.write(instance.get_execution().get_statement().generate_code());
				writer.write("\n");
				
				CDependenceNode source = dependence_graph.get_node(instance);
				for(CDependenceEdge edge : source.get_ou_edges()) {
					writer.write("\t--> ");
					if(edge instanceof CDependenceControlEdge) {
						CDependenceControlEdge cedge = (CDependenceControlEdge) edge;
						writer.write("[" + cedge.get_predicate_value() + "]--> \t");
						writer.write(cedge.get_target().get_execution().toString());
						writer.write("\n");
					}
					else if(edge instanceof CDependenceDataEdge) {
						CDependenceDataEdge dedge = (CDependenceDataEdge) edge;
						if(dedge.is_define_use())
							 writer.write("[def_use]-->\t");
						else writer.write("[use_def]-->\t");
						
						writer.write(dedge.get_target().get_instance().get_execution().toString());
						writer.write("\t");
						writer.write("{");
						writer.write(dedge.get_source_reference().generate_code());
						writer.write("} --> {");
						writer.write(dedge.get_target_reference().generate_code());
						writer.write("}\n");
					}
				}
			}
		}
		
		writer.write('\n');
	}
	private static void output(CirCallContextInstanceGraph program_graph, 
			CDependenceGraph dependence_graph, File file) throws Exception {
		Queue<CirFunctionCallTreeNode> queue = new LinkedList<CirFunctionCallTreeNode>();
		queue.add(program_graph.get_call_tree().get_root());
		
		FileWriter writer = new FileWriter(file);
		while(!queue.isEmpty()) {
			CirFunctionCallTreeNode tree_node = queue.poll();
			for(CirFunctionCallTreeNode child : tree_node.get_children()) {
				queue.add(child);
			}
			output(program_graph, dependence_graph, tree_node, writer);
		}
		writer.close();
	}
	private static void testing(File file) throws Exception {
		System.out.println("Testing " + file.getName());
		
		AstTree ast_tree = parse(file);
		System.out.println("\t(1) parsing to AST tree");
		
		CirTree cir_tree = parse(ast_tree);
		System.out.println("\t(2) parsing to CIR tree");
		
		CirCallContextInstanceGraph program_graph = translate(cir_tree);
		System.out.println("\t(3) translate to flow graph (" + program_graph.size() + ")");
		
		CDependenceGraph dependence_graph = generate(program_graph);
		System.out.println("\t(4) generate dependence graph (" + dependence_graph.size() + ")");
		
		output(program_graph, dependence_graph, new File(postfx + "dep/" + file.getName() + ".txt"));
		System.out.println("\t(5) output the dependence graph to the output file");
		
		System.out.println();
	}
	
}
