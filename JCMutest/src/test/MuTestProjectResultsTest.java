package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.jcsa.jcmutest.mutant.MutaClass;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcmutest.project.MuTestProjectTestSpace;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.cmd.CCompiler;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * This file presents how to create, execute, and derive results of mutation 
 * testing project.
 * 
 * @author yukimula
 *
 */
public class MuTestProjectResultsTest {
	
	/* parameters used */
	private static final Random random = new Random(System.currentTimeMillis());
	/** the path of directory where the xxx.c code files are preserved **/
	static final String code_file_directory = "/home/dzt2/Development/Data/cfiles/";
	/** the directory where the data files in test/inputs/ are used **/
	static final String input_dir_directory = "/home/dzt2/Development/Data/inputs/";
	/** the directory where the test.suite information are derived **/
	static final String test_suite_directory = "/home/dzt2/Development/Data/tests/";
	/** the path where the root directory of mutation test project is created **/
	static final String projects_directory = "/home/dzt2/Development/Data/projects/";
	/** the file used to configuration as the AstTree's sizeof evaluation **/
	static final File sizeof_template_file = new File("config/cruntime.txt");
	/** the header file and library used to compile instrumented programs **/
	static final File instrument_head_file = new File("config/jcinst.h");
	/** the header file used to pre-process xxx.c code file to xxx.i files **/
	static final File preprocess_macro_file = new File("config/linux.h");
	/** the header file used to compile the program consisting of mutation **/
	static final File mutation_head_file = new File("config/jcmutest.h");
	/** the directory where the coverage path information is visualized to **/
	static final String output_directory = "results/paths/";
	/** the maximal time (seconds) to execute one single test for a mutant **/
	static final long max_timeout_seconds = 1;
	
	/* main executing method */
	public static void main(String[] args) throws Exception {
		for(File root : new File(projects_directory).listFiles()) {
			//if(root.getName().equals("is_prime")) {
			if(true) {
				/* 1. obtain the mutation test project */
				System.out.println("Testing " + root.getName());
				MuTestProject project = create_project(
						new File(code_file_directory + root.getName() + ".c"));
				
				/* 2. collect all the test cases for running */
				// TODO fix here to change the tests being selected
				// Collection<TestInput> test_cases = select_all_test_cases(project);
				Collection<TestInput> test_cases = select_test_cases(project, 0, 128);
				project.execute_instrumental(test_cases);
				
				/* 3. perform the execution or evaluations */
				// TODO fix this method to choose the function you want to do
				//do_mutation_executing(project, test_cases, false);
				// do_evaluate_detect_ratio(project, test_cases);
				// do_evaluate_coincidental_correctness(project, test_cases);
				for(TestInput test_case : test_cases) {
					if(random.nextBoolean()) {
						File output = new File(output_directory + root.
								getName() + "." + test_case.get_id() + ".txt");
						if(do_visualize_coverage_path(project, test_case, output)) {
							System.out.println("\tvisualize: " + output.getAbsolutePath());
							break;
						}
					}
				}
				System.out.println();
			}
		}
	}
	
