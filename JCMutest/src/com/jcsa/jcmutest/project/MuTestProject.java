package com.jcsa.jcmutest.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.test.cmd.CCompiler;

/**
 * Mutation test project provides the top-perspective to manage the data and
 * testing process for mutation analysis over the C programs.
 * 
 * @author yukimula
 *
 */
public class MuTestProject {
	
	/* definitions */
	private MuTestProjectFiles files;
	private MuTestProjectConfig config;
	private MuTestCodeSpace code_space;
	private MuTestInputSpace test_space;
	private MuTestExecution exec_space;
	public MuTestProject(File root, MuCommandUtil command_util) throws Exception {
		if(root == null)
			throw new IllegalArgumentException("Invalid root: null");
		else {
			this.files = new MuTestProjectFiles(this, root);
			this.config = new MuTestProjectConfig(this, command_util);
			this.code_space = new MuTestCodeSpace(this);
			this.test_space = new MuTestInputSpace(this);
			this.exec_space = new MuTestExecution(this);
		}
	}
	
	/* getters */
	/**
	 * @return the name of the mutation test project
	 */
	public String get_name() {
		return this.files.get_root().getName();
	}
	/**
	 * @return the files in the mutation test project
	 */
	public MuTestProjectFiles get_files() { return this.files; } 
	/**
	 * @return the configuration data in test project
	 */
	public MuTestProjectConfig get_config() { return this.config; }
	/**
	 * @return the code space in the project 
	 */
	public MuTestCodeSpace get_code_space() { return this.code_space; }
	public MuTestInputSpace get_test_space() { return this.test_space; }
	public MuTestExecution get_exec_space() { return this.exec_space; }
	
	/* setters */
	/**
	 * 
	 * set the configuration data in mutation test project
	 * @param compiler the system compiler used for compilation
	 * @param lang_std the language standard used to parse source code
	 * @param compilation_parameters the parameters like -lm to compile C code
	 * @param sizeof_template_file configs/cruntime.txt for sizeof operation in static analysis
	 * @param instrument_head_file config/jcinst.h to compile instrumental code
	 * @param preprocess_macro_file config/linux.h to -imacros the preprocess
	 * @param mutation_head_file config/jcmutest.h to compile the mutation code
	 * @throws Exception
	 */
	public void set_config(CCompiler compiler, ClangStandard lang_std, 
			Iterable<String> compilation_parameters, File sizeof_template_file, 
			File instrument_head_file, File preprocess_macro_file, 
			File mutation_head_file, long max_timeout_seconds) throws Exception {
		this.config.set(compiler, lang_std, 
				compilation_parameters, sizeof_template_file, 
				instrument_head_file, preprocess_macro_file, 
				mutation_head_file, max_timeout_seconds);
	}
	/**
	 * input the cfiles, hfiles and lfiles, and building the code file space,
	 * this will clear the mutant spaces
	 * @param cfiles xxx.c files before pre-processing
	 * @param hfiles xxx.h header files for compilation
	 * @param lfiles xxx.lib library files to compile
	 * @throws Exception
	 */
	public void input_code(Iterable<File> cfiles, Iterable<File> hfiles, 
			Iterable<File> lfiles) throws Exception {
		this.code_space.input_code_files(cfiles, hfiles, lfiles);
	}
	/**
	 * update and generate the mutations in each code file of the space
	 * @param mutation_classes
	 * @throws Exception
	 */
	public void generate_mutants(Iterable<MutaClass> mutation_classes) throws Exception {
		this.code_space.update_mutations(mutation_classes);
	}
	/**
	 * update the test inputs used for executing the program under test and
	 * generate the test scripts file.
	 * @param test_suite_files
	 * @throws Exception
	 */
	public void input_tests(Iterable<File> test_suite_files,
			File inputs_directory) throws Exception {
		this.test_space.set_test_space(test_suite_files);
		FileOperations.copy_all(inputs_directory, this.files.get_inputs_directory());
	}
	/**
	 * Execute tests against original program
	 * @throws Exception
	 */
	public void execute_original_program() throws Exception {
		this.exec_space.execute_normal_testing();
	}
	/**
	 * Execute tests against original program with instrumental analysis
	 * @throws Exception
	 */
	public void execute_instrumental_program() throws Exception {
		this.exec_space.execute_instrumental_testing();
	}
	/**
	 * Execute tests against the mutated program
	 * @param mutant mutation being tested over
	 * @throws Exception
	 * @return the result of the testing on specified mutant
	 */
	public MuTestResult execute_mutation_program(Mutant mutant) throws Exception {
		return this.exec_space.execute_mutation_testing(mutant);
	}
	/**
	 * @return the mutants that can not be successfully compiled
	 * @throws Exception
	 */
	public List<Mutant> test_compile_mutants(int start_id) throws Exception {
		List<Mutant> error_mutants = new ArrayList<Mutant>();
		for(MuTestCodeFile code_file : this.code_space.get_code_files()) {
			for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
				if(mutant.get_id() >= start_id) {
					if(!this.exec_space.test_compile(mutant)) {
						throw new RuntimeException("Failed to compile " + mutant);
					}
					else {
						System.out.println("\t==> Test-in-" + 
								code_file.get_mutant_space().size() + ": " +
								code_file.get_name() + mutant.toString());
					}
				}
			}
		}
		return error_mutants;
	}
	
}
