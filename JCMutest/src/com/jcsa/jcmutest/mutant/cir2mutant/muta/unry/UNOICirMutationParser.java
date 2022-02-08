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

public class UNOICirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		/* declarations */
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		CirExecution execution = expression.execution_of();
		SymbolExpression condition, muta_value;
		CirConditionState constraint; CirAbstErrorState init_error;
		
		/* (expr --> -expr) */
		if(mutation.get_operator() == MutaOperator.insert_arith_neg) {
			if(CirMutations.is_boolean(expression)) {
				constraint = CirAbstractState.eva_need(execution, Boolean.FALSE);
				init_error = CirAbstractState.set_expr(expression, expression);
			}
			else {
				condition = SymbolFactory.not_equals(expression, Integer.valueOf(0));
				constraint = CirAbstractState.eva_need(execution, condition);
				muta_value = SymbolFactory.arith_neg(expression);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
		}
		/* (expr --> ~expr) */
		else if(mutation.get_operator() == MutaOperator.insert_bitws_rsv) {
			if(CirMutations.is_boolean(expression)) {
				constraint = CirAbstractState.eva_need(execution, expression);
				init_error = CirAbstractState.set_expr(expression, Boolean.TRUE);
			}
			else {
				constraint = CirAbstractState.cov_time(execution, 1);
				muta_value = SymbolFactory.bitws_rsv(expression);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
		}
		/* (expr --> !expr) */
		else if(mutation.get_operator() == MutaOperator.insert_logic_not) {
			if(CirMutations.is_boolean(expression)) {
				constraint = CirAbstractState.cov_time(execution, 1);
				muta_value = SymbolFactory.sym_condition(expression, false);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
			else {
				constraint = CirAbstractState.cov_time(execution, 1);
				muta_value = SymbolFactory.sym_condition(expression, false);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
		}
		/* (expr --> abs(expr)) */
		else if(mutation.get_operator() == MutaOperator.insert_abs_value) {
			if(CirMutations.is_boolean(expression)) {
				constraint = CirAbstractState.eva_need(execution, Boolean.FALSE);
				init_error = CirAbstractState.set_expr(expression, expression);
			}
			else {
				condition = SymbolFactory.smaller_tn(expression, Integer.valueOf(0));
				constraint = CirAbstractState.eva_need(execution, condition);
				muta_value = SymbolFactory.arith_neg(expression);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
		}
		/* (expr --> -abs(expr)) */
		else if(mutation.get_operator() == MutaOperator.insert_nabs_value) {
			if(CirMutations.is_boolean(expression)) {
				constraint = CirAbstractState.eva_need(execution, Boolean.FALSE);
				init_error = CirAbstractState.set_expr(expression, expression);
			}
			else {
				condition = SymbolFactory.greater_tn(expression, Integer.valueOf(0));
				constraint = CirAbstractState.eva_need(execution, condition);
				muta_value = SymbolFactory.arith_neg(expression);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + mutation.toString());
		}
		this.put_infection_pair(constraint, init_error);
	}

}
