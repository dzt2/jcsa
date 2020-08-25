package com.jcsa.jcmutest.mutant.sad2mutant.muta;

import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;

/**
 * The cause-effect relationship between SadVertex.
 * 
 * @author yukimula
 *
 */
public class SadRelation {
	
	/* definitions */
	/** the assertion to be hold for the cause-effect being caused **/
	private SadAssertion assertion;
	/** the source vertex as the cause that leads the target as effect **/
	private SadVertex source;
	/** the target vertex as the effect that lead from source as cause **/
	private SadVertex target;
	/**
	 * create a cause-effect relationship from source to target with 
	 * an assertion that needs to be hold for cause-effect to occur.
	 * @param assertion
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected SadRelation(SadAssertion assertion, SadVertex 
				source, SadVertex target) throws Exception {
		if(assertion == null)
			throw new IllegalArgumentException("Invalid assertion: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			this.assertion = assertion;
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return the assertion to be hold for the cause-effect being caused
	 */
	public SadAssertion get_assertion() {
		return this.assertion;
	}
	/**
	 * @return the source vertex as the cause that leads the target as effect
	 */
	public SadVertex get_source() {
		return this.source;
	}
	/**
	 * @return the target vertex as the effect that lead from source as cause
	 */
	public SadVertex get_target() {
		return this.target;
	}
	
}
