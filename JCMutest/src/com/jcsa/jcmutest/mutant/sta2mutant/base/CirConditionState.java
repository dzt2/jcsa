package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * <code>
 * 	|--	CirConditionState		[category, execution, statement, {parameter}]	<br>
 * 	|--	|--	CirCoverTimesState	[cov_time, execution, statement, {int_times}]	<br>
 * 	|--	|--	CirConstraintState	[eva_expr, execution, statement, {condition}]	<br>
 * 	|--	|--	CirTerminatedState	[end_stmt, execution, statement, {exception}]	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirConditionState extends CirAbstractState {

	protected CirConditionState(CirStateCategory category, CirExecution 
			execution, SymbolExpression parameter) throws Exception {
		super(category, execution, execution.get_statement(), 1);
		this.set_parameter(0, CirStateValuation.evaluate(parameter));
	}
	
	/**
	 * @return the unique parameter of symbolic expression used to define state
	 */
	protected SymbolExpression get_parameter() { return this.get_parameter(0); }

}
