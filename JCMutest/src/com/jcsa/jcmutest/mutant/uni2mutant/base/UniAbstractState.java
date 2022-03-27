package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	It describes a uniformed abstract state being evaluated at some program code
 * 	location (combining ast_node, cir_node and cir_execution tuple).<br>
 * 	<br>
 * 	
 * 	<code>
 * 	UniAbstractState				[st_class, c_loct; lsh_value, rsh_value]	<br>
 * 	|--	UniConditionState			[st_class, c_stmt; lsh_value, rsh_value]	<br>
 * 	|--	|--	UniCoverTimesState		[cov_time, c_stmt; min_times, max_times]	<br>
 * 	|--	|--	UniConstraintState		[eva_cond, c_stmt; condition, must_need]	<br>
 * 	|--	|--	UniSeedMutantState		[sed_muta, c_stmt; mutant_ID, clas_oprt]	<br>
 * 	|--	UniPathErrorState			[s_class,  c_stmt; lsh_value, rsh_value]	<br>
 * 	|--	|--	UniBlockErrorState		[mut_stmt, c_stmt; orig_exec, muta_exec]	<br>
 * 	|--	|--	UniFlowsErrorState		[mut_flow, c_stmt; orig_next, muta_next]	<br>
 * 	|--	|--	UniTrapsErrorState		[trp_stmt, c_stmt; exception, exception]	<br>
 * 	|--	UniDataErrorState			[st_class, c_expr; orig_expr, parameter]	<br>
 * 	|--	|--	UniValueErrorState		[set_expr, c_expr; orig_expr, muta_expr]	<br>
 * 	|--	|--	UniIncreErrorState		[inc_expr, c_expr; orig_expr, different]	<br>
 * 	|--	|--	UniBixorErrorState		[xor_expr, c_expr; orig_expr, different]	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *	
 */
public abstract class UniAbstractState {
	
	/* definitions */
	/** the category of this state **/
	private	UniAbstractClass	_class;
	/** the store-unit of the state **/
	private	UniAbstractStore	_store;
	/** the left-operand to define **/
	private	SymbolExpression	lvalue;
	/** the right-operand to define **/
	private	SymbolExpression	rvalue;
	
	/* constructor */
	/**
	 * It creates an abstract symbolic state for mutation testing.
	 * @param _class	the category of this state
	 * @param _store	the store-unit of the state
	 * @param lvalue	the left-operand to define
	 * @param rvalue	the right-operand to define
	 * @throws Exception
	 */
	protected UniAbstractState(UniAbstractClass _class, UniAbstractStore _store, 
			SymbolExpression lvalue, SymbolExpression rvalue) throws Exception {
		if(_class == null) {
			throw new IllegalArgumentException("Invalid _class: null");
		}
		else if(_store == null) {
			throw new IllegalArgumentException("Invalid _store: null");
		}
		else if(lvalue == null) {
			throw new IllegalArgumentException("Invalid lvalue: null");
		}
		else if(rvalue == null) {
			throw new IllegalArgumentException("Invalid rvalue: null");
		}
		else {
			this._class = _class;
			this._store = _store;
			this.lvalue = UniAbstractStates.evaluate(lvalue, null, null);
			this.rvalue = UniAbstractStates.evaluate(rvalue, null, null);
		}
	}
	
	/* getters */
	/**
	 * @return the execution where this state is evaluated and defined
	 */
	public	CirExecution		get_execution()		{ return this._store.get_exe_location(); }
	/**
	 * @return the category of this state
	 */
	public	UniAbstractClass	get_state_class()	{ return this._class; }
	/**
	 * @return the store-unit of the state
	 */
	public	UniAbstractStore	get_state_store()	{ return this._store; }
	/**
	 * @return the left-operand to define
	 */
	public	SymbolExpression	get_loperand()		{ return this.lvalue; }
	/**
	 * @return the right-operand to define
	 */
	public 	SymbolExpression	get_roperand()		{ return this.rvalue; }
	@Override
	public 	String	toString() {
		return this._class + "(" + this._store + "; " + this.lvalue + ", " + this.rvalue + ")";
	}
	@Override
	public	int		hashCode()	{ return this.toString().hashCode(); }
	@Override
	public	boolean	equals(Object obj) {
		if(obj instanceof UniAbstractState) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	
	
	
	
	
}
