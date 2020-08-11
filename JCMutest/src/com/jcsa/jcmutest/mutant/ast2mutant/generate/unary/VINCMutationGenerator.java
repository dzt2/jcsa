package com.jcsa.jcmutest.mutant.ast2mutant.generate.unary;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VINCMutationGenerator extends AstMutationGenerator {
	
	private int[] int_differences = new int[] {
		1, 2,
		-1,-2
	};
	
	private double[] float_multiplies = new double[] {
		1.0001, 1.01,
		0.9999, 0.99
	};

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return this.is_numeric_expression(location) && 
				!this.is_left_reference(location) &&
				!this.is_condition_expression(location);
	}

	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_value_type());
		
		if(CTypeAnalyzer.is_integer(data_type)) {
			for(int difference : this.int_differences) {
				mutations.add(AstMutations.inc_constant(expression, difference));
			}
		}
		else if(CTypeAnalyzer.is_real(data_type)) {
			for(double multiply : this.float_multiplies) {
				mutations.add(AstMutations.mul_constant(expression, multiply));
			}
		}
	}

}
