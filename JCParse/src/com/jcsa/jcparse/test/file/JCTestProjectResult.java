package com.jcsa.jcparse.test.file;

import java.io.File;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.test.CommandUtil;
import com.jcsa.jcparse.test.inst.InstrumentalLine;
import com.jcsa.jcparse.test.inst.InstrumentalLines;
import com.jcsa.jcparse.test.inst.InstrumentalNode;
import com.jcsa.jcparse.test.inst.InstrumentalNodes;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It provides the interfaces to fetch the results generated during the
 * testing process.
 * 
 * @author yukimula
 *
 */
public class JCTestProjectResult {
	
	/* data construction */
	/** the project that the result serves for **/
	private JCTestProject project;
	/**
	 * @param project
	 */
	protected JCTestProjectResult(JCTestProject project) {
		this.project = project;
	}
	/** 
	 * @return the project that the result serves for
	 */
	public JCTestProject get_project() { return this.project; }
	
	/* data loaders */
	/**
	 * @param input the test input of which result is fetched
	 * @return the standard output information generated when program is
	 * 			executed against the test input as specified or null if
	 * 			the program has not been executed against the input and
	 * 			no information is generated from its standard output.
	 * @throws Exception
	 */
	public String load_stdout(TestInput input) throws Exception {
		File stdout_file = input.get_stdout_file(this.project.
				get_project_files().get_normal_output_directory());
		if(stdout_file.exists()) {
			return CommandUtil.read_text(stdout_file);
		}
		else {
			return null;
		}
	}
	/**
	 * @param input the test input of which result is fetched
	 * @return the standard error information generated when program is
	 * 			executed against the test input as specified or null if
	 * 			the program has not been executed against the input and
	 * 			no information is generated from its standard errors.
	 * @throws Exception
	 */
	public String load_stderr(TestInput input) throws Exception {
		File stderr_file = input.get_stderr_file(this.project.
				get_project_files().get_normal_output_directory());
		if(stderr_file.exists()) {
			return CommandUtil.read_text(stderr_file);
		}
		else {
			return null;
		}
	}
	
	/* instrumental loader */
	/**
	 * @param template
	 * @param ast_tree
	 * @param input
	 * @param complete whether to generate the complete sequence of lines
	 * @return the original instrumental lines read from data file
	 * @throws Exception
	 */
	public List<InstrumentalLine> load_instrumental_lines(CRunTemplate template, 
			AstTree ast_tree, TestInput input, boolean complete) throws Exception {
		File instrumental_file = input.get_instrument_file(this.project.
				get_project_files().get_instrument_output_directory());
		if(instrumental_file.exists()) {
			if(complete) {
				return InstrumentalLines.complete_lines(
						template, ast_tree, instrumental_file);
			}
			else {
				return InstrumentalLines.simple_lines(
						template, ast_tree, instrumental_file);
			}
		}
		else {
			return null;
		}
	}
	/**
	 * @param template
	 * @param ast_tree
	 * @param cir_tree
	 * @param input
	 * @return load the sequence of instrumental nodes in execution path
	 * @throws Exception
	 */
	public List<InstrumentalNode> load_instrumental_nodes(CRunTemplate template,
			AstTree ast_tree, CirTree cir_tree, TestInput input) throws Exception {
		File instrumental_file = input.get_instrument_file(this.project.
				get_project_files().get_instrument_output_directory());
		if(instrumental_file.exists()) {
			return InstrumentalNodes.get_nodes(template, 
					ast_tree, cir_tree, instrumental_file);
		}
		else {
			return null;
		}
	}
	/**
	 * @param template
	 * @param ast_tree
	 * @param cir_tree
	 * @param input
	 * @return the state transition path parsed from instrumental path
	 * @throws Exception
	 */
	public CStatePath load_instrumental_path(CRunTemplate template,
			AstTree ast_tree, CirTree cir_tree, TestInput input) throws Exception {
		File instrumental_file = input.get_instrument_file(this.project.
				get_project_files().get_instrument_output_directory());
		if(instrumental_file.exists()) {
			return CStatePath.read_path(template, ast_tree, cir_tree, instrumental_file);
		}
		else {
			return null;
		}
	}
	 
}
