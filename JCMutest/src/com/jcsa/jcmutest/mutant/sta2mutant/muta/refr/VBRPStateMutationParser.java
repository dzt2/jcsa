package com.jcsa.jcmutest.mutant.sta2mutant.muta.refr;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class VBRPStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		/* declarations */
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		CirExecution execution = expression.execution_of();
		
		/* constraint-error infection pairs */
		CirConditionState constraint; CirAbstErrorState init_error;
		if(mutation.get_operator() == MutaOperator.set_true) {
			constraint = CirAbstractState.eva_cond(execution, expression, false);
			init_error = CirAbstractState.set_expr(expression, Boolean.TRUE);
		}
		else if(mutation.get_operator() == MutaOperator.set_false) {
			constraint = CirAbstractState.eva_cond(execution, expression, true);
			init_error = CirAbstractState.set_expr(expression, Boolean.FALSE);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + mutation);
		}
		this.put_infection_pair(constraint, init_error);
	}

}
