package com.jcsa.jcmutest.backups;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class VINCStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_beg_statement(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected CirStatement find_end_statement(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		SymExpression operand = SymFactory.new_constant(mutation.get_parameter());
		
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		SecStateError init_error;
		switch(mutation.get_operator()) {
		case inc_constant:	init_error = this.add_expression(expression, operand); break;
		case mul_constant:	init_error = this.mul_expression(expression, operand); break;
		default:			return false;
		}
		
		this.add_infection(constraint, init_error); return true;
	}

}
