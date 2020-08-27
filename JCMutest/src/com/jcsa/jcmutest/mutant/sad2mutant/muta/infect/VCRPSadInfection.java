package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadParser;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadInfection;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class VCRPSadInfection extends SadInfection {

	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		AstExpression location = (AstExpression) mutation.get_location();
		CirExpression expression = this.find_result(tree, location);
		CirStatement statement = expression.statement_of();
		Object parameter = mutation.get_parameter();
		
		if(statement != null) {
			SadExpression condition; SadAssertion constraint, state_error;
			if(parameter instanceof Long) {
				long value = ((Long) parameter).longValue();
				condition = SadFactory.not_equals(CBasicTypeImpl.bool_type, (SadExpression)
							SadParser.cir_parse(expression), SadFactory.constant(value));
				constraint = SadFactory.assert_condition(statement, condition);
				state_error = SadFactory.set_expression(statement, expression, SadFactory.constant(value));
			}
			else {
				double value = ((Double) parameter).doubleValue();
				condition = SadFactory.not_equals(CBasicTypeImpl.bool_type, (SadExpression)
						SadParser.cir_parse(expression), SadFactory.constant(value));
				constraint = SadFactory.assert_condition(statement, condition);
				state_error = SadFactory.set_expression(statement, expression, SadFactory.constant(value));
			}
			this.connect(reach_node, state_error, constraint);
		}
	}

}
