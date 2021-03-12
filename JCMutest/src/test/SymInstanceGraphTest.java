package test;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Random;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant._back_.SymInstanceEdge;
import com.jcsa.jcmutest.mutant.cir2mutant._back_.SymInstanceGraph;
import com.jcsa.jcmutest.mutant.cir2mutant._back_.SymInstanceNode;
import com.jcsa.jcmutest.mutant.cir2mutant._back_.SymInstanceStatus;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymFlowError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstanceUtils;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymValueError;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.state.CStatePath;

public class SymInstanceGraphTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/rprojects/";
	private static final String result_dir = "result/graphs/";
	private static final int maximal_distance = 3;
	private static final Random random = new Random(System.currentTimeMillis());
	
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			testing(root);
		}
	}
	
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	private static String get_code(Object source) throws Exception {
		String code;
		if(source instanceof AstNode) {
			code = ((AstNode) source).generate_code();
		}
		else if(source instanceof CirNode) {
			code = ((CirNode) source).generate_code(true);
		}
		else if(source instanceof SymbolNode) {
			code = ((SymbolNode) source).generate_code(true);
		}
		else {
			code = source.toString();
		}
		
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		return buffer.toString();
	}
	private static void output_instance_status(FileWriter writer, SymInstanceStatus status) throws Exception {
		writer.write("\t\t\t#status");
		writer.write("\t[" + status.get_execution_times() + ", " + status.get_acception_times() + ", " + status.get_rejection_times() + "]");
		if(status.is_executed())
			writer.write("\twas_executed");
		else
			writer.write("\tnot_executed");
		if(status.is_accepted())
			writer.write("\twas_accepted");
		else
			writer.write("\tnot_accepted");
		if(status.is_acceptable())
			writer.write("\twas_acceptable");
		else
			writer.write("\tnot_acceptable");
		writer.write("\n");
		
		writer.write("\t\t\t#annot");
		writer.write("\t[ ");
		for(CirAnnotation annotation : status.get_cir_annotations()) {
			writer.write(annotation.get_type() + "; ");
		}
		writer.write("]\n");
	}
	private static void output_instance_node(FileWriter writer, SymInstanceNode node) throws Exception {
		writer.write("\t\t#node\t" + node.hashCode());
		writer.write("\t" + node.get_type());
		writer.write("\t" + node.get_execution());
		writer.write("\t\"" + get_code(node.get_execution().get_statement()) + "\"\n");
		
		if(node.has_state_error()) {
			SymStateError state_error = node.get_state_error();
			writer.write("\t\t\t#error\t" + state_error.get_type());
			writer.write("\t" + state_error.get_execution());
			writer.write("\t" + get_code(state_error.get_location()));
			if(state_error instanceof SymFlowError) {
				writer.write("\t" + ((SymFlowError) state_error).get_original_flow());
				writer.write("\t" + ((SymFlowError) state_error).get_mutation_flow());
			}
			else if(state_error instanceof SymValueError) {
				SymbolExpression orig_value = ((SymValueError) state_error).get_original_value();
				SymbolExpression muta_value = ((SymValueError) state_error).get_mutation_value();
				orig_value = orig_value.evaluate(null);
				muta_value = muta_value.evaluate(null);
				writer.write("\t" + get_code(orig_value) + "\t" + get_code(muta_value));
			}
			writer.write("\n");
		}
		output_instance_status(writer, node.get_status());
	}
	private static void output_instance_edge(FileWriter writer, SymInstanceEdge edge) throws Exception {
		writer.write("\t\t#edge");
		writer.write("\t" + edge.get_type());
		writer.write("\t" + edge.get_source().hashCode() + "\t" + edge.get_target().hashCode() + "\n");
		SymConstraint constraint = edge.get_constraint();
		writer.write("\t\t\t#cons\t" + constraint.get_execution() + 
				"\t" + get_code(constraint.get_condition()) + 
				"\tat " + get_code(constraint.get_statement()) + "\n");
		
		constraint = edge.get_source().get_graph().get_cir_mutations().optimize(constraint, null);
		writer.write("\t\t\t#optm\t" + constraint.get_execution() + 
				"\t" + get_code(constraint.get_condition()) + 
				"\tat " + get_code(constraint.get_statement()) + "\n");
		Collection<SymConstraint> improve_constraints = SymInstanceUtils.
				improve_constraints(edge.get_source().get_graph().get_cir_mutations(), constraint);
		for(SymConstraint improve_constraint : improve_constraints) {
			writer.write("\t\t\t#optm\t" + improve_constraint.get_execution() + 
					"\t" + get_code(improve_constraint.get_condition()) + 
					"\tat " + get_code(improve_constraint.get_statement()) + "\n");
		}
		
		output_instance_status(writer, edge.get_status());
	}
	private static void output_instance_graph(FileWriter writer, Mutant mutant, CDependGraph dependence_graph, CStatePath state_path) throws Exception {
		writer.write("#muta\t" + mutant.get_id() + "\n");
		writer.write("\t#class: " + mutant.get_mutation().get_class() + 
					":" + mutant.get_mutation().get_operator() + "\n");
		writer.write("\t#location: " + get_code(mutant.get_mutation().get_location()));
		AstNode location = mutant.get_mutation().get_location();
		writer.write(" at line " + location.get_location().line_of() + "\n");
		
		SymInstanceGraph graph = SymInstanceGraph.new_graph(dependence_graph, mutant, maximal_distance);
		if(state_path == null)
			graph.evaluate();	/* perform static evaluation to test its status account */
		else
			graph.evaluate(state_path);	/* dynamic evaluation to test its status result */
		
		for(SymInstanceNode node : graph.get_nodes()) {
			output_instance_node(writer, node);
			for(SymInstanceEdge edge : node.get_ou_edges()) {
				output_instance_edge(writer, edge);
			}
		}
		writer.write("\n");
		writer.flush();
	}
	private static void output_mutations(MuTestProject project, File output, CStatePath state_path) throws Exception {
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		CirFunction root_function = code_file.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1));
		
		System.out.println("Write mutants to " + output.getAbsolutePath());
		FileWriter writer = new FileWriter(output);
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			System.out.println("\t==> " + mutant.toString());
			output_instance_graph(writer, mutant, dependence_graph, state_path);
		}
		writer.close();
	}
	private static boolean output_mutations(MuTestProject project, int test_id) throws Exception {
		TestInput test_case = project.get_test_space().get_test_space().get_input(test_id);
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		CStatePath state_path = project.get_test_space().load_instrumental_path(
				code_file.get_sizeof_template(), 
				code_file.get_ast_tree(), code_file.get_cir_tree(), test_case);
		if(state_path != null) {
			File output = new File(result_dir + project.get_name() + "." + test_id + ".txt");
			output_mutations(project, output, state_path);
		}
		return state_path != null;
	}
	protected static void testing(File root) throws Exception {
		MuTestProject project = get_project(root);
		System.out.println("Testing on " + project.get_name());
		output_mutations(project, new File(result_dir + project.get_name() + ".txt"), null);
		
		int number = project.get_test_space().number_of_test_inputs();
		for(int k = 0; k < 16; k++) {
			int test_id = Math.abs(random.nextInt()) % number;
			if(output_mutations(project, test_id)) break;
		}
	}
	
}
