package com.jcsa.jcparse.test.inp;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.test.cmd.CommandProcess;

/**
 * 	It provides the interface for executing the test inputs in space.
 * 	
 * 	@author yukimula
 *	
 */
public class TestInputExecution {
	
	/** the head of the bash-shell script language **/
	private static final String bash_shell_head = "#! /bin/bash\n\n";
	
	/** the template of command for running bash shell file **/
	private static final String exec_shell_template = "bash %s";
	
	/**
	 * @param efile the executional file for running test inputs
	 * @param inputs the test inputs being executed on the efile
	 * @param odir the output directory where stdout and stderr are generated
	 * @param timeout the maximal seconds that is needed for running test 
	 * 		  or negative (or zero) when the time-out is not established.
	 * @param shell_file the file where the shell scripts of running tests are recorded.
	 * @throws Exception
	 */
	private static void generate_bash(File efile, 
			Iterable<TestInput> inputs, File odir, 
			long timeout, File shell_file) throws Exception {
		FileWriter writer = new FileWriter(shell_file);
		writer.write(bash_shell_head);
		for(TestInput input : inputs) {
			writer.write(input.command(efile, odir, timeout) + "\n");
		}
		writer.close();
	}
	
	/**
	 * @param efile the executional file for running test inputs
	 * @param inputs the test inputs being executed on the efile
	 * @param odir the output directory where stdout and stderr are generated
	 * @param timeout the maximal seconds that is needed for running test 
	 * 		  or negative (or zero) when the time-out is not established.
	 * @param shell_file the file where the shell scripts of running tests are recorded.
	 * @param cur_directory the directory under which the command-lines are executed.
	 * @throws Exception this process can take much time for running the test inputs!
	 */
	public static void do_testing(File efile, Iterable<TestInput> inputs, File odir, 
			long timeout, File shell_file, File cur_directory) throws Exception {
		/** 1. delete the original shell script file **/
		if(shell_file.exists()) {
			shell_file.delete();
			while(shell_file.exists());
		}
		
		/** 2. generate the shell script file **/
		TestInputExecution.generate_bash(efile, inputs, odir, timeout, shell_file);
		
		/** 3. running the bash script file **/
		String command = String.format(exec_shell_template, shell_file.getAbsolutePath());
		CommandProcess.
				do_process(command.split(" "), cur_directory, CommandProcess.buff_size_2);
	}
	
}
