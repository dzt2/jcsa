package com.jcsa.jcmuta.project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The thread to execute the system command process in local platform.
 * @author yukimula
 *
 */
public class CommandProcess implements Runnable {
	
	/* thread to consume the standard output and errors */
	/**
	 * The sub-thread to consume the bytes generated in a stream.
	 * @author yukimula
	 *
	 */
	protected class StreamConsumer implements Runnable {
		
		private InputStreamReader reader;
		
		protected StreamConsumer(InputStream input_stream) throws Exception {
			if(input_stream == null)
				throw new IllegalArgumentException("Invalid input_stream");
			else {
				this.reader = new InputStreamReader(input_stream);
			}
		}
		
		@Override
		public void run() {
			/* read all the bytes within */
			char[] buff = new char[1024]; 
			while(this.get(buff) != -1) ;
			
			/* end of all */
			try {
				reader.close(); 
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * safety reading input stream
		 * @param buff
		 * @return
		 */
		private int get(char[] buff) {
			try {
				return reader.read(buff);
			} catch (IOException e) {
				return -1;
			}
		}
		
	}
	
	/* attributes */
	private String command;
	private int return_code;
	private File work_directory;
	
	/* constructor */
	/**
	 * To create a thread to execute the command-line process in local system
	 * @param command : command to process
	 * @param work_directory : directory under execution
	 * @throws Exception
	 */
	protected CommandProcess(String command, File work_directory) throws Exception {
		if(command == null || command.isBlank())
			throw new IllegalArgumentException("No command is provided");
		else if(work_directory == null || !work_directory.isDirectory())
			throw new IllegalArgumentException("No work-directory used");
		else {
			this.command = command.strip();
			this.work_directory = work_directory;
			this.return_code = 0;
		}
	}
	
	@Override
	public void run() {
		/* get execution runtime object */
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		StreamConsumer stdout = null, stderr = null;
		Thread stdout_thread = null, stderr_thread = null;
		
		try {
			/* start command process */
			/*if(cursor == null) proc = rt.exec(commands, null);
			else proc = rt.exec(commands, null, cursor); */
			process = runtime.exec(this.command, null, this.work_directory);
			
			/* consumes the stdout and stderr information */
			stdout = new StreamConsumer(process.getInputStream());
			stderr = new StreamConsumer(process.getErrorStream());
			stderr_thread = new Thread(stdout); stderr_thread.start();
			stdout_thread = new Thread(stderr); stdout_thread.start();
			
			/* wait for termination */
			this.return_code = process.waitFor();
			stderr_thread.join(); stdout_thread.join();
		} catch (InterruptedException e) {
			this.return_code = -1; 
		} catch (IOException e) {
			e.printStackTrace();
			this.return_code = -2;
		} catch (Exception e) {
			e.printStackTrace();
			this.return_code = 0;
		}
		finally {
			process.destroy();
		}
	}
	
	protected int get_return_code() { return this.return_code; }
	
}
