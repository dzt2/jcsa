package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;


/**
 * 	It describes the contextual state in the abstract execution of mutation.	<br>
 * 	<br>
 * 	<code>
 * 	AstContextState					[category, location, loperand, roperand]	<br>
 * 	|--	AstConditionState			[category, location, loperand, roperand]	<br>
 * 	|--	|--	AstSeedMutantState		[sed_muta, location,  mutant_ID, operators]	<br>
 * 	|--	|--	AstCoverTimesState		[cov_time, statement, min_times, max_times]	<br>
 * 	|--	|--	AstConstraintState		[eva_cond, statement, condition, must_need]	<br>
 * 	|--	AstPathErrorState			[category, statement, loperand, roperand]	<br>
 * 	|--	|--	AstBlockErrorState		[mut_stmt, statement, orig_exec, muta_exec]	<br>
 * 	|--	|--	AstFlowsErrorState		[mut_flow, statement, orig_next, muta_next]	<br>
 * 	|--	|--	AstTrapsErrorState		[mut_trap, module,    exception, exception]	<br>
 * 	|--	AstDataErrorState			[category,expression, loperand, roperand]	<br>
 * 	|--	|--	AstValueErrorState		[set_expr,expression, orig_expr, muta_expr]	<br>
 * 	|--	|--	AstIncreErrorState		[inc_expr,expression, orig_expr, different]	<br>
 * 	|--	|--	AstBixorErrorState		[xor_expr,expression, orig_expr, different]	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class AstContextState {
	
	/* attributes */
	/** the category of the mutation in context-based form **/
	private		AstContextClass		category;
	/** the AstCirNode location where mutation is enclosed **/
	private		AstCirNode			location;
	/** the original state of the location depends on type **/
	private		SymbolExpression	loperand;
	/** the mutation state of the location depends on type **/
	private		SymbolExpression	roperand;
	
	/* getters */
	/**	
	 * @return	the category of the mutation in context-based form
	 */
	public	AstContextClass		get_category()	{ return this.category; }
	/**
	 * @return	the AstCirNode location where mutation is enclosed
	 */
	public	AstCirNode			get_location()	{ return this.location; }
	/**
	 * @return	the original state of the location depends on type
	 */
	public	SymbolExpression	get_loperand()	{ return this.loperand; }
	/**
	 * @return	the mutation state of the location depends on type
	 */
	public	SymbolExpression	get_roperand()	{ return this.roperand; }
	
	/* general */
	public	String				toString()		{
		return this.category + "(" + this.location.get_node_id() + 
				"; " + this.loperand + ", " + this.roperand + ")";
	}
	@Override
	public	int					hashCode()		{ return this.toString().hashCode(); }
	@Override
	public	boolean				equals(Object obj) {
		if(obj instanceof AstContextState) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* classifier */
	/**
	 * @return whether the mutation represents a constraint
	 */
	public	boolean				is_conditional()	{
		switch(this.category) {
		case sed_muta:
		case cov_time:
		case eva_cond:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether the mutation refers to statement-error
	 */
	public	boolean				is_path_error()		{
		switch(this.category) {
		case mut_stmt:
		case mut_flow:
		case mut_trap:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether the mutation refers to expression-error
	 */
	public	boolean				is_data_error()		{
		switch(this.category) {
		case set_expr:
		case inc_expr:
		case xor_expr:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether the mutation refers to any possible error (change)
	 */
	public 	boolean				is_abst_error()		{ return this.is_data_error() || this.is_path_error(); }
	
	/* construct */
	/**
	 * @param category	the category of the mutation in context-based form
	 * @param location	the AstCirNode location where mutation is enclosed
	 * @param loperand	the original state of the location depends on type
	 * @param roperand	the mutation state of the location depends on type
	 * @throws Exception
	 */
	protected	AstContextState(AstContextClass category, AstCirNode location, 
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
			this.loperand = loperand; this.roperand = roperand;
		}
	}
	/**
	 * @param location	the location in which the mutation is directly seeded
	 * @param mutant
	 * @return			sed_muta(location;	mutant_ID,	operators)
	 * @throws Exception
	 */
	public static AstSeedMutantState	sed_muta(AstCirNode location, Mutant mutant) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant as null");
		}
		else { 	return new AstSeedMutantState(location, mutant);  }
	}
	/**
	 * @param statement	the statement to be executed at the coverage condition
	 * @param min_times	the minimal times for executing the target statement
	 * @param max_times	the maximal times for executing the target statement
	 * @return			cov_time(statement, min_times, max_times)
	 * @throws Exception
	 */
	public static AstCoverTimesState	cov_time(AstCirNode statement, int min_times, int max_times) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(min_times > max_times || max_times < 0) {
			throw new IllegalArgumentException(min_times + " --> " + max_times);
		}
		else { return new AstCoverTimesState(statement, min_times, max_times); }
	}
	/**
	 * @param statement	the statement in which the condition is evaluated
	 * @param condition	the condition to be satisfied at the given points
	 * @param must_need	True (always met) False (satisfied at least once)
	 * @return			eva_cond(statement, condition, must_need)
	 * @throws Exception
	 */
	public static AstConstraintState	eva_cond(AstCirNode statement, Object condition, boolean must_need) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else { return new AstConstraintState(statement, SymbolFactory.sym_condition(condition, true), must_need); }
	}
	/**
	 * @param statement
	 * @param muta_exec
	 * @return mut_stmt(statement, !muta_exec, muta_exec)
	 * @throws Exception
	 */
	public static AstBlockErrorState	mut_stmt(AstCirNode statement, boolean muta_exec) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else {
			return new AstBlockErrorState(statement, !muta_exec, muta_exec);
		}
	}
	/**
	 * @param statement
	 * @param orig_next
	 * @param muta_next
	 * @return	mut_flow(statement; orig_next, muta_next)
	 * @throws Exception
	 */
	public static AstFlowsErrorState	mut_flow(AstCirNode statement, AstCirNode orig_next, AstCirNode muta_next) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(orig_next == null) {
			throw new IllegalArgumentException("Invalid orig_next: null");
		}
		else if(muta_next == null) {
			throw new IllegalArgumentException("Invalid muta_next: null");
		}
		else { return new AstFlowsErrorState(statement, orig_next, muta_next); }
	}
	/**
	 * @param location
	 * @return mut_trap(module, exception, exception)
	 * @throws Exception
	 */
	public static AstTrapsErrorState	mut_trap(AstCirNode location) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {  return new AstTrapsErrorState(location.module_of());  }
	}
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return
	 * @throws Exception
	 */
	public static AstValueErrorState	set_expr(AstCirNode expression, 
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
			return new AstValueErrorState(expression, orig_value, muta_value);
		}
	}
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return
	 * @throws Exception
	 */
	public static AstIncreErrorState	inc_expr(AstCirNode expression, 
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
			return new AstIncreErrorState(expression, orig_value, difference);
		}
	}
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return
	 * @throws Exception
	 */
	public static AstBixorErrorState	xor_expr(AstCirNode expression, 
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
			return new AstBixorErrorState(expression, orig_value, difference);
		}
	}
	
}
