package com.jcsa.jcmutest.mutant.sec2mutant.muta.refs;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class VBRPInfectionParser extends SecInfectionParser {
	
	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}
	
	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		SecDescription constraint, init_error;
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		
		switch(mutation.get_operator()) {
		case set_true:
		{
			constraint = this.get_constraint(expression, false);
			init_error = this.set_expression(expression, Boolean.TRUE);
			break;
		}
		case set_false:
		{
			constraint = this.get_constraint(expression, true);
			init_error = this.set_expression(expression, Boolean.FALSE);
			break;
		}
		default: return false;
		}
		
		this.add_infection(constraint, init_error); return true;
	}
	
}
