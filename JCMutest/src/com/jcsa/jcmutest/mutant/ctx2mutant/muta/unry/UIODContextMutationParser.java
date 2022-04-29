package com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UIODContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		AstExpression operand;
		if(expression instanceof AstIncreUnaryExpression) {
			operand = ((AstIncreUnaryExpression) expression).get_operand();
		}
		else {
			operand = ((AstIncrePostfixExpression) expression).get_operand();
		}
		this.put_infection(this.eva_cond(Boolean.TRUE), this.set_expr(
							SymbolFactory.sym_expression(expression), 
							SymbolFactory.sym_expression(operand)));
	}

}
