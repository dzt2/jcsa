package test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.test.cmd.CCompiler;

public class MuTestProjectCompilation {
	
	private static final String root_path = "/home/dzt2/Development/Data/Code3/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 5;
	
	public static void main(String[] args) throws Exception {
		File[] cfiles = new File(root_path + "cfiles").listFiles();
		Scanner in = new Scanner(System.in);
		for(File cfile : cfiles) {
			if(cfile.getName().endsWith(".c")) {
				testing(cfile, in);
			}
		}
		in.close();
	}
	protected static void testing(File cfile, Scanner in) throws Exception {
		String name = get_name(cfile);
		File root = new File(root_path + "projects/" + name);
		if(!root.exists()) {
			System.out.println("----------------------------------");
			new_project(cfile);
			get_project(cfile);
			in.nextLine();
			System.out.println("----------------------------------");
			System.out.println();
		}
	}
	
	/* create */
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
	protected static MuTestProject new_project(File cfile) throws Exception {
		String name = get_name(cfile);
		File root = new File(root_path + "projects/" + name);
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
		File inputs_directory = new File(root_path + "inputs/" + name);
		if(!inputs_directory.exists()) FileOperations.mkdir(inputs_directory);
		project.set_inputs_directory(inputs_directory);
		project.add_test_inputs(test_suite_files);
		
		/* generate mutations */
		project.generate_mutants(get_classes());
		
		return project;
	}
	protected static MuTestProject get_project(File cfile) throws Exception {
		String name = get_name(cfile);
		File root = new File(root_path + "projects/" + name);
		MuTestProject project = new MuTestProject(root, MuCommandUtil.linux_util);
		System.out.println("Project-" + name);
		
		System.out.println("Configuration:");
		System.out.println("\tcompiler: " + project.get_config().get_compiler());
		System.out.println("\tlang_std: " + project.get_config().get_lang_standard());
		System.out.println("\tparameters: " + project.get_config().get_compile_parameters());
		System.out.println("\tmax_timeout: " + project.get_config().get_maximal_timeout_seconds());
		System.out.println("\tsizeof_template? " + project.get_config().get_sizeof_template_file().exists());
		System.out.println("\tpreprocess_macro? " + project.get_config().get_preprocess_macro_file().exists());
		System.out.println("\tmutation_head? " + project.get_config().get_mutation_head_file().exists());
		System.out.println("\tinstrument_head? " + project.get_config().get_instrument_head_file().exists());
		System.out.println("\tconfig_data? " + project.get_config().get_config_data_file().exists());
		
		System.out.println("Mutation-Code-Files:");
		for(MuTestProjectCodeFile code_file : project.get_code_space().get_code_files()) {
			System.out.println("\tname: " + code_file.get_name());
			System.out.println("\tcfile: " + code_file.get_cfile().exists());
			System.out.println("\tifile: " + code_file.get_ifile().exists());
			System.out.println("\tsfile: " + code_file.get_sfile().exists());
			System.out.println("\tmfile: " + code_file.get_mfile().exists());
			System.out.println("\tufile: " + code_file.get_ufile().exists());
			System.out.println("\tmutants: " + code_file.get_mutant_space().size());
		}
		
		System.out.println("Testing-Space with " + project.
				get_test_space().get_test_space().number_of_inputs() + " tests.");
		
		System.out.println("Testing Compilation on Mutations:");
		File error_directory = new File("result/err/" + name);
		if(!error_directory.exists()) { error_directory.mkdir(); }
		int[] error_total_numbers = project.assert_compilation(error_directory);
		System.out.println("Error-Rate: " + error_total_numbers[0] + "/" + error_total_numbers[1]);
		
		return project;
	}
	
}
