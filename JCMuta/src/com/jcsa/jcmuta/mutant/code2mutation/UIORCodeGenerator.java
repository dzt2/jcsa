package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.MutaOperator;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class UIORCodeGenerator extends MutaCodeGenerator {

	@Override
	protected void generate_coverage_code(AstMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		expression = CTypeAnalyzer.get_expression_of(expression);
		String expr_code = "(" + expression.get_location().read() + ")";
		
		String replace = String.format(
				MutaCodeTemplates.trap_on_expr_template, expr_code);
		this.replace_muta_code(mutation.get_location(), replace);
	}
	
	private boolean is_unchanged_lvalue(MutaOperator operator) throws Exception {
		switch(operator) {
		case prev_inc_to_post_inc:
		case prev_dec_to_post_dec:
		case post_inc_to_prev_inc:
		case post_dec_to_prev_dec:	return true;
		default: return false;
		}
	}
	
	private boolean is_used_in_context(AstNode location) throws Exception {
		location = location.get_parent();
		while(location != null) {
			
			if(location instanceof AstParanthExpression
				|| location instanceof AstConstExpression) {
				/* unable to determine whether to be used */
			}
			else if(location instanceof AstExpression) {
				return true;
			}
			else if(location instanceof AstStatement) {
				return false;
			}
			
			location = location.get_parent();
		}
		return false;
	}
	
	@Override
	protected void generate_weakness_code(AstMutation mutation) throws Exception {
		if(this.is_unchanged_lvalue(mutation.get_mutation_operator())) {
			if(this.is_used_in_context(mutation.get_location())) {
				this.generate_coverage_code(mutation);
			}
			else {
				/* equivalent mutants */
				this.buffer.append(mutation.get_location().
						get_tree().get_ast_root().get_location().read());
			}
		}
		else {
			this.generate_coverage_code(mutation);
		}
	}

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location(), operand;
		if(location instanceof AstIncreUnaryExpression) 
			operand = ((AstIncreUnaryExpression) location).get_operand();
		else 
			operand = ((AstIncrePostfixExpression) location).get_operand();
		
		String operator = mutation.get_mutation_operator().toString();
		String replace;
		if(operator.endsWith("prev_inc")) {
			replace = "++" + operand.get_code();
		}
		else if(operator.endsWith("post_inc")) {
			replace = operand.get_code() + "++";
		}
		else if(operator.endsWith("prev_dec")) {
			replace = "--" + operand.get_code();
		}
		else if(operator.endsWith("post_dec")) {
			replace = operand.get_code() + "--";
		}
		else throw new IllegalArgumentException("Invalid: " + operator);
		
		this.replace_muta_code(location, replace);
	}

}
