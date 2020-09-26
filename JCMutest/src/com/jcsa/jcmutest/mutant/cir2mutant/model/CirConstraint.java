package com.jcsa.jcmutest.mutant.cir2mutant.model;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

/**
 * The constraint defines a condition that needs to be satisfied at some point
 * of the program during testing.
 * 
 * @author yukimula
 *
 */
public class CirConstraint {
	
	/* definitions */
	/** the statement where the condition is asserted **/
	private CirExecution execution;
	/** the condition that will be asserted in testing **/
	private SymExpression condition;
	/**
	 * create a constraint that asserts the condition at the specified statement.
	 * @param statement the statement where the condition will be asserted
	 * @param condition the condition that will be asserted during testing
	 * @throws Exception
	 */
	protected CirConstraint(CirStatement statement, 
			SymExpression condition) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(condition == null)
			throw new IllegalArgumentException("Invalid condition: null");
		else {
			this.execution = statement.get_tree().get_localizer().get_execution(statement);
			this.condition = condition;
		}
	}
	
	/* getters */
	/**
	 * @return the statement where the condition will be asserted
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the statement where the condition will be asserted
	 */
	public CirStatement get_statement() { return this.execution.get_statement(); }
	/**
	 * @return the condition that will be asserted during testing
	 */
	public SymExpression get_condition() { return this.condition; }
	/**
	 * @return code that describes the constraint under test
	 * @throws Exception
	 */
	protected String generate_code() throws Exception {
		return this.execution + "::(" + this.condition.generate_code() + ")";
	}
	@Override
	public String toString() {
		try {
			return this.generate_code();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof CirConstraint)
			return this.toString().equals(obj.toString());
		else
			return false;
	}
	
}
