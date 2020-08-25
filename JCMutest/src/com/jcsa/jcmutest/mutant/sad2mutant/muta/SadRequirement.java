package com.jcsa.jcmutest.mutant.sad2mutant.muta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;

public class SadRequirement {
	
	/* definitions */
	/** the assertion that defines the requirement **/
	private SadAssertion assertion;
	/** the propagation from its causes **/
	private List<SadPropagation> in;
	/** the propagation that causes the effects fromt this node **/
	private List<SadPropagation> ou;
	/**
	 * create an isolated requirement w.r.t. the assertion.
	 * @param assertion
	 * @throws Exception
	 */
	protected SadRequirement(SadAssertion assertion) throws Exception {
		if(assertion == null)
			throw new IllegalArgumentException("Invalid assertion: null");
		else {
			this.assertion = assertion;
			this.in = new LinkedList<SadPropagation>();
			this.ou = new LinkedList<SadPropagation>();
		}
	}
	
	/* getters */
	/**
	 * @return the assertion that defines the requirement
	 */
	public SadAssertion get_assertion() { return this.assertion; }
	/**
	 * @return the propagation from its causes
	 */
	public Iterable<SadPropagation> get_in_propagations() {
		return this.in;
	}
	/**
	 * @return the propagation that causes the effects from this node
	 */
	public Iterable<SadPropagation> get_ou_propagations() {
		return this.ou;
	}
	@Override
	public String toString() {
		return this.assertion.toString();
	}
	/**
	 * @param constraint
	 * @param target
	 * @return link this cause to an effect with specified constraint
	 * @throws Exception
	 */
	public SadPropagation propagate(SadAssertion constraint, SadRequirement target) throws Exception {
		SadPropagation propagation = new SadPropagation(constraint, this, target);
		this.ou.add(propagation);
		target.in.add(propagation);
		return propagation;
	}
	/**
	 * @param target
	 * @return link this cause to an effect without any constraints
	 * @throws Exception
	 */
	public SadPropagation propagate(SadRequirement target) throws Exception {
		SadAssertion constraint = SadFactory.conjunct(
				target.assertion.get_location(), new ArrayList<SadAssertion>());
		SadPropagation propagation = new SadPropagation(constraint, this, target);
		this.ou.add(propagation);
		target.in.add(propagation);
		return propagation;
	}
	
}
