package com.jcsa.jcmuta.mutant.code2mutation;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.text.CText;

public class OPDLCodeGenerator extends MutaCodeGenerator {
	
	private void coverage_test_in_binary_expression(AstBinaryExpression expression, int index) throws Exception {
		AstExpression operand;
		if(index == 0)
			operand = expression.get_loperand();
		else operand = expression.get_roperand();
		String code = "(" + CTypeAnalyzer.get_expression_of(operand).get_code() + ")";
		String replace = String.format(MutaCodeTemplates.trap_on_expr_template, code);
		this.replace_muta_code(operand, replace);
	}
	
	private void coverage_test_in_comma_expression(AstCommaExpression expression, int index) throws Exception {
		AstExpression operand = expression.get_expression(index);
		String code = "(" + CTypeAnalyzer.get_expression_of(operand).get_code() + ")";
		String replace = String.format(MutaCodeTemplates.trap_on_expr_template, code);
		this.replace_muta_code(operand, replace);
	}
	
	@Override
	protected void generate_coverage_code(AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		int index = (int) mutation.get_parameter();
		if(location instanceof AstBinaryExpression) 
			this.coverage_test_in_binary_expression((AstBinaryExpression) location, index);
		else 
			this.coverage_test_in_comma_expression((AstCommaExpression) location, index);
	}
	
	private void weak_mutation_in_binary_expression(AstBinaryExpression expression, int index) throws Exception {
		COperator operator = expression.get_operator().get_operator();
		AstExpression loperand = expression.get_loperand();
		AstExpression roperand = expression.get_roperand();
		loperand = CTypeAnalyzer.get_expression_of(loperand);
		roperand = CTypeAnalyzer.get_expression_of(roperand);
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_value_type());
		CType data_type1 = CTypeAnalyzer.get_value_type(loperand.get_value_type());
		CType data_type2 = CTypeAnalyzer.get_value_type(roperand.get_value_type());
		
		String data_type_name = this.get_data_type_code(data_type);
		String operator_type = this.get_operator_type(operator);
		char data_type_tag = this.get_data_type_tag(data_type1, data_type2);
		String operator_name = this.get_operator_name(operator);
		String loperand_code = "(" + loperand.get_code() + ")";
		String roperand_code = "(" + roperand.get_code() + ")";
		String left_right;
		if(index == 0) 
			left_right = "true";
		else
			left_right = "false";
		
		String replace = String.format(MutaCodeTemplates.delete_operand_template, data_type_name,
				operator_type, data_type_tag, operator_name, loperand_code, roperand_code, left_right);
		this.replace_muta_code(expression, replace);
	}
	
	private void weak_mutation_in_comma_expression(AstCommaExpression expression, int index) throws Exception {
		this.strong_mutation_in_comma_expression(expression, index);
	}
	
	@Override
	protected void generate_weakness_code(AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		int index = (int) mutation.get_parameter();
		if(location instanceof AstBinaryExpression) 
			this.weak_mutation_in_binary_expression((AstBinaryExpression) location, index);
		else 
			this.weak_mutation_in_comma_expression((AstCommaExpression) location, index);
	}
	
	private void strong_mutation_in_binary_expression(AstBinaryExpression expression, int index) throws Exception {
		AstExpression operand;
		if(index == 0)
			operand = expression.get_roperand();
		else
			operand = expression.get_loperand();
		operand = CTypeAnalyzer.get_expression_of(operand);
		
		String replace = "(" + operand.get_code() + ")";
		this.replace_muta_code(expression, replace);
	}
	
	private void strong_mutation_in_comma_expression(AstCommaExpression expression, int index) throws Exception {
		/** declarations **/
		int beg = expression.get_location().get_bias();
		int end = beg + expression.get_location().get_length();
		CText text = expression.get_tree().get_source_code();
		
		/** previous part **/
		for(int k = 0; k < beg; k++) buffer.append(text.get_char(k));
		
		this.buffer.append("(");
		
		List<String> arguments = new ArrayList<String>();
		for(int k = 0; k < expression.number_of_arguments(); k++) {
			AstExpression element = expression.get_expression(k);
			if(k != index) {
				element = CTypeAnalyzer.get_expression_of(element);
				arguments.add("(" + element.get_code() + ")");
			}
		}
		
		for(int k = 0; k < arguments.size(); k++) {
			this.buffer.append(arguments.get(k));
			if(k != arguments.size() - 1) {
				this.buffer.append(", ");
			}
		}
		
		this.buffer.append(")");
		
		/** following part **/
		for(int k = end; k < text.length(); k++) buffer.append(text.get_char(k));
	}
	
	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		
		int index = (int) mutation.get_parameter();
		if(location instanceof AstBinaryExpression) 
			this.strong_mutation_in_binary_expression((AstBinaryExpression) location, index);
		else 
			this.strong_mutation_in_comma_expression((AstCommaExpression) location, index);
	}

}
