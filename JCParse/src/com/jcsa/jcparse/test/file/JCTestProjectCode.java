package com.jcsa.jcparse.test.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.test.CommandUtil;
import com.jcsa.jcparse.test.cmd.CCompiler;

/**
 * 	It provides the management on source code under test in the project.
 * 	<br>
 * 	<code>
 * 	---	[cfiles]			{source code files before pre-processing}
 * 	--- [ifiles]			{source code files after pre-processing}
 * 	--- [sfiles]			{source code files with instrumental methods}
 * 	---	[hfiles]			{header files being used for pre-processing}
 * 	---	[lfiles]			{library files being linked with compilation}
 * 	---	[config/jcinst.h]	{header file to implement the instrumentation}
 * 	---	[efiles/xxx.exe]	{the executional file being compiled after}
 * 	</code>
 * 	@author yukimula
 *	
 */
public class JCTestProjectCode {
	
	/* constructor */
	/** the test project that performs on the code **/
	private JCTestProject project;
	/** the buffer to preserve the parsed data for c source code **/
	private Map<String, AstCirFile> program_buff;
	/**
	 * create the code space in the test project.
	 * @param project
	 */
	protected JCTestProjectCode(JCTestProject project) {
		this.project = project;
		this.program_buff = new HashMap<String, AstCirFile>();
	}
	
	/* getters */
	/**
	 * @param files
	 * @return the list of files in the array (non-null)
	 */
	private Iterable<File> list_of_files(File[] files) {
		List<File> file_list = new ArrayList<File>();
		if(files != null) {
			for(File file : files) {
				if(file != null && file.exists())
					file_list.add(file);
			}
		}
		return file_list;
	}
	/**
	 * @return the set of source code files {xxx.c} before pre-processed
	 */
	public Iterable<File> get_source_code_files() {
		return this.list_of_files(this.project.
				get_project_files().get_c_file_directory().listFiles());
	}
	/**
	 * @return the set of source code files {xxx.i} after pre-processing
	 */
	public Iterable<File> get_intermediate_files() {
		return this.list_of_files(this.project.
				get_project_files().get_i_file_directory().listFiles());
	}
	/**
	 * @return the set of source code files {xxx.i} being instrumented
	 */
	public Iterable<File> get_instrumental_files() {
		return this.list_of_files(this.project.
				get_project_files().get_s_file_directory().listFiles());
	}
	/**
	 * @return the set of header files {xxx.h} being included in pre-processing
	 * 		   as well as the compilation phasis.
	 */
	public Iterable<File> get_header_files() {
		return this.list_of_files(this.project.
				get_project_files().get_h_file_directory().listFiles());
	}
	/**
	 * @return the set of library files {xxx.lib} being linked in compilation
	 */
	public Iterable<File> get_library_files() {
		return this.list_of_files(this.project.
				get_project_files().get_l_file_directory().listFiles());
	}
	/**
	 * @return the set of diretories where the header files are used
	 */
	public Iterable<File> get_header_directories() {
		List<File> hdirs = new ArrayList<File>();
		hdirs.add(this.project.get_project_files().get_h_file_directory());
		hdirs.add(this.project.get_project_files().get_config_directory());
		return hdirs;
	}
	/**
	 * @return the executional file {xxx.exe} being compiled
	 */
	public File get_executional_file() {
		return new File(
				this.project.get_project_files().get_exe_directory().getAbsolutePath() + 
				"/" + this.project.get_name() + ".exe");
	}
	/**
	 * @return the executional file {xxx.ins.exe} being compiled for instrumental code files
	 */
	public File get_instrument_executional_file() {
		return new File(
				this.project.get_project_files().get_exe_directory().getAbsolutePath() + 
				"/" + this.project.get_name() + ".ins.exe");
	}
	/**
	 * @return [config/linux.h]
	 */
	public Iterable<File> get_macros_files() {
		List<File> mfiles = new ArrayList<File>();
		mfiles.add(this.project.get_config().get_c_pre_process_mac_file());
		return mfiles;
	}
	/**
	 * @return the number of .c files in cfiles/ and ifiles/ and sfiles/
	 */
	public int number_of_c_files() { 
		int counter = 0;
		for(@SuppressWarnings("unused") File file : this.get_source_code_files()) {
			counter++;
		}
		return counter;
	}
	/**
	 * @param k [0, this.number_of_c_files())
	 * @return the program being parsed for the kth source file in ifiles/
	 * @throws Exception
	 */
	public AstCirFile get_program(int k) throws Exception {
		File[] cfiles = this.project.get_project_files().get_i_file_directory().listFiles();
		if(cfiles == null) {
			throw new IllegalArgumentException("No .c file is generated in ifiles/");
		}
		else {
			File cfile = cfiles[k];
			if(!this.program_buff.containsKey(cfile.getName())) {
				AstCirFile program = AstCirFile.parse(cfile, 
						this.project.get_config().get_c_template_file(), 
						this.project.get_config().get_lang_standard());
				this.program_buff.put(cfile.getName(), program);
			}
			return this.program_buff.get(cfile.getName());
		}
	}
	
