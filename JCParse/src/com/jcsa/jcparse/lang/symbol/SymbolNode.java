package com.jcsa.jcparse.lang.symbol;

import java.util.ArrayList;
import java.util.List;


/**
 * 	This class defines the abstract top-class of symbolic nodes used to describe
 * 	the symbolic expressions used in Z3-theorem prover and satisfiability proof.
 * 	<br>
 * 	<code>
 * 	SymbolNode								[_class; source; parent; children]	<br>
 * 	|--	SymbolElement						(Non-Expression as Elemental Node)	<br>
 * 	|--	|--	SymbolArgumentList				{arg_list --> (expr {, expr}*)}		<br>		
 * 	|--	|--	SymbolField						[field_name: String]				<br>
 * 	|--	|--	SymbolType						[type: CType]						<br>
 * 	|--	|--	SymbolOperator					[operator: COperator]				<br>
 * 	|--	SymbolExpression					[data_type: CType]					<br>
 * 	|--	|--	SymbolBasicExpression			(basic expression as the leaf node)	<br>
 * 	|--	|--	|--	SymbolIdentifier			[name: String; scope: Object;]		<br>
 * 	|--	|--	|--	SymbolConstant				[constant: CConstant]				<br>
 * 	|--	|--	|--	SymbolLiteral				[literal: String]					<br>
 * 	|--	|--	SymbolCompositeExpression		(comp_expr --> operator expression)	<br>
 * 	|--	|--	|--	SymbolUnaryExpression		(unary)	[neg, rsv, not, adr, ref]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(arith)	[add, sub, mul, div, mod]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(bitws)	[and, ior, xor, lsh, rsh]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(logic)	[and, ior, eqv, neq, imp]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(relate)[grt, gre, smt, sme, neq...]<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(assign)[ass, pss]					<br>
 * 	|--	|--	SymbolSpecialExpression												<br>
 * 	|--	|--	|--	SymbolCastExpression		(cast_expr --> {type_name} expr)	<br>
 * 	|--	|--	|--	SymbolCallExpression		(call_expr --> expr arg_list)		<br>
 * 	|--	|--	|--	SymbolConditionExpression	(cond_expr --> expr ? expr : expr)	<br>
 * 	|--	|--	|--	SymbolInitializerList		(init_list --> {expr (, expr)*})	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class SymbolNode {
	
	/* attributes */
	/** the class of symbolic node **/
	private	SymbolClass			_class;
	/** the source this node refers to **/
	private	Object				source;
	/** the parent of this node or null **/
	private	SymbolNode			parent;
	/** the children inserted under the node **/
	private	List<SymbolNode>	children;
	
	/* constructor */
	/**
	 * It only sets the _class attribute but remains all the others
	 * @param _class
	 * @throws Exception
	 */
	protected SymbolNode(SymbolClass _class) throws Exception {
		if(_class == null) {
			throw new IllegalArgumentException("Invalid _class: null");
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
	 * @return the class of this symbolic node
	 */
	public	SymbolClass	get_sclass()			{ return this._class; }
	/**
	 * @return whether there is a source that this node represents
	 */
	public	boolean		has_source()			{ return this.source != null; }
	/**
	 * @return the source object that the symbolic node represents
	 */
	public	Object		get_source()			{ return this.source; }
	/**
	 * @return whether this node is a root without any parent
	 */
	public	boolean		is_root()				{ return this.parent == null; }
	/**
	 * @return the parent of this node or null for root node
	 */
	public	SymbolNode	get_parent()			{ return this.parent; }
	/**
	 * @return whether this node is a leaf without any child
	 */
	public	boolean		is_leaf()				{ return this.children.isEmpty(); }
	/**
	 * @return the number of children under this node
	 */
	public	int			number_of_children()	{ return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child node under this node
	 * @throws IndexOutOfBoundsException
	 */
	public	SymbolNode	get_child(int k) throws IndexOutOfBoundsException { return this.children.get(k); }
	/**
	 * @return the list of child-nodes inserted under this node
	 */
	public 	Iterable<SymbolNode> get_children()	{ return this.children; }	
	
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
	 * @return it creates a new isolated node that copies this one
	 * @throws Exception
	 */
	protected abstract SymbolNode new_one() throws Exception;
	/**
	 * @param simplified whether to create simplified code
	 * @return	the code describing the symbolic expression
	 * @throws Exception
	 */
	protected abstract String generate_code(boolean simplified) throws Exception;
	/**
	 * @return whether this node is a reference-expression
	 */
	protected abstract boolean is_refer_type();
	/**
	 * @return whether this node produces side-effects
	 */
	protected abstract boolean is_side_affected();
	
	/* general methods */
	@Override
	public 	SymbolNode	clone() {
		/* 1. creates an isolated copy */
		SymbolNode parent;
		try {
			parent = this.new_one();
		}
		catch(Exception ex) {
			return null;
		}
		
		/* 2. set the data source */
		parent.set_source(this.source);
		
		/* 3. add the children to parent */
		for(SymbolNode child : this.children) {
			parent.add_child(child.clone());
		}
		
		/* 3. return completion */	return parent;
	}
	@Override
	public String toString() { return this.generate_unique_code(); }
	@Override
	public int hashCode() { return this.toString().hashCode(); }
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
	/**
	 * @return whether this node is a reference-expression
	 */
	public boolean is_reference() { return this.is_refer_type(); }
	/**
	 * @return whether the evaluation of this node can produce side-effects
	 */
	public boolean has_side_effects() {
		if(this.is_side_affected()) {
			return true;
		}
		else {
			for(SymbolNode child : this.children) {
				if(child.has_side_effects()) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @return the simplified version
	 */
	public String generate_simple_code() { 
		try {
			return this.generate_code(true);
		}
		catch(Exception ex) {
			ex.printStackTrace(); return null;
		}
	}
	/**
	 * @return the unique code
	 */
	public String generate_unique_code() {
		try {
			return this.generate_code(false);
		}
		catch(Exception ex) {
			ex.printStackTrace(); return null;
		}
	}
	
}
