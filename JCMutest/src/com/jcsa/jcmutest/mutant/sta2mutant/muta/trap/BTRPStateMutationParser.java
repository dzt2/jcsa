package com.jcsa.jcmutest.mutant.sta2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 *	R{cov_stmt(execution)}
 * 	<br>
 * 	I{expression as value}
 * 	<br>
 * 	P{set_trap(execution)}
 * 	<br>
 * 	@author yukimula
 *
 */
public class BTRPStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		/* 1. get basic expression and value */
		CirExpression expression = get_cir_expression(mutation.get_location());
		boolean value; CirExecution execution = this.get_r_execution();
		switch(mutation.get_operator()) {
		case trap_on_true:	value = true;	break;
		case trap_on_false:	value = false;	break;
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		
		
		/* 2. generate state infection pairs */
		if(expression != null) {
			CirConditionState constraint = CirAbstractState.eva_cond(execution, expression, value);
			CirAbstErrorState init_error = CirAbstractState.set_trap(execution);
			this.put_infection_pair(constraint, init_error);
		}
	}

}
