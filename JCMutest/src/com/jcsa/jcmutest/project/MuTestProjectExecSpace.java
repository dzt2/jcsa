package com.jcsa.jcmutest.project;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.file.TestInputs;

/**
 * The execution space where the program and test script files are compiled,
 * generated and executed in an automatic manner.
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
	
	/* setters */
	/**
	 * the original & instrumental program is successfully compiled
	 * @throws Exception
	 */
	protected void compile_program(boolean instrumental) throws Exception {
		/* declarations */
		MuTestProjectConfig config = this.project.get_config();
		MuTestProjectCodeSpace code_space = project.get_code_space();
		MuCommandUtil command_util = config.get_command_util();
		
		if(instrumental) {
			/* compile the instrumental program */
			FileOperations.delete(this.get_instrumental_executional_file());
			if(!command_util.do_compile(config.get_compiler(), 
					code_space.get_sfiles(), 
					this.get_instrumental_executional_file(), 
					code_space.get_hdirs(), code_space.get_lfiles(), 
					config.get_compile_parameters())) {
				throw new RuntimeException("Unable to compile the instrumental program");
			}
		}
		else {
			/* compile the original program */
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
	}
	/**
	 * the mutation program compiled from parsing mfiles directory
	 * @param mutant
	 * @return whether the mutant can be compiled successfully
	 * @throws Exception
	 */
	protected boolean compile_program(Mutant mutant) throws Exception {
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
	 * #!bash
	 * cd efiles
	 * rm normal_outputs/*
	 * efile inputs (timeout) > output 2> errors
	 * @throws Exception
	 */
	private void generate_normal_shell() throws Exception {
		FileWriter writer = new FileWriter(this.get_normal_test_script_file());
		TestInputs test_space = this.project.get_test_space().get_test_space();
		
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template, project.get_files().get_efiles_directory().getAbsolutePath()));
		writer.write(String.format(remove_files_template, this.project.
				get_test_space().get_normal_output_directory().getAbsolutePath()));
		
		for(TestInput input : test_space.get_inputs()) {
			String command = input.command(this.get_normal_executional_file(), 
					this.project.get_test_space().get_normal_output_directory(), 0);
			writer.write(command);
			writer.write("\n");
		}
		
		writer.close();
	}
	/**
	 * #!bash
	 * cd efiles/
	 * {
	 * 	rm s_output/*
	 * 	command
	 * 	cp instrument_txt_file s_output_file 
	 * }
	 * @throws Exception
	 */
	private void generate_instrumental_shell() throws Exception {
		FileWriter writer = new FileWriter(this.get_instrumental_test_script_file());
		
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template, project.get_files().get_efiles_directory().getAbsolutePath()));
		writer.write(String.format(remove_files_template, 
						this.project.get_test_space().get_instrumental_output_directory().getAbsolutePath()));
		
		for(TestInput input : this.project.get_test_space().get_test_space().get_inputs()) {
			writer.write("\n");
			writer.write(input.command(this.get_instrumental_executional_file(), 
					this.project.get_test_space().get_instrumental_out_file(), 
					this.project.get_test_space().get_instrumental_err_file(), 0));
			writer.write("\n");
			writer.write(String.format(copy_file_template, 
					this.project.get_test_space().get_instrumental_txt_file(),
					input.get_instrument_file(this.project.get_test_space().
							get_instrumental_output_directory()).getAbsolutePath()));
		}
		
		writer.close();
	}
	/**
	 * #!bash
	 * cd efiles
	 * rm mutation_outputs/*
	 * efile inputs (timeout) > output 2> errors
	 * @throws Exception
	 */
	private void generate_mutation_shell() throws Exception {
		FileWriter writer = new FileWriter(this.get_mutation_test_script_file());
		long timeout = this.project.get_config().get_maximal_timeout_seconds();
		
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
	 * compile original and instrumental programs
	 * generate test shell scripts for all versions
	 * and call it before execute_xxx method invoked
	 * @throws Exception
	 */
	protected void initialize_testing() throws Exception {
		this.compile_program(true);
		this.compile_program(false);
		this.generate_normal_shell();
		this.generate_instrumental_shell();
		this.generate_mutation_shell();
	}
	/**
	 * execute tests against the original program without instrumentation
	 * @param instrumental whether to execute the instrumental version of program
	 * @throws Exception
	 */
	protected void execute_original_program(boolean instrumental) throws Exception {
		MuTestProjectConfig config = this.project.get_config();
		MuCommandUtil command_util = config.get_command_util();
		
		if(instrumental) {
			/* execute instrumental testing */
			command_util.do_execute(this.get_instrumental_test_script_file(), 
					this.get_efiles_directory());
		}
		else {
			/* execute normal testing */
			command_util.do_execute(this.get_normal_test_script_file(), 
					this.get_efiles_directory());
		}
	}
	/**
	 * (1) compile the mutation program
	 * (2) perform execution on mutation shell script
	 * (3) save the test result in results directory
	 * @param mutant
	 * @throws Exception
	 */
	protected MuTestProjectTestResult execute_mutation_program(Mutant mutant) throws Exception {
		MuTestProjectConfig config = this.project.get_config();
		MuCommandUtil command_util = config.get_command_util();
		this.compile_program(mutant);
		command_util.do_execute(this.get_mutation_test_script_file(), 
				this.get_efiles_directory());
		return this.project.get_test_space().save_test_result(mutant);
	}
	
}
