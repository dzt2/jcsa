package com.jcsa.jcmutest.mutant.sec2mutant.mut.unry;

import java.util.ArrayList;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.StateMutationParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class UIOIStateMutationParser extends StateMutationParser {
	
	@Override
	protected CirStatement find_beg_statement(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}
	
	@Override
	protected CirStatement find_end_statement(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}
	
	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		CirExpression expression = get_cir_expression(mutation.get_location());
		ArrayList<SecStateError> init_errors = new ArrayList<SecStateError>();
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		
		switch(mutation.get_operator()) {
		case insert_prev_inc:
		{
			init_errors.add(this.add_reference(expression, Integer.valueOf(1)));
			if(expression.statement_of() != null) {
				init_errors.add(this.add_expression(expression, Integer.valueOf(1)));
			}
			break;
		}
		case insert_prev_dec:
		{
			init_errors.add(this.sub_reference(expression, Integer.valueOf(1)));
			if(expression.statement_of() != null) {
				init_errors.add(this.sub_expression(expression, Integer.valueOf(1)));
			}
			break;
		}
		case insert_post_inc:
		{
			init_errors.add(this.add_reference(expression, Integer.valueOf(1)));
			break;
		}
		case insert_post_dec:
		{
			init_errors.add(this.sub_reference(expression, Integer.valueOf(1)));
			break;
		}
		default: return false;
		}
		
		for(SecStateError init_error : init_errors) {
			this.add_infection(constraint, init_error);
		}
		return !init_errors.isEmpty();
	}
	
}
