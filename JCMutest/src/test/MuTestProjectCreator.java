package test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.MutaClass;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.test.cmd.CCompiler;

public class MuTestProjectCreator {
	
	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 1;

	public static void main(String[] args) {
		for(File cfile : new File(root_path + "cfiles/").listFiles()) {
			if(cfile.getName().endsWith(".c")) {
				try {
					MuTestProjectCodeFile codeFile = get_project(cfile);
					int functions = 0;
					for(CirFunction function : codeFile.get_ast_file().get_function_graph().get_functions()) {
						function.get_name();
						functions++;
					}
					int testNumber = codeFile.get_code_space().get_project().get_test_space().number_of_test_inputs();
					int mutantNumber = codeFile.get_mutant_space().size();
					System.out.println("Testing " + cfile.getName() + ": " + functions + 
							" Functions; " + testNumber + " Tests; " + mutantNumber + " Mutants.");
				}
				catch(Exception ex) {
					System.out.println("Testing " + cfile.getName() + " FAILED!!!");
					continue;
				}
			}
		}
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
		File root = new File(root_path + "projectsAll/" + name);

		if(!root.exists()) {
			project = new MuTestProject(root, MuCommandUtil.linux_util);

			/* set configuration data */
			List<String> parameters = new ArrayList<String>();
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
	
}
