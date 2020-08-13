package com.jcsa.jcmutest.project.util;

import java.io.File;

import com.jcsa.jcparse.test.cmd.CCompiler;
import com.jcsa.jcparse.test.cmd.CommandProcess;

class LinuxCommandUtil implements MuCommandUtil {
	
	/** the template for generating the pre-processing of C compilation using four parameters:
	 * 	(1) CCompiler compiler: the compiler program used to pre-process the xxx.c code file.
	 * 	(2) File cfile: the source code file {xxx.c} to be pre-processed.
	 * 	(3) File ifile: the intermediate code file {xxx.i} generated after pre-processing xxx.c.
	 * **/
	private static final String preprocess_template = "%s -ansi -fno-builtin -E -P %s -o %s";
	
	/** the template of command for running bash shell file:
	 * 	(1) File sfile: the shell script file being executed.
	 * **/
	private static final String exec_shell_template = "bash %s";
	
	protected LinuxCommandUtil() { }
	
	@Override
	public boolean do_preprocess(CCompiler compiler, File cfile, File ifile, Iterable<File> hdirs,
			Iterable<String> parameters) throws Exception {
		/* 1. generate the command for pre-processing */
		StringBuilder command = new StringBuilder();
		command.append(String.format(preprocess_template, compiler.toString(), 
						cfile.getAbsolutePath(), ifile.getAbsolutePath()));
		for(File hdir : hdirs) 
			command.append(" -I ").append(hdir.getAbsolutePath()); 
		for(String parameter : parameters) 
			command.append(" ").append(parameter); 
		String[] commands = command.toString().split(" ");
		
		/* 2. delete old ifile and perform compilation */
		FileOperations.delete(ifile);
		CommandProcess.do_process(commands, null, CommandProcess.buff_size_2);
		
		/* 3. whether the pre-processing succeeds */ return ifile.exists();
	}
	
	@Override
	public boolean do_compile(CCompiler compiler, Iterable<File> ifiles, File efile, Iterable<File> hdirs,
			Iterable<File> lfiles, Iterable<String> parameters) throws Exception {
		/* 1. generate the commands for running compilation */
		StringBuilder command = new StringBuilder();
		command.append(compiler.toString());
		for(File ifile : ifiles) command.append(" ").append(ifile.getAbsolutePath());
		command.append(" -o ").append(efile.getAbsolutePath());
		for(File hdir : hdirs) command.append(" -I ").append(hdir.getAbsolutePath());
		for(File lfile : lfiles) command.append(" -L ").append(lfile.getAbsolutePath());
		for(String parameter : parameters) command.append(" ").append(parameter);
		String[] commands = command.toString().strip().split(" ");
		
		/* 2. perform the compilation and remove old file */
		FileOperations.delete(efile);
		CommandProcess.do_process(commands, null, CommandProcess.buff_size_2);
		
		/* 3. whether the executional is generated */	return efile.exists();
	}
	
	@Override
	public boolean do_execute(File shell_file, File exe_dir) throws Exception {
		String[] command = String.format(exec_shell_template, 
				shell_file.getAbsolutePath()).strip().split(" ");
		CommandProcess.do_process(command, exe_dir, CommandProcess.buff_size_2);
		return true;
	}
	
}
