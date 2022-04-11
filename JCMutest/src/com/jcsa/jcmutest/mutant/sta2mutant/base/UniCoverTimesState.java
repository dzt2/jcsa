package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	cov_time(statement; min_time, max_time)
 * 	
 * 	@author yukimula
 *
 */
public class UniCoverTimesState extends UniConditionState {

	protected UniCoverTimesState(CirStatement location, int min_times, int max_times) throws Exception {
		super(UniAbstractClass.cov_time, location, 
				SymbolFactory.sym_constant(Integer.valueOf(min_times)),
				SymbolFactory.sym_constant(Integer.valueOf(max_times)));
		if(min_times > max_times || max_times <= 0) {
			throw new IllegalArgumentException(min_times + " --> " + max_times);
		}
	}
	
	/**
	 * @return the minimal times for running the statement
	 */
	public int get_minimal_times() {
		return ((SymbolConstant) this.get_loperand()).get_int();
	}
	
	/**
	 * @return the maximal times for running the statement
	 */
	public int get_maximal_times() {
		return ((SymbolConstant) this.get_roperand()).get_int();
	}

}
