package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lopt.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.lopt.context.CirFunctionCallPathType;
import com.jcsa.jcparse.lopt.context.CirFunctionCallTreeNode;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;
import com.jcsa.jcparse.lopt.models.influence.CInfluenceEdge;
import com.jcsa.jcparse.lopt.models.influence.CInfluenceGraph;
import com.jcsa.jcparse.lopt.models.influence.CInfluenceNode;

/**
 * Used to test the parsing and structure of influence graph.
 * 
 * @author yukimula
 *
 */
public class CInfluenceTest {
	
	protected static final String prefix = "D:/SourceCode/MyData/CODE2/gfiles/";
	protected static final String postfx = "result/inf/";
	protected static final File template_file = new File("config/run_temp.txt");
	
	public static void main(String[] args) throws Exception {
		for(File file : new File(prefix).listFiles()) {
			testing(file);
		}
	}
	private static void testing(File file) throws Exception {
		System.out.println("Testing on " + file.getName());
		AstCirFile ast_file = parse(file);
		System.out.println("\t1. get the AST-tree [" + ast_file.get_ast_tree().number_of_nodes() + "]");
		CirTree cir_tree = ast_file.get_cir_tree();
		System.out.println("\t2. get th  CIR-tree [" + cir_tree.size() + "]");
		CirCallContextInstanceGraph program_graph = translate(cir_tree);
		System.out.println("\t3. get program graph [" + program_graph.size() + "]");
		CInfluenceGraph influence_graph = generate(program_graph);
		System.out.println("\t4. get influence graph [" + influence_graph.size() + "]");
		output_graph(influence_graph, new File(postfx + file.getName() + ".txt"));
		System.out.println("\t5. output the influence graph.");
		System.out.println();
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
	private static CInfluenceGraph generate(CirInstanceGraph program_graph) throws Exception {
		return CInfluenceGraph.graph(program_graph);
	}
	
	/* output methods */
	private static StringBuilder buffer = new StringBuilder();
	private static String get_identifier(CInfluenceNode node) throws Exception {
		/* context */
		CirFunctionCallTreeNode context = 
				(CirFunctionCallTreeNode) node.get_instance_context(); 
		buffer.setLength(0);
		while(context != null) {
			if(context.get_context() != null) {
				buffer.append(context.get_context().
						get_call_execution().toString());
				buffer.append(":"); 
			}
			else {
				buffer.append(context.get_function().get_name());
			}
			context = context.get_parent();
		}
		
		/* identifier */
		return buffer.toString() + "[" + node.get_cir_source().get_node_id() + "]";
	}
	/**
	 * ID type execution cir_source 
	 * @param node
	 * @param writer
	 * @throws Exception
	 */
	private static void output_node(CInfluenceNode node, FileWriter writer) throws Exception {
		writer.write(get_identifier(node));
		writer.write("\t");
		writer.write(node.get_node_type().toString());
		writer.write("\t");
		String cir_name = node.get_cir_source().getClass().getSimpleName();
		cir_name = cir_name.substring(3, cir_name.length() - 4);
		writer.write(cir_name + "\t");
		AstNode ast_node = node.get_cir_source().get_ast_source();
		if(ast_node != null) {
			String code = ast_node.get_location().trim_code(64);
			writer.write("\"" + code + "\"");
		}
		writer.write("\n");
		
		for(CInfluenceEdge edge : node.get_ou_edges()) {
			writer.write("\t==> ");
			writer.write(edge.get_type().toString());
			writer.write("\t");
			writer.write(get_identifier(edge.get_target()));
			writer.write("\n");
		}
	}
	private static void output_graph(CInfluenceGraph graph, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		for(CirInstanceNode instance : graph.get_instances()) {
			for(CInfluenceNode influence_node : graph.get_nodes(instance)) {
				output_node(influence_node, writer);
			}
		}
		writer.close();
	}
	
}
