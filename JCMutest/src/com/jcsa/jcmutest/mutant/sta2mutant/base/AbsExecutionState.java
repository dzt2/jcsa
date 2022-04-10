package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	It describes the abstract execution state defined in mutation testing.		<br>
 * 	<br>
 * 	<code>
 * 	AbsExecutionState				state_class(_store, lvalue, rvalue)			<br>
 * 	|--	AbsConditionState			state_class(statement; lvalue, rvalue)		<br>
 * 	|--	|--	AbsCoverTimesState		cov_stmt(statement;  min_times, max_times)	<br>
 * 	|--	|--	AbsConstraintState		eva_cond(statement;  condition, must_need)	<br>
 * 	|--	AbsPathErrorState			state_class(statement; lvalue, rvalue)		<br>
 * 	|--	|--	AbsBlockErrorState		set_stmt(statement;  orig_exec, muta_exec)	<br>
 * 	|--	|--	AbsFlowsErrorState		set_flow(statement;  orig_next, muta_next)	<br>
 * 	|--	|--	AbsTrapsErrorState		trp_stmt(statement;  exception, exception)	<br>
 * 	|--	AbsDataErrorState			state_class(expression;lvalue, rvalue)		<br>
 * 	|--	|--	AbsValueErrorState		set_expr(expression; ori_value, muta_value)	<br>
 * 	|--	|--	AbsIncreErrorState		inc_expr(expression; ori_value, difference)	<br>
 * 	|--	|--	AbsBixorErrorState		xor_expr(expression; ori_value, difference)	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class AbsExecutionState {
	
	/* definition */
	/** the class of the execution state **/
	private	AbsExecutionClass	_class;
	/** the store of the execution state **/
	private	AbsExecutionStore	_store;
	/** the left parameter to define it **/
	private	SymbolExpression	lvalue;
	/** the righ-parameter to define it **/
	private	SymbolExpression	rvalue;
	/**
	 * @param _class the class of the execution state
	 * @param _store the store of the execution state
	 * @param lvalue the left parameter to define it
	 * @param rvalue the righ-parameter to define it
	 * @throws Exception
	 */
	protected AbsExecutionState(AbsExecutionClass _class,
			AbsExecutionStore _store, 
			SymbolExpression lvalue,
			SymbolExpression rvalue) throws Exception {
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
			this._class = _class; this._store = _store;
			this.lvalue = StateMutations.evaluate(lvalue, null, null);
			this.rvalue = StateMutations.evaluate(rvalue, null, null);
		}
	}
	
	/* getters */
	/**
	 * @return the class of the execution state
	 */
	public 	AbsExecutionClass 	get_state_class() 	{ return this._class; }
	/**
	 * @return the store of the execution state
	 */
	public 	AbsExecutionStore	get_state_store()	{ return this._store; }
	/**
	 * @return the left parameter to define it
	 */
	public	SymbolExpression	get_loperand()		{ return this.lvalue; }
	/**
	 * @return the right parameter to define it
	 */
	public 	SymbolExpression	get_roperand()		{ return this.rvalue; }
	/**
	 * @return the execution point where the state is defined
	 */
	public	CirExecution		get_execution()		{ return this._store.get_exe_location(); }
	@Override
	public String toString() {
		return this._class + "(" + this._store + "; " + this.lvalue + ", " + this.rvalue + ")";
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AbsExecutionState) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* classify */
	/**
	 * @return whether the state is conditional
	 */
	public 	boolean is_conditional() { 
		switch(this._class) {
		case cov_time:
		case eva_cond:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether the state is path-errors
	 */
	public	boolean	is_path_error() {
		switch(this._class) {
		case set_stmt:
		case set_flow:	
		case trp_stmt:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether the state is data-errors
	 */
	public	boolean	is_data_error() {
		switch(this._class) {
		case set_expr:
		case inc_expr:	
		case xor_expr:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether the state is abstract error state,
	 */
	public 	boolean is_abst_error() { 
		return this.is_path_error() || this.is_data_error(); 
	}
	
	/* factory */
	/**
	 * @param statement	the statement location to be covered by the state
	 * @param min_times	the minimal times for executing the statement
	 * @param max_times	the maximal times for executing the statement
	 * @return			cov_time(statement; min_times, max_times)
	 * @throws Exception
	 */
	public	static	AbsCoverTimesState	cov_time(CirStatement statement, int min_times, int max_times) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(min_times > max_times || max_times <= 0) {
			throw new IllegalArgumentException(min_times + " : " + max_times);
		}
		else {
			return new AbsCoverTimesState(AbsExecutionStore.cir_store(statement), min_times, max_times);
		}
	}
	/**
	 * @param statement	the statement location to evaluate the condition
	 * @param condition	the symbolic condition to be evaluated in the statement
	 * @param must_need	True (being satisfied always); False (met at least once)
	 * @return			eva_cond(statement; condition, must_need)
	 * @throws Exception
	 */
	public	static	AbsConstraintState	eva_cond(CirStatement statement, Object condition, boolean must_need) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new AbsConstraintState(AbsExecutionStore.cir_store(statement),
						SymbolFactory.sym_condition(condition, true), must_need);
		}
	}
	/**
	 * @param statement	the statement location where the trap is arised
	 * @return			trp_stmt(statement; exception, exception);
	 * @throws Exception
	 */
	public 	static	AbsTrapsErrorState	trp_stmt(CirStatement statement) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else {
			return new AbsTrapsErrorState(AbsExecutionStore.cir_store(statement));
		}
	}
	/**
	 * @param statement	the statement location where the trap is arising
	 * @param muta_exec	True (executed) False (not executed) in mutation
	 * @return			set_stmt(statement; !muta_exec, muta_exec)
	 * @throws Exception
	 */
	public 	static	AbsBlockErrorState	set_stmt(CirStatement statement, boolean muta_exec) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else {
			return new AbsBlockErrorState(AbsExecutionStore.cir_store(statement), muta_exec);
		}
	}
	/**
	 * @param statement	the statement from which the flow is mutated
	 * @param orig_next	the statement executed in next in the original
	 * @param muta_next	the statement executed in next in the mutation
	 * @return			set_flow(statement; orig_next, muta_next)
	 * @throws Exception
	 */
	public	static	AbsFlowsErrorState	set_flow(CirStatement statement, CirExecution orig_next, CirExecution muta_next) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(orig_next == null) {
			throw new IllegalArgumentException("Invalid orig_next: null");
		}
		else if(muta_next == null) {
			throw new IllegalArgumentException("Invalid muta_next: null");
		}
		else {
			return new AbsFlowsErrorState(AbsExecutionStore.cir_store(statement), orig_next, muta_next);
		}
	}
	/**
	 * @param store	the expression storage locations 
	 * @return		the symbolic original expression
	 * @throws Exception
	 */
	private	static	SymbolExpression	ori_expr(AbsExecutionStore store) throws Exception {
		CirExpression expression = (CirExpression) store.get_cir_location();
		if(store.get_store_class() == AbsExecutionLType.refr) {
			if(StateMutations.is_assigned(expression)) {
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				expression = statement.get_rvalue();
			}
			return SymbolFactory.sym_expression(expression);
		}
		else if(store.get_store_class() == AbsExecutionLType.bool) {
			return SymbolFactory.sym_condition(expression, true);
		}
		else {
			return SymbolFactory.sym_expression(expression);
		}
	}
	/**
	 * @param store	[bool, argv, expr, refr]
	 * @param value	the value to replace the original version
	 * @return		set_expr(expression; ori_value, mut_value)
	 * @throws Exception
	 */
	public	static	AbsValueErrorState	set_expr(AbsExecutionStore store, Object value) throws Exception {
		if(store == null || !store.is_expression()) {
			throw new IllegalArgumentException("Invalid store: " + store);
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}
		else {
			SymbolExpression orig_value, muta_value;
			orig_value = ori_expr(store);
			if(store.get_store_class() == AbsExecutionLType.bool) {
				muta_value = SymbolFactory.sym_condition(value, true);
			}
			else {
				muta_value = SymbolFactory.sym_expression(value);
			}
			return new AbsValueErrorState(store, orig_value, muta_value);
		}
	}
	/**
	 * @param store			[argv, expr, refr]
	 * @param difference
	 * @return				inc_expr(expression; orig_value, difference).
	 * @throws Exception
	 */
	public 	static	AbsIncreErrorState	inc_expr(AbsExecutionStore store, Object difference) throws Exception {
		if(store == null || !store.is_expression()) {
			throw new IllegalArgumentException("Invalid store as " + store);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			SymbolExpression orig_value, muta_value;
			orig_value = ori_expr(store);
			muta_value = SymbolFactory.sym_expression(difference);
			return new AbsIncreErrorState(store, orig_value, muta_value);
		}
	}
	/**
	 * @param store			[argv, expr, refr]
	 * @param difference
	 * @return				xor_expr(expression; orig_value, difference).
	 * @throws Exception
	 */
	public 	static	AbsBixorErrorState	xor_expr(AbsExecutionStore store, Object difference) throws Exception {
		if(store == null || !store.is_expression()) {
			throw new IllegalArgumentException("Invalid store as " + store);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			SymbolExpression orig_value, muta_value;
			orig_value = ori_expr(store);
			muta_value = SymbolFactory.sym_expression(difference);
			return new AbsBixorErrorState(store, orig_value, muta_value);
		}
	}
	
}
