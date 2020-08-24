package com.jcsa.jcmutest.mutant.sad2mutant;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * Symbolic Assertion Description language defines the language to describe
 * the symbolic assertion in C program such that the constraints as well as 
 * symbolic state errors caused by mutation in static and dynamic analysis.
 * <br>
 * <br>
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	SadNode								{source: AstNode|CirNode}		<br>
 * 	+------------------------------------------------------------------+<br>
 * 	SadExpression						{data_type: CType}				<br>
 * 	|--	SadBasicExpression												<br>
 * 	|--	|--	SadIdExpression				{identifier: String}			<br>
 * 	|--	|--	SadConstant					{constant: CConstant}			<br>
 * 	|--	|--	SadLiteral					{literal: String}				<br>
 * 	|--	|--	SadDefaultValue												<br>
 * 	|--	SadUnaryExpression				{+,-,~,!,*,&,cast}				<br>
 * 	|-- SadBinaryExpression				{-,/,%,<<,>>,<,<=,>,>=,==,!=}	<br>
 * 	|-- SadMultiExpression				{+,*,&,|,^,&&,||}				<br>
 * 	|--	SadFieldExpression												<br>
 * 	|--	SadInitializerList												<br>
 * 	|--	SadCallExpression												<br>
 * 	+------------------------------------------------------------------+<br>
 * 	SadStatement														<br>
 * 	|--	SadAssignStatement												<br>
 * 	|--	SadGotoStatement												<br>
 * 	|--	SadIfStatement													<br>
 * 	|-- SadCallStatement												<br>
 * 	|-- SadLabelStatement												<br>
 * 	+------------------------------------------------------------------+<br>
 * 	SadToken															<br>
 * 	|--	SadLabel						{pointer: CirExecution}			<br>
 * 	|-- SadField						{name: String}					<br>
 * 	|--	SadArgumentList													<br>
 * 	+------------------------------------------------------------------+<br>
 * 	SadAssertion						{location: SadStatement}		<br>
 * 	|--	SadExecuteOnAssertion											<br>
 * 	|--	SadConditionAssertion											<br>
 * 	|--	SadSetExpressionAssertion										<br>
 * 	|--	SadSetLabelAssertion											<br>
 * 	|--	SadAddOperandAssertion											<br>
 * 	|--	SadInsOperandAssertion											<br>
 * 	|--	SadAddOperatorAssertion											<br>
 * 	|--	SadInsOperatorAssertion											<br>
 * 	|--	SadMutExpressionAssertion										<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * <br>
 * @author yukimula
 *
 */
public abstract class SadNode {
	
	/* definition */
	/** either AstNode or CirNode **/
	private CirNode source;
	/** the parent of this node under the parent **/
	private SadNode parent;
	/** the index of the node in its parent or -1 **/
	private int child_index;
	/** the set of children under the parent node **/
	private List<SadNode> children;
	/**
	 * create an isolated node w.r.t. the cir-code range
	 * @param tree
	 * @param node_id
	 */
	protected SadNode(CirNode source) {
		this.source = source;
		this.parent = null;
		this.child_index = -1;
		this.children = new LinkedList<SadNode>();
	}
	
	/* getters */
	/**
	 * @return the ast-source to which the node refers or null
	 */
	public AstNode get_ast_source() {
		if(this.source == null)
			return null;
		else
			return this.source.get_ast_source();
	}
	/**
	 * @return the cir-source to which the node refers or null
	 */
	public CirNode get_cir_source() {
		return this.source;
	}
	/**
	 * @return the parent of this node or null
	 */
	public SadNode get_parent() {
		return this.parent;
	}
	/**
	 * @return the index of the node in its parent or -1
	 */
	public int get_child_index() {
		return this.child_index;
	}
	/**
	 * @return whether the node is a root without parent
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return whether the node is a leaf without children
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/**
	 * @return the children under this node
	 */
	public Iterable<SadNode> get_children() {
		return this.children;
	}
	/**
	 * @return the number of children under this node
	 */
	public int number_of_children() {
		return this.children.size();
	}
	/**
	 * @param k
	 * @return the kth child under this node
	 * @throws IndexOutOfBoundsException
	 */
	public SadNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	/**
	 * add the child to the tail of this node
	 * @param child
	 * @throws IllegalArgumentException
	 */
	public void add_child(SadNode child) throws IllegalArgumentException {
		if(child == null || child.parent != null)
			throw new IllegalArgumentException("Invalid child: " + child);
		else {
			child.child_index = this.children.size();
			child.parent = this;
			this.children.add(child);
		}
	}
	
	/* generator */
	/**
	 * @return generate the code describing the symbolic assertion node
	 * @throws Exception
	 */
	public abstract String generate_code() throws Exception;
	/**
	 * @return clone the isolated copy of this node
	 */
	protected abstract SadNode clone_self();
	/**
	 * generate the copy of this node
	 */
	public SadNode clone() {
		SadNode parent = this.clone_self();
		for(SadNode child : this.children) {
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
	
}
