package com.jcsa.jcmuta.mutant.error2mutation;

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
	private CirStatement statement;
	private SymExpression condition;
	protected StateConstraint(CirStatement statement, SymExpression condition) throws IllegalArgumentException {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(condition == null)
			throw new IllegalArgumentException("Invalid condition: null");
		else { 
			this.statement = statement; this.condition = condition; 
			this.execution = statement.get_tree().get_function_call_graph().
				get_function(statement).get_flow_graph().get_execution(statement);
		}
	}
	
	public CirExecution get_execution_point() { return this.execution; }
	/**
	 * get the program point where the constraint is evaluated
	 * @return
	 */
	public CirStatement get_statement_point() { return this.statement; }
	/**
	 * get the constraint being evaluated
	 * @return
	 */
	public SymExpression get_sym_condition() { return this.condition; }
	
}
