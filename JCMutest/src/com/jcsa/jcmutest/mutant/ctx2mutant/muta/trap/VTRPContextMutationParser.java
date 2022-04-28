package com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VTRPContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}

	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		SymbolExpression condition;
		if(mutation.get_operator() == MutaOperator.trap_on_pos) {
			if(SymbolFactory.is_bool(expression.get_value_type())) {
				condition = SymbolFactory.sym_condition(expression, true);
			}
			else {
				condition = SymbolFactory.greater_tn(expression, Integer.valueOf(0));
			}
		}
		else if(mutation.get_operator() == MutaOperator.trap_on_zro) {
			if(SymbolFactory.is_bool(expression.get_value_type())) {
				condition = SymbolFactory.sym_condition(expression, false);
			}
			else {
				condition = SymbolFactory.equal_with(expression, Integer.valueOf(0));
			}
		}
		else if(mutation.get_operator() == MutaOperator.trap_on_neg) {
			if(SymbolFactory.is_bool(expression.get_value_type())) {
				condition = SymbolFactory.sym_constant(Boolean.FALSE);
			}
			else {
				condition = SymbolFactory.smaller_tn(expression, Integer.valueOf(0));
			}
		}
		else if(mutation.get_operator() == MutaOperator.trap_on_dif) {
			condition = SymbolFactory.not_equals(expression, mutation.get_parameter());
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + mutation);
		}
		this.put_infection(this.eva_cond(condition), this.mut_trap());
	}

}
