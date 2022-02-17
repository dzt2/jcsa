package test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.jcsa.jcmutest.mutant.MutaClass;
import com.jcsa.jcmutest.mutant.ast2mutant.MutationGenerators;
import com.jcsa.jcmutest.project.MuTestProject;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcmutest.project.MuTestProjectTestSpace;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.test.cmd.CCompiler;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;
import com.jcsa.jcparse.test.state.CStateUnit;

public class MutExecutionPathTest {

	private static final Random random = new Random();
	private static final String root_path = "/home/dzt2/Development/Data/";
	private static final String code_path = "/home/dzt2/Development/Data/cfiles/";
	private static final File sizeof_template_file = new File("config/cruntime.txt");
	private static final File instrument_head_file = new File("config/jcinst.h");
	private static final File preprocess_macro_file = new File("config/linux.h");
	private static final File mutation_head_file = new File("config/jcmutest.h");
	private static final long max_timeout_seconds = 1;
	private static final String result_dir = "results/paths/";

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
	private static MuTestProject get_project(File cfile) throws Exception {
		String name = get_name(cfile);
		File root = new File(root_path + "rprojects/" + name);

		if(!root.exists()) {
			MuTestProject project = new MuTestProject(root, MuCommandUtil.linux_util);

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

			return project;
		}
		else {
			return new MuTestProject(root, MuCommandUtil.linux_util);
		}
	}
	private static String strip_code(String code) {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				ch = ' ';
			}
			buffer.append(ch);
		}
		return buffer.toString();
	}
	private static void write_state_path(FileWriter writer, CStatePath path) throws Exception {
		int index = 0;
		for(CStateNode state_node : path.get_nodes()) {
			CirExecution execution = state_node.get_execution();
			writer.write("(" + (index++) + "):\t" + execution.toString() + "\n");
			writer.write("\t[STMT]\t\"" + strip_code(execution.get_statement().generate_code(true)) + "\"\n");
			
			for(CStateUnit unit : state_node.get_units()) {
				writer.write("\t[UNIT]");
				writer.write("\t\"" + strip_code(unit.get_expression().generate_code(true)) + "\"");
				writer.write(": " + strip_code(SymbolFactory.sym_expression(unit.get_value()).generate_code(false)));
				writer.write("\n");
			}
			
			writer.write("\n");
		}
	}
	public static void testing(File cfile, int tid) throws Exception {
		System.out.println("Testing on " + cfile.getName());
		
		/* load the mutation test project and print the basic information */
		MuTestProject project = get_project(cfile);
		MuTestProjectCodeFile code_file = project.get_code_space().get_code_file(cfile);
		MuTestProjectTestSpace tspace = project.get_test_space();
		System.out.println("\t1. Load " + code_file.get_mutant_space().size() + 
				" mutations and " + tspace.number_of_test_inputs() + " tests.");
		
		/* update the test integer ID to obtain the state path for coverage */
		if(tid < 0) {
			tid = Math.abs(random.nextInt()) % tspace.number_of_test_inputs();
		}
		TestInput test_case = tspace.get_test_space().get_input(tid);
		System.out.println("\t2. Select TEST#" + tid + " for coverage analysis");
		
		/* load the coverage path and execution state from specified test case */
		CStatePath state_path = null;
		while(true) {
			try {
				state_path = tspace.load_instrumental_path(code_file.get_sizeof_template(), 
							code_file.get_ast_tree(), code_file.get_cir_tree(), test_case);
				if(state_path == null) {
					project.execute_instrumental(tspace.get_test_inputs(tid, tid + 1));
				}
				else {
					System.out.println("\t3. Succeed to load coverage state path...");
					break;
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("\t3. Unable to load coverage path and states...");
				state_path = null;
				break;
			}
		}
	
		/* write the state path if possible */
		if(state_path != null) {
			FileWriter writer = new FileWriter(new File(result_dir + code_file.get_name() + ".txt"));
			writer.write("Test: " + tspace.get_test_space().get_input(tid).get_parameter() + "\n");
			write_state_path(writer, state_path);
			writer.close();
			System.out.println("\t4. Write the state coverage path to output.");
		}
		System.out.println();
	}
	public static void main(String[] args) throws Exception {
		for(File cfile : new File(code_path).listFiles()) {
			if(cfile.getName().contains("days")) {
				testing(cfile, 32);
			}
			else {
				// testing(cfile, -1);
			}
		}
	}

}
