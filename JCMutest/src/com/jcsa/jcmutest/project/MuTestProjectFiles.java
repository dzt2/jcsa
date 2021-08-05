package com.jcsa.jcmutest.project;

import java.io.File;

import com.jcsa.jcmutest.project.util.FileOperations;

/**
 * <code>
 * 	[project]														<br>
 * 	|--	[code]			// code files directory						<br>
 * 	|--	|--	[cfiles]	// source files before pre-processed		<br>
 * 	|--	|--	[ifiles]	// source files after pre-processing		<br>
 * 	|--	|--	[sfiles]	// source files with instrumentation		<br>
 * 	|--	|--	[mfiles]	// source files seeded with mutation		<br>
 * 	|--	|--	[hfiles]	// header files to compile programs			<br>
 * 	|--	|--	[lfiles]	// library files for linking program		<br>
 * 	|--	|--	[mutants]	// the data file preserving mutations		<br>
 * 	|--	[test]			// test files directory						<br>
 * 	|--	|--	test.suite	// file that preserve test suite data		<br>
 * 	|--	|--	[inputs]	// the data files used in testing			<br>
 * 	|--	|--	[n_output]	// normal output files in testing			<br>
 * 	|--	|--	[s_output]	// instrumental output files in testing		<br>
 * 	|--	|--	[m_output]	// mutation output files in testing			<br>
 * 	|--	|--	[result]	// analysis result files after testing		<br>
 * 	|--	|--	instrument.txt | instrument.out | instrument.err		<br>
 * 	|--	|-- [efiles]		// executional and script files			<br>
 * 	|--	[config]		// configuration data files in project		<br>
 *
 * </code>
 *
 * @author yukimula
 *
 */
public class MuTestProjectFiles {

	/* file-names */
	private static final String code_name = "code";
	private static final String cfiles_name = "cfiles";
	private static final String ifiles_name = "ifiles";
	private static final String sfiles_name = "sfiles";
	private static final String mfiles_name = "mfiles";
	private static final String hfiles_name = "hfiles";
	private static final String lfiles_name = "lfiles";
	private static final String test_name = "test";
	private static final String test_suite_name = "test.suite";
	private static final String mutants_name = "mutants";
	private static final String inputs_name = "inputs";
	private static final String n_output_name = "n_output";
	private static final String s_output_name = "s_output";
	private static final String m_output_name = "m_output";
	private static final String result_name = "result";
	private static final String instrument_txt_name = "instrument.txt";
	private static final String instrument_out_name = "instrument.out";
	private static final String instrument_err_name = "instrument.err";
	private static final String config_name = "config";
	private static final String efiles_name = "efiles";

	/* attributes */
	/** the project in which the files are produced **/
	private MuTestProject project;
	/** the root directory of mutation test project **/
	private File root;
	/** code/cfiles for source files before preprocessed **/
	private File cfiles_directory;
	/** code/ifiles for source files after preprocessing **/
	private File ifiles_directory;
	/** code/sfiles for source files with instrumentation **/
	private File sfiles_directory;
	/** code/mfiles for source files seeded with mutation **/
	private File mfiles_directory;
	/** code/hfiles for header files used for compilation **/
	private File hfiles_directory;
	/** code/lfiles for library file used for compilation **/
	private File lfiles_directory;
	/** test/test.suite file to preserve test inputs used **/
	private File test_suite_file;
	/** test/mutants for mutation data files being seeded **/
	private File mutants_directory;
	/** test/inputs input data used by input in execution **/
	private File inputs_directory;
	/** test/n_output normal test outputs data generated **/
	private File n_output_directory;
	/** test/s_output instrumental test outputs files **/
	private File s_output_directory;
	/** test/m_output the mutation test outputs files **/
	private File m_output_directory;
	/** test/result the result files for mutation analysis **/
	private File result_directory;
	/** test/instrument.txt to preserve the instrumental result **/
	private File instrument_txt_file;
	/** test/instrument.out to preserve output of instrumental code **/
	private File instrument_out_file;
	/** test/instrument.err to preserve errors of instrumental code **/
	private File instrument_err_file;
	/** config/ directory to preserve the configuration data files **/
	private File config_directory;
	/** efiles/ directory of the executional and shell script file **/
	private File efiles_directory;

