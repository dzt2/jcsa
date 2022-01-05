package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * end_time(execution, statement, {exception});
 * 
 * @author yukimula
 *
 */
public class CirTerminatedState extends CirConditionState {

	protected CirTerminatedState(CirExecution execution, SymbolExpression parameter) throws Exception {
		super(CirStateCategory.end_stmt, execution, CirStateValuation.trap_value);
	}
	
}
