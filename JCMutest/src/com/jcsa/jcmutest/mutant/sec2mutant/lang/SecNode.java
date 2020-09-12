package com.jcsa.jcmutest.mutant.sec2mutant.lang;

import java.util.LinkedList;
import java.util.List;

/**
 * The symbolic error and constraint language (SEC) defines the following syntax to
 * describe the constraint as well as the state error that are expected to occur in
 * testing so that the mutation under analysis could be detected.<br>
 * <br>
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecNode																	<br>
 * 	|--	SecToken															<br>
 * 	|--	SecDescription					{statement: SecStatement}			<br>
 * 	|--	|--	SecConstraint				asserton(stmt, expr) === evaluator	<br>
 * 	|--	|--	SecStateError													<br>
 * 	|--	|--	SecDescriptions													<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecToken																<br>
 * 	|--	SecKeyword						{keyword: SecKeywords}				<br>
 * 	|--	SecType							{vtype: SecValueTypes}				<br>
 * 	|--	SecOperator						{operator: COperator}				<br>
 * 	|--	SecExpression					{expression: SymExpression}			<br>
 * 	|--	SecStatement					{statement: CirStatement}			<br>
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
 * 	+----------------------------------------------------------------------+<br>
 * 	SecDescriptions						{descriptions: SecDescription+}		<br>
 * 	|--	SecConjunctDescriptions												<br>
 * 	|--	SecDisjunctDescriptions												<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * <br>
 * @author yukimula
 *
 */
public abstract class SecNode {
	
	/* definition */
	/** the children under this node **/
	private List<SecNode> children;
	public SecNode() {
		this.children = new LinkedList<SecNode>();
	}
	
	/* getters */
	/**
	 * @return the number of children under this node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @return the children under this node
	 */
	public Iterable<SecNode> get_children() { return this.children; }
	/**
	 * @param k
	 * @return the kth child under this node
	 * @throws IndexOutOfBoundsException
	 */
	public SecNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	/**
	 * add a child in the tail of its children list
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void add_child(SecNode child) throws IllegalArgumentException {
		if(child == null)
			throw new IllegalArgumentException("Invalid child: null");
		else
			this.children.add(child);
	}
	/**
	 * @return generate the code that describes the SecNode
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
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof SecNode) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
}
