package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import com.jcsa.jcmutest.mutant.cir2mutant.CirInfection;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It represents a semantic property annotated at some execution point in
 * the program that is correlated with mutation killability.
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
	
	/* factory methods */
	/* constraint class */
	/**
	 * @param execution
	 * @param times
	 * @return constraint:cov_stmt:execution:statement:integer
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
			return new CirAnnotation(CirAnnotationClass.constraint,
					CirAnnotationType.cov_stmt, 
					execution, execution.get_statement(),
					SymbolFactory.sym_constant(Integer.valueOf(times)));
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @param value
	 * @return constraint:eva_expr:execution:statement:{condition as value}
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
			return new CirAnnotation(CirAnnotationClass.constraint,
					CirAnnotationType.eva_expr, 
					execution, execution.get_statement(),
					SymbolFactory.sym_condition(condition, value));
		}
	}
	/* stmt_error class */
	/**
	 * @param execution
	 * @return stmt_error:mut_stmt:execution:statement:true
	 * @throws Exception
	 */
	protected static CirAnnotation add_stmt(CirExecution execution) throws Exception {
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
	 * @param execution
	 * @return stmt_error:mut_stmt:execution:statement:false
	 * @throws Exception
	 */
	protected static CirAnnotation del_stmt(CirExecution execution) throws Exception {
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
	 * @param orig_flow
	 * @param muta_flow
	 * @return stmt_error:mut_flow:source:orig_target:muta_target
	 * @throws Exception
	 */
	protected static CirAnnotation mut_flow(CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws Exception {
		if(orig_flow == null) {
			throw new IllegalArgumentException("Invalid orig_flow: null");
		}
		else if(muta_flow == null) {
			throw new IllegalArgumentException("Invalid muta_flow: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt_error,
					CirAnnotationType.mut_flow,
					orig_flow.get_source(),
					orig_flow.get_target().get_statement(),
					SymbolFactory.sym_expression(muta_flow.get_target()));
		}
	}
	/**
	 * @param execution
	 * @return stmt_error:trp_stmt:execution:statement:null
	 * @throws Exception
	 */
	protected static CirAnnotation trp_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt_error,
					CirAnnotationType.trp_stmt, 
					execution, execution.get_statement(), null);
		}
	}
	/**
	 * @param reference
	 * @param mut_value
	 * @return stmt_error:mut_stat:execution:reference:mut_value
	 * @throws Exception
	 */
	protected static CirAnnotation mut_stat(CirExpression reference, SymbolExpression mut_value) throws Exception {
		if(reference == null || reference.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + reference);
		}
		else if(mut_value == null) {
			throw new IllegalArgumentException("Invalid mut_value as null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt_error,
					CirAnnotationType.mut_stat,
					reference.execution_of(), reference, mut_value);
		}
	}
	/* expr_error class */
	/**
	 * @param expression
	 * @param muta_value
	 * @return expr_error:execution:expression:muta_value
	 * @throws Exception
	 */
	protected static CirAnnotation set_expr(CirExpression expression, SymbolExpression mut_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(mut_value == null) {
			throw new IllegalArgumentException("Invalid mut_value as null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.expr_error,
					CirAnnotationType.set_expr,
					expression.execution_of(), expression, mut_value);
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return expr_error:sub_expr:execution:expression:sub_value
	 * @throws Exception
	 */
	protected static CirAnnotation sub_expr(CirExpression expression, SymbolExpression sub_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(sub_value == null) {
			throw new IllegalArgumentException("Invalid sub_value as null");
		}
		else if(CirInfection.is_numeric(expression) || CirInfection.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.expr_error,
					CirAnnotationType.sub_expr,
					expression.execution_of(), expression, sub_value);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param xor_value
	 * @return expr_error:xor_expr:execution:expression:xor_value
	 * @throws Exception
	 */
	protected static CirAnnotation xor_expr(CirExpression expression, SymbolExpression xor_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(xor_value == null) {
			throw new IllegalArgumentException("Invalid xor_value as null");
		}
		else if(CirInfection.is_integer(expression)) {
			return new CirAnnotation(CirAnnotationClass.expr_error,
					CirAnnotationType.xor_expr,
					expression.execution_of(), expression, xor_value);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param ext_value
	 * @return expr_error:ext_expr:execution:expression:ext_value
	 * @throws Exception
	 */
	protected static CirAnnotation ext_expr(CirExpression expression, SymbolExpression ext_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(ext_value == null) {
			throw new IllegalArgumentException("Invalid ext_value as null");
		}
		else if(CirInfection.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.expr_error,
					CirAnnotationType.ext_expr,
					expression.execution_of(), expression, ext_value);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression.get_data_type());
		}
	}
	
}
