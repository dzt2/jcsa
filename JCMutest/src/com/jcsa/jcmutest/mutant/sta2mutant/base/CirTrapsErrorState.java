package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirTrapsErrorState extends CirPathErrorState {

	protected CirTrapsErrorState(CirAbstractStore location) throws Exception {
		super(CirAbstractClass.mut_trap, location, 
				SymbolFactory.sym_constant(Boolean.TRUE), 
				StateMutations.trap_value);
	}

}
