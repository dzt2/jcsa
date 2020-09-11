package com.jcsa.jcmutest.mutant.sec2mutant.muta.unry;

import java.util.ArrayList;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class UIOIInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		CirExpression expression = get_cir_expression(mutation.get_location());
		ArrayList<SecDescription> init_errors = new ArrayList<SecDescription>();
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		
		switch(mutation.get_operator()) {
		case insert_prev_inc:
		{
			init_errors.add(this.inc_reference(expression));
			if(expression.statement_of() != null) {
				init_errors.add(this.add_expression(expression, Integer.valueOf(1)));
			}
			break;
		}
		case insert_prev_dec:
		{
			init_errors.add(this.dec_reference(expression));
			if(expression.statement_of() != null) {
				init_errors.add(this.sub_expression(expression, Integer.valueOf(1)));
			}
			break;
		}
		case insert_post_inc:
		{
			init_errors.add(this.inc_reference(expression));
			break;
		}
		case insert_post_dec:
		{
			init_errors.add(this.dec_reference(expression));
			break;
		}
		default: return false;
		}
		
		if(init_errors.isEmpty()) {
			return false;
		}
		else if(init_errors.size() == 1) {
			this.add_infection(constraint, init_errors.get(0));
		}
		else {
			this.add_infection(constraint, this.conjunct(init_errors));
		}
		return true;
	}

}
