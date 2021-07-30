package com.jcsa.jcparse.lang.irlang.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.parse.code.CodeGeneration;

/**
 * The abstract implementation of node in C-like intermediate representation.
 * 
 * @author yukimula
 *
 */
public abstract class CirNodeImpl implements CirNode {
	
	/* properties */
	private AstNode ast_source;
	private CirTree tree;
	private int node_id;
	private CirNode parent;
	private int child_index;
	private List<CirNode> children;
	
	/* constructor and initializer */
	/**
	 * create an instance of CirNode with node-id in the tree structure
	 * @param tree
	 * @param node_id
	 * @param linked : true if the children is in the linked list (as fixed number)
	 * @throws IllegalArgumentException
	 */
	protected CirNodeImpl(CirTree tree, int node_id, boolean linked) throws IllegalArgumentException {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else if(node_id < 0)
			throw new IllegalArgumentException("Invalid: " + node_id);
		else {
			this.tree = tree;
			this.node_id = node_id;
			this.ast_source = null;
			this.parent = null;
			this.child_index = -1;
			if(linked) {
				this.children = new LinkedList<CirNode>();
			}
			else {
				this.children = new ArrayList<CirNode>();
			}
		}
	}
	
	/* local properties */
	@Override
	public AstNode get_ast_source() { return this.ast_source; }
	@Override
	public void set_ast_source(AstNode source) throws IllegalArgumentException { 
		if(this.ast_source != null)
			throw new IllegalArgumentException("duplicated assignment.");
		else if(source == null) return;	/* ignore the setting if null provided */
		else {
			this.ast_source = source;
			((CirTreeImpl) this.tree).link_ast_with_cir(this);
		}
	}
	@Override
	public CirTree get_tree() { return this.tree; }
	@Override
	public int get_node_id() { return this.node_id; }
	
	/* parent-child relation */
	@Override
	public CirNode get_parent() { return this.parent; }
	@Override
	public int get_child_index() { return this.child_index; }
	@Override
	public Iterable<CirNode> get_children() { return children; }
	@Override
	public int number_of_children() { return this.children.size(); }
	@Override
	public CirNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	
	@Override
	public String generate_code(boolean simplified) throws Exception {
		return CodeGeneration.generate_code(simplified, this);
	}
	
	/* children setter */
	/**
	 * add a new child (without being linked to any parent) under this node
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void add_child(CirNodeImpl child) throws IllegalArgumentException {
		if(child == null)
			throw new IllegalArgumentException("invalid child: null");
		else if(child.parent != null)
			throw new IllegalArgumentException("duplicated parent");
		else { 
			child.parent = this;
			child.child_index = this.children.size();
			this.children.add(child);
		}
	}
	
	@Override
	public CirFunctionDefinition function_of() {
		CirNode node = this;
		while(node != null) {
			if(node instanceof CirFunctionDefinition)
				break;
			else node = node.get_parent();
		}
		return (CirFunctionDefinition) node;
	}
	
	@Override
	public CirExecution execution_of() {
		CirNode node = this;
		while(node != null) {
			if(node instanceof CirStatement) {
				try {
					return this.tree.get_localizer().get_execution((CirStatement) this);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			else {
				node = node.get_parent();
			}
		}
		return null;
	}
	
}
