package com.jcsa.jcparse.test.cmd;

import java.io.File;
import java.io.IOException;

/**
 * It provides the interface to run command-line for Java on the system platform
 * on which it is executed.
 *
 * @author yukimula
 *
 */
public class CommandProcess implements Runnable {

	/* properties */
	/** the maximal number of bytes allowed to preserve in buffers **/
	private int buffer_size;
	/** the exit code after running the command-line as specified **/
	private int exit_code;
	/** the command-line to be executed in the runner thread **/
	private String[] commands;
	/** the current directory where the command-line is executed **/
	private File cursor;
	/** whether it is out of time when the command-line is finished **/
	private boolean out_of_time;
	/** the buffer to preserve standard output **/
	private StringBuilder stdout;
	/** the buffer to preserve standard errors **/
	private StringBuilder stderr;
	/** the thread that executes this process runnable **/
	private Thread thread;

	/* constructor */
	/**
	 *
	 * @param cmd : command to process
	 * @param dir : directory under execution
	 * @param buff_size : negative if not to save the buffer
	 * @param time_out : negative if no time-out is set
	 * @throws Exception
	 */
	private CommandProcess(String[] cmd, File dir, int buff_size) throws Exception {
		if(cmd == null || cmd.length <= 0)
			throw new IllegalArgumentException("Invalid commands: null");
		else {
			this.commands = cmd; this.cursor = dir;
			this.buffer_size = buff_size;
			this.exit_code = 0;
			this.out_of_time = false;
			this.stdout = new StringBuilder();
			this.stderr = new StringBuilder();
			this.thread = null;
		}
	}

	@Override
	public void run() {
		/** 1. initialize the runtime process **/
		Runtime rt = Runtime.getRuntime();
		Process proc = null;
		StreamConsumer stdout_consumer = null;
		StreamConsumer stderr_consumer = null;
		Thread stdout_thread = null, stderr_thread = null;

		try {
			/** 2. construct the command-line process **/
			if(cursor == null) proc = rt.exec(commands, null);
			else proc = rt.exec(commands, null, cursor);

			/** 3. create the consumers that consumes the stdout and stderr information **/
			/* consumes the stdout and stderr information */
			stdout_consumer = new StreamConsumer(proc.getInputStream(), buffer_size, stdout);
			stderr_consumer = new StreamConsumer(proc.getErrorStream(), buffer_size, stderr);
			stderr_thread = new Thread(stderr_consumer); stderr_thread.start();
			stdout_thread = new Thread(stdout_consumer); stdout_thread.start();
			stderr_thread.join(); stdout_thread.join();

			/** 3. wait for the command-line process to terminate **/
			exit_code = proc.waitFor();
		}
		/** case-1. interrupt occurs when running command-line is out of time **/
		catch (InterruptedException e) {
			exit_code = -1;
			out_of_time = true;
		}
		/** case-2. interrupt when the IO error occurs in reading input-stream **/
		catch (IOException e) {
			e.printStackTrace();
			exit_code = -2;
		}
		/** case-3. when other exception occurs, it assumes normally exit from **/
		catch (Exception e) {
			e.printStackTrace();
			exit_code = -3;
		}
		/** finally, it always destroy the command-line process and threads **/
		finally {
			/* destroy command-line process */ 	proc.destroy();
		}
	}
	/**
	 * execute the thread under the command until it is completed
	 * @return the result of executing the command-line process
	 * @throws Exception
	 */
	public CommandResult join() throws Exception {
		this.thread.join();
		CommandStatus status;
		if(this.out_of_time) {
			status = CommandStatus.out_of_time;
		}
		else if(this.stdout.length() >= this.buffer_size
				|| this.stderr.length() >= this.buffer_size) {
			status = CommandStatus.out_of_buff;
		}
		else if(this.exit_code != 0) {
			status = CommandStatus.except_exit;
		}
		else {
			status = CommandStatus.normal_exit;
		}
		return new CommandResult(status, this.exit_code,
				this.stdout.toString(), this.stderr.toString());
	}

	/* running method */
	/**
	 * @param command the command-line to be executed
	 * @param cur_directory the directory where the command-line is executed
	 * @param buffer_length the number of bytes allowed in stdout and stderr
	 * @return the result of running the command-line
	 * @throws Exception
	 */
	public static CommandResult do_process(String[] command,
			File cur_directory, int buffer_length) throws Exception {
		/** 1. create the command-line processing threads **/
		CommandProcess processor =
				new CommandProcess(command, cur_directory, buffer_length);

		/** 2. execute the command-line process until it finally ends **/
		Thread thread = new Thread(processor); thread.start(); thread.join();

		/** 3. generate the command-line result created by the process **/
		CommandStatus status;
		if(processor.out_of_time) {
			status = CommandStatus.out_of_time;
		}
		else if(processor.stdout.length() >= processor.buffer_size
				|| processor.stderr.length() >= processor.buffer_size) {
			status = CommandStatus.out_of_buff;
		}
		else if(processor.exit_code != 0) {
			status = CommandStatus.except_exit;
		}
		else {
			status = CommandStatus.normal_exit;
		}
		return new CommandResult(status, processor.exit_code,
				processor.stdout.toString(), processor.stderr.toString());
	}
	/**
	 * @param command
	 * @param cur_directory
	 * @param buffer_length
	 * @return the process with a thread that starts to execute it
	 * @throws Exception
	 */
	public static CommandProcess new_process(String[] command,
			File cur_directory, int buffer_length) throws Exception {
		/** 1. create the command-line processing threads **/
		CommandProcess processor =
				new CommandProcess(command, cur_directory, buffer_length);

		/** 2. execute the command-line process concurrently **/
		Thread thread = new Thread(processor); thread.start();

		/** 3. reset the thread and return the process thread **/
		processor.thread = thread;	return processor;
	}

	/* buffer size selections */
	public static final int buff_size_0 = 1024 * 16;
	public static final int buff_size_1 = 1024 * 1024;
	public static final int buff_size_2 = 1024 * 1024 * 64;
	public static final int buff_size_3 = 1024 * 1024 * 512;

}
