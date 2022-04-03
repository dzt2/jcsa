package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	It specifies the abstract execution state defined in mutation testing.		<br>
 * 	<br>
 * 	<code>
 * 	AbsExecutionState				{state_class, location, state_value}		<br>
 * 	|--	AbsCoverTimesState			{covt, statement,  min_times}				<br>
 * 	|--	AbsReachTimesState			{rect, statement,  max_times}				<br>
 * 	|--	AbsConstraintState			{eval, statement,  condition}				<br>
 * 	|--	AbsValidationState			{vald, statement,  condition}				<br>
 * 	|--	AbsExpressionState			{expr, expression, sym_value}				<br>
 * 	|--	AbsReferencesState			{refr, reference,  sym_value}				<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class AbsExecutionState {
	
	/* attributes */
	/** the category of this abstract state **/
	private	AbsExecutionClass	state_class;
	/** the location to preserve this state **/
	private	AbsExecutionStore	state_store;
	/** the symbolic parameter of the state **/
	private	SymbolExpression	state_value;
	
	/* getters */
	/**
	 * @return the category of this abstract state
	 */
	public AbsExecutionClass	get_state_class() 	{ return this.state_class; }
	/**
	 * @return the location to preserve this state
	 */
	public AbsExecutionStore	get_state_store()	{ return this.state_store; }
	/**
	 * @return the symbolic parameter of the state
	 */
	public SymbolExpression		get_state_value()	{ return this.state_value; }
	/** 
	 * @return the category of this state location
	 */
	public AbsExecutionLType	get_store_class()	{ return this.state_store.get_store_class(); }
	/**
	 * @return the syntactic location of the state
	 */
	public AstNode				get_ast_location()	{ return this.state_store.get_ast_location(); }
	/**
	 * @return the intermediate point of the state
	 */
	public CirNode				get_cir_location()	{ return this.state_store.get_cir_location(); }
	/**
	 * @return the execution location of the state
	 */
	public CirExecution			get_exe_location()	{ return this.state_store.get_exe_location(); }
	
}
