package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutations;
import com.jcsa.jcparse.lang.program.AstCirNode;

public class TrpStmtMutation extends ContextMutation {

	protected TrpStmtMutation(AstCirNode location) throws Exception {
		super(ContextMutaClass.trp_stmt, location, 
				ContextMutations.true_value, 
				ContextMutations.trap_value);
	}

}
