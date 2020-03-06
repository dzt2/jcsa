package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

public class EQARMutationGenerator extends AstMutationGenerator {
	
	private boolean is_refence(AstExpression expression) throws Exception {
		expression = CTypeAnalyzer.get_expression_of(expression);
		if(expression instanceof AstArrayExpression
				|| expression instanceof AstIdExpression
				|| expression instanceof AstFieldExpression) {
			return true;
		}
		else if(expression instanceof AstPointUnaryExpression) {
			return ((AstPointUnaryExpression) expression).
					get_operator().get_operator() == COperator.dereference;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstRelationExpression) {
			COperator operator = 
					((AstRelationExpression) location).get_operator().get_operator();
			if(operator == COperator.equal_with) { 
				if(this.is_refence(((AstRelationExpression) location).get_loperand())) {
					locations.add(location);
				}
			}
		}
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstRelationExpression expression = (AstRelationExpression) location;
		mutations.add(AstMutation.EQAR(expression));
	}

}