	/* 1. create a new project or open an existing one */
	/**
	 * @param cfile 	{xxx.c} --> {xxx}
	 * @return 			the name used to create project, for example:
	 * 					"bubble_sort.c" is parsed to "bubble_sort"
	 */
	private static String derive_project_name(File cfile) {
		String name = cfile.getName();
		int index = name.indexOf('.');
		if(index > 0) {
			name = name.substring(0, index).strip();
		}
		return name;
	}
	/**
	 * @return the set mutation operators used to generate mutants
	 */
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
	/**	
	 * @param cfile	the xxx.c code file defined in code_file_directory path
	 * @return		project is generated for the xxx.c in project_directory
	 * @throws Exception
	 */
	protected static MuTestProject create_project(File cfile) throws Exception {
		if(cfile == null || !cfile.exists()) {
			throw new IllegalArgumentException("invalid cfile: " + cfile);
		}
		else {
			/* 1. create an empty initialized project */
			String name = derive_project_name(cfile); MuTestProject project;
			File project_dir = new File(projects_directory + name);
			if(project_dir.exists()) {	/* if existing, direct open it */
				return new MuTestProject(project_dir, MuCommandUtil.linux_util);
			}
			/* otherwise, create an empty new project in project_dir */
			project = new MuTestProject(project_dir, MuCommandUtil.linux_util);
			
			/* 2. set the configuration parameters to project */
			List<String> parameters = new ArrayList<String>();
			parameters.add("-lm");	/* -lm is parameter used to compile math.h */
			project.set_config(CCompiler.clang, ClangStandard.gnu_c89, parameters, 
					sizeof_template_file, instrument_head_file, 
					preprocess_macro_file, mutation_head_file, max_timeout_seconds);
			
			/* 3. set the input code files */
			List<File> cfiles = new ArrayList<File>();
			List<File> hfiles = new ArrayList<File>();
			List<File> lfiles = new ArrayList<File>();
			cfiles.add(cfile);
			// 	hfiles.add(hfile);	--> add xxx.h
			//	lfiles.add(lfile);	--> add xxx.l
			project.set_cfiles(cfiles, hfiles, lfiles);
			
			/* 4. input the test suite file for test cases generation */
			File test_suite_file = new File(test_suite_directory + name + ".txt");
			List<File> test_suite_files = new ArrayList<File>();
			if(test_suite_file.exists()) test_suite_files.add(test_suite_file);
			project.add_test_inputs(test_suite_files);
			
			/* 5. set the input data used in tests/inputs/ */
			File inputs_directory = new File(input_dir_directory + name);
			if(!inputs_directory.exists()) FileOperations.mkdir(inputs_directory);
			project.set_inputs_directory(inputs_directory);
			
			/* 6. generate mutations */
			project.generate_mutants(get_classes());
			
			return project;
		}
	}
	
	/* 2. test case selection from mutation project using id and number */
	/**
	 * @param project	the project from which the test cases are derived
	 * @param beg_tid	the test-id from which the test input is selected
	 * @param t_number	the number of test cases selected from beg_tid
	 * @return			the set of TestInput (test cases) being selected
	 * @throws Exception
	 */
	protected static Collection<TestInput> select_test_cases(MuTestProject 
				project, int beg_tid, int t_number) throws Exception {
		/* 1. obtain the test space in project */
		MuTestProjectTestSpace tspace = project.get_test_space();
		
		/* 2. get the selected test cases in given range */
		return tspace.get_test_inputs(beg_tid, beg_tid + t_number);
	}
	/**
	 * @param project
	 * @return all the test cases defined in the project
	 * @throws Exception
	 */
	protected static Collection<TestInput> select_all_test_cases(MuTestProject project) throws Exception {
		/* 1. obtain the test space in project */
		MuTestProjectTestSpace tspace = project.get_test_space();
		/* 2. return all the test cases in the project */
		return tspace.get_test_inputs();
	}
	
