package com.jcsa.jcmutest.mutant.ctx2mutant.muta.refr;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VCRPContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}

	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		SymbolExpression 	orig_value = SymbolFactory.sym_expression(mutation.get_location());
		SymbolConstant 		muta_value = SymbolFactory.sym_constant(mutation.get_parameter());
		this.put_infection(
				this.eva_cond(SymbolFactory.not_equals(orig_value, muta_value), false), 
				this.set_expr(orig_value, muta_value));
	}

}
