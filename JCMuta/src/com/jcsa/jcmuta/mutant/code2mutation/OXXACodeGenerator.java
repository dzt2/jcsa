package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class OXXACodeGenerator extends MutaCodeGenerator {

	@Override
	protected void generate_coverage_code(AstMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		expression = CTypeAnalyzer.get_expression_of(expression);
		String expr_code = "(" + expression.get_location().read() + ")";
		
		String replace = String.format(
				MutaCodeTemplates.trap_on_expr_template, expr_code);
		this.replace_muta_code(mutation.get_location(), replace);
	}

	@Override
	protected void generate_weakness_code(AstMutation mutation) throws Exception {
		this.generate_stronger_code(mutation);
	}

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstBinaryExpression expression = (AstBinaryExpression) mutation.get_location();
		AstExpression loperand = CTypeAnalyzer.get_expression_of(expression.get_loperand());
		AstExpression roperand = CTypeAnalyzer.get_expression_of(expression.get_roperand());
		String loperand_code = "(" + loperand.get_code() + ")";
		String roperand_code = "(" + roperand.get_code() + ")";
		
		String mutation_operator = mutation.get_mutation_operator().toString();
		int index = mutation_operator.indexOf("_to_");
		String operator_name = mutation_operator.substring(index + 4).strip();
		String operator_code = this.get_operator_code(operator_name);
		
		String replace = "(" + loperand_code + " " + 
				operator_code + " " + roperand_code + ")";
		this.replace_muta_code(expression, replace);
	}

}
