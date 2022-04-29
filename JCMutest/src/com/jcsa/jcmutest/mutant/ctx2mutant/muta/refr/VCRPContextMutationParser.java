package com.jcsa.jcmutest.mutant.ctx2mutant.muta.refr;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VCRPContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		SymbolExpression orig_value = SymbolFactory.sym_expression(mutation.get_location());
		SymbolExpression muta_value = SymbolFactory.sym_expression(mutation.get_parameter());
		this.put_infection(this.eva_cond(SymbolFactory.not_equals(orig_value, muta_value)), 
				this.set_expr(orig_value, muta_value));
	}

}
