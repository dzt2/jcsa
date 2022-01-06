package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.lexical.COperator;

public class ORXNMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {}

	@Override
	protected boolean available(AstNode location) throws Exception {
		if(location instanceof AstRelationExpression) {
			return this.is_numeric_expression(((AstRelationExpression) location).get_loperand())
					&& this.is_numeric_expression(((AstRelationExpression) location).get_roperand());
		}
		else {
			return false;
		}
	}

	private final COperator[] operators = new COperator[] {
			COperator.arith_add, COperator.arith_sub, COperator.arith_mul,
			COperator.arith_div, COperator.arith_mod, COperator.bit_and,
			COperator.bit_or, COperator.bit_xor, COperator.left_shift,
			COperator.righ_shift, COperator.greater_tn, COperator.greater_eq,
			COperator.smaller_tn, COperator.smaller_eq, COperator.not_equals,
			COperator.not_equals
		};

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstBinaryExpression expression = (AstBinaryExpression) location;
		for(COperator operator : this.operators) {
			if(this.is_compatible(expression, operator)) {
				mutations.add(AstMutations.ORXN((AstRelationExpression) expression, operator));
			}
		}
	}

}
