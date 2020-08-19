package com.jcsa.jcmutest.mutant.cir2mutation;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;

public class CTRPMutationParser extends CirMutationParser {

	@Override
	public void parse(CirTree cir_tree, AstMutation source, Collection<CirMutation> targets) throws Exception {
		AstNode parameter = (AstNode) source.get_parameter();
		while(parameter != null) {
			if(parameter instanceof AstCaseStatement) {
				CirCaseStatement statement = (CirCaseStatement) this.get_cir_node(
									cir_tree, parameter, CirCaseStatement.class, 0);
				targets.add(CirMutations.BTRP(statement.get_condition(), true));
			}
			else {
				parameter = parameter.get_parent();
			}
		}
		throw new IllegalArgumentException("Invalid source: " + source);
	}

}
