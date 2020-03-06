package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class UNOICodeGenerator extends MutaCodeGenerator {

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
		
		String data_type_code = this.get_data_type_code(data_type);
		char data_type_tag = this.get_data_type_tag(data_type);
		String operator_name;
		switch(mutation.get_mutation_operator()) {
		case insert_arith_neg:	operator_name = "arith_neg";	break;
		case insert_bitws_rsv:	operator_name = "bitws_rsv";	break;
		case insert_logic_not:	operator_name = "logic_not";	break;
		case insert_abs:		operator_name = "abs_invoc";	break;
		case insert_neg_abs:	operator_name = "nabs_invoc";	break;
		default: throw new IllegalArgumentException("Invalid: " + mutation.get_mutation_operator());
		}
		String expr_code = "(" + CTypeAnalyzer.get_expression_of(expression).get_code() + ")";
		
		String replace = String.format(MutaCodeTemplates.insert_unary_operand_template, 
				data_type_code, data_type_tag, operator_name, expr_code);
		this.replace_muta_code(expression, replace);
	}

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		expression = CTypeAnalyzer.get_expression_of(expression); String replace;
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_value_type());
		String expr_code = "(" + CTypeAnalyzer.get_expression_of(expression).get_code() + ")";
		char data_type_tag = this.get_data_type_tag(data_type);
		String data_type_code = this.get_data_type_code(data_type);
		
		switch(mutation.get_mutation_operator()) {
		case insert_arith_neg:
		{
			replace = "(-" + expr_code + ")";
		}
		break;
		case insert_bitws_rsv:
		{
			replace = "(~" + expr_code + ")";
		}
		break;
		case insert_logic_not:
		{
			replace = "(!" + expr_code + ")";
		}
		break;
		case insert_abs:
		{
			replace = String.format(MutaCodeTemplates.abs_invocation_template, 
					data_type_code, data_type_tag, expr_code);
		}
		break;
		case insert_neg_abs:
		{
			replace = String.format(MutaCodeTemplates.nabs_invocation_template, 
					data_type_code, data_type_tag, expr_code);
		}
		break;
		default: throw new IllegalArgumentException("Invalid " + mutation.get_mutation_operator());
		}
		
		this.replace_muta_code(expression, replace);
	}

}
