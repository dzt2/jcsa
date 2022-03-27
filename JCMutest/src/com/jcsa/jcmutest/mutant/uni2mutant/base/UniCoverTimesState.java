package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * cov_time(statement; min_times, max_times)
 * 
 * @author yukimula
 *
 */
public class UniCoverTimesState extends UniConditionState {
	
	/**
	 * @param _store	the statement location
	 * @param min_times	the minimal times for running the statement
	 * @param max_times	the maximal times for running the statement
	 * @throws Exception
	 */
	protected UniCoverTimesState(UniAbstractStore _store, int min_times, int max_times) throws Exception {
		super(UniAbstractClass.cov_time, _store, 
				SymbolFactory.sym_constant(Integer.valueOf(min_times)), 
				SymbolFactory.sym_constant(Integer.valueOf(max_times)));
		if(min_times > max_times || max_times < 0) {
			throw new IllegalArgumentException(min_times + " --> " + max_times);
		}
	}
	
	/**
	 * @return the minimal times for running the statement
	 */
	public int get_min_times() {
		return ((SymbolConstant) this.get_loperand()).get_int();
	}
	
	/**
	 * @return the maximal times for running the statement
	 */
	public int get_max_times() {
		return ((SymbolConstant) this.get_roperand()).get_int();
	}
	
}
