package com.jcsa.jcparse.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.test.inp.TestInput;

/**
 * The file structure of the C-test-project is defined as following:<br>
 * 	[root]
 * 	|--	[cfiles]		{xxx.c of source code files}
 * 	|--	[ifiles]		{xxx.c after pre-processing}
 * 	|--	[sfiles]		{xxx.c after instrumentation}
 * 	|--	[hfiles]		{xxx.h for pre-processing}
 * 	|--	[lfiles]		{xxx.lib for linking and compilation}
 * 	|--	[efiles]		{xxx.exe, xxx.ins.exe xxx.sh, xxx.ins.sh}
 * 	|--	[tests]			{xxx.inp to provide test input files}
 * 	|--	[inputs]		{input data used during testing}
 * 	|--	[n_output]		{xxx.out, xxx.err for normal testing}
 * 	|--	[s_output]		{xxx.out, xxx.err, xxx.ins, NONE.ins for testing instrumental code}
 * 	|--	[config]		{linux.h, jcinst.h, cruntime.txt}
 * 	|--	xxx.tproj		{ClangStandard, compile_parameters}
 * @author yukimula
 *
 */
public class JCTestProjectFiles {
	
	private static final String cfiles_name = "cfiles";
	private static final String ifiles_name = "ifiles";
	private static final String sfiles_name = "sfiles";
	private static final String hfiles_name = "hfiles";
	private static final String lfiles_name = "lfiles";
	private static final String efiles_name = "efiles";
	private static final String tests_name = "tests";
	private static final String input_name = "inputs";
	private static final String normal_output_name = "n_output";
	private static final String instrument_output_name = "s_output";
	private static final String instrument_output_buff = "NONE.ins";
	private static final String config_name = "config";
	
	private File root_dir;
	private File cfiles_dir;
	private File ifiles_dir;
	private File sfiles_dir;
	private File hfiles_dir;
	private File lfiles_dir;
	private File efiles_dir;
	private File tests_file;
	private File input_dir;
	private File normal_output_dir;
	private File instrument_output_dir;
	private File instrument_buffer_file;
	private File config_dir;
	private File project_file;
	
	private File try_to_mkdir(File dir) throws Exception {
		if(!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}
	private File try_to_create(File file) throws Exception {
		if(!file.exists()) {
			file.createNewFile();
		}
		return file;
	}
	protected JCTestProjectFiles(File root) throws Exception {
		if(root == null)
			throw new IllegalArgumentException("Invalid root: null");
		else {
			this.root_dir = this.try_to_mkdir(root);
			this.cfiles_dir = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + cfiles_name));
			this.ifiles_dir = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + ifiles_name));
			this.sfiles_dir = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + sfiles_name));
			this.hfiles_dir = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + hfiles_name));
			this.lfiles_dir = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + lfiles_name));
			this.efiles_dir = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + efiles_name));
			this.tests_file = this.try_to_create(new File(root.getAbsolutePath() + "/" + tests_name));
			this.input_dir = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + input_name));
			this.normal_output_dir = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + normal_output_name));
			this.instrument_output_dir = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + instrument_output_name));
			this.instrument_buffer_file = this.try_to_create(
						new File(this.instrument_output_dir.getAbsolutePath() + "/" + instrument_output_buff));
			this.config_dir = this.try_to_mkdir(new File(root.getAbsolutePath() + "/" + config_name));
			this.project_file = new File(root.getAbsolutePath() + "/" + root.getName() + ".tproj");
		}
	}
	
	/* getters */
	public File get_root_dir() { return this.root_dir; }
	public File get_cfiles_dir() { return this.cfiles_dir; }
	public File get_ifiles_dir() { return this.ifiles_dir; }
	public File get_sfiles_dir() { return this.sfiles_dir; }
	public File get_hfiles_dir() { return this.hfiles_dir; }
	public File get_lfiles_dir() { return this.lfiles_dir; }
	public File get_efiles_dir() { return this.efiles_dir; }
	public File get_tests_file() { return this.tests_file; }
	public File get_input_dir() { return this.input_dir; }
	public File get_normal_output_dir() { return this.normal_output_dir; }
	public File get_instrument_output_dir() { return this.instrument_output_dir; }
	public File get_instrument_buffer_file() {
		return this.instrument_buffer_file;
	}
	public File get_config_dir() { return this.config_dir; }
	public File get_project_file() { return this.project_file; }
	
	/* implicator */
	private List<File> files_of(File dir) {
		File[] files = dir.listFiles();
		List<File> file_list = new ArrayList<File>();
		if(files != null) {
			for(File file : files) {
				file_list.add(file);
			}
		}
		return file_list;
	}
	public Iterable<File> get_cfiles() { return this.files_of(this.cfiles_dir); }
	public Iterable<File> get_ifiles() { return this.files_of(this.ifiles_dir); }
	public Iterable<File> get_sfiles() { return this.files_of(this.sfiles_dir); }
	public Iterable<File> get_hfiles() { return this.files_of(this.hfiles_dir); }
	public Iterable<File> get_lfiles() { return this.files_of(this.lfiles_dir); }
	public File[] get_normal_output_files(TestInput test) {
		File stdout = test.get_stdout_file(this.normal_output_dir);
		File stderr = test.get_stderr_file(this.normal_output_dir);
		return new File[] { stdout, stderr };
	}
	public File[] get_instrument_output_files(TestInput test) {
		File stdout = test.get_stdout_file(this.instrument_output_dir);
		File stderr = test.get_stderr_file(this.instrument_output_dir);
		File inst_file = test.get_instrument_file(this.instrument_output_dir);
		return new File[] { stdout, stderr, inst_file };
	}
	
	
}
