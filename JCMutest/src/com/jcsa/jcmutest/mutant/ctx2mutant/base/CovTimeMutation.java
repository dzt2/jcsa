package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CovTimeMutation extends ContextMutation {

	protected CovTimeMutation(AstCirNode location, int min_times, int max_times) throws Exception {
		super(ContextMutaClass.cov_time, location, 
				SymbolFactory.sym_constant(min_times), 
				SymbolFactory.sym_constant(max_times));
		if(max_times < min_times || max_times < 0) {
			throw new IllegalArgumentException(min_times + " --> " + max_times);
		}
	}
	
	/**
	 * @return the minimal times for running the statement
	 */
	public	int	get_min_times() { return ((SymbolConstant) this.get_loperand()).get_int(); }
	
	/**
	 * @return the maximal times for running the statement
	 */
	public	int	get_max_times() { return ((SymbolConstant) this.get_roperand()).get_int(); }
	
}
