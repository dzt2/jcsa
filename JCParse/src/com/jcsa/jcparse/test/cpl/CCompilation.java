package com.jcsa.jcparse.test.cpl;

import java.io.File;

import com.jcsa.jcparse.test.cmd.CommandProcess;

/**
 * It provides the interface to compile the C programs in command-line way.
 * 
 * @author yukimula
 *
 */
public class CCompilation {
	
	/** gcc|clang -ansi -fno-builtin -imacros configs/linux.h -E -P source.c -o target.c **/
	private static final String preprocess_template = "%s -ansi -fno-builtin -imacros %s -E -P %s -o %s";
	
	/**
	 * @param compiler the compiler used to compile the C program in preprocessing way.
	 * @param cfile the .c source file
	 * @param ifile the .i file being generated after preprocessing
	 * @param hfile the config/linux.h in imacros
	 * @param dfiles the set of directories for linking .h files in .c file
	 * @return the command to preprocess the compilation of .c file
	 * @throws Exception
	 */
	private static String preprocess_command(CCompiler compiler, 
			File cfile, File ifile, File hfile, Iterable<File> dfiles) throws Exception {
		StringBuilder command = new StringBuilder();
		
		command.append(String.format(preprocess_template, compiler.toString(), 
				hfile.getAbsolutePath(), cfile.getAbsolutePath(), ifile.getAbsolutePath()));
		for(File dfile : dfiles) {
			if(dfile != null && dfile.isDirectory()) {
				command.append(" -I ").append(dfile.getAbsolutePath());
			}
		}
		
		return command.toString();
	}
	
	/**
	 * @param compiler the compiler used to compile the C program files in generation way
	 * @param cfiles the set of .c files being used in compilation
	 * @param hdirs the set of directories where .h files are included and used
	 * @param lfiles the set of library files used in compilating and generation
	 * @param params the set of parameters used for compiling the .c source files
	 * @param efile the executional file being generated from .c files as given
	 * @return
	 * @throws Exception
	 */
	private static String compilation_command(CCompiler compiler, 
			Iterable<File> cfiles, Iterable<File> hdirs, 
			Iterable<File> lfiles, Iterable<String> params,
			File efile) throws Exception {
		StringBuilder command = new StringBuilder();
		
		command.append(compiler.toString());
		for(File cfile : cfiles) {
			command.append(" ").append(cfile.getAbsolutePath());
		}
		for(File hdir : hdirs) {
			command.append(" -I ").append(hdir.getAbsolutePath());
		}
		for(File lfile : lfiles) {
			command.append(" -L ").append(lfile.getAbsolutePath());
		}
		for(String param : params) {
			command.append(" ").append(param.strip());
		}
		command.append(" -o ").append(efile.getAbsolutePath());
		
		return command.toString();
	}
	
	/**
	 * @param compiler the compiler used to compile the C program in preprocessing way.
	 * @param cfile the .c source file
	 * @param ifile the .i file being generated after preprocessing
	 * @param hfile the config/linux.h in imacros
	 * @param dfiles the set of directories for linking .h files in .c file
	 * @return the command to preprocess the compilation of .c file
	 * @throws Exception
	 */
	public static boolean do_preprocess(CCompiler compiler, File cfile, 
			File ifile, File hfile, Iterable<File> dfiles) throws Exception {
		/* 1. generate the command for pre-processing the command-line */
		String command = CCompilation.preprocess_command(
					compiler, cfile, ifile, hfile, dfiles);
		String[] commands = command.strip().split(" ");
		
		/* 2. delete the original intermediate file for being over-written */
		if(ifile.exists()) {
			ifile.delete();
			while(ifile.exists());
		}
		
		/* 3. perform command processing for pre-processing source file */
		CommandProcess.do_process(commands, null, CommandProcess.buff_size_1);
		
		/* 4. return true if the generation succeed. */ return ifile.exists();
	}
	
	/**
	 * @param compiler the compiler used to compile the C program files in generation way
	 * @param cfiles the set of .c files being used in compilation
	 * @param hdirs the set of directories where .h files are included and used
	 * @param lfiles the set of library files used in compilating and generation
	 * @param params the set of parameters used for compiling the .c source files
	 * @param efile the executional file being generated from .c files as given
	 * @return true if the efile is generated successfully
	 * @throws Exception
	 */
	public static boolean do_compilation(CCompiler compiler, 
			Iterable<File> cfiles, Iterable<File> hdirs, 
			Iterable<File> lfiles, Iterable<String> params,
			File efile) throws Exception {
		/* 1. generate the command for running compilation */
		String command = CCompilation.compilation_command(
				compiler, cfiles, hdirs, lfiles, params, efile);
		String[] commands = command.strip().split(" ");
		
		/* 2. remove the original executional file */
		if(efile.exists()) {
			efile.delete();
			while(efile.exists());
		}
		
		/* 3. perform command processing for compiling source file */
		CommandProcess.do_process(commands, null, CommandProcess.buff_size_1);
		
		/* 4. return true if the generation succeed. */ return efile.exists();
	}
	
}