	/* actions */
	/**
	 * delete all the code files in cfiles, ifiles, sfiles, 
	 * hfiles, lfiles and efiles/xxx.exe.
	 * @throws Exception
	 */
	private void clear_code_files() throws Exception {
		JCTestProjectFiles pfiles = this.project.get_project_files();
		CommandUtil.delete_files_in(pfiles.get_c_file_directory());
		CommandUtil.delete_files_in(pfiles.get_i_file_directory());
		CommandUtil.delete_files_in(pfiles.get_s_file_directory());
		CommandUtil.delete_files_in(pfiles.get_h_file_directory());
		CommandUtil.delete_files_in(pfiles.get_l_file_directory());
		CommandUtil.delete_file(this.get_executional_file());
		CommandUtil.delete_file(this.get_instrument_executional_file());
	}
	/**
	 * copy the source code files into the cfiles/ directory
	 * @param cfiles set of xxx.c files being used as source code files in testing
	 * @throws Exception
	 */
	private void set_source_code_files(Iterable<File> cfiles) throws Exception {
		for(File cfile : cfiles) {
			File tfile = new File(this.project.get_project_files().
					get_c_file_directory().getAbsolutePath() + "/" + cfile.getName());
			CommandUtil.copy_file(cfile, tfile);
		}
	}
	/**
	 * copy the header code files into the hfiles/ directory
	 * @param hfiles set of xxx.h files being used for pre-processing the program.
	 * @throws Exception
	 */
	private void set_header_code_files(Iterable<File> hfiles) throws Exception {
		for(File hfile : hfiles) {
			File tfile = new File(this.project.get_project_files().
					get_h_file_directory().getAbsolutePath() + "/" + hfile.getName());
			CommandUtil.copy_file(hfile, tfile);
		}
	}
	/**
	 * copy the library files into the lfiles/ directory
	 * @param lfiles
	 * @throws Exception
	 */
	private void set_library_files(Iterable<File> lfiles) throws Exception {
		for(File lfile : lfiles) {
			File tfile = new File(this.project.get_project_files().
					get_l_file_directory().getAbsolutePath() + "/" + lfile.getName());
			CommandUtil.copy_file(lfile, tfile);
		}
	}
	/**
	 * generate the intermediate code files (xxx.c) using pre-processing phasis.
	 * @throws Exception
	 */
	private void gen_intermediate_files() throws Exception {
		CommandUtil util = this.project.get_config().get_command_util();
		CCompiler compiler = this.project.get_config().get_compiler();
		for(File cfile : this.get_source_code_files()) {
			File ifile = new File(this.project.get_project_files().get_i_file_directory().getAbsolutePath() + "/" + cfile.getName());
			if(!util.do_preprocess(compiler, cfile, ifile, this.get_header_directories(), this.get_macros_files())) {
				throw new RuntimeException("Unable to pre-processing the compilation: " + ifile.getAbsolutePath());
			}
		}
	}
	/**
	 * generate the instrumental code files (xxx.c) using instrumental interface
	 * @throws Exception
	 */
	private void gen_instrumental_files() throws Exception {
		CommandUtil util = this.project.get_config().get_command_util();
		File rfile = this.project.get_project_files().get_instrument_result_file();
		File c_template_file = this.project.get_config().get_c_template_file();
		ClangStandard standard = this.project.get_config().get_lang_standard();
		
		for(File ifile : this.get_intermediate_files()) {
			File sfile = new File(this.project.get_project_files().
					get_s_file_directory().getAbsolutePath() + "/" + ifile.getName());
			if(!util.do_instrument(ifile, sfile, rfile, c_template_file, standard)) {
				throw new RuntimeException("Failed to instrument: " + sfile.getAbsolutePath());
			}
		}
	}
	/**
	 * generate the executional files {xxx.exe and xxx.ins.exe}
	 * @throws Exception
	 */
	private void gen_executional_files() throws Exception {
		CommandUtil util = this.project.get_config().get_command_util();
		CCompiler compiler = this.project.get_config().get_compiler();
		Iterable<String> parameters = this.project.get_config().get_compile_parameters();
		
		/* compile the intermediate code files to generate non-instrumental program. */
		if(!util.do_compile(compiler, 
				this.get_intermediate_files(), 
				this.get_executional_file(), 
				this.get_header_directories(), 
				this.get_library_files(), 
				parameters)) {
			throw new RuntimeException("Unable to compile " + this.get_executional_file().getAbsolutePath());
		}
		
		/* compile the instrumental code files to generate instrumental program. */
		if(!util.do_compile(compiler, 
				this.get_instrumental_files(), 
				this.get_instrument_executional_file(), 
				this.get_header_directories(), 
				this.get_library_files(), 
				parameters)) {
			throw new RuntimeException("Unable to compile " + this.get_instrument_executional_file().getAbsolutePath());
		}
	}
	/**
	 * input the code, header and library files
	 * @param cfiles the xxx.c code files
	 * @param hfiles the xxx.h header files
	 * @param lfiles the xxx.lib library files
	 * @throws Exception
	 */
	protected void set(Iterable<File> cfiles, Iterable<File> hfiles, Iterable<File> lfiles) throws Exception {
		if(cfiles == null)
			throw new IllegalArgumentException("Invalid cfiles: null");
		else if(hfiles == null)
			throw new IllegalArgumentException("Invalid hfiles: null");
		else if(lfiles == null)
			throw new IllegalArgumentException("Invalid lfiles: null");
		else {
			this.clear_code_files();
			this.set_source_code_files(cfiles);
			this.set_header_code_files(hfiles);
			this.set_library_files(lfiles);
			this.gen_intermediate_files();
			this.gen_instrumental_files();
			this.gen_executional_files();
		}
	}
	/**
	 * update the intermediate code, instrumental code and executional files.
	 * @throws Exception
	 */
	protected void update() throws Exception {
		this.gen_intermediate_files();
		this.gen_instrumental_files();
		this.gen_executional_files();
	}
	
}
