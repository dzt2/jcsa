package com.jcsa.jcparse.lang.symbol;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.parse.code.SymbolCodeGenerator;

/**
 * 	It defines the data model to describe syntactic structure of expressions in symbolic analysis, defined as following.	<br>
 * 	<br>
 * 	<code>
 * 	SymbolNode							{source; clone(); generate_code(boolean);}											<br>
 * 	|--	SymbolUnit						[<i>usually taken as leaf or connector</i>]											<br>
 * 	|--	|--	SymbolArgumentList			[<i>SymbolCallExpression.children[1]</i>]											<br>
 * 	|--	|--	SymbolField					{name: String}	[<i>SymbolFieldExpression.children[1]</i>]							<br>
 * 	|--	|--	SymbolOperator				{operator: COperator}																<br>
 * 	|--	SymbolExpression				{data_type: CType;}																	<br>
 * 	|--	|--	SymbolBasicExpression		[<i>as leaf in SymbolExpression tree</i>]											<br>
 * 	|--	|--	|--	SymbolIdentifier		{name: String}																		<br>
 * 	|--	|--	|--	SymbolConstant			{constant: CConstant}																<br>
 * 	|--	|--	|--	SymbolLiteral			{literal: String}																	<br>
 *	|--	|--	SymbolBinaryExpression		{operator: +, -, *, /, %, &, |, ^, <<, >>, &&, ||}									<br>
 *	|--	|--	SymbolUnaryExpression		{operator: -, ~, !, *, &, =} 														<br>
 *	|--	|--	SymbolFieldExpression																							<br>
 *	|--	|--	SymbolCallExpression																							<br>
 *	|--	|--	SymbolInitializerList																							<br>
 * 	</code>
 * 	<br>
 * 	@author yukimula
 *
 */
public abstract class SymbolNode {
	
	/* attributes */
	/** parent of this symbolic node or null if it is root **/
	private SymbolNode parent;
	/** the index of this node as child of its parent or -1 if the node is root **/
	private int child_index;
	/** sequence of children under the symbolic tree node **/
	private List<SymbolNode> children;
	/** either AstNode, CirNode, CirExecution etc. from which it is created from factory **/
	private Object source;
	
	/* constructor */
	/**
	 * create an isolated symbolic node in tree
	 */
	protected SymbolNode() {
		this.parent = null;
		this.child_index = -1;
		this.children = new LinkedList<SymbolNode>();
		this.source = null;
	}
	
	/* getters */
	/**
	 * @return whether the node is root without parent
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return whether the node is leaf without children
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/**
	 * @return parent of this symbolic node or null if it is root
	 */
	public SymbolNode get_parent() { return this.parent; }
	/**
	 * @return the index of this node as child of its parent or -1 if the node is root
	 */
	public int get_child_index() { return this.child_index; }
	/**
	 * @return sequence of children under the symbolic tree node
	 */
	public Iterable<SymbolNode> get_children() { return this.children; }
	/**
	 * @return number of children under this node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child under the node
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolNode get_child(int k) throws IndexOutOfBoundsException { return this.children.get(k); }
	/**
	 * @return whether the node contains any source in the tree
	 */
	public boolean has_source() { return this.source != null; }
	/**
	 * @return the source from which the symbolic node is created
	 */
	public Object get_source() { return this.source; }
	
	/* setters */
	/**
	 * set the source of the symbolic node
	 * @param source
	 */
	public void set_source(Object source) { this.source = source; }
	/**
	 * add the child at the tail of the children list in the node
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void add_child(SymbolNode child) throws IllegalArgumentException {
		if(child == null)
			throw new IllegalArgumentException("Invalid child: " + child);
		else {
			if(child.parent != null)
				child = child.clone();
			child.parent = this;
			child.child_index = this.children.size();
			this.children.add(child);
		}
	}
	
	/* abstract methods */
	/**
	 * @return generate the copy of this node as isolated node without parent and children and no source
	 * @throws Exception
	 */
	protected abstract SymbolNode construct() throws Exception;
	
	/* general methods */
	public SymbolNode clone() {
		SymbolNode parent;
		try {
			parent = this.construct();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		for(SymbolNode child : this.children) {
			parent.add_child(child.clone());
		}
		parent.set_source(this.source);
		
		return parent;
	}
	/**
	 * @param simplified whether to generate simplified code for describing SymbolNode
	 * @return
	 * @throws Exception
	 */
	public String generate_code(boolean simplified) throws Exception {
		return SymbolCodeGenerator.generate_code(this, simplified);
	}
	@Override
	public String toString() {
		try {
			return this.generate_code(false);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof SymbolNode) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* verification methods */
	/**
	 * @return true for {identifier|field_expression|de_reference}
	 */
	public boolean is_reference() {
		return SymbolNode.is_reference(this);
	}
	/**
	 * @param node
	 * @return true for {identifier|field_expression|de_reference}
	 */
	public static boolean is_reference(SymbolNode node) {
		if(node instanceof SymbolIdentifier) {
			return true;
		}
		else if(node instanceof SymbolFieldExpression) {
			return true;
		}
		else if(node instanceof SymbolUnaryExpression) {
			return ((SymbolUnaryExpression) node).get_operator().get_operator() == COperator.dereference;
		}
		else {
			return false;
		}
	}
	
}
