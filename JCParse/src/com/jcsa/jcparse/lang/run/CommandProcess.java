package com.jcsa.jcparse.lang.run;

//import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A thread that processes the command line in local system platform.
 * @author yukimula
 */
public class CommandProcess implements Runnable {
	
	/**
	 * collect the information from stream
	 * @author yukimula
	 */
	protected static class StreamConsumer implements Runnable {
		
		private StringBuilder buffer;
		private int max_buff_size;
		private InputStreamReader reader;
		protected StreamConsumer(InputStream in, int max_size, StringBuilder buffer) throws Exception {
			if(in == null)
				throw new IllegalArgumentException("Invalid stream: null");
			else {
				this.max_buff_size = max_size; this.buffer = buffer;
				this.reader = new InputStreamReader(in); 
			}
		}
		
		@Override
		public void run() {
			//BufferedReader reader = new BufferedReader(rs);
			char[] buff = new char[1024]; int length;
			while((length = this.get(buff)) != -1) {
				if(buffer.length() <= max_buff_size)
					buffer.append(buff, 0, length);
				else break;
			}
			/* end of all */
			try {
				reader.close(); 
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private int get(char[] buff) {
			try {
				return reader.read(buff);
			} catch (IOException e) {
				// e.printStackTrace();
				return -1;
			}
		}
		public void terminate() { 
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}
		public StringBuilder get_buffer() { return buffer; }
	}
	
	public static final int buffer_size_0 = 1024 * 16;
	public static final int buffer_size_1 = 1024 * 1024;
	public static final int buffer_size_2 = 1024 * 1024 * 128;
	public static final int buffer_size_3 = 1024 * 1024 * 512;
	
	/* properties */
	private int buffer_size;
	private int exit_code;
	private String[] commands;
	private File cursor;
	private boolean out_of_time;
	private boolean out_of_memory;
	private StringBuilder stdout, stderr;
	
	/**
	 * 
	 * @param cmd : command to process
	 * @param dir : directory under execution
	 * @param buff_size : negative if not to save the buffer
	 * @param time_out : negative if no time-out is set
	 * @throws Exception
	 */
	protected CommandProcess(String[] cmd, File dir, int buff_size) throws Exception {
		if(cmd == null || cmd.length <= 0)
			throw new IllegalArgumentException("Invalid commands: null");
		/*else if(dir == null || !dir.exists() || !dir.isDirectory())
			throw new IllegalArgumentException("Invalid directory: null");*/
		else {
			this.commands = cmd; this.cursor = dir;
			this.buffer_size = buff_size;
			this.exit_code = 0;
			this.out_of_time = false;
			this.out_of_memory = false;
			this.stdout = new StringBuilder(); 
			this.stderr = new StringBuilder();
		}
	}
	@Override
	public void run() {
		/* get execution runtime object */
		Runtime rt = Runtime.getRuntime();
		Process proc = null;
		StreamConsumer stdout_consumer = null;
		StreamConsumer stderr_consumer = null;
		Thread stdout_thread = null, stderr_thread = null;
		
		try {
			/* start command process */
			if(cursor == null) proc = rt.exec(commands, null);
			else proc = rt.exec(commands, null, cursor);
			
			/* consumes the stdout and stderr information */
			stdout_consumer = new StreamConsumer(proc.getInputStream(), buffer_size, stdout);
			stderr_consumer = new StreamConsumer(proc.getErrorStream(), buffer_size, stderr);
			stderr_thread = new Thread(stderr_consumer); stderr_thread.start();
			stdout_thread = new Thread(stdout_consumer); stdout_thread.start();
			stderr_thread.join(); stdout_thread.join();
			
			/* wait for termination */
			exit_code = proc.waitFor(); 
		} catch (InterruptedException e) {
			//e.printStackTrace();
			// might be unable to be triggered now...
			exit_code = -1; out_of_time = true;	
		} catch (IOException e) {
			e.printStackTrace();
			exit_code = -2;
		} catch (Exception e) {
			e.printStackTrace();
			exit_code = 0;
		}
		finally {
			// destroy all processes
			proc.destroy();	
			
			// set out of memory
			if(stdout.length() > buffer_size)
				this.out_of_memory = true;
		}
	}
	
	public int get_exit_code() { return exit_code; }
	public boolean is_out_of_time() { return out_of_time; }
	public boolean is_out_of_memory() { return out_of_memory; }
	public StringBuilder get_stdout() { return stdout; }
	public StringBuilder get_stderr() { return stderr; }
	public void clear() { 
		if(stdout != null) stdout.setLength(0); 
		if(stderr != null) stderr.setLength(0); 
	}
	
	/**
	 * Do the execution of the given command 
	 * @param command the command being executed in runtime-environment.
	 * @param run_directory the directory where the command is running or null for current directory.
	 * @param buffer_size the maximal size for preserving information in stdout and stderr. 
	 * @return all the resulting information is preserved in the object of CommandProcess.
	 * @throws Exception
	 */
	public static CommandProcess do_process(String[] command, File run_directory, int buffer_size) throws Exception {
		CommandProcess processor = new CommandProcess(command, run_directory, buffer_size);
		Thread thread = new Thread(processor); thread.start(); thread.join(); return processor;
	}
	
}
