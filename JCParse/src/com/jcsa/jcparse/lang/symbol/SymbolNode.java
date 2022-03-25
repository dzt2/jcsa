package com.jcsa.jcparse.lang.symbol;

import java.util.ArrayList;
import java.util.List;


/**
 * 	It describes the syntactic structure of symbolic expressions or functions
 * 	used in static program analysis, of which grammer is defined as following.
 * 	<br>
 * 	<code>
 * 	SymbolNode								[_class, source, parent, children]	<br>
 * 	|--	SymbolElement						(non-typed symbolic node as token)	<br>
 * 	|--	|--	SymbolType						[type_name: CType]					<br>
 * 	|--	|--	SymbolField						[field_name: String]				<br>
 * 	|--	|--	SymbolOperator					[operator: COperator]				<br>
 * 	|--	|--	SymbolArgumentList				(args_list --> (expr {, expr}+))	<br>
 * 	|--	SymbolExpression					(typed evaluation unit) [data_type]	<br>
 * 	|--	|--	SymbolBasicExpression			(basic expression without child)	<br>
 * 	|--	|--	|--	SymbolIdentifier			[name: String, scope: Object]		<br>
 * 	|--	|--	|--	SymbolConstant				[constant: CConstant]				<br>
 * 	|--	|--	|--	SymbolLiteral				[literal: String]					<br>
 * 	|--	|--	SymbolCompositeExpression		[comp_expr --> operator expression+]<br>
 * 	|--	|--	|--	SymbolUnaryExpression		(unary)	[neg, rsv, not, adr, ref]	<br>
 * 	|--	|--	|--	SymbolArithExpression		(arith)	[add, sub, mul, div, mod]	<br>
 * 	|--	|--	|--	SymbolBitwsExpression		(bitws)	[and, ior, xor, lsh, rsh]	<br>
 * 	|--	|--	|--	SymbolLogicExpression		(logic)	[and, ior, eqv, neq, imp]	<br>
 * 	|--	|--	|--	SymbolRelationExpression	(relate)[grt, gre, smt, sme, neq]	<br>
 * 	|--	|--	|--	SymbolAssignExpression		(assign)[eas, ias]					<br>
 * 	|--	|--	SymbolSpecialExpression												<br>
 * 	|--	|--	|--	SymbolCastExpression		(cast_expr --> {type_name} expr)	<br>
 * 	|--	|--	|--	SymbolInitializerList		(seq_list --> (expression+))		<br>
 * 	|--	|--	|--	SymbolCallExpression		(call_expr --> expr seq_list)		<br>
 * 	|--	|--	|--	SymbolIfElseExpression		(cond_expr --> expr ? expr : expr)	<br>
 * 	|--	|--	|--	SymbolFieldExpression		(field_expr --> expr.field)			<br>
 * 	|--	|--	|--	SymbolExpressionList		(expr_list --> (expr (, expr)+))	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class SymbolNode {
	
	/* attributes */
	/** the class of the symbolic node **/
	private SymbolClass			_class;
	/** the source from which the node is produced **/
	private	Object				source;
	/** the parent of this node or null if it is root **/
	private	SymbolNode			parent;
	/** the child nodes inserted under this node **/
	private	List<SymbolNode>	children;
	/**
	 * It constructs an isolated node w.r.t. the given class without any content
	 * @param _class
	 * @throws IllegalArgumentException
	 */
	protected SymbolNode(SymbolClass _class) throws IllegalArgumentException {
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
	 * @return the class of the symbolic node
	 */
	public SymbolClass	get_symbol_class() 	{ return this._class; }
	/**
	 * @return the source from which the node is produced
	 */
	public Object		get_source()		{ return this.source; }
	/**
	 * @return whether this node is a root without parent
	 */
	public boolean		is_root()			{ return this.parent == null; }
	/**
	 * @return the parent of this node or null if it is root 
	 */
	public SymbolNode	get_parent() 		{ return this.parent; }
	/**
	 * @return whether this node is a leaf without and child
	 */
	public boolean		is_leaf()			{ return this.children.isEmpty(); }
	/**
	 * @return the number of child nodes inserted under this one
	 */
	public int			number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child node inserted under this node
	 * @throws IndexOutOfBoundsException
	 */
	public SymbolNode	get_child(int k) throws IndexOutOfBoundsException { return this.children.get(k); }
	/**
	 * @return the children inserted under this node
	 */
	public Iterable<SymbolNode> get_children() { return this.children; }
	
	/* implement */
	/**
	 * @return it creates an isolated node as the copy of this
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
	 * @return whether this node produces the side-effects
	 */
	protected abstract boolean is_side_affected();
	
	/* generalize */
	/**
	 * @return the simple code to describe this symbolic node
	 * @throws Exception
	 */
	public String get_simple_code() { 
		try {
			return this.generate_code(true);
		} catch (Exception e) {
			e.printStackTrace(); return null;
		} 
	}
	/** 
	 * @return the unique code to describe this symbolic node
	 * @throws Exception
	 */
	public String get_unique_code() { 
		try {
			return this.generate_code(false);
		} catch (Exception e) {
			e.printStackTrace(); return null;
		} 
	}
	@Override
	public String toString() { return this.get_unique_code(); }
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
	@Override
	public SymbolNode clone() {
		SymbolNode parent;
		try {
			parent = this.new_one();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
		parent.set_source(this.source);
		for(SymbolNode child : this.children) {
			parent.add_child(child.clone());
		}
		return parent;
	}
	
	/* setters */
	/**
	 * It adds a new child in the tail of this node's children
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void add_child(SymbolNode child) throws IllegalArgumentException {
		if(child == null) {
			throw new IllegalArgumentException("Invalid child: null");
		}
		else {
			if(child.parent != null) {
				child = child.clone();
			}
			this.children.add(child);
			child.parent = this;
		}
	}
	/**
	 * It sets the source from which this node is created
	 * @param source
	 */
	protected void set_source(Object source) { this.source = source; }
	
}