	/* constructor */
	private File try_to_mkdir(File dir) throws Exception {
		if(FileOperations.mkdir(dir)) return dir;
		else throw new IllegalArgumentException("Invalid: " + dir);
	}
	private File try_to_create(File file) throws Exception {
		if(!file.exists()) {
			FileOperations.write(file, "");
		}
		return file;
	}
	/**
	 * construct the file structure of mutation test project
	 * @param project
	 * @param root
	 * @throws Exception
	 */
	protected MuTestProjectFiles(MuTestProject project, File root) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			this.project = project;
			this.root = this.try_to_mkdir(root);
			this.config_directory = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + config_name));

			/* code */
			File code_dir = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + code_name));
			this.cfiles_directory = this.try_to_mkdir(new File(code_dir.getAbsolutePath() + "/" + cfiles_name));
			this.ifiles_directory = this.try_to_mkdir(new File(code_dir.getAbsolutePath() + "/" + ifiles_name));
			this.sfiles_directory = this.try_to_mkdir(new File(code_dir.getAbsolutePath() + "/" + sfiles_name));
			this.mfiles_directory = this.try_to_mkdir(new File(code_dir.getAbsolutePath() + "/" + mfiles_name));
			this.hfiles_directory = this.try_to_mkdir(new File(code_dir.getAbsolutePath() + "/" + hfiles_name));
			this.lfiles_directory = this.try_to_mkdir(new File(code_dir.getAbsolutePath() + "/" + lfiles_name));
			this.mutants_directory = this.try_to_mkdir(new File(code_dir.getAbsolutePath() + "/" + mutants_name));

			/* test */
			File test_dir = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + test_name));
			this.test_suite_file = this.try_to_create(new File(test_dir.getAbsolutePath() + "/" + test_suite_name));
			this.inputs_directory = this.try_to_mkdir(new File(test_dir.getAbsolutePath() + "/" + inputs_name));
			this.n_output_directory = this.try_to_mkdir(new File(test_dir.getAbsolutePath() + "/" + n_output_name));
			this.s_output_directory = this.try_to_mkdir(new File(test_dir.getAbsolutePath() + "/" + s_output_name));
			this.m_output_directory = this.try_to_mkdir(new File(test_dir.getAbsolutePath() + "/" + m_output_name));
			this.result_directory = this.try_to_mkdir(new File(test_dir.getAbsolutePath() + "/" + result_name));
			this.instrument_txt_file = this.try_to_create(new File(test_dir.getAbsolutePath() + "/" + instrument_txt_name));
			this.instrument_err_file = this.try_to_create(new File(test_dir.getAbsolutePath() + "/" + instrument_err_name));
			this.instrument_out_file = this.try_to_create(new File(test_dir.getAbsolutePath() + "/" + instrument_out_name));
			this.efiles_directory = this.try_to_mkdir(new File(test_dir.getAbsolutePath() + "/" + efiles_name));
		}
	}

	/* getters */
	/**
	 * @return the root directory of mutation test project
	 */
	public File get_root() { return this.root; }
	/**
	 * @return code/cfiles for source files before preprocessed
	 */
	public File get_cfiles_directory() { return this.cfiles_directory; }
	/**
	 * @return code/ifiles for source files after preprocessing
	 */
	public File get_ifiles_directory() { return this.ifiles_directory; }
	/**
	 * @return code/sfiles for source files with instrumentation
	 */
	public File get_sfiles_directory() { return this.sfiles_directory; }
	/**
	 * @return code/mfiles for source files seeded with mutation
	 */
	public File get_mfiles_directory() { return this.mfiles_directory; }
	/**
	 * @return code/hfiles for header files used for compilation
	 */
	public File get_hfiles_directory() { return this.hfiles_directory; }
	/**
	 * @return code/lfiles for library file used for compilation
	 */
	public File get_lfiles_directory() { return this.lfiles_directory; }
	/**
	 * @return test/test.suite file to preserve test inputs used
	 */
	public File get_test_suite_file() { return this.test_suite_file; }
	/**
	 * @return test/instrument.txt to preserve the instrumental result
	 */
	public File get_instrument_txt_file() { return this.instrument_txt_file; }
	/**
	 * @return test/instrument.out to preserve output of instrumental code
	 */
	public File get_instrument_out_file() { return this.instrument_out_file; }
	/**
	 * @return test/instrument.err to preserve errors of instrumental code
	 */
	public File get_instrument_err_file() { return this.instrument_err_file; }
	/**
	 * @return test/mutants for mutation data files being seeded
	 */
	public File get_mutants_directory() { return this.mutants_directory; }
	/**
	 * @return test/inputs input data used by input in execution
	 */
	public File get_inputs_directory() { return this.inputs_directory; }
	/**
	 * @return test/n_output normal test outputs data generated
	 */
	public File get_n_output_directory() { return this.n_output_directory; }
	/**
	 * @return test/instrument.out to preserve output of instrumental code
	 */
	public File get_s_output_directory() { return this.s_output_directory; }
	/**
	 * @return test/m_output the mutation test outputs files
	 */
	public File get_m_output_directory() { return this.m_output_directory; }
	/**
	 * @return test/result the result files for mutation analysis
	 */
	public File get_result_directory() { return this.result_directory; }
	/**
	 * @return config/ directory to preserve the configuration data files
	 */
	public File get_config_directory() { return this.config_directory; }
	/**
	 * @return efiles/ directory of the executional and shell script file
	 */
	public File get_efiles_directory() { return this.efiles_directory; }
	/**
	 * @return the project in which the files are produced
	 */
	public MuTestProject get_project() { return this.project; }

}
