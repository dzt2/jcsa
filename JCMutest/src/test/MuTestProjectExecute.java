package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.MutaClass;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcmutest.project.MuTestProjectTestSpace;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.test.cmd.CCompiler;
import com.jcsa.jcparse.test.file.TestInput;

public class MuTestProjectExecute {

	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 1;

	public static void main(String[] args) throws Exception {
		testing("prime_factor.c", 672, 512);
	}
	
	/* initialize */
	private static String get_name(File cfile) {
		int index = cfile.getName().lastIndexOf('.');
		return cfile.getName().substring(0, index).trim();
	}
	private static Iterable<MutaClass> get_classes() {
		Set<MutaClass> classes = new HashSet<>();
		classes.addAll(MutationGenerators.trapping_classes());
		classes.addAll(MutationGenerators.unary_classes());
		classes.addAll(MutationGenerators.statement_classes());
		classes.addAll(MutationGenerators.operator_classes());
		classes.addAll(MutationGenerators.assign_classes());
		classes.addAll(MutationGenerators.reference_classes());
		return classes;
	}
	private static MuTestProjectCodeFile get_project(File cfile) throws Exception {
		String name = get_name(cfile); MuTestProject project;
		File root = new File(root_path + "projects/" + name);

		if(!root.exists()) {
			project = new MuTestProject(root, MuCommandUtil.linux_util);

			/* set configuration data */
			List<String> parameters = new ArrayList<>();
			parameters.add("-lm");
			project.set_config(CCompiler.clang, ClangStandard.gnu_c89,
					parameters, sizeof_template_file, instrument_head_file,
					preprocess_macro_file, mutation_head_file, max_timeout_seconds);

			/* input the code files */
			List<File> cfiles = new ArrayList<>();
			List<File> hfiles = new ArrayList<>();
			List<File> lfiles = new ArrayList<>();
			cfiles.add(cfile);
			project.set_cfiles(cfiles, hfiles, lfiles);

			/* input the test inputs */
			File test_suite_file = new File(root_path + "tests/" + name + ".txt");
			List<File> test_suite_files = new ArrayList<>();
			if(test_suite_file.exists()) test_suite_files.add(test_suite_file);
			File inputs_directory = new File(root_path + "inputs/" + name);
			if(!inputs_directory.exists()) FileOperations.mkdir(inputs_directory);
			project.set_inputs_directory(inputs_directory);
			project.add_test_inputs(test_suite_files);

			/* generate mutations */
			project.generate_mutants(get_classes());
		}
		else {
			project = new MuTestProject(root, MuCommandUtil.linux_util);
		}
		
		return project.get_code_space().get_code_file(cfile);
	}
	
	/* executions */
	/**
	 * @param code_file		to derive mutation test result of each mutant
	 * @param prev_mutants	the set of mutants from which alive are found
	 * @return				the set of mutants selected from prev_mutants
	 * @throws Exception
	 */
	private static Collection<Mutant> filter_undetected_mutants(
			MuTestProjectCodeFile code_file, Iterable<Mutant> prev_mutants) throws Exception {
		if(code_file == null) {
			throw new IllegalArgumentException("Invalid code_file: null");
		}
		else {
			/* 1. initialize the previous mutation list  */
			if(prev_mutants == null) {
				prev_mutants = code_file.get_mutant_space().get_mutants();
			}
			List<Mutant> post_mutants = new ArrayList<Mutant>();
			
			/* 2. filter the alive mutations from prev */
			MuTestProjectTestSpace tspace = 
					code_file.get_code_space().get_project().get_test_space();
			for(Mutant mutant : prev_mutants) {
				MuTestProjectTestResult result = tspace.get_test_result(mutant);
				if(result == null || result.get_kill_set().degree() <= 0) {
					post_mutants.add(mutant);
				}
			}
			return post_mutants;
		}
	}
	/**
	 * @param mutants		the set of mutants to be executed for this iteration
	 * @param test_cases	the set of test cases used to kill the input mutants
	 * @return				the set of mutants that have not been killed through
	 * @throws Exception
	 */
	private static Collection<Mutant> execute_mutants_tests(MuTestProjectCodeFile cfile, 
			Collection<Mutant> mutants, Collection<TestInput> test_cases) throws Exception {
		if(mutants == null) {
			throw new IllegalArgumentException("Invalid mutants: null");
		}
		else if(test_cases == null) {
			throw new IllegalArgumentException("Invalid test_cases: null");
		}
		else {
			/* running the mutations over the input test cases */
			System.out.println("\tIteration: " + mutants.size() + 
					" mutants on " + test_cases.size() + " tests.");
			cfile.get_code_space().get_project().execute(mutants, test_cases);
			return filter_undetected_mutants(cfile, mutants);
		}
	}
	/**
	 * @param name		the name of mutation test project
	 * @param tid		the ID of TestInput from which it is used to execute
	 * @param tnumber	the number of tests selected each iteration of run
	 * @throws Exception
	 */
	private static void testing(String name, int tid, int tnumber) throws Exception {
		System.out.println("Testing on " + name);
		
		/* 1. read the project file and collect undetected mutations */
		File cfile = new File(root_path + "cfiles/" + name);
		MuTestProjectCodeFile code_file = get_project(cfile);
		Collection<Mutant> mutants = filter_undetected_mutants(code_file, null);
		int end = code_file.get_code_space().
				get_project().get_test_space().number_of_test_inputs();
		
		/* 2. execute iteration based on input test parameters */
		MuTestProjectTestSpace tspace = 
				code_file.get_code_space().get_project().get_test_space();
		while(tid < end) {
			int next_tid = Math.min(end, tid + tnumber);
			Collection<TestInput> test_cases = tspace.get_test_inputs(tid, next_tid);
			mutants = execute_mutants_tests(code_file, mutants, test_cases);
			tid = next_tid;
		}
		
		/* 3. end of the execution of the entire program testing */
		System.out.println();
	}
	
}
