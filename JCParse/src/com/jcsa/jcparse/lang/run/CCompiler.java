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
	 * @param ifile the code file with preprocessed (without #include...)
	 * @param hfile the config/linux.h for removing special keyword in C89.
	 * @return the command for running pre-processing compilation.
	 * @throws Exception
	 */
	public String preprocess_command(File cfile, File ifile, File hfile) throws Exception {
		String command = String.format(preprocess_template, 
				this.compiler.toString(), hfile.getAbsolutePath(), 
				cfile.getAbsolutePath(), ifile.getAbsolutePath());
		return command;
	}
	
	/**
	 * perform preprocess command on specified inputs.
	 * @param cfile the source code file before being preprecessed.
	 * @param ifile the code file with preprocessed (without #include...)
	 * @param hfile the config/linux.h for removing special keyword in C89.
	 * @return true if the pre-processing-compilation success.
	 * @throws Exception
	 */
	public boolean do_preprocess(File cfile, File ifile, File hfile) throws Exception {
		/* 1. generate the command for running preprocessing */
		String command = this.preprocess_command(cfile, ifile, hfile);
		String[] commands = command.strip().split(" ");
		
		/* 2. delete the original intermediate file for being over-written */
		if(ifile.exists()) {
			ifile.delete();
			while(ifile.exists());
		}
		
		/* 3. perform command processing for pre-processing source file */
		CommandProcess.do_process(commands, null, CommandProcess.buffer_size_0);
		
		/* 4. return true if the generation succeeds. */
		return ifile.exists();
	}
	
	
	
}
