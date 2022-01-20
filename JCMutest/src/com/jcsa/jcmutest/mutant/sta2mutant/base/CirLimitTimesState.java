package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;

/**
 * [stmt:statement] <== cov_time(true, int_times)
 * 
 * @author yukimula
 *
 */
public class CirLimitTimesState extends CirConditionState {

	protected CirLimitTimesState(CirExecution point, int int_times) throws Exception {
		super(point, CirStateValue.cov_time(true, int_times));
	}
	
	/**
	 * @return the maximal times that the statement should be executed
	 */
	public int get_maximal_times() {
		return ((SymbolConstant) this.get_roperand()).get_int();
	}

}
