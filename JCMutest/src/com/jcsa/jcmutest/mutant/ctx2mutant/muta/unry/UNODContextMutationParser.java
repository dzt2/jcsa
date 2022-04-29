package com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UNODContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		AstUnaryExpression expression = (AstUnaryExpression) mutation.get_location();
		AstExpression operand = expression.get_operand();
		SymbolExpression orig_value = SymbolFactory.sym_expression(expression);
		if(mutation.get_operator() == MutaOperator.delete_arith_neg) {
			this.put_infection(this.eva_cond(SymbolFactory.not_equals(operand, 0)), 
					this.set_expr(orig_value, SymbolFactory.sym_expression(operand)));
		}
		else if(mutation.get_operator() == MutaOperator.delete_bitws_rsv) {
			this.put_infection(this.eva_cond(Boolean.TRUE), this.set_expr(orig_value, SymbolFactory.sym_expression(operand)));
		}
		else if(mutation.get_operator() == MutaOperator.delete_logic_not) {
			this.put_infection(this.eva_cond(Boolean.TRUE), this.set_expr(orig_value, SymbolFactory.sym_condition(operand, true)));
		}
		else {
			throw new IllegalArgumentException("Invalid mutation: " + mutation);
		}
	}

}
