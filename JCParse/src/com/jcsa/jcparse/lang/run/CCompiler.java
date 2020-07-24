package com.jcsa.jcparse.lang.run;

import java.io.File;

/**
 * It generates the command for compiling source file into .exe program.
 * 
 * @author yukimula
 *
 */
public class CCompiler {
	
	/** the program used to compile program into .exe file **/
	private CCompilers compiler;
	
	/** gcc|clang -ansi -fno-builtin -imacros configs/linux.h -E -P source.c -o target.c **/
	private static final String preprocess_template = "%s -ansi -fno-builtin -imacros %s -E -P %s -o %s";
	
	/**
	 * @param cfile the source code file before being preprecessed.
	 * @param tfile the code file with preprocessed (without #include...)
	 * @param hfile the config/linux.h for removing special keyword in C89.
	 * @param ifiles the directory where the .h files are included.
	 * @return the command for running pre-processing compilation.
	 * @throws Exception
	 */
	public String preprocess_command(File cfile, File tfile, File hfile, Iterable<File> ifiles) throws Exception {
		StringBuilder command = new StringBuilder();
		command.append(String.format(preprocess_template, 
				this.compiler.toString(), hfile.getAbsolutePath(), 
				cfile.getAbsolutePath(), tfile.getAbsolutePath()));
		for(File ifile : ifiles) { command.append(" -I ").append(ifile.getAbsolutePath()); }
		return command.toString();
	}
	
	/**
	 * perform preprocess command on specified inputs.
	 * @param cfile the source code file before being preprecessed.
	 * @param tfile the code file with preprocessed (without #include...)
	 * @param hfile the config/linux.h for removing special keyword in C89.
	 * @param ifiles the directory where the .h files are included.
	 * @return true if the pre-processing-compilation success.
	 * @throws Exception
	 */
	public boolean do_preprocess(File cfile, File tfile, File hfile, Iterable<File> ifiles) throws Exception {
		/* 1. generate the command for running preprocessing */
		String command = this.preprocess_command(cfile, tfile, hfile, ifiles);
		String[] commands = command.strip().split(" ");
		
		/* 2. delete the original intermediate file for being over-written */
		if(tfile.exists()) {
			tfile.delete();
			while(tfile.exists());
		}
		
		/* 3. perform command processing for pre-processing source file */
		CommandProcess.do_process(commands, null, CommandProcess.buffer_size_0);
		
		/* 4. return true if the generation succeeds. */
		return tfile.exists();
	}
	
	/**
	 * clang|gcc 
	 * @param cfiles the set of .c files being compiled together
	 * @param hfiles the directories where .h files are included.
	 * @param lfiles the directories where library files are linked.
	 * @param params other parameters used in compilation.
	 * @param exe_file the executional file being compiled.
	 * @return the command for running compilation
	 * @throws Exception
	 */
	public String compile_command(Iterable<File> cfiles, 
			Iterable<File> hfiles, 
			Iterable<File> lfiles,
			Iterable<String> params,
			File exe_file) throws Exception {
		StringBuilder command = new StringBuilder();
		
		command.append(this.compiler.toString());
		for(File cfile : cfiles) {
			command.append(" ").append(cfile.getAbsolutePath());
		}
		for(File hfile : hfiles) {
			command.append(" -I ").append(hfile.getAbsolutePath());
		}
		for(File lfile : lfiles) {
			command.append(" -L ").append(lfile.getAbsolutePath());
		}
		for(String param : params) {
			command.append(" ").append(param.strip());
		}
		command.append(" -o ").append(exe_file.getAbsolutePath());
		
		return command.toString();
	}
	
	/**
	 * @param cfiles the set of .c files being compiled together
	 * @param hfiles the directories where .h files are included.
	 * @param lfiles the directories where library files are linked.
	 * @param params other parameters used in compilation.
	 * @param exe_file the executional file being compiled.
	 * @return true if the executional file is generated.
	 * @throws Exception
	 */
	public boolean do_compile(Iterable<File> cfiles, 
			Iterable<File> hfiles, 
			Iterable<File> lfiles,
			Iterable<String> params,
			File exe_file) throws Exception {
		/* 1. generate the command for running compilation */
		String command = this.compile_command(cfiles, hfiles, lfiles, params, exe_file);
		String[] commands = command.strip().split(" ");
		
		/* 2. remove the original executional file */
		if(exe_file.exists()) {
			exe_file.delete();
			while(exe_file.exists());
		}
		
		/* 3. perform command processing for compiling source file */
		CommandProcess.do_process(commands, null, CommandProcess.buffer_size_1);
		
		/* 4. return whether exe file is generated */	return exe_file.exists();
	}
	
	/**
	 * @param compiler the program used in compilation
	 */
	private CCompiler(CCompilers compiler) throws Exception {
		if(compiler == null)
			throw new IllegalArgumentException("Invalid compiler: null");
		this.compiler = compiler;
	}
	
	/**
	 * create a compiler with specified program being used.
	 * @param compiler
	 * @return
	 * @throws Exception
	 */
	public static CCompiler get_compiler(CCompilers compiler) throws Exception {
		return new CCompiler(compiler);
	}
	
}
