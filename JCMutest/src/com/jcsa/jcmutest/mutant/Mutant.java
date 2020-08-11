package com.jcsa.jcmutest.mutant;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;

/**
 * Mutant provides interface to manage the mutations generated on C program.
 * 
 * @author yukimula
 *
 */
public class Mutant {
	
	/* definition */
	/** the space in which the mutant is managed **/
	private MutantSpace space;
	/** the unique ID of the mutant in its space **/
	private int id;
	/** the source mutation under the management **/
	private AstMutation mutation;
	/**
	 * @param space the space in which the mutant is managed
	 * @param id the unique ID of the mutant in its space
	 * @param mutation the source mutation under the management
	 * @throws Exception
	 */
	protected Mutant(MutantSpace space, int id, AstMutation mutation) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("Invalid space: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			this.space = space;
			this.id = id;
			this.mutation = mutation;
		}
	}
	
	/* getters */
	/**
	 * @return the space in which the mutant is managed
	 */
	public MutantSpace get_space() { return this.space; }
	/**
	 * @return the unique ID of the mutant in its space
	 */
	public int get_id() { return this.id; }
	/**
	 * @return the source mutation under the management
	 */
	public AstMutation get_mutation() { return mutation; }
	/**
	 * remove this mutant from the space
	 */
	protected void remove() { 
		this.space = null; 
		this.id = -1; 
		this.mutation = null; 
	}
	
}
