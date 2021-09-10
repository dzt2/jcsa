package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It denotes the store-value pair to preserve the value of some store unit in
 * the execution state of the program point at some time.
 * 
 * @author yukimula
 *
 */
public class CirStoreValue {
	
	/* attributes */
	/** the type of the store units to preserve **/
	private CirStoreClass 			store_type;
	/** the location to describe the store unit **/
	private CirNode					store_unit;
	/** the type of the value hold by the store **/
	private CirValueClass			value_type;
	/** the symbolic value holded and evaluated **/
	private SymbolExpression		symb_value;
	/** the list of concrete values be evaluate **/
	private List<SymbolExpression>	con_values;
	
	/* constructor */
	/**
	 * @param store_type
	 * @param store_unit
	 * @param value_type
	 * @param symb_value
	 * @throws Exception
	 */
	private CirStoreValue(CirStoreClass store_type, CirNode store_unit,
			CirValueClass value_type, SymbolExpression symb_value) throws Exception {
		if(store_type == null) {
			throw new IllegalArgumentException("Invalid store_type.");
		}
		else if(store_unit == null) {
			throw new IllegalArgumentException("Invalid store_unit.");
		}
		else if(value_type == null) {
			throw new IllegalArgumentException("Invalid value_type.");
		}
		else if(symb_value == null) {
			throw new IllegalArgumentException("Invalid symb_value.");
		}
		else {
			this.store_type = store_type;
			this.store_unit = store_unit;
			this.value_type = value_type;
			this.symb_value = this.eval(symb_value, null);
			this.con_values = new ArrayList<SymbolExpression>();
		}
	}
	/**
	 * @param expression
	 * @param context
	 * @return the evaluation result of expression using context or exception if divided by zero.
	 * @throws Exception
	 */
	private SymbolExpression eval(SymbolExpression expression, SymbolProcess context) throws Exception {
		try {
			return expression.evaluate(context);
		}
		catch(ArithmeticException ex) {
			return CirValueScope.expt_value;
		}
	}
	
	/* getters */
	/**
	 * @return the type of the store units to preserve
	 */
	public CirStoreClass	get_store_type() { return this.store_type; }
	/**
	 * @return the location to describe the store unit
	 */
	public CirNode			get_store_unit() { return this.store_unit; }
	/**
	 * @return the type of the value hold by the store
	 */
	public CirValueClass	get_value_type() { return this.value_type; }
	/**
	 * @return the symbolic value holded and evaluated
	 */
	public SymbolExpression	get_symb_value() { return this.symb_value; }
	/**
	 * @return the list of concrete values be evaluate
	 */
	public Iterable<SymbolExpression> get_conc_values() { return this.con_values; }
	
	/* element */
	/**
	 * the key of the store unit defined
	 * @return [store_class::store_unit_id]
	 */
	public String get_key_string() {
		return String.format("[%s::%d]", 
				this.get_store_type().toString(), 
				this.get_store_unit().get_node_id());
	}
	/**
	 * @return (type::value)
	 */
	public String get_val_string() {
		String value;
		try {
			value = this.get_symb_value().generate_code(true);
		}
		catch(Exception ex) {
			ex.printStackTrace(System.err);
			value = null;
		}
		return String.format("(%s::%s)", this.get_value_type().toString(), value);
	}
	@Override
	public String toString() { 
		return this.get_key_string() + " --> " + this.get_val_string();
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
	
	/* factory */
	/**
	 * It creates the store unit preserving logical formula for covering 
	 * execution with specified times.
	 * 
	 * @param execution
	 * @param times
	 * @return [cond::statement] --> (bool::{execution >= times}) 
	 * @throws Exception
	 */
	protected static CirStoreValue new_cond(CirExecution execution, int times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(times <= 0) {
			throw new IllegalArgumentException("Invalid times: " + times);
		}
		else {
			return new CirStoreValue(CirStoreClass.cond,
					execution.get_statement(), CirValueClass.bool,
					SymbolFactory.greater_eq(execution, times));
		}
	}
	/**
	 * It creates the store unit preserving logical formula of specified condition as value
	 * @param execution
	 * @param condition
	 * @param value
	 * @return [cond::statement] --> (bool::{condition as value}) 
	 * @throws Exception
	 */
	protected static CirStoreValue new_cond(CirExecution execution, Object condition, boolean value) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirStoreValue(CirStoreClass.cond,
					execution.get_statement(), CirValueClass.bool,
					SymbolFactory.sym_condition(condition, value));
		}
	}
	/**
	 * It creates the store unit preserving the data value for specified expression or reference.
	 * @param expression
	 * @param value
	 * @return [expr|vars::expression] --> (type::value)
	 * @throws Exception
	 */
	protected static CirStoreValue new_data(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as: null");
		}
		else {
			/* 1. declarations of its attributes */
			CirStoreClass store_type; CirExpression store_unit;
			CirValueClass value_type; SymbolExpression symb_value;
			
			/* 2. determine store unit [class:node] */
			if(CirMutations.is_assigned(expression)) {
				store_type = CirStoreClass.vars;
			}
			else {
				store_type = CirStoreClass.expr;
			}
			store_unit = expression;
			
			/* 3. determine symbolic value (type::value) */
			symb_value = SymbolFactory.sym_expression(value);
			if(CirMutations.is_void(expression)) {
				value_type = CirValueClass.none;
			}
			else if(CirMutations.is_boolean(expression)) {
				value_type = CirValueClass.bool;
				symb_value = SymbolFactory.sym_condition(symb_value, true);
			}
			else if(CirMutations.is_usigned(expression)) {
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
			
			/* 4. return the store-value pair */
			return new CirStoreValue(store_type, store_unit, value_type, symb_value);
		}
	}
	/**
	 * @param execution
	 * @param do_or_not
	 * @return [stmt:statement] --> (bool:do_or_not)
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
	 * @return [stmt:statement] --> (bool:expt_value)
	 * @throws Exception
	 */
	protected static CirStoreValue new_trap(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			execution = execution.get_graph().get_exit();
			return new CirStoreValue(CirStoreClass.stmt, execution.get_statement(),
					CirValueClass.bool, CirValueScope.expt_value);
		}
	}
	
	/* evaluation */
	/**
	 * It clears the concrete values evaluated from the store-value
	 */
	protected void clc() { this.con_values.clear(); }
	/**
	 * @param context
	 * @return It evaluates the value to concrete one using context and update
	 * @throws Exception
	 */
	protected SymbolExpression add(SymbolProcess context) throws Exception {
		SymbolExpression value = this.eval(this.symb_value, context);
		this.con_values.add(value);
		return value;
	}
	
}
