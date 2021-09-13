package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It defines the annotation value that is annotated at some store unit with a 
 * specified value category to describe the semantic property.
 * 
 * @author yukimula
 *
 */
public class CirAnnotation {
	
	/* definitions */
	/** the type of store unit to be annotated **/
	private CirAnnotationClass	store_type;
	/** the C-based store unit to be annotated **/
	private CirNode				store_unit;
	/** the type of value annotated on a store **/
	private CirAnnotationType	value_type;
	/** the symbolic value annotated on a unit **/
	private SymbolExpression	symb_value;
	
	/* constructor */
	/**
	 * @param store_type
	 * @param store_unit
	 * @param value_type
	 * @param symb_value
	 * @throws Exception
	 */
	private CirAnnotation(CirAnnotationClass store_type, CirNode store_unit,
			CirAnnotationType value_type, SymbolExpression symb_value) throws Exception {
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
			this.symb_value = CirAnnotationValue.safe_evaluate(symb_value, null);
		}
	}
	
	/* getters */
	/**
	 * @return the control-flow node where the annotation is inserted to be evaluated in
	 */
	public CirExecution			get_exec_point() { return this.store_unit.execution_of(); }
	/**
	 * @return the type of store unit to be annotated
	 */
	public CirAnnotationClass	get_store_type() { return this.store_type; }
	/**
	 * @return the C-based store unit to be annotated
	 */
	public CirNode				get_store_unit() { return this.store_unit; }
	/**
	 * @return the type of value annotated on a store
	 */
	public CirAnnotationType	get_value_type() { return this.value_type; }
	/**
	 * @return the symbolic value annotated on a unit
	 */
	public SymbolExpression		get_symb_value() { return this.symb_value; } 
	@Override
	public String toString() {
		try {
			return String.format("[%s:%d] --> (%s:%s)", 
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
	
	/* factory */
	/**
	 * @param execution
	 * @param times
	 * @return [cond:statement] --> (cov_stmt:times)
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
			return new CirAnnotation(CirAnnotationClass.cond, 
					execution.get_statement(), CirAnnotationType.cov_stmt,
					SymbolFactory.sym_constant(Integer.valueOf(times)));
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @param value
	 * @return [cond:statement] --> (eva_expr:{condition as value})
	 * @throws Exception
	 */
	protected static CirAnnotation eva_expr(CirExecution execution, Object condition, boolean value) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.cond,
					execution.get_statement(), CirAnnotationType.eva_expr,
					SymbolFactory.sym_condition(condition, value));
		}
	}
	/**
	 * @param execution
	 * @param do_or_not
	 * @return [stmt:statement] --> (ori_stmt:do_or_not)
	 * @throws Exception
	 */
	protected static CirAnnotation ori_stmt(CirExecution execution, boolean do_or_not) throws Exception {
		if(execution == null || execution.get_statement() instanceof CirTagStatement) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt,
					execution.get_statement(), CirAnnotationType.ori_stmt,
					SymbolFactory.sym_constant(Boolean.valueOf(do_or_not)));
		}
	}
	/**
	 * @param execution
	 * @param do_or_not
	 * @return [stmt:statement] --> (mut_stmt:do_or_not)
	 * @throws Exception
	 */
	protected static CirAnnotation mut_stmt(CirExecution execution, boolean do_or_not) throws Exception {
		if(execution == null || execution.get_statement() instanceof CirTagStatement) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt,
					execution.get_statement(), CirAnnotationType.mut_stmt,
					SymbolFactory.sym_constant(Boolean.valueOf(do_or_not)));
		}
	}
	/**
	 * @param execution
	 * @return [stmt:statement] --> (trp_stmt:expt_value)
	 * @throws Exception
	 */
	protected static CirAnnotation trp_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			execution = execution.get_graph().get_exit();
			return new CirAnnotation(CirAnnotationClass.stmt,
					execution.get_statement(), CirAnnotationType.trp_stmt,
					CirAnnotationValue.expt_value);
		}
	}
	/**
	 * @param expression
	 * @return [expr|refr:expression] --> (ori_expr:original_value)
	 * @throws Exception
	 */
	protected static CirAnnotation ori_expr(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as null");
		}
		else if(CirMutations.is_assigned(expression)) {
			CirAssignStatement statement = (CirAssignStatement) expression.statement_of();
			CirExpression orig_expression = statement.get_rvalue();
			
			SymbolExpression original_value;
			if(CirMutations.is_boolean(orig_expression)) {
				original_value = SymbolFactory.sym_condition(value, true);
			}
			else {
				original_value = SymbolFactory.sym_expression(value);
			}
			return new CirAnnotation(CirAnnotationClass.refr, expression,
					CirAnnotationType.ori_expr, original_value);
		}
		else {
			SymbolExpression original_value;
			if(CirMutations.is_boolean(expression)) {
				original_value = SymbolFactory.sym_condition(value, true);
			}
			else {
				original_value = SymbolFactory.sym_expression(value);
			}
			return new CirAnnotation(CirAnnotationClass.expr, expression,
					CirAnnotationType.ori_expr, original_value);
		}
	}
	/**
	 * @param expression
	 * @param value
	 * @return [expr|refr:expression] --> (mut_expr:value)
	 * @throws Exception
	 */
	protected static CirAnnotation mut_expr(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as null");
		}
		else if(CirMutations.is_assigned(expression)) {
			CirAssignStatement statement = (CirAssignStatement) expression.statement_of();
			CirExpression orig_expression = statement.get_rvalue();
			
			SymbolExpression mutation_value;
			if(CirMutations.is_boolean(orig_expression)) {
				mutation_value = SymbolFactory.sym_condition(value, true);
			}
			else {
				mutation_value = SymbolFactory.sym_expression(value);
			}
			return new CirAnnotation(CirAnnotationClass.refr, expression,
					CirAnnotationType.mut_expr, mutation_value);
		}
		else {
			SymbolExpression mutation_value;
			if(CirMutations.is_boolean(expression)) {
				mutation_value = SymbolFactory.sym_condition(value, true);
			}
			else {
				mutation_value = SymbolFactory.sym_expression(value);
			}
			return new CirAnnotation(CirAnnotationClass.expr, expression,
					CirAnnotationType.mut_expr, mutation_value);
		}
	}
	
	/* difference */
	/**
	 * @param execution
	 * @param orig_value
	 * @param muta_value
	 * @return [stmt:statement] --> (cmp_diff:muta_value)
	 * @throws Exception
	 */
	protected static CirAnnotation cmp_diff(CirExecution execution, boolean orig_value, boolean muta_value) throws Exception {
		if(execution == null || execution.get_statement() instanceof CirTagStatement) {
			throw new IllegalArgumentException("Invalid execution: " + execution);
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt, 
					execution.get_statement(), CirAnnotationType.cmp_diff, 
					SymbolFactory.sym_constant(Boolean.valueOf(muta_value)));
		}
	}
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return [expr:expression] --> (cmp_diff:{orig_value != muta_value})
	 * @throws Exception
	 */
	protected static CirAnnotation cmp_diff(CirExpression expression, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(CirMutations.is_assigned(expression)) {
			return new CirAnnotation(CirAnnotationClass.refr,
					expression, CirAnnotationType.cmp_diff,
					SymbolFactory.not_equals(orig_value, muta_value));
		}
		else {
			return new CirAnnotation(CirAnnotationClass.expr,
					expression, CirAnnotationType.cmp_diff,
					SymbolFactory.not_equals(orig_value, muta_value));
		}
	}
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return [expr:expression] --> (sub_diff:{muta_value - orig_value})
	 * @throws Exception
	 */
	protected static CirAnnotation sub_diff(CirExpression expression, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(CirMutations.is_numeric(expression) || CirMutations.is_address(expression)) {
			SymbolExpression difference = CirAnnotationValue.sub_difference(expression, orig_value, muta_value);
			if(CirMutations.is_assigned(expression)) {
				return new CirAnnotation(CirAnnotationClass.refr, expression,
						CirAnnotationType.sub_diff, difference);
			}
			else {
				return new CirAnnotation(CirAnnotationClass.expr, expression,
						CirAnnotationType.sub_diff, difference);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression);
		}
	}
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return [expr:expression] --> (xor_diff:{muta_value ^ orig_value})
	 * @throws Exception
	 */
	protected static CirAnnotation xor_diff(CirExpression expression, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(CirMutations.is_integer(expression)) {
			SymbolExpression difference = CirAnnotationValue.xor_difference(expression, orig_value, muta_value);
			if(CirMutations.is_assigned(expression)) {
				return new CirAnnotation(CirAnnotationClass.refr, expression,
						CirAnnotationType.xor_diff, difference);
			}
			else {
				return new CirAnnotation(CirAnnotationClass.expr, expression,
						CirAnnotationType.xor_diff, difference);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression);
		}
	}
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return [expr:expression] --> (ext_diff:{abs(muta_value) - abs(orig_value)})
	 * @throws Exception
	 */
	protected static CirAnnotation ext_diff(CirExpression expression, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(CirMutations.is_numeric(expression)) {
			SymbolExpression difference = CirAnnotationValue.ext_difference(expression, orig_value, muta_value);
			if(CirMutations.is_assigned(expression)) {
				return new CirAnnotation(CirAnnotationClass.refr, expression,
						CirAnnotationType.ext_diff, difference);
			}
			else {
				return new CirAnnotation(CirAnnotationClass.expr, expression,
						CirAnnotationType.ext_diff, difference);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression);
		}
	}
	
	/* prediction */
	/**
	 * @param execution
	 * @param difference
	 * @return [stmt:statement] --> (cmp_diff:difference)
	 * @throws Exception
	 */
	protected static CirAnnotation cmp_diff(CirExecution execution, boolean difference) throws Exception {
		if(execution == null || execution.get_statement() instanceof CirTagStatement) {
			throw new IllegalArgumentException("Invalid execution: " + execution);
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt, 
					execution.get_statement(), CirAnnotationType.cmp_diff, 
					SymbolFactory.sym_constant(Boolean.valueOf(difference)));
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [expr:expression] --> (cmp_diff:difference)
	 * @throws Exception
	 */
	protected static CirAnnotation cmp_diff(CirExpression expression, SymbolExpression difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(CirMutations.is_assigned(expression)) {
			return new CirAnnotation(CirAnnotationClass.refr,
					expression, CirAnnotationType.cmp_diff, difference);
		}
		else {
			return new CirAnnotation(CirAnnotationClass.expr,
					expression, CirAnnotationType.cmp_diff, difference);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [expr:expression] --> (sub_diff:difference)
	 * @throws Exception
	 */
	protected static CirAnnotation sub_diff(CirExpression expression, SymbolExpression difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(CirMutations.is_numeric(expression) || CirMutations.is_address(expression)) {
			if(CirMutations.is_assigned(expression)) {
				return new CirAnnotation(CirAnnotationClass.refr, expression,
						CirAnnotationType.sub_diff, difference);
			}
			else {
				return new CirAnnotation(CirAnnotationClass.expr, expression,
						CirAnnotationType.sub_diff, difference);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [expr:expression] --> (xor_diff:difference)
	 * @throws Exception
	 */
	protected static CirAnnotation xor_diff(CirExpression expression, SymbolExpression difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(CirMutations.is_integer(expression)) {
			if(CirMutations.is_assigned(expression)) {
				return new CirAnnotation(CirAnnotationClass.refr, expression,
						CirAnnotationType.xor_diff, difference);
			}
			else {
				return new CirAnnotation(CirAnnotationClass.expr, expression,
						CirAnnotationType.xor_diff, difference);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [expr:expression] --> (ext_diff:difference)
	 * @throws Exception
	 */
	protected static CirAnnotation ext_diff(CirExpression expression, SymbolExpression difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(CirMutations.is_numeric(expression)) {
			if(CirMutations.is_assigned(expression)) {
				return new CirAnnotation(CirAnnotationClass.refr, expression,
						CirAnnotationType.ext_diff, difference);
			}
			else {
				return new CirAnnotation(CirAnnotationClass.expr, expression,
						CirAnnotationType.ext_diff, difference);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression);
		}
	}
	
}
