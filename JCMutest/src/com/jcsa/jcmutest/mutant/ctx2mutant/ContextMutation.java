package com.jcsa.jcmutest.mutant.ctx2mutant;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstAbstErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstConditionState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstCoverTimesState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstSeedMutantState;
import com.jcsa.jcparse.lang.program.AstCirNode;

/**
 * 	It specifies a context-mutation at the original point using AstContextState.
 * 	
 * 	@author yukimula
 *
 */
public class ContextMutation {
	
	/* definitions */
	/** the source mutation **/
	private	Mutant mutant;
	/** the coverage state for reachability **/
	private	AstCoverTimesState	c_state;
	/** the state directly seeding mutation **/
	private AstSeedMutantState	s_state;
	/** the conditional state for infection **/
	private	List<AstConditionState>	i_states;
	/** abstract error state to propagation **/
	private	List<AstAbstErrorState>	p_states;
	
	/* getters */
	/**
	 * @return	the source syntactic mutant in the source code
	 */
	public	Mutant		get_mutant()	{ return this.mutant; }
	/**
	 * @return	the location where the mutation is directly seeded
	 */
	public	AstCirNode	get_location()	{ return this.s_state.get_location(); }
	/**
	 * @return 	the statement where the mutation is enclosed with
	 */
	public	AstCirNode	get_statement()	{ return this.c_state.get_location(); }
	/**
	 * @return	the condition for reaching the mutated location
	 */
	public	AstCoverTimesState	get_coverage_state()	{ return this.c_state; }
	/**
	 * @return	the state to localize the local position of defect
	 */
	public	AstSeedMutantState	get_mutation_state()	{ return this.s_state; }
	/**
	 * @return	the number of pairs of infection and initial error
	 */
	public	int	number_of_infection_pairs() { return this.i_states.size(); }
	/**
	 * @return 	the state infection condition
	 */
	public	AstConditionState	get_infection_state(int k) throws IndexOutOfBoundsException	{ return this.i_states.get(k); }
	/**
	 * @return	the initial error state for propagation
	 */
	public	AstAbstErrorState	get_ini_error_state(int k) throws IndexOutOfBoundsException	{ return this.p_states.get(k); }
	/**
	 * It appends a new infection-pair of condition and initial error state
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	public 	void	put_infection_error(AstConditionState constraint, AstAbstErrorState init_error) throws Exception {
		if(constraint == null) {
			throw new IllegalArgumentException("Invalid constraint as null");
		}
		else if(init_error == null) {
			throw new IllegalArgumentException("Invalid init_error as null");
		}
		else { this.i_states.add(constraint); this.p_states.add(init_error); }
	}
	
	/* constructor */
	/**
	 * It creates a new ContextMutation for modeling
	 * @param mutant
	 * @param location
	 * @throws Exception
	 */
	public ContextMutation(Mutant mutant, AstCirNode location) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {
			this.mutant = mutant;
			this.c_state = AstContextState.cov_time(location.statement_of(), 1, Integer.MAX_VALUE);
			this.s_state = AstContextState.sed_muta(location, mutant);
			this.i_states = new ArrayList<AstConditionState>();
			this.p_states = new ArrayList<AstAbstErrorState>();
		}
	}
	
}
