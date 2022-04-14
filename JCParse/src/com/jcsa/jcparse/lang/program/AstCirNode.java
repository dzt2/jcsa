package com.jcsa.jcparse.lang.program;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstField;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.program.types.AstCirDataType;
import com.jcsa.jcparse.lang.program.types.AstCirEdgeType;
import com.jcsa.jcparse.lang.program.types.AstCirNodeType;
import com.jcsa.jcparse.lang.program.types.AstCirParChild;

/**
 * 	It represents a functional simplified abstract syntactic node w.r.t. the 
 * 	CirNode(s) using AstCirData, and organized as tree + graph structure.
 * 	@author yukimula
 *
 */
public class AstCirNode {
	
	/* definitions */
	/** the tree in which this node is created **/
	private	AstCirTree			tree;
	/** the unique integer ID of this node in tree **/
	private	int					node_id;
	/** the syntax-directed type of this node based on its source **/
	private	AstCirNodeType		type;
	/** the syntactic node that this tree node refers and represents **/
	private	AstNode				source;
	/** the token to represent the content of this node **/
	private	Object				token;
	/** the list of data-connection from AstNode to CirNode(s) related **/
	private	List<AstCirData>	data_list;
	/** the parent of this node or null if this node is a root **/
	private	AstCirNode			parent;
	/** the type of the node in the context of its parent or null if it is root **/
	private	AstCirParChild		pc_type;
	/** the list of child nodes inserted under this one in tree-structure model **/
	private	List<AstCirNode>	children;
	/** the set of edges from other nodes to this AstCirNode as dependence **/
	private	List<AstCirEdge>	in_edges;
	/** the set of edges from this AstCirNode to other nodes as dependence **/
	private	List<AstCirEdge>	ou_edges;
	
	/* getters */
	/**
	 * @return the tree in which this node is created
	 */
	public	AstCirTree				get_tree()			{ return this.tree; }
	/**
	 * @return the unique integer ID of this node in tree
	 */
	public	int						get_node_id()		{ return this.node_id; }
	/**
	 * @return the syntax-directed type of this node based on its source
	 */
	public	AstCirNodeType			get_node_type()		{ return this.type; }
	/**
	 * @return the syntactic node that this tree node refers and represents
	 */
	public	AstNode					get_ast_source()	{ return this.source; }
	/**
	 * @return the token that represents the AstNode source's content
	 */
	public 	Object					get_token()			{ return this.token; }
	/**
	 * @return the list of data-connection from AstNode to CirNode(s) related
	 */
	public	Iterable<AstCirData>	get_data_items()	{ return this.data_list; }
	/**
	 * @return whether this node is a root without any parent
	 */
	public	boolean					is_root()			{ return this.parent == null; }
	/**
	 * @return the parent of this node or null if this node is a root 
	 */
	public	AstCirNode				get_parent()		{ return this.parent; }
	/**
	 * @return the type of the node in the context of its parent or null for root
	 */
	public	AstCirParChild			get_child_type()	{ return this.pc_type; }
	/**
	 * @return whether this node is a leaf without any child
	 */
	public	boolean					is_leaf()			{ return this.children.isEmpty(); }
	/**
	 * @return the list of child nodes inserted under this one in tree-structure model
	 */
	public	Iterable<AstCirNode>	get_children()		{ return this.children; }
	/**
	 * @return the set of edges from other nodes to this AstCirNode as dependence
	 */
	public	Iterable<AstCirEdge>	get_in_edges()		{ return this.in_edges; }
	/**
	 * @return the set of edges from this AstCirNode to other nodes as dependence
	 */
	public	Iterable<AstCirEdge>	get_ou_edges()		{ return this.ou_edges; }
	
	/* index */
	/**
	 * @return the number of data-connections from AstNode to CirNode related
	 */
	public	int			number_of_data_items()	{ return this.data_list.size(); }
	/**
	 * @return the number of children inserted under this node
	 */
	public	int			number_of_children()	{ return this.children.size(); }
	/**
	 * @return the number of dependence edges from others to this node
	 */
	public	int			get_in_degree()			{ return this.in_edges.size(); }
	/**
	 * @return the number of dependence edges from this node to others
	 */
	public	int			get_ou_degree()			{ return this.ou_edges.size(); }
	/**
	 * @param k
	 * @return the kth data-connection from this AstNode to the CirNode related
	 * @throws IndexOutOfBoundsException
	 */
	public	AstCirData	get_data_item(int k) throws IndexOutOfBoundsException { return this.data_list.get(k); }
	/**
	 * @param k
	 * @return the kth child inserted under this node
	 * @throws IndexOutOfBoundsException
	 */
	public	AstCirNode	get_child(int k) throws IndexOutOfBoundsException { return this.children.get(k); }
	/**
	 * @param k
	 * @return the kth edge from other node to this one
	 * @throws IndexOutOfBoundsException
	 */
	public	AstCirEdge	get_in_edge(int k) throws IndexOutOfBoundsException { return this.in_edges.get(k); }
	/**
	 * @param k
	 * @return the kth edge from this node to the other
	 * @throws IndexOutOfBoundsException
	 */
	public	AstCirEdge	get_ou_edge(int k) throws IndexOutOfBoundsException { return this.ou_edges.get(k); }
	
