package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcmutest.mutant.uni2mutant.base.impl.UniAbstractStates;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	It creates an abstract symbolic state used to describe mutation testing process.
 * 	<br>
 * 	<code>
 * 	UniAbstractState				[class; store; lvalue, rvalue]				<br>
 * 	|--	UniConditionalState			class(statement; lvalue, rvalue)			<br>
 * 	|--	|--	UniCoverTimesState		cov_time(statement; min_times, max_times)	<br>
 * 	|--	|--	UniConstraintState		eva_bool(statement; condition, must_need)	<br>
 * 	|--	|--	UniSeedMutantState		sed_muta(statement; mutant_ID, clas_oprt)	<br>
 * 	|--	UniPathErrorState			class(statement; lvalue, rvalue)			<br>
 * 	|--	|--	UniBlockErrorState		mut_stmt(statement; orig_exec, muta_exec)	<br>
 * 	|--	|--	UniFlowsErrorState		mut_flow(statement; orig_next, muta_next)	<br>
 * 	|--	|--	UniTrapsErrorState		trp_stmt(statement; exception, exception)	<br>
 * 	|--	UniDataErrorState			class(expression; orig_value, parameter)	<br>
 * 	|--	|--	UniValueErrorState		set_expr(expression; orig_value, muta_value)<br>
 * 	|--	|--	UniIncreErrorState		inc_expr(expression; orig_value, difference)<br>
 * 	|--	|--	UniBixorErrorState		xor_expr(expression; orig_value, difference)<br>
 * 	</code>
 * 
 * 	@author yukimula
 *
 */
public abstract class UniAbstractState {
	
	/* attributes */
	/** the class of this abstract symbolic state **/
	private	UniAbstractClass	state_class;
	/** the store-location to evaluate this state **/
	private	UniAbstractStore	state_store;
	/** the left-operand used to define the state **/
	private	SymbolExpression	loperand;
	/** the righ-operand used to define the state **/
	private	SymbolExpression	roperand;
	/**
	 * It creates an abstract symbolic state used in mutation testing.
	 * @param state_class	the class of this abstract symbolic state
	 * @param state_store	the store-location to evaluate this state
	 * @param loperand		the left-operand used to define the state
	 * @param roperand		the righ-operand used to define the state
	 * @throws Exception
	 */
	protected UniAbstractState(UniAbstractClass state_class,
			UniAbstractStore state_store,
			SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		if(state_class == null) {
			throw new IllegalArgumentException("Invalid state_store: null");
		}
		else if(state_store == null) {
			throw new IllegalArgumentException("Invalid state_store: null");
		}
		else if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand as: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand as: null");
		}
		else {
			this.state_class = state_class;
			this.state_store = state_store;
			this.loperand = UniAbstractStates.evaluate(loperand);
			this.roperand = UniAbstractStates.evaluate(roperand);
		}
	}
	
	/* getters */
	/**
	 * @return the class of this abstract symbolic state
	 */
	public 	UniAbstractClass	get_class()  { return this.state_class; }
	/**
	 * @return the store-location to evaluate this state
	 */
	public 	UniAbstractStore	get_store()	 { return this.state_store; }
	/**
	 * @return the left-operand used to define the state
	 */
	public 	SymbolExpression	get_lvalue() { return this.loperand; }
	/**
	 * @return the righ-operand used to define the state
	 */
	public 	SymbolExpression	get_rvalue() { return this.roperand; }
	/**
	 * @return the execution point where this state is defined
	 */
	public 	CirExecution get_execution() { return this.state_store.get_exe_location(); }
	
	/* general */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.state_class.toString());
		buffer.append(":");
		buffer.append(this.state_store.toString());
		buffer.append(":");
		buffer.append(this.loperand.toString());
		buffer.append(":");
		buffer.append(this.roperand.toString());
		return buffer.toString();
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof UniAbstractState) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* classify */
	/**
	 * @return {cov_time, eva_bool, sed_muta}
	 */
	public boolean is_conditions() {
		switch(this.state_class) {
		case cov_time:
		case eva_bool:
		case sed_muta:	return true;
		default:		return false;
		}
	}
	/**
	 * @return {mut_stmt, mut_flow, trp_stmt}
	 */
	public boolean is_path_error() {
		switch(this.state_class) {
		case mut_stmt:
		case mut_flow:
		case trp_stmt:	return true;
		default:		return false;
		}
	}
	/**
	 * @return {set_expr, inc_expr, xor_expr}
	 */
	public boolean is_data_error() {
		switch(this.state_class) {
		case set_expr:
		case inc_expr:
		case xor_expr:	return true;
		default:		return false;
		}
	}
	/**
	 * @return {mut_stmt, mut_flow, trp_stmt, set_expr, inc_expr, xor_expr}
	 */
	public boolean is_abst_error() {
		return this.is_path_error() || this.is_data_error();
	}
	
}
