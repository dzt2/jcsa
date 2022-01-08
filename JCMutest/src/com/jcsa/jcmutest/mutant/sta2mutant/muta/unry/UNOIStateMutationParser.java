package com.jcsa.jcmutest.mutant.sta2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UNOIStateMutationParser extends StateMutationParser {

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
			if(StateMutations.is_boolean(expression)) {
				constraint = CirAbstractState.eva_cond(execution, Boolean.FALSE, true);
				init_error = CirAbstractState.set_expr(expression, expression);
			}
			else if(StateMutations.is_numeric(expression)) {
				condition = SymbolFactory.not_equals(expression, Integer.valueOf(0));
				constraint = CirAbstractState.eva_cond(execution, condition, true);
				muta_value = SymbolFactory.arith_neg(expression);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
			else {
				throw new IllegalArgumentException(expression.generate_code(true));
			}
		}
		/* (expr --> ~expr) */
		else if(mutation.get_operator() == MutaOperator.insert_bitws_rsv) {
			if(StateMutations.is_boolean(expression)) {
				constraint = CirAbstractState.cov_time(execution, 1);
				init_error = CirAbstractState.set_expr(expression, Boolean.TRUE);
			}
			else if(StateMutations.is_integer(expression)) {
				constraint = CirAbstractState.cov_time(execution, 1);
				muta_value = SymbolFactory.bitws_rsv(expression);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
			else {
				throw new IllegalArgumentException(expression.generate_code(true));
			}
		}
		/* (expr --> !expr) */
		else if(mutation.get_operator() == MutaOperator.insert_logic_not) {
			if(StateMutations.is_boolean(expression)) {
				constraint = CirAbstractState.cov_time(execution, 1);
				muta_value = SymbolFactory.sym_condition(expression, false);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
			else if(StateMutations.is_numeric(expression)) {
				constraint = CirAbstractState.cov_time(execution, 1);
				muta_value = SymbolFactory.sym_condition(expression, false);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
			else {
				throw new IllegalArgumentException(expression.generate_code(true));
			}
		}
		/* (expr --> abs(expr)) */
		else if(mutation.get_operator() == MutaOperator.insert_abs_value) {
			if(StateMutations.is_boolean(expression)) {
				constraint = CirAbstractState.eva_cond(execution, Boolean.FALSE, true);
				init_error = CirAbstractState.set_expr(expression, expression);
			}
			else if(StateMutations.is_numeric(expression)) {
				condition = SymbolFactory.smaller_tn(expression, Integer.valueOf(0));
				constraint = CirAbstractState.eva_cond(execution, condition, true);
				muta_value = SymbolFactory.arith_neg(expression);
				init_error = CirAbstractState.set_expr(expression, muta_value);
			}
			else {
				throw new IllegalArgumentException(expression.generate_code(true));
			}
		}
		/* (expr --> -abs(expr)) */
		else if(mutation.get_operator() == MutaOperator.insert_nabs_value) {
			if(StateMutations.is_boolean(expression)) {
				constraint = CirAbstractState.eva_cond(execution, Boolean.FALSE, true);
				init_error = CirAbstractState.set_expr(expression, expression);
			}
			else if(StateMutations.is_numeric(expression)) {
				condition = SymbolFactory.greater_tn(expression, Integer.valueOf(0));
				constraint = CirAbstractState.eva_cond(execution, condition, true);
				muta_value = SymbolFactory.arith_neg(expression);
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
