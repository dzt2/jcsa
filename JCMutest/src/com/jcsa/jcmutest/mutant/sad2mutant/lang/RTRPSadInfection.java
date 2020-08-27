package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadInfection;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class RTRPSadInfection extends SadInfection {

	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		AstExpression location = (AstExpression) mutation.get_location();
		AstExpression parameter = (AstExpression) mutation.get_parameter();
		CirExpression source = this.find_result(tree, location);
		CirExpression target = this.find_result(tree, parameter);
		CirStatement statement = source.statement_of();
		
		SadExpression condition = SadFactory.not_equals(
				CBasicTypeImpl.bool_type, 
				(SadExpression) SadParser.cir_parse(source), 
				(SadExpression) SadParser.cir_parse(target));
		SadAssertion constraint = SadFactory.assert_condition(statement, condition);
		SadAssertion state_error = SadFactory.set_expression(statement, 
					source, (SadExpression) SadParser.cir_parse(target));
		this.connect(reach_node, state_error, constraint);
	}

}
