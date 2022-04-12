package com.jcsa.jcmutest.mutant.sta2mutant.abst;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	It creates a node that connects AstNode with CirNode in a context-tree.
 * 	
 * 	@author yukimula
 *
 */
public class AstContextNode {
	
	/* attributes */
	/** the abstract syntactic context tree **/
	private	AstContextTree			tree;
	/** the integer ID of this node in tree **/
	private	int						node_id;
	/** the abstract syntactic node sources **/
	private	AstNode					source;
	/** the C-intermediate represent target **/
	private	CirNode					target;
	/** the class of this node w.r.t. AST **/
	private	AstContextType			type;
	/** the type of the edge from parent to this **/
	private	AstContextLink			edge;
	/** the parent node of node or null for root **/
	private AstContextNode			parent;
	/** the child nodes under this one or empty for leaf **/
	private	List<AstContextNode>	children;
	
	/* constructor */
	/**
	 * It creates an isolated node w.r.t. the AstNode as source
	 * @param tree
	 * @param node_id
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected	AstContextNode(AstContextTree tree, int node_id,
			AstNode source, CirNode target) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			this.tree = tree; this.node_id = node_id;
			this.source = source; this.target = target;
			this.type = this.derive_type(this.source);
			this.edge = null;
			this.parent = null;
			this.children = new ArrayList<AstContextNode>();
		}
	}
	/**
	 * It derives the type of AstNode as the source of this tree node
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	AstContextType derive_type(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstDeclarator || source instanceof AstName) {
			return AstContextType.declarator;
		}
		else if(source instanceof AstInitDeclarator) {
			return AstContextType.assignment;
		}
		else if(source instanceof AstInitializerBody 
				|| source instanceof AstInitializerList) {
			return AstContextType.init_body;
		}
		else if(source instanceof AstCompoundStatement || source instanceof AstStatementList) {
			return AstContextType.stmt_body;
		}
		else if(source instanceof AstKeyword) {
			return AstContextType.key_word;
		}
		else if(source instanceof AstOperator) {
			return AstContextType.operator;
		}
		else if(source instanceof AstArgumentList) {
			return AstContextType.args_list;
		}
		else if(source instanceof AstTypeName) {
			return AstContextType.type_name;
		}
		else if(source instanceof AstDeclarationStatement || source instanceof AstDeclaration) {
			return AstContextType.decl_stmt;
		}
		else if(source instanceof AstGotoStatement || 
				source instanceof AstBreakStatement || 
				source instanceof AstContinueStatement) {
			return AstContextType.skip_stmt;
		}
		else if(source instanceof AstReturnStatement) {
			return AstContextType.retr_stmt;
		}
		else if(source instanceof AstIfStatement || source instanceof AstCaseStatement) {
			return AstContextType.ifte_stmt;
		}
		else if(source instanceof AstDefaultStatement || source instanceof AstLabeledStatement) {
			return AstContextType.labl_stmt;
		}
		else if(source instanceof AstSwitchStatement) {
			return AstContextType.swit_stmt;
		}
		else if(source instanceof AstForStatement || 
				source instanceof AstWhileStatement || 
				source instanceof AstDoWhileStatement) {
			return AstContextType.loop_stmt;
		}
		else if(source instanceof AstAssignExpression 
				|| source instanceof AstArithAssignExpression 
				|| source instanceof AstBitwiseAssignExpression 
				|| source instanceof AstShiftAssignExpression) {
			return AstContextType.assignment;
		}
		else if(source instanceof AstIncreUnaryExpression || source instanceof AstIncrePostfixExpression) {
			return AstContextType.increment;
		}
		else if(source instanceof AstConstant || source instanceof AstSizeofExpression) {
			return AstContextType.constant;
		}
		else if(source instanceof AstLiteral) {
			return AstContextType.literal;
		}
		else if(source instanceof AstIdExpression || source instanceof AstArrayExpression || source instanceof AstFieldExpression) {
			return AstContextType.reference;
		}
		else if(source instanceof AstFunCallExpression) {
			return AstContextType.call_expr;
		}
		else if(source instanceof AstPointUnaryExpression) {
			if(((AstPointUnaryExpression) source).get_operator().get_operator() == COperator.dereference) {
				return AstContextType.reference;
			}
			else {
				return AstContextType.expression;
			}
		}
		else if(source instanceof AstParanthExpression) {
			return this.derive_type(((AstParanthExpression) source).get_sub_expression());
		}
		else if(source instanceof AstConstExpression) {
			return this.derive_type(((AstConstExpression) source).get_expression());
		}
		else if(source instanceof AstInitializer) {
			if(((AstInitializer) source).is_body()) {
				source = ((AstInitializer) source).get_body();
			}
			else {
				source = ((AstInitializer) source).get_expression();
			}
			return this.derive_type(source);
		}
		else if(source instanceof AstFieldInitializer) {
			source = ((AstFieldInitializer) source).get_initializer();
			return this.derive_type(source);
		}
		else if(source instanceof AstExpression) {
			return AstContextType.expression;
		}
		else if(source instanceof AstFunctionDefinition) {
			return AstContextType.function;
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	
	/* getters */
	/**
	 * @return the abstract syntactic context tree
	 */
	public 	AstContextTree 	get_tree() 			{ return this.tree; }
	/**
	 * @return the integer ID of this node in tree
	 */
	public	int				get_node_id()		{ return this.node_id; }
	/**
	 * @return the abstract syntactic node sources
	 */
	public	AstNode			get_ast_source()	{ return this.source; }
	/**
	 * @return the C-intermediate represent target
	 */
	public	CirNode			get_cir_target()	{ return this.target; }
	/**
	 * @return whether the node refers to any C-intermediate node
	 */
	public 	boolean			has_cir_target()	{ return this.target != null; } 
	/**
	 * @return the execution point of the ast-location refers to
	 */
	public	CirExecution	get_execution()		{ return (this.target == null) ? null : this.target.execution_of();	}
	/**
	 * @return the type of the node
	 */
	public	AstContextType	get_node_type()		{ return this.type; }
	/**
	 * @return the type of the input edge
	 */
	public	AstContextLink	get_edge_type()		{ return this.edge; }
	/**
	 * @return whether the node is a root
	 */
	public	boolean			is_root()			{ return this.parent == null; }
	/**
	 * @return the parent node of node or null for root
	 */
	public	AstContextNode	get_parent()		{ return this.parent; }
	/**
	 * @return the child nodes under this one or empty for leaf
	 */
	public	Iterable<AstContextNode> get_children() { return this.children; }
	/**
	 * @return whether the node is a leaf without any children
	 */
	public	boolean			is_leaf()			{ return this.children.isEmpty(); }
	/**
	 * @return the number of child nodes in the parent
	 */
	public 	int				number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child in this one
	 * @throws IndexOutOfBoundsException
	 */
	public	AstContextNode	get_child(int k) throws IndexOutOfBoundsException { return this.children.get(k); }
	
	/* setters */
	/**
	 * add a new child under this one
	 * @param edge
	 * @param child
	 * @throws Exception
	 */
	protected void add_child(AstContextLink edge, AstContextNode child) throws Exception {
		if(edge == null) {
			throw new IllegalArgumentException("Invalid edge: null");
		}
		else if(child == null || child.is_root()) {
			throw new IllegalArgumentException("Invalid: " + child);
		}
		else { child.parent = this; this.children.add(child); child.edge = edge; }
	}
	
}
