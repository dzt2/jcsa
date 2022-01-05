package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * cov_time(execution, statement, {int_times}); 
 * 
 * @author yukimula
 *
 */
public class CirCoverTimesState extends CirConditionState {

	protected CirCoverTimesState(CirExecution execution, SymbolExpression parameter) throws Exception {
		super(CirStateCategory.cov_time, execution, parameter);
	}
	
	/**
	 * @return the times that the statement needs be executed for
	 */
	public int get_int_times() { return ((SymbolConstant) this.get_parameter()).get_int(); }
	
}
