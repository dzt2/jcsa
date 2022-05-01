package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;


/**
 * 	It describes the abstract execution state in term of syntactic context.
 * 	<br>
 * 	<code>
 * 	AstContextState					[category, location,  l_operand, r_operand]	<br>
 * 	|--	AstConditionState			[category, location,  l_operand, r_operand]	<br>
 * 	|--	|--	AstConstraintState		[eva_cond, statement, condition, must_need]	<br>
 * 	|--	|--	AstCoverTimesState		[cov_time, statement, min_times, max_times]	<br>
 * 	|--	|--	AstSeedMutantState		[sed_muta, location,  mutant_ID, operators]	<br>
 * 	|--	AstAbstErrorState			[category, location,  l_operand, r_operand]	<br>
 * 	|--	|--	AstBlockErrorState		[set_stmt, statement, orig_exec, muta_exec]	<br>
 * 	|--	|--	AstFlowsErrorState		[set_flow, statement, orig_next, muta_next]	<br>
 * 	|--	|--	AstValueErrorState		[set_expr, expression,orig_expr, muta_expr]	<br>
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
	 * @return whether the mutation refers to any possible error (change)
	 */
	public 	boolean				is_abst_error()	{ 
		switch(this.category) {
		case set_stmt:
		case set_flow:
		case set_expr:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether the state is connected with expression location
	 */
	public	boolean				is_expr_state()	{ return this.location.is_expression_node(); }
	/**
	 * @return whether the state is connected with statement locations
	 */
	public	boolean				is_stmt_state()	{ return this.location.is_statement_node(); }
	
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
	
}
