package com.jcsa.jcmutest.mutant.cir2mutant.stat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.stat.anot.CirAttributeState;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;

/**
 * It represents a node in CirMutationTree, referring to a unique CirAttribute
 * along with a set of its CirAnnotation as features.
 * 
 * @author yukimula
 *
 */
public class CirMutationTreeNode {
	
	/* definitions */
	private CirMutationTree				tree;
	private CirMutationTreeType			type;
	private CirAttributeState			data;
	private CirMutationTreeEdge 		in_edge;
	private List<CirMutationTreeEdge> 	ou_edges;
	
	/* constructors */
	/**
	 * It creates a root node in CirMutationTree for reaching the program entry
	 * @param tree
	 * @param entry
	 * @throws Exception
	 */
	private CirMutationTreeNode(CirMutationTree tree, CirExecution entry) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(entry == null) {
			throw new IllegalArgumentException("Invalid entry: null");
		}
		else {
			this.tree = tree;
			this.type = CirMutationTreeType.pre_condition;
			this.data = new CirAttributeState(CirAttribute.new_cover_count(entry, 1));
			this.in_edge = null;
			this.ou_edges = new ArrayList<CirMutationTreeEdge>();
		}
	}
	/**
	 * It creates a child node under the parent using the given edge of type and specified node type
	 * @param parent
	 * @param node_type
	 * @param node_attribute
	 * @param edge_flow
	 * @throws Exception
	 */
	private CirMutationTreeNode(CirMutationTreeNode parent, CirMutationTreeType node_type,
			CirAttribute node_attribute, CirMutationTreeFlow edge_type) throws Exception {
		if(parent == null) {
			throw new IllegalArgumentException("Invalid parent as null");
		}
		else if(node_type == null) {
			throw new IllegalArgumentException("Invalid node_type: null");
		}
		else if(node_attribute == null) {
			throw new IllegalArgumentException("Invalid node_attribute: null");
		}
		else if(edge_type == null) {
			throw new IllegalArgumentException("Invalid edge_type: null");
		}
		else {
			this.tree = parent.tree;
			this.type = node_type;
			this.data = new CirAttributeState(node_attribute);
			this.in_edge = new CirMutationTreeEdge(edge_type, parent, this);
			this.ou_edges = new ArrayList<CirMutationTreeEdge>();
		}
	}
	/**
	 * It creates a root node with attribute to reach the program entry.
	 * @param tree
	 * @return 
	 * @throws Exception
	 */
	protected static CirMutationTreeNode new_root(CirMutationTree tree) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else {
			CirTree cir_tree = tree.get_mutant().get_space().get_cir_tree();
			CirFunction main_function = cir_tree.get_function_call_graph().get_main_function();
			
			if(main_function == null) {
				AstNode ast_location = tree.get_mutant().get_mutation().get_location();
				while(ast_location != null) {
					if(ast_location instanceof AstFunctionDefinition) {
						Iterable<CirNode> cir_defs = cir_tree.get_localizer().
								get_cir_nodes(ast_location, CirFunctionDefinition.class);
						CirNode cir_def = cir_defs.iterator().next();
						for(CirFunction function : cir_tree.get_function_call_graph().get_functions()) {
							if(function.get_definition() == cir_def) {
								main_function = function;
								break;
							}
						}
						break;
					}
					else {
						ast_location = ast_location.get_parent();
					}
				}
			}
			
			if(main_function == null) {
				throw new IllegalArgumentException("Cannot found function entry");
			}
			else {
				CirExecution entry = main_function.get_flow_graph().get_entry();
				return new CirMutationTreeNode(tree, entry);
			}
		}
	}
	/**
	 * @param edge_type			type of edge that links this node to the target child
	 * @param child_type		type of the child node created from this parent node
	 * @param child_attribute	the attribute hold within the created child target in
	 * @return It creates a child node from this one using the specified edge of type and the 
	 * 		   specified type of node, in which an existing child will be returned if the given
	 * 		   child_attribute equals with any of the child's attribute anyway.
	 * @throws Exception
	 */
	protected CirMutationTreeNode new_child(CirMutationTreeFlow edge_type,
			CirMutationTreeType child_type, CirAttribute child_attribute) throws Exception {
		if(edge_type == null) {
			throw new IllegalArgumentException("Invalid edge_type: null");
		}
		else if(child_type == null) {
			throw new IllegalArgumentException("Invalid child_type: null");
		}
		else if(child_attribute == null) {
			throw new IllegalArgumentException("Invalid child_attribute: null");
		}
		else {
			for(CirMutationTreeEdge ou_edge : this.ou_edges) {
				if(ou_edge.get_target().data.get_attribute().equals(child_attribute)) {
					return ou_edge.get_target();
				}
			}
			
			CirMutationTreeNode child = new CirMutationTreeNode(
					this, child_type, child_attribute, edge_type);
			this.ou_edges.add(child.in_edge); return child;
		}
	}
	
	/* getters */
	/**
	 * @return the tree where this node is created
	 */
	public CirMutationTree	get_tree() { return this.tree; }
	/**
	 * @return the type of this node in the step of killing process
	 */
	public CirMutationTreeType get_node_type() { return this.type; }
	/**
	 * @return the data unit of CirAttribute and its annotations in the node
	 */
	public CirAttributeState get_data() { return this.data; }
	/**
	 * @return the attribute that is represented by this tree node
	 */
	public CirAttribute get_attribute() { return this.data.get_attribute(); }
	/**
	 * @return the execution where the attribute of this node is defined
	 */
	public CirExecution get_execution() { return this.data.get_attribute().get_execution(); }
	
	/* structure */
	/**
	 * @return whether the node is a root without any parent
	 */
	public boolean is_root() { return this.in_edge == null; }
	/**
	 * @return the edge that links its parent to this node or null if it is root
	 */
	public CirMutationTreeEdge get_in_edge() { return this.in_edge; }
	/**
	 * @return the parent that links to this node or null if it is root
	 */
	public CirMutationTreeNode get_parent() {
		if(this.in_edge == null) {
			return null;
		}
		else {
			return this.in_edge.get_source();
		}
	}
	/**
	 * @return whether the node is a leaf without any child
	 */
	public boolean is_leaf() { return this.ou_edges.isEmpty(); }
	/**
	 * @return the set of edges linking from this node to its children
	 */
	public Iterable<CirMutationTreeEdge> get_ou_edges() { return this.ou_edges; }
	/**
	 * @param k
	 * @return the kth edge linking from this node to one of its children
	 * @throws IndexOutOfBoundsException
	 */
	public CirMutationTreeEdge get_ou_edge(int k) throws IndexOutOfBoundsException {
		return this.ou_edges.get(k);
	}
	/**
	 * @return the number of children created under this node
	 */
	public int number_of_children() { return this.ou_edges.size(); }
	/**
	 * @param k
	 * @return the kth child created under this node in the tree
	 * @throws IndexOutOfBoundsException
	 */
	public CirMutationTreeNode get_child(int k) throws IndexOutOfBoundsException {
		return this.ou_edges.get(k).get_target();
	}
	
	/* iterator */
	/**
	 * The iterator to traverse the nodes under a given root (including the root)
	 * @author yukimula
	 *
	 */
	private static class CirMutationTreeNodeIterator implements Iterator<CirMutationTreeNode> {
		
		private Queue<CirMutationTreeNode> queue;
		
		private CirMutationTreeNodeIterator(CirMutationTreeNode root) {
			this.queue = new LinkedList<CirMutationTreeNode>();
			this.queue.add(root);
		}

		@Override
		public boolean hasNext() {
			return !this.queue.isEmpty();
		}

		@Override
		public CirMutationTreeNode next() {
			CirMutationTreeNode node = this.queue.poll();
			for(CirMutationTreeEdge ou_edge : node.get_ou_edges()) {
				this.queue.add(ou_edge.get_target());
			}
			return node;
		}
		
	}
	/**
	 * @return the list of nodes from root until this node
	 */
	public List<CirMutationTreeNode> get_pred_nodes() {
		List<CirMutationTreeNode> pred_nodes = new ArrayList<CirMutationTreeNode>();
		
		CirMutationTreeNode node = this;
		while(node != null) {
			pred_nodes.add(node);
			node = node.get_parent();
		}
		
		for(int k = 0; k < pred_nodes.size() / 2; k++) {
			int i = k, j = pred_nodes.size() - 1 - k;
			CirMutationTreeNode ni = pred_nodes.get(i);
			CirMutationTreeNode nj = pred_nodes.get(j);
			pred_nodes.set(i, nj); pred_nodes.set(j, ni);
		}
		
		return pred_nodes;
	}
	/**
	 * @return the set of nodes under this one (including itself)
	 */
	public Iterator<CirMutationTreeNode> get_post_nodes() {
		return new CirMutationTreeNodeIterator(this);
	}
	
}
