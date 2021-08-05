package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VINCMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {}

	private final int[] inc_constants = new int[] {
		1, 2, 3, -1, -2, -3
	};

	private final double[] mul_constants = new double[] {
		0.9, 0.99, 0.999, 1.1, 1.01, 1.001,
	};

	@Override
	protected boolean available(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			if(this.is_condition_expression(location)) {
				return false;
			}
			else {
				return this.is_numeric_expression(location)
						&& !this.is_left_reference(location)
						&& !this.is_assign_expression(location);
			}
		}
		else {
			return false;
		}
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		expression = CTypeAnalyzer.get_expression_of(expression);
		CType type = CTypeAnalyzer.get_value_type(expression.get_value_type());

		if(CTypeAnalyzer.is_integer(type)) {
			for(int inc_constant : this.inc_constants) {
				mutations.add(AstMutations.inc_constant(expression, inc_constant));
			}
		}
		else if(CTypeAnalyzer.is_real(type)) {
			for(double mul_constant : this.mul_constants) {
				mutations.add(AstMutations.mul_constant(expression, mul_constant));
			}
		}
	}

}
