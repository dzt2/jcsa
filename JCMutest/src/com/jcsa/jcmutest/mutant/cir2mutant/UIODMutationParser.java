package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;

public class UIODMutationParser extends CirMutationParser {
	
	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		AstNode location = source.get_location();
		switch(source.get_operator()) {
		case delete_prev_inc:
		case delete_post_inc:
		{
			CirAssignStatement stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
			targets.add(CirMutations.inc_expression(stmt.get_rvalue(), -1));
			break;
		}
		case delete_prev_dec:
		case delete_post_dec:
		{
			CirAssignStatement stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
			targets.add(CirMutations.inc_expression(stmt.get_rvalue(), 1));
			break;
		}
		default: throw new IllegalArgumentException("Unsupport: " + source.toString());
		}
	}
	
}
