package com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.program.AstCirNode;

public class ETRPContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		this.put_infection(this.eva_cond(Boolean.TRUE), this.trp_stmt());
	}

}
