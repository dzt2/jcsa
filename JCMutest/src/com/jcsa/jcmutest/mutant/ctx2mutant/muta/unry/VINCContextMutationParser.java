package com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VINCContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}

	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		SymbolExpression orig_value = SymbolFactory.sym_expression(expression);
		SymbolExpression muta_value; Object parameter = mutation.get_parameter();
		if(mutation.get_operator() == MutaOperator.inc_constant) {
			muta_value = SymbolFactory.arith_add(expression.get_value_type(), orig_value, parameter);
		}
		else if(mutation.get_operator() == MutaOperator.mul_constant) {
			muta_value = SymbolFactory.arith_mul(expression.get_value_type(), orig_value, parameter);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + mutation);
		}
		this.put_infection(this.cov_time(1, Integer.MAX_VALUE), this.set_expr(orig_value, muta_value));
	}

}
