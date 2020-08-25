package com.jcsa.jcmutest.mutant.sad2mutant.muta;

import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;

/**
 * The propagation from a state error to another with some constraint being asserted.
 * 
 * @author yukimula
 *
 */
public class SadPropagation {
	
	/* definition */
	/** the constraint required to be hold for propagation **/
	private SadAssertion constraint;
	/** the source requirement that causes another target **/
	private SadRequirement source;
	/** the target requirement that caused by another target **/
	private SadRequirement target;
	/**
	 * propagation from source to target with a constraint
	 * @param constraint
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected SadPropagation(SadAssertion constraint, SadRequirement 
				source, SadRequirement target) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			this.constraint = constraint;
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return the constraint required to be hold for propagation
	 */
	public SadAssertion get_constraint() {
		return this.constraint;
	}
	/**
	 * @return the source requirement that causes another target
	 */
	public SadRequirement get_source() {
		return this.source;
	}
	/**
	 * @return the target requirement that caused by the source
	 */
	public SadRequirement get_target() {
		return this.target;
	}
	
}
