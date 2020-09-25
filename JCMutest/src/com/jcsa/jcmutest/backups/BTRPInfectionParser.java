package com.jcsa.jcmutest.backups;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class BTRPInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		boolean value;
		switch(mutation.get_operator()) {
		case trap_on_true:	value = true;	break;
		case trap_on_false:	value = false;	break;
		default: return false;
		}
		
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		SecConstraint constraint = this.get_constraint(expression, value);
		SecStateError init_error = this.trap_statement(statement);
		this.add_infection(constraint, init_error);
		return true;
	}

}
