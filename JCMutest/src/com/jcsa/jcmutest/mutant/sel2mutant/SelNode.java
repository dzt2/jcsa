package com.jcsa.jcmutest.mutant.sel2mutant;

import java.util.LinkedList;
import java.util.List;

/**
 * The symbolic error language defines the syntax to describe the constraints as
 * well as the state errors required for killing a mutation.<br>
 * <br>
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelNode																	<br>
 * 	|--	SelToken															<br>
 * 	|--	SelDescription				{statement; keyword}					<br>
 * 	|--	|--	SelConstraint													<br>
 * 	|--	|--	SelStatementError		{orig_statement}						<br>
 * 	|--	|--	SelExpressionError		{orig_expression}						<br>
 * 	|--	|--	SelTypedValueError		{orig_expression}						<br>
 * 	|-- |--	SelDescriptions			{descriptions: List}					<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelToken																<br>
 * 	|--	SelDataType					{data_type, value_type}					<br>
 * 	|--	SelKeyword					{keyword: SelKeywords}					<br>
 * 	|--	SelOperator					{operator: COperator}					<br>
 * 	|--	SelExpression				{expression: SymExpression}				<br>
 * 	|--	SelStatement				{statement; execution}					<br>
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
 * <br>
 * 
 * @author yukimula
 *
 */
public abstract class SelNode {
	
	/* definitions */
	/** the children under this node **/
	private List<SelNode> children;
	/**
	 * create an isolated node without children
	 */
	protected SelNode() {
		this.children = new LinkedList<SelNode>();
	}
	
	/* getters */
	/**
	 * @return the children under this node
	 */
	public Iterable<SelNode> get_children() { return this.children; }
	/**
	 * @return the number of children under this node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child under this node
	 * @throws IndexOutOfBoundsException
	 */
	public SelNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	/**
	 * @return code to describe the content of the SelNode
	 * @throws Exception
	 */
	public abstract String generate_code() throws Exception;
	@Override
	public String toString() {
		while(true) {
			try {
				return this.generate_code();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof SelNode) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
}
