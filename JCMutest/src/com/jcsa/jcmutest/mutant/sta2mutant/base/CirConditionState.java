package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * <code>
 * 	|--	CirConditionState	[state_class, execution, {stmt|cond}, [value]]		<br>
 * 	|--	|--	CirCheckPointState	cov_stmt(execution, statement, 	{TRUE});		<br>
 * 	|--	|--	CirCoverTimesState	cov_time(execution, statement, 	{int_times});	<br>
 * 	|--	|--	CirConstraintState	eva_cond(execution, condition,	{CONDITION});	<br>
 * </code>
 * <br>
 * @author yukimula
 *
 */
public abstract class CirConditionState extends CirAbstractState {
	
	protected CirConditionState(CirStateClass state_class, 
			CirStateStore state_store, SymbolExpression value) throws Exception {
		super(state_class, state_store, 1);
		this.set_state_value(0, value);
	}
	
	/**
	 * @return the unique symbolic value to define this state
	 */
	public SymbolExpression	get_uvalue() { return this.get_state_value(0); }
	
}
