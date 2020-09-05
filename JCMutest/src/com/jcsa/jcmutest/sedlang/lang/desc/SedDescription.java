package com.jcsa.jcmutest.sedlang.lang.desc;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcmutest.sedlang.lang.token.SedKeyword;
import com.jcsa.jcmutest.sedlang.lang.token.SedStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	SedDescription						{statement: SedStatement}			<br>
 * 	|--	SedConstraint														<br>
 * 	|--	|--	SedExecutionConstraint		exec(statement, integer)			<br>
 * 	|--	|--	SedConditionConstraint		assert(statement, expression)		<br>
 * 	|--	SedStatementError				{orig_statement: SedStatement}		<br>
 * 	|--	|--	SedAddStatementError		add_stmt(statement)					<br>
 * 	|--	|--	SedDelStatementError		del_stmt(statement)					<br>
 * 	|--	|--	SedSetStatementError		set_stmt(statement, statement)		<br>
 * 	|--	|--	SedMutStatementError		mut_stmt(statement, statement)		<br>
 * 	|--	SedAbstractValueError			{orig_expression: SedExpression}	<br>
 * 	|--	|--	SedAppExpressionError		app_expr(expr, oprt, expr)			<br>
 * 	|--	|--	SedInsExpressionError		ins_expr(expr, oprt, expr)			<br>
 * 	|--	|--	SedMutExpressionError		mut_expr(expr, expr)				<br>
 * 	|--	|--	SedNevExpressionError		nev_expr(expr, oprt)				<br>
 * 	|--	SedConcreteValueError			{orig_expression: SedExpression}	<br>
 * 	|--	|--	SedChgExpressionError		{bool|char|sign|usig|real|addr|list}<br>
 * 	|--	|--	SedSetExpressionError		{bool|char|sign|usig|real|addr|list}<br>
 * 	|--	|--	SedAddExpressionError		{char|sign|usig|real|addr}			<br>
 * 	|--	|--	SedIncExpressionError		{char|sign|usig|real|addr}			<br>
 * 	|--	|--	SedDecExpressionError		{char|sign|usig|real|addr}			<br>
 * 	|--	|--	SedMulExpressionError		{char|sign|usig|real}				<br>
 * 	|--	|--	SedExtExpressionError		{char|sign|usig|real}				<br>
 * 	|--	|--	SedShkExpressionError		{char|sign|usig|real}				<br>
 * 	|--	|--	SedAndExpressionError		{char|sign|usig}					<br>
 * 	|--	|--	SedIorExpressionError		{char|sign|usig}					<br>
 * 	|--	|--	SedXorExpressionError		{char|sign|usig}					<br>
 * 	|--	|--	SedNegExpressionError		{char|sign|usig|real}				<br>
 * 	|--	|--	SedRsvExpressionError		{char|sign|usig|real}				<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SedDescription extends SedNode {
	
	public SedDescription(CirStatement statement, SedKeywords keyword) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement");
		else if(keyword == null)
			throw new IllegalArgumentException("Invalid keyword");
		else {
			this.add_child(new SedStatement(statement));
			this.add_child(new SedKeyword(keyword));
		}
	}
	
	/**
	 * @return statement that is described
	 */
	public SedStatement get_statement() {
		return (SedStatement) this.get_child(0);
	}
	
	/**
	 * @return the keyword of the description
	 */
	public SedKeyword get_keyword() {
		return (SedKeyword) this.get_child(1);
	}

	@Override
	public String generate_code() throws Exception {
		return this.get_statement().generate_code() + "::" + 
				this.get_keyword().generate_code() + 
				this.generate_content() + ";";
	}
	
	/**
	 * @return param1, param2, ..., paramN
	 * @throws Exception
	 */
	protected abstract String generate_content() throws Exception;

}
