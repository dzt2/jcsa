package com.jcsa.jcparse.test.state;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;


/**
 * Each node records the value hold by expressions after a statement being executed.
 * 
 * @author yukimula
 *
 */
public class CStateNode {
	
	/* definitions */
	/** the path in which the node is created **/
	protected CStatePath path;
	/** the index of the state node in the path **/
	protected int index;
	/** the execution of statement before which the state is recorded **/
	private CirExecution execution;
	/** the units record the value hold by the expressions after the
	 * 	statement being executed during testing in the statement. **/
	private List<CStateUnit> units;
	
	/* constructor */
	/**
	 * create an isolated node in the path of the state transition.
	 * @param path
	 * @param index
	 * @param execution
	 * @throws Exception
	 */
	protected CStateNode(CirExecution execution) throws IllegalArgumentException {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution");
		else {
			this.path = null;
			this.index = -1;
			this.execution = execution;
			this.units = new ArrayList<CStateUnit>();
		}
	}
	
	/* getters */
	/**
	 * @return the node is isolated when it has not been added in the path
	 */
	public boolean is_isolated() { return this.path == null; }
	/**
	 * @return the path of state transition where the node is created
	 */
	public CStatePath get_path() { return this.path; }
	/**
	 * @return the index of the node in its path
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the execution of the statement of which state is recorded
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the statement in which the state is evaluated
	 */
	public CirStatement get_statement() { return this.execution.get_statement(); }
	/**
	 * @return the state of the expressions being evaluated
	 */
	public Iterable<CStateUnit> get_units() { return this.units; }
	/**
	 * @param expression
	 * @return whether there is state w.r.t. the expression in the statement
	 */
	public boolean has_unit(CirExpression expression) {
		for(CStateUnit unit : this.units) {
			if(unit.get_expression() == expression) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @param expression
	 * @return the state of the expression in the statement being executed
	 */
	public CStateUnit get_unit(CirExpression expression) {
		for(CStateUnit unit : this.units) {
			if(unit.get_expression() == expression) {
				return unit;
			}
		}
		return null;
	}
	/**
	 * set the value of the expression being evaluated in the statement
	 * @param expression
	 * @param value
	 * @throws Exception
	 */
	public void set_unit(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() != this.get_statement())
			throw new IllegalArgumentException("Invalid expression: " + expression);
		else {
			/* 1. obtain the element w.r.t. expression */
			CStateUnit element = null;
			for(CStateUnit unit : this.units) {
				if(unit.get_expression() == expression) {
					element = unit;
					break;
				}
			}
			if(element == null) {
				element = new CStateUnit(this, expression, null);
				this.units.add(element);
			}
			
			/* 2. generate the symbolic value of value */
			SymExpression sym_value;
			if(value instanceof Boolean || value instanceof Character
				|| value instanceof Short || value instanceof Integer
				|| value instanceof Long || value instanceof Float
				|| value instanceof Double) {
				sym_value = SymFactory.new_constant(value);
			}
			else {
				sym_value = SymFactory.parse(expression);
			}
			
			/* 3. record value */ element.set_value(sym_value);
		}
	}
	/**
	 * @return the node previous to this node or null;
	 */
	public CStateNode get_prev_node() {
		if(this.path == null) {
			return null;
		}
		else if(index > 0) {
			return this.path.get_node(this.index - 1);
		}
		else {
			return null;
		}
	}
	/**
	 * @return the node next to this node or null
	 */
	public CStateNode get_next_node() {
		if(this.path == null) {
			return null;
		}
		else if(index < this.path.size() - 1) {
			return this.path.get_node(this.index + 1);
		}
		else {
			return null;
		}
	}
	
}
