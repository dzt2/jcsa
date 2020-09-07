package com.jcsa.jcmutest.mutant.sel2mutant.lang.desc;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelNode;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelKeyword;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelConstraint															<br>
 * 	|--	SelExecutionConstraint		execute(stmt, int)						<br>
 * 	|--	SelConditionConstraint		asserts(stmt, expr)						<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelStatementError				{orig_statement: SelStatement}			<br>
 * 	|--	SelAddStatementError		add_stmt(stmt)							<br>
 * 	|--	SelDelStatementError		del_stmt(stmt)							<br>
 * 	|--	SelSetStatementError		set_stmt(stmt, stmt)					<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelExpressionError				{orig_expression: SelExpression}		<br>
 * 	|--	SelNebExpressionError		nev_expr(expr, oprt)					<br>
 * 	|--	SelSetExpressionError		set_expr(expr, expr)					<br>
 * 	|--	SelAddExpressionError		add_expr(expr, oprt, expr)				<br>
 * 	|--	SelInsExpressionError		ins_expr(expr, oprt, expr)				<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelTypedValueError				{orig_expression; type: SelDataType}	<br>
 * 	|--	SelUnaryValueError													<br>
 * 	|--	|--	SelChgValueError		chg_val[bool|char|sign|usign|...|body)	<br>
 * 	|--	|--	SelNegValueError		neg_val[char|sign|usign|real]			<br>
 * 	|--	|--	SelRsvValueError		rsv_val[char|sign|usign]				<br>
 * 	|--	|--	SelIncValueError		inc_val[char|sign|usign|real|addr]		<br>
 * 	|--	|--	SelDecValueError		dec_val[char|sign|usign|real|addr]		<br>
 * 	|--	|--	SelExtValueError		ext_val[char|sign|usign|real]			<br>
 * 	|--	|--	SelShkValueError		shk_val[char|sign|usign|real]			<br>
 * 	|--	SelBinaryValueError			{muta_expression: SelExpression}		<br>
 * 	|--	|--	SelSetValueError		set_val[bool|char|sign|usign|...|body]	<br>
 * 	|--	|--	SelAddValueError		add_val[char|sign|usign|real|addr]		<br>
 * 	|--	|--	SelMulValueError		mul_val[char|sign|usign|real]			<br>
 * 	|--	|--	SelModValueError		mod_val[char|sign|usign]				<br>
 * 	|--	|--	SelAndValueError		and_val[char|sign|usign]				<br>
 * 	|--	|--	SelIorValueError		ior_val[char|sign|usign]				<br>
 * 	|--	|--	SelXorValueError		xor_val[char|sign|usign]				<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelDescriptions					{descriptions: List[SelDescription]}	<br>
 * 	|--	SelConjunctDescriptions												<br>
 * 	|--	SelDisjunctDescriptions												<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SelDescription extends SelNode {
	
	public SelDescription(CirStatement statement, SelKeywords keyword) throws Exception {
		this.add_child(new SelStatement(statement));
		this.add_child(new SelKeyword(keyword));
	}
	
	/**
	 * @return the statement in which the description is defined on
	 */
	public SelStatement get_location() { return (SelStatement) this.get_child(0); }
	
	/**
	 * @return the keyword that defines the type of the description
	 */
	public SelKeyword get_keyword() { return (SelKeyword) this.get_child(1); }

	@Override
	public String generate_code() throws Exception {
		return this.get_location().generate_code() + ":" + this.
				get_keyword().generate_code() + generate_content();
	}
	
	/**
	 * @return location:keyword[content]
	 * @throws Exception
	 */
	protected abstract String generate_content() throws Exception;

}
