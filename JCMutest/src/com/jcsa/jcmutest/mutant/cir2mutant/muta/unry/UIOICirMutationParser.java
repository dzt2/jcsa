package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class UIOICirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		CirExpression reference = this.get_cir_expression(mutation.get_location());
		CirConditionState constraint = CirAbstractState.cov_time(this.get_r_execution(), 1);
		
		CirAbstErrorState init_error;
		if(mutation.get_operator() == MutaOperator.insert_post_inc) {
			init_error = CirAbstractState.inc_vdef(reference, Integer.valueOf(1));
			this.put_infection_pair(constraint, init_error);
		}
		else if(mutation.get_operator() == MutaOperator.insert_post_dec) {
			init_error = CirAbstractState.inc_vdef(reference, Integer.valueOf(-1));
			this.put_infection_pair(constraint, init_error);
		}
		else if(mutation.get_operator() == MutaOperator.insert_prev_inc) {
			init_error = CirAbstractState.inc_expr(reference, Integer.valueOf(1));
			this.put_infection_pair(constraint, init_error);
			
			init_error = CirAbstractState.inc_vdef(reference, Integer.valueOf(1));
			this.put_infection_pair(constraint, init_error);
		}
		else if(mutation.get_operator() == MutaOperator.insert_prev_dec) {
			init_error = CirAbstractState.inc_expr(reference, Integer.valueOf(-1));
			this.put_infection_pair(constraint, init_error);
			
			init_error = CirAbstractState.inc_vdef(reference, Integer.valueOf(-1));
			this.put_infection_pair(constraint, init_error);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + mutation.toString());
		}
	}

}
