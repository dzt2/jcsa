package com.jcsa.jcmuta.mutant.sem2mutation.muta;

import java.util.LinkedList;
import java.util.List;

public class SemanticInference {
	
	/* attributes */
	/** the assertions that required to be hold before **/
	private List<SemanticAssertion> prev_conditions;
	/** the assertions that expected to happen since **/
	private List<SemanticAssertion> post_conditions;
	
	/* constructor */
	/**
	 * create a semantic inference from a set of assertions
	 * to another set of assertions.
	 */
	protected SemanticInference() {
		this.prev_conditions = new LinkedList<SemanticAssertion>();
		this.post_conditions = new LinkedList<SemanticAssertion>();
	}
	
	/* getters */
	/**
	 * get the assertions that are required to hold for the post-conditions to happen
	 * @return
	 */
	public Iterable<SemanticAssertion> get_prev_conditions() { return this.prev_conditions; }
	/**
	 * get the assertions that are expected to happen if the prev-conditions are hold
	 * @return
	 */
	public Iterable<SemanticAssertion> get_post_conditions() { return this.post_conditions; }
	
	/* setters */
	/**
	 * add the prev-condition in the inference if it is not in and link
	 * the prev-condition out to this inference for further linkage.
	 * @param prev_condition
	 * @throws Exception
	 */
	protected void add_prev_condition(SemanticAssertion prev_condition) throws Exception {
		if(prev_condition == null)
			throw new IllegalArgumentException("Invalid prev_condition: null");
		else if(!this.prev_conditions.contains(prev_condition)) {
			this.prev_conditions.add(prev_condition);
			prev_condition.ou.add(this);
		}
	}
	/**
	 * add the post-condition in the inference if it is not in and link
	 * the inference to the post-condition from others in the graph.
	 * @param post_condition
	 * @throws Exception
	 */
	protected void add_post_condition(SemanticAssertion post_condition) throws Exception {
		if(post_condition == null)
			throw new IllegalArgumentException("Invalid post_condition: null");
		else if(!this.post_conditions.contains(post_condition)) {
			this.post_conditions.add(post_condition);
			post_condition.in.add(this);
		}
	}
	
}
