package com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateOperatorParsers;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class OLXNStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		CirAssignStatement save1 = (CirAssignStatement) this.
				get_cir_node(mutation.get_location(), CirSaveAssignStatement.class);
		CirAssignStatement save2 = (CirAssignStatement) this.
				get_cir_nodes(mutation.get_location(), CirSaveAssignStatement.class).get(1);
		CirExpression loperand = save1.get_rvalue(), roperand = save2.get_rvalue();
		StateOperatorParsers.parse(mutation, 
				this.get_r_execution(), expression, loperand, roperand, this.infection_map);
	}

}
