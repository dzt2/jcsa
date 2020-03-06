package com.jcsa.jcmuta.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmuta.mutant.code2mutation.MutationCodeType;

public class MutaSourceFiles {
	
	/* argument */
	/** the name of directory to preserve source code files **/
	private static final String source_directory_name = "source";
	/** the name of directory to preserve mutant data files **/
	private static final String mutant_directory_name = "mutant";
	
	/* attributes */
	/** the mutation test project **/
	private MutaProject project;
	/** the source file directory **/
	private File source_directory;
	/** the mutant file directory **/
	private File mutant_directory;
	/** the source code files in the directory **/
	private List<MutaSourceFile> source_files;
	
	/* constructor */
	/**
	 * create the space for source code files and mutation data files
	 * @param project
	 * @throws Exception
	 */
	protected MutaSourceFiles(MutaProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			/** set the project and the source files list **/
			this.project = project; 
			File directory = this.project.project_directory;
			this.source_files = new ArrayList<MutaSourceFile>();
			
			/** project/source/ **/
			this.source_directory = new File(directory.getAbsolutePath() 
					+ File.separator + source_directory_name);
			if(!this.source_directory.exists()) source_directory.mkdir();
			
			/** project/mutant/ **/
			this.mutant_directory = new File(directory.getAbsolutePath() 
					+ File.separator + mutant_directory_name);
			if(!this.mutant_directory.exists()) mutant_directory.mkdir();
			
			/** update the source files **/ this.update_source_files();
		}
	}
	
	/* getters */
	/**
	 * get the project where the source files are compiled, tested, mutated.
	 * @return
	 */
	public MutaProject get_project() { return this.project; }
	/**
	 * project/source/
	 * @return
	 */
	public File get_source_directory() { return this.source_directory; }
	/**
	 * project/mutant/
	 * @return
	 */
	public File get_mutant_directory() { return this.mutant_directory; }
	/**
	 * get the source files with their mutations in the project
	 * @return
	 */
	public Iterable<MutaSourceFile> get_source_files() { return this.source_files; }
	
	/* setters */
	/**
	 * generate the source files and their mutations from the 
	 * existing files of the source directory
	 * @throws Exception
	 */
	private void update_source_files() throws Exception {
		this.source_files.clear();
		File[] files = this.source_directory.listFiles();
		
		if(files != null) {
			for(File file : files) {
				MutaSourceFile source_file = 
						new MutaSourceFile(this, file);
				this.source_files.add(source_file);
			}
		}
	}
	/**
	 * add a new source file in the source directory
	 * @param code_file
	 * @throws Exception when the name of new code file conflicts with existing source file in directory
	 */
	public void add_source_file(File code_file) throws Exception {
		if(code_file == null || !code_file.exists())
			throw new IllegalArgumentException("Invalid code file: null");
		else {
			String file_name = code_file.getName();
			for(int index = 0; index < source_files.size(); index++) {
				MutaSourceFile source_file = source_files.get(index);
				String source_name = source_file.get_source_file().getName();
				if(source_name.equals(file_name)) {
					throw new IllegalArgumentException("Invalid: " + 
						source_file.get_source_file().getAbsolutePath());
				}
			}
			
			MutaSourceFile source_file = new MutaSourceFile(this, code_file);
			this.source_files.add(source_file);
		}
	}
	
	/* command generator */
	/**
	 * get the set of source files being compiled before mutated
	 * @return
	 * @throws Exception
	 */
	protected List<File> get_orig_source_files() throws Exception {
		List<File> source_files = new ArrayList<File>();
		for(MutaSourceFile source_file : this.source_files) {
			source_files.add(source_file.get_source_file());
		}
		return source_files;
	}
	/**
	 * get the files being compiled to generate mutated program
	 * @param mutant
	 * @return the first file refers to the mutant file generated
	 * @throws Exception
	 */
	protected List<File> get_muta_source_files(Mutant mutant, MutationCodeType type) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			List<File> source_files = new ArrayList<File>();
			source_files.add(mutant.generate_code(type));
			
			for(MutaSourceFile source_file : this.source_files) {
				if(source_file != mutant.get_space().get_source_file()) {
					source_files.add(source_file.get_source_file());
				}
			}
			
			return source_files;
		}
	}
	
}
