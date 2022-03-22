package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractClass;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * cov_time(statement; min_times, max_times)
 * 
 * @author yukimula
 *
 */
public class UniCoverTimesState extends UniConditionState {

	protected UniCoverTimesState(UniAbstractStore state_store, int min_times, int max_times) throws Exception {
		super(UniAbstractClass.cov_time, state_store, 
				SymbolFactory.sym_constant(Integer.valueOf(min_times)), 
				SymbolFactory.sym_constant(Integer.valueOf(max_times)));
	}
	
	/**
	 * @return the minimal times for running the target statement
	 */
	public int get_min_times() { return ((SymbolConstant) this.get_lvalue()).get_int(); }
	
	/**
	 * @return the maximal times for running the target statement
	 */
	public int get_max_times() { return ((SymbolConstant) this.get_rvalue()).get_int(); }
	
}
