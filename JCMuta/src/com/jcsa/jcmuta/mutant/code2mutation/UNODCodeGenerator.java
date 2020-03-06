package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class UNODCodeGenerator extends MutaCodeGenerator {

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
		AstNode location = mutation.get_location();
		CType data_type; String operator_name; 
		AstExpression operand;
		
		if(location instanceof AstArithUnaryExpression) {
			operand = ((AstArithUnaryExpression) location).get_operand();
			data_type = ((AstArithUnaryExpression) location).get_value_type();
			operator_name = "arith_neg";
		}
		else if(location instanceof AstBitwiseUnaryExpression) {
			operand = ((AstBitwiseUnaryExpression) location).get_operand();
			data_type = ((AstBitwiseUnaryExpression) location).get_value_type();
			operator_name = "bitws_rsv";
		}
		else if(location instanceof AstLogicUnaryExpression) {
			operand = ((AstLogicUnaryExpression) location).get_operand();
			data_type = ((AstLogicUnaryExpression) location).get_value_type();
			operator_name = "logic_not";
		}
		else throw new IllegalArgumentException("Invalid location");
		
		operand = CTypeAnalyzer.get_expression_of(operand);
		data_type = CTypeAnalyzer.get_value_type(data_type);
		
		String data_type_code = this.get_data_type_code(data_type);
		char data_type_tag = this.get_data_type_tag(data_type);
		String expr_code = "(" + operand.get_code() + ")";
		
		String replace = String.format(
				MutaCodeTemplates.delete_unary_operand_template, 
				data_type_code, data_type_tag, operator_name, expr_code);
		this.replace_muta_code(location, replace);
	}
	
	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		AstExpression operand;
		if(location instanceof AstArithUnaryExpression) {
			operand = ((AstArithUnaryExpression) location).get_operand();
		}
		else if(location instanceof AstBitwiseUnaryExpression) {
			operand = ((AstBitwiseUnaryExpression) location).get_operand();
		}
		else if(location instanceof AstLogicUnaryExpression) {
			operand = ((AstLogicUnaryExpression) location).get_operand();
		}
		else throw new IllegalArgumentException("Invalid location");
		String replace = "(" + operand.get_code() + ")";
		this.replace_muta_code(location, replace);
	}

}
