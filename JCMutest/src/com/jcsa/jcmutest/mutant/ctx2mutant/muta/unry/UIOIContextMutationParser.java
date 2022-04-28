package com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UIOIContextMutationParser extends ContextMutationParser {
	
	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}
	
	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		SymbolExpression orig_value = SymbolFactory.sym_expression(expression);
		SymbolExpression muta_value; CType type = expression.get_value_type();
		switch(mutation.get_operator()) {
		case insert_prev_inc:	muta_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.arith_add(type, orig_value, 1)); break;
		case insert_prev_dec:	muta_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.arith_sub(type, orig_value, 1)); break;
		case insert_post_inc:	muta_value = SymbolFactory.imp_assign(orig_value, SymbolFactory.arith_add(type, orig_value, 1)); break;
		case insert_post_dec:	muta_value = SymbolFactory.imp_assign(orig_value, SymbolFactory.arith_sub(type, orig_value, 1)); break;
		default:				throw new IllegalArgumentException(mutation.get_operator().toString());
		}
		this.put_infection(this.cov_time(1), this.set_expr(orig_value, muta_value));
	}
	
}
