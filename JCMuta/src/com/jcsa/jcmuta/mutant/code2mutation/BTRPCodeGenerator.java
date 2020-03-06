package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;


public class BTRPCodeGenerator extends MutaCodeGenerator {

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
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_value_type());
		
		char data_type_tag = this.get_data_type_tag(data_type);
		String expr_code = "(" + expression.get_location().read() + ")";
		String value_code;
		switch(mutation.get_mutation_operator()) {
		case trap_on_true:	value_code = "true";	break;
		case trap_on_false:	value_code = "false";	break;
		default: throw new IllegalArgumentException(
				"Invalid operator: " + mutation.get_mutation_operator());
		}
		
		String replace = String.format(MutaCodeTemplates.
				trap_on_bool_template, data_type_tag, expr_code, value_code);
		this.replace_muta_code(mutation.get_location(), replace);
	}

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		this.generate_weakness_code(mutation);
	}

}
