package com.jcsa.jcmutest.selang.muta;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedMutation {
	
	/* definitions */
	/** the mutation from which this node is parsed **/
	private Mutant mutant;
	/** the faulty statement where mutation is seeded **/
	private CirStatement statement;
	/** constraints required for infecting program state **/
	private List<SedDescription> constraints;
	/** the initial state error caused by the mutation **/
	private List<SedDescription> init_errors;
	/**
	 * create an empty sed-mutation without infection links
	 * @param mutant
	 * @param statement
	 * @throws Exception
	 */
	protected SedMutation(Mutant mutant, CirStatement statement) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else if(statement == null)
			throw new IllegalArgumentException("Invalid statement");
		else {
			this.mutant = mutant;
			this.statement = statement;
			this.constraints = new ArrayList<SedDescription>();
			this.init_errors = new ArrayList<SedDescription>();
		}
	}
	
	/* getters */
	/**
	 * @return the mutation from which this node is parsed
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the faulty statement where mutation is seeded
	 */
	public CirStatement get_statement() { return this.statement; }
	/**
	 * @param k
	 * @return {constraint, state_error}
	 * @throws Exception
	 */
	public SedDescription[] get_infection(int k) throws Exception {
		return new SedDescription[] {
			this.constraints.get(k), this.init_errors.get(k)
		};
	}
	/**
	 * @return the number of constraint-error pair
	 */
	public int number_of_infections() { return this.constraints.size(); }
	/**
	 * add infection-pair in the space
	 * @param constraint
	 * @param error
	 * @throws Exception
	 */
	public void add_infection(SedDescription constraint, SedDescription error) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else if(error == null)
			throw new IllegalArgumentException("Invalid initial state error");
		else {
			this.constraints.add(constraint);
			this.init_errors.add(error);
		}
	}
	
}
