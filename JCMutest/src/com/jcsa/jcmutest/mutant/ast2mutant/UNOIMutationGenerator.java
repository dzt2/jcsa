package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UNOIMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception { }

	@Override
	protected boolean available(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			if(this.is_condition_expression(location)) {
				return this.is_numeric_expression(location);
			}
			else if(this.is_numeric_expression(location)) {
				return !this.is_left_reference(location)
						&& !this.is_assign_expression(location);
			}
			else {
				return false;
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
		if(this.is_condition_expression(expression)) {
			mutations.add(AstMutations.UNOI(expression, COperator.logic_and));
		}
		else {
			mutations.add(AstMutations.UNOI(expression, COperator.negative));
			if(CTypeAnalyzer.is_integer(type)) {
				mutations.add(AstMutations.UNOI(expression, COperator.bit_not));
			}
			if(CTypeAnalyzer.is_signed(type)) {
				mutations.add(AstMutations.UNOI(expression, COperator.positive));
			}
			mutations.add(AstMutations.UNOI(expression, COperator.assign));
		}
	}

}
