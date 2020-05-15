package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmuta.project.Mutant;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;

/**
 * The infection of state errors for one mutation (or fault) in software under test.
 * <br>
 * 	(1) <code>mutant</code>: source fault that causes error.<br>
 * 	(2) <code>reach_point</code>: the statement where fault is seeded.<br>
 * 	(3) <code>path_condition</code>: the constraints required for covering the faulty statement.
 * 	(4) <code>{constraints : state_error}</code>: mapping from the 
 * @author yukimula
 *
 */
public class MutantInfection {
	
	/* attributes */
	/** the source code mutant that infects the program state **/
	private Mutant mutant;
	/** the faulty statement where the fault is injected **/
	private CirStatement statement;
	/** constraints that need to be satified for covering faulty statement **/
	private StateConstraints path_condition;
	/** mapping from the initial state error to the constraints for infecting them **/
	private Map<StateError, StateConstraints> infections;
	
	/* constructor */
	/**
	 * create a semantic mutation
	 * @param mutant
	 * @throws Exception
	 */
	protected MutantInfection(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.mutant = mutant;
			this.statement = null;
			this.path_condition = null;
			this.infections = new HashMap<StateError, StateConstraints>();
		}
	}
	
	/* getters */
	/**
	 * get the mutant that causing the infection of state errors
	 * @return
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * get the statement where the fault is injected
	 * @return
	 */
	public CirStatement get_faulty_statement() { return this.statement; }
	/**
	 * get the path condition for covering the faulty statement
	 * @return
	 */
	public StateConstraints get_path_condition() { return this.path_condition; }
	/**
	 * get the state errors directly caused by the fault
	 * @return
	 */
	public Iterable<StateError> get_initial_errors() { return this.infections.keySet(); }
	/**
	 * get the constraint required for causing the initial state error
	 * @param error
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateConstraints get_infection_constraint(StateError error) throws IllegalArgumentException {
		if(this.infections.containsKey(error)) return this.infections.get(error);
		else throw new IllegalArgumentException("Undefined: " + error.toString());
	}
	/**
	 * get the mapping from initial state error to the state constraints required to infect.
	 * @return
	 */
	public Map<StateError, StateConstraints> get_infections() { return this.infections; }
	
	/* setters */
	/**
	 * set the reachability location and path condition
	 * @param statement
	 * @param path_condition
	 */
	protected void set_reachability(CirStatement statement, StateConstraints path_condition) {
		this.statement = statement; this.path_condition = path_condition;
	}
	/**
	 * add the infection pair { error : constraint }
	 * @param error
	 * @param constraints
	 */
	protected void add_infection(StateError error, StateConstraints constraints) {
		this.infections.put(error, constraints);
	}
	
}
