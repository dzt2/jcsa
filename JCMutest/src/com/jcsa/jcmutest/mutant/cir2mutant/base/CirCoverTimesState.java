package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * cov_stmt([statement, stmt_key]; [lest_most, int_times])
 * where: lest_most is True if the condition is to reach statement for at least
 * 		  N times; or lest_most is False if it requires the statement must be
 * 		  executed for at most given N times.
 * 
 * @author yukimula
 *
 */
public class CirCoverTimesState extends CirConditionState {

	protected CirCoverTimesState(CirExecution execution, boolean lest_most, int int_times) throws Exception {
		super(CirAbstractClass.cov_stmt, execution, 
				SymbolFactory.sym_constant(Boolean.valueOf(lest_most)), 
				SymbolFactory.sym_constant(Integer.valueOf(int_times)));
	}
	
	/* special getters */
	/**
	 * @return whether the state is to reach statement for at least given times
	 */
	public boolean is_reach_coverage() {
		SymbolConstant loperand = (SymbolConstant) this.get_loperand();
		return loperand.get_bool();
	}
	/**
	 * @return whether the state is to reach statement for at most given times
	 */
	public boolean is_limit_coverage() {
		SymbolConstant loperand = (SymbolConstant) this.get_loperand();
		return !loperand.get_bool();
	}
	/**
	 * @return the executing times to cover or limit the target statement
	 */
	public int get_executed_times() {
		SymbolConstant roperand = (SymbolConstant) this.get_roperand();
		return roperand.get_int().intValue();
	}
	
}
