package test;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lopt.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.lopt.context.CirFunctionCallPathType;
import com.jcsa.jcparse.lopt.context.CirFunctionCallTreeNode;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;
import com.jcsa.jcparse.lopt.models.defuse.CDefineUseEdge;
import com.jcsa.jcparse.lopt.models.defuse.CDefineUseGraph;
import com.jcsa.jcparse.lopt.models.defuse.CDefineUseNode;

public class CDefineUseTest {
	
	protected static final String prefix = "D:/SourceCode/MyData/CODE2/gfiles/";
	protected static final String postfx = "result/";
	protected static final File template_file = new File("config/cruntime.txt");
	
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
	private static AstCirFile parse(File file) throws Exception {
		return AstCirFile.parse(file, template_file, ClangStandard.gnu_c89);
	}
	private static CirCallContextInstanceGraph translate(CirTree cir_tree) throws Exception {
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		return CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
	}
	private static CDefineUseGraph define_use_graph(CirInstanceGraph input) throws Exception {
		return CDefineUseGraph.define_use_graph(input);
	}
	private static void output(CirCallContextInstanceGraph program_graph, CDefineUseGraph 
			define_use_graph, CirFunctionCallTreeNode tree_node, FileWriter writer) throws Exception {
		writer.write("FUNCTION\t");
		writer.write(tree_node.toString());
		writer.write("\n");
		
		CirExecutionFlowGraph flow_graph = tree_node.get_function().get_flow_graph();
		for(int k = 1; k <= flow_graph.size(); k++) {
			CirExecution execution = flow_graph.get_execution(k % flow_graph.size());
			if(program_graph.has_instance(tree_node, execution)) {
				CirInstanceNode instance = program_graph.get_instance(tree_node, execution);
				if(define_use_graph.has_nodes(instance)) {
					writer.write("\t");
					writer.write(execution.toString());
					writer.write("\t");
					writer.write(execution.get_statement().generate_code());
					writer.write('\n');
					
					for(CDefineUseNode node : define_use_graph.get_nodes(instance)) {
						writer.write("\t    ");
						if(node.is_define()) 
							 writer.write("DEF\t");
						else writer.write("USE\t");
						writer.write(node.get_reference());
						writer.write('\n');
						
						for(CDefineUseEdge edge : node.get_ou_edges()) {
							CDefineUseNode target = edge.get_target();
							
							writer.write("\t\t==> ");
							writer.write(target.get_instance().get_execution() + "\t");
							writer.write(target.get_reference());
							writer.write("\n");
						}
						
						for(CDefineUseEdge edge : node.get_in_edges()) {
							CDefineUseNode source = edge.get_source();
							
							writer.write("\t\t<-- ");
							writer.write(source.get_instance().get_execution() + "\t");
							writer.write(source.get_reference());
							writer.write("\n");
						}
						
						writer.write('\n');
					}
				}
			}
		}
		writer.write("\n");
	}
	private static void output(CirCallContextInstanceGraph program_graph, CDefineUseGraph 
			define_use_graph, File file) throws Exception {
		Queue<CirFunctionCallTreeNode> queue = new LinkedList<CirFunctionCallTreeNode>();
		queue.add(program_graph.get_call_tree().get_root());
		
		FileWriter writer = new FileWriter(file);
		while(!queue.isEmpty()) {
			CirFunctionCallTreeNode tree_node = queue.poll();
			for(CirFunctionCallTreeNode child : tree_node.get_children()) {
				queue.add(child);
			}
			output(program_graph, define_use_graph, tree_node, writer);
		}
		writer.close();
	}
	private static void testing(File file) throws Exception {
		System.out.println("Testing " + file.getName());
		AstCirFile ast_file = parse(file);
		
		ast_file.get_ast_tree();
		System.out.println("\t(1) parsing to AST tree");
		
		CirTree cir_tree = ast_file.get_cir_tree();
		System.out.println("\t(2) parsing to CIR tree");
		
		CirCallContextInstanceGraph program_graph = translate(cir_tree);
		System.out.println("\t(3) translate to flow graph (" + program_graph.size() + ")");
		
		CDefineUseGraph define_use_graph = define_use_graph(program_graph);
		System.out.println("\t(4) generate def-use graph (" + define_use_graph.size() + ")");
		
		output(program_graph, define_use_graph, new File(postfx + "use/" + file.getName() + ".txt"));
		System.out.println("\t(5) output the def-use graph to the output file");
		
		System.out.println();
	}
	
}
