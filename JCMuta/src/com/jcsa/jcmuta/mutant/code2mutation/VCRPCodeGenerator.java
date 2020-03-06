package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class VCRPCodeGenerator extends MutaCodeGenerator {
	
	private String get_replacement(CConstant parameter) throws Exception {
		Object value;
		switch(parameter.get_type().get_tag()) {
		case c_bool:
			value = parameter.get_bool(); break;
		case c_char: case c_uchar:
			value = (int) parameter.get_char(); break;
		case c_short: case c_ushort:
		case c_int: case c_uint:
			value = parameter.get_integer(); break;
		case c_long: case c_ulong:
		case c_llong: case c_ullong:
			value = parameter.get_long(); break;
		case c_float:
			value = parameter.get_float(); break;
		case c_double: case c_ldouble:
			value = parameter.get_double(); break;
		default: throw new IllegalArgumentException("Invalid type");
		}
		return value.toString();
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
		String value = get_replacement((CConstant) mutation.get_parameter());
		
		String replace = String.format(MutaCodeTemplates.
				trap_if_different_template, expression_code, value);
		this.replace_muta_code(mutation.get_location(), replace);
	}
	
	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		String value = get_replacement((CConstant) mutation.get_parameter());
		this.replace_muta_code(mutation.get_location(), value);
	}
	
}
