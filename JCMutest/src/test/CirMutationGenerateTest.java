package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.MutantSpace;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirErrorSentence;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.path.CirStateErrorAnalyzer;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.test.cmd.CCompiler;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.state.CStatePath;

public class CirMutationGenerateTest {
	
	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 5;
	private static final String result_dir = "result/cir2/";
	
	public static void main(String[] args) throws Exception {
		String name = "profit.c";
		testing(new File(root_path + "cfiles/" + name), 3000);
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
		File root = new File(root_path + "rprojects/" + name);
		if(!root.exists()) {
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
		else {
			return new MuTestProject(root, MuCommandUtil.linux_util);
		}
	}
	private static void output_mutation(MuTestProject project, Mutant mutant, FileWriter writer, 
			Map<CirMutation, Collection<CirMutation>> errors) throws Exception {
		/* AST mutation information */
		AstMutation mutation = mutant.get_mutation();
		writer.write("Mutant#" + mutant.get_id() + "::" + 
				mutation.get_class() + "::" + mutation.get_operator() + "\n");
		AstNode location = mutant.get_mutation().get_location();
		int line = location.get_location().line_of() + 1;
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
		
		MuTestProjectTestResult result = project.get_test_space().get_test_result(mutant);
		if(result == null) {
			writer.write("Result: not-executed\n");
		}
		else if(result.get_kill_set().degree() == 0){
			writer.write("Result: not killed by any\n");
		}
		else {
			writer.write("Result: killed by " + result.get_kill_set().degree() + " tests\n");
		}
		
		/* CIR mutation information */
		if(mutant.has_cir_mutations()) {
			writer.write("+------------------------------------------------+\n");
			int index = 0;
			for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
				writer.write("\tCir-Mutation[" + (index++) + "]: " + cir_mutation + "\n");
				CirErrorSentence cir_sentence = CirErrorSentence.parse(cir_mutation.get_state_error(), null);
				if(cir_sentence != null)
					writer.write("\tCir-Errors: " + cir_sentence + "\n");
				else
					writer.write("\tCir-Errors: No Error in This Location\n");
				for(CirMutation err_mutation : errors.get(cir_mutation)) {
					writer.write("\t\tGeneration: " + err_mutation.toString() + "\n");
					
					CirErrorSentence err_sentence = CirErrorSentence.parse(err_mutation.get_state_error(), null);
					if(err_sentence != null)
						writer.write("\t\t==> Error_Words: " + err_sentence + "\n");
					else
						writer.write("\t\t==> Error_Words: No Error in This Location\n");
				}
			}
			writer.write("+------------------------------------------------+\n");
		}
		writer.write("\n");
	}
	private static void generate_without_context(MuTestProject project) throws Exception {
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		MutantSpace mspace = code_file.get_mutant_space();
		FileWriter writer = new FileWriter(new File(result_dir + project.get_name() + ".x.txt"));
		Map<CirMutation, Collection<CirMutation>> errors = CirStateErrorAnalyzer.solve_errors(mspace.get_cir_mutations());
		
		int error = 0, total = 0;
		for(Mutant mutant : mspace.get_mutants()) {
			total++;
			if(mutant.has_cir_mutations()) {
				if(!mutant.get_cir_mutations().iterator().hasNext()) {
					error++;
				}
				else {
					output_mutation(project, mutant, writer, errors);
				}
			}
			else {
				error++;
			}
		}
		System.out.println("\tError-Rate: " + error + "/" + total);
		
		writer.close();
	}
	private static void testing(File cfile, int tid) throws Exception {
		MuTestProject project = get_project(cfile);
		System.out.println("Testing on " + cfile.getName());
		System.out.println("\tCreate testing project.");
		if(tid < 0 || tid >= project.get_test_space().number_of_test_inputs())
			generate_without_context(project);
		else 
			generate_with_contexts(project, tid);
		System.out.println("\tClose testing projects.");
		System.out.println("");
	}
	private static boolean generate_with_contexts(MuTestProject project, int tid) throws Exception {
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		MutantSpace mutant_space = code_file.get_mutant_space();
		System.out.println("\tLoad " + mutant_space.size() + " mutants from.");
		System.out.println("\tLoad " + project.get_test_space().get_test_space().number_of_inputs() + "tests.");
		
		TestInput input = project.get_test_space().get_test_space().get_input(tid);
		System.out.println("\tSelect test#" + tid + " for mutation analysis.");
		
		CStatePath state_path = project.get_test_space().load_instrumental_path(code_file.
				get_sizeof_template(), code_file.get_ast_tree(), code_file.get_cir_tree(), input);
		if(state_path == null)
			System.out.println("\tUsing context-insensitive for generation analysis");
		else
			System.out.println("\tUsing context-sensitive for generation analysis\n\t==> with " + state_path.size() + " nodes.");
		
		Map<CirMutation, Collection<CirMutation>> errors = 
				CirStateErrorAnalyzer.solve_errors(mutant_space.get_cir_mutations(), state_path);
		System.out.println("\tObtain state error data generated from " + errors.size() + " cir-mutations");
		
		FileWriter writer = new FileWriter(new File(result_dir + project.get_name() + ".x.txt"));
		int error = 0, total = 0;
		for(Mutant mutant : mutant_space.get_mutants()) {
			total++;
			if(mutant.has_cir_mutations()) {
				if(!mutant.get_cir_mutations().iterator().hasNext()) {
					error++;
				}
				else {
					output_mutation(project, mutant, writer, errors);
				}
			}
			else {
				error++;
			}
		}
		System.out.println("\tError-Rate: " + error + "/" + total);
		writer.close();
		
		return state_path != null;
	}
	
}
