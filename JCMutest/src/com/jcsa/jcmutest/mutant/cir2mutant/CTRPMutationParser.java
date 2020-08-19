package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;

public class CTRPMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, 
			List<CirMutation> targets) throws Exception {
		AstNode parameter = (AstNode) source.get_parameter();
		while(parameter != null) {
			if(parameter instanceof AstCaseStatement) {
				CirCaseStatement stmt = (CirCaseStatement) get_cir_nodes(
						tree, parameter, CirCaseStatement.class).get(0);
				targets.add(CirMutations.BTRP(stmt.get_condition(), true));
			}
			else {
				parameter = parameter.get_parent();
			}
		}
		throw new IllegalArgumentException("Not-case-statement");
	}

}