	/* setters */
	/**
	 * It creates an isolated node in AstCirTree with specified ID and AST-source
	 * @param tree		the tree in which this node is created
	 * @param node_id	the unique integer ID of this node in tree
	 * @param source	the syntactic node that this node refer to
	 * @throws IllegalArgumentException
	 */
	protected AstCirNode(AstCirTree tree, int node_id, AstNode source, Object token) throws IllegalArgumentException {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as: null");
		}
		else if(node_id < 0) {
			throw new IllegalArgumentException("Invalid node_id: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			this.tree = tree; this.node_id = node_id;
			this.type = this.new_type(source);
			this.source = source; this.token = token;
			this.data_list = new ArrayList<AstCirData>();
			this.pc_type = null; this.parent = null;
			this.children = new ArrayList<AstCirNode>();
			this.in_edges = new ArrayList<AstCirEdge>();
			this.ou_edges = new ArrayList<AstCirEdge>();
		}
	}
	/**
	 * @param source
	 * @return the inferred type from the source
	 * @throws IllegalArgumentException
	 */
	private	AstCirNodeType	new_type(AstNode source) throws IllegalArgumentException {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstTranslationUnit)		{ return AstCirNodeType.tra_unit; }
		else if(source instanceof AstFunctionDefinition)	{ return AstCirNodeType.func_def; } 
		else if(source instanceof AstField)					{ return AstCirNodeType.name_expr; }
		else if(source instanceof AstIdExpression || 
				source instanceof AstName) 					{ return AstCirNodeType.name_expr; }
		else if(source instanceof AstConstant ||
				source instanceof AstSizeofExpression)		{ return AstCirNodeType.cons_expr; }
		else if(source instanceof AstLiteral)				{ return AstCirNodeType.strg_expr; }
		else if(source instanceof AstKeyword) 				{
			if(((AstKeyword) source).get_keyword() == CKeyword.c89_return) {
				return AstCirNodeType.name_expr;
			}
			else {
				throw new IllegalArgumentException("Invalid: " + ((AstKeyword) source).get_keyword());
			}
		}
		else if(source instanceof AstArrayExpression || 
				source instanceof AstFieldExpression) 		{ return AstCirNodeType.refr_expr; }
		else if(source instanceof AstPointUnaryExpression) 	{
			if(((AstPointUnaryExpression) source).get_operator().get_operator() == COperator.dereference) {
				return AstCirNodeType.refr_expr;
			}
			else {
				return AstCirNodeType.unry_expr;
			}
		}
		else if(source instanceof AstInitDeclarator)		{ return AstCirNodeType.decl_expr; }
		else if(source instanceof AstArithAssignExpression || 
				source instanceof AstBitwiseAssignExpression || 
				source instanceof AstShiftAssignExpression || 
				source instanceof AstAssignExpression) 		{ return AstCirNodeType.assg_expr; }
		else if(source instanceof AstIncrePostfixExpression ||
				source instanceof AstIncreUnaryExpression)	{ return AstCirNodeType.incr_expr; }
		else if(source instanceof AstArithBinaryExpression || 
				source instanceof AstBitwiseBinaryExpression || 
				source instanceof AstShiftBinaryExpression ||
				source instanceof AstLogicBinaryExpression ||
				source instanceof AstRelationExpression)	{ return AstCirNodeType.biny_expr; }
		else if(source instanceof AstUnaryExpression)		{ return AstCirNodeType.unry_expr; }
		else if(source instanceof AstCastExpression)		{ return AstCirNodeType.cast_expr; }
		else if(source instanceof AstConditionalExpression)	{ return AstCirNodeType.cond_expr; }
		else if(source instanceof AstCommaExpression)		{ return AstCirNodeType.coma_expr; }
		else if(source instanceof AstInitializerBody)		{ return AstCirNodeType.init_body; }
		else if(source instanceof AstFunCallExpression)		{ return AstCirNodeType.call_expr; }
		else if(source instanceof AstDeclarationStatement ||
				source instanceof AstExpressionStatement)	{ return AstCirNodeType.expr_stmt; }
		else if(source instanceof AstCompoundStatement)		{ return AstCirNodeType.comp_stmt; }
		else if(source instanceof AstBreakStatement ||
				source instanceof AstContinueStatement ||
				source instanceof AstGotoStatement)			{ return AstCirNodeType.skip_stmt; }
		else if(source instanceof AstLabeledStatement ||
				source instanceof AstDefaultStatement)		{ return AstCirNodeType.labl_stmt; }
		else if(source instanceof AstIfStatement)			{ return AstCirNodeType.ifte_stmt; }
		else if(source instanceof AstCaseStatement)			{ return AstCirNodeType.case_stmt; }
		else if(source instanceof AstSwitchStatement)		{ return AstCirNodeType.swit_stmt; }
		else if(source instanceof AstReturnStatement)		{ return AstCirNodeType.retr_stmt; }
		else if(source instanceof AstDoWhileStatement ||
				source instanceof AstWhileStatement ||
				source instanceof AstForStatement)			{ return AstCirNodeType.loop_stmt; }
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	/**
	 * It appends a child under this node using the given link tag
	 * @param link
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void 			add_child(AstCirParChild type, AstCirNode child) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(child == null || child.parent != null) {
			throw new IllegalArgumentException("Invalid child: " + child);
		}
		else {
			child.pc_type = type; child.parent = this; this.children.add(child);
		}
	}
	/**
	 * It creates a new data state under this node using given location and type
	 * @param type
	 * @param location
	 * @throws IllegalArgumentException
	 */
	protected AstCirData 	add_state(AstCirDataType type, CirNode location) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type as null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {
			this.data_list.add(new AstCirData(this, type, location));
			return this.data_list.get(this.data_list.size() - 1);
		}
	}
	/**
	 * It creates a dependence (semantic) related edge from this node to the target with given type
	 * @param type
	 * @param target
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected AstCirEdge	link_edge(AstCirEdgeType type, AstCirNode target) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type as null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			AstCirEdge edge = new AstCirEdge(type, this, target);
			this.ou_edges.add(edge); target.in_edges.add(edge);
			return edge;
		}
	}
	
}
