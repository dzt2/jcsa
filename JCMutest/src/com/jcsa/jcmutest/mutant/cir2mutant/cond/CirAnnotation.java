package com.jcsa.jcmutest.mutant.cir2mutant.cond;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * The annotation to describe killing constraint using:						<br>
 * 	store_type:	the category of store unit to preserve the annotated values.<br>
 *  store_unit: the location of store unit to preserve the annotated values.<br>
 *  value_type: the category of annotated values be connected to store unit.<br>
 *  symb_value: the symbolic value to annotate the store unit in executions.<br>
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
			this.symb_value = CirValueScope.evaluate(symb_value, null);
		}
	}
	
	/* getters */
	/**
	 * @return the category of store unit to preserve the annotated values
	 */
	public CirStoreClass 	get_store_type() { return this.store_type; }
	/**
	 * @return the location of store unit to preserve the annotated values
	 */
	public CirNode			get_store_unit() { return this.store_unit; }
	/**
	 * @return the category of annotated values be connected to store unit
	 */
	public CirValueClass	get_value_type() { return this.value_type; }
	/**
	 * @return the symbolic value to annotate the store unit in executions
	 */
	public SymbolExpression	get_symb_value() { return this.symb_value; } 
	
	/* general */
	@Override
	public String toString() {
		try {
			String store_type = this.store_type.toString();
			String store_unit = "" + this.store_unit.get_node_id();
			String value_type = this.value_type.toString();
			String symb_value = this.symb_value.generate_code(true);
			return String.format("[%s:%s] -> (%s:%s)", 
					store_type, store_unit, value_type, symb_value);
		}
		catch(Exception ex) {
			ex.printStackTrace(System.err);
			return null;
		}
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CirAnnotation) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	
	/* factory */
	/**
	 * @param execution
	 * @param times
	 * @return [cond:statement] --> (cov_stmt:integer)
	 * @throws Exception
	 */
	protected static CirAnnotation cov_stmt(CirExecution execution, int times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution as null");
		}
		else if(times <= 0) {
			throw new IllegalArgumentException("Invalid times as " + times);
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
	 * @return [cond:statement] --> (cov_stmt:condition)
	 * @throws Exception
	 */
	protected static CirAnnotation eva_expr(CirExecution execution, Object condition) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution as null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition as null");
		}
		else {
			return new CirAnnotation(CirStoreClass.cond, 
					execution.get_statement(), CirValueClass.eva_expr, 
					SymbolFactory.sym_condition(condition, true));
		}
	}
	/**
	 * @param orig_flow
	 * @return [stmt:if.source] --> (ori_flow:orig_flow.source)
	 * @throws Exception
	 */
	protected static CirAnnotation ori_flow(CirExecutionFlow orig_flow) throws Exception {
		if(orig_flow == null) {
			throw new IllegalArgumentException("Invalid orig_flow: null");
		}
		else {
			return new CirAnnotation(CirStoreClass.stmt, 
					orig_flow.get_source().get_statement(), 
					CirValueClass.ori_flow, 
					SymbolFactory.sym_expression(orig_flow.get_target()));
		}
	}
	/**
	 * @param orig_flow
	 * @return [stmt:if.source] --> (mut_flow:muta_flow.target)
	 * @throws Exception
	 */
	protected static CirAnnotation mut_flow(CirExecutionFlow muta_flow) throws Exception {
		if(muta_flow == null) {
			throw new IllegalArgumentException("Invalid muta_flow: null");
		}
		else {
			return new CirAnnotation(CirStoreClass.stmt, 
					muta_flow.get_source().get_statement(), 
					CirValueClass.ori_flow, 
					SymbolFactory.sym_expression(muta_flow.get_target()));
		}
	}
	/**
	 * @param execution
	 * @param do_or_not
	 * @return [stmt:statement] --> (ori_stmt:do_or_not)
	 * @throws Exception
	 */
	protected static CirAnnotation ori_stmt(CirExecution execution, boolean do_or_not) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution as null");
		}
		else {
			return new CirAnnotation(CirStoreClass.stmt, 
					execution.get_statement(), 
					CirValueClass.ori_stmt, 
					SymbolFactory.sym_constant(do_or_not));
		}
	}
	/**
	 * @param execution
	 * @param do_or_not
	 * @return [stmt:statement] --> (mut_stmt:do_or_not)
	 * @throws Exception
	 */
	protected static CirAnnotation mut_stmt(CirExecution execution, boolean do_or_not) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution as null");
		}
		else {
			return new CirAnnotation(CirStoreClass.stmt, 
					execution.get_statement(), 
					CirValueClass.mut_stmt, 
					SymbolFactory.sym_constant(do_or_not));
		}
	}
	/**
	 * @param execution
	 * @return [stmt:statement] --> (trp_stmt:expt_value)
	 * @throws Exception
	 */
	protected static CirAnnotation trp_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution as null");
		}
		else {
			return new CirAnnotation(CirStoreClass.stmt, 
					execution.get_statement(), 
					CirValueClass.trp_stmt, 
					CirValueScope.expt_value);
		}
	}
	/**
	 * @param expression
	 * @return [expr:expression] --> (ori_expr:original_value)
	 * @throws Exception
	 */
	protected static CirAnnotation ori_expr(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutations.is_assigned(expression)) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			return new CirAnnotation(CirStoreClass.vars, expression, 
					CirValueClass.ori_vars, 
					SymbolFactory.sym_expression(statement.get_rvalue()));
		}
		else {
			return new CirAnnotation(CirStoreClass.expr, expression, 
					CirValueClass.ori_expr, 
					SymbolFactory.sym_expression(expression));
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return [expr:expression] --> (mut_expr|mut_vars:muta_value)
	 * @throws Exception
	 */
	protected static CirAnnotation mut_expr(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(CirMutations.is_assigned(expression)) {
			if(CirMutations.is_boolean(expression)) {
				muta_value = SymbolFactory.sym_condition(muta_value, true);
			}
			else {
				muta_value = SymbolFactory.sym_expression(muta_value);
			}
			return new CirAnnotation(CirStoreClass.vars, expression, 
					CirValueClass.mut_vars, (SymbolExpression) muta_value);
		}
		else {
			if(CirMutations.is_boolean(expression)) {
				muta_value = SymbolFactory.sym_condition(muta_value, true);
			}
			else {
				muta_value = SymbolFactory.sym_expression(muta_value);
			}
			return new CirAnnotation(CirStoreClass.expr, expression, 
					CirValueClass.mut_expr, (SymbolExpression) muta_value);
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return [expr:expression] --> (cmp_diff:not_equals)
	 * @throws Exception
	 */
	protected static CirAnnotation cmp_diff(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(CirMutations.is_assigned(expression)) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			CirExpression orig_value = statement.get_rvalue();
			return new CirAnnotation(CirStoreClass.vars, expression,
					CirValueClass.cmp_diff, 
					SymbolFactory.not_equals(orig_value, muta_value));
		}
		else {
			return new CirAnnotation(CirStoreClass.expr, expression, 
					CirValueClass.cmp_diff, 
					SymbolFactory.not_equals(expression, muta_value));
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return [expr:expression] --> (sub_diff:subtraction)
	 * @throws Exception
	 */
	protected static CirAnnotation sub_diff(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(CirMutations.is_assigned(expression)) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			
			CirExpression orig_value = statement.get_rvalue();
			SymbolExpression difference;
			if(CirMutations.is_numeric(orig_value)) {
				difference = SymbolFactory.arith_sub(orig_value.get_data_type(), muta_value, orig_value);
			}
			else if(CirMutations.is_address(orig_value)) {
				difference = SymbolFactory.arith_sub(orig_value.get_data_type(), muta_value, orig_value);
			}
			else {
				throw new IllegalArgumentException(orig_value.generate_code(true));
			}
			
			return new CirAnnotation(CirStoreClass.vars, expression, CirValueClass.sub_diff, difference);
		}
		else {
			CirExpression orig_value = expression;
			SymbolExpression difference;
			if(CirMutations.is_numeric(orig_value)) {
				difference = SymbolFactory.arith_sub(orig_value.get_data_type(), muta_value, orig_value);
			}
			else if(CirMutations.is_address(orig_value)) {
				difference = SymbolFactory.arith_sub(orig_value.get_data_type(), muta_value, orig_value);
			}
			else {
				throw new IllegalArgumentException(orig_value.generate_code(true));
			}
			return new CirAnnotation(CirStoreClass.expr, expression, CirValueClass.sub_diff, difference);
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return [expr:expression] --> (xor_diff:exclude_or)
	 * @throws Exception
	 */
	protected static CirAnnotation xor_diff(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(CirMutations.is_assigned(expression)) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			CirExpression orig_value = statement.get_lvalue();
			SymbolExpression difference;
			if(CirMutations.is_integer(orig_value)) {
				difference = SymbolFactory.bitws_xor(orig_value.get_data_type(), muta_value, orig_value);
			}
			else {
				throw new IllegalArgumentException(orig_value.generate_code(true));
			}
			return new CirAnnotation(CirStoreClass.vars, expression, CirValueClass.xor_diff, difference);
		}
		else {
			CirExpression orig_value = expression;
			SymbolExpression difference;
			if(CirMutations.is_integer(orig_value)) {
				difference = SymbolFactory.bitws_xor(orig_value.get_data_type(), muta_value, orig_value);
			}
			else {
				throw new IllegalArgumentException(orig_value.generate_code(true));
			}
			return new CirAnnotation(CirStoreClass.expr, expression, CirValueClass.xor_diff, difference);
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return [expr:expression] --> (ext_diff:extend_subtract)
	 * @throws Exception
	 */
	protected static CirAnnotation ext_diff(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(CirMutations.is_assigned(expression)) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			CirExpression orig_value = statement.get_lvalue();
			SymbolExpression difference;
			if(CirMutations.is_numeric(expression)) {
				difference = CirValueScope.ext_diff(orig_value, muta_value);
			}
			else {
				throw new IllegalArgumentException(orig_value.generate_code(true));
			}
			return new CirAnnotation(CirStoreClass.vars, expression, CirValueClass.ext_diff, difference);
		}
		else {
			CirExpression orig_value = expression;
			SymbolExpression difference;
			if(CirMutations.is_numeric(expression)) {
				difference = CirValueScope.ext_diff(orig_value, muta_value);
			}
			else {
				throw new IllegalArgumentException(orig_value.generate_code(true));
			}
			return new CirAnnotation(CirStoreClass.expr, expression, CirValueClass.ext_diff, difference);
		}
	}
	
}
