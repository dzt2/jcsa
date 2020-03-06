package com.jcsa.jcparse.lopt.context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCall;

/**
 * The tree describes the function calling expansion based on one unique root-function node.
 * 
 * @author yukimula
 *
 */
public class CirFunctionCallTree {
	
	/* parameters */
	/** the value set to maximal_depth in building algorithm to specify that
	 *  there is no constrain on the length of any paths in the calling tree **/
	public static final int UNLIMITED_DEPTH = -1;
	/** the maximal number of nodes allowed in the function calling tree **/
	private static final int MAXIMAL_TREE_SIZE = Short.MAX_VALUE;
	
	/* definitions */
	/** the root node in this tree **/
	private CirFunctionCallTreeNode root;
	/** used to traverse the nodes in calling tree with respect to a BFS algorithm **/
	private List<CirFunctionCallTreeNode> nodes;
	/**
	 * create a function calling tree with respect to the function with respect to the root node
	 * @param root_function
	 * @throws Exception
	 */
	protected CirFunctionCallTree(CirFunction root_function) throws Exception {
		this.root = new CirFunctionCallTreeNode(this, root_function);
		this.nodes = new ArrayList<CirFunctionCallTreeNode>();
	}
	
	/* getters */
	/**
	 * get the number of nodes created in this tree
	 * @return
	 */
	public int size() { return this.root.size(); }
	/**
	 * get the root node in the tree that directly or indirectly call all
	 * the other nodes in the calling tree
	 * @return
	 */
	public CirFunctionCallTreeNode get_root() { return root; }
	/**
	 * get the set of nodes created in the tree in BFS order
	 * @return
	 */
	public Iterable<CirFunctionCallTreeNode> get_nodes() { return nodes; }
	
	/* setters */
	/**
	 * update the nodes in the tree for BFS algorithm
	 */
	private void update_nodes() {
		Queue<CirFunctionCallTreeNode> queue = 
				new LinkedList<CirFunctionCallTreeNode>();
		queue.add(this.root); this.nodes.clear();
		while(!queue.isEmpty()) {
			CirFunctionCallTreeNode node = queue.poll();
			this.nodes.add(node);
			for(CirFunctionCallTreeNode child : node.get_children()) {
				queue.add(child);
			}
		}
	}
	
	/* creator methods */
	/**
	 * create a child under the given parent with respect to the given calling relation
	 * @param parent
	 * @param call_context
	 * @param type
	 * @param maximal_depth
	 * @return null if the new path from parent to child breaks any of its constrain
	 * @throws Exception
	 */
	private static CirFunctionCallTreeNode create_child(CirFunctionCallTreeNode parent,
			CirFunctionCall call_context, CirFunctionCallPathType type, int maximal_depth) throws Exception {
		/* A. declarations and initialization */
		int depth = 0; CirFunctionCallTreeNode save_parent = parent;
		
		/* B. create the child if it does not contain duplicated nodes in path */
		if(type == CirFunctionCallPathType.unique_path) {
			/* validate the constraint on the new path from parent to child */
			while(parent != null) {
				if(maximal_depth >= 0 && depth >= maximal_depth) {
					return null;	// when the path size is out of range
				}
				else if(parent.get_function() == call_context.get_callee()) {
					return null;	// when path contains duplicated node
				}
				else {
					parent = parent.get_parent(); depth++;
				}
			}
			
			return save_parent.new_child(call_context);	// it's valid path
		}
		
		/* C. create the child if it does not contain duplicated edges in path */
		else if(type == CirFunctionCallPathType.simple_path) {
			/* validate the constraint on the new path from parent to child */
			while(parent != null) {
				if(maximal_depth >= 0 && depth >= maximal_depth) {
					return null;	// when the path size is out of range
				}
				else if(parent.get_context() == call_context) {
					return null;	// when path contains duplicated edge
				}
				else {
					parent = parent.get_parent(); depth++;
				}
			}
			
			return save_parent.new_child(call_context);	// it's valid path
		}
		
		/* D. create the child if its lenght is not going to exceed the maximal depth */
		else {
			/* validate the constraint on the new path from parent to child */
			while(parent != null) {
				if(maximal_depth >= 0 && depth >= maximal_depth) {
					return null;	// when the path size is out of range
				}
				else {
					parent = parent.get_parent(); depth++;
				}
			}
			
			return save_parent.new_child(call_context);	// it's valid path
		}
		
	}
	/**
	 * create the children of the parent if the path is valid, and throw out-of-memory
	 * errors when the number of created nodes are out of size.
	 * @param parent
	 * @param queue which will be updated during the expansion of the function calling tree
	 * @param counter
	 * @param type
	 * @param maximal_depth
	 * @return the number of created nodes after creating the children of the nodes
	 * @throws Exception
	 */
	private static int create_children(Queue<CirFunctionCallTreeNode> queue, int counter,
			CirFunctionCallPathType type, int maximal_depth) throws Exception {
		CirFunctionCallTreeNode parent = queue.poll(); 
		Iterable<CirFunctionCall> call_contexts = parent.get_function().get_ou_calls();
		
		for(CirFunctionCall call_context : call_contexts) {
			CirFunctionCallTreeNode child = create_child(
					parent, call_context, type, maximal_depth);
			if(child != null) { counter++; queue.add(child); }
		}
		
		return counter;
	}
	/**
	 * create the function calling tree with respect to the given parameters
	 * @param tree
	 * @param type specifies the constraints over the path from root to leaf
	 * @param maximal_depth maximal length of the path from root to the leaf
	 * @return the number of created nodes within the tree under being built
	 * @throws Exception
	 */
	private static int create_tree(CirFunctionCallTree tree, 
			CirFunctionCallPathType type, int maximal_depth) throws Exception {
		Queue<CirFunctionCallTreeNode> queue = 
				new LinkedList<CirFunctionCallTreeNode>();
		int counter = 0; queue.add(tree.get_root());
		
		while(!queue.isEmpty()) {
			counter = create_children(queue, counter, type, maximal_depth);
			if(counter > MAXIMAL_TREE_SIZE) {
				throw new Exception(new OutOfMemoryError(counter + " is out of range"));
			}
		}
		
		return counter;
	}
	/**
	 * create a function calling tree with respect to the parameters as given
	 * @param root_function
	 * @param type
	 * @param maximal_depth
	 * @return
	 * @throws Exception
	 */
	protected static CirFunctionCallTree tree(CirFunction root_function, 
			CirFunctionCallPathType type, int maximal_depth) throws Exception {
		if(root_function == null)
			throw new IllegalArgumentException("invalid root_function");
		else if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else {
			CirFunctionCallTree tree = new CirFunctionCallTree(root_function);
			create_tree(tree, type, maximal_depth); tree.update_nodes(); return tree;
		}
	}
	
}
