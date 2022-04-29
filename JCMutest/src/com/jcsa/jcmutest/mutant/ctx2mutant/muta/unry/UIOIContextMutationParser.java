package com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UIOIContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		SymbolExpression orig_value = SymbolFactory.sym_expression(mutation.get_location());
		SymbolExpression muta_value; CType type = orig_value.get_data_type();
		switch(mutation.get_operator()) {
		case insert_prev_inc:	
			muta_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.arith_add(type, orig_value, 1));
			break;
		case insert_prev_dec:	
			muta_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.arith_sub(type, orig_value, 1));
			break;
		case insert_post_inc:	
			muta_value = SymbolFactory.imp_assign(orig_value, SymbolFactory.arith_add(type, orig_value, 1));
			break;
		case insert_post_dec:	
			muta_value = SymbolFactory.imp_assign(orig_value, SymbolFactory.arith_sub(type, orig_value, 1));
			break;
		default:	throw new IllegalArgumentException("Invalid: " + mutation.get_operator());
		}
		this.put_infection(this.eva_cond(Boolean.TRUE), this.set_expr(orig_value, muta_value));
	}

}
