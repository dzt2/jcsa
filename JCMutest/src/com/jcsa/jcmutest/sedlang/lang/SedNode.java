package com.jcsa.jcmutest.sedlang.lang;

import java.util.LinkedList;
import java.util.List;

/**
 * The symbolic execution description (SED) language defines the following syntax:
 * <br>
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SedNode																	<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SedToken																<br>
 * 	|--	SedField						{name: String}						<br>
 * 	|--	SedOperator						{operator: COperator}				<br>
 * 	|--	SedKeyword						{keyword: SedKeywords}				<br>
 * 	|--	SedStatement					{cir_statement: CirStatement}		<br>
 * 	|--	SedArgumentList														<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SedExpression						{cir_expression: CirExpression}		<br>
 * 	|--	SedBasicExpression													<br>
 * 	|--	|--	SedIdExpression				{name: String}						<br>
 * 	|--	|--	SedConstant					{bool|char|int|long|float|double}	<br>
 * 	|--	|--	SedLiteral					{literal: String}					<br>
 * 	|--	|--	SedDefaultValue													<br>
 * 	|--	SedUnaryExpression				{-, ~, !, &, *, cast}				<br>
 * 	|--	SedBinaryExpression				[+, -, *, /, ..., ==, !=]			<br>
 * 	|--	SedFieldExpression													<br>
 * 	|--	SedInitializerList													<br>
 * 	|--	SedCallExpression													<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SedDescription						{statement: SedStatement}			<br>
 * 	|--	SedConstraint														<br>
 * 	|--	|--	SedExecutionConstraint		exec(statement, integer)			<br>
 * 	|--	|--	SedConditionConstraint		assert(statement, expression)		<br>
 * 	|--	SedStatementError				{orig_statement: SedStatement}		<br>
 * 	|--	|--	SedAddStatementError		add_stmt(statement)					<br>
 * 	|--	|--	SedDelStatementError		del_stmt(statement)					<br>
 * 	|--	|--	SedSetStatementError		set_stmt(statement, statement)		<br>
 * 	|--	|--	SedMutStatementError		mut_stmt(statement, statement)		<br>
 * 	|--	SedAbstractValueError				{orig_expression: SedExpression}	<br>
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
 * 	+----------------------------------------------------------------------+<br>
 * 	SedDescriptions					{descriptions: List[SedDescription]}	<br>
 * 	|--	SedConjunctDescriptions												<br>
 * 	|--	SedDisjunctDescriptions												<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SedNode {
	
	/* definitions */
	/** the parent of the node or null if it's root **/
	private SedNode parent;
	/** the index of the node as child of its parent or -1 if it's root **/
	private int index;
	/** the list of children added in this node or empty if it's leaf **/
	private List<SedNode> children;
	/**
	 * create an isolated node to describe symbolic execution state
	 */
	public SedNode() {
		this.parent = null;
		this.index = -1;
		this.children = new LinkedList<SedNode>();
	}
	
	/* getters */
	/**
	 * @return whether the node is root {parent == null}
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return whether the node is leaf {children is empty}
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/**
	 * @return the parent of the node or null if it's root
	 */
	public SedNode get_parent() { return this.parent; }
	/**
	 * @return the index of the node as child of its parent or -1 if it's root
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the list of children added in this node or empty if it's leaf
	 */
	public Iterable<SedNode> get_children() { return this.children; }
	/**
	 * @return the number of children under this node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child under this node
	 * @throws IndexOutOfBoundsException
	 */
	public SedNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	/**
	 * add the child to the tail of this node
	 * @param child
	 * @throws IllegalArgumentException
	 */
	public void add_child(SedNode child) throws IllegalArgumentException {
		if(child == null)
			throw new IllegalArgumentException("Invalid child: null");
		else {
			if(child.parent != null)
				child = child.clone();
			child.parent = this;
			child.index = this.children.size();
			this.children.add(child);
		}
	}
	@Override
	public SedNode clone() {
		SedNode parent = null;
		while(parent == null) {
			try {
				parent = this.construct();
			}
			catch(Exception ex) {
				ex.printStackTrace();
				parent = null;
			}
		}
		for(SedNode child : this.children) {
			parent.add_child(child.clone());
		}
		return parent;
	}
	/**
	 * @return the code generated from the SedNode
	 * @throws Exception
	 */
	public abstract String generate_code() throws Exception;
	/**
	 * @return create the copy of this node without parent and children
	 */
	protected abstract SedNode construct() throws Exception;
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
	
}
