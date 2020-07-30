package com.jcsa.jcparse.test.file;

import java.io.File;

import com.jcsa.jcparse.flwa.dynamics.AstPath;
import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.test.exe.CommandUtil;
import com.jcsa.jcparse.test.exe.TestInput;

/**
 * The execution result records the execution path, standard output and error
 * information that is generated during tested against an input.
 * 
 * @author yukimula
 *
 */
public class TestExecutionResult {
	
	private JCTestProject project;
	private TestInput input;
	private AstCirFile program;
	
	/**
	 * @param project the test-project being executed
	 * @param program the program being parsed for analysis
	 * @param input the test input being used
	 * @throws Exception
	 */
	protected TestExecutionResult(JCTestProject project, 
			AstCirFile program, TestInput input) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else if(program == null)
			throw new IllegalArgumentException("Invalid program: null");
		else if(input == null)
			throw new IllegalArgumentException("Invalid input: null");
		else {
			this.project = project;
			this.program = program;
			this.input = input;
		}
	}
	
	/* getters */
	/**
	 * @return the program being parsed
	 */
	public AstCirFile get_program() { return this.program; }
	/**
	 * @return input that is executed against the program
	 */
	public TestInput get_test_input() { return this.input; }
	/**
	 * @return the standard output file w.r.t. the test input
	 */
	public File get_stdout_file() { return this.input.get_stdout_file(this.project.get_project_files().get_normal_output_directory()); }
	/**
	 * @return the standard errors file w.r.t. the test input
	 */
	public File get_stderr_file() { return this.input.get_stderr_file(this.project.get_project_files().get_normal_output_directory()); }
	/**
	 * @return the instrumental result file w.r.t. the test input
	 */
	public File get_instrument_file() { return this.input.get_instrument_file(this.project.get_project_files().get_instrument_output_directory()); }
	/**
	 * @return the content of the standard output information w.r.t. the test input
	 * @throws Exception the input was NOT executed against the program yet.
	 */
	public String load_stdout() throws Exception { return CommandUtil.read_text(get_stdout_file()); }
	/**
	 * @return the content of the standard errors information w.r.t. the test input
	 * @throws Exception the input was NOT executed against the program yet.
	 */
	public String load_stderr() throws Exception { return CommandUtil.read_text(get_stderr_file()); }
	/**
	 * @return the execution path of the program when executed against the test input or null if the result is not recorded.
	 * @throws Exception the input was NOT executed against the program yet.
	 */
	public AstPath load_ast_path() throws Exception { return AstPath.path(this.program.get_ast_tree(), this.get_instrument_file()); }
	
}
