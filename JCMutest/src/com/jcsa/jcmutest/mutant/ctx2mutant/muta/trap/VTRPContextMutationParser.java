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
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		SymbolExpression orig_value = SymbolFactory.sym_expression(expression);
		if(mutation.get_operator() == MutaOperator.trap_on_pos) {
			if(SymbolFactory.is_bool(orig_value)) {
				this.put_infection(this.eva_cond(SymbolFactory.sym_condition(orig_value, true)), this.trp_stmt());
			}
			else {
				this.put_infection(this.eva_cond(SymbolFactory.greater_tn(orig_value, Integer.valueOf(0))), this.trp_stmt());
			}
		}
		else if(mutation.get_operator() == MutaOperator.trap_on_zro) {
			if(SymbolFactory.is_bool(orig_value)) {
				this.put_infection(this.eva_cond(SymbolFactory.sym_condition(orig_value, false)), this.trp_stmt());
			}
			else {
				this.put_infection(this.eva_cond(SymbolFactory.equal_with(orig_value, Integer.valueOf(0))), this.trp_stmt());
			}
		}
		else if(mutation.get_operator() == MutaOperator.trap_on_neg) {
			if(SymbolFactory.is_bool(orig_value)) {
				this.put_infection(this.eva_cond(Boolean.FALSE), this.trp_stmt());
			}
			else {
				this.put_infection(this.eva_cond(SymbolFactory.smaller_tn(orig_value, Integer.valueOf(0))), this.trp_stmt());
			}
		}
		else if(mutation.get_operator() == MutaOperator.trap_on_dif) {
			this.put_infection(this.eva_cond(SymbolFactory.not_equals(orig_value, mutation.get_parameter())), this.trp_stmt());
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + mutation);
		}
	}

}
