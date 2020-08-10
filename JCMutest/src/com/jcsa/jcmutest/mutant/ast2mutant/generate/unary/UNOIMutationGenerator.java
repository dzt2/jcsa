package com.jcsa.jcmutest.mutant.ast2mutant.generate.unary;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UNOIMutationGenerator extends AstMutationGenerator {
	
	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return this.is_numeric_expression(location) 
				&& !this.is_left_reference(location);
	}
	
	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		CType data_type = CTypeAnalyzer.
				get_value_type(expression.get_value_type());
		
		if(this.is_condition_expression(expression)) {
			mutations.add(AstMutations.UNOI(expression, COperator.logic_not));
		}
		else {
			if(CTypeAnalyzer.is_integer(data_type)) {
				mutations.add(AstMutations.UNOI(expression, COperator.bit_not));
			}
			mutations.add(AstMutations.UNOI(expression, COperator.negative));
			mutations.add(AstMutations.UNOI(expression, COperator.positive));
			mutations.add(AstMutations.UNOI(expression, COperator.assign));
		}
	}
	
}
