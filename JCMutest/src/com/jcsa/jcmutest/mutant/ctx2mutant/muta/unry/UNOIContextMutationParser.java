package com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UNOIContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}

	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		SymbolExpression orig_value = SymbolFactory.sym_expression(expression); 
		if(mutation.get_operator() == MutaOperator.insert_arith_neg) {
			if(SymbolFactory.is_bool(orig_value)) {
				this.put_infection(this.eva_cond(Boolean.FALSE), 
								this.set_expr(orig_value, orig_value));
			}
			else {
				this.put_infection(
						this.eva_cond(SymbolFactory.not_equals(orig_value, 0)), 
						this.set_expr(orig_value, SymbolFactory.arith_neg(orig_value)));
			}
		}
		else if(mutation.get_operator() == MutaOperator.insert_bitws_rsv) {
			if(SymbolFactory.is_bool(orig_value)) {
				this.put_infection(
						this.eva_cond(SymbolFactory.sym_condition(orig_value, false)),
						this.set_expr(orig_value, SymbolFactory.sym_constant(Boolean.TRUE)));
			}
			else {
				this.put_infection(this.cov_time(1), this.set_expr(orig_value, SymbolFactory.bitws_rsv(orig_value)));
			}
		}
		else if(mutation.get_operator() == MutaOperator.insert_logic_not) {
			this.put_infection(this.cov_time(1), this.set_expr(
					SymbolFactory.sym_condition(expression, true), 
					SymbolFactory.sym_condition(expression, false)));
		}
		else if(mutation.get_operator() == MutaOperator.insert_abs_value) {
			if(SymbolFactory.is_bool(orig_value)) {
				this.put_infection(this.eva_cond(Boolean.FALSE), this.set_expr(orig_value, orig_value));
			}
			else {
				this.put_infection(
						this.eva_cond(SymbolFactory.smaller_tn(expression, 0)), 
						this.set_expr(orig_value, SymbolFactory.arith_neg(orig_value)));
			}
		}
		else if(mutation.get_operator() == MutaOperator.insert_nabs_value) {
			if(SymbolFactory.is_bool(orig_value)) {
				this.put_infection(this.eva_cond(Boolean.FALSE), this.set_expr(orig_value, orig_value));
			}
			else {
				this.put_infection(
						this.eva_cond(SymbolFactory.greater_tn(expression, 0)), 
						this.set_expr(orig_value, SymbolFactory.arith_neg(orig_value)));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + mutation);
		}
	}

}
