package com.jcsa.jcmutest.mutant.sad2mutant.muta;

public class SadMutation {
	
	/** the requirement for reaching the faulty statement **/
	private SadRequirement coverage;
	protected SadMutation(SadRequirement coverage) throws Exception {
		if(coverage == null)
			throw new IllegalArgumentException("Invalid coverage");
		else {
			this.coverage = coverage;
		}
	}
	
	/* getters */
	public SadRequirement get_coverage() {
		return this.coverage;
	}
	
	
}
