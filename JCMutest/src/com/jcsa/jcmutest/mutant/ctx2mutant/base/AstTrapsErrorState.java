package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutations;
import com.jcsa.jcparse.lang.program.AstCirNode;

public class AstTrapsErrorState extends AstPathErrorState {

	protected AstTrapsErrorState(AstCirNode location) throws Exception {
		super(AstContextClass.mut_trap, location, 
				ContextMutations.trap_value, 
				ContextMutations.trap_value);
		if(!location.is_module_node()) {
			throw new IllegalArgumentException("Invalid: " + location);
		}
	}

}
