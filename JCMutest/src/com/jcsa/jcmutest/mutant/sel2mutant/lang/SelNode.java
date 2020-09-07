package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import java.util.LinkedList;
import java.util.List;

/**
 * Symbolic error language (SEL) defines the syntax to describe the constraints as
 * well as the state errors that are required for killing a mutation in testing.<br>
 * <br>
 * <code>	
 * 	+----------------------------------------------------------------------+<br>
 * 	SelNode																	<br>
 * 	|--	SelToken															<br>
 * 	|--	SelDescription					{statement; keyword}				<br>
 * 	|--	|--	SelConstraint													<br>
 * 	|--	|--	SelStatementError												<br>
 * 	|--	|--	SelExpressionError												<br>
 * 	|--	|--	SelTypedValueError												<br>
 * 	|--	|--	SelDescriptions													<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelToken																<br>
 * 	|--	SelKeyword						{keyword: SelKeywords}				<br>
 * 	|--	SelDataType						{ctype: CType; vtype: SelDataType}	<br>
 * 	|--	SelOperator						{operator: COperator}				<br>
 * 	|--	SelExpression					{expression: SymExpression}			<br>
 * 	|--	SelStatement					{execution; statement: CirStatement}<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelConstraint															<br>
 * 	|--	SelExecutionConstraint			execute(stmt, int)					<br>
 * 	|--	SelConditionConstraint			asserts(stmt, expr)					<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelStatementError					{orig_stmt: SelStatement}			<br>
 * 	|--	SelAddStatementError			add_stmt(stmt)						<br>
 * 	|--	SelDelStatementError			del_stmt(stmt)						<br>
 * 	|--	SelSetStatementError			set_stmt(stmt, stmt)				<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelExpressionError					{orig_expr: SelExpression}			<br>
 * 	|--	SelSetExpressionError			set_expr(expr, expr)				<br>
 * 	|--	SelInsExpressionError			ins_expr(expr, oprt)				<br>
 * 	|--	SelAddExpressionError			add_expr(expr, oprt, expr)			<br>
 * 	|--	SelPutExpressionError			put_expr(expr, oprt, expr)			<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelTypedValueError					{orig_expr; data_type: SelDataType}	<br>
 * 	|--	SelUnaryValueError													<br>
 * 	|--	|--	SelChgValueError			chg_value[type](expr)				<br>
 * 	|--	|--	SelNegValueError			neg_value[type](expr)				<br>
 * 	|--	|--	SelRsvValueError			rsv_value[type](expr)				<br>
 * 	|--	|--	SelIncValueError			inc_value[type](expr)				<br>
 * 	|--	|--	SelDecValueError			dec_value[type](expr)				<br>
 * 	|--	|--	SelExtValueError			ext_value[type](expr)				<br>
 * 	|--	|--	SelShkValueError			shk_value[type](expr)				<br>
 * 	|--	SelBinaryValueError				{muta_expr: SelExpression}			<br>
 * 	|--	|--	SelAddValueError			add_value[type](expr, expr)			<br>
 * 	|--	|--	SelMulValueError			mul_value[type](expr, expr)			<br>
 * 	|--	|--	SelModValueError			mod_value[type](expr, expr)			<br>
 * 	|--	|--	SelAndValueError			and_value[type](expr, expr)			<br>
 * 	|--	|--	SelIorValueError			ior_value[type](expr, expr)			<br>
 * 	|--	|--	SelXorValueError			xor_value[type](expr, expr)			<br>
 * 	|--	|--	SelSetValueError			set_value[type](expr, expr)			<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SelDescriptions						{descriptions: List[SelDescription]}<br>
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
	
	/* definition */
	/** the set of children under this node **/
	private List<SelNode> children;
	/**
	 * create an isolated node in Symbolic Error Language.
	 */
	protected SelNode() {
		this.children = new LinkedList<SelNode>();
	}
	
	/* getters */
	/**
	 * @return the set of children under this node
	 */
	public Iterable<SelNode> get_children() { return this.children; }
	/**
	 * @return the number of children under this node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child under the node
	 * @throws IndexOutOfBoundsException
	 */
	public SelNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	/**
	 * add a new child in the tail of the node
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void add_child(SelNode child) throws IllegalArgumentException {
		if(child == null)
			throw new IllegalArgumentException("Invalid child: null");
		else
			this.children.add(child);
			
	}
	/**
	 * @return code generated to describe this node
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
