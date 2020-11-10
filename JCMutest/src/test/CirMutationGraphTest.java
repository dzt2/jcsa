package test;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirMutationEdge;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirMutationGraph;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirMutationNode;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirMutationStatus;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirMutationUtils;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;

public class CirMutationGraphTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/mprojects/";
	private static final String result_dir = "result/trees/";
	private static final int maximal_distance = 3;
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			testing(root);
		}
	}
	
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	private static void output_mutation_status(CirMutationStatus status, FileWriter writer) throws Exception {
		writer.write("[" + status.get_execution_times() + ", " + status.get_acception_times() + ", " + status.get_rejection_times() + "]::");
		writer.write("[ ");
		for(CirAnnotation annotation : status.get_annotations()) {
			writer.write(annotation.get_type() + "; ");
		}
		writer.write("].{ ");
		for(CirAnnotation annotation : status.get_annotations()) {
			writer.write(annotation.get_type() + "; ");
		}
		writer.write("}");
	}
	private static void output_mutation_edge(CirMutationEdge edge, FileWriter writer) throws Exception {
		// edge.append_status(null);
		
		writer.write("\t\t==> " + edge.get_type());
		writer.write("\t" + edge.get_target().hashCode());
		writer.write("\t" + edge.get_constraint().toString());
		writer.write("\t");
		output_mutation_status(edge.get_status(), writer);
		writer.write("\n");
	}
	private static void output_mutation_node(CirMutationNode node, FileWriter writer) throws Exception {
		writer.write("\tnode[" + node.hashCode() + "]");
		writer.write("\t" + node.get_type());
		writer.write("\t" + node.get_execution());
		if(node.has_state_error()) {
			writer.write("\t" + node.get_state_error());
		}
		writer.write("\t");
		output_mutation_status(node.get_status(), writer);
		writer.write("\n");
		
		for(CirMutationEdge edge : node.get_ou_edges()) {
			output_mutation_edge(edge, writer);
		}
	}
	private static String get_code(AstNode location, int length) throws Exception {
		String code = location.generate_code();
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length() && k < length; k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		return buffer.toString();
	}
	private static void output_mutation_graph(CirMutationGraph graph, FileWriter writer) throws Exception {
		Mutant mutant = graph.get_mutant();
		AstMutation mutation = mutant.get_mutation();
		writer.write("#BEG\t" + mutant.get_id() + "\n");
		writer.write("\tClass: " + mutation.get_class() + "::" + mutation.get_operator() + "\n");
		AstNode location = mutation.get_location();
		String code = get_code(location, 64);
		int line = location.get_location().line_of();
		writer.write("\tLine[" + line + "]: \"" + code + "\"\n");
		if(mutation.has_parameter()) {
			writer.write("\tParam: " + mutation.get_parameter() + "\n");
		}
		
		for(CirMutationNode node : graph.get_nodes()) {
			output_mutation_node(node, writer);
		}
		
		writer.write("\tACCEPTANCE\n\t{\n");
		for(Object subject : CirMutationUtils.utils.find_acceptable_border(graph)) {
			writer.write("\t\t");
			if(subject instanceof CirMutationEdge) {
				CirMutationEdge edge = (CirMutationEdge) subject;
				writer.write(edge.get_type() + "[" + edge.get_source().hashCode() + ", " + edge.get_target().hashCode() + "]");
				writer.write("\t" + edge.get_constraint());
				CirMutationStatus status = edge.get_status();
				writer.write("\t[" + status.get_execution_times() + ", " + status.
						get_acception_times() + ", " + status.get_rejection_times() + "]");
			}
			else {
				CirMutationNode node = (CirMutationNode) subject;
				writer.write("node[" + node.hashCode() + "]\t" + node.get_type() + "(" + node.get_execution() + ")");
				if(node.has_state_error()) {
					writer.write("\t" + node.get_state_error().toString());
				}
				CirMutationStatus status = node.get_status();
				writer.write("\t[" + status.get_execution_times() + ", " + status.
						get_acception_times() + ", " + status.get_rejection_times() + "]");
			}
			writer.write("\n");
		}
		writer.write("\t}\n");
		writer.write("#END\n");
	}
	private static void output(MuTestProject project, File output) throws Exception {
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		CirFunction root_function = code_file.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1));
		
		FileWriter writer = new FileWriter(output);
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			System.out.println("\t--> " + mutant.toString());
			CirMutationGraph graph = CirMutationGraph.new_graph(mutant, dependence_graph, maximal_distance);
			CirMutationUtils.utils.abst_evaluate(graph);
			output_mutation_graph(graph, writer);
			writer.write("\n");
		}
		writer.close();
	}
	protected static void testing(File root) throws Exception {
		System.out.println("Testing on " + root.getName());
		MuTestProject project = get_project(root);
		output(project, new File(result_dir + root.getName() + ".txt"));
		System.out.println();
	}
	
}
