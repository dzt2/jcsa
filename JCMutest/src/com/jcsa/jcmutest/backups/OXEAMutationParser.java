package com.jcsa.jcmutest.backups;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;

public class OXEAMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.get_cir_nodes(
				tree, source.get_location(), CirAssignStatement.class).get(0);
		CirComputeExpression expression = (CirComputeExpression) statement.get_rvalue();
		targets.add(CirMutations.set_expression(expression, expression.get_operand(1)));
	}

}
