package com.jcsa.jcmutest.project;

import java.io.File;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcmutest.project.util.MuCommandUtil;

/**
 * 	[efiles]		<br>
 * 	|-- xxx.n.exe	<br>
 * 	|--	xxx.m.exe	<br>
 * 	|--	xxx.s.exe	<br>
 * 	|-- xxx.n.sh	<br>
 * 	|--	xxx.m.sh	<br>
 * 	|--	xxx.s.sh	<br>
 * @author yukimula
 *
 */
public class MuTestExecution {
	
	/* definition */
	private MuTestProject project;
	protected MuTestExecution(MuTestProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			this.project = project;
		}
	}
	
	/* getters */
	/**
	 * @return mutation test project in which the code part is defined
	 */
	public MuTestProject get_project() { return this.project; }
	/**
	 * @return efiles directory in which executional files are produced
	 */
	public File get_efiles_directory() { return project.get_files().get_efiles_directory(); }
	/**
	 * @return the executional file compiled from normal programs
	 */
	public File get_normal_executional_file() {
		File directory = this.get_efiles_directory();
		return new File(directory.getAbsolutePath() + "/" + project.get_name() + ".n.exe");
	}
	/**
	 * @return the executional file compiled from mutation programs
	 */
	public File get_mutation_executional_file() {
		File directory = this.get_efiles_directory();
		return new File(directory.getAbsolutePath() + "/" + project.get_name() + ".m.exe");
	}
	/**
	 * @return the executional file compiled from mutation programs
	 */
	public File get_instrumental_executional_file() {
		File directory = this.get_efiles_directory();
		return new File(directory.getAbsolutePath() + "/" + project.get_name() + ".s.exe");
	}
	/**
	 * @return shell script file for normal testing
	 */
	public File get_normal_script_file() {
		File directory = this.get_efiles_directory();
		return new File(directory.getAbsolutePath() + "/" + project.get_name() + ".n.sh");
	}
	/**
	 * @return shell script file for mutation testing
	 */
	public File get_mutation_script_file() {
		File directory = this.get_efiles_directory();
		return new File(directory.getAbsolutePath() + "/" + project.get_name() + ".m.sh");
	}
	/**
	 * @return shell script file for normal testing
	 */
	public File get_instrumental_script_file() {
		File directory = this.get_efiles_directory();
		return new File(directory.getAbsolutePath() + "/" + project.get_name() + ".s.sh");
	}
	/**
	 * @param mutant
	 * @return result/id to preserve the test result
	 */
	private File get_mutation_result_file(Mutant mutant) {
		File directory = project.get_files().get_result_directory();
		return new File(directory.getAbsolutePath() + "/" + mutant.get_id());
	}
	/**
	 * @param mutant
	 * @return mutation test result for the specified mutant
	 * @throws Exception
	 */
	public MuTestResult load_test_result(Mutant mutant) throws Exception {
		MuTestResult result = new MuTestResult(mutant, project.
				get_test_space().get_test_space().number_of_inputs());
		File result_file = this.get_mutation_result_file(mutant);
		result.load(result_file);
		return result;
	}
	
	/* update */
	/**
	 * compile from the normal and instrumental file
	 * @throws Exception
	 */
	protected void compile_program() throws Exception {
		FileOperations.delete(this.get_normal_executional_file());
		FileOperations.delete(this.get_instrumental_executional_file());
		
		MuTestProjectConfig config = project.get_config();
		MuTestCodeSpace code_space = project.get_code_space();
		MuCommandUtil command_util = config.get_command_util();
		
		if(!command_util.do_compile(config.get_compiler(), code_space.get_ifiles(), 
				this.get_normal_executional_file(), code_space.get_hdirs(), 
				code_space.get_lfiles(), config.get_compile_parameters())) {
			throw new RuntimeException("Failed to compile " + this.get_normal_executional_file().getAbsolutePath());
		}
		
		if(!command_util.do_compile(config.get_compiler(), code_space.get_sfiles(), 
				this.get_instrumental_executional_file(), code_space.get_hdirs(), 
				code_space.get_lfiles(), config.get_compile_parameters())) {
			throw new RuntimeException("Failed to compile " + this.get_instrumental_executional_file().getAbsolutePath());
		}
	}
	/**
	 * generate the mfiles and compile them into 
	 * @param mutant
	 * @throws Exception
	 */
	private void compile_mutant(Mutant mutant) throws Exception {
		FileOperations.delete(this.get_mutation_executional_file());
		MuTestProjectConfig config = project.get_config();
		MuTestCodeSpace code_space = project.get_code_space();
		MuCommandUtil command_util = config.get_command_util();
		
		if(!code_space.generate_mfiles(mutant)) {
			throw new RuntimeException("Undefined: " + mutant);
		}
		
		if(!command_util.do_compile(config.get_compiler(), 
				code_space.get_mfiles(), this.get_mutation_executional_file(), 
				code_space.get_hdirs(), code_space.get_lfiles(), 
				config.get_compile_parameters())) {
			throw new RuntimeException("Failed to compile " + this.get_mutation_executional_file().getAbsolutePath());
		}
	}
	/**
	 * generate all the test script files for running tests
	 * @throws Exception
	 */
	protected void generate_test_scripts() throws Exception {
		MuTestInputSpace test_space = project.get_test_space();
		MuTestProjectConfig config = project.get_config();
		test_space.set_normal_shell_script(
				this.get_normal_executional_file(), 
				this.get_normal_script_file(), 0);
		test_space.set_instrumental_shell_script(
				this.get_instrumental_executional_file(), 
				this.get_instrumental_script_file(), 0);
		test_space.set_mutation_shell_script(
				this.get_mutation_executional_file(), 
				this.get_mutation_script_file(), 
				config.get_maximal_timeout_seconds());
	}
	protected void execute_normal_testing() throws Exception {
		FileOperations.delete_in(project.get_files().get_n_output_directory());
		MuTestProjectConfig config = project.get_config();
		MuCommandUtil command_util = config.get_command_util();
		command_util.do_execute(this.get_normal_script_file(), this.get_efiles_directory());
	}
	protected void execute_instrumental_testing() throws Exception {
		FileOperations.delete_in(project.get_files().get_s_output_directory());
		MuTestProjectConfig config = project.get_config();
		MuCommandUtil command_util = config.get_command_util();
		command_util.do_execute(this.get_instrumental_script_file(), this.get_efiles_directory());
	}
	protected void execute_mutation_testing(Mutant mutant) throws Exception {
		MuTestProjectConfig config = project.get_config();
		MuCommandUtil command_util = config.get_command_util();
		
		/* compile the program from the mutation files in mfiles */
		this.compile_mutant(mutant);
		
		/* perform mutation shell script and generate in m_outputs */
		FileOperations.delete_in(project.get_files().get_m_output_directory());
		command_util.do_execute(this.get_normal_script_file(), this.get_efiles_directory());
		
		/* generate the test result */
		MuTestResult result = new MuTestResult(mutant, project.
				get_test_space().get_test_space().number_of_inputs());
		result.set(project.get_test_space().get_normal_output_directory(), 
				project.get_test_space().get_mutation_output_directory());
		result.save(this.get_mutation_result_file(mutant));
	}
	
}
