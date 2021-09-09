package com.jcsa.jcmutest.mutant.cir2mutant.stat;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It is the basic element in differential state analysis, which is a pair of 
 * the store unit and the value hold in it at some execution point and state.
 * 
 * @author yukimula
 *
 */
public class CirStoreValue {
	
	/* definitions */
	private CirStoreClass		store_type;
	private CirNode				store_unit;
	private CirValueClass		value_type;
	private SymbolExpression	symb_value;
	private CirStoreValue(CirStoreClass store_type, CirNode store_unit,
			CirValueClass value_type, SymbolExpression symb_value) throws Exception {
		if(store_type == null) {
			throw new IllegalArgumentException("Invalid store_type: null");
		}
		else if(store_unit == null) {
			throw new IllegalArgumentException("Invalid store_unit: null");
		}
		else if(value_type == null) {
			throw new IllegalArgumentException("Invalid value_type: null");
		}
		else if(symb_value == null) {
			throw new IllegalArgumentException("Invalid symb_value: null");
		}
		else {
			this.store_type = store_type;
			this.store_unit = store_unit;
			this.value_type = value_type;
			this.symb_value = CirStoreValue.eval(symb_value, null);	/* simplify the symbolic value as holding */
		}
	}
	private static SymbolExpression eval(SymbolExpression symb_value, SymbolProcess context) throws Exception {
		try {
			return symb_value.evaluate(context);
		}
		catch(ArithmeticException ex) {
			return CirValueScope.except_value;
		}
	}
	
	/* getters */
	/**
	 * @return the category of the store unit
	 */
	public CirStoreClass 	get_store_type() { return this.store_type; }
	/**
	 * @return the store unit to preserve value
	 */
	public CirNode			get_store_unit() { return this.store_unit; }
	/**
	 * @return the category of value hold here
	 */
	public CirValueClass	get_value_type() { return this.value_type; }
	/**
	 * @return the symbolic value hold in this store unit
	 */
	public SymbolExpression	get_symb_value() { return this.symb_value; }
	@Override
	public String toString() {
		try {
			String store_type = this.store_type.toString();
			String store_unit = this.store_type.getClass().getSimpleName();
			store_unit = store_unit.substring(3, store_unit.length() - 4);
			store_unit = store_unit + "%" + this.store_unit.get_node_id();
			String value_type = this.value_type.toString();
			String symb_value = this.symb_value.generate_code(true);
			return String.format("%s[%s](%s::%s)", store_type, store_unit, value_type, symb_value);
		}
		catch(Exception ex) {
			return null;
		}
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof CirStoreValue) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	/**
	 * @param context
	 * @return evaluate the value hold in the store unit and return it or exception value if divided by zero
	 * @throws Exception
	 */
	public SymbolExpression evaluate(SymbolProcess context) throws Exception {
		return CirStoreValue.eval(this.symb_value, context);
	}
	
	/* factory */
	/**
	 * @param execution
	 * @param times
	 * @return cond[statement] --> (bool::{execution >= times})
	 * @throws Exception
	 */
	protected static CirStoreValue new_cond(CirExecution execution, int times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(times <= 0) {
			throw new IllegalArgumentException("Invalid times: " + times);
		}
		else {
			return new CirStoreValue(CirStoreClass.cond, execution.get_statement(),
					CirValueClass.bool, SymbolFactory.greater_eq(execution, times));
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @param value
	 * @return cond[statement] --> (bool::{condition as value})
	 * @throws Exception
	 */
	protected static CirStoreValue new_cond(CirExecution execution, Object condition, boolean value) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirStoreValue(CirStoreClass.cond, execution.get_statement(),
					CirValueClass.bool, SymbolFactory.sym_condition(condition, value));
		}
	}
	/**
	 * @param expression
	 * @param value
	 * @return expr|refr[expression] --> (type::value)
	 * @throws Exception
	 */
	protected static CirStoreValue new_expr(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as: null");
		}
		else {
			/* declarations */
			CirStoreClass store_type; CirNode store_unit;
			CirValueClass value_type; SymbolExpression symb_value;
			
			/* determine the store unit along with its category */
			if(CirMutations.is_assigned(expression)) {
				store_type = CirStoreClass.refr;
				store_unit = expression;
			}
			else {
				store_type = CirStoreClass.expr;
				store_unit = expression;
			}
			
			/* determine the value hold along with its category */
			if(CirMutations.is_boolean(expression)) {
				value_type = CirValueClass.bool;
				symb_value = SymbolFactory.sym_condition(value, true);
			}
			else {
				symb_value = SymbolFactory.sym_expression(value);
				if(CirMutations.is_usigned(expression)) {
					value_type = CirValueClass.usig;
				}
				else if(CirMutations.is_integer(expression)) {
					value_type = CirValueClass.sign;
				}
				else if(CirMutations.is_doubles(expression)) {
					value_type = CirValueClass.real;
				}
				else if(CirMutations.is_address(expression)) {
					value_type = CirValueClass.addr;
				}
				else {
					value_type = CirValueClass.auto;
				}
			}
			
			/* generate the data store unit for analysis */
			return new CirStoreValue(store_type, store_unit, value_type, symb_value);
		}
	}
	/**
	 * @param execution
	 * @param do_or_not
	 * @return stmt[statement] --> (bool::{do_or_not})
	 * @throws Exception
	 */
	protected static CirStoreValue new_stmt(CirExecution execution, boolean do_or_not) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirStoreValue(CirStoreClass.stmt, execution.get_statement(),
					CirValueClass.bool, SymbolFactory.sym_constant(do_or_not));
		}
	}
	/**
	 * @param execution
	 * @return stmt[statement] --> (bool::exception)
	 * @throws Exception
	 */
	protected static CirStoreValue new_trap(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			execution = execution.get_graph().get_exit();
			return new CirStoreValue(CirStoreClass.stmt, execution.get_statement(),
					CirValueClass.bool, CirValueScope.except_value);
		}
	}
	
}
