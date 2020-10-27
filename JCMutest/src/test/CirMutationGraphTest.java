package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirFlowError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirTrapError;
import com.jcsa.jcmutest.mutant.cir2mutant.graph.CirMutationGraph;
import com.jcsa.jcmutest.mutant.cir2mutant.graph.CirMutationNode;
import com.jcsa.jcmutest.mutant.cir2mutant.graph.CirMutationTreeNode;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.test.cmd.CCompiler;

public class CirMutationGraphTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 5;
	private static final int maximal_distance = 1;
	private static final String result_dir = "result/graphs/";
	
	public static void main(String[] args) throws Exception {
		for(File cfile : new File(root_path + "cfiles").listFiles()) {
			if(cfile.getName().endsWith(".c")) {
				testing(cfile);
			}
		}
	}
	
	private static String get_name(File cfile) {
		int index = cfile.getName().lastIndexOf('.');
		return cfile.getName().substring(0, index).strip();
	}
	private static Iterable<MutaClass> get_classes() {
		Set<MutaClass> classes = new HashSet<MutaClass>();
		classes.addAll(MutationGenerators.trapping_classes());
		classes.addAll(MutationGenerators.unary_classes());
		classes.addAll(MutationGenerators.statement_classes());
		classes.addAll(MutationGenerators.operator_classes());
		classes.addAll(MutationGenerators.assign_classes());
		classes.addAll(MutationGenerators.reference_classes());
		return classes;
	}
	private static MuTestProject new_project(File cfile) throws Exception {
		String name = get_name(cfile);
		File root = new File(root_path + "mprojects/" + name);
		
		if(!root.exists()) {
			return new MuTestProject(root, MuCommandUtil.linux_util);
		}
		else {
			MuTestProject project = new MuTestProject(root, MuCommandUtil.linux_util);
			
			/* set configuration data */
			List<String> parameters = new ArrayList<String>();
			parameters.add("-lm");
			project.set_config(CCompiler.clang, ClangStandard.gnu_c89, 
					parameters, sizeof_template_file, instrument_head_file, 
					preprocess_macro_file, mutation_head_file, max_timeout_seconds);
			
			/* input the code files */
			List<File> cfiles = new ArrayList<File>();
			List<File> hfiles = new ArrayList<File>();
			List<File> lfiles = new ArrayList<File>();
			cfiles.add(cfile);
			project.set_cfiles(cfiles, hfiles, lfiles);
			
			/* input the test inputs */
			File test_suite_file = new File(root_path + "tests/" + name + ".c.txt");
			List<File> test_suite_files = new ArrayList<File>();
			if(test_suite_file.exists()) test_suite_files.add(test_suite_file);
			File inputs_directory = new File(root_path + "v_inputs/" + name);
			if(!inputs_directory.exists()) FileOperations.mkdir(inputs_directory);
			project.set_inputs_directory(inputs_directory);
			project.add_test_inputs(test_suite_files);
			
			/* generate mutations */
			project.generate_mutants(get_classes());
			
			return project;
		}
	}
	private static void write_mutation_tree_node(CirMutationTreeNode tree_node, FileWriter writer) throws Exception {
		CirConstraint constraint = tree_node.get_cir_mutation().get_constraint();
		CirStateError state_error = tree_node.get_cir_mutation().get_state_error();
		
		writer.write("\t\t#beg_node\n");
		writer.write("\t\t\t#const");
		writer.write("\t" + constraint.get_execution());
		writer.write("\t" + constraint.get_condition().generate_code());
		writer.write("\n");
		writer.write("\t\t\t#error");
		writer.write("\t" + state_error.get_type());
		writer.write("\t" + state_error.get_execution());
		if(state_error instanceof CirTrapError) { }
		else if(state_error instanceof CirFlowError) {
			writer.write("\t" + ((CirFlowError) state_error).get_mutation_flow().get_target());
		}
		else if(state_error instanceof CirExpressionError) {
			CirExpression source = ((CirExpressionError) state_error).get_expression();
			SymExpression target = ((CirExpressionError) state_error).get_mutation_value();
			writer.write("\t" + source.generate_code(true));
			writer.write("\t" + target.generate_code());
		}
		else if(state_error instanceof CirReferenceError) {
			CirExpression source = ((CirReferenceError) state_error).get_reference();
			SymExpression target = ((CirReferenceError) state_error).get_mutation_value();
			writer.write("\t" + source.generate_code(true));
			writer.write("\t" + target.generate_code());
		}
		else if(state_error instanceof CirStateValueError) {
			CirExpression source = ((CirStateValueError) state_error).get_reference();
			SymExpression target = ((CirStateValueError) state_error).get_mutation_value();
			writer.write("\t" + source.generate_code(true));
			writer.write("\t" + target.generate_code());
		}
		else {
			throw new IllegalArgumentException(state_error.toString());
		}
		writer.write("\n");
		writer.write("\t\t#end_node\n");
	}
	private static void write_mutation_node(CirMutationNode node, FileWriter writer) throws Exception {
		writer.write("\t#beg_tree\n");
		
		if(node.is_root()) {
			writer.write("\t\t#beg_path\n");
			for(CirConstraint constraint : node.get_path_constraints()) {
				writer.write("\t\t\t#const");
				writer.write("\t" + constraint.get_execution());
				writer.write("\t" + constraint.get_condition().generate_code());
				writer.write("\n");
			}
			writer.write("\t\t#end_path\n");
		}
		
		Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
		queue.add(node.get_tree().get_root());
		while(!queue.isEmpty()) {
			CirMutationTreeNode tree_node = queue.poll();
			for(CirMutationTreeNode child : tree_node.get_children()) {
				queue.add(child);
			}
			write_mutation_tree_node(tree_node, writer);
		}
		
		writer.write("\t#end_tree\n");
	}
	private static void write_mutant(Mutant mutant, CDependGraph dependence_graph, FileWriter writer) throws Exception {
		writer.write("#beg_mutant\n");
		
		AstMutation mutation = mutant.get_mutation();
		writer.write("\tOperator: " + mutation.get_class() + "@" + mutation.get_operator() + "\n");
		String code = mutation.get_location().generate_code(); 
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length() && buffer.length() < 64; k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) ch = ' ';
			buffer.append(ch);
		}
		code = buffer.toString();
		int line = mutation.get_location().get_location().line_of() + 1;
		writer.write("\tLocation: \"" + code + "\" at line " + line + "\n");
		if(mutation.has_parameter()) {
			writer.write("\tParameter: " + mutation.get_parameter() + "\n");
		}
		
		CirMutationGraph graph = CirMutationGraph.new_graph(mutant, dependence_graph, maximal_distance);
		for(CirMutationNode node : graph.get_nodes()) {
			write_mutation_node(node, writer);
		}
		
		writer.write("#end_mutant\n");
	}
	private static void write_mutants(MuTestProject project) throws Exception {
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		CirTree cir_tree = code_file.get_cir_tree();
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		CirInstanceGraph instance_graph = CirCallContextInstanceGraph.
							graph(root_function, CirFunctionCallPathType.unique_path, -1);
		CDependGraph dependence_graph = CDependGraph.graph(instance_graph);
		
		FileWriter writer = new FileWriter(result_dir + code_file.get_cfile().getName() + ".txt");
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			System.out.println("\t==> " + mutant.toString());
			write_mutant(mutant, dependence_graph, writer);
			writer.write("\n");
		}
		writer.close();
	}
	protected static void testing(File cfile) throws Exception {
		MuTestProject project = new_project(cfile);
		System.out.println("Testing on " + project.get_name());
		write_mutants(project);
	}
	
}
