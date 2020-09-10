package com.jcsa.jcmutest.mutant.sec2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class UNOIInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		CirExpression expression = 
				this.get_cir_expression(mutation.get_location());
		SecConstraint constraint; SecDescription init_error;
		
		switch(mutation.get_operator()) {
		case insert_arith_neg:
		{
			constraint = this.get_constraint(SymFactory.not_equals(expression, Integer.valueOf(0)), true);
			init_error = this.neg_expression(expression);
			break;
		}
		case insert_bitws_rsv:
		{
			constraint = this.get_constraint(Boolean.TRUE, true);
			init_error = this.rsv_expression(expression);
			break;
		}
		case insert_logic_not:
		{
			constraint = this.get_constraint(Boolean.TRUE, true);
			init_error = this.not_expression(expression);
			break;
		}
		case insert_abs_value:
		{
			constraint = this.get_constraint(SymFactory.smaller_tn(expression, Integer.valueOf(0)), true);
			init_error = this.neg_expression(expression);
			break;
		}
		case insert_nabs_value:
		{
			constraint = this.get_constraint(SymFactory.greater_tn(expression, Integer.valueOf(0)), true);
			init_error = this.neg_expression(expression);
			break;
		}
		default:	return false;
		}
		
		this.add_infection(constraint, init_error);
		return true;
	}

}
