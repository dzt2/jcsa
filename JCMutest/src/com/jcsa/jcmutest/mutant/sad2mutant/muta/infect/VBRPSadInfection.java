package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadInfection;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class VBRPSadInfection extends SadInfection {

	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		AstExpression location = (AstExpression) mutation.get_location();
		CirExpression expression = this.find_result(tree, location);
		CirStatement statement = expression.statement_of();
		SadAssertion constraint, state_error;
		switch(mutation.get_operator()) {
		case set_true:
		{
			constraint = SadFactory.assert_condition(statement, 
						this.condition_of(expression, false));
			state_error = SadFactory.set_expression(statement, 
						expression, SadFactory.constant(true));
			break;
		}
		case set_false:
		{
			constraint = SadFactory.assert_condition(statement, 
						this.condition_of(expression, true));
			state_error = SadFactory.set_expression(statement, 
						expression, SadFactory.constant(false));
			break;
		}
		default: throw new IllegalArgumentException("Invalid: " + mutation);
		}
		this.connect(reach_node, state_error, constraint);
	}

}