	/* 2. perform executions on mutation (fast & exhaustive version) and insturmented file */
	/**
	 * It selects the set of mutants that are not killed by now
	 * @param code_file		the code file in which mutants are selected
	 * @param prev_mutants	the set of mutants from which the alive mutants are selected
	 * 						or null to select from the entire set of mutant in code file
	 * @return				the set of mutants remain unkilled selected from prev_mutants
	 * @throws Exception
	 */
	private static Collection<Mutant> select_alive_mutants(
			MuTestProjectCodeFile code_file, 
			Iterable<Mutant> prev_mutants) throws Exception {
		if(code_file == null) {
			throw new IllegalArgumentException("Invalid code_file: null");
		}
		else {
			/* 1. if prev_mutants is null, select from entire mutant space */
			List<Mutant> alive_mutants = new ArrayList<Mutant>();
			if(prev_mutants == null) {
				prev_mutants = code_file.get_mutant_space().get_mutants();
			}
			
			/* 2. get the test space for deriving test results */
			MuTestProjectTestSpace tspace = code_file.get_code_space().get_project().get_test_space();
			
			/* 3. for each mutant, it decides whether it has been killed or not */
			for(Mutant mutant : prev_mutants) {
				/* (1) derive the mutation test results from test space */
				MuTestProjectTestResult result = tspace.get_test_result(mutant);
				
				/* (2) result is null iff. the mutant is never executed */
				if(result == null) {
					alive_mutants.add(mutant);
				}
				/* (3) result.degree() returns the number of tests killing it */
				else if(result.get_kill_set().degree() <= 0) {
					alive_mutants.add(mutant);	/** degree() == 0 means no test kill it by now **/
				}
			}
			
			/* 4. return the set of alive mutations */	return alive_mutants;
		}
	}
	/**
	 * @param code_file		the code file in which the mutants are selected to execute
	 * @param prev_mutants	the set of mutants being executed against the test cases
	 * @param test_cases	the set of test cases applied in the execution iteration
	 * @return				the set of mutants that are not killed after this execution
	 * @throws Exception
	 */
	private static Collection<Mutant> execute_mutants_iteration(
			MuTestProjectCodeFile code_file, 
			Collection<Mutant> prev_mutants,
			Collection<TestInput> test_cases) throws Exception {
		if(code_file == null) {
			throw new IllegalArgumentException("Invalid code_file: null");
		}
		else if(prev_mutants == null) {
			throw new IllegalArgumentException("Invalid prev_mutants: null");
		}
		else if(test_cases == null) {
			throw new IllegalArgumentException("Invalid test_cases: null");
		}
		else if(prev_mutants.isEmpty() || test_cases.isEmpty()) {	
			return prev_mutants;	// when no mutant or no test is used
		}
		else {
			System.out.print("\tRun: " + prev_mutants.size() + " mutants " 
						+ "against " + test_cases.size() + " test cases.");
			
			/* NOTE: the execution iteration may take much time here... */
			long beg_time = System.currentTimeMillis();
			code_file.get_code_space().get_project().execute(prev_mutants, test_cases);
			long end_time = System.currentTimeMillis();
			long seconds = (end_time - beg_time) / 1000;
			System.out.println("\t[" + seconds + " .s]");
			
			return select_alive_mutants(code_file, prev_mutants);
		}
	}
	/**
	 * The fast version of mutant execution only executes the alive mutants on
	 * a subset of tests, and obtain incomplete results in testing.
	 * 
	 * That means: once a mutant is killed, it is never executed against any test
	 * after the mutant is killed in the executing iteration.
	 * 
	 * @param code_file		the code file of which (alive) mutants will be executed
	 * @param test_cases	the set of test cases to be executed against mutants
	 * @param iter_size		the number of test cases used to execute each iteration
	 * 
	 * @return	the set of mutants that remain unkilled after the execution
	 * @throws Exception
	 */
	private static Collection<Mutant> efficient_execute(MuTestProjectCodeFile code_file,
			Collection<TestInput> test_cases, int iter_size) throws Exception {
		if(code_file == null) {
			throw new IllegalArgumentException("Invalid code_file: null");
		}
		else if(iter_size <= 0) {
			throw new IllegalArgumentException("Invalid iter_size: " + iter_size);
		}
		else if(test_cases == null) {
			throw new IllegalArgumentException("Invalid test_cases: null");
		}
		else {
			/* 1. only select unkilled mutants in code_file to execute */
			Collection<Mutant> mutants = select_alive_mutants(code_file, null);
			List<TestInput> test_cases_buffer = new ArrayList<TestInput>();
			
			/* 2. execute only alive mutants to a small number of tests each iteration */
			for(TestInput test_case : test_cases) {
				test_cases_buffer.add(test_case);
				
				if(test_cases_buffer.size() >= iter_size) {
					// when buffer reaches iteration-size, execute mutants on tests in buffer
					// and then update mutants by removing those that are killed in iteration
					mutants = execute_mutants_iteration(code_file, mutants, test_cases_buffer);
					test_cases_buffer.clear();
				}
			}
			
			/* 3. execute the alive mutants against tests remaining in last */
			if(!test_cases_buffer.isEmpty()) {
				mutants = execute_mutants_iteration(code_file, mutants, test_cases_buffer);
			}
			return mutants;
		}
	}
	/**
	 * The exhaustive version of execution will execute every mutant against 
	 * every test in input set whatever the mutant is killed or not.
	 * @param code_file
	 * @param test_cases
	 * @return	the set of mutants that remain unkilled after the execution
	 * @throws Exception
	 */
	private static Collection<Mutant> exhaustive_execute(
			MuTestProjectCodeFile code_file,
			Collection<TestInput> test_cases) throws Exception {
		if(code_file == null) {
			throw new IllegalArgumentException("Invalid code_file: null");
		}
		else if(test_cases == null) {
			throw new IllegalArgumentException("Invalid test_cases: null");
		}
		else {
			/* 1. derive all the mutants for exhaustive testing */
			Collection<Mutant> mutants = new ArrayList<Mutant>();
			for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
				mutants.add(mutant);
			}
			
			/* 2. exhaustive executing all mutants on all tests and return alived ones */
			return execute_mutants_iteration(code_file, mutants, test_cases);
		}
	}
	/**
	 * It performs execution of mutants against given test cases
	 * (This method is executed before mutation test results are extracted)
	 * 
	 * @param project	the project from which the test cases are derived
	 * @param test_cases	the set of test cases to be executed in testing
	 * @param efficient true to execute in efficient way or exhaustive way
	 * @throws Exception
	 */
	protected static void do_mutation_executing(MuTestProject project, 
			Collection<TestInput> test_cases, boolean efficient) throws Exception {
		/* 1. select the test cases and code file being executed */
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		
		/* 2. perform mutants execution against input test cases */
		Collection<Mutant> alive_mutants;
		if(efficient) {
			alive_mutants = efficient_execute(code_file, test_cases, 64);
		}
		else {
			alive_mutants = exhaustive_execute(code_file, test_cases);
		}
		
		/* 3. report the mutation score */
		int mutant_number = code_file.get_mutant_space().size();
		int alive_number = alive_mutants.size();
		int killed_number = mutant_number - alive_number;
		double mutation_score = ((double) killed_number) / (mutant_number + 0.0);
		mutation_score = ((int) (mutation_score * 10000)) / 100.0;
		System.out.println("\tReport: " + mutation_score + "% \t[" + 
				killed_number + "/" + alive_number + "/" + mutant_number + "]");
		return;
	}
	
	/* 3. evaluate mutation score (fault detection ratio) */
	/**
	 * @param code_file
	 * @param mutant
	 * @param test_case
	 * @return whether the mutant is killed by the given test
	 * @throws Exception
	 */
	private static boolean is_killed_by(MuTestProjectCodeFile code_file, 
			Mutant mutant, TestInput test_case) throws Exception {
		if(code_file == null) {
			throw new IllegalArgumentException("Invalid code_file: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else if(test_case == null) {
			throw new IllegalArgumentException("Invalid test_case: null");
		}
		else {
			/* 1. obtain the test result of mutant from space */
			MuTestProjectTestSpace tspace = code_file.get_code_space().get_project().get_test_space();
			MuTestProjectTestResult result = tspace.get_test_result(mutant);
			
			/* 2. if result is null, it means the mutant is never executed before */
			if(result == null) { return false; }	// not-executed test is not killed
			/* 3. it determines whether the mutant is killed by the test case */
			else { return result.get_kill_set().get(test_case.get_id()); }
		}
	}
	/**
	 * @param code_file		the code file of which mutants are evaluated
	 * @param mutant		the mutant to be decided whether it is killed
	 * @param test_cases	the set of test cases to evaluate its mutation score
	 * @return				whether the mutant is killed by any test in test_cases 
	 * @throws Exception
	 */
	private static boolean is_killed_in(MuTestProjectCodeFile code_file, Mutant 
				mutant, Collection<TestInput> test_cases) throws Exception {
		if(code_file == null) {
			throw new IllegalArgumentException("Invalid code_file: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else if(test_cases == null) {
			throw new IllegalArgumentException("Invalid test_cases: null");
		}
		else {
			/* 1. obtain the test result of mutant from space */
			MuTestProjectTestSpace tspace = code_file.get_code_space().get_project().get_test_space();
			MuTestProjectTestResult result = tspace.get_test_result(mutant);
			
			/* 2. if result is null, it means the mutant is never executed before */
			if(result == null) { return false; }	// not-executed test is not killed
			
			/* 3. to test whether the mutant is detected by any test cases */
			for(TestInput test_case : test_cases) {
				/* the result shows the mutant is killed by test case */
				if(result.get_kill_set().get(test_case.get_id())) {
					return true;
				}
			}
			
			/* 4. the mutant is not killed by any tests */	return false;
		}
	}
	/**	
	 * @param code_file		the code file of which mutants will be used to compute score
	 * @param test_cases	the set of test cases being evaluated for its mutation score
	 * @return				[total_mutants, kiled_mutants, alive_mutants, mutation_score]
	 * @throws Exception
	 */
	private static double[] compute_mutation_score(MuTestProjectCodeFile 
			code_file, Collection<TestInput> test_cases) throws Exception {
		int total = 0, killed = 0;
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			if(is_killed_in(code_file, mutant, test_cases)) {
				killed++;
			}
			total++;
		}
		double score = ((double) killed) / (total + 0.0);
		return new double[] { total, killed, total - killed, score };
	}
	/**
	 * It implements the evaluation of fault-detection-ratio (mutation score) on given test cases
	 * @param project
	 * @param test_cases
	 * @throws Exception
	 */
	protected static void do_evaluate_detect_ratio(MuTestProject project, Collection<TestInput> test_cases) throws Exception {
		/* 1. obtain the code file in project to be tested */
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		
		/* 2. evaluate the number of mutants, killed, alive and score */
		double[] results = compute_mutation_score(code_file, test_cases);
		int killed = (int) results[1];
		int alive = (int) results[2];
		double score = ((int) (results[3] * 10000)) / 100.0;
		
		/* 3. print the score and killed, alive, mutant number */
		System.out.println("Evaluation: " + killed + " killed, " + alive + " alive, " + score + "% score.");
	}
	
	/* 4. evaluate the coincidental correctness and ratio */
	/**
	 * This derives the map from execution point (CFG-node) to the set of mutants seeded there
	 * @param code_file	the code file in which the mutant's execution point is extracted
	 * @return			the map from execution node (CFG-node) to the set of mutants injected in the node
	 * @throws Exception
	 */
	private static Map<CirExecution, Collection<Mutant>> derive_execution_mutants(MuTestProjectCodeFile code_file) throws Exception {
		if(code_file == null) {
			throw new IllegalArgumentException("Invalid code_file: null");
		}
		else {
			Map<CirExecution, Collection<Mutant>> results = 
					new HashMap<CirExecution, Collection<Mutant>>();
			for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
				try {
					Collection<CirMutation> cir_mutations = CirMutations.parse(mutant);
					for(CirMutation cir_mutation : cir_mutations) {
						CirExecution execution = cir_mutation.get_execution();
						if(!results.containsKey(execution)) {
							results.put(execution, new ArrayList<Mutant>());
						}
						results.get(execution).add(mutant);
					}
				}
				catch(Exception ex) { continue; }	/* pass if fail to parse */
			}
			return results;
		}
	}
	/**
	 * This derives the map from each test case to the mutants covered by it
	 * @param code_file
	 * @param test_cases
	 * @return		the map from each test case to the mutants covered by it
	 * @throws Exception
	 */
	private static Map<TestInput, Collection<Mutant>> derive_test_covered_mutants(
			MuTestProjectCodeFile code_file, Collection<TestInput> test_cases) throws Exception {
		if(code_file == null) {
			throw new IllegalArgumentException("Invalid code_file: null");
		}
		else if(test_cases == null) {
			throw new IllegalArgumentException("Invalid test_cases: null");
		}
		else {
			/* 1. initialize the map from CFG-node to the mutants seeded there */
			Map<CirExecution, Collection<Mutant>> exec_mutants = derive_execution_mutants(code_file);
			Map<TestInput, Collection<Mutant>> test_mutants = new HashMap<TestInput, Collection<Mutant>>();
			
			/* 2. it performs instrumental on test cases to generate coverage file (xxx.ins) */
			code_file.get_code_space().get_project().execute_instrumental(test_cases);
			
			/* 3. for each test, derive the coverage path and decide which mutants are covered */
			MuTestProjectTestSpace tspace = code_file.get_code_space().get_project().get_test_space();
			for(TestInput test_case : test_cases) {
				/* a. it derives the coverage path from the given test case */
				CStatePath coverage_path = tspace.load_instrumental_path(
						code_file.get_sizeof_template(), 
						code_file.get_ast_tree(), code_file.get_cir_tree(), test_case);
				
				/* b. it traverses through the path to extract covered execution nodes */
				Set<CirExecution> covered_executions = new HashSet<CirExecution>();
				for(CStateNode state_node : coverage_path.get_nodes()) {
					CirExecution execution = state_node.get_execution();
					covered_executions.add(execution);
				}
				
				/* c. it determines of which mutants are covered by the test */
				Set<Mutant> covered_mutants = new HashSet<Mutant>();
				for(CirExecution covered_execution : covered_executions) {
					if(exec_mutants.containsKey(covered_execution)) {
						covered_mutants.addAll(exec_mutants.get(covered_execution));
					}
				}
				
				/* d. it append the test to the mutants being covered */
				test_mutants.put(test_case, covered_mutants);
			}
			
			/* 4. the map from test case to the set of mutants covered by it */
			return test_mutants;
		}
	}
	/**
	 * It evaluates the ratios of coincidental correctness of each test case
	 * @param project		the project of which mutants are evaluated
	 * @param test_cases	the set of test cases used to evaluate CC-ratio
	 * @return				the map from test case to [killed, alive, alive_ratio]
	 * @throws Exception
	 */
	protected static Map<TestInput, Double[]> do_evaluate_coincidental_correctness(
			MuTestProject project, Collection<TestInput> test_cases) throws Exception {
		if(project == null) {
			throw new IllegalArgumentException("Invalid project as null");
		}
		else if(test_cases == null) {
			throw new IllegalArgumentException("Invalid test_cases: null");
		}
		else {
			/* 1. derive the code file for fault analysis and derive map from test case to mutants covered */
			MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
			Map<TestInput, Collection<Mutant>> test_mutants = derive_test_covered_mutants(code_file, test_cases);
			Map<TestInput, Double[]> test_cc_results = new HashMap<TestInput, Double[]>();
			
			/* 2. it computes the alive, killed mutants and CC-ratio covered by each test case in inputs  */
			for(TestInput test_case : test_mutants.keySet()) {
				/* (a) it derives the set of mutants being covered by the test */
				Collection<Mutant> covered_mutants = test_mutants.get(test_case);
				double killed_mutants = 0.0, alive_mutants = 0.0, alive_ratio = 0.0;
				
				/* (b) account the number of mutants being covered that are killed
				 * or not killed by the input test */
				for(Mutant mutant : covered_mutants) {
					if(is_killed_by(code_file, mutant, test_case)) {
						killed_mutants++;
					}
					else {
						alive_mutants++;
					}
				}
				if(killed_mutants > 0) {
					alive_ratio = killed_mutants / (killed_mutants + alive_mutants);
				}
				
				/* (c) report the results and record the results */
				test_cc_results.put(test_case, new Double[] { killed_mutants, alive_mutants, alive_ratio });
				alive_ratio = ((int) (alive_ratio * 10000)) / 100.0;
				System.out.println("\t\tCoincidental-Correct: \t#" + test_case.get_id() + "\t" + alive_ratio + "%");
			}
			
			/* 3. the map from each test case to [killed, alive, alive_ratio] */
			return test_cc_results;
		}
	}
	
	/* 5. visualize the coverage path based on AST-code lines */
	/**
	 * remove the spaces
	 * @param code
	 * @param max_length
	 * @return
	 */
	private static String strip_code(String code, int max_length) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length() && k < max_length; k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		if(buffer.length() >= max_length) {
			buffer.append("...");
		}
		return buffer.toString();
	}
	/**
	 * It prints the statement in sequence of being executed by the test-case
	 * @param project
	 * @param test_case
	 * @throws Exception
	 */
	protected static boolean do_visualize_coverage_path(MuTestProject project, 
					TestInput test_case, File output) throws Exception {
		/* 1. derive the coverage-path in CStatePath forms */
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();
		CStatePath coverage_path;
		try {
			coverage_path = project.get_test_space().load_instrumental_path(
					code_file.get_sizeof_template(), 
					code_file.get_ast_tree(), code_file.get_cir_tree(), test_case);
		}
		catch(Exception ex) { return false; }
		
		/* 2. print the statement-coverage in sequence of being covered */
		FileWriter writer = new FileWriter(output);
		writer.write("EID\tCLS\tCIR\tLINE\tAST\tCODE\n");
		for(CStateNode state_node : coverage_path.get_nodes()) {
			CirStatement statement = state_node.get_statement();
			String eid = state_node.get_execution().toString();
			String cls = statement.getClass().getSimpleName();
			cls = cls.substring(3, cls.length() - 4).strip();
			String cir = strip_code(statement.generate_code(true), 96);
			
			String line = "null", ast = "", code = "";
			if(statement.get_ast_source() != null) {
				AstNode ast_node = statement.get_ast_source();
				line = "" + ast_node.get_location().line_of();
				ast = ast_node.getClass().getSimpleName();
				ast = ast.substring(3, ast.length() - 4).strip();
				code = strip_code(ast_node.generate_code(), 96);
				code = "\"" + code + "\"";
			}
			writer.write(eid + "\t" + cls + "\t" + cir + "\t" + line + "\t" + ast + "\t" + code);
			writer.write("\n");
		}
		writer.close();
		return true;
	}
	
}
