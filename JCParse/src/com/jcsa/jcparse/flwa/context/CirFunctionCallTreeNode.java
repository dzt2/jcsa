package com.jcsa.jcparse.flwa.context;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCall;

/**
 * Each node in function calling tree refers to a function in C-like intermediate
 * representation in a specific calling path.
 * @author yukimula
 *
 */
public class CirFunctionCallTreeNode {
	
	/* properties */
	/** the tree where the node is created **/
	private CirFunctionCallTree tree;
	/** the parent under which this node created **/
	private CirFunctionCallTreeNode parent;
	/** the set of children which this node creates **/
	private List<CirFunctionCallTreeNode> children;
	/** the context under which the function of node called **/
	private CirFunctionCall context;
	/** the function that this tree node represents **/
	private CirFunction function;
	
	/* constructors */
	/**
	 * create the root node in the tree with respect to the given function
	 * @param tree
	 * @param function
	 * @throws Exception
	 */
	protected CirFunctionCallTreeNode(CirFunctionCallTree tree, CirFunction function) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("invalid tree as null");
		else if(function == null)
			throw new IllegalArgumentException("invalid function: null");
		else {
			this.tree = tree;
			this.context = null;
			this.function = function;
			this.parent = null;
			this.children = new LinkedList<CirFunctionCallTreeNode>();
		}
	}
	/**
	 * create a tree node representing the function being called by the specified parent.
	 * @param parent
	 * @param call
	 * @throws Exception
	 */
	private CirFunctionCallTreeNode(CirFunctionCallTreeNode parent, CirFunctionCall call) throws Exception {
		if(parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else if(call == null)
			throw new IllegalArgumentException("Invalid call: null");
		else if(call.get_caller() != parent.function)
			throw new IllegalArgumentException("Unable to match: " + call.get_callee());
		else {
			this.tree = parent.tree;
			this.parent = parent;
			this.context = call;
			this.function = call.get_callee();
			this.children = new LinkedList<CirFunctionCallTreeNode>();
		}
	}
	
	/* getters */
	/**
	 * get the tree where the node is created
	 * @return
	 */
	public CirFunctionCallTree get_tree() { return this.tree; }
	/**
	 * get the parent which calls this node in tree
	 * @return
	 */
	public CirFunctionCallTreeNode get_parent() { return parent; }
	/**
	 * get the function this tree node represents
	 * @return
	 */
	public CirFunction get_function() { return function; }
	/**
	 * get the context in which the tree node is called
	 * @return null for any root node in the tree
	 */
	public CirFunctionCall get_context() { return context; }
	/**
	 * get the children called under the tree node
	 * @return
	 */
	public Iterable<CirFunctionCallTreeNode> get_children() { return children; }
	/**
	 * whether the tree node is root in the tree
	 * @return
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * whether the node is a leaf in the tree
	 * @return
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/**
	 * get the number of all the nodes under the root of the current node in tree
	 * @return
	 */
	public int size() {
		int size = 0;
		for(CirFunctionCallTreeNode child : this.children)
			size = size + child.size();
		return size + 1;
	}
	@Override
	public String toString() {
		Stack<CirFunctionCall> calls = new Stack<CirFunctionCall>();
		CirFunctionCallTreeNode node = this;
		
		while(node != null) {
			if(node.context != null) {
				calls.push(node.context);
			}
			node = node.parent;
		}
		
		StringBuilder buffer = new StringBuilder();
		while(!calls.isEmpty()) {
			CirFunctionCall call = calls.pop();
			CirExecution call_stmt = call.get_call_execution();
			buffer.append(call_stmt.toString()).append("::");
		}
		buffer.append(this.function.get_name());
		
		return buffer.toString();
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	
	/* setters */
	/**
	 * create a new child that this tree node calls or return the existing one
	 * if the calling related with some child node.
	 * @param call
	 * @return
	 * @throws Exception
	 */
	protected CirFunctionCallTreeNode new_child(CirFunctionCall call) throws Exception {
		for(CirFunctionCallTreeNode child : this.children) {
			if(child.context == call) return child;
		}
		CirFunctionCallTreeNode child = new CirFunctionCallTreeNode(this, call);
		this.children.add(child); return child;
	}
	
}
