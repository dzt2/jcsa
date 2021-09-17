package com.jcsa.jcmutest.mutant.cir2mutant.stat.anot;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It specifies a logical assertion (predicate) that is annotated with some 
 * program execution point using the store-value pair format as following:<br>
 * <code>
 * 	logic_class{execution}(store_class:store_unit, value_class:symb_value)
 * </code>
 * 
 * @author yukimula
 *
 */
public class CirAnnotation {
	
	/* definitions */
	/** the logical predicate defining the function for the annotation **/
	private CirLogicClass		logic_type;
	/** the execution point where this logical annotation is evaluated **/
	private CirExecution		execution;
	/** the category of the store unit being annotated with the logics **/
	private CirStoreClass		store_type;
	/** the store unit in C-intermediate representative code locations **/
	private CirNode				store_unit;
	/** the category of the symbolic value that is annotated with unit **/
	private CirValueClass		value_type;
	/** the symbolic value being annotated with unit in the annotation  **/
	private SymbolExpression	symb_value;
	
	/* getters */
	/**
	 * @return the logical predicate defining the function for the annotation
	 */
	public CirLogicClass	get_logic_type() { return this.logic_type; }
	/**
	 * @return the execution point where this logical annotation is evaluated
	 */
	public CirExecution		get_execution () { return this.execution; }
	/**
	 * @return the category of the store unit being annotated with the logics
	 */
	public CirStoreClass	get_store_type() { return this.store_type; }
	/**
	 * @return the store unit in C-intermediate representative code locations
	 */
	public CirNode			get_store_unit() { return this.store_unit; }
	/**
	 * @return the category of the symbolic value that is annotated with unit
	 */
	public CirValueClass	get_value_type() { return this.value_type; }
	/**
	 * @return the symbolic value being annotated with unit in the annotation
	 */
	public SymbolExpression get_symb_value() { return this.symb_value; }
	@Override
	public String toString() {
		return String.format("%s{%s}([%s:%d], [%s:%s])", 
				this.logic_type.toString(), this.execution.toString(),
				this.store_type.toString(), this.store_unit.get_node_id(),
				this.value_type.toString(), this.symb_value.toString());
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
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
	
	/* factory methods */
	/**
	 * private constructor for factory mode
	 * @param logic_type
	 * @param execution
	 * @param store_type
	 * @param store_unit
	 * @param value_type
	 * @param symb_value
	 * @throws Exception
	 */
	private CirAnnotation(CirLogicClass logic_type, CirExecution execution,
			CirStoreClass store_type, CirNode store_unit, CirValueClass 
			value_type, SymbolExpression symb_value) throws Exception {
		if(logic_type == null) {
			throw new IllegalArgumentException("Invalid logic_type: null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution as null");
		}
		else if(store_type == null) {
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
			this.logic_type = logic_type;
			this.execution  = execution ;
			this.store_type = store_type;
			this.store_unit = store_unit;
			this.value_type = value_type;
			this.symb_value = CirValueScope.safe_evaluate(symb_value, null);
		}
	}
	/**
	 * @param execution
	 * @param times
	 * @return cov_stmt{execution}([stmt:statement], [usig:execution_times])
	 * @throws Exception
	 */
	public static CirAnnotation cov_stmt(CirExecution execution, int times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(times <= 0) {
			throw new IllegalArgumentException("Invalid times: " + times);
		}
		else {
			return new CirAnnotation(CirLogicClass.cov_stmt, execution,
					CirStoreClass.cond, execution.get_statement(),
					CirValueClass.usig, SymbolFactory.sym_constant(times));
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @return eva_expr{execution}([cond:statement], [bool:condition])
	 * @throws Exception
	 */
	public static CirAnnotation eva_expr(CirExecution execution, Object condition) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirAnnotation(CirLogicClass.eva_expr, execution,
					CirStoreClass.cond, execution.get_statement(),
					CirValueClass.bool,
					SymbolFactory.sym_condition(condition, true));
		}
	}
	/**
	 * @param execution
	 * @return trp_stmt{execution}([stmt:statement], [bool:expt_value])
	 * @throws Exception
	 */
	public static CirAnnotation trp_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			execution = execution.get_graph().get_exit();
			return new CirAnnotation(CirLogicClass.trp_stmt, execution,
					CirStoreClass.stmt, execution.get_statement(),
					CirValueClass.bool, CirValueScope.expt_value);
		}
	}
	/**
	 * @param execution
	 * @param exec_flag
	 * @return mut_stmt{execution}([stmt:statement], [bool:exec_flag])
	 * @throws Exception
	 */
	public static CirAnnotation mut_stmt(CirExecution execution, Object exec_flag) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(exec_flag == null) {
			throw new IllegalArgumentException("Invalid exec_flag: null");
		}
		else {
			return new CirAnnotation(CirLogicClass.mut_stmt, execution,
					CirStoreClass.stmt, execution.get_statement(),
					CirValueClass.bool, 
					SymbolFactory.sym_condition(exec_flag, true));
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return mut_expr{execution}([expr:expression], [type:muta_value])
	 * @throws Exception 
	 */
	public static CirAnnotation mut_expr(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value as null");
		}
		else if(CirMutations.is_assigned(expression)) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else {
			CirValueClass value_type; SymbolExpression symb_value;
			if(CirMutations.is_boolean(expression)) {
				value_type = CirValueClass.bool;
				symb_value = SymbolFactory.sym_condition(muta_value, true);
			}
			else if(CirMutations.is_usigned(expression)) {
				value_type = CirValueClass.usig;
				symb_value = SymbolFactory.sym_expression(muta_value);
			}
			else if(CirMutations.is_integer(expression)) {
				value_type = CirValueClass.sign;
				symb_value = SymbolFactory.sym_expression(muta_value);
			}
			else if(CirMutations.is_doubles(expression)) {
				value_type = CirValueClass.real;
				symb_value = SymbolFactory.sym_expression(muta_value);
			}
			else if(CirMutations.is_address(expression)) {
				value_type = CirValueClass.addr;
				symb_value = SymbolFactory.sym_expression(muta_value);
			}
			else {
				value_type = CirValueClass.auto;
				symb_value = SymbolFactory.sym_expression(muta_value);
			}
			return new CirAnnotation(CirLogicClass.mut_expr, expression.execution_of(),
					CirStoreClass.expr, expression, value_type, symb_value);
		}
	}
	/**
	 * @param reference
	 * @param muta_value
	 * @return mut_refr{execution}([refr:reference], [type:muta_value])
	 * @throws Exception
	 */
	public static CirAnnotation mut_refr(CirExpression reference, Object muta_value) throws Exception {
		if(reference == null || reference.statement_of() == null) {
			throw new IllegalArgumentException("Invalid reference: " + reference);
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value as null");
		}
		else if(CirMutations.is_assigned(reference)) {
			CirAssignStatement stmt = (CirAssignStatement) reference.get_parent();
			CirExpression expression = stmt.get_rvalue();
			
			CirValueClass value_type; SymbolExpression symb_value;
			if(CirMutations.is_boolean(expression)) {
				value_type = CirValueClass.bool;
				symb_value = SymbolFactory.sym_condition(muta_value, true);
			}
			else if(CirMutations.is_usigned(expression)) {
				value_type = CirValueClass.usig;
				symb_value = SymbolFactory.sym_expression(muta_value);
			}
			else if(CirMutations.is_integer(expression)) {
				value_type = CirValueClass.sign;
				symb_value = SymbolFactory.sym_expression(muta_value);
			}
			else if(CirMutations.is_doubles(expression)) {
				value_type = CirValueClass.real;
				symb_value = SymbolFactory.sym_expression(muta_value);
			}
			else if(CirMutations.is_address(expression)) {
				value_type = CirValueClass.addr;
				symb_value = SymbolFactory.sym_expression(muta_value);
			}
			else {
				value_type = CirValueClass.auto;
				symb_value = SymbolFactory.sym_expression(muta_value);
			}
			
			return new CirAnnotation(CirLogicClass.mut_refr, reference.execution_of(),
					CirStoreClass.refr, reference, value_type, symb_value);
		}
		else {
			throw new IllegalArgumentException("Invalid reference: " + reference);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return sub_diff{execution}([expr|refr], (type:difference))
	 * @throws Exception
	 */
	public static CirAnnotation sub_diff(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference as null");
		}
		else if(CirMutations.is_assigned(expression)) {
			CirValueClass value_type; SymbolExpression symb_value;
			if(CirMutations.is_integer(expression)) {
				value_type = CirValueClass.sign;
			}
			else if(CirMutations.is_doubles(expression)) {
				value_type = CirValueClass.real;
			}
			else if(CirMutations.is_address(expression)) {
				value_type = CirValueClass.addr;
			}
			else {
				throw new IllegalArgumentException("Invalid: " + expression.get_data_type() + "\t" 
						+ expression.generate_code(true) + "\t" + difference);
			}
			symb_value = SymbolFactory.sym_expression(difference);
			return new CirAnnotation(CirLogicClass.sub_diff, expression.execution_of(),
					CirStoreClass.refr, expression, value_type, symb_value);
		}
		else {
			CirValueClass value_type; SymbolExpression symb_value;
			if(CirMutations.is_integer(expression)) {
				value_type = CirValueClass.sign;
			}
			else if(CirMutations.is_doubles(expression)) {
				value_type = CirValueClass.real;
			}
			else if(CirMutations.is_address(expression)) {
				value_type = CirValueClass.addr;
			}
			else {
				throw new IllegalArgumentException("Invalid: " + expression);
			}
			symb_value = SymbolFactory.sym_expression(difference);
			return new CirAnnotation(CirLogicClass.sub_diff, expression.execution_of(),
					CirStoreClass.expr, expression, value_type, symb_value);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return xor_diff{execution}([expr:refr], [sign:difference])
	 * @throws Exception
	 */
	public static CirAnnotation xor_diff(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference as null");
		}
		else if(CirMutations.is_assigned(expression)) {
			CirValueClass value_type; SymbolExpression symb_value;
			if(CirMutations.is_integer(expression)) {
				value_type = CirValueClass.sign;
			}
			else {
				throw new IllegalArgumentException("Invalid: " + expression);
			}
			symb_value = SymbolFactory.sym_expression(difference);
			return new CirAnnotation(CirLogicClass.xor_diff, expression.execution_of(),
					CirStoreClass.refr, expression, value_type, symb_value);
		}
		else {
			CirValueClass value_type; SymbolExpression symb_value;
			if(CirMutations.is_integer(expression)) {
				value_type = CirValueClass.sign;
			}
			else {
				throw new IllegalArgumentException("Invalid: " + expression);
			}
			symb_value = SymbolFactory.sym_expression(difference);
			return new CirAnnotation(CirLogicClass.xor_diff, expression.execution_of(),
					CirStoreClass.expr, expression, value_type, symb_value);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return ext_diff{execution}([expr:refr], [type:difference])
	 * @throws Exception
	 */
	public static CirAnnotation ext_diff(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference as null");
		}
		else if(CirMutations.is_assigned(expression)) {
			CirValueClass value_type; SymbolExpression symb_value;
			if(CirMutations.is_integer(expression)) {
				value_type = CirValueClass.sign;
			}
			else if(CirMutations.is_doubles(expression)) {
				value_type = CirValueClass.real;
			}
			else {
				throw new IllegalArgumentException("Invalid: " + expression);
			}
			symb_value = SymbolFactory.sym_expression(difference);
			return new CirAnnotation(CirLogicClass.ext_diff, expression.execution_of(),
					CirStoreClass.refr, expression, value_type, symb_value);
		}
		else {
			CirValueClass value_type; SymbolExpression symb_value;
			if(CirMutations.is_integer(expression)) {
				value_type = CirValueClass.sign;
			}
			else if(CirMutations.is_doubles(expression)) {
				value_type = CirValueClass.real;
			}
			else {
				throw new IllegalArgumentException("Invalid: " + expression);
			}
			symb_value = SymbolFactory.sym_expression(difference);
			return new CirAnnotation(CirLogicClass.ext_diff, expression.execution_of(),
					CirStoreClass.expr, expression, value_type, symb_value);
		}
	}
	
}
