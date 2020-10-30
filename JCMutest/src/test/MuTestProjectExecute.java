package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.MutantSpace;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
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
		testing("is_prime.c", 0, 0, false, true);
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
			File inputs_directory = new File(root_path + "inputs/" + name);
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
	private static void execute_project(MuTestProject project, File cfile, int mid, 
			int tid, boolean test_mutation, boolean test_instrumentation) throws Exception {
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_file(cfile);
		
		List<Mutant> mutants = new ArrayList<Mutant>();
		MutantSpace mspace = code_file.get_mutant_space();
		for(int k = mid; k < mspace.size(); k++) {
			mutants.add(mspace.get_mutant(k));
		}
		
		Collection<TestInput> tests = project.get_test_space().get_test_inputs(
				tid, project.get_test_space().number_of_test_inputs());
		
		if(test_mutation) 
			project.execute(mutants, tests);
		if(test_instrumentation)
			project.execute_instrumental(tests);
	}
	protected static void testing(String name, int mid, int tid, 
			boolean test_mutation, boolean test_instrumentation) throws Exception {
		File cfile = new File(root_path + "cfiles/" + name);
		MuTestProject project = get_project(cfile);
		System.out.println("Generate project for " + name);
		execute_project(project, cfile, mid, tid, test_mutation, test_instrumentation);
	}
	
}
