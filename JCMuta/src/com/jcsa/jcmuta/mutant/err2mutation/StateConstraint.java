package com.jcsa.jcmuta.mutant.err2mutation;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * {statement; sym_expression}
 * @author yukimula
 *
 */
public class StateConstraint {
	
	private CirExecution execution;
	private SymExpression condition;
	protected StateConstraint(CirExecution execution, SymExpression condition) throws IllegalArgumentException {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(condition == null)
			throw new IllegalArgumentException("Invalid condition: null");
		else { this.execution = execution; this.condition = condition; }
	}
	
	/**
	 * get the program point where the constraint is evaluated
	 * @return
	 */
	public CirExecution get_execution_point() { return this.execution; }
	/**
	 * get the program point where the constraint is evaluated
	 * @return
	 */
	public CirStatement get_statement_point() { return this.execution.get_statement(); }
	/**
	 * get the constraint being evaluated
	 * @return
	 */
	public SymExpression get_sym_condition() { return this.condition; }
	
}
