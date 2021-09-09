package com.jcsa.jcmutest.mutant.cir2mutant.stat;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It maintains the values of each store unit at a given execution point.
 * 
 * @author yukimula
 *
 */
public class CirStateData {
	
	/* definitions */
	private CirExecution 						execution;
	private Collection<CirStoreValue> 			conditions;
	private Map<CirStatement, CirStoreValue> 	stmt_values;
	private Map<CirExpression, CirStoreValue>	expr_values;
	protected CirStateData(CirExecution execution) throws IllegalArgumentException {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution as null");
		}
		else {
			this.execution = execution;
			this.conditions = new HashSet<CirStoreValue>();
			this.stmt_values = new HashMap<CirStatement, CirStoreValue>();
			this.expr_values = new HashMap<CirExpression,CirStoreValue>();
		}
	}
	
	/* getters */
	/** 
	 * @return the execution point where the data state is maintained
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the pred_conditions to ensure the generation of state
	 */
	public Iterable<CirStoreValue> get_conditions() { return this.conditions; }
	public Iterable<CirStatement> get_stmt_units() { return this.stmt_values.keySet(); }
	public Iterable<CirStoreValue> get_stmt_values() { return this.stmt_values.values(); }
	public boolean has_stmt_value(CirStatement statement) {
		return this.stmt_values.containsKey(statement);
	}
	public CirStoreValue get_stmt_value(CirStatement statement) {
		if(this.stmt_values.containsKey(statement)) {
			return this.stmt_values.get(statement);
		}
		else {
			return null;
		}
	}
	public Iterable<CirExpression> get_expr_units() { return this.expr_values.keySet(); }
	public Iterable<CirStoreValue> get_expr_values() { return this.expr_values.values(); }
	public boolean has_expr_value(CirExpression expression) {
		return this.expr_values.containsKey(expression);
	}
	public CirStoreValue get_expr_value(CirExpression expression) {
		return this.expr_values.get(expression);
	}
	
	/* setters */
	/**
	 * add either the path condition or data store value into the state
	 * @param store_value
	 * @throws Exception
	 */
	protected void add_store_value(CirStoreValue store_value) throws Exception {
		if(store_value == null) {
			throw new IllegalArgumentException("Invalid store_value: null");
		}
		else if(store_value.get_store_type() == CirStoreClass.cond) {
			this.conditions.add(store_value);
		}
		else if(store_value.get_store_type() == CirStoreClass.stmt) {
			this.stmt_values.put((CirStatement) store_value.get_store_unit(), store_value);
		}
		else {
			this.expr_values.put((CirExpression) store_value.get_store_unit(), store_value);
		}
	}
	
}
