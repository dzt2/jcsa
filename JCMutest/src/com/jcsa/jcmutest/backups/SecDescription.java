package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecDescription						{statement: SecStatement}			<br>
 * 	|--	SecConstraint					{sym_condition: SymExpression}		<br>
 * 	|--	SecStateError					{cir_location: CirNode}				<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecConstraint															<br>
 * 	|--	SecConditionConstraint			assert(statement, expression)		<br>
 * 	|--	SecExecutionConstraint			execute(statement, expression)		<br>
 * 	|--	SecConjunctConstraints			conjunct{constraint+}				<br>
 * 	|--	SecDisjunctConstraints			disjunct{constraint+}				<br>
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
public abstract class SecDescription extends SecNode {
	
	/**
	 * @param statement the statement where the description is evaluated
	 * @param keyword
	 * @throws Exception
	 */
	public SecDescription(CirStatement statement, SecKeywords keyword) throws Exception {
		this.add_child(new SecStatement(statement));
		this.add_child(new SecKeyword(keyword));
	}
	
	/**
	 * @return the statement where the description is evaluated
	 */
	public SecStatement get_statement() { return (SecStatement) this.get_child(0); }
	
	/**
	 * @return keyword that defines the semantic of the description
	 */
	public SecKeyword get_keyword() { return (SecKeyword) this.get_child(1); }
	
	/**
	 * @return For SecConstraint, it is the statement where the constraint is evaluated;
	 * 		   while for SecStateError, it is the location where the error occurs.
	 */
	public abstract CirNode get_cir_location();
	
	@Override
	public String generate_code() throws Exception {
		return this.get_statement().generate_code() + ":" + this.
				get_keyword().generate_code() + this.generate_content();
	}
	
	/**
	 * @return the content following description head as "statement:keyword{content}"
	 * @throws Exception
	 */
	protected abstract String generate_content() throws Exception;
	
}
