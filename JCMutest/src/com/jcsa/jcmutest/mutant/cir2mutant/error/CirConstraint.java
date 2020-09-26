package com.jcsa.jcmutest.mutant.cir2mutant.error;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.test.state.CStateContexts;

/**
 * The constraint required to be satisfied when C-intermediate code is executed
 * at some point of program, denoted as <code>(statement, condition)</code>, in 
 * which <code>statement</code> refers to the point where constraint evaluated,
 * while <code>condition</code> defines the boolean expression being asserted at
 * the point of the <code>statement</code>.<br>
 * 
 * @author yukimula
 *
 */
public class CirConstraint {
	
	/* definitions */
	/** where the condition is asserted **/
	private CirStatement statement;
	/** the condition being asserted to determine 
	 *  whether the constraint is satisfied **/
	private SymExpression condition;
	/**
	 * create a constraint that evaluates the condition at the statement 
	 * @param statement where the condition is asserted
	 * @param condition the condition being asserted to determine 
	 *  	  whether the constraint is satisfied 
	 * @throws IllegalArgumentException
	 */
	private CirConstraint(CirStatement statement, SymExpression condition) throws IllegalArgumentException {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(condition == null)
			throw new IllegalArgumentException("Invalid condition: null");
		else {
			this.statement = statement;
			this.condition = condition;
		}
	}
	
	/* getters */
	/**
	 * @return where the condition is asserted
	 */
	public CirStatement get_statement() { return this.statement; }
	/**
	 * @return the condition being asserted to determine 
	 *  	   whether the constraint is satisfied
	 */
	public SymExpression get_condition() { return this.condition; }
	/**
	 * @param contexts
	 * @return the condition being evaluated under the given contexts
	 * @throws Exception
	 */
	public SymExpression get_condition(CStateContexts contexts) throws Exception {
		return SymEvaluator.evaluate_on(this.condition, contexts);
	}
	/**
	 * @return code that describes the constraint 
	 * @throws Exception
	 */
	protected String generate_code() throws Exception {
		CirExecution execution = statement.get_tree().
				get_localizer().get_execution(statement);
		return execution + "::(" + this.condition.generate_code() + ")";
	}
	@Override
	public String toString() {
		try {
			return this.generate_code();
		} catch (Exception e) {
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
			return obj.toString().equals(this.toString());
		else
			return false;
	}
	
	/* creator */
	/**
	 * @param statement
	 * @param expression
	 * @param value
	 * @return a constraint that evaluates condition as expression == value
	 * 		   at the point of the specified statement.
	 * @throws Exception
	 */
	protected static CirConstraint new_constraint(CirStatement statement,
				Object expression, boolean value) throws Exception {
		SymExpression condition = SymFactory.parse(expression);
		CType type = CTypeAnalyzer.get_value_type(condition.get_data_type());
		if(CTypeAnalyzer.is_boolean(type)) {
			if(value) { }
			else {
				condition = SymFactory.logic_not(condition);
			}
		}
		else if(CTypeAnalyzer.is_integer(type)
				|| CTypeAnalyzer.is_real(type)
				|| CTypeAnalyzer.is_pointer(type)) {
			if(value) {
				condition = SymFactory.not_equals(condition, Integer.valueOf(0));
			}
			else {
				condition = SymFactory.equal_with(condition, Integer.valueOf(0));
			}
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
		return new CirConstraint(statement, condition);
	}
	
}
