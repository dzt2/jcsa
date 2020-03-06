package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class EQARCodeGenerator extends ETRPCodeGenerator {
	
	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstRelationExpression expression = (AstRelationExpression) mutation.get_location();
		AstExpression loperand = CTypeAnalyzer.get_expression_of(expression.get_loperand());
		AstExpression roperand = CTypeAnalyzer.get_expression_of(expression.get_roperand());
		
		String loperand_code = "(" + loperand.get_code() + ")";
		String roperand_code = "(" + roperand.get_code() + ")";
		String replace = "(" + loperand_code + " = " + roperand_code + ")";
		
		this.replace_muta_code(expression, replace);
	}

}
