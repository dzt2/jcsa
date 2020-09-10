package com.jcsa.jcmutest.mutant.sec2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class VINCInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		SymExpression operand = SymFactory.new_constant(mutation.get_parameter());
		
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		SecDescription init_error;
		switch(mutation.get_operator()) {
		case inc_constant:	init_error = this.add_expression(expression, operand); break;
		case mul_constant:	init_error = this.mul_expression(expression, operand); break;
		default:			return false;
		}
		
		this.add_infection(constraint, init_error); return true;
	}

}