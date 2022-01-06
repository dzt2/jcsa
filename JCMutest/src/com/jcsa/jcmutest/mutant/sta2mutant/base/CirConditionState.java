package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * <code>
 * 	|--	CirConditionState		execution[stmt]		:=	{cov_time|eva_expr}		<br>
 * 	|--	|--	CirCoverTimesState	[stmt:statement]	:=	{cov_time:int_times}	<br>
 * 	|--	|--	CirConstraintState	[stmt:statement]	:=	{eva_cond:condition}	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirConditionState extends CirAbstractState {
	
	protected CirConditionState(CirExecution execution, CirStateValue value) throws Exception {
		super(execution, CirStateStore.new_unit(execution.get_statement()), value);
	}
	
	/**
	 * @return the unary value preserved in the state connected with statement unit
	 */
	public SymbolExpression get_uvalue() { return this.get_value().get_uvalue(); }
	
}
