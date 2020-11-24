package test;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymFlowError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymTrapError;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirMutationEdge;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirMutationGraph;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirMutationNode;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirMutationStatus;
import com.jcsa.jcmutest.mutant.cir2mutant.tree.CirMutationUtils;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcmutest.project.MuTestProjectTestSpace;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.sym.SymNode;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It performs dynamic and abstract evaluation on mutation nodes and edges
 * to validate its feasibility.
 * 
 * @author yukimula
 *
 */
public class CirMutationUtilsTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/rprojects/";
	private static final String result_dir = "result/graphs/";
	private static final int maximal_distance = 1;
	private static final int maximal_length = 128;
	private static final Random random = new Random(System.currentTimeMillis());
	public static void main(String[] args) throws Exception {
		for(File root : new File(root_path).listFiles()) {
			testing(root);
		}
	}
	
	/* getters */
	/**
	 * @param root
	 * @return load the mutation test project (existing) and return its main code file instance
	 * @throws Exception
	 */
	private static MuTestProjectCodeFile get_project(File root) throws Exception {
		if(!root.exists()) {
			throw new IllegalArgumentException("Invalid root: " + root);
		}
		else {
			MuTestProject project = new MuTestProject(root, MuCommandUtil.linux_util);
			MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
			System.out.println("\t1. Open mutation test project for " + project.get_name());
			System.out.println("\t\tFile: " + code_file.get_cfile().getName());
			System.out.println("\t\tTest: " + project.get_test_space().get_test_inputs().size() + " test cases.");
			System.out.println("\t\tMutant: " + code_file.get_mutant_space().size() + " mutations.");
			return code_file;
		}
	}
	/**
	 * @param code_file
	 * @return generate the dependence graph to describe the specified code in file
	 * @throws Exception
	 */
	private static CDependGraph get_dependence_graph(MuTestProjectCodeFile code_file) throws Exception {
		CirFunction root_function = code_file.get_cir_tree().get_function_call_graph().get_main_function();
		CirInstanceGraph instance_graph = 
				CirCallContextInstanceGraph.graph(root_function, CirFunctionCallPathType.unique_path, -1);
		return CDependGraph.graph(instance_graph);
	}
	
	/* output methods */
	/**
	 * write the head with specified tabs
	 * @param writer
	 * @param tabs
	 * @throws Exception
	 */
	private static void write_new_head(FileWriter writer, int tabs) throws Exception {
		for(int k = 0; k < tabs; k++) writer.write("\t");
	}
	/**
	 * write the new line in the specified writer
	 * @param writer
	 * @throws Exception
	 */
	private static void write_new_line(FileWriter writer) throws Exception {
		writer.write("\n");
	}
	/**
	 * generate the non-space text
	 * @param text
	 * @param length
	 * @return
	 */
	private static String non_space_text(String text, int length) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < text.length() && k < length; k++) {
			char ch = text.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		return buffer.toString();
	}
	/**
	 * code of execution, cir-node, or ast-node
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	private static String get_code(Object instance) throws Exception {
		if(instance instanceof CirNode) {
			return non_space_text(((CirNode) instance).generate_code(true), maximal_length);
		}
		else if(instance instanceof AstNode) {
			return non_space_text(((AstNode) instance).generate_code(), maximal_length);
		}
		else if(instance instanceof CirExecution) {
			return get_code(((CirExecution) instance).get_statement());
		}
		else if(instance instanceof SymNode) {
			return non_space_text(((SymNode) instance).generate_code(), maximal_length);
		}
		else {
			return non_space_text(instance.toString(), maximal_length);
		}
	}
	/**
	 * #const execution condition
	 * @param writer
	 * @param constraint
	 * @throws Exception
	 */
	private static void write_constraint(FileWriter writer, SymConstraint constraint) throws Exception {
		writer.write("#const\t");
		writer.write(constraint.get_execution().toString() + "\t");
		writer.write(get_code(constraint.get_condition()));
	}
	/**
	 * #error execution type {}
	 * @param writer
	 * @param state_error
	 * @throws Exception
	 */
	private static void write_state_error(FileWriter writer, SymStateError state_error) throws Exception {
		writer.write("#error\t" + state_error.get_execution());
		writer.write("\t" + state_error.get_type().toString());
		if(state_error instanceof SymFlowError) {
			CirExecutionFlow orig_flow = ((SymFlowError) state_error).get_original_flow();
			CirExecutionFlow muta_flow = ((SymFlowError) state_error).get_mutation_flow();
			writer.write("\t" + orig_flow.get_type() + "[" + orig_flow.get_target() + "]");
			writer.write("\t" + muta_flow.get_type() + "[" + muta_flow.get_target() + "]");
		}
		else if(state_error instanceof SymTrapError) {
			writer.write("\t\"" + get_code(state_error.get_statement()) + "\"");
		}
		else if(state_error instanceof SymExpressionError) {
			writer.write("\t\"" + get_code(((SymExpressionError) state_error).get_expression()) + "\"");
			writer.write("\t\"" + get_code(((SymExpressionError) state_error).get_mutation_value()) + "\"");
		}
		else if(state_error instanceof SymReferenceError) {
			writer.write("\t\"" + get_code(((SymReferenceError) state_error).get_expression()) + "\"");
			writer.write("\t\"" + get_code(((SymReferenceError) state_error).get_mutation_value()) + "\"");
		}
		else if(state_error instanceof SymStateValueError) {
			writer.write("\t\"" + get_code(((SymStateValueError) state_error).get_expression()) + "\"");
			writer.write("\t\"" + get_code(((SymStateValueError) state_error).get_mutation_value()) + "\"");
		}
		else {
			throw new IllegalArgumentException("Invalid state-error: " + state_error);
		}
	}
	/**
	 * @param node
	 * @return String identifier of mutation node
	 */
	private static String mutation_node_key(CirMutationNode node) {
		return node.get_type() + "[" + node.hashCode() + "]";
	}
	private static void write_mutation_status(FileWriter writer, CirMutationStatus status) throws Exception {
		writer.write("[" + status.get_execution_times() + ", " + status.
				get_acception_times() + ", " + status.get_rejection_times() + "]");
		writer.write(".{ ");
		for(CirAnnotation annotation : status.get_annotations()) {
			if(annotation.get_parameter() != null)
				writer.write(annotation.get_type() + ":" + annotation.get_parameter() + "; ");
			else
				writer.write(annotation.get_type() + "; ");
		}
		writer.write("}");
	}
	/**
	 * #flow target_identifier constraint
	 * @param writer
	 * @param edge
	 * @param tabs
	 * @throws Exception
	 */
	private static void write_mutation_edge(FileWriter writer, CirMutationEdge edge, int tabs) throws Exception {
		write_new_head(writer, tabs);
		writer.write("#flow");
		writer.write("\t" + mutation_node_key(edge.get_target()));
		writer.write("\t");
		write_constraint(writer, edge.get_constraint());
		writer.write("\t");
		write_mutation_status(writer, edge.get_status());
		write_new_line(writer);
	}
	/**
	 * 	#node identifier execution statement
	 * 	{
	 * 		#error ...
	 * 		#note  ...
	 * 		#flow  ...
	 * 	}
	 * @param writer
	 * @param node
	 * @throws Exception
	 */
	private static void write_mutation_node(FileWriter writer, CirMutationNode node, int tabs) throws Exception {
		write_new_head(writer, tabs);
		writer.write("#node\t" + mutation_node_key(node));
		writer.write("\t" + node.get_execution());
		writer.write("\t\"" + get_code(node.get_statement()) + "\"");
		write_new_line(writer);
		
		write_new_head(writer, tabs);
		writer.write("{");
		write_new_line(writer);
		
		if(node.has_state_error()) {
			write_new_head(writer, tabs + 1);
			write_state_error(writer, node.get_state_error());
			write_new_line(writer);
			
			write_new_head(writer, tabs + 1);
			writer.write("#note\t");
			write_mutation_status(writer, node.get_status());
			write_new_line(writer);
		}
		
		for(CirMutationEdge edge : node.get_ou_edges()) {
			write_mutation_edge(writer, edge, tabs + 1);
		}
		
		write_new_head(writer, tabs);
		writer.write("}");
		write_new_line(writer);
	}
	/**
	 * 	#BegMuta
	 * 		class: class:operator
	 * 		location: code at line x
	 * 		parameter: xxx
	 * 		{
	 * 			#node ...
	 * 		}
	 * 		#border
	 * 		{
	 * 			#node id				status
	 * 			#flow source target		status
	 * 		}
	 * 	#EndMuta
	 * @param writer
	 * @param graph
	 * @throws Exception
	 */
	private static void write_mutation_graph(FileWriter writer, CirMutationGraph graph, Boolean result) throws Exception {
		writer.write("#BegMuta\n");
		
		Mutant mutant = graph.get_mutant();
		AstMutation mutation = mutant.get_mutation();
		writer.write("\tclass: " + mutation.get_class() + "." + mutation.get_operator() + "\n");
		writer.write("\tlocation: \"" + get_code(mutation.get_location()) + 
				"\" at Line " + mutation.get_location().get_location().line_of() + "\n");
		if(mutation.has_parameter())
			writer.write("\tparameter: " + mutation.get_parameter() + "\n");
		if(result == null)
			writer.write("\tresult: unknown\n");
		else if(result.booleanValue())
			writer.write("\tresult: killed\n");
		else
			writer.write("\tresult: survive\n");
		
		writer.write("\t{\n");
		for(CirMutationNode node : graph.get_nodes()) {
			write_mutation_node(writer, node, 2);
		}
		writer.write("\t}\n");
		
		writer.write("\t#border\n");
		writer.write("\t{\n");
		for(Object subject : CirMutationUtils.utils.find_acceptable_border(graph)) {
			CirMutationStatus status;
			if(subject instanceof CirMutationNode) {
				writer.write("\t\t#node\t" + mutation_node_key((CirMutationNode) subject));
				status = ((CirMutationNode) subject).get_status();
			}
			else {
				CirMutationEdge edge = (CirMutationEdge) subject;
				writer.write("\t\t#flow\t" + edge.get_type());
				writer.write("\t" + mutation_node_key(edge.get_source()));
				writer.write("\t" + mutation_node_key(edge.get_target()));
				status = edge.get_status();
			}
			writer.write("\t[" + status.get_execution_times() + ", " + status.
					get_acception_times() + ", " + status.get_rejection_times() + "]");
			writer.write("\n");
		}
		writer.write("\t}\n");
		
		writer.write("#EndMuta\n");
	}
	/**
	 * write the information of mutation to the mutations in code file
	 * @param code_file
	 * @param depenence_graph
	 * @throws Exception
	 */
	private static void write_project(MuTestProjectCodeFile code_file, CDependGraph dependence_graph) throws Exception {
		File output = new File(result_dir + code_file.get_cfile().getName() + ".txt");
		FileWriter writer = new FileWriter(output);
		MuTestProjectTestSpace tspace = code_file.get_code_space().get_project().get_test_space();
		
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			System.out.println("\t--> Generate for " + mutant.toString());
			CirMutationGraph graph = CirMutationGraph.new_graph(mutant, dependence_graph, maximal_distance);
			CirMutationUtils.utils.abst_evaluate(graph);
			
			MuTestProjectTestResult result = tspace.get_test_result(mutant);
			Boolean bool_result;
			if(result == null)
				bool_result = null;
			else if(result.get_kill_set().degree() > 0)
				bool_result = Boolean.TRUE;
			else
				bool_result = Boolean.FALSE;
			
			write_mutation_graph(writer, graph, bool_result);
			write_new_line(writer);
		}
		
		writer.close();
	}
	private static boolean write_project(MuTestProjectCodeFile code_file, CDependGraph dependence_graph, int test_id) throws Exception {
		File output = new File(result_dir + code_file.get_cfile().getName() + "." + test_id + ".txt");
		MuTestProjectTestSpace tspace = code_file.get_code_space().get_project().get_test_space();
		TestInput test_case = tspace.get_test_space().get_input(test_id);
		CStatePath state_path = tspace.load_instrumental_path(
				code_file.get_sizeof_template(), 
				code_file.get_ast_tree(), 
				code_file.get_cir_tree(), test_case);
		
		if(state_path != null) {
			FileWriter writer = new FileWriter(output);
			for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
				System.out.println("\t--> Generate for " + mutant.toString());
				CirMutationGraph graph = CirMutationGraph.new_graph(mutant, dependence_graph, maximal_distance);
				CirMutationUtils.utils.conc_evaluate(graph, state_path);
				
				MuTestProjectTestResult result = tspace.get_test_result(mutant);
				Boolean bool_result;
				if(result == null)
					bool_result = null;
				else if(result.get_kill_set().get(test_id))
					bool_result = Boolean.TRUE;
				else
					bool_result = Boolean.FALSE;
				
				writer.write("Test#" + test_case.get_id() + ": " + test_case.get_parameter() + "\n");
				write_mutation_graph(writer, graph, bool_result);
				write_new_line(writer);
			}
			writer.close();
		}
		else {
			System.out.println("\t??? Unable to obtain the execution path for test#" + test_id);
		}
		
		return state_path != null;
	}
	protected static void testing(File root) throws Exception {
		System.out.println("Testing on " + root.getName());
		MuTestProjectCodeFile code_file = get_project(root);
		CDependGraph dependence_graph = get_dependence_graph(code_file);
		write_project(code_file, dependence_graph);
		int number = code_file.get_code_space().get_project().get_test_space().number_of_test_inputs();
		for(int k = 0; k < 8; k++) {
			int test_id = Math.abs(random.nextInt()) % number;
			if(write_project(code_file, dependence_graph, test_id)) {
				break;
			}
		}
		System.out.println();
	}
	
}
