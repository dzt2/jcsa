package com.jcsa.jcmutest.mutant.sta2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VINCStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		CirExecution execution = expression.execution_of(); SymbolExpression muta_value;
		if(mutation.get_operator() == MutaOperator.inc_constant) {
			muta_value = SymbolFactory.arith_add(expression.get_data_type(), expression, mutation.get_parameter());
		}
		else if(mutation.get_operator() == MutaOperator.mul_constant) {
			muta_value = SymbolFactory.arith_mul(expression.get_data_type(), expression, mutation.get_parameter());
		}
		else {
			throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		CirAbstErrorState init_error = CirAbstractState.set_expr(expression, muta_value);
		this.put_infection_pair(CirAbstractState.cov_time(execution, 1), init_error);
	}

}
