package com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class BTRPContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		SymbolExpression condition;
		AstExpression expression = (AstExpression) location.get_ast_source();
		switch(mutation.get_operator()) {
		case trap_on_true:	condition = SymbolFactory.sym_condition(expression, true);	break;
		case trap_on_false:	condition = SymbolFactory.sym_condition(expression, false); break;
		default:			throw new IllegalArgumentException("Invalid operator: " + mutation);
		}
		this.put_infection(this.eva_cond(condition), this.trp_stmt());
	}

}
