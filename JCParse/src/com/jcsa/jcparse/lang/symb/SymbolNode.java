package com.jcsa.jcparse.lang.symb;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;

/**
 * 	It describes the syntactic definition of node used in symbolic analysis, including as following:	<br>
 * 	<code>
 * 	SymbolNode							[get_source(); get_parent(); get_children(); clone()]			<br>
 * 	|--	SymbolElement					(<i>non-typed elements used in symbolic analysis</i>)			<br>
 * 	|--	|--	SymbolField					[get_name(): String]											<br>
 * 	|--	|--	SymbolOperator				[get_operator(): COperator]										<br>
 * 	|--	|--	SymbolType					[get_type(): CType]												<br>
 * 	|--	|--	SymbolArgumentList			{arg_list |--> (expression {, expression}*)}					<br>
 * 	|--	SymbolExpression				[get_data_type();]												<br>
 * 	|--	|--	SymbolBasicExpression		(<i>leaf expression used in the computation</i>)				<br>
 * 	|--	|--	|--	SymbolIdentifier		[get_name(): String]											<br>
 * 	|--	|--	|--	SymbolConstant			[get_constant(): CConstant; ...]								<br>
 * 	|--	|--	|--	SymbolLiteral			[get_literal(): String]											<br>
 * 	|--	|--	SymbolUnaryExpression		{unary_expr |--> (+, -, ~, !, &, *, ++, --, p++, p--) expr}		<br>
 * 	|--	|--	SymbolBinaryExpression		{+,-,*,/,%, &,|,^,<<,>>, &&,||, <,<=,>,>=,==,!=, :=}			<br>
 * 	|--	|--	SymbolCastExpression		{cast_expr 	|--> (type) expression}								<br>
 * 	|--	|--	SymbolFieldExpression		{field_expr |--> expression.field}								<br>
 * 	|--	|--	SymbolCallExpression		{call_expr 	|--> expression argument_list}						<br>
 * 	|--	|--	SymbolInitializerLiist		{init_list	|--> {expr (, expr)*}}								<br>
 * 	|--	|--	SymbolConditionExpression	{cond_expr	|--> expr ? expr : expr}							<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class SymbolNode {
	
	/* static parameter for type generation */
	protected static final CTypeFactory type_factory = new CTypeFactory();
	
	/* attributes and constructor */
	/** the category of this symbolic node that it is defined **/
	private SymbolClass			_class;
	/** the source object that this symbolic node represents **/
	private Object 				source;
	/** the parent of this node or null if this node is root **/
	private SymbolNode 			parent;
	/** the child nodes inserted under this node as its next **/
	private List<SymbolNode> 	children;
	/** It creates an isolated node without parent and child */
	protected SymbolNode(SymbolClass _class) throws Exception {
		if(_class == null) {
			throw new IllegalArgumentException("Invalid class: " + _class);
		}
		else {
			this._class = _class;
			this.source = null;
			this.parent = null;
			this.children = new ArrayList<SymbolNode>();
		}
	}
	
	/* getters */
	/**
	 * @return the category of this symbolic node
	 */
	public SymbolClass get_symbol_class() { return this._class; }
	/**
	 * @return whether there exists the object that this node represents
	 */
	public boolean has_source() { return this.source != null; }
	/**
	 * @return the parent of this node or null if this node is root
	 */
	public Object get_source() { return this.source; }
	/**
	 * @return whether this node is a root without any parent used
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return the parent of this node or null if this node is root
	 */
	public SymbolNode get_parent() { return this.parent; }
	/**
	 * @return whether this node is a leaf without any child
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/** 
	 * @return the child nodes inserted under this node as its next
	 */
	public Iterable<SymbolNode> get_children() { return this.children; }
	/**
	 * @return the number of child nodes inserted in this node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k	the index to retrieve the child node
	 * @return	the kth child under this symbol node
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolNode get_child(int k) throws IndexOutOfBoundsException { return this.children.get(k); }
	
	/* setters */
	/**
	 * @param source the Java object to be establish as the source of this node
	 */
	protected void set_source(Object source) { this.source = source; }
	/**
	 * @param child the node to be inserted in the tail of this node's children
	 * @throws Exception
	 */
	protected void add_child(SymbolNode child) throws IllegalArgumentException {
		if(child == null) {
			throw new IllegalArgumentException("Invalid child: null");
		}
		else {
			if(child.get_parent() != null) {
				child = (SymbolNode) child.clone();
			}
			child.parent = this;
			this.children.add(child);
		}
	}
	
	/* implementation methods */
	/**
	 * @return the isolated instancce copied from this one
	 */
	protected abstract SymbolNode construct_copy() throws Exception;
	/**
	 * @param simplified	whether to generate the simplified code of the node
	 * @return				the code that this symbolic node is described by
	 * @throws Exception
	 */
	protected abstract String get_code(boolean simplified) throws Exception;
	/**
	 * @return whether this node is a reference expression
	 */
	protected abstract boolean is_refer_type();
	/**
	 * @return whether this node contains side-effect in local position
	 */
	protected abstract boolean is_side_affected();
	
	/* general methods */
	@Override
	public SymbolNode clone() {
		SymbolNode parent;
		try {
			parent = this.construct_copy();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
		for(SymbolNode child : this.children) {
			parent.add_child(child);
		}
		parent.set_source(this.source);
		return parent;
	}
	/**
	 * @param simplified	whether to generate the simplified code of the node
	 * @return				the code that this symbolic node is described by
	 * @throws Exception
	 */
	public String generate_code(boolean simplified) throws Exception {
		return this.get_code(simplified);
	}
	/**
	 * @return the simplified version of the code describing this node
	 * @throws Exception
	 */
	public String generate_simple_code() throws Exception {
		return this.get_code(true);
	}
	/**
	 * @return non-simplified version of the code describing this node
	 * @throws Exception
	 */
	public String generate_unique_code() throws Exception {
		return this.get_code(false);
	}
	@Override
	public String toString() {
		try {
			return this.generate_unique_code();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof SymbolNode) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	/**
	 * @return whether this node is a reference expression
	 */
	public boolean is_reference() { return this.is_refer_type(); }
	/**
	 * @return whether this symbolic node contains side-effects
	 */
	public boolean has_side_effect() {
		if(this.is_side_affected()) {
			return true;
		}
		else {
			for(SymbolNode child : this.children) {
				if(child.has_side_effect()) {
					return true;
				}
			}
			return false;
		}
	}
	
}
