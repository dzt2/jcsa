package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VTRPMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception { }

	@Override
	protected boolean available(AstNode location) throws Exception {
		if(location instanceof AstExpression) {
			if(!this.is_condition_expression(location)) {
				if(this.is_numeric_expression(location)) {
					if(!this.is_left_reference(location)) {
						if(!this.is_assign_expression(location)) {
							return true;
						}
						else {
							return location instanceof AstIncreUnaryExpression ||
									location instanceof AstIncrePostfixExpression;
						}
					}
					else {
						return false;
					}
				}
				else {
					return false;
				}
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
		CType data_type = CTypeAnalyzer.
					get_value_type(expression.get_value_type());
		mutations.add(AstMutations.trap_on_pos(expression));
		mutations.add(AstMutations.trap_on_zro(expression));
		if(CTypeAnalyzer.is_signed(data_type)) {
			mutations.add(AstMutations.trap_on_neg(expression));
		}
	}

}
