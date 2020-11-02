package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.project.MuTestFeatureWriter;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestResult;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.flwa.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.flwa.context.CirFunctionCallPathType;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.test.cmd.CCompiler;
import com.jcsa.jcparse.test.file.TestInput;

/**
 * To write the feature information.
 * 
 * @author yukimula
 *
 */
public class MuTestProjectFeatureWrite {
	
	private static final String root_path = "/home/dzt2/Development/Data/"; 
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 5;
	private static final String result_dir = root_path + "features/";
	private static final int maximal_distance = 1;
	private static final Random random = new Random(System.currentTimeMillis() * 769);
	
	public static void main(String[] args) throws Exception {
		for(File rfile : new File(root_path + "rprojects/").listFiles()) {
			File cfile = new File(root_path + "cfiles/" + rfile.getName() + ".c");
			testing(cfile);
		}
		
		/* testing(new File(root_path + "cfiles/is_prime.c")); */
	}
	
	/* project getters */
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
	private static CirCallContextInstanceGraph translate(CirTree cir_tree) throws Exception {
		CirFunction root_function = cir_tree.get_function_call_graph().get_function("main");
		return CirCallContextInstanceGraph.graph(root_function, 
				CirFunctionCallPathType.unique_path, -1);
	}
	private static TestInput random_test_case(MuTestProject project) throws Exception {
		int number = project.get_test_space().number_of_test_inputs();
		int tid = (Math.abs(random.nextInt())) % number;
		return project.get_test_space().get_test_space().get_input(tid);
	}
	/**
	 * @param project
	 * @param muta_classes
	 * @return the set of test cases that can kill all the non-equivalent mutations of specified classes
	 * @throws Exception
	 */
	private static Set<TestInput> sufficient_test_cases(MuTestProjectCodeFile code_file, Collection<MutaClass> muta_classes) throws Exception {
		Set<Mutant> mutants = new HashSet<Mutant>();
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			if(muta_classes.contains(mutant.get_mutation().get_class())) {
				mutants.add(mutant);
			}
		}
		
		Set<TestInput> test_cases = new HashSet<TestInput>(); Set<Mutant> removes = new HashSet<Mutant>();
		int test_size = code_file.get_code_space().get_project().get_test_space().number_of_test_inputs();
		
		while(!mutants.isEmpty()) {
			Mutant mutant = mutants.iterator().next();
			removes.clear(); removes.add(mutant);
			
			MuTestProjectTestResult result = code_file.get_code_space().
					get_project().get_test_space().get_test_result(mutant);
			if(result != null && result.get_kill_set().degree() > 0) {
				/* select a random test case from killing set */
				int number = result.get_kill_set().degree();
				number = (Math.abs(random.nextInt())) % number;
				TestInput test_case = null;
				for(int tid = 0; tid < test_size && number >= 0; tid++) {
					if(result.get_kill_set().get(tid)) {
						number--;
						test_case = code_file.get_code_space().get_project().get_test_space().get_test_space().get_input(tid);
					}
				}
				
				if(test_case == null)
					throw new RuntimeException("Invalid test_case as null");
				else
					test_cases.add(test_case);
				
				for(Mutant in_mutant : mutants) {
					MuTestProjectTestResult in_result = code_file.get_code_space().
							get_project().get_test_space().get_test_result(in_mutant);
					if(in_result != null) {
						if(in_result.get_kill_set().get(test_case.get_id())) {
							removes.add(in_mutant);
						}
					}
					else {
						removes.add(in_mutant);
					}
				}
			}
			
			mutants.removeAll(removes);
		}
		return test_cases;
	}
	private static void write_features(MuTestProject project, File cfile) throws Exception {
		MuTestProjectCodeFile cspace = project.get_code_space().get_code_file(cfile);
		CDependGraph dependence_graph = CDependGraph.graph(translate(cspace.get_cir_tree()));
		File directory = new File(result_dir + project.get_name());
		if(!directory.exists()) directory.mkdir();
		MuTestFeatureWriter writer = new MuTestFeatureWriter();
		
		/* static feature generation */
		TestInput test_case = random_test_case(project);
		Collection<MutaClass> killed_classes = new HashSet<MutaClass>();
		killed_classes.add(MutaClass.STRP); killed_classes.add(MutaClass.ETRP);
		Collection<TestInput> test_cases = sufficient_test_cases(cspace, killed_classes);
		System.out.println("\t--> Select test#" + test_case.get_id() + " and " + test_cases.size() + " test cases.");
		writer.write_features(project, cfile, directory, dependence_graph, test_case, test_cases, maximal_distance);
	}
	protected static void testing(File cfile) throws Exception {
		MuTestProject project = get_project(cfile);
		System.out.println("Write features for " + project.get_name());
		write_features(project, cfile);
		System.out.println("\n");
	}
	
}
