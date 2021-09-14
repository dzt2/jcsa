package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It describes a basic value which is annotated at some store unit defined in
 * the program under test.
 * 
 * @author yukimula
 *
 */
public class CirAnnotation {
	
	/* definitions */
	private CirStoreClass		store_type;
	private CirNode				store_unit;
	private CirValueClass		value_type;
	private SymbolExpression	symb_value;
	private CirAnnotation(CirStoreClass store_type, CirNode store_unit,
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
			this.symb_value = CirValueScope.safe_evaluate(symb_value, null);
		}
	}
	
	/* getters */
	/**
	 * @return the execution point where the annotation is introduced
	 */
	public CirExecution		get_execution()	 { return this.store_unit.execution_of(); }
	/**
	 * @return the type of the store unit being annotated with the value
	 */
	public CirStoreClass 	get_store_type() { return this.store_type; }
	/**
	 * @return the location of store unit being annotated with the value
	 */
	public CirNode			get_store_unit() { return this.store_unit; }
	/** 
	 * @return the type of the value being annotated with the location
	 */
	public CirValueClass	get_value_type() { return this.value_type; }
	/**
	 * @return the symbolic value being annotated with the store units
	 */
	public SymbolExpression	get_symb_value() { return this.symb_value; } 
	@Override
	public String toString() {
		try {
			return String.format("[%s:%d]->(%s:%s)", 
					this.store_type.toString(),
					this.store_unit.get_node_id(),
					this.value_type.toString(),
					this.symb_value.generate_code(true));
		}
		catch(Exception ex) {
			ex.printStackTrace(System.err);
			return null;
		}
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof CirAnnotation) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	
	/* constructor */
	/**
	 * @param execution
	 * @param times
	 * @return [cond:statement]-->(cov_stmt:times)
	 * @throws Exception
	 */
	protected static CirAnnotation cov_stmt(CirExecution execution, int times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(times <= 0) {
			throw new IllegalArgumentException("Invalid times: " + times);
		}
		else {
			return new CirAnnotation(CirStoreClass.cond,
					execution.get_statement(), CirValueClass.cov_stmt,
					SymbolFactory.sym_constant(Integer.valueOf(times)));
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @return [cond:statement]-->(eva_expr:condition)
	 * @throws Exception
	 */
	protected static CirAnnotation eva_expr(CirExecution execution, Object condition) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirAnnotation(CirStoreClass.cond,
					execution.get_statement(), CirValueClass.eva_expr,
					SymbolFactory.sym_condition(condition, true));
		}
	}
	/**
	 * @param execution
	 * @param do_or_not
	 * @return [stmt:statement]-->(mut_stmt:do_or_not)
	 * @throws Exception
	 */
	protected static CirAnnotation mut_stmt(CirExecution execution, boolean do_or_not) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(execution.get_statement() instanceof CirTagStatement) {
			throw new IllegalArgumentException("Invalid: " + execution);
		}
		else {
			return new CirAnnotation(CirStoreClass.stmt, 
					execution.get_statement(), CirValueClass.mut_stmt,
					SymbolFactory.sym_constant(Boolean.valueOf(do_or_not)));
		}
	}
	/**
	 * @param expression
	 * @param value
	 * @return [expr:expression]-->(mut_expr:value)
	 * @throws Exception
	 */
	protected static CirAnnotation mut_expr(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(value == null) {
			throw new IllegalArgumentException("No mutated values are established");
		}
		else {
			/* declarations */
			CirStoreClass store_type; CirNode store_unit;
			CirValueClass value_type; SymbolExpression symb_value;
			
			/* determine store-part */
			if(CirMutations.is_assigned(expression)) {
				store_type = CirStoreClass.refr;
			}
			else {
				store_type = CirStoreClass.expr;
			}
			store_unit = expression;
			
			/* determine value-part */
			if(CirMutations.is_boolean(expression)) {
				symb_value = SymbolFactory.sym_condition(value, true);
			}
			else {
				symb_value = SymbolFactory.sym_expression(value);
			}
			value_type = CirValueClass.mut_expr;
			
			/* return finally */
			return new CirAnnotation(store_type, store_unit, value_type, symb_value);
		}
	}
	/**
	 * @param execution
	 * @return [stmt:statement]-->(trp_stmt:expt_value)
	 * @throws Exception
	 */
	protected static CirAnnotation trp_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			execution = execution.get_graph().get_exit();
			return new CirAnnotation(CirStoreClass.stmt, 
					execution.get_statement(), 
					CirValueClass.trp_stmt, CirValueScope.expt_value);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [expr:expression]-->(sub_diff:difference)
	 * @throws Exception
	 */
	protected static CirAnnotation sub_diff(CirExpression expression, SymbolExpression difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("No difference has been established");
		}
		else if(CirMutations.is_numeric(expression) || CirMutations.is_address(expression)) {
			if(CirMutations.is_assigned(expression)) {
				return new CirAnnotation(CirStoreClass.refr, expression, CirValueClass.sub_diff, difference);
			}
			else {
				return new CirAnnotation(CirStoreClass.expr, expression, CirValueClass.sub_diff, difference);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [expr:expression]-->(ext_diff:difference)
	 * @throws Exception
	 */
	protected static CirAnnotation ext_diff(CirExpression expression, SymbolExpression difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("No difference has been established");
		}
		else if(CirMutations.is_numeric(expression)) {
			if(CirMutations.is_assigned(expression)) {
				return new CirAnnotation(CirStoreClass.refr, expression, CirValueClass.ext_diff, difference);
			}
			else {
				return new CirAnnotation(CirStoreClass.expr, expression, CirValueClass.ext_diff, difference);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [expr:expression]-->(ext_diff:difference)
	 * @throws Exception
	 */
	protected static CirAnnotation xor_diff(CirExpression expression, SymbolExpression difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("No difference has been established");
		}
		else if(CirMutations.is_integer(expression)) {
			if(CirMutations.is_assigned(expression)) {
				return new CirAnnotation(CirStoreClass.refr, expression, CirValueClass.xor_diff, difference);
			}
			else {
				return new CirAnnotation(CirStoreClass.expr, expression, CirValueClass.xor_diff, difference);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression.get_data_type());
		}
	}
	
}
