package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcparse.lang.program.AstCirNode;

/**
 * 	It combines every necessary mutations into the instance of one AstMutation.
 * 	
 * 	@author yukimula
 *
 */
public class AstContextMutation {
	
	/** the mutation as the AST-based source from program **/
	private	Mutant			ast_mutant;
	/** the mutated location as the source representative **/
	private	SedMutaMutation	m_location;
	/** the reach-ability condition for covering mutation **/
	private	CovTimeMutation r_condition;
	/** the maps from initial state mutation to condition **/
	private	Map<ContextMutation, ContextMutation> infection_map;
	/**
	 * @param mutant		the mutation as the AST-based source from program
	 * @param location		the mutated location as the source representative
	 * @throws Exception
	 */
	protected AstContextMutation(Mutant mutant, AstCirNode location) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {
			this.ast_mutant = mutant;
			this.m_location = ContextMutation.sed_muta(location, mutant);
			while(!location.is_statement_node()) { location = location.get_parent(); }
			this.r_condition = ContextMutation.cov_time(location, 1, Integer.MAX_VALUE);
			this.infection_map = new HashMap<ContextMutation, ContextMutation>();
		}
	}
	
	/* getters */
	/**
	 * @return the ast-source mutation from this instance is parsed
	 */
	public	Mutant			get_ast_mutant() { return this.ast_mutant; }
	/**
	 * @return the location where this mutation is embedded directly
	 */
	public	AstCirNode		get_ast_location()	{ return this.m_location.get_location(); }
	/**
	 * @return the source mutation state from the location generate
	 */
	public	SedMutaMutation	get_source_mutation() { return this.m_location; }
	/**
	 * @return the reach-ability condition for covering mutation
	 */
	public	CovTimeMutation	get_reach_condition() { return this.r_condition; }
	/**
	 * @return	the maps from initial state mutation to condition
	 */
	public 	Map<ContextMutation, ContextMutation> get_infection_map() { return this.infection_map; }
	/**
	 * It puts the infection-condition-error pairs to the maps
	 * @param condition
	 * @param init_error
	 * @throws Exception
	 */
	public	void	put_infection(ContextMutation condition, ContextMutation init_error) throws Exception {
		if(condition == null || !condition.is_conditional()) {
			throw new IllegalArgumentException("Invalid condition: " + condition);
		}
		else if(init_error == null || !init_error.is_abst_error()) {
			throw new IllegalArgumentException("Invalid abst_error: " + init_error);
		}
		else { this.infection_map.put(init_error, condition); }
	}
	
}
