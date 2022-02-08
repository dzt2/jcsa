package com.jcsa.jcmutest.mutant.cir2mutant.muta.refr;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VBRPCirMutationParser extends CirMutationParser {

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
			SymbolExpression condition = SymbolFactory.sym_condition(expression, false);
			constraint = CirAbstractState.eva_need(execution, condition);
			init_error = CirAbstractState.set_expr(expression, Boolean.TRUE);
		}
		else if(mutation.get_operator() == MutaOperator.set_false) {
			SymbolExpression condition = SymbolFactory.sym_condition(expression, true);
			constraint = CirAbstractState.eva_need(execution, condition);
			init_error = CirAbstractState.set_expr(expression, Boolean.FALSE);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + mutation);
		}
		this.put_infection_pair(constraint, init_error);
	}

}
