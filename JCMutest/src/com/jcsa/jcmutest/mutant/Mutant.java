package com.jcsa.jcmutest.mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.mutant.mutation.MutaGroup;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * Mutant is an object that is managed in the space,
 * directly used for mutation testing.
 * 
 * @author yukimula
 *
 */
public class Mutant {
	
	/* definition */
	/** the space in which the mutant is managed **/
	private MutantSpace space;
	/** the unique identifier of the mutant in the space **/
	private int id;
	/** coverage, weak and strong mutation of the mutant **/
	private AstMutation[] mutations;
	/**
	 * @param space the space in which the mutant is managed
	 * @param id the unique identifier of the mutant in the space
	 * @param mutations coverage, weak and strong mutation of the mutant
	 * @throws Exception
	 */
	protected Mutant(MutantSpace space, int id,
			AstMutation[] mutations) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("Invalid space: null");
		else if(mutations == null || mutations.length != 3)
			throw new IllegalArgumentException("Invalid mutations: null");
		else {
			this.space = space;
			this.id = id;
			this.mutations = mutations;
		}
	}
	
	/* getters */
	/**
	 * @return the space in which the mutant is created
	 */
	public MutantSpace get_space() { return this.space; }
	/**
	 * @return the unique identifier of the mutant in the space
	 */
	public int get_id() { return this.id; }
	/**
	 * @return coverage mutation of the mutant
	 */
	public AstMutation get_coverage_mutation() { return mutations[0]; }
	/**
	 * @return weak mutation of the mutant
	 */
	public AstMutation get_weak_mutation() { return mutations[1]; }
	/**
	 * @return strong mutation of the mutant
	 */
	public AstMutation get_strong_mutation() { return mutations[2]; }
	/**
	 * delete this mutant from the space
	 */
	protected void delete() {
		this.space = null;
		this.id = -1;
		this.mutations = null;
	}
	
	/* implicator */
	/**
	 * @return the representative mutation of this mutant
	 */
	public AstMutation get_mutation() { return this.get_strong_mutation(); }
	/**
	 * @return the group of the representative mutation
	 */
	public MutaGroup get_muta_group() { return this.get_strong_mutation().get_group(); }
	/**
	 * @return the class of the representative mutation
	 */
	public MutaClass get_muta_class() { return this.get_strong_mutation().get_class(); }
	/**
	 * @return the operator of the representative mutation
	 */
	public MutaOperator get_muta_operator() { return this.get_strong_mutation().get_operator(); }
	/**
	 * @return the location of the representative mutation
	 */
	public AstNode get_location() { return this.get_strong_mutation().get_location(); }
	/**
	 * @return the parameter of the representative mutation
	 */
	public Object get_parameter() { return this.get_strong_mutation().get_parameter(); }
	
	
	
	
	
}
