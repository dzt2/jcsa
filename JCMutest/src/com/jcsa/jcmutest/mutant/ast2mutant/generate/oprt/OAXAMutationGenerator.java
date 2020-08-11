package com.jcsa.jcmutest.mutant.ast2mutant.generate.oprt;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OAXAMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return location instanceof AstArithAssignExpression;
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
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstArithAssignExpression expression = (AstArithAssignExpression) location;
		for(COperator operator : this.operators) {
			if(this.is_compatible(expression, operator)) {
				mutations.add(AstMutations.OAXA(expression, operator));
			}
		}
	}

}
