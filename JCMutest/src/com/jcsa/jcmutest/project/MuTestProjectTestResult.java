package com.jcsa.jcmutest.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcparse.base.BitSequence;

/**
 * It records the data information from testing results, including:<br>
 * 	1. mutant: the mutation that is executed against test cases.<br>
 * 	2. exec_set: the matrix defines of which tests are executed against the mutant.<br>
 * 	3. kill_set: the matrix defines of which tests in exec_set killed the mutation.<br>
 * <br>
 * NOTE that: the kill-set is the subset of the exec-set.<br>
 * 
 * @author yukimula
 *
 */
public class MuTestProjectTestResult {
	
	/* definitions */
	/** the mutation that is executed against test cases **/
	private Mutant mutant;
	/** the matrix defines of which tests are executed against the mutant **/
	private BitSequence exec_set;
	/** the matrix defines of which tests in exec_set killed the mutation **/
	private BitSequence kill_set;
	/**
	 * create a test result initialized as non-compiled mutant with non-executed
	 * set of test matrix.
	 * @param mutant
	 * @param number_of_tests
	 * @throws Exception
	 */
	protected MuTestProjectTestResult(Mutant mutant, int number_of_tests) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else if(number_of_tests < 0)
			throw new IllegalArgumentException("Invalid number-of-tests");
		else {
			this.mutant = mutant;
			this.exec_set = new BitSequence(number_of_tests);
			this.kill_set = new BitSequence(number_of_tests);
		}
	}
	
	/* getters */
	/**
	 * @return the mutation that is executed against test cases
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the number of test inputs to build up the mutation project
	 */
	public int number_of_tests() { return this.exec_set.length(); }
	/**
	 * @return the matrix defines of which tests are executed against the mutant
	 */
	public BitSequence get_exec_set() { return this.exec_set; }
	/**
	 * @return the matrix defines of which tests in exec_set killed the mutation
	 */
	public BitSequence get_kill_set() { return this.kill_set; }
	
	/* setters */
	/**
	 * exec_set			{101101...001011}
	 * kill_set			{100001...001000}
	 * @param rfile
	 * @throws Exception
	 */
	protected void save(File rfile) throws Exception {
		if(rfile == null)
			throw new IllegalArgumentException("Invalid rfile: null");
		else {
			FileWriter writer = new FileWriter(rfile);
			writer.write(this.exec_set.toString() + "\n");
			writer.write(this.kill_set.toString() + "\n");
			writer.close();
		}
	}
	/**
	 * @param rfile
	 * @return 	(1) if rfile does not exist, returns null;
	 * 			(2) else generate the test result saved in file.
	 * @throws Exception
	 */
	protected static MuTestProjectTestResult load(Mutant mutant, int number_of_tests, File rfile) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else if(rfile == null)
			throw new IllegalArgumentException("Invalid rfile: null");
		else if(rfile.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(rfile));
			MuTestProjectTestResult result = new MuTestProjectTestResult(mutant, number_of_tests);
			String line = reader.readLine();
			if(line != null)
				parse(result.exec_set, line.strip());
			else {
				reader.close(); return null;
			}
			line = reader.readLine();
			if(line != null)
				parse(result.kill_set, line.strip());
			else {
				reader.close(); return null;
			}
			reader.close();
			return result;
		}
		else {
			return null;	/* no result file for mutant if it has not been tested */
		}
	}
	private static void parse(BitSequence bits, String text) throws Exception {
		for(int k = 0; k < text.length() && k < bits.length(); k++) {
			switch(text.charAt(k)) {
			case '0':	bits.set(k, BitSequence.BIT0); 	break;
			case '1':	bits.set(k, BitSequence.BIT1); 	break;
			default: 	/* invalid character ignored */	break;
			}
		}
	}
	
}
