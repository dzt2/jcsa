package com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UIORContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		SymbolExpression orig_value = SymbolFactory.sym_expression(mutation.get_location());
		AstExpression operand = (AstExpression) mutation.get_location();
		if(operand instanceof AstIncreUnaryExpression) {
			operand = ((AstIncreUnaryExpression) operand).get_operand();
		}
		else {
			operand = ((AstIncrePostfixExpression) operand).get_operand();
		}
		
		SymbolExpression muta_value; CType type = operand.get_value_type();
		switch(mutation.get_operator()) {
		case prev_dec_to_prev_inc:
		case post_inc_to_prev_inc:
		case post_dec_to_prev_inc:
			muta_value = SymbolFactory.exp_assign(operand, SymbolFactory.arith_add(type, operand, 1)); break;
		case prev_inc_to_prev_dec:
		case post_inc_to_prev_dec:
		case post_dec_to_prev_dec:
			muta_value = SymbolFactory.exp_assign(operand, SymbolFactory.arith_sub(type, operand, 1)); break;
		case prev_inc_to_post_inc:
		case prev_dec_to_post_inc:
		case post_dec_to_post_inc:
			muta_value = SymbolFactory.imp_assign(operand, SymbolFactory.arith_add(type, operand, 1)); break;
		case prev_inc_to_post_dec:
		case prev_dec_to_post_dec:
		case post_inc_to_post_dec:
			muta_value = SymbolFactory.imp_assign(operand, SymbolFactory.arith_sub(type, operand, 1)); break;
		default:	throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		this.put_infection(this.eva_cond(Boolean.TRUE), this.set_expr(orig_value, muta_value));
	}

}
