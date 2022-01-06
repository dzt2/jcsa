package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;

/**
 * execution [stmt:statement] <== {cov_time:int_times}
 * 
 * @author yukimula
 *
 */
public class CirCoverTimesState extends CirConditionState {
	
	protected CirCoverTimesState(CirExecution execution, int int_times) throws Exception {
		super(execution, CirStateValue.cov_time(int_times));
	}
	
	/**
	 * @return the number of execution times the statement needs be executed
	 */
	public int get_int_times() { 
		return ((SymbolConstant) this.get_uvalue()).get_int();
	}
	
}
