package com.jcsa.jcmutest.mutant.ctx2mutant.tree;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutations;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class ContextAnnotation {
	
	/* definitions */
	/** the category of the annotation being defined **/
	private ContextAnnotationClass	category;
	/** the location where the annotation is defined **/
	private	AstCirNode				location;
	/** the symbolic value to define this annotation **/
	private	SymbolExpression		parameter;
	/**
	 * It creates an annotation with specified category and location.
	 * @param category	the category of the annotation being defined
	 * @param location	the location where the annotation is defined
	 * @param parameter	the symbolic value to define this annotation
	 * @throws Exception
	 */
	protected ContextAnnotation(ContextAnnotationClass category,
			AstCirNode location, 
			SymbolExpression parameter) throws IllegalArgumentException {
		if(category == null) {
			throw new IllegalArgumentException("Invalid category: null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(parameter == null) {
			throw new IllegalArgumentException("Invalid parameter: null");
		}
		else {
			this.category = category;
			this.location = location;
			this.parameter = parameter;
		}
	}
	
	/* getters */
	/**
	 * @return	the category of the annotation being defined
	 */
	public ContextAnnotationClass 	get_category() 	{ return this.category; }
	/**
	 * @return	the location where the annotation is defined
	 */
	public AstCirNode				get_location()	{ return this.location; }
	/**
	 * @return	the symbolic value to define this annotation
	 */
	public SymbolExpression			get_parameter()	{ return this.parameter; }
	@Override
	public String toString() { 
		return this.category + "(" + this.location.get_node_id() + ", " + this.parameter + ")"; 
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ContextAnnotation) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	
	/* factory */
	/**
	 * @param statement	the statement to be covered 
	 * @param min_times	the minimal times for running
	 * @return			cov_time(statement, min_times)
	 * @throws Exception
	 */
	public static ContextAnnotation cov_time(AstCirNode statement, int min_times) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(min_times <= 0) {
			throw new IllegalArgumentException("Invalid min_times: " + min_times);
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.cov_time, 
					statement, SymbolFactory.sym_constant(Integer.valueOf(min_times)));
		}
	}
	/**
	 * @param statement	the statement where the condition is evaluated
	 * @param condition	the symbolic condition to be evaluated on that point
	 * @return			eva_cond(statement, condition)
	 * @throws Exception
	 */
	public static ContextAnnotation eva_cond(AstCirNode statement, Object condition) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.eva_cond,
					statement, SymbolFactory.sym_condition(condition, true));
		}
	}
	/**
	 * @param statement	the statement where the condition is evaluated
	 * @param condition	the symbolic condition to be evaluated on that point
	 * @return			mus_cond(statement, condition)
	 * @throws Exception
	 */
	public static ContextAnnotation mus_cond(AstCirNode statement, Object condition) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.mus_cond,
					statement, SymbolFactory.sym_condition(condition, true));
		}
	}
	/**
	 * @param statement	the statement to be mutated as execution
	 * @param muta_exec	True (executed) False (not executed)
	 * @return			set_stmt(statement; muta_exec)
	 * @throws Exception
	 */
	public static ContextAnnotation set_stmt(AstCirNode statement, boolean muta_exec) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.set_stmt,
					statement, SymbolFactory.sym_constant(Boolean.valueOf(muta_exec)));
		}
	}
	/**
	 * @param statement
	 * @param muta_next
	 * @return set_flow(statement, muta_next)
	 * @throws Exception
	 */
	public static ContextAnnotation set_flow(AstCirNode statement, AstCirNode muta_next) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(muta_next == null) {
			throw new IllegalArgumentException("Invalid muta_next: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.set_flow, statement, 
					SymbolFactory.sym_constant(Integer.valueOf(muta_next.get_node_id())));
		}
	}
	/**
	 * @param statement
	 * @return trp_stmt(statement; exception)
	 * @throws Exception
	 */
	public static ContextAnnotation trp_stmt(AstCirNode statement) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.trp_stmt, 
					statement.module_of(), ContextMutations.trap_value);
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return set_expr(expression, muta_value)
	 * @throws Exception
	 */
	public static ContextAnnotation set_expr(AstCirNode expression, Object muta_value) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(expression.is_expression_node()) {
			return new ContextAnnotation(ContextAnnotationClass.set_expr, 
					expression, SymbolFactory.sym_expression(muta_value));
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return inc_expr(expression, muta_value)
	 * @throws Exception
	 */
	public static ContextAnnotation inc_expr(AstCirNode expression, Object difference) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else if(expression.is_expression_node()) {
			return new ContextAnnotation(ContextAnnotationClass.inc_expr, 
					expression, SymbolFactory.sym_expression(difference));
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return xor_expr(expression, muta_value)
	 * @throws Exception
	 */
	public static ContextAnnotation xor_expr(AstCirNode expression, Object difference) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else if(expression.is_expression_node()) {
			return new ContextAnnotation(ContextAnnotationClass.xor_expr, 
					expression, SymbolFactory.sym_expression(difference));
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression);
		}
	}
	
}
