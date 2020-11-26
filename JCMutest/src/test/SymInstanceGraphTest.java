package test;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymFlowError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstanceUtils;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceEdge;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceGraph;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceNode;
import com.jcsa.jcmutest.mutant.cir2mutant.path.SymInstanceStatus;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.symbol.SymEvaluator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymNode;

public class SymInstanceGraphTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/mprojects/";
	private static final String result_dir = "result/graphs/";
	private static final int maximal_distance = 3;
	
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
		else if(source instanceof SymNode) {
			code = ((SymNode) source).generate_code();
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
				SymExpression orig_value = ((SymValueError) state_error).get_original_value();
				SymExpression muta_value = ((SymValueError) state_error).get_mutation_value();
				orig_value = SymEvaluator.evaluate_on(orig_value);
				muta_value = SymEvaluator.evaluate_on(muta_value);
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
	private static void output_instance_graph(FileWriter writer, Mutant mutant, CDependGraph dependence_graph) throws Exception {
		writer.write("#muta\t" + mutant.get_id() + "\n");
		writer.write("\t#class: " + mutant.get_mutation().get_class() + 
					":" + mutant.get_mutation().get_operator() + "\n");
		writer.write("\t#location: " + get_code(mutant.get_mutation().get_location()));
		AstNode location = mutant.get_mutation().get_location();
		writer.write(" at line " + location.get_location().line_of() + "\n");
		
		SymInstanceGraph graph = SymInstanceGraph.new_graph(dependence_graph, mutant, maximal_distance);
		graph.evaluate();	/* perform static evaluation to test its status account */
		
		for(SymInstanceNode node : graph.get_nodes()) {
			output_instance_node(writer, node);
			for(SymInstanceEdge edge : node.get_ou_edges()) {
				output_instance_edge(writer, edge);
			}
		}
		writer.write("\n");
	}
	private static void output_mutations(MuTestProject project, File output) throws Exception {
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		CirFunction root_function = code_file.get_cir_tree().get_function_call_graph().get_main_function();
		CDependGraph dependence_graph = CDependGraph.graph(CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1));
		
		FileWriter writer = new FileWriter(output);
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			System.out.println("\t==> " + mutant.toString());
			output_instance_graph(writer, mutant, dependence_graph);
		}
		writer.close();
	}
	protected static void testing(File root) throws Exception {
		MuTestProject project = get_project(root);
		System.out.println("Testing on " + project.get_name());
		output_mutations(project, new File(result_dir + project.get_name() + ".txt"));
	}
	
}
