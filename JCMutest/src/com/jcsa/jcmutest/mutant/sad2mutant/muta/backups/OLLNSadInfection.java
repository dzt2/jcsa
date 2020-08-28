package com.jcsa.jcmutest.mutant.sad2mutant.muta.backups;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.SadInfection;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OLLNSadInfection extends SadInfection {

	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		AstBinaryExpression location = (AstBinaryExpression) mutation.get_location();
		CirExpression expression = this.find_result(tree, location);
		CirStatement statement = expression.statement_of();
		CirExpression loperand = this.find_result(tree, CTypeAnalyzer.get_expression_of(location.get_loperand()));
		CirExpression roperand = this.find_result(tree, CTypeAnalyzer.get_expression_of(location.get_roperand()));
		COperator operator = location.get_operator().get_operator();
		COperator parameter = (COperator) mutation.get_parameter();
		
		if(statement != null) {
			SadExpression condition = this.not_equals(
					this.condition_of(loperand, true), this.condition_of(roperand, true));
			SadAssertion constraint = SadFactory.assert_condition(statement, condition);
			
			SadAssertion state_error;
			if(operator == COperator.logic_and) {
				if(parameter == COperator.logic_or) {
					state_error = SadFactory.set_expression(statement, expression, this.sad_expression(true));
				}
				else {
					throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else if(operator == COperator.logic_or) {
				if(parameter == COperator.logic_and) {
					state_error = SadFactory.set_expression(statement, expression, this.sad_expression(false));
				}
				else {
					throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else {
				throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			
			this.connect(reach_node, state_error, constraint);
		}
	}

}
