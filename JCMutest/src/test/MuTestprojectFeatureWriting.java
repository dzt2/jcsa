package test;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.stat.CirMutationFeatureWriter;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcmutest.project.MuTestProjectTestSpace;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.test.file.TestInput;

public class MuTestprojectFeatureWriting {
	
	private static final String root_path = "/home/dzt2/Development/Data/projects/";
	private static String result_dir = "/home/dzt2/Development/Data/zexp/features2/";
	private static final double random_test_ratio = 0.005;
	private static final int min_test_number = 24;
	private static final int max_infecting_times = 4;
	private static final Random random = new Random(System.currentTimeMillis());
	
	public static void main(String[] args) throws Exception {
		print_static_features();
		print_dynamic_features();
	}
	protected static void print_static_features() throws Exception {
		/** static features **/
		result_dir = "/home/dzt2/Development/Data/zexp/features/";
		for(File root : new File(root_path).listFiles()) {
			testing(root, false);
		}
	}
	protected static void print_dynamic_features() throws Exception {
		/** dynamic features **/
		result_dir = "/home/dzt2/Development/Data/zexp/deatures/";
		String[] file_names = new String[] {
				"bi_search",
				"check_date",
				"days",
				"fizz_buzz",
				"hereon_triangle",
				"is_prime",
				"medium",
				"minmax",
				"profit",
				"tcas",
				"triangle",
				// "bubble_sort", 
				// "insert_sort", 
				// "insert_sort2", 
				// "calendar", 
				// "prime_factor", 
				// "quick_sort", 
				// "gen_primes", 
		};
		for(String file_name : file_names) {
			testing(new File(root_path + file_name), true);
		}
	}
	
	/* testing functions */
	private static MuTestProject get_project(File root) throws Exception {
		return new MuTestProject(root, MuCommandUtil.linux_util);
	}
	private static void testing(File root, boolean selected) throws Exception {
		/* 1. open project and get data interface */
		MuTestProject project = get_project(root);
		File output_directory = new File(result_dir + project.get_name());
		System.out.println("Testing on " + project.get_name() + " for writing features.");
		FileOperations.mkdir(output_directory);
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_files().iterator().next();

		/* 2. select test cases and generate instrumental files. */
		Collection<TestInput> test_cases;
		if(selected) {
			Collection<MutaClass> classes = new HashSet<>();
			classes.add(MutaClass.STRP); classes.add(MutaClass.BTRP);
			Set<Mutant> selected_mutants = select_mutants(code_file, classes);
			test_cases = select_tests(selected_mutants, project.get_test_space());
			System.out.println("\t==> Select " + test_cases.size() + " test cases from " +
								project.get_test_space().number_of_test_inputs() + " inputs.");
		}
		else { test_cases = null; }		/* no test case is used for dynamic generation */

		/* 3. Generate feature information to output directory finally */
		CirMutationFeatureWriter.write_features(code_file, output_directory, max_infecting_times, test_cases);
		System.out.println();
	}
	
	/* dynamic test cases selection and generation */
	/**
	 * select mutants w.r.t. the given mutation operators
	 * @param code_file
	 * @param muta_classes
	 * @return
	 */
	private static Set<Mutant> select_mutants(MuTestProjectCodeFile code_file, Collection<MutaClass> muta_classes) {
		Set<Mutant> mutants = new HashSet<>();
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			if(muta_classes.contains(mutant.get_mutation().get_class())) {
				mutants.add(mutant);
			}
		}
		return mutants;
	}
	/**
	 * @param mutants
	 * @return a randomly selected mutant from the set
	 */
	private static Mutant select_random_mutant(Collection<Mutant> mutants) {
		int number = mutants.size();
		if(number > 0) {
			int counter = random.nextInt();
			counter = Math.abs(counter) % number;
			Mutant selected_mutant = null;
			for(Mutant mutant : mutants) {
				selected_mutant = mutant;
				if(counter-- > 0) {
					break;
				}
			}
			return selected_mutant;
		}
		return null;
	}
	/**
	 * @param mutant
	 * @param tspace
	 * @return select a random test cases that kill the target mutant
	 * @throws Exception
	 */
	private static TestInput select_random_test(Mutant mutant, MuTestProjectTestSpace tspace) throws Exception {
		MuTestProjectTestResult tresult = tspace.get_test_result(mutant);
		if(tresult != null && tresult.get_kill_set().degree() > 0) {
			int number = tresult.get_kill_set().degree();
			int counter = Math.abs(random.nextInt());
			counter = counter % number;
			TestInput test_case = null;
			for(int tid = 0; tid < tspace.number_of_test_inputs(); tid++) {
				if(tresult.get_kill_set().get(tid)) {
					test_case = tspace.get_test_space().get_input(tid);
					if(counter-- > 0) {
						break;
					}
				}
			}
			return test_case;
		}
		return null;
	}
	/**
	 * select random test from the space
	 * @param tspace
	 * @return
	 * @throws Exception
	 */
	private static TestInput select_random_test(MuTestProjectTestSpace tspace) throws Exception {
		int tnumber = tspace.get_test_space().number_of_inputs();
		int counter = Math.abs(random.nextInt()) % tnumber;
		TestInput test_case = null;
		for(int tid = 0; tid < tnumber; tid++) {
			test_case = tspace.get_test_space().get_input(tid);
			if(counter-- <= 0)
				break;
		}
		return test_case;
	}
	/**
	 * remove all the mutants killed by the test
	 * @param mutants
	 * @param test
	 * @throws Exception
	 */
	private static void kill_mutants_in(Set<Mutant> mutants, TestInput
			test, MuTestProjectTestSpace tspace) throws Exception {
		Set<Mutant> removed_mutants = new HashSet<>();
		for(Mutant mutant : mutants) {
			MuTestProjectTestResult result = tspace.get_test_result(mutant);
			if(result == null || result.get_kill_set().degree() == 0) {
				removed_mutants.add(mutant);
			}
			else if(result.get_kill_set().get(test.get_id())) {
				removed_mutants.add(mutant);
			}
		}
		mutants.removeAll(removed_mutants);
		return;
	}
	/**
	 * @param mutants
	 * @param tspace
	 * @return the set of test cases for killing all the mutants selected
	 * @throws Exception
	 */
	private static Set<TestInput> select_tests(Set<Mutant> mutants, MuTestProjectTestSpace tspace) throws Exception {
		Set<TestInput> test_cases = new HashSet<>();

		/* minimal test cases */
		while(!mutants.isEmpty()) {
			Mutant next_mutant = select_random_mutant(mutants);
			mutants.remove(next_mutant);

			TestInput test_case = select_random_test(next_mutant, tspace);
			if(test_case != null) {
				test_cases.add(test_case);
				kill_mutants_in(mutants, test_case, tspace);
			}
		}

		/* random test selection to minimal size */
		int minimal_size = ((int) (tspace.number_of_test_inputs() * random_test_ratio)) + 1;
		if(minimal_size < min_test_number) minimal_size = min_test_number;	/* minimal size */
		while(test_cases.size() < minimal_size) {
			TestInput test_case = select_random_test(tspace);
			if(test_case != null)
				test_cases.add(test_case);
		}
		return test_cases;
	}
	
}
