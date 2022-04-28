package com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UIODContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}

	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		SymbolExpression orig_value, muta_value; AstExpression operand;
		if(expression instanceof AstIncreUnaryExpression) {
			operand = ((AstIncreUnaryExpression) expression).get_operand();
		}
		else if(expression instanceof AstIncrePostfixExpression) {
			operand = ((AstIncrePostfixExpression) expression).get_operand();
		}
		else {
			throw new IllegalArgumentException(expression.generate_code());
		}
		orig_value = SymbolFactory.sym_expression(expression);
		muta_value = SymbolFactory.sym_expression(operand);
		this.put_infection(this.cov_time(1), this.set_expr(orig_value, muta_value));
	}

}
