package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

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
 * 	|--	|--	CirNonCoveredState	[stmt:statement]	:=	{non_stmt:execution}	<br>
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
	
	/* constraint */
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
		else if(int_times <= 0) {
			throw new IllegalArgumentException("Invalid: " + int_times);
		}
		else {
			return new CirCoverTimesState(execution, int_times);
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @param value
	 * @return execution[stmt:statement] := {eva_cond:condition as value}
	 * @throws Exception
	 */
	public static CirConstraintState eva_cond(CirExecution execution, Object condition, boolean value) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution; null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirConstraintState(execution, condition, value);
		}
	}
	/**
	 * @param execution
	 * @return it requires the execution point should not be executed
	 * @throws Exception
	 */
	public static CirNonCoveredState non_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirNonCoveredState(execution);
		}
	}
	
	/* path error */
	/**
	 * @param execution
	 * @param execute
	 * @return	[stmt:statement] <== {set_stmt:!execute:execute}
	 * @throws Exception
	 */
	public static CirBlockErrorState set_stmt(CirExecution execution, boolean execute) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(execution.get_statement() instanceof CirTagStatement) {
			throw new IllegalArgumentException(execution.toString());
		}
		else {
			return new CirBlockErrorState(execution, execute);
		}
	}
	/**
	 * @param oflow	the execution flow from source statement that is executed in original version
	 * @param mflow	the execution flow from source statement that is executed in mutation version
	 * @return		[stmt:statement] <== {set_flow:oflow:mflow}
	 * @throws Exception
	 */
	public static CirFlowsErrorState set_flow(CirExecutionFlow oflow, CirExecutionFlow mflow) throws Exception {
		if(oflow == null) {
			throw new IllegalArgumentException("Invalid oflow: null");
		}
		else if(mflow == null) {
			throw new IllegalArgumentException("Invalid mflow: null");
		}
		else { return new CirFlowsErrorState(oflow, mflow); }
	}
	/**
	 * @param execution
	 * @return [stmt:statement] <== {set_trap:exec:exception}
	 * @throws Exception
	 */
	public static CirTrapsErrorState set_trap(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirTrapsErrorState(execution.get_graph().get_exit());
		}
	}
	
	/* data error */
	/**
	 * @param expression
	 * @param muta_value
	 * @return	[usep|defp:expression] <== {set_expr:orig_value:muta_value}
	 * @throws Exception
	 */
	public static CirValueErrorState set_expr(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			SymbolExpression ovalue, mvalue;
			if(StateMutations.is_boolean(expression)) {
				ovalue = SymbolFactory.sym_condition(expression, true);
				mvalue = SymbolFactory.sym_condition(muta_value, true);
			}
			else if(StateMutations.is_assigned(expression)) {
				CirAssignStatement statement = 
						(CirAssignStatement) expression.statement_of();
				ovalue = SymbolFactory.sym_expression(statement.get_rvalue());
				mvalue = SymbolFactory.sym_expression(muta_value);
			}
			else {
				ovalue = SymbolFactory.sym_expression(expression);
				mvalue = SymbolFactory.sym_expression(muta_value);
			}
			return new CirValueErrorState(expression.execution_of(),
					CirStateStore.new_unit(expression), ovalue, mvalue);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [usep|defp:expression] <== {inc_expr:base:difference}
	 * @throws Exception
	 */
	public static CirIncreErrorState inc_expr(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else if(StateMutations.is_numeric(expression) 
				|| StateMutations.is_address(expression)) {
			SymbolExpression ovalue, mvalue;
			if(StateMutations.is_assigned(expression)) {
				CirAssignStatement statement = 
						(CirAssignStatement) expression.statement_of();
				ovalue = SymbolFactory.sym_expression(statement.get_rvalue());
				mvalue = SymbolFactory.sym_expression(difference);
			}
			else {
				ovalue = SymbolFactory.sym_expression(expression);
				mvalue = SymbolFactory.sym_expression(difference);
			}
			return new CirIncreErrorState(expression.execution_of(),
					CirStateStore.new_unit(expression), ovalue, mvalue);
		}
		else {
			throw new IllegalArgumentException(expression.generate_code(true));
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [usep|defp:expression] <== {xor_expr:base:difference}
	 * @throws Exception
	 */
	public static CirBixorErrorState xor_expr(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else if(StateMutations.is_integer(expression)) {
			SymbolExpression ovalue, mvalue;
			if(StateMutations.is_assigned(expression)) {
				CirAssignStatement statement = 
						(CirAssignStatement) expression.statement_of();
				ovalue = SymbolFactory.sym_expression(statement.get_rvalue());
				mvalue = SymbolFactory.sym_expression(difference);
			}
			else {
				ovalue = SymbolFactory.sym_expression(expression);
				mvalue = SymbolFactory.sym_expression(difference);
			}
			return new CirBixorErrorState(expression.execution_of(),
					CirStateStore.new_unit(expression), ovalue, mvalue);
		}
		else {
			throw new IllegalArgumentException(expression.generate_code(true));
		}
	}
	
	/* vdef error */
	/**
	 * @param expression
	 * @param reference
	 * @param muta_value
	 * @return	[vdef:expression:reference] <== {set_expr:ovalue:mvalue}
	 * @throws Exception
	 */
	public static CirValueErrorState set_vdef(CirExpression expression, 
			SymbolExpression reference, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(reference == null) {
			throw new IllegalArgumentException("Invalid reference: null.");
		}
		else {
			SymbolExpression ovalue, mvalue;
			if(StateMutations.is_boolean(reference.get_data_type())) {
				ovalue = SymbolFactory.sym_condition(reference, true);
				mvalue = SymbolFactory.sym_condition(muta_value, true);
			}
			else {
				ovalue = SymbolFactory.sym_expression(reference);
				mvalue = SymbolFactory.sym_expression(muta_value);
			}
			return new CirValueErrorState(expression.execution_of(),
					CirStateStore.new_vdef(expression, reference), 
					ovalue, mvalue);
		}
	}
	/**
	 * @param expression
	 * @param reference
	 * @param muta_value
	 * @return	[vdef:expression:reference] <== {inc_expr:base:difference}
	 * @throws Exception
	 */
	public static CirIncreErrorState inc_vdef(CirExpression expression, 
			SymbolExpression reference, Object difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else if(reference == null) {
			throw new IllegalArgumentException("Invalid reference: null.");
		}
		else {
			SymbolExpression ovalue = reference, mvalue;
			mvalue = SymbolFactory.sym_expression(difference);
			return new CirIncreErrorState(expression.execution_of(),
					CirStateStore.new_vdef(expression, reference), 
					ovalue, mvalue);
		}
	}
	/**
	 * @param expression
	 * @param reference
	 * @param muta_value
	 * @return	[vdef:expression:reference] <== {xor_expr:base:difference}
	 * @throws Exception
	 */
	public static CirBixorErrorState xor_vdef(CirExpression expression, 
			SymbolExpression reference, Object difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else if(reference == null) {
			throw new IllegalArgumentException("Invalid reference: null.");
		}
		else {
			SymbolExpression ovalue = reference, mvalue;
			mvalue = SymbolFactory.sym_expression(difference);
			return new CirBixorErrorState(expression.execution_of(),
					CirStateStore.new_vdef(expression, reference), 
					ovalue, mvalue);
		}
	}
	
}
