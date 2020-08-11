package com.jcsa.jcmutest.mutant.ast2mutant.generate.oprt;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OLXNMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return location instanceof AstLogicBinaryExpression;
	}
	
	private final COperator[] operators = new COperator[] {
			COperator.arith_add, COperator.arith_sub, COperator.arith_mul,
			COperator.arith_div, COperator.arith_mod, COperator.bit_and,
			COperator.bit_or, COperator.bit_xor, COperator.left_shift,
			COperator.righ_shift, COperator.logic_and, COperator.logic_or,
			COperator.greater_tn, COperator.greater_eq, COperator.smaller_tn,
			COperator.smaller_eq, COperator.equal_with, COperator.not_equals
	};
	
	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstLogicBinaryExpression expression = (AstLogicBinaryExpression) location;
		for(COperator operator : this.operators) {
			if(this.is_compatible(expression, operator)) {
				mutations.add(AstMutations.OLXN(expression, operator));
			}
		}
	}

}
