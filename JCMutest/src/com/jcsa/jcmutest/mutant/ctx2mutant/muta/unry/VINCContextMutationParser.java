package com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VINCContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		SymbolExpression orig_value = SymbolFactory.sym_expression(mutation.get_location());
		SymbolExpression difference = SymbolFactory.sym_expression(mutation.get_parameter());
		if(mutation.get_operator() == MutaOperator.inc_constant) {
			this.put_infection(this.eva_cond(Boolean.TRUE), this.set_expr(orig_value, 
					SymbolFactory.arith_add(orig_value.get_data_type(), orig_value, difference)));
		}
		else {
			this.put_infection(this.eva_cond(SymbolFactory.not_equals(orig_value, 0)), this.set_expr(orig_value, 
					SymbolFactory.arith_mul(orig_value.get_data_type(), orig_value, difference)));
		}
	}

}
