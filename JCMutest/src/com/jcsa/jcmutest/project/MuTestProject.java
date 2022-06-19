package com.jcsa.jcmutest.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.jcsa.jcmutest.mutant.MutaClass;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.test.cmd.CCompiler;
import com.jcsa.jcparse.test.file.TestInput;

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
	private MuTestProjectCodeSpace code_space;
	private MuTestProjectTestSpace test_space;
	private MuTestProjectExecSpace exec_space;
	public MuTestProject(File root, MuCommandUtil command_util) throws Exception {
		if(root == null)
			throw new IllegalArgumentException("Invalid root: null");
		else {
			this.files = new MuTestProjectFiles(this, root);
			this.config = new MuTestProjectConfig(this, command_util);
			this.code_space = new MuTestProjectCodeSpace(this);
			this.test_space = new MuTestProjectTestSpace(this);
			this.exec_space = new MuTestProjectExecSpace(this);
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
	 * @return code space of the project wher code and mutations are managed
	 */
	public MuTestProjectCodeSpace get_code_space() { return this.code_space; }
	/**
	 * @return test space of the project manages the test data used in testing
	 */
	public MuTestProjectTestSpace get_test_space() { return this.test_space; }
	/**
	 * @return execution space of the project manages the compilation and execution
	 * 		   of the testing process.
	 */
	public MuTestProjectExecSpace get_exec_space() { return this.exec_space; }
	/**
	 * @param beg
	 * @param end
	 * @return the tests inputs range from [beg, end)
	 * @throws Exception
	 */
	public Collection<TestInput> get_tests(int beg, int end) throws Exception {
		return this.test_space.get_test_inputs(beg, end);
	}
	/**
	 * @param cfile
	 * @param beg
	 * @param end
	 * @return the mutants range from [beg, end) w.r.t. specified code file
	 * @throws Exception
	 */
	public Collection<Mutant> get_mutants(File cfile, int beg, int end) throws Exception {
		Collection<Mutant> mutants = new ArrayList<>();
		MuTestProjectCodeFile code_file = this.code_space.get_code_file(cfile);
		if(code_file != null) {
			for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
				if(mutant.get_id() >= beg && mutant.get_id() < end) {
					mutants.add(mutant);
				}
			}
		}
		return mutants;
	}

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
	 * delete the original code files and set the new code items for specified input source code files.
	 * @param cfiles xxx.c files
	 * @param hfiles xxx.h files
	 * @param lfiles xxx.lib files
	 * @throws Exception
	 */
	public void set_cfiles(Iterable<File> cfiles, Iterable<File> hfiles, Iterable<File> lfiles) throws Exception {
		this.code_space.set_cfiles(cfiles, hfiles, lfiles);
	}
	/**
	 * generate the mutants in space of each code file in the project
	 * @param mutation_classes
	 * @throws Exception
	 */
	public void generate_mutants(Iterable<MutaClass> mutation_classes) throws Exception {
		this.code_space.set_mutants(mutation_classes);
	}
	/**
	 * set the input data in inputs directory
	 * @param test_suite_files
	 * @param inputs_directory
	 * @throws Exception
	 */
	public void set_inputs_directory(File inputs_directory) throws Exception {
		this.test_space.set_inputs_directory(inputs_directory);
	}
	/**
	 * add new test inputs to the test space
	 * @param test_suite_files
	 * @throws Exception
	 */
	public void add_test_inputs(Iterable<File> test_suite_files) throws Exception {
		this.test_space.add_test_inputs(test_suite_files);
	}

	/* execution utilities */
	/**
	 * To assert whether the normal, instrumental and mutated program can be
	 * correctly compiled, if not, the incorrect code will be output on the
	 * directory of specified. [error_number, total_number]
	 * @param error_directory
	 * @throws Exception
	 */
	public int[] assert_compilation(File error_directory) throws Exception {
		this.exec_space.compile_normal_program();
		this.exec_space.compile_instrumental_program();
		int total_number = 0, error_number = 0;
		for(MuTestProjectCodeFile code_file : this.code_space.get_code_files()) {
			for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
				if(this.exec_space.compile_mutation_program(mutant)) {
					System.out.println("\t==> Pass on " + code_file.get_name() +
							"[" + mutant.get_id() + "/" + code_file.get_mutant_space().size() + "]:\t" + mutant.get_mutation());
				}
				else {
					File target = new File(error_directory.getAbsolutePath() + "/" +
										code_file.get_name() + mutant.get_mutation().
										get_class() + "." + mutant.get_id() + ".c");
					FileOperations.copy(code_file.get_mfile(), target);
					error_number++;
				}
				total_number++;
			}
		}
		return new int[] { error_number, total_number };
	}
	/**
	 * execute the mutation testing by executing each test against each mutant and generating their results
	 * in linear way (classical method will take much time to complete the testing process).
	 * @param mutants
	 * @param tests
	 * @throws Exception
	 * @return the seconds taken for executing the mutation testing process
	 */
	public long execute(Collection<Mutant> mutants, Collection<TestInput> tests) throws Exception {
		long orig_begtime = System.currentTimeMillis();
		this.exec_space.generate_exec_scripts(tests);
		this.exec_space.execute_normal_program();
		long orig_endtime = System.currentTimeMillis();
		long orig_seconds = (orig_endtime - orig_begtime) / 1000;
		System.out.println("\t1. Complete original program on " + tests.size() + " tests: [" + orig_seconds + " seconds]");
		
		System.out.println("\t2. Start mutation testing over " + mutants.size() + " mutants.");
		long beg = System.currentTimeMillis(), counter = 0, total_size = mutants.size();
		for(Mutant mutant : mutants) {
			counter++;
			System.out.print("\t\t(" + counter + "/" + total_size + ") \t" + mutant);
			
			long local_begtime = System.currentTimeMillis();
			this.exec_space.execute_mutation_program(mutant);
			long local_endtime = System.currentTimeMillis();
			long local_secs = (local_endtime - local_begtime) / 1000;
			
			System.out.print("\t==> [" + local_secs + " seconds]\n");
		}
		long end = System.currentTimeMillis();
		long time = (end - beg) / 1000;
		System.out.println("\t3. Complete all the testing process using " + time + " seconds");
		return time;
	}
	/**
	 * execute the instrumental program over the specified test inputs
	 * @param tests
	 * @throws Exception
	 */
	public void execute_instrumental(Collection<TestInput> tests) throws Exception {
		this.exec_space.generate_exec_scripts(tests);
		this.exec_space.execute_instrumental_program();
	}
	/**
	 * @param mutants
	 * @return
	 * @throws Exception
	 */
	public Collection<Mutant> check_trivial_equivalence(MuTestProjectCodeFile code_file, String[] optimize_arguments) throws Exception {
		Collection<Mutant> equivalent_mutants = new HashSet<Mutant>();
		FileOperations.delete(this.exec_space.get_normal_executional_file());
		int counter = 0, total = code_file.get_mutant_space().size();
		
		for(Mutant mutant : code_file.get_mutant_space().get_mutants()) {
			System.out.println("\t\t==> TCE_Proceed[" + counter + "/" + total + "]");
			if(this.exec_space.compile_equivalence_check(mutant, optimize_arguments)) {
				equivalent_mutants.add(mutant);
			}
			counter++;
		}
		return equivalent_mutants;
	}
	
}
