package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It denotes an abstract annotation to describe the abstract features of killing process.
 *
 * @author yukimula
 *
 */
public class CirAnnotation {

	/* attributes */
	/** abstract category of the annotation **/
	private CirAnnotationClass		category;
	/** refined class of the annotation **/
	private CirAnnotationType		operator;
	/** the CFG-node where the annotation is evaluated **/
	private CirExecution			execution;
	/** the C-intermediate location where annotation is defined **/
	private CirNode					location;
	/** symbolic expression as the parameter to refine the node **/
	private SymbolExpression		parameter;

	/* constructor */
	/**
	 * @param category
	 * @param operator
	 * @param execution
	 * @param location
	 * @param parameter
	 * @throws IllegalArgumentException
	 */
	private CirAnnotation(CirAnnotationClass category, CirAnnotationType operator, CirExecution
			execution, CirNode location, SymbolExpression parameter) throws IllegalArgumentException {
		if(category == null) {
			throw new IllegalArgumentException("Invalid category: null");
		}
		else if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {
			this.category = category;
			this.operator = operator;
			this.execution = execution;
			this.location = location;
			this.parameter = parameter;
		}
	}

	/* getters */
	/**
	 * @return abstract category of the annotation
	 */
	public CirAnnotationClass	get_category() 	{ return this.category; }
	/**
	 * @return the refined class of the annotation
	 */
	public CirAnnotationType	get_operator() 	{ return this.operator; }
	/**
	 * @return the CFG-node where the annotation is evaluated
	 */
	public CirExecution			get_execution() { return this.execution; }
	/**
	 * @return the C-intermediate location where annotation is defined
	 */
	public CirNode				get_location() 	{ return this.location; }
	/**
	 * @return whether the parameter is not null
	 */
	public boolean				has_parameter() { return this.parameter != null; }
	/**
	 * @return symbolic expression as the parameter to refine the node
	 */
	public SymbolExpression		get_parameter() { return this.parameter; }

