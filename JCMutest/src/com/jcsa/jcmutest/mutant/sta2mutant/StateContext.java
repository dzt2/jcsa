package com.jcsa.jcmutest.mutant.sta2mutant;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sta2mutant.base.UniAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.UniAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.UniConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.UniSeedMutantState;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * 	It defines the program context that encloses the injected mutation.
 * 	
 * 	@author yukimula
 *
 */
public class StateContext {
	
	/* definitions */
	/** the syntactic mutation being injected **/
	private	Mutant 				mutant;
	/** the syntactic context where the mutation is used **/
	private	AstNode				program_context;
	/** the state of seeding mutation to the coverage **/
	private	UniSeedMutantState	reachability;
	/** the set of state mutations as initial errors **/
	private	List<StateMutation> state_mutations;
	/**
	 * It creates a context to enclose syntactic mutation in a given section
	 * @param mutant		the mutation being injected
	 * @param context		the program context to enclose the mutant
	 * @param execution		the first execution as the reachability point
	 * @throws Exception
	 */
	public StateContext(Mutant mutant, AstNode context, CirExecution execution) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant as null");
		}
		else if(context == null) {
			throw new IllegalArgumentException("Invalid context: null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			this.mutant = mutant; this.program_context = context;
			this.reachability = UniAbstractState.sed_muta(execution.get_statement(), mutant);
			this.state_mutations = new ArrayList<StateMutation>();
		}
	}
	
	/* getters */
	/**
	 * @return the syntactic mutation being injected
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the syntactic context where the mutation is used
	 */
	public AstNode get_program_context() { return this.program_context; }
	/**
	 * @return the state of seeding mutation to the coverage
	 */
	public UniSeedMutantState get_reach_state() { return this.reachability; }
	/**
	 * @return the reachability point
	 */
	public CirExecution get_reach_execution() { return this.reachability.get_execution(); }
	/**
	 * @return the set of state mutations as initial errors
	 */
	public Iterable<StateMutation> get_state_mutations() { return this.state_mutations; }
	/**
	 * @return whether there exists state mutation in the context
	 */
	public boolean has_state_mutations() { return !this.state_mutations.isEmpty(); }
	/**
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	public void add_state_mutation(UniConditionState constraint, UniAbstErrorState init_error) throws Exception {
		this.state_mutations.add(new StateMutation(this, constraint, init_error));
	}
	
}
