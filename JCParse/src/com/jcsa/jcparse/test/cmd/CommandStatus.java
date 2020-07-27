package com.jcsa.jcparse.test.cmd;

/**
 * The status of running the command-line.
 * 
 * @author yukimula
 *
 */
public enum CommandStatus {
	
	/** normally exit from command-process **/	normal_exit,
	
	/** command-line exits when out-of-time **/	out_of_time,
	
	/** buffer to preserve is out of memory **/	out_of_buff,
	
	/** process exits in exceptional way **/	except_exit,
	
}
