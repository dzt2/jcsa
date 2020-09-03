package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
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
public abstract class SedConExpressionError extends SedExpressionError {

	protected SedConExpressionError(CirStatement statement, 
			CirExpression orig_expression) throws Exception {
		super(statement, orig_expression);
		if(!this.verify_orig_type())
			throw new IllegalArgumentException("Invalid data type");
	}
	
	/**
	 * @return whether the type of the original expression is valid
	 * @throws Exception
	 */
	protected abstract boolean verify_orig_type() throws Exception;

}
