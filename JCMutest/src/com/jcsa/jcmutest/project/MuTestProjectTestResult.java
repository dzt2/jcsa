package com.jcsa.jcmutest.project;

import java.io.File;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcparse.base.BitSequence;
import com.jcsa.jcparse.test.file.TestInput;
import com.jcsa.jcparse.test.file.TestInputs;

/**
 * It records the state of test results for each mutant.
 * 
 * @author yukimula
 *
 */
public class MuTestProjectTestResult {
	
	/* definition */
	private boolean executed;
	private Mutant mutant;
	private BitSequence score;
	protected MuTestProjectTestResult(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		this.mutant = mutant;
		this.executed = false;
		this.score = null;
	}
	
	/* getters */
	/**
	 * @return whether the mutant has been executed before
	 *         amd false means it may not be compiled.
	 */
	public boolean is_executed() { return this.executed; }
	/**
	 * @return the mutant of which results are described
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return it specifies of which test in space kill this mutant
	 * 			or null if the mutant has not been executed yet.
	 */
	public BitSequence get_socre_list() { return this.score; }
	
	/* setters */
	/**
	 * Load the old data from result file
	 * @param rfile
	 * @throws Exception
	 */
	protected void load(File rfile) throws Exception {
		if(!rfile.exists()) {
			this.executed = false;
			this.score = null;
		}
		else {
			String text = FileOperations.read(rfile).strip();
			this.executed = true;
			this.score = new BitSequence(text.length());
			for(int k = 0; k < text.length(); k++) {
				if(text.charAt(k) == '0') {
					this.score.set(k, BitSequence.BIT0);
				}
				else {
					this.score.set(k, BitSequence.BIT1);
				}
			}
		}
	}
	/**
	 * save the result to the specified file in results directory
	 * @param rfile
	 * @throws Exception
	 */
	private void save(File rfile) throws Exception {
		FileOperations.delete(rfile);
		if(this.score != null) {
			FileOperations.write(rfile, this.score.toString());
		}
	}
	/**
	 * generate the score-matrix for the mutant 
	 * @param test_number the number of test inputs involved in testing
	 * @param n_outputs the directory where outputs from original program 
	 * @param m_outputs the directory where outputs from mutated programs
	 * @param rfile the file that preserves the updated results of mutant
	 * @throws Exception
	 */
	protected void update(TestInputs test_space, File n_outputs, File m_outputs, File rfile) throws Exception {
		if(test_space == null)
			throw new IllegalArgumentException("Invalid test_space: null");
		else if(n_outputs == null || !n_outputs.isDirectory())
			throw new IllegalArgumentException("Not directory: " + n_outputs);
		else if(m_outputs == null || !m_outputs.isDirectory())
			throw new IllegalArgumentException("Not directory: " + m_outputs);
		else if(rfile == null)
			throw new IllegalArgumentException("Invalid result file as null");
		else {
			this.score = new BitSequence(test_space.number_of_inputs());
			
			for(TestInput input : test_space.get_inputs()) {
				File osource = input.get_stdout_file(n_outputs);
				File otarget = input.get_stdout_file(m_outputs);
				if(FileOperations.compare(osource, otarget)) {
					File esource = input.get_stderr_file(n_outputs);
					File etarget = input.get_stderr_file(m_outputs);
					if(FileOperations.compare(esource, etarget)) {
						this.score.set(input.get_id(), BitSequence.BIT0);
					}
					else {
						this.score.set(input.get_id(), BitSequence.BIT1);
					}
				}
				else {
					this.score.set(input.get_id(), BitSequence.BIT1);
				}
			}
			
			this.save(rfile);
		}
	}
	
}
