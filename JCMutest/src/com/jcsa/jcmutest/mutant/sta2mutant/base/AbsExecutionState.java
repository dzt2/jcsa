package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	It specifies the abstract execution state defined in mutation testing.		<br>
 * 	<br>
 * 	<code>
 * 	AbsExecutionState				{state_class, location, state_value}		<br>
 * 	|--	AbsCoverTimeState			{covt, statement,  min_times}				<br>
 * 	|--	AbsConstrainState			{eval, statement,  condition}				<br>
 * 	|--	AbsDataValueState			{expr, expression, sym_value}				<br>
 * 	|--	AbsStatementState			{exec, statement,  sym_value}				<br>
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
	/**
	 * @param state_class the category of this abstract state
	 * @param state_store the location to preserve this state
	 * @param state_value the symbolic parameter of the state
	 * @throws Exception
	 */
	protected AbsExecutionState(AbsExecutionClass state_class,
			AbsExecutionStore state_store,
			SymbolExpression state_value) throws Exception {
		if(state_class == null) {
			throw new IllegalArgumentException("Invalid state_class: null");
		}
		else if(state_store == null) {
			throw new IllegalArgumentException("Invalid state_store: null");
		}
		else if(state_value == null) {
			throw new IllegalArgumentException("Invalid state_value: null");
		}
		else {
			this.state_class = state_class;
			this.state_store = state_store;
			this.state_value = StateMutations.evaluate(state_value, null, null);
		}
	}
	/**
	 * @return the clone of this state
	 */
	protected abstract AbsExecutionState copy() throws Exception;
	
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
	
	/* general */
	@Override
	public String				toString() {
		return this.state_class + "(" + this.state_store + ", " + this.state_value + ")";
	}
	@Override
	public int					hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean				equals(Object obj) {
		if(obj instanceof AbsExecutionState) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	@Override
	public AbsExecutionState	clone() {
		try {
			return this.copy();
		}
		catch(Exception ex) {
			return null;
		}
	}
	
	/* factory */
	
	
	
	
	
	
}
