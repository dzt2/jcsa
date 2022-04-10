package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	cov_stmt(statement; min_times, max_times).
 * 	
 * 	@author yukimula
 *
 */
public class AbsCoverTimesState extends AbsConditionalState {

	protected AbsCoverTimesState(AbsExecutionStore _store, int min_times, int max_times) throws Exception {
		super(AbsExecutionClass.cov_time, _store, 
				SymbolFactory.sym_constant(Integer.valueOf(min_times)), 
				SymbolFactory.sym_constant(Integer.valueOf(max_times)));
	}
	
	/**
	 * @return the minimal times for running the statement
	 */
	public int get_minimal_times() { return ((SymbolConstant) this.get_loperand()).get_int(); }
	
	/**
	 * @return the maximal times for running the statement
	 */
	public int get_maximal_times() { return ((SymbolConstant) this.get_roperand()).get_int(); }
	
}
