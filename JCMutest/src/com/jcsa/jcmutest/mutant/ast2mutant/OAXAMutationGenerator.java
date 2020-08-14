package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OAXAMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {}

	@Override
	protected boolean available(AstNode location) throws Exception {
		return location instanceof AstArithAssignExpression
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
				mutations.add(AstMutations.OAXA((AstArithAssignExpression) expression, operator));
			}
		}
	}

}
