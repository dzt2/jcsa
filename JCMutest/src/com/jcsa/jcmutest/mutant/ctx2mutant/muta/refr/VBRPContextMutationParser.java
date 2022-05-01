package com.jcsa.jcmutest.mutant.ctx2mutant.muta.refr;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VBRPContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		if(mutation.get_operator() == MutaOperator.set_true) {
			this.put_infection(this.eva_cond(SymbolFactory.sym_condition(expression, false)), this.set_expr(
					SymbolFactory.sym_condition(expression, true), SymbolFactory.sym_constant(Boolean.TRUE)));
		}
		else {
			this.put_infection(this.eva_cond(SymbolFactory.sym_condition(expression, true)), this.set_expr(
					SymbolFactory.sym_condition(expression, true), SymbolFactory.sym_constant(Boolean.FALSE)));
		}
	}

}
