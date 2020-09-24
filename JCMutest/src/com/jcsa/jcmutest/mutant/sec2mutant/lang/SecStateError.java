package com.jcsa.jcmutest.mutant.sec2mutant.lang;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.state.CStateContexts;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecStateError															<br>
 * 	|--	SecStatementError				{orig_stmt: SecStatement}			<br>
 * 	|--	|--	SecAddStatementError		add_stmt(orig_stmt)					<br>
 * 	|--	|--	SecDelStatementError		del_stmt(orig_stmt)					<br>
 * 	|--	|--	SecSetStatementError		set_stmt(orig_stmt, muta_stmt)		<br>
 * 	|--	SecExpressionError				{orig_expr: SecExpression}			<br>
 * 	|--	|--	SecSetExpressionError		set_expr(orig_expr, muta_expr)		<br>
 * 	|--	|--	SecAddExpressionError		add_expr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecInsExpressionError		ins_expr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecUnyExpressionError		uny_expr(orig_expr, oprt)			<br>
 * 	|--	SecReferenceError				{orig_expr: SecExpression}			<br>
 * 	|--	|--	SecSetReferenceError		set_refr(orig_expr, muta_expr)		<br>
 * 	|--	|--	SecAddReferenceError		add_refr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecInsReferenceError		ins_refr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecUnyReferenceError		uny_expr(orig_expr, oprt)			<br>
 * 	|--	SecUniqueError														<br>
 * 	|--	|--	SecTrapError				trap()								<br>
 * 	|--	|--	SecNoneError				none()								<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SecStateError extends SecDescription {

	public SecStateError(CirStatement statement, SecKeywords keyword) throws Exception {
		super(statement, keyword);
	}
	
	/**
	 * @param contexts
	 * @return the set of state errors extended from the source, which are semantically
	 * 		   equivalent with the source state error.
	 * @throws Exception
	 */
	public Iterable<SecStateError> extend(CStateContexts contexts) throws Exception {
		return SecOptimizer.extend(this, contexts);
	}
	
	public SecStateError optimize(CStateContexts contexts) throws Exception {
		return SecOptimizer.optimize(this, contexts);
	}
	
}
