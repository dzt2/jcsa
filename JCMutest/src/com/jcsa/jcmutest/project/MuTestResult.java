package com.jcsa.jcmutest.project;

import java.io.File;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.project.util.FileOperations;
import com.jcsa.jcparse.base.BitSequence;

/**
 * It preserves the results of executing each mutant against each test.
 * 
 * @author yukimula
 *
 */
public class MuTestResult {
	
	/* definitions */
	private Mutant mutant;
	private BitSequence score;
	protected MuTestResult(Mutant mutant, int number_of_tests) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.mutant = mutant;
			this.score = new BitSequence(number_of_tests);
		}
	}
	
	/* getters */
	public Mutant get_mutant() { return this.mutant; }
	public BitSequence get_score_list() { return this.score; }
	public int get_score_degree() { return score.degree(); }
	
	/* setters */
	/**
	 * @param source
	 * @param target
	 * @return whether two files are identical
	 * @throws Exception
	 */
	private boolean compare(File source, File target) throws Exception {
		if(!source.exists()) {
			return !target.exists();
		}
		else if(!target.exists()) {
			return false;
		}
		else {
			String x = FileOperations.read(source);
			String y = FileOperations.read(target);
			return x.equals(y);
		}
	}
	/**
	 * update the score-list in the program
	 * @param n_outputs
	 * @param m_outputs
	 * @throws Exception
	 */
	protected void set(File n_outputs, File m_outputs) throws Exception {
		for(int tid = 0; tid < score.length(); tid++) {
			File nofile = new File(n_outputs.getAbsolutePath() + "/" + tid + ".out");
			File nefile = new File(n_outputs.getAbsolutePath() + "/" + tid + ".err");
			File mofile = new File(m_outputs.getAbsolutePath() + "/" + tid + ".out");
			File mefile = new File(m_outputs.getAbsolutePath() + "/" + tid + ".err");
			if(this.compare(nofile, mofile) && this.compare(nefile, mefile)) {
				score.set(tid, false);
			}
			else {
				score.set(tid, true);	// mutant is killed by the test
			}
		}
	}
	/**
	 * save the score list to the file
	 * @param rfile
	 * @throws Exception
	 */
	protected void save(File rfile) throws Exception {
		FileOperations.write(rfile, this.score.toString());
	}
	/**
	 * load the score list from the file
	 * @param rfile
	 * @throws Exception
	 */
	protected void load(File rfile) throws Exception {
		this.score.clear();
		if(rfile.exists()) {
			String text = FileOperations.read(rfile);
			text = text.strip();
			for(int k = 0; k < text.length(); k++) {
				switch(text.charAt(k)) {
				case '0':	score.set(k, false); break;
				case '1':	score.set(k, true);  break;
				default: 	break;
				}
			}
		}
	}
	
}
