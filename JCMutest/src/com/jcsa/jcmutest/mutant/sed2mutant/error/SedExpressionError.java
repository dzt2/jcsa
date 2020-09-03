package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.util.SedParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	|--	SedExpressionError				{orig_expression: SetExpr}		<br>
 * 	|--	|--	SedAbstractExpressionError									<br>
 * 	|--	|--	|--	SedInsExpressionError	ins_expr(expr, oprt)			<br>
 * 	|--	|--	|--	SedAppExpressionError	app_expr(e, o, e)				<br>
 * 	|--	|--	|--	SedMutExpressionError	mut_expr(expr, expr)			<br>
 * 	|--	|--	SedConcreteExpressionError									<br>
 * 	|--	|--	|--	SedNegExpressionError	neg_{char|sign|usign|real}		<br>
 * 	|--	|--	|--	SedRsvExpressionError	rsv_{char|sign|usign}			<br>
 * 	|--	|--	|--	SedAddExpressionError	add_{char|sign|usign|real|addr}	<br>
 * 	|--	|--	|--	SedMulExpressionError 	mul_{char|sign|usign|real}		<br>
 * 	|--	|--	|--	SedAndExpressionError	and_{char|sign|usign}			<br>
 * 	|--	|--	|--	SedIorExpressionError	ior_{char|sign|usign}			<br>
 * 	|--	|--	|--	SedXorExpressionError	xor_{char|sign|usign}			<br>
 * 	|--	|--	|--	SedIncExpressionError	inc_{char|sign|usign|real|addr}	<br>
 * 	|--	|--	|--	SedDecExpressionError	dec_{char|sign|usign|real|addr}	<br>
 * 	|--	|--	|--	SedExtExpressionError	ext_{char|sign|usign|real}		<br>
 * 	|--	|--	|--	SedShkExpressionError	shk_{char|sign|usign|real}		<br>
 * 	|--	|--	|--	SedSetExpressionError	set_{bool|char|sign..|addr|list}<br>
 * 	|--	|--	|--	SedChgExpressionError	chg_{bool|char|sign..|addr|list}<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedExpressionError extends SedStateError {
	
	/* definition */
	private SedExpression orig_expression;
	private SedLocationType expression_type;
	protected SedExpressionError(CirStatement statement,
			CirExpression orig_expression) throws Exception {
		super(statement);
		this.orig_expression = (SedExpression) SedParser.parse(orig_expression);
		this.expression_type = SedStateError.location_type(this.orig_expression);
	}
	
	/* getters */
	/**
	 * @return the expression in which the error is seeded
	 */
	public SedExpression get_orig_expression() {
		return this.orig_expression;
	}
	/**
	 * @return the type of the orignal expression where the error is injected
	 */
	public SedLocationType get_orig_type() {
		return this.expression_type;
	}
	
}