	/* universals */
	@Override
	public String toString() {
		return this.category + "$" + this.operator + "$" +
				this.execution + "$" + this.location + "$" + this.parameter;
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
	
	/* factory approach */
	/* constraint class */
	/**
	 * @param execution	the CFG-node to be counted for coverage analysis
	 * @param times		the minimal time of the statement being executed
	 * @return constraint:cov_stmt:execution:statement:integer
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
			return new CirAnnotation(CirAnnotationClass.constraint,
					CirAnnotationType.cov_stmt, 
					execution, execution.get_statement(),
					SymbolFactory.sym_constant(Integer.valueOf(times)));
		}
	}
	/**
	 * @param execution	the CFG-node in which the constraint should be evaluated
	 * @param condition	the conditional expression being evaluated with CFG-node
	 * @param value		the boolean value that the condition needs be hold as it
	 * @return			constraint:eva_expr:execution:statement:{condition as value}
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
			return new CirAnnotation(CirAnnotationClass.constraint,
					CirAnnotationType.eva_expr,
					execution, execution.get_statement(),
					SymbolFactory.sym_condition(condition, true));
		}
	}
	/* stmt_error class */
	/**
	 * @param orig_flow	the original flow being replaced with mutation flow
	 * @param muta_flow	the mutation flow to replace with the original flow
	 * @return			stmt_error:mut_flow:orig_source:orig_target:muta_target
	 * @throws Exception
	 */
	public static CirAnnotation mut_flow(CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws Exception {
		if(orig_flow == null) {
			throw new IllegalArgumentException("Invalid orig_flow: null");
		}
		else if(muta_flow == null) {
			throw new IllegalArgumentException("Invalid muta_flow: null");
		}
		else if(muta_flow.get_source() == orig_flow.get_source()) {
			throw new IllegalArgumentException(orig_flow + " ==> " + muta_flow);
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt_error,
					CirAnnotationType.mut_flow, orig_flow.get_source(),
					orig_flow.get_target().get_statement(),
					SymbolFactory.sym_expression(muta_flow.get_target()));
		}
	}
	/**
	 * @param execution	the CFG-Node that was executed even when it should not
	 * @return			stmt_error:mut_stmt:execution:statement:true
	 * @throws Exception
	 */
	public static CirAnnotation add_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt_error,
					CirAnnotationType.mut_stmt, 
					execution, execution.get_statement(),
					SymbolFactory.sym_constant(Boolean.TRUE));
		}
	}
	/**
	 * @param execution	the CFG-Node that is not executed even when it should be
	 * @return			stmt_error:mut_stmt:execution:statement:false
	 * @throws Exception
	 */
	public static CirAnnotation del_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt_error,
					CirAnnotationType.mut_stmt, 
					execution, execution.get_statement(),
					SymbolFactory.sym_constant(Boolean.FALSE));
		}
	}
	/**
	 * @param execution	the CFG-node where the trapping failure will be produced
	 * @return			stmt_error:trp_stmt:execution:statement:null
	 * @throws Exception
	 */
	public static CirAnnotation trp_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt_error,
					CirAnnotationType.trp_stmt, 
					execution, execution.get_statement(), null);
		}
	}
	/* symb_error class */
	/**
	 * @param orig_expression	the original expression to be replaced with mutation value
	 * @param muta_expression	the mutation expression to replace with the original value
	 * @return					symb_error:mut_expr:execution:orig_expression:muta_expression
	 * @throws Exception
	 */
	public static CirAnnotation mut_expr(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null || orig_expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + orig_expression);
		}
		else if(muta_expression == null) {
			throw new IllegalArgumentException("Invalid muta_expression: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.symb_error,
					CirAnnotationType.mut_expr, orig_expression.execution_of(),
					orig_expression, SymbolFactory.sym_expression(muta_expression));
		}
	}
	/**
	 * @param orig_expression	the original expression to be replaced with mutation value
	 * @param muta_expression	the mutation expression to replace with the original value
	 * @return					symb_error:mut_refr:execution:orig_expression:muta_expression
	 * @throws Exception
	 */
	public static CirAnnotation mut_refr(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null || orig_expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + orig_expression);
		}
		else if(muta_expression == null) {
			throw new IllegalArgumentException("Invalid muta_expression: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.symb_error,
					CirAnnotationType.mut_refr, orig_expression.execution_of(),
					orig_expression, SymbolFactory.sym_expression(muta_expression));
		}
	}
	/**
	 * @param orig_expression	the original expression to be replaced with mutation value
	 * @param muta_expression	the mutation expression to replace with the original value
	 * @return					symb_error:mut_stat:execution:orig_expression:muta_expression
	 * @throws Exception
	 */
	public static CirAnnotation mut_stat(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null || orig_expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + orig_expression);
		}
		else if(muta_expression == null) {
			throw new IllegalArgumentException("Invalid muta_expression: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.symb_error,
					CirAnnotationType.mut_stat, orig_expression.execution_of(),
					orig_expression, SymbolFactory.sym_expression(muta_expression));
		}
	}
	/* bool_error class */
	/**
	 * @param expression	the original expression being replaced with the value
	 * @param value			boolean value to replace with the original expression
	 * @return				bool_error:set_bool:execution:expression:boolean
	 * @throws Exception
	 */
	public static CirAnnotation set_bool(CirExpression expression, boolean value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_boolean(expression)) {
			return new CirAnnotation(CirAnnotationClass.bool_error,
					CirAnnotationType.set_bool, expression.execution_of(),
					expression, SymbolFactory.sym_constant(Boolean.valueOf(value)));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with any value
	 * @return				bool_error:chg_bool:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation chg_bool(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_boolean(expression)) {
			return new CirAnnotation(CirAnnotationClass.bool_error,
					CirAnnotationType.chg_bool, 
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/* addr_error class */
	/**
	 * @param expression	the original expression being replaced with the value
	 * @param value			the constant of address to replace the original value
	 * @return				addr_error:set_addr:execution:expression:constant
	 * @throws Exception
	 */
	public static CirAnnotation set_addr(CirExpression expression, long value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.addr_error,
					CirAnnotationType.set_addr, 
					expression.execution_of(), expression,
					SymbolFactory.sym_constant(Long.valueOf(value)));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				addr_error:set_null:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation set_null(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.addr_error,
					CirAnnotationType.set_null,
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				addr_error:set_invp:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation set_invp(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.addr_error,
					CirAnnotationType.set_invp,
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				addr_error:chg_addr:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation chg_addr(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.addr_error,
					CirAnnotationType.chg_addr,
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/* auto_error class */
	/**
	 * @param expression	the original expression being replaced with the value
	 * @param value			the constant of values to replace with original value
	 * @return				auto_error:set_auto:execution:expression:constant
	 * @throws Exception
	 */
	public static CirAnnotation set_auto(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as null");
		}
		else if(CirMutation.is_automic(expression)) {
			return new CirAnnotation(CirAnnotationClass.auto_error,
					CirAnnotationType.set_auto, 
					expression.execution_of(), expression,
					SymbolFactory.sym_constant(value));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				auto_error:chg_auto:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation chg_auto(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_automic(expression)) {
			return new CirAnnotation(CirAnnotationClass.auto_error,
					CirAnnotationType.chg_auto, 
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/* numb_error class */
	/**
	 * @param expression	the original expression being replaced with the value
	 * @param value			the constant of values to replace with original value
	 * @return				numb_error:set_numb:execution:expression:value
	 * @throws Exception
	 */
	public static CirAnnotation set_numb(CirExpression expression, long value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.numb_error,
					CirAnnotationType.set_numb, expression.execution_of(),
					expression, SymbolFactory.sym_constant(Long.valueOf(value)));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @param value			the constant of values to replace with original value
	 * @return				numb_error:set_numb:execution:expression:value
	 * @throws Exception
	 */
	public static CirAnnotation set_numb(CirExpression expression, double value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.numb_error,
					CirAnnotationType.set_numb, expression.execution_of(),
					expression, SymbolFactory.sym_constant(Double.valueOf(value)));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				numb_error:chg_numb:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation chg_numb(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.numb_error,
					CirAnnotationType.chg_numb, 
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				numb_error:set_post:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation set_post(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.numb_error,
					CirAnnotationType.set_post, 
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				numb_error:set_npos:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation set_npos(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.numb_error,
					CirAnnotationType.set_npos, 
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				numb_error:set_negt:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation set_negt(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.numb_error,
					CirAnnotationType.set_negt, 
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				numb_error:set_nneg:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation set_nneg(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.numb_error,
					CirAnnotationType.set_nneg, 
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				numb_error:set_zero:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation set_zero(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.numb_error,
					CirAnnotationType.set_zero, 
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				numb_error:set_nzro:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation set_nzro(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.numb_error,
					CirAnnotationType.set_nzro, 
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/* scop_error class */
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				scop_error:inc_scop:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation inc_scop(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression) || CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.inc_scop, expression.execution_of(),
					expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				scop_error:dec_scop:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation dec_scop(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression) || CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.dec_scop, expression.execution_of(),
					expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				scop_error:ext_scop:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation ext_scop(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression) || CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.ext_scop, expression.execution_of(),
					expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression	the original expression being replaced with the value
	 * @return				scop_error:shk_scop:execution:expression:null
	 * @throws Exception
	 */
	public static CirAnnotation shk_scop(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(CirMutation.is_numeric(expression) || CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.shk_scop, expression.execution_of(),
					expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	
}
