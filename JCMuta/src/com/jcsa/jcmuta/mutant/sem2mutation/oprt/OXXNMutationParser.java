package com.jcsa.jcmuta.mutant.sem2mutation.oprt;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationUtil;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class OXXNMutationParser extends SemanticMutationParser {

	/**
	 * get the location that the trapping really occurs.
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private AstExpression get_location(AstMutation ast_mutation) throws Exception {
		AstExpression expression = (AstExpression) ast_mutation.get_location();
		return CTypeAnalyzer.get_expression_of(expression);
	}
	
	/**
	 * get the expression representing the AST mutation
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirExpression get_expression(AstMutation ast_mutation) throws Exception {
		return this.get_result(this.get_location(ast_mutation));
	}
	
	/**
	 * get the expression representing the left-operand
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirExpression get_loperand(AstMutation ast_mutation) throws Exception {
		AstBinaryExpression expression = (AstBinaryExpression) this.get_location(ast_mutation);
		return this.get_result(CTypeAnalyzer.get_expression_of(expression.get_loperand()));
	}
	
	/**
	 * get the expression representing the right-operand
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirExpression get_roperand(AstMutation ast_mutation) throws Exception {
		AstBinaryExpression expression = (AstBinaryExpression) this.get_location(ast_mutation);
		return this.get_result(CTypeAnalyzer.get_expression_of(expression.get_roperand()));
	}
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		CirExpression expression = get_expression(ast_mutation);
		if(expression != null) return expression.statement_of();
		else return null;
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_expression(ast_mutation);
		CirExpression loperand = this.get_loperand(ast_mutation);
		CirExpression roperand = this.get_roperand(ast_mutation);
		Object lvalue = SemanticMutationUtil.get_constant(loperand);
		Object rvalue = SemanticMutationUtil.get_constant(roperand);
		this.generate_infections(ast_mutation, 
				expression, loperand, roperand, lvalue, rvalue);
	}
	
	/**
	 * construct the semantic mutation infection for analysis
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	protected abstract void generate_infections(AstMutation ast_mutation,
			CirExpression expression, CirExpression loperand, 
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception;
	
}
