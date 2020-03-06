package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VINCCodeGenerator extends MutaCodeGenerator {
	
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
		AstNode location = mutation.get_location();
		
		String replace;
		switch(mutation.get_mutation_operator()) {
		case inc_value:
		{
			int value = (int) mutation.get_parameter();
			if(value > 0) 
				replace = "((" + location.get_code() + ") + " + value + ")";
			else 
				replace = "((" + location.get_code() + ") - " + (-value) + ")";
		}
		break;
		case mul_value:
		{
			double value = (double) mutation.get_parameter();
			replace = "((" + location.get_code() + ") * " + value + ")";
		}
		break;
		default: throw new IllegalArgumentException("Invalid operator");
		}
		
		this.replace_muta_code(location, replace);
	}
	
}
