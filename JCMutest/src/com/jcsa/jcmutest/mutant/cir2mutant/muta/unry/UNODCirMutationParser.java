package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UNODCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		/* declarations and data getters */
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		CirExecution execution = this.get_r_execution(); 
		SymbolExpression condition, muta_value;
		CirConditionState constraint; CirAbstErrorState init_error;
		
		/* (-expr, expr) */
		if(mutation.get_operator() == MutaOperator.delete_arith_neg) {
			if(CirMutations.is_numeric(expression)) {
				condition = SymbolFactory.not_equals(expression, Integer.valueOf(0));
				muta_value = SymbolFactory.arith_neg(expression);
				constraint = CirAbstractState.eva_need(execution, condition);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
			else {
				throw new IllegalArgumentException(expression.generate_code(true));
			}
		}
		/* (~expr, expr) */
		else if(mutation.get_operator() == MutaOperator.delete_bitws_rsv) {
			if(CirMutations.is_integer(expression)) {
				constraint = CirAbstractState.cov_time(execution, 1);
				muta_value = SymbolFactory.bitws_rsv(expression);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
			else {
				throw new IllegalArgumentException(expression.generate_code(true));
			}
		}
		/* (!expr, expr) */
		else if(mutation.get_operator() == MutaOperator.delete_logic_not) {
			if(CirMutations.is_boolean(expression)) {
				constraint = CirAbstractState.cov_time(execution, 1);
				muta_value = SymbolFactory.sym_condition(expression, false);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
			else if(CirMutations.is_numeric(expression)) {
				constraint = CirAbstractState.cov_time(execution, 1);
				muta_value = SymbolFactory.sym_condition(expression, false);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
			else {
				throw new IllegalArgumentException(expression.generate_code(true));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + mutation.toString());
		}
		this.put_infection_pair(constraint, init_error);
	}

}
