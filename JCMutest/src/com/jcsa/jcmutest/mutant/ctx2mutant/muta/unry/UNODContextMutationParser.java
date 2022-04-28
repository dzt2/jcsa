package com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UNODContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}

	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		AstUnaryExpression expression = (AstUnaryExpression) mutation.get_location();
		SymbolExpression operand = SymbolFactory.sym_expression(expression.get_operand());
		if(mutation.get_operator() == MutaOperator.delete_arith_neg) {
			this.put_infection(
					this.eva_cond(SymbolFactory.not_equals(operand, 0)), 
					this.set_expr(SymbolFactory.arith_neg(operand), operand));
		}
		else if(mutation.get_operator() == MutaOperator.delete_bitws_rsv) {
			this.put_infection(this.cov_time(1), 
					this.set_expr(SymbolFactory.bitws_rsv(operand), operand));
		}
		else if(mutation.get_operator() == MutaOperator.delete_logic_not) {
			this.put_infection(this.cov_time(1), this.set_expr(
					SymbolFactory.sym_condition(operand, false), 
					SymbolFactory.sym_condition(operand, true)));
		}
		else {
			throw new IllegalArgumentException("Invalid operator: " + mutation);
		}
	}

}
