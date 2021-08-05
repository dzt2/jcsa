package com.jcsa.jcparse.test.inst;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * The instrumental node refers to a statement being executed during testing,
 * of which expressions are instrumented and recorded with their values.
 *
 * @author yukimula
 *
 */
public class InstrumentalNode {

	/* definitions */
	/** the execution of statement **/
	private CirExecution execution;
	/** units of expressions under the statement being instrumented **/
	private List<InstrumentalUnit> units;
	/**
	 * create an empty instrumental node w.r.t. the execution of statement
	 * @param execution
	 * @throws Exception
	 */
	protected InstrumentalNode(CirExecution execution) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else {
			this.execution = execution;
			this.units = new ArrayList<>();
		}
	}

	/* getters */
	/**
	 * @return the execution of statement being executed in testing
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the statement being executed in testing
	 */
	public CirStatement get_statement() { return this.execution.get_statement(); }
	/**
	 * @return the set of instrumental units w.r.t. the expressions in the node
	 */
	public Iterable<InstrumentalUnit> get_units() { return this.units; }
	/**
	 * @param expression
	 * @return whether there is value recorded to the expression in statement
	 */
	public boolean has_unit(CirExpression expression) {
		for(InstrumentalUnit unit : this.units) {
			if(unit.get_expression() == expression) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @param expression
	 * @return the instrumental unit w.r.t. the expression under the statement
	 */
	public InstrumentalUnit get_unit(CirExpression expression) {
		for(InstrumentalUnit unit : this.units) {
			if(unit.get_expression() == expression) {
				return unit;
			}
		}
		return null;
	}
	/**
	 * set the value of the expression under the statement to update its unit
	 * @param expression
	 * @param value
	 * @throws Exception
	 */
	protected void set_unit(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() != this.get_statement())
			throw new IllegalArgumentException("Invalid expression: " + expression);
		else {
			/* find the existing unit w.r.t. the expression */
			InstrumentalUnit element = null;
			for(InstrumentalUnit unit : this.units) {
				if(unit.get_expression() == expression) {
					element = unit;
					break;
				}
			}

			/* add the new instrumental unit to the node */
			if(element == null) {
				element = new InstrumentalUnit(expression, value);
				this.units.add(element);
			}
			/* update the value hold by the unit of node */
			else {
				element.set_value(value);
			}
		}
	}

}
