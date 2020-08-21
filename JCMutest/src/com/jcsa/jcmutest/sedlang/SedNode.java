package com.jcsa.jcmutest.sedlang;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * Symbolic state description language describes the program state during program
 * execution using the symbolic form, of which definition is shown as following:<br>
 * <code>
 * 	<i>SedNode</i>										{source: CirNode}		<br>
 * 	+--------------------------------------------------------------------------+<br>
 * 	|-- <i>SedExpression</i>							{data_type: CType}		<br>
 * 	|--	|--	<i>SedBasicExpression</i>											<br>
 * 	|--	|--	|-- SedIdentifier							{name: String}			<br>
 * 	|--	|--	|--	SedConstant								{constant: CConstant}	<br>
 * 	|--	|--	|--	SedLiteral								{literal: String}		<br>
 * 	|--	|--	|--	SedDefaultValue													<br>
 * 	|--	|--	SedUnaryExpression				{operator: +, -, ~, !, *, &, cast}	<br>
 * 	|--	|--	SedBinaryExpression				{operator: -, /, %, <<, >>, ...}	<br>
 * 	|--	|--	SedMultiExpression				{operator: +, *, &, |, ^, &&, ||}	<br>
 * 	|--	|--	SedCallExpression													<br>
 * 	|--	|--	SedFieldExpression													<br>
 * 	|--	|--	SedInitializerList													<br>
 * 	+--------------------------------------------------------------------------+<br>
 * 	|--	<i>SedStatement</i>								{source: CirStatement}	<br>
 * 	|--	|--	SedAssignStatement													<br>
 * 	|--	|--	SedGotoStatement													<br>
 * 	|--	|--	SedIfStatement														<br>
 * 	|--	|--	SedCallStatement													<br>
 * 	|--	|--	SedLabelStatement													<br>
 * 	+--------------------------------------------------------------------------+<br>
 * 	|--	SedField										{name: String}			<br>
 * 	|--	SedArgumentList															<br>
 * 	|--	SedOperator										{operator: COperator}	<br>
 * 	|--	SedLabel										{source: CirStatement}	<br>
 * 	+--------------------------------------------------------------------------+<br>
 * 	|--	<i>SedAssertion</i>														<br>
 * 	|--	|--	SedConditionAssertion												<br>
 * 	|--	|--	SedStatementAssertion												<br>
 * 	|--	|--	SedConjunctAssertion												<br>
 * 	|--	|--	SedDisjunctAssertion												<br>
 * 	+--------------------------------------------------------------------------+<br>
 * 	|--	<i>SedMutation</i>														<br>
 * 	|--	|--	SedSetExpression													<br>
 * 	|--	|--	SedAddExpression													<br>
 * 	|--	|--	SedInsExpression													<br>
 * 	|--	|--	SedInsOperator														<br>
 * 	|--	|--	SedSetOperator														<br>
 * 	|--	|--	SedMutExpression													<br>
 * 	+--------------------------------------------------------------------------+<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SedNode {	
	
	/* definitions */
	/** the source to which the SedNode represents **/
	private CirNode source;
	/** the parent of the node or null if it is root **/
	private SedNode parent;
	/** the index of the child under its parent or -1 **/
	private int child_index;
	/** the children added under this node **/
	private List<SedNode> children;
	/**
	 * create an isolated SedNode w.r.t. the source of CirNode
	 * @param source null if no source is linked from the node
	 */
	protected SedNode(CirNode source) {
		this.source = source;
		this.parent = null;
		this.child_index = -1;
		this.children = new LinkedList<SedNode>();
	}
	
	/* getters */
	/**
	 * @return whether the SedNode refers to any source in CIR code
	 */
	public boolean has_source() { return this.source != null; }
	/**
	 * @return the CIR code to which the SedNode corresponds
	 */ 
	public CirNode get_source() { return this.source; }
	/**
	 * @return the parent of this node or null if it is root
	 */
	public SedNode get_parent() { return this.parent; }
	/**
	 * @return the index of the child under its parent or -1
	 */
	public int get_child_index() { return this.child_index; }
	/**
	 * @return whether the node is root
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return whether the node is leaf when it contains no children
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/**
	 * @return the children added under this node
	 */
	public Iterable<SedNode> get_children() { return children; }
	/**
	 * @return the number of children under the node
	 */
	public int number_of_children() { return children.size(); }
	/**
	 * @param k
	 * @return the kth child under this node
	 * @throws IndexOutOfBoundsException
	 */
	public SedNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	/**
	 * add the child at the tail of the children list under this node
	 * @param child
	 * @throws IllegalArgumentException
	 */
	public void add_child(SedNode child) throws IllegalArgumentException {
		if(child == null || child.parent != null) {
			throw new IllegalArgumentException("Invalid child: " + child);
		}
		else {
			child.child_index = this.children.size();
			child.parent = this;
			this.children.add(child);
			return;
		}
	}
	@Override
	public SedNode clone() {
		SedNode parent = this.copy_self();
		for(SedNode child : this.children) {
			parent.add_child(child.clone());
		}
		return parent;
	}
	@Override
	public String toString() {
		try {
			return this.generate_code();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public boolean equals(Object another) {
		if(another == this) {
			return true;
		}
		else if(another instanceof SedNode) {
			return another.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* implementation methods */
	/**
	 * @return generate the copy of this type of node without children and parent
	 */
	protected abstract SedNode copy_self();
	/**
	 * @return generate the code that describes the symbolic execution description
	 * 		   language code.
	 * @throws Exception
	 */
	public abstract String generate_code() throws Exception;
	
}