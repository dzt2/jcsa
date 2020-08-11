package com.jcsa.jcmutest.mutant.ast2mutant.generate.trap;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VTRPMutationGenerator extends AstMutationGenerator {
	
	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return this.is_numeric_expression(location) 
				&& !this.is_left_reference(location);
	}
	
	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		expression = CTypeAnalyzer.get_expression_of(expression);
		CType type = CTypeAnalyzer.get_value_type(expression.get_value_type());
		
		if(!(expression instanceof AstConstant)) {
			mutations.add(AstMutations.trap_on_pos(expression));
			mutations.add(AstMutations.trap_on_zro(expression));
			if(CTypeAnalyzer.is_signed(type))
				mutations.add(AstMutations.trap_on_neg(expression));
		}
	}
	
}
