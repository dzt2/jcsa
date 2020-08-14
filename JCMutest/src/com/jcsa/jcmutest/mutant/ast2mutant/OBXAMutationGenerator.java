package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OBXAMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {}

	@Override
	protected boolean available(AstNode location) throws Exception {
		return (location instanceof AstBitwiseAssignExpression
				|| location instanceof AstShiftAssignExpression)
				&& this.is_numeric_expression(location);
	}
	
	private final COperator[] operators = new COperator[] {
			COperator.assign,
			COperator.arith_add_assign,
			COperator.arith_sub_assign,
			COperator.arith_mul_assign,
			COperator.arith_div_assign,
			COperator.arith_mod_assign,
			COperator.bit_and_assign,
			COperator.bit_or_assign,
			COperator.bit_xor_assign,
			COperator.left_shift_assign,
			COperator.righ_shift_assign
		};
	
	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstBinaryExpression expression = (AstBinaryExpression) location;
		for(COperator operator : this.operators) {
			if(this.is_compatible(expression, operator)) {
				if(expression instanceof AstBitwiseAssignExpression) {
					mutations.add(AstMutations.OBXA((AstBitwiseAssignExpression) expression, operator));
				}
				else {
					mutations.add(AstMutations.OBXA((AstShiftAssignExpression) expression, operator));
				}
			}
		}
	}

}
