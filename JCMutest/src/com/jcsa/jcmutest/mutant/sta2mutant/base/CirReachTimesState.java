package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;

/**
 * [stmt:statement] <== cov_time(false, int_times)
 * 
 * @author yukimula
 *
 */
public class CirReachTimesState extends CirConditionState {

	protected CirReachTimesState(CirExecution point, int int_times) throws Exception {
		super(point, CirStateValue.cov_time(false, int_times));
	}
	
	/**
	 * @return the minimal times that the statement should be executed
	 */
	public int get_minimal_times() {
		return ((SymbolConstant) this.get_roperand()).get_int();
	}
	
}
