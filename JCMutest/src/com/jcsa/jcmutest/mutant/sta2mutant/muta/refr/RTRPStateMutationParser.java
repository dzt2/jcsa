package com.jcsa.jcmutest.mutant.sta2mutant.muta.refr;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class RTRPStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		SymbolExpression mvalue = SymbolFactory.sym_expression(mutation.get_parameter());
		SymbolExpression condition = SymbolFactory.not_equals(expression, mvalue);
		CirExecution execution = expression.execution_of();
		CirConditionState constraint = CirAbstractState.eva_cond(execution, condition, true);
		this.put_infection_pair(constraint, CirAbstractState.set_expr(expression, mvalue));
	}

}
