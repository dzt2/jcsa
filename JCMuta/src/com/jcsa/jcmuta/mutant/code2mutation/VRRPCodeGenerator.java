package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.scope.CName;

public class VRRPCodeGenerator extends MutaCodeGenerator {
	
	private String get_replacement(CName cname) throws Exception {
		return cname.get_name();
	}
	
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
		AstExpression expression = (AstExpression) mutation.get_location();
		expression = CTypeAnalyzer.get_expression_of(expression);
		String expression_code = expression.get_code();
		String value = get_replacement((CName) mutation.get_parameter());
		
		String replace = String.format(MutaCodeTemplates.
				trap_if_different_template, expression_code, value);
		this.replace_muta_code(mutation.get_location(), replace);
	}

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		String value = get_replacement((CName) mutation.get_parameter());
		this.replace_muta_code(mutation.get_location(), value);
	}

}
