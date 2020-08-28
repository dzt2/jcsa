package com.jcsa.jcmutest.mutant.sad2mutant.muta.backups;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.SadInfection;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OLRNSadInfection extends SadInfection {

	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		AstBinaryExpression location = (AstBinaryExpression) mutation.get_location();
		CirExpression expression = this.find_result(tree, location);
		CirStatement statement = expression.statement_of();
		// CirExpression loperand = this.find_result(tree, CTypeAnalyzer.get_expression_of(location.get_loperand()));
		// CirExpression roperand = this.find_result(tree, CTypeAnalyzer.get_expression_of(location.get_roperand()));
		COperator operator = location.get_operator().get_operator();
		COperator parameter = (COperator) mutation.get_parameter();
		
		if(statement != null) {
			if(operator == COperator.logic_and) {
				switch(parameter) {
				case greater_tn: break;
				case greater_eq: break;
				case smaller_tn: break;
				case smaller_eq: break;
				case equal_with: break;
				case not_equals: break;
				default: throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else if(operator == COperator.logic_or) {
				switch(parameter) {
				case greater_tn: break;
				case greater_eq: break;
				case smaller_tn: break;
				case smaller_eq: break;
				case equal_with: break;
				case not_equals: break;
				default: throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else {
				throw new IllegalArgumentException("Invalid: " + operator);
			}
		}
	}
	
}
