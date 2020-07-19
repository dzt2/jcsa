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
import com.jcsa.jcparse.lopt.ingraph.CirInstanceEdge;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceGraph;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceNode;

public class CDominanceTest {
	
	protected static final String prefix = "/home/dzt2/Development/DataSet/Code/ifiles/";
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
	private static CDominanceGraph generate(CirInstanceGraph graph) throws Exception {
		return CDominanceGraph.forward_dominance_graph(graph);
	}
	private static void output(CirCallContextInstanceGraph program_graph, 
			CDominanceGraph dominance_graph, File file) throws Exception {
		Queue<CirFunctionCallTreeNode> queue = new LinkedList<CirFunctionCallTreeNode>();
		queue.add(program_graph.get_call_tree().get_root());
		
		FileWriter writer = new FileWriter(file);
		while(!queue.isEmpty()) {
			CirFunctionCallTreeNode tree_node = queue.poll();
			for(CirFunctionCallTreeNode child : tree_node.get_children()) {
				queue.add(child);
			}
			output(program_graph, dominance_graph, tree_node, writer);
		}
		writer.close();
	}
	private static void output(CirInstanceGraph program_graph, CDominanceGraph dominance_graph,
			CirFunctionCallTreeNode tree_node, FileWriter writer) throws Exception {
		writer.write("FUNCTION\t");
		writer.write(tree_node.toString());
		writer.write("\n");
		
		CirFunction function = tree_node.get_function();
		CirExecutionFlowGraph flow_graph = function.get_flow_graph();
		
		for(int k = 1; k <= flow_graph.size(); k++) {
			CirExecution execution = flow_graph.get_execution(k % flow_graph.size());
			if(program_graph.has_instance(tree_node, execution)) {
				CirInstanceNode target_node = program_graph.get_instance(tree_node, execution);
				
				writer.write("\t");
				writer.write(target_node.get_execution().toString());
				writer.write("\t--> ");
				
				CDominanceNode target = dominance_graph.get_node(target_node);
				writer.write("{ ");
				for(CDominanceNode source : target.get_in_nodes()) {
					CirExecution source_node;
					if(source.is_exec())
						 source_node = source.get_execution();
					else {
						CirInstanceEdge edge = (CirInstanceEdge) source.get_instance();
						source_node = edge.get_source().get_execution();
					}
					writer.write(source_node.toString());
					writer.write("; ");
				}
				writer.write("}\n");
			}
		}
		
		writer.write('\n');
	}
	private static void testing(File file) throws Exception {
		System.out.println("Testing " + file.getName());
		
		AstCirFile ast_file = parse(file);
		System.out.println("\t(1) parsing to AST tree");
		
		CirTree cir_tree = ast_file.get_cir_tree();
		System.out.println("\t(2) parsing to CIR tree");
		
		CirCallContextInstanceGraph program_graph = translate(cir_tree);
		System.out.println("\t(3) translate to flow graph (" + program_graph.size() + ")");
		
		CDominanceGraph dominance_graph = generate(program_graph);
		System.out.println("\t(4) generate dominance graph (" + dominance_graph.size() + ")");
		
		output(program_graph, dominance_graph, new File(postfx + "dom/" + file.getName() + ".txt"));
		System.out.println("\t(5) output the dominance graph to the output file");
		
		System.out.println();
	}
	
}
