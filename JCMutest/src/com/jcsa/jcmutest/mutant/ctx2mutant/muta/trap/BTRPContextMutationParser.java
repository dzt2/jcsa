package com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstConditionState;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class BTRPContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}

	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		/* 1. generate conditions */
		SymbolExpression condition; 
		switch(mutation.get_operator()) {
		case trap_on_true:	condition = SymbolFactory.sym_condition(mutation.get_location(), true);	 break;
		case trap_on_false:	condition = SymbolFactory.sym_condition(mutation.get_location(), false); break; 
		default:			throw new IllegalArgumentException("Unsupported: " + mutation.get_operator());
		}
		AstConditionState constraint = this.eva_cond(condition);
		
		/* 2. generate init_error and combines */
		this.put_infection(constraint, this.mut_trap());
	}

}
