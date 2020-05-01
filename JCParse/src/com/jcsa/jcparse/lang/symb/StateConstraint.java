package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * [statement, expression]
 * @author yukimula
 *
 */
public class StateConstraint {
	
	/* attributes */
	/** execution point **/
	private CirExecution execution;
	/** statement point **/
	private CirStatement statement;
	/** symbolic condition **/
	private SymExpression condition;
	
	/* constructor*/
	/**
	 * create a state constrainst of [statement, condition]
	 * @param statement
	 * @param condition
	 * @throws Exception
	 */
	protected StateConstraint(CirStatement statement, SymExpression condition) throws Exception {
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
	
	/* getters */
	/**
	 * get the execution point to evaluate condition
	 * @return
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * get the statement point to evaluate condition
	 * @return
	 */
	public CirStatement get_statement() { return this.statement; }
	/**
	 * get the condition to be evaluated
	 * @return
	 */
	public SymExpression get_condition() { return this.condition; }
	
	@Override
	public String toString() {
		return execution.toString() + " : " + condition.toString();
	}
	
}
