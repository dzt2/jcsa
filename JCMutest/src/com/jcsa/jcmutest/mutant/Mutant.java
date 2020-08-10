package com.jcsa.jcmutest.mutant;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;

/**
 * 	The mutant instance used to generate the mutation code.
 * 	
 * 	@author yukimula
 *	
 */
public class Mutant {
	
	/* definitions */
	/** the space of mutations generated **/
	private MutantSpace space;
	/** the integer ID of the mutant in the space **/
	private int id;
	/** the mutation to be performed on source code **/
	private AstMutation mutation;
	/**
	 * @param space the space of mutations generated
	 * @param id the integer ID of the mutant in the space
	 * @param mutation the mutation to be performed on source code
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
	 * @return the space of mutations generated
	 */
	public MutantSpace get_space() { return this.space; }
	/**
	 * @return the integer ID of the mutant in the space
	 */
	public int get_id() { return this.id; }
	/**
	 * @return the mutation to be performed on source code
	 */
	public AstMutation get_mutation() { return this.mutation; }
	
}
