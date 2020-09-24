package com.jcsa.jcmutest.mutant.sec2mutant.mut;

import java.util.ArrayList;
import java.util.List;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * The mutation on program state describes of which statements are first
 * and final point being executed and the constraints w.r.t. the initial
 * state errors provided for executing.
 * 
 * @author yukimula
 *
 */
public class StateMutation {
	
	/* definitions */
	/** the mutant that infect errors w.r.t. constraint **/
	private Mutant mutant;
	/** the first execution being executed for reaching **/
	private CirExecution beg_execution;
	/** the final execution being executed for checking **/
	private CirExecution end_execution;
	/** mapping from the initial error to the constraint **/
	private List<StateErrorPair> pairs;
	
	/* constructor */
	/**
	 * create a state mutation w.r.t. the mutant without infection errors
	 * @param mutant
	 * @throws IllegalArgumentException
	 */
	protected StateMutation(Mutant mutant) throws IllegalArgumentException {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.mutant = mutant;
			this.beg_execution = null;
			this.end_execution = null;
			this.pairs = new ArrayList<StateErrorPair>();
		}
	}
	
	/* getters */
	/**
	 * @return the mutant to which the mutation corresponds
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the mutation that causes the state error in infection
	 */
	public AstMutation get_mutation() { return this.mutant.get_mutation(); }
	/**
	 * @return whether there is a point for reaching the faulty statement
	 */
	public boolean has_beg_execution() { return this.beg_execution != null; }
	/**
	 * @return whether there is a point for checking the faulty state
	 */
	public boolean has_end_execution() { return this.end_execution != null; }
	/**
	 * @return the first statement for reaching the faulty statement
	 */
	public CirExecution get_beg_execution() { return this.beg_execution; }
	/**
	 * @return the final statement for checking the faulty statement
	 */
	public CirExecution get_end_execution() { return this.end_execution; }
	/**
	 * @return the pairs of constraint-errors for infecting state
	 */
	public Iterable<StateErrorPair> get_pairs() { return this.pairs; }
	
	/* setters */
	/**
	 * set the first statement for reaching faulty statement
	 * @param execution
	 */
	protected void set_beg_execution(CirExecution execution) {
		this.beg_execution = execution;
	}
	/**
	 * set the final statement for checking faulty statement
	 * @param execution
	 */
	public void set_end_execution(CirExecution execution) {
		this.end_execution = execution;
	}
	/**
	 * add the constraint-error pair to the mutation
	 * @param constraint
	 * @param state_error
	 * @throws IllegalArgumentException
	 */
	protected void add_state_pair(SecConstraint constraint, 
			SecStateError state_error) throws IllegalArgumentException {
		this.pairs.add(new StateErrorPair(constraint, state_error));
	}
	
}
