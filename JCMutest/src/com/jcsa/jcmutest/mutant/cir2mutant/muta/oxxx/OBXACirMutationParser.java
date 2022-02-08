package com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxx;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirOperatorParsers;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class OBXACirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return (CirStatement) this.get_cir_node(mutation.get_location(), CirAssignStatement.class);
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		CirAssignStatement assign_stmt = (CirAssignStatement) 
				this.get_cir_node(mutation.get_location(), CirBinAssignStatement.class);
		CirComputeExpression expression = (CirComputeExpression) assign_stmt.get_rvalue();
		CirExpression loperand = expression.get_operand(0), roperand = expression.get_operand(1);
		CirOperatorParsers.parse(mutation, this.get_r_execution(), expression, loperand, roperand, this.i_states, this.p_states);
	}

}
