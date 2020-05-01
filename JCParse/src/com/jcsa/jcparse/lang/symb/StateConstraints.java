package com.jcsa.jcparse.lang.symb;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class StateConstraints {
	
	/** whether it is conjunct or disjunct **/
	private boolean conjunct;
	/** set of state constraints being used **/
	private List<StateConstraint> constraints;
	
	/**
	 * create an empty constraint set with conjunction set
	 * @param conjunct
	 */
	public StateConstraints(boolean conjunct) {
		this.conjunct = conjunct;
		this.constraints = new ArrayList<StateConstraint>();
	}
	
	/**
	 * whether the constraints is conjunction
	 * @return
	 */
	public boolean is_conjunct() { return this.conjunct; }
	/**
	 * whether the constraints is disjunction
	 * @return
	 */
	public boolean is_disjunct() { return !this.conjunct; }
	/**
	 * get the number of state constraints in
	 * @return
	 */
	public int size() { return this.constraints.size(); }
	/**
	 * get the state constraints in the set
	 * @return
	 */
	public Iterable<StateConstraint> get_constraints() { return this.constraints; }
	/**
	 * add the state constraint in the set
	 * @param statement
	 * @param condition
	 * @throws Exception
	 */
	public void add_constraint(CirStatement statement, SymExpression condition) throws Exception {
		this.constraints.add(new StateConstraint(statement, condition));
	}
	
}
