package com.jcsa.jcmutest.project;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.test.file.TestInput;

/**
 * The execution space where the program and test script files are compiled,
 * generated and executed in an automatic manner.<br>
 * <code>
 * 	0.	inputs: mutants, tests;	output: results.			<br>
 * 	1. 	generate_exec_scripts(tests)						<br>
 * 	2. 	execute_normal_program()							<br>
 * 	3. 	[execute_instrumental_program()]?					<br>
 * 	4. 	for mutant in mutants:								<br>
 * 	5.		result = execute_mutation_program(mutant)		<br>
 * 	6. 		results.add(result)								<br>
 * 	7.	return results										<br>
 * </code>
 * @author yukimula
 *
 */
public class MuTestProjectExecSpace {

	/** the head of the bash-shell script language **/
	private static final String bash_shell_head = "#! /bin/bash\n\n";
	/** the template for changing current directory **/
	private static final String cd_template = "cd %s\n\n";
	/** command for deleting the instrumental result file in testing **/
	private static final String remove_files_template = "rm %s/*\n";
	/** command for copying source to target file **/
	private static final String copy_file_template = "cp %s %s\n";

	/* definition */
	private MuTestProject project;
	protected MuTestProjectExecSpace(MuTestProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			this.project = project;
		}
	}

	/* getters */
	/**
	 * @return the project that defines the code space as well
	 */
	public MuTestProject get_project() { return this.project; }
	/**
	 * @return the directory where the executional and script files are generated
	 */
	public File get_efiles_directory() {
		return this.project.get_files().get_efiles_directory();
	}
	/**
	 * @return executional program compiled from original program (without instrumentation)
	 */
	public File get_normal_executional_file() {
		return new File(this.get_efiles_directory().getAbsolutePath() + "/" + this.project.get_name() + ".n.out");
	}
	/**
	 * @return executional program compiled from original program (with instrumentation)
	 */
	public File get_instrumental_executional_file() {
		return new File(this.get_efiles_directory().getAbsolutePath() + "/" + this.project.get_name() + ".s.out");
	}
	/**
	 * @return executional program compiled from mutated program (with some mutant)
	 */
	public File get_mutation_executional_file() {
		return new File(this.get_efiles_directory().getAbsolutePath() + "/" + this.project.get_name() + ".m.out");
	}
	/**
	 * @return the test script file for executing program compiled from normal original program
	 */
	public File get_normal_test_script_file() {
		return new File(this.get_efiles_directory().getAbsolutePath() + "/" + this.project.get_name() + ".n.sh");
	}
	/**
	 * @return the test script file for executing program compiled from instrumental original program
	 */
	public File get_instrumental_test_script_file() {
		return new File(this.get_efiles_directory().getAbsolutePath() + "/" + this.project.get_name() + ".s.sh");
	}
	/**
	 * @return the test script file for executing program compiled from mutated program
	 */
	public File get_mutation_test_script_file() {
		return new File(this.get_efiles_directory().getAbsolutePath() + "/" + this.project.get_name() + ".m.sh");
	}

	/* running methods */
	/**
	 * #!bash
	 * cd efiles
	 * rm normal_outputs/*
	 * [efile inputs (timeout) > output 2> errors]+
	 * @throws Exception
	 */
	private void generate_normal_shell(Iterable<TestInput> tests, long timeout) throws Exception {
		/*
		 * #!bash
		 * cd project/efiles/
		 * rm project/test/n_outputs/*
		 */
		FileWriter writer = new FileWriter(this.get_normal_test_script_file());
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template,
				project.get_files().get_efiles_directory().getAbsolutePath()));
		writer.write(String.format(remove_files_template, this.project.
				get_test_space().get_normal_output_directory().getAbsolutePath()));

		/*
		 * {(timeout max_seconds)? [project/xxx.n.out {input_parameter} > n_outputs/tid.out 2> n_outputs/tid.err]}+
		 */
		for(TestInput input : tests) {
			String command = input.command(this.get_normal_executional_file(), this.
					project.get_test_space().get_normal_output_directory(), timeout);
			writer.write(command);
			writer.write("\n");
		}
		writer.write("\n");
		writer.close();
	}
	/**
	 * 	#!bash
	 * 	cd efiles/
	 * 	rm s_outputs/*
	 * 	{
	 * 		command > instrument.out 2> instrument.err
	 * 		cp instrument.txt m_output/tid.ins
	 * 	}
	 * @param tests
	 * @param timeout
	 * @throws Exception
	 */
	private void generate_instrumental_shell(Iterable<TestInput> tests, long timeout) throws Exception {
		/*
		 * #!bash
		 * cd project/efiles/
		 * rm project/s_outputs/*
		 */
		FileWriter writer = new FileWriter(this.get_instrumental_test_script_file());
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template, project.get_files().get_efiles_directory().getAbsolutePath()));
		writer.write(String.format(remove_files_template,
						this.project.get_test_space().get_instrumental_output_directory().getAbsolutePath()));

		/*
		 * (timeout max_seconds)? [efiles/xxx.s.out input_parameter > instrument.out 2> instrument.err]
		 * cp instrument.txt project/s_outputs/tid.ins
		 */
		for(TestInput input : tests) {
			writer.write("\n");
			writer.write(input.command(this.get_instrumental_executional_file(),
					this.project.get_test_space().get_instrumental_out_file(),
					this.project.get_test_space().get_instrumental_err_file(), timeout));
			writer.write("\n");
			writer.write(String.format(copy_file_template,
					this.project.get_test_space().get_instrumental_txt_file(),
					input.get_instrument_file(this.project.get_test_space().
							get_instrumental_output_directory()).getAbsolutePath()));
		}
		writer.write("\n");
		writer.close();
	}
	/**
	 * #!bash
	 * cd efiles
	 * rm mutation_outputs/*
	 * efile inputs (timeout) > output 2> errors
	 * @param tests
	 * @param timeout
	 * @throws Exception
	 */
	private void generate_mutation_shell(Iterable<TestInput> tests, long timeout) throws Exception {
		/*
		 * #!bash
		 * cd project/efiles/
		 * rm project/m_outputs/*
		 * {
		 * (timeout max_seconds)? project/xxx.m.out input_parameter > m_ouputs/tid.out 2> m_outputs/tid.err +
		 * }
		 */
		FileWriter writer = new FileWriter(this.get_mutation_test_script_file());
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template, project.get_files().get_efiles_directory().getAbsolutePath()));
		writer.write(String.format(remove_files_template, this.project.
				get_test_space().get_mutation_output_directory().getAbsolutePath()));

		for(TestInput input : this.project.get_test_space().get_test_space().get_inputs()) {
			String command = input.command(this.get_mutation_executional_file(),
					this.project.get_test_space().get_mutation_output_directory(), timeout);
			writer.write(command);
			writer.write("\n");
		}
		writer.close();
	}
	/**
	 * generate the execution script files for running tests on normal,
	 * instrumental and mutated program as compiled.
	 * @param tests
	 * @throws Exception
	 */
	protected void generate_exec_scripts(Iterable<TestInput> tests) throws Exception {
		long timeout = project.get_config().get_maximal_timeout_seconds();
		this.generate_normal_shell(tests, 0);
		this.generate_instrumental_shell(tests, 0);
		this.generate_mutation_shell(tests, timeout);
	}
	/**
	 * generate xxx.n.out from ifiles
	 * @throws Exception
	 */
	protected void compile_normal_program() throws Exception {
		MuTestProjectConfig config = this.project.get_config();
		MuTestProjectCodeSpace code_space = project.get_code_space();
		MuCommandUtil command_util = config.get_command_util();
		FileOperations.delete(this.get_normal_executional_file());
		if(!command_util.do_compile(config.get_compiler(),
				code_space.get_ifiles(),
				this.get_normal_executional_file(),
				code_space.get_hdirs(),
				code_space.get_lfiles(),
				config.get_compile_parameters())) {
			throw new RuntimeException("Unable to compile the normal program");
		}
	}
	/**
	 * compile and execute the original program, which produces the
	 * outputs in the n_outputs directory.
	 * @throws Exception
	 */
	protected void execute_normal_program() throws Exception {
		this.compile_normal_program();
		MuTestProjectConfig config = this.project.get_config();
		MuCommandUtil command_util = config.get_command_util();
		command_util.do_execute(this.get_normal_test_script_file(),
				this.get_efiles_directory());
	}
	/**
	 * compile xxx.s.out from sfiles
	 * @throws Exception
	 */
	protected void compile_instrumental_program() throws Exception {
		MuTestProjectConfig config = this.project.get_config();
		MuTestProjectCodeSpace code_space = project.get_code_space();
		MuCommandUtil command_util = config.get_command_util();
		FileOperations.delete(this.get_instrumental_executional_file());
		if(!command_util.do_compile(config.get_compiler(),
				code_space.get_sfiles(),
				this.get_instrumental_executional_file(),
				code_space.get_hdirs(), code_space.get_lfiles(),
				config.get_compile_parameters())) {
			throw new RuntimeException("Unable to compile the instrumental program");
		}
	}
	/**
	 * compile and execute the instrumental program, which generates the
	 * instrumental results on s_outputs directory.
	 * @throws Exception
	 */
	protected void execute_instrumental_program() throws Exception {
		this.compile_instrumental_program();
		MuTestProjectConfig config = this.project.get_config();
		MuCommandUtil command_util = config.get_command_util();
		command_util.do_execute(this.get_instrumental_test_script_file(),
				this.get_efiles_directory());
	}
	/**
	 * @param mutant
	 * @return compile the mutated program or return false if mutation is error
	 * @throws Exception
	 */
	protected boolean compile_mutation_program(Mutant mutant) throws Exception {
		/* declarations */
		MuTestProjectConfig config = this.project.get_config();
		MuTestProjectCodeSpace code_space = project.get_code_space();
		MuCommandUtil command_util = config.get_command_util();

		/* generate the mfiles */ code_space.set_mfiles(mutant);

		/* compile the mutation program */
		FileOperations.delete(this.get_mutation_executional_file());
		return command_util.do_compile(config.get_compiler(),
				code_space.get_mfiles(),
				this.get_mutation_executional_file(),
				code_space.get_hdirs(),
				code_space.get_lfiles(),
				config.get_compile_parameters());
	}
	/**
	 * @param mutant
	 * @return the test result executed w.r.t. the mutant and test inputs established before
	 * @throws Exception
	 */
	protected MuTestProjectTestResult execute_mutation_program(Mutant mutant) throws Exception {
		this.compile_mutation_program(mutant);
		MuTestProjectConfig config = this.project.get_config();
		MuCommandUtil command_util = config.get_command_util();
		command_util.do_execute(this.get_mutation_test_script_file(),
				this.get_efiles_directory());
		return this.project.get_test_space().update_test_result(mutant);
	}

}
