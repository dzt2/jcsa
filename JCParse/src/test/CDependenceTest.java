package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.context.CirFunctionCallTreeNode;
import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.program.AstCirTree;

public class CDependenceTest {

	protected static final String prefix = "/home/dzt2/Development/Data/ifiles/";
	protected static final String postfx = "result/dep/";
	protected static final File template_file = new File("config/cruntime.txt");

	public static void main(String[] args) throws Exception {
		File[] files = new File(prefix).listFiles();
		for(File file : files) {
			System.out.println("Testing on " + file.getName());
			testing(file);
			System.out.println();
		}
	}
	protected static void testing(File file) throws Exception {
		AstCirTree ast_file = parse(file);
		CirCallContextInstanceGraph program_graph = translate(ast_file.get_cir_tree());
		CDependGraph depend_graph = CDependGraph.graph(program_graph);
		output(depend_graph, new File(postfx + file.getName() + ".txt"));
	}

	/* basic methods */
	private static AstCirTree parse(File file) throws Exception {
		return AstCirTree.parse(file, template_file, ClangStandard.gnu_c89);
	}
	private static CirCallContextInstanceGraph translate(CirTree cir_tree) throws Exception {
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		return CirCallContextInstanceGraph.graph(root_function,
				CirFunctionCallPathType.unique_path, -1);
	}

	/* output method */
	private static void output_node(CDependNode node, FileWriter writer) throws Exception {
		writer.write("\t");
		writer.write(node.toString());
		writer.write("\t");
		writer.write(node.get_execution().toString());
		writer.write("\t");
		writer.write(node.get_statement().generate_code(true));
		writer.write("\n");

		for(CDependEdge edge : node.get_in_edges()) {
			writer.write("\t<-- ");
			writer.write(edge.get_type().toString());
			writer.write("\t");
			writer.write(edge.get_source().toString());
			writer.write("\t");
			if(edge.get_element() != null) {
				writer.write("{");
				writer.write(edge.get_element().toString());
				writer.write("}");
			}
			writer.write("\n");
		}
		writer.write("\n");
	}
	private static void output(CDependGraph depend_graph, File output) throws Exception {
		CirCallContextInstanceGraph program_graph =
				(CirCallContextInstanceGraph) depend_graph.get_program_graph();
		FileWriter writer = new FileWriter(output);

		Iterable<CirFunctionCallTreeNode> contexts = program_graph.get_call_contexts();
		for(CirFunctionCallTreeNode context : contexts) {
			CirFunction function = context.get_function();
			CirExecutionFlowGraph flow_graph = function.get_flow_graph();
			for(int k = 1; k <= flow_graph.size(); k++) {
				CirExecution execution = flow_graph.get_execution(k % flow_graph.size());
				if(program_graph.has_instance(context, execution)) {
					CirInstanceNode instance = program_graph.get_instance(context, execution);
					if(depend_graph.has_node(instance)) {
						CDependNode node = depend_graph.get_node(instance);
						output_node(node, writer);
					}
				}
			}
		}

		writer.close();
	}

}
