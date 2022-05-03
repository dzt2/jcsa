package com.jcsa.jcmutest.mutant.ctx2mutant.tree;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutation;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	The contextual annotation to represent the contextual mutation state.
 * 	
 * 	@author yukimula
 *
 */
public class ContextAnnotation {
	
	/* definitions */
	/** the category of the annotation to define its type **/
	private	ContextAnnotationClass	category;
	/** the location at which the annotation is specified **/
	private	AstCirNode				location;
	/** the left-operand to describe the definition of the annotation **/
	private	SymbolExpression		loperand;
	/** the righ-operand to describe the definition of the annotation **/
	private	SymbolExpression		roperand;
	/**
	 * It creates an annotation to represent the context mutation state.
	 * @param category	the category of the annotation to define its type
	 * @param location	the location at which the annotation is specified
	 * @param loperand	the left-operand to describe the definition of the annotation
	 * @param roperand	the righ-operand to describe the definition of the annotation
	 * @throws Exception
	 */
	private	ContextAnnotation(ContextAnnotationClass category, AstCirNode location, 
			SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(category == null) {
			throw new IllegalArgumentException("Invalid category: null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			this.category = category; this.location = location;
			this.loperand = ContextMutation.evaluate(loperand, null, null);
			this.roperand = ContextMutation.evaluate(roperand, null, null);
		}
	}
	
	/* getters */
	/**
	 * @return	the category of the annotation to define its type
	 */
	public	ContextAnnotationClass	get_category()	{ return this.category; }
	/**
	 * @return	the location at which the annotation is specified
	 */
	public	AstCirNode				get_location()	{ return this.location; }
	/**
	 * @return	the left-operand to describe the definition of the annotation
	 */
	public	SymbolExpression		get_loperand()	{ return this.loperand; }
	/**
	 * @return	the righ-operand to describe the definition of the annotation
	 */
	public	SymbolExpression		get_roperand()	{ return this.roperand; }
	@Override
	public 	String	toString() 	{
		return this.category + "(" + this.location.get_node_id() + "; " + this.loperand + ", " + this.roperand + ")";
	}
	@Override
	public	int		hashCode()	{ return this.toString().hashCode(); }
	@Override
	public	boolean	equals(Object obj) {
		if(obj instanceof ContextAnnotation) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* factorys */
	/**
	 * @param statement	the statement to be covered at
	 * @param min_times	the minimal times for coverage
	 * @param max_times	the maximal times for coverage
	 * @return	cov_time(statement, min_times, max_times)
	 * @throws Exception
	 */
	public	static	ContextAnnotation	cov_time(AstCirNode statement, int min_times, int max_times) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid location: " + statement);
		}
		else if(min_times <= 0 || min_times > max_times) {
			throw new IllegalArgumentException(min_times + " |--> " + max_times);
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.cov_time, statement, 
					SymbolFactory.sym_constant(Integer.valueOf(min_times)), 
					SymbolFactory.sym_constant(Integer.valueOf(max_times)));
		}
	}
	/**
	 * @param statement	the statement where the condition is evaluated
	 * @param condition	the condition to be evaluated at the statement
	 * @param must_need	True (always satisfied); False (at least once)
	 * @return			eva_cond(statement; condition, must_need)
	 * @throws Exception
	 */
	public	static	ContextAnnotation	eva_cond(AstCirNode statement, Object condition, boolean must_need) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid location: " + statement);
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.eva_cond, statement,
					SymbolFactory.sym_condition(condition, true),
					SymbolFactory.sym_constant(Boolean.valueOf(must_need)));
		}
	}
	/**
	 * @param location	the location where the mutation is injected in source code
	 * @param mutant_ID	the integer ID of the syntactic mutation being introduced
	 * @param operators	the string code of the mutation operator
	 * @return			sed_muta(location; mutant_ID, operators)
	 * @throws Exception
	 */
	public	static	ContextAnnotation	sed_muta(AstCirNode location, int mutant_ID, String operators) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(operators == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.sed_muta, location,
					SymbolFactory.sym_constant(Integer.valueOf(mutant_ID)),
					SymbolFactory.literal(operators));
		}
	}
	/**
	 * @param statement	the statement of which execution is mutated
	 * @param muta_exec	True (executed) False (not-executed in mutation)
	 * @return
	 * @throws Exception
	 */
	public	static	ContextAnnotation	set_stmt(AstCirNode statement, boolean muta_exec) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid location: " + statement);
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.set_stmt, statement,
					SymbolFactory.sym_constant(Boolean.valueOf(!muta_exec)),
					SymbolFactory.sym_constant(Boolean.valueOf(muta_exec)));
		}
	}
	/**
	 * @param statement
	 * @param orig_next
	 * @param muta_next
	 * @return set_flow(statement, orig_next, muta_next)
	 * @throws Exception
	 */
	public	static	ContextAnnotation	set_flow(AstCirNode statement, int orig_next, int muta_next) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid location: " + statement);
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.set_flow, statement,
					SymbolFactory.sym_constant(Integer.valueOf(orig_next)),
					SymbolFactory.sym_constant(Integer.valueOf(muta_next)));
		}
	}
	/**
	 * @param location
	 * @return trp_stmt(module, exception, exception)
	 * @throws Exception
	 */
	public	static	ContextAnnotation	trp_stmt(AstCirNode location) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {
			return new ContextAnnotation(
					ContextAnnotationClass.trp_stmt, location.module_of(), 
					ContextMutation.trap_value, ContextMutation.trap_value);
		}
	}
	/**
	 * @param expression	the expression at which the data error is injected
	 * @param orig_value	the original value holds by the expression at that point
	 * @param muta_value	the mutated value hold by the expression at the point
	 * @return				set_expr(expression, orig_value, muta_value)
	 * @throws Exception
	 */
	public	static	ContextAnnotation	set_expr(AstCirNode expression, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(expression == null || !expression.is_expression_node()) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.set_expr, expression, orig_value, muta_value);
		}
	}
	/**
	 * @param expression	the expression at which the data state error is injected
	 * @param orig_value	the original value holds by the expression at that point
	 * @param muta_value	the difference to be increased to the expression at node
	 * @return				set_expr(expression, orig_value, muta_value)
	 * @throws Exception
	 */
	public	static	ContextAnnotation	inc_expr(AstCirNode expression, 
			SymbolExpression orig_value, SymbolExpression difference) throws Exception {
		if(expression == null || !expression.is_expression_node()) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.inc_expr, expression, orig_value, difference);
		}
	}
	/**
	 * @param expression	the expression at which the data state error is injected
	 * @param orig_value	the original value holds by the expression at that point
	 * @param muta_value	the difference to be increased to the expression at node
	 * @return				set_expr(expression, orig_value, muta_value)
	 * @throws Exception
	 */
	public	static	ContextAnnotation	xor_expr(AstCirNode expression, 
			SymbolExpression orig_value, SymbolExpression difference) throws Exception {
		if(expression == null || !expression.is_expression_node()) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.xor_expr, expression, orig_value, difference);
		}
	}
	/**
	 * @param statement
	 * @param orig_value
	 * @param muta_value
	 * @return
	 * @throws Exception
	 */
	public	static	ContextAnnotation	set_refr(AstCirNode statement, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.set_refr, statement, orig_value, muta_value);
		}
	}
	/**
	 * @param statement
	 * @param orig_value
	 * @param muta_value
	 * @return
	 * @throws Exception
	 */
	public	static	ContextAnnotation	inc_refr(AstCirNode statement, 
			SymbolExpression orig_value, SymbolExpression difference) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.inc_refr, statement, orig_value, difference);
		}
	}
	/**
	 * @param statement
	 * @param orig_value
	 * @param muta_value
	 * @return
	 * @throws Exception
	 */
	public	static	ContextAnnotation	xor_refr(AstCirNode statement, 
			SymbolExpression orig_value, SymbolExpression difference) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			return new ContextAnnotation(ContextAnnotationClass.xor_refr, statement, orig_value, difference);
		}
	}
	
}
