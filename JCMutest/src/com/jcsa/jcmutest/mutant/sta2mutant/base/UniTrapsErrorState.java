package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UniTrapsErrorState extends UniPathErrorState {

	protected UniTrapsErrorState(CirStatement location) throws Exception {
		super(UniAbstractClass.trp_stmt, location, 
				SymbolFactory.sym_constant(Boolean.TRUE), 
				StateMutations.trap_value);
	}

}
