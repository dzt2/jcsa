package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.MutantSpace;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.path.CirLocalPropagation;
import com.jcsa.jcmutest.mutant.cir2mutant.path.CirPathConstraints;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.dominate.CDominanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.test.cmd.CCompiler;

public class CirMutationParserTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 5;
	private static final String result_dir = "result/cir/";
	
	public static void main(String[] args) throws Exception {
		for(File cfile : new File(root_path + "cfiles").listFiles()) {
			if(cfile.getName().endsWith(".c")) {
				System.out.println("+-----------------------------------------+");
				testing(cfile);
				System.out.println("+-----------------------------------------+\n");
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
	private static MuTestProject get_project(File cfile) throws Exception {
		String name = get_name(cfile);
		File root = new File(root_path + "mprojects/" + name);
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
		File test_suite_file = new File(root_path + "tests/" + name + ".txt");
		List<File> test_suite_files = new ArrayList<File>();
		if(test_suite_file.exists()) test_suite_files.add(test_suite_file);
		File inputs_directory = new File(root_path + "vinputs/");
		if(!inputs_directory.exists()) FileOperations.mkdir(inputs_directory);
		project.set_inputs_directory(inputs_directory);
		project.add_test_inputs(test_suite_files);
		
		/* generate mutations */
		project.generate_mutants(get_classes());
		
		return project;
		
	}
	private static CirCallContextInstanceGraph translate(CirTree cir_tree) throws Exception {
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		return CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
	}
	private static CDominanceGraph generate(CirInstanceGraph graph) throws Exception {
		return CDominanceGraph.forward_dominance_graph(graph);
	}
	private static void output_mutation(Mutant mutant, FileWriter writer, CDominanceGraph dominance_graph) throws Exception {
		AstMutation mutation = mutant.get_mutation();
		writer.write("Mutant#" + mutant.get_id() + "::" + 
				mutation.get_class() + "::" + mutation.get_operator() + "\n");
		AstNode location = mutant.get_mutation().get_location();
		int line = location.get_location().line_of();
		String code = location.generate_code();
		if(code.contains("\n")) {
			int index = code.indexOf('\n');
			code = code.substring(0, index).strip();
		}
		String ast_class = location.getClass().getSimpleName();
		ast_class = ast_class.substring(3, ast_class.length() - 4).strip();
		writer.write("Location[" + line + "]: " + location.generate_code() + " {" + ast_class + "}\n");
		if(mutation.has_parameter())
			writer.write("Parameter: " + mutation.get_parameter().toString() + "\n");
		if(mutant.has_cir_mutations()) {
			CirMutations cir_mutations = mutant.get_space().get_cir_mutations();
			writer.write("+------------------------------------------------+\n");
			int index = 0;
			for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
				writer.write("\tCir-Mutation[" + (index++) + "]: " + cir_mutation + "\n");
				
				Set<CirConstraint> path_constraints = CirPathConstraints.common_path_constraints(
						dominance_graph, cir_mutation.get_statement(), mutant.get_space().get_cir_mutations());
				writer.write("\t\tPath: { ");
				for(CirConstraint constraint : path_constraints) {
					writer.write(constraint.toString() + "; ");
				}
				writer.write("}\n");
				
				Iterable<CirMutation> next_mutations = CirLocalPropagation.
								local_propagate(cir_mutations, cir_mutation);
				for(CirMutation next_mutation : next_mutations) {
					writer.write("\t\t" + next_mutation.toString() + "\n");
				}
			}
			writer.write("+------------------------------------------------+\n");
		}
		writer.write("\n");
		// System.out.println("\t\t--> " + mutant.get_mutation().toString());
	}
	protected static void testing(File cfile) throws Exception {
		MuTestProject project = get_project(cfile);
		System.out.println("Create project: " + cfile.getName());
		
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_file(cfile);
		CDominanceGraph dominance_graph = generate(translate(code_file.get_cir_tree()));
		
		MutantSpace mspace = code_file.get_mutant_space();
		FileWriter writer = new FileWriter(new File(result_dir + cfile.getName() + ".txt"));
		FileWriter writer2 = new FileWriter(new File(result_dir + cfile.getName() + ".err"));
		FileWriter writer3 = new FileWriter(new File(result_dir + cfile.getName() + ".emp"));
		int error = 0, total = 0; 
		Map<MutaClass, Integer> empty_counter = new HashMap<MutaClass, Integer>();
		for(Mutant mutant : mspace.get_mutants()) {
			total++;
			if(mutant.has_cir_mutations()) {
				if(!mutant.get_cir_mutations().iterator().hasNext()) {
					if(!empty_counter.containsKey(mutant.get_mutation().get_class())) {
						empty_counter.put(mutant.get_mutation().get_class(), 0);
					}
					int counter = empty_counter.get(mutant.get_mutation().get_class());
					empty_counter.put(mutant.get_mutation().get_class(), counter + 1);
					output_mutation(mutant, writer3, dominance_graph);
				}
				else {
					output_mutation(mutant, writer, dominance_graph);
				}
			}
			else {
				output_mutation(mutant, writer2, dominance_graph);
				error++;
			}
		}
		System.out.println("Error-Rate: " + error + "/" + total);
		System.out.print("\t{ ");
		for(MutaClass mclass : empty_counter.keySet()) {
			Integer counter = empty_counter.get(mclass);
			System.out.print(mclass + ": " + counter + "; ");
		}
		System.out.println("}");
		writer.close(); writer2.close(); writer3.close(); 
	}
	
}
