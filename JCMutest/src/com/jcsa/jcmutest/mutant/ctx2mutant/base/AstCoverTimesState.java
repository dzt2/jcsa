package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class AstCoverTimesState extends AstConditionState {

	protected AstCoverTimesState(AstCirNode location, int min_times, int max_times) throws Exception {
		super(AstContextClass.cov_time, location, 
				SymbolFactory.sym_constant(Integer.valueOf(min_times)), 
				SymbolFactory.sym_constant(Integer.valueOf(max_times)));
		if(!location.is_statement_node()) {
			throw new IllegalArgumentException("Invalid location: null");
		}
	}

	/**
	 * @return the minimal times for running the statement
	 */
	public	int	get_minimal_times() { return ((SymbolConstant) this.get_loperand()).get_int(); }
	
	/**
	 * @return the maximal times for running the statement
	 */
	public	int	get_maximal_times() { return ((SymbolConstant) this.get_roperand()).get_int(); }
	
}
