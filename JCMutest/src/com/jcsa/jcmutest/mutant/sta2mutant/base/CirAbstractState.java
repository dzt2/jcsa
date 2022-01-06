package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateValuations;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * It describes an abstract execution state at some point in mutation testing.
 * <br>
 * <code>
 * 	CirAbstractState		[state_class, execution, state_store, values]		<br>
 * 	<br>
 * 	|--	CirConditionState	[state_class, execution, {stmt|cond}, [value]]		<br>
 * 	|--	|--	CirCheckPointState	cov_stmt(execution, statement, 	{TRUE});		<br>
 * 	|--	|--	CirCoverTimesState	cov_time(execution, statement, 	{int_times});	<br>
 * 	|--	|--	CirConstraintState	eva_cond(execution, condition,	{CONDITION});	<br>
 * 	<br>
 * 	|--	CirPathErrorState	[state_class, execution, {statement}, values]		<br>
 * 	|--	|--	CirBlockErrorState	mut_stmt(execution, statement, 	{bool, bool});	<br>
 * 	|--	|--	CirFlowsErrorState	mut_flow(execution,	statement,	{oexe, mexe});	<br>
 * 	|--	|--	CirTrapsErrorState	trp_stmt(execution,	statement,	{EXCEPTION});	<br>
 * 	<br>
 * 	|--	CirDataErrorState	[state_class, execution, {expression}, [orig, muta]]<br>
 * 	|--	|--	CirDiferErrorState	dif_usep(execution,	{usep|cond}, [orig, muta]);	<br>
 * 	|--	|--	CirValueErrorState	mut_usep(execution, {usep|cond}, [orig, muta]);	<br>
 * 	|--	|--	CirReferErrorState	mut_defp(execution, {defp|vdef}, [orig, muta]);	<br>
 * 	<br>
 * 	|--	CirDifferentState	[state_class, execution, {expression}, [value]]		<br>
 * 	|--	|--	CirExtetErrorState	ext_expr(execution,	{usep|defp|vdef}, {diff});	<br>
 * 	|--	|--	CirIncreErrorState	inc_expr(execution,	{usep|defp|vdef}, {diff});	<br>
 * 	|--	|--	CirBixorErrorState	xor_expr(execution, {usep|defp|vdef}, {diff});	<br>
 * </code>
 * <br>
 * @author yukimula
 *
 */
public abstract class CirAbstractState {
	
	/* attributes */
	private	CirStateClass		state_class;
	private	CirExecution		state_point;
	private CirStateStore		state_store;
	private	SymbolExpression[]	state_values;
	
	/* constructor */
	/**
	 * It creates a universal abstract state in mutation testing
	 * @param state_class	the category of the execution state
	 * @param state_store	the store location to preserve this state
	 * @param number_of_values	the number of values preserved in defining the state
	 * @throws Exception
	 */
	protected CirAbstractState(CirStateClass state_class, 
			CirStateStore state_store, int number_of_values) throws Exception {
		if(state_class == null) {
			throw new IllegalArgumentException("Invalid state_class: null");
		}
		else if(state_store == null) {
			throw new IllegalArgumentException("Invalid state_store: null");
		}
		else if(number_of_values <= 0) {
			throw new IllegalArgumentException("Invalid: " + number_of_values);
		}
		else {
			this.state_class = state_class;
			this.state_point = state_store.get_execution();
			this.state_store = state_store;
			this.state_values = new SymbolExpression[number_of_values];
			if(this.state_point == null)
				throw new IllegalArgumentException("No exception defined");
		}
	}
	/**
	 * It sets the symbolic value at the kth location of the state buffer
	 * @param k
	 * @param value
	 * @throws Exception
	 */
	protected void set_state_value(int k, SymbolExpression value) throws Exception {
		if(k < 0 || k >= this.state_values.length) {
			throw new IllegalArgumentException("Invalid k as " + k);
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}
		else {
			this.state_values[k] = StateValuations.evaluate(value);
		}
	}
	/**
	 * @param k
	 * @return the kth value preserved in the abstract state definition
	 */
	protected SymbolExpression get_state_value(int k) {
		return this.state_values[k];
	}
	
	/* getters */
	/**
	 * @return the category of the abstract execution state in mutation testing
	 */
	public	CirStateClass	get_state_class() 	{ return this.state_class; 	}
	/**
	 * @return the execution point where the state is described and established
	 */
	public	CirExecution	get_state_point()	{ return this.state_point;	}
	/**
	 * @return	the statement where this abstract state is defined upon
	 */
	public	CirStatement	get_statement()		{ return this.state_point.get_statement(); }
	/**
	 * @return the store instance to preserve values of the state in execution
	 */
	public	CirStateStore	get_state_store()	{ return this.state_store;	}
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.state_class.toString());
		buffer.append("(");
		buffer.append(this.state_store.toString());
		for(SymbolExpression value : this.state_values) {
			buffer.append(", ");
			buffer.append(value.toString());
		}
		buffer.append(")");
		return buffer.toString();
	}
	@Override
	public	int	hashCode()	{ return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CirAbstractState) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* TODO factory */
	
	
}
