package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	It describes the context-based syntactic mutation by specified class and
 * 	AstCirNode as location, including two state values as change parameters.<br>
 * 	<br>
 * 	<code>
 * 	ContextMutation				{category, location; loperand, roperand}	<br>
 * 	|--	CovTimeMutation			cov_time(statement;  min_times, max_times)	<br>
 * 	|--	EvaCondMutation			eva_cond(statement;	 condition, must_need)	<br>
 * 	|--	SedMutaMutation			sed_muta(statement;	 muta_clas, parameter)	<br>
 * 	|--	
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class ContextMutation {
	
	/* definitions */
	/** the category of the mutation in context-based form **/
	private		ContextMutaClass	category;
	/** the AstCirNode location where mutation is enclosed **/
	private		AstCirNode			location;
	/** the original state of the location depends on type **/
	private		SymbolExpression	loperand;
	/** the mutation state of the location depends on type **/
	private		SymbolExpression	roperand;
	/**
	 * @param category the category of the mutation in context-based form
	 * @param location the AstCirNode location where mutation is enclosed
	 * @param loperand the original state of the location depends on type
	 * @param roperand the mutation state of the location depends on type
	 * @throws Exception
	 */
	protected	ContextMutation(ContextMutaClass category,
			AstCirNode location, SymbolExpression loperand, 
			SymbolExpression roperand) throws Exception {
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
	
	/* getters */
	/**
	 * @return the category of the mutation in context-based form
	 */
	public	ContextMutaClass	get_category()	{ return this.category; }
	/**
	 * @return the AstCirNode location where mutation is enclosed
	 */
	public	AstCirNode			get_location()	{ return this.location; }
	/**
	 * @return the original state of the location depends on type
	 */
	public	SymbolExpression	get_loperand()	{ return this.loperand; }
	/**
	 * @return the mutation state of the location depends on type
	 */
	public	SymbolExpression	get_roperand()	{ return this.roperand; }
	@Override
	public	String				toString()		{
		return this.category + "(" + this.location.get_node_id() + 
				"; " + this.loperand + ", " + this.roperand + ")";
	}
	@Override
	public	int					hashCode()		{ return this.toString().hashCode(); }
	@Override
	public	boolean				equals(Object obj) {
		if(obj instanceof ContextMutation) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
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
		case set_stmt:
		case set_flow:
		case trp_stmt:	return true;
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
	
	/* factory methods */
	/**
	 * @param location
	 * @param mutant 
	 * @return sed_muta(location; mutant.ID, mutant.operator)
	 * @throws Exception
	 */
	public static SedMutaMutation	sed_muta(AstCirNode location, Mutant mutant) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant as null");
		}
		else { 	return new SedMutaMutation(location, mutant); 	}
	}
	/**
	 * @param location
	 * @param min_times
	 * @param max_times
	 * @return cov_time(statement; min_times, max_times)
	 * @throws Exception
	 */
	public static CovTimeMutation	cov_time(AstCirNode location, int min_times, int max_times) throws Exception {
		if(location == null || !location.is_statement_node()) {
			throw new IllegalArgumentException("Invalid location: " + location);
		}
		else if(min_times > max_times || max_times < 0) {
			throw new IllegalArgumentException(min_times + " --> " + max_times);
		}
		else { 	return new CovTimeMutation(location, min_times, max_times);  }
	}
	/**
	 * @param location
	 * @param condition
	 * @param must_need
	 * @return eva_cond(statement; condition, must_need)
	 * @throws Exception
	 */
	public static EvaCondMutation	eva_cond(AstCirNode location, Object condition, boolean must_need) throws Exception {
		if(location == null || !location.is_statement_node()) {
			throw new IllegalArgumentException("Invalid location: " + location);
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else { return new EvaCondMutation(location, condition, must_need); }
	}
	/**
	 * @param location
	 * @param orig_exec
	 * @param muta_exec
	 * @return set_stmt(statement; orig_exec, muta_exec);
	 * @throws Exception
	 */
	public static SetStmtMutation	set_stmt(AstCirNode location, boolean orig_exec, boolean muta_exec) throws Exception {
		if(location == null || !location.is_statement_node()) {
			throw new IllegalArgumentException("Invalid location: " + location);
		}
		else {  return new SetStmtMutation(location, orig_exec, muta_exec);  }
	}
	/**
	 * @param location
	 * @param orig_next
	 * @param muta_next
	 * @return set_flow(location; orig_next, muta_next)
	 * @throws Exception
	 */
	public static SetFlowMutation	set_flow(AstCirNode location, AstCirNode orig_next, AstCirNode muta_next) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(orig_next == null) {
			throw new IllegalArgumentException("Invalid orig_next: null");
		}
		else if(muta_next == null) {
			throw new IllegalArgumentException("Invalid muta_next: null");
		}
		else { return new SetFlowMutation(location, orig_next, muta_next); }
	}
	/**
	 * @param location
	 * @return trp_stmt(trans_root, true_value, trap_value)
	 * @throws Exception
	 */
	public static TrpStmtMutation	trp_stmt(AstCirNode location) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else { return new TrpStmtMutation(location); }
	}
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return	set_expr(expression; orig_value, muta_value)
	 * @throws Exception
	 */
	public static SetExprMutation	set_expr(AstCirNode expression, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(expression == null || !expression.is_expression_node()) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else { return new SetExprMutation(expression, orig_value, muta_value); }
	}
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return inc_expr(expression; orig_value, muta_value)
	 * @throws Exception
	 */
	public static IncExprMutation	inc_expr(AstCirNode expression, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(expression == null || !expression.is_expression_node()) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else { return new IncExprMutation(expression, orig_value, muta_value); }
	}
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return xor_expr(expression; orig_value, muta_value)
	 * @throws Exception
	 */
	public static XorExprMutation	xor_expr(AstCirNode expression, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(expression == null || !expression.is_expression_node()) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else { return new XorExprMutation(expression, orig_value, muta_value); }
	}
	/**
	 * @param mutant
	 * @param location
	 * @return it creates the compositional mutation as output
	 * @throws Exception
	 */
	public static AstContextMutation ast_mutation(Mutant mutant, AstCirNode location) throws Exception {
		return new AstContextMutation(mutant, location);
	}
	
}
