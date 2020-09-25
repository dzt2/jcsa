package com.jcsa.jcmutest.backups;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class VCRPStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_beg_statement(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected CirStatement find_end_statement(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		SecConstraint constraint; SecStateError init_error;
		CirExpression orig_expression = this.get_cir_expression(mutation.get_location());
		SymExpression muta_expression = SymFactory.parse(mutation.get_parameter());
		
		constraint = this.get_constraint(this.sym_condition(COperator.
				not_equals, orig_expression, muta_expression), true);
		init_error = this.set_expression(orig_expression, muta_expression);
		this.add_infection(constraint, init_error); return true;
	}

}
