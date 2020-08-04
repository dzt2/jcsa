package com.jcsa.jcparse.test.cmd;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.parse.code.CodeGeneration;
import com.jcsa.jcparse.test.CommandUtil;
import com.jcsa.jcparse.test.file.TestInput;

/**
 * It performs the implementation of running command-line interfaces
 * on the platform of Linux system.
 * 
 * @author yukimula
 *
 */
public class LinuxCommandUtil implements CommandUtil {
	
	/**
	 * @param file the file to be deleted
	 * @throws Exception
	 */
	private void delete_file(File file) throws Exception {
		file.delete();
		while(file.exists());
	}
	
	/** the template for generating the pre-processing of C compilation using four parameters:
	 * 	(1) CCompiler compiler: the compiler program used to pre-process the xxx.c code file.
	 * 	(2) File cfile: the source code file {xxx.c} to be pre-processed.
	 * 	(3) File ifile: the intermediate code file {xxx.i} generated after pre-processing xxx.c.
	 * **/
	private static final String pre_process_template = "%s -ansi -fno-builtin -E -P %s -o %s";
	
	/** the head of the bash-shell script language **/
	private static final String bash_shell_head = "#! /bin/bash\n\n";
	
	/** the template for changing current directory **/
	private static final String cd_template = "cd %s\n";
	
	/** the template of command for running bash shell file:
	 * 	(1) File sfile: the shell script file being executed.
	 * **/
	private static final String exec_shell_template = "bash %s";
	
	/** command for copying instrumental result to specified file **/
	private static final String copy_file_template = "cp %s %s\n";
	
	/** command for deleting the instrumental result file in testing **/
	private static final String remove_file_template = "rm %s\n";
	
	@Override
	public boolean do_preprocess(CCompiler compiler, File cfile, File ifile, Iterable<File> hdirs,
			Iterable<File> mfiles) throws Exception {
		/* 1. generate the pre-processing command for creating the ifile */
		StringBuilder command = new StringBuilder();
		command.append(String.format(pre_process_template, compiler.
				toString(), cfile.getAbsolutePath(), ifile.getAbsolutePath()));
		for(File hdir : hdirs) command.append(" -I ").append(hdir.getAbsolutePath());
		for(File mfile : mfiles) command.append(" -imacros ").append(mfile.getAbsolutePath());
		String[] commands = command.toString().strip().split(" ");
		
		/* 2. perform the pre-processing compilation on cfile */
		this.delete_file(ifile);
		CommandProcess.do_process(commands, null, CommandProcess.buff_size_2);
		
		/* 3. whether intermediate code generated. */	return ifile.exists();
	}

	@Override
	public boolean do_compile(CCompiler compiler, Iterable<File> ifiles, File efile, Iterable<File> hdirs,
			Iterable<File> lfiles, Iterable<String> params) throws Exception {
		/* 1. generate the command for compiling C programs. */
		StringBuilder command = new StringBuilder();
		command.append(compiler.toString());
		for(File ifile : ifiles) command.append(" ").append(ifile.getAbsolutePath());
		command.append(" -o ").append(efile.getAbsolutePath());
		for(File hdir : hdirs) command.append(" -I ").append(hdir.getAbsolutePath());
		for(File lfile : lfiles) command.append(" -L ").append(lfile.getAbsolutePath());
		for(String param : params) {
			if(!param.isBlank()) command.append(" ").append(param.strip());
		}
		String[] commands = command.toString().strip().split(" ");
		
		/* 2. generate the xxx.exe file by compiling the source code files */
		this.delete_file(efile);
		CommandProcess.do_process(commands, null, CommandProcess.buff_size_2);
		
		/* 3. whether intermediate code generated. */	return efile.exists();
	}
	
	@Override
	public boolean do_instrument(File ifile, File sfile, File 
			rfile, File c_template_file, ClangStandard standard) throws Exception {
		/* 1. parse the intermediate code file (after pre-processing) */
		AstCirFile ast_file = AstCirFile.parse(ifile, c_template_file, standard);
		AstTree ast_tree = ast_file.get_ast_tree();
		
		/* 2. generate the code with instrumental methods being seeded */
		String code = CodeGeneration.instrument_code(ast_tree, rfile);
		FileWriter writer = new FileWriter(sfile); writer.write(code); writer.close();
		
		/* 3. whether the instrumental code is generated */	return sfile.exists();
	}

	@Override
	public boolean gen_normal_test_shell(File cdir, File efile, 
			Iterable<TestInput> inputs, File odir, long timeout, File sfile) throws Exception {
		FileWriter writer = new FileWriter(sfile);
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template, cdir.getAbsolutePath()));
		for(TestInput input : inputs) {
			String command = input.command(efile, odir, timeout);
			writer.write(command + "\n");
		}
		writer.write("\n"); writer.close(); 
		return sfile.exists();
	}

	@Override
	public boolean gen_instrumental_shell(File cdir, File efile, 
			Iterable<TestInput> inputs, File rfile, File odir, long timeout,
			File sfile) throws Exception {
		FileWriter writer = new FileWriter(sfile);
		writer.write(bash_shell_head);
		writer.write(String.format(cd_template, cdir.getAbsolutePath()));
		for(TestInput input : inputs) {
			String command = input.command(efile, odir, timeout);
			writer.write(String.format(remove_file_template, rfile.getAbsolutePath()));
			writer.write(command + "\n");
			writer.write(String.format(
					copy_file_template, rfile.getAbsolutePath(), 
					input.get_instrument_file(odir).getAbsolutePath()));
			writer.write("\n");
		}
		writer.write("\n"); writer.close(); 
		return sfile.exists();
	}

	@Override
	public boolean do_execute_shell(File sfile, File cdir) throws Exception {
		String[] command = String.format(exec_shell_template, sfile.getAbsolutePath()).strip().split(" ");
		CommandProcess.do_process(command, cdir, CommandProcess.buff_size_2);
		return true;
	}
	
}
