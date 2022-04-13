package com.jcsa.jcmutest.mutant.sta2mutant.base;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
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
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
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
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	It represents the node in AstContextTree.
 * 	
 *	@author yukimula
 *
 */
public class AstContextNode {
	
	/* attributes */
	/** the tree where this node is created **/
	private	AstContextTree			tree;
	/** the integer ID of this node in the tree **/
	private	int						node_id;
	/** the type of the node in AstContext tree **/
	private AstContextNodeType		type;
	/** abstract syntactic node of the node **/
	private	AstNode					source;
	/** the list of data items linked from this node **/
	private	List<AstContextData>	state_list;
	/** the type of the link from parent to this node **/
	private	AstContextNodeLink		parent_link;
	/** the parent node of the node in tree **/
	private	AstContextNode			parent;
	/** the children insert under this node **/
	private	List<AstContextNode>	children;
	/** the dependence-related edges from **/
	private	List<AstContextEdge>	in_edges;
	/** the dependence-related edges forward **/
	private	List<AstContextEdge>	ou_edges;
	
	/* constructors */
	/**
	 * It creates an isolated node in the AstContextTree
	 * @param tree
	 * @param node_id
	 * @param source
	 * @throws Exception
	 */
	protected AstContextNode(AstContextTree tree, int node_id, AstNode source) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			this.tree = tree; this.node_id = node_id;
			this.source = source;
			this.type = this.derive_node_type(source);
			this.state_list = new ArrayList<AstContextData>();
			this.parent = null; this.parent_link = null;
			this.children = new ArrayList<AstContextNode>();
			this.in_edges = new ArrayList<AstContextEdge>();
			this.ou_edges = new ArrayList<AstContextEdge>();
		}
	}
	/**
	 * @param source
	 * @return it infers the type of the node w.r.t. AstNode class
	 * @throws Exception
	 */
	private	AstContextNodeType	derive_node_type(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstTranslationUnit) 	{ return AstContextNodeType.transition; }
		else if(source instanceof AstFunctionDefinition){ return AstContextNodeType.function; }
		else if(source instanceof AstDeclaration)		{ return AstContextNodeType.declaration; }
		else if(source instanceof AstName)	 			{ return AstContextNodeType.name; }
		else if(source instanceof AstField)				{ return AstContextNodeType.field; }
		else if(source instanceof AstKeyword)			{ return AstContextNodeType.keyword; }
		else if(source instanceof AstTypeName)			{ return AstContextNodeType.typename; }
		else if(source instanceof AstOperator)			{ return AstContextNodeType.operator; }
		// else if(source instanceof AstArgumentList)		{ return AstContextNodeType.argument_list; }
		else if(source instanceof AstDeclarationStatement || 
				source instanceof AstExpressionStatement) { return AstContextNodeType.base_statement; }
		else if(source instanceof AstCompoundStatement) { return AstContextNodeType.comp_statement; }
		else if(source instanceof AstGotoStatement || source instanceof AstContinueStatement
				|| source instanceof AstBreakStatement)	{ return AstContextNodeType.skip_statement; }
		else if(source instanceof AstReturnStatement)	{ return AstContextNodeType.retr_statement; }
		else if(source instanceof AstLabeledStatement ||
				source instanceof AstDefaultStatement)	{ return AstContextNodeType.labl_statement; }
		else if(source instanceof AstIfStatement)		{ return AstContextNodeType.ifte_statement; }
		else if(source instanceof AstForStatement || source instanceof AstWhileStatement ||
				source instanceof AstDoWhileStatement)	{ return AstContextNodeType.loop_statement; }
		else if(source instanceof AstSwitchStatement)	{ return AstContextNodeType.swit_statement; }
		else if(source instanceof AstCaseStatement)		{ return AstContextNodeType.case_statement; }
		else if(source instanceof AstIdExpression || source instanceof AstFieldExpression ||
				source instanceof AstArrayExpression)	{ return AstContextNodeType.refr_expression; }
		else if(source instanceof AstConstant || 
				source instanceof AstSizeofExpression) 	{ return AstContextNodeType.cons_expression; }
		else if(source instanceof AstLiteral)			{ return AstContextNodeType.strl_expression; }
		else if(source instanceof AstIncreUnaryExpression ||
				source instanceof AstIncrePostfixExpression)	{ return AstContextNodeType.incr_expression; } 
		else if(source instanceof AstCommaExpression)	{ return AstContextNodeType.seqs_expression; }
		else if(source instanceof AstInitializerBody)	{ return AstContextNodeType.init_expression; }
		else if(source instanceof AstConditionalExpression)	{ return AstContextNodeType.ifte_expression; }
		else if(source instanceof AstFunCallExpression)	{ return AstContextNodeType.call_expression; }
		else if(source instanceof AstArithAssignExpression || 
				source instanceof AstBitwiseAssignExpression || 
				source instanceof AstShiftAssignExpression || 
				source instanceof AstAssignExpression) 	{ return AstContextNodeType.assg_expression; }
		else if(source instanceof AstArithBinaryExpression || 
				source instanceof AstBitwiseBinaryExpression ||
				source instanceof AstShiftBinaryExpression || 
				source instanceof AstLogicBinaryExpression ||
				source instanceof AstRelationExpression)	{ return AstContextNodeType.bnry_expression; }
		else if(source instanceof AstPointUnaryExpression) {
			if(((AstPointUnaryExpression) source).get_operator().get_operator() == COperator.dereference) {
				return AstContextNodeType.refr_expression;
			}
			else {
				return AstContextNodeType.unry_expression;
			}
		}
		else if(source instanceof AstInitDeclarator)	{ return AstContextNodeType.assg_expression; }
		else if(source instanceof AstUnaryExpression)	{ return AstContextNodeType.unry_expression; }
		else if(source instanceof AstCastExpression)	{ return AstContextNodeType.cast_expression; }
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	
	/* getters */
	/**
	 * @return the tree where this node is created
	 */
	public	AstContextTree				get_tree()			{ return this.tree; }
	/**
	 * @return the integer ID of this node in the tree
	 */
	public	int							get_node_id()		{ return this.node_id; }
	/**
	 * @return the type of the node in AstContext tree
	 */
	public	AstContextNodeType			get_type()			{ return this.type; }
	/**
	 * @return abstract syntactic node of the node
	 */
	public	AstNode						get_ast_source()	{ return this.source; }
	/**
	 * @return the list of data items linked from this node
	 */
	public	Iterable<AstContextData>	get_states()		{ return this.state_list; }
	/**
	 * @return the number of states connected with this node
	 */
	public	int							number_of_states()	{ return this.state_list.size(); }
	/**
	 * @return whether this node is a root without parent
	 */
	public	boolean						is_root()			{ return this.parent == null; }
	/**
	 * @return the parent node of the node in tree
	 */
	public	AstContextNode				get_parent()		{ return this.parent; }
	/**
	 * @return the type of the link from parent to this node or null if it is the root
	 */ 
	public	AstContextNodeLink			get_pa_child_link()	{ return this.parent_link; }
	/**
	 * @return whether this node is a leaf without any child
	 */
	public	boolean						is_leaf()			{ return this.children.isEmpty(); }
	/**
	 * @return the children insert under this node
	 */
	public	Iterable<AstContextNode>	get_children()		{ return this.children; }
	/**
	 * @return the number of children created under this node
	 */
	public	int							number_of_children()	{ return this.children.size(); }
	/**
	 * @return the dependence-related edges from
	 */
	public	Iterable<AstContextEdge>	get_in_edges() 		{ return this.in_edges; }
	/**
	 * @return the dependence-related edges forward
	 */
	public	Iterable<AstContextEdge>	get_ou_edges() 		{ return this.ou_edges; }
	/**
	 * @return	the number of dependence edges linked to this node
	 */
	public	int							get_in_degree()		{ return this.in_edges.size(); }
	/**
	 * @return	the number of dependence edges linked from this node
	 */
	public	int							get_ou_degree()		{ return this.ou_edges.size(); }
	/**
	 * @param k
	 * @return the kth state connected with this node
	 * @throws IndexOutOfBoundsException
	 */
	public	AstContextData				get_state(int k) throws IndexOutOfBoundsException { return this.state_list.get(k); }
	/**
	 * @param k
	 * @return the kth child node inserted under this node
	 * @throws IndexOutOfBoundsException
	 */
	public	AstContextNode				get_child(int k) throws IndexOutOfBoundsException { return this.children.get(k); }
	/**
	 * @param k
	 * @return the kth edge to this node from other as dependence
	 * @throws IndexOutOfBoundsException
	 */
	public	AstContextEdge				get_in_edge(int k) throws IndexOutOfBoundsException { return this.in_edges.get(k); }
	/**
	 * @param k
	 * @return the kth edge from this node to other as dependence
	 * @throws IndexOutOfBoundsException
	 */
	public	AstContextEdge				get_ou_edge(int k) throws IndexOutOfBoundsException { return this.ou_edges.get(k); }
	
	/* setters */
	/**
	 * It inserts the child in the tail of this one using the given link-type
	 * @param link
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected	void			add_child(AstContextNodeLink link, AstContextNode child) throws IllegalArgumentException {
		if(link == null) {
			throw new IllegalArgumentException("Invalid link: null");
		}
		else if(child == null) {
			throw new IllegalArgumentException("Invalid child: null");
		}
		else if(child.get_parent() != null) {
			throw new IllegalArgumentException("Invalid: " + child);
		}
		else {
			this.children.add(child); child.parent = this;
			child.parent_link = link;
		}
	}
	/**
	 * @param type
	 * @param target
	 * @return it connects this node to the target using a dependence edge of specified type
	 * @throws IllegalArgumentException
	 */
	protected	AstContextEdge	add_edge(AstContextEdgeType type, AstContextNode target) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			AstContextEdge edge = new AstContextEdge(type, this, target);
			this.ou_edges.add(edge); target.in_edges.add(edge);
			return edge;
		}
	}
	/**
	 * It adds a new state w.r.t. the given store and value under this node
	 * @param type
	 * @param store
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected	AstContextData	add_state(AstContextDataType type, CirNode store, SymbolExpression value) throws IllegalArgumentException {
		AstContextData state = new AstContextData(this, type, store, value);
		this.state_list.add(state); return state;
	}
	
	// TODO implement retrieve methods
	
	
	
	
}
