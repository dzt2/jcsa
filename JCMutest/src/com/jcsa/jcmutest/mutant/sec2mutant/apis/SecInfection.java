package com.jcsa.jcmutest.mutant.sec2mutant.apis;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecInfection {
	
	/* attributes */
	/** the mutant on which the infection is needed **/
	private Mutant mutant;
	/** the statement that is executed iff. the mutant is reached **/
	protected CirStatement statement;
	/** constraints required for infecting the program state **/
	private List<SecDescription> constraints;
	/** the initial state errors caused when constraints are met **/
	private List<SecDescription> init_errors;
	
	/* constructor */
	/**
	 * create an empty infection instance for the mutation
	 * @param mutant
	 * @throws Exception
	 */
	protected SecInfection(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.mutant = mutant;
			this.statement = null;
			this.constraints = new LinkedList<SecDescription>();
			this.init_errors = new LinkedList<SecDescription>();
		}
	}
	
	/* getters */
	/**
	 * @return the mutation on which the infection is required for killing it
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the mutation on which the infection is required for killing it
	 */
	public AstMutation get_mutation() { return this.mutant.get_mutation(); }
	/**
	 * @return whether the statement where the fault is seeded exists
	 */
	public boolean has_statement() { return this.statement != null; }
	/**
	 * @return the statement where the fault is injected
	 */
	public CirStatement get_statement() { return this.statement; }
	/**
	 * @return the number of the infection pairs in the module
	 */
	public int number_of_infection_pairs() { return this.constraints.size(); }
	/**
	 * @param k
	 * @return the kth infection pair as {constraint, state_error}
	 * @throws IndexOutOfBoundsException
	 */
	public SecDescription[] get_infection_pair(int k) throws IndexOutOfBoundsException {
		return new SecDescription[] { constraints.get(k), init_errors.get(k) };
	}
	/**
	 * @return whether the number of infection pairs are non-zeros
	 */
	public boolean has_infection_pairs() { return !this.constraints.isEmpty(); }
	/**
	 * add a infection-pair [constraint, init_error] in the module
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	protected void add_infection_pair(SecDescription constraint,
			SecDescription init_error) throws Exception {
		if(!SecFactory.is_constraint(constraint))
			throw new IllegalArgumentException(constraint.generate_code());
		else if(!SecFactory.is_state_error(init_error))
			throw new IllegalArgumentException(init_error.generate_code());
		else {
			this.constraints.add(constraint);
			this.init_errors.add(init_error);
		}
	}
	
}
