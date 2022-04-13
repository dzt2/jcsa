package com.jcsa.jcmutest.mutant.sta2mutant.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.irlang.CirTree;

/**
 * 	It defines the tree of program context in terms of abstract syntactic tree.	<br>
 * 	
 * 	@author yukimula
 *
 */
public class AstContextTree {
	
	/* definitions */
	private	AstTree							ast_tree;
	private	CirTree							cir_tree;
	private	AstContextNode					root;
	private	List<AstContextNode>			nodes;
	private	Map<AstNode, AstContextNode>	index;
	public	AstContextTree(AstTree ast_tree, CirTree cir_tree) throws Exception {
		if(ast_tree == null) {
			throw new IllegalArgumentException("Invalid ast_tree: null");
		}
		else if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else {
			this.cir_tree = cir_tree;
			this.ast_tree = ast_tree;
			this.nodes = new ArrayList<AstContextNode>();
			this.index = new HashMap<AstNode, AstContextNode>();
			this.root = AstContextParser.parser.parse(this);
			for(AstContextNode tree_node : this.nodes) {
				AstCirConnection.connector.connect(tree_node);
			}
		}
	}
	
	/* getters */
	/**
	 * @return the abstract syntactic tree as the data source
	 */
	public	AstTree						get_ast_tree()	{ return this.ast_tree; }
	/**
	 * @return the C-intermediate representations data source
	 */
	public	CirTree						get_cir_tree()	{ return this.cir_tree; }
	/**
	 * @return the number of nodes created under this tree
	 */
	public	int							number_of_nodes()	{ return this.nodes.size(); }
	/**
	 * @return the root node of this tree
	 */
	public 	AstContextNode				get_root()	{ return this.root; }
	/**
	 * @return the list of nodes created under this tree
	 */
	public	Iterable<AstContextNode>	get_nodes()	{ return this.nodes; }
	/**
	 * @param k
	 * @return the node w.r.t. the given id
	 * @throws IndexOutOfBoundsException
	 */
	public	AstContextNode				get_node(int k) throws IndexOutOfBoundsException { return this.nodes.get(k); }
	/**
	 * @param source
	 * @return it localizes the real source node of AstNode
	 */
	private	AstNode						loc_source(AstNode source) {
		while(source != null) {
			if(source instanceof AstParanthExpression) {
				source = ((AstParanthExpression) source).get_sub_expression();
			}
			else if(source instanceof AstConstExpression) {
				source = ((AstConstExpression) source).get_expression();
			}
			else if(source instanceof AstArgumentList) {
				source = source.get_parent();
			}
			else if(source instanceof AstDeclarator) {
				if(((AstDeclarator) source).get_production() == DeclaratorProduction.identifier) {
					source = ((AstDeclarator) source).get_identifier();
				}
				else {
					source = ((AstDeclarator) source).get_declarator();
				}
			}
			else if(source instanceof AstInitializer) {
				if(((AstInitializer) source).is_body()) {
					source = ((AstInitializer) source).get_body();
				}
				else {
					source = ((AstInitializer) source).get_expression();
				}
			}
			else if(source instanceof AstFieldInitializer) {
				source = ((AstFieldInitializer) source).get_initializer();
			}
			else if(source instanceof AstStatementList || source instanceof AstInitializerList) {
				source = source.get_parent();
			}
			else { break; }
		}
		return source;
	}
	/**
	 * @param source 
	 * @return whether there exist node w.r.t. the source or its representative node
	 */
	public	boolean						has_node(AstNode source) { return this.index.containsKey(this.loc_source(source)); }
	/**
	 * @param source
	 * @return the node w.r.t. the source or 
	 * @throws IllegalArgumentException
	 */
	public	AstContextNode				get_node(AstNode source) throws IllegalArgumentException {
		source = this.loc_source(source);
		if(source == null || !this.index.containsKey(source)) {
			throw new IllegalArgumentException("Undefined: " + source);
		}
		else {
			return this.index.get(source);
		}
	}
	
	/* setters */
	/**
	 * It creates a new node w.r.t. the source in this tree
	 * @param source
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected AstContextNode			new_node(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(!this.index.containsKey(source)){
			AstContextNode node = new AstContextNode(this, this.nodes.size(), source);
			this.nodes.add(node); this.index.put(source, node); return node;
		}
		else {
			throw new IllegalArgumentException("Defined: " + source);
		}
	}
	
	
	
	
}
