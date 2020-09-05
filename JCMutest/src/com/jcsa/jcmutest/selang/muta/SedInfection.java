package com.jcsa.jcmutest.selang.muta;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It records the data used to describe the infection process of killing a mutant,
 * including:<br>
 * 	1. <b>source_mutation</b>: the mutation from which the infection is required.<br>
 * 	2. <b>infection_pairs</b>: the pair of constraint and initial state error that
 * 	   is needed for infecting the program state in form of SedDescription.<br>
 * 	3. <b>fault_statement</b>: the statement being seeded with fault in testing.<br>
 *
 * @author yukimula
 */
public class SedInfection {
	
	/* definitions */
	/** the mutant from which the infection is required for killing it **/
	private Mutant source_mutation;
	/** the statement where mutation was seeded and need to be reached **/
	private CirStatement statement;
	/** the constraints that need to be satisfied for infecting states **/
	private List<SedDescription> constraints;
	/** the initial state errors that are caused by executing a faulty
	 *  statement when the corresponding constraints are satisfied. **/
	private List<SedDescription> init_errors;
	
	/* constructor */
	protected SedInfection(Mutant source_mutation) throws Exception {
		if(source_mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			this.source_mutation = source_mutation;
			this.statement = null;
			this.constraints = new ArrayList<SedDescription>();
			this.init_errors = new ArrayList<SedDescription>();
		}
	}
	
	/* getters */
	/**
	 * @return the mutant from which the infection is required for killing it
	 */
	public Mutant get_mutant() { return this.source_mutation; }
	/**
	 * @return the mutation that defines the source mutant.
	 */
	public AstMutation get_mutation() { return this.source_mutation.get_mutation(); }
	/**
	 * @return the statement where mutation was seeded and need to be reached
	 */
	public CirStatement get_statement() { return this.statement; }
	/**
	 * @return the number of infection pairs as [constraint, init_error]*
	 */
	public int number_of_infection_pairs() { return this.constraints.size(); }
	/**
	 * @param k
	 * @return the kth infection pair for this mutant as [constraint, init_error]
	 * @throws IndexOutOfBoundsException
	 */
	public SedDescription[] get_infection_pair(int k) throws IndexOutOfBoundsException {
		return new SedDescription[] { constraints.get(k), init_errors.get(k) };
	}
	
	/* setters */
	/**
	 * set the statement where mutation was seeded and need to be reached
	 * @param statement
	 */
	public void set_statement(CirStatement statement) { this.statement = statement; }
	/**
	 * add a new infection pair 
	 * @param constraint the constraint that needs to be met for causing initial error
	 * @param init_error initial error that needs to be infected for killing this mutant
	 * @throws Exception
	 */
	public void add_infection_pair(
			SedDescription constraint, SedDescription init_error) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint");
		else if(init_error == null)
			throw new IllegalArgumentException("Invalid init_error");
		else {
			this.constraints.add(constraint);
			this.init_errors.add(init_error);
		}
	}
	
}
