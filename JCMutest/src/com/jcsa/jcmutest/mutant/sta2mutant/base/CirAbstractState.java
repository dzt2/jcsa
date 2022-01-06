package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It describes an abstract execution state established at some store unit in
 * the program point with some established value.
 * <br>
 * <code>
 * 	CirAbstractState			execution[store] 	:= 	{value}					<br>
 * 	<br>
 * 	|--	CirConditionState		execution[stmt]		:=	{cov_time|eva_expr}		<br>
 * 	|--	|--	CirCoverTimesState	[stmt:statement]	:=	{cov_time:int_times}	<br>
 * 	|--	|--	CirConstraintState	[stmt:statement]	:=	{eva_cond:condition}	<br>
 * 	<br>
 * 	|--	CirPathErrorState		execution[stmt]		:=	{set_stmt|flow|trap}	<br>
 * 	|--	|--	CirBlockErrorState	[stmt:statement]	:=	{set_stmt:bool:bool}	<br>
 * 	|--	|--	CirFlowsErrorState	[stmt:statement]	:=	{set_flow:exec:exec}	<br>
 * 	|--	|--	CirTrapsErrorState	[stmt:statement]	:=	{set_trap:exec:expt}	<br>
 * 	<br>
 * 	|--	CirDataErrorState		execution[expr]		:=	{set|inc|xor_expr}		<br>
 * 	|--	|--	CirValueErrorState	[usep|defp|vdef]	:=	{set_expr:orig:muta}	<br>
 * 	|--	|--	CirIncreErrorState	[usep|defp|vdef]	:=	{inc_expr:base:diff}	<br>
 * 	|--	|--	CirBixorErrorState	[usep|defp|vdef]	:=	{xor_expr:base:diff}	<br>
 * </code>
 * <br>
 * @author yukimula
 *
 */
public abstract class CirAbstractState {
	
	/* attributes */
	private CirExecution	point;
	private CirStateStore	store;
	private CirStateValue	value;
	protected CirAbstractState(CirExecution execution,
			CirStateStore store, CirStateValue value) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(store == null) {
			throw new IllegalArgumentException("Invalid store: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}
		else {
			this.point = execution;
			this.store = store;
			this.value = value;
		}
	}
	
	/* getters */
	/**
	 * @return the execution point where the state is defined on
	 */
	public CirExecution 	get_execution() { return this.point; }
	/**
	 * @return the statement where this state is established on
	 */
	public CirStatement		get_statement() { return this.point.get_statement(); }
	/**
	 * @return the store unit to preserve value defining the state
	 */
	public CirStateStore 	get_store()	{ return this.store; }
	/**
	 * @return the value being preserved in this execution state
	 */
	public CirStateValue	get_value()	{ return this.value; }
	@Override
	public String toString() {
		return this.point + "[" + this.store + "] := " + this.value;
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CirAbstractState) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* factory */
	/**
	 * @param execution
	 * @param int_times	(>= 2)
	 * @return	execution[stmt:statement] := {cov_time:int_times}
	 * @throws Exception
	 */
	public static CirCoverTimesState cov_time(CirExecution execution, int int_times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution; null");
		}
		else if(int_times <= 1) {
			throw new IllegalArgumentException("Invalid: " + int_times);
		}
		else {
			return new CirCoverTimesState(execution, int_times);
		}
	}
	
	
	
}
