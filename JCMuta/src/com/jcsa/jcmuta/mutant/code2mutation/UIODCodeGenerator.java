package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class UIODCodeGenerator extends MutaCodeGenerator {

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
		AstNode location = mutation.get_location(), operand;
		if(location instanceof AstIncreUnaryExpression) {
			operand = ((AstIncreUnaryExpression) location).get_operand();
		}
		else {
			operand = ((AstIncrePostfixExpression) location).get_operand();
		}
		this.replace_muta_code(mutation.get_location(), "(" + operand.get_code() + ")");
	}

}
