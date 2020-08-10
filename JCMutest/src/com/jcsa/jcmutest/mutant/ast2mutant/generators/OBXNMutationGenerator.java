package com.jcsa.jcmutest.mutant.ast2mutant.generators;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OBXNMutationGenerator extends AstMutationGenerator {
	
	private COperator[] operators = new COperator[] {
			COperator.arith_add, 
			COperator.arith_sub, 
			COperator.arith_mul, 
			COperator.arith_div, 
			COperator.arith_mod,
			COperator.bit_and, 
			COperator.bit_or, 
			COperator.bit_xor, 
			COperator.left_shift,
			COperator.righ_shift,
			COperator.logic_and,
			COperator.logic_or,
			COperator.greater_tn,
			COperator.greater_eq,
			COperator.smaller_tn,
			COperator.smaller_eq,
			COperator.equal_with,
			COperator.not_equals
	};

	@Override
	protected boolean is_seeded_location(AstNode location) throws Exception {
		return location instanceof AstBitwiseBinaryExpression
				|| location instanceof AstShiftBinaryExpression;
	}

	@Override
	protected Iterable<AstMutation> seed_mutations(AstNode location) throws Exception {
		AstBinaryExpression expression = (AstBinaryExpression) location;
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		for(COperator operator : this.operators) {
			if(operator != expression.get_operator().get_operator()) {
				if(expression instanceof AstBitwiseBinaryExpression) {
					mutations.add(AstMutations.OBXN((AstBitwiseBinaryExpression) expression, operator));
				}
				else {
					mutations.add(AstMutations.OBXN((AstShiftBinaryExpression) expression, operator));
				}
			}
		}
		return mutations;
	}
	
}
