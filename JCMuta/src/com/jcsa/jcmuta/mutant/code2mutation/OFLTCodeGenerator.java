package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OFLTCodeGenerator extends ETRPCodeGenerator {

	@Override
	protected void generate_weakness_code(AstMutation mutation) throws Exception {
		AstRelationExpression expression = (AstRelationExpression) mutation.get_location();
		AstExpression loperand = CTypeAnalyzer.get_expression_of(expression.get_loperand());
		AstExpression roperand = CTypeAnalyzer.get_expression_of(expression.get_roperand());
		String loperand_code = "(" + loperand.get_code() + ")";
		String roperand_code = "(" + roperand.get_code() + ")";
		
		String replace;
		if(expression.get_operator().get_operator() == COperator.equal_with) {
			replace = String.format(
					MutaCodeTemplates.real_equal_with_template, 
					"_weak", loperand_code, roperand_code);
		}
		else {
			replace = String.format(
					MutaCodeTemplates.real_not_equals_template,
					"_weak", loperand_code, roperand_code);
		}
		
		this.replace_muta_code(expression, replace);
	}

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstRelationExpression expression = (AstRelationExpression) mutation.get_location();
		AstExpression loperand = CTypeAnalyzer.get_expression_of(expression.get_loperand());
		AstExpression roperand = CTypeAnalyzer.get_expression_of(expression.get_roperand());
		String loperand_code = "(" + loperand.get_code() + ")";
		String roperand_code = "(" + roperand.get_code() + ")";
		
		String replace;
		if(expression.get_operator().get_operator() == COperator.equal_with) {
			replace = String.format(
					MutaCodeTemplates.real_equal_with_template, 
					"", loperand_code, roperand_code);
		}
		else {
			replace = String.format(
					MutaCodeTemplates.real_not_equals_template,
					"", loperand_code, roperand_code);
		}
		
		this.replace_muta_code(expression, replace);
	}

}
