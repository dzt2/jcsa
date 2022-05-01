package com.jcsa.jcmutest.mutant.ctx2mutant.muta.oxxx;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.oprt.CirOperatorMutationParsers;
import com.jcsa.jcparse.lang.program.AstCirNode;

public class OBXNContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		CirOperatorMutationParsers.parse_mutation(this.get_output());
	}

}
