package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UIORMutationGenerator extends AstMutationGenerator {

	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstIncreUnaryExpression
			|| location instanceof AstIncrePostfixExpression) {
			locations.add(location);
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		if(expression instanceof AstIncreUnaryExpression) {
			AstIncreUnaryExpression inc_expr = (AstIncreUnaryExpression) expression;
			if(inc_expr.get_operator().get_operator() == COperator.increment) {
				// mutations.add(AstMutation.UIOR(expression, true, COperator.increment));
				mutations.add(AstMutation.UIOR(expression, true, COperator.decrement));
				mutations.add(AstMutation.UIOR(expression, false, COperator.increment));
				mutations.add(AstMutation.UIOR(expression, false, COperator.decrement));
			}
			else {
				mutations.add(AstMutation.UIOR(expression, true, COperator.increment));
				// mutations.add(AstMutation.UIOR(expression, true, COperator.decrement));
				mutations.add(AstMutation.UIOR(expression, false, COperator.increment));
				mutations.add(AstMutation.UIOR(expression, false, COperator.decrement));
			}
		}
		else {
			AstIncrePostfixExpression inc_expr = (AstIncrePostfixExpression) expression;
			if(inc_expr.get_operator().get_operator() == COperator.increment) {
				mutations.add(AstMutation.UIOR(expression, true, COperator.increment));
				mutations.add(AstMutation.UIOR(expression, true, COperator.decrement));
				// mutations.add(AstMutation.UIOR(expression, false, COperator.increment));
				mutations.add(AstMutation.UIOR(expression, false, COperator.decrement));
			}
			else {
				mutations.add(AstMutation.UIOR(expression, true, COperator.increment));
				mutations.add(AstMutation.UIOR(expression, true, COperator.decrement));
				mutations.add(AstMutation.UIOR(expression, false, COperator.increment));
				// mutations.add(AstMutation.UIOR(expression, false, COperator.decrement));
			}
		}
	}

}
