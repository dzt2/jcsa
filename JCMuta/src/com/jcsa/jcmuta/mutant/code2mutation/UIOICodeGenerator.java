package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class UIOICodeGenerator extends MutaCodeGenerator {

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
		this.generate_coverage_code(mutation);
	}

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		String code = mutation.get_location().get_code(), replace;
		switch(mutation.get_mutation_operator()) {
		case insert_prev_inc:	replace = "++" + code;	break;
		case insert_prev_dec:	replace = "--" + code;	break;
		case insert_post_inc:	replace = code + "++";	break;
		case insert_post_dec:	replace = code + "--";	break;
		default: throw new IllegalArgumentException(
				"Invalid: " + mutation.get_mutation_operator());
		}
		this.replace_muta_code(mutation.get_location(), "(" + replace + ")");
	}

}
