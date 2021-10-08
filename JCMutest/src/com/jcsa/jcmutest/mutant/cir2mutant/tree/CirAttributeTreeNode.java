package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;

/**
 * It denotes a node in CirAttributeTree in error-creation-propagation process.
 * 
 * @author yukimula
 *
 */
public class CirAttributeTreeNode {
	
	/* definitions */
	/** the tree where this node is created **/
	private CirAttributeTree tree;
	/** the state of the CirAttribute of the node **/
	private CirAttributeState state;
	/** the edge from its parent to this node or null if it is a root **/
	private CirAttributeTreeEdge in_edge;
	/** the set of edges pointing from this node to its child in tree **/
	private List<CirAttributeTreeEdge> ou_edges;
	
	/* constructors */
	/**
	 * It creates a root node in the tree using the given attribute
	 * @param tree
	 * @param attribute
	 * @throws Exception
	 */
	private CirAttributeTreeNode(CirAttributeTree tree, CirAttribute attribute) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			this.tree = tree;
			this.state = new CirAttributeState(attribute);
			this.in_edge = null;
			this.ou_edges = new ArrayList<CirAttributeTreeEdge>();
		}
	}
	/**
	 * It creates a child node under the parent using an edge of specified type and given attribute of the child
	 * @param parent
	 * @param type
	 * @param attribute
	 * @throws Exception
	 */
	private CirAttributeTreeNode(CirAttributeTreeNode parent, CirAttributeTreeType type, CirAttribute attribute) throws Exception {
		if(parent == null) {
			throw new IllegalArgumentException("Invalid parent: null");
		}
		else if(type == null) {
			throw new IllegalArgumentException("Invalid type as null");
		}
		else if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			this.tree = parent.tree;
			this.state = new CirAttributeState(attribute);
			this.in_edge = new CirAttributeTreeEdge(type, parent, this);
			this.ou_edges = new ArrayList<CirAttributeTreeEdge>();
		}
	}
	/**
	 * It creates a root node in the tree when given the mutant as input
	 * @param tree
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	protected static CirAttributeTreeNode new_root(CirAttributeTree tree, Mutant mutant) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			CirTree cir_tree = mutant.get_space().get_cir_tree();
			CirFunction main_function = cir_tree.get_function_call_graph().get_main_function();
			
			if(main_function == null) {
				AstNode ast_location = mutant.get_mutation().get_location();
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
				return new CirAttributeTreeNode(tree, CirAttribute.new_cover_count(entry, 1));
			}
		}
	}
	/**
	 * It creates a child under the node using given attribute and edge of specified type
	 * @param type
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	protected CirAttributeTreeNode new_child(CirAttributeTreeType type, CirAttribute attribute) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type as null");
		}
		else if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			for(CirAttributeTreeEdge ou_edge : this.ou_edges) {
				CirAttributeTreeNode child = ou_edge.get_target();
				if(child.state.get_attribute().equals(attribute)) {
					return child;
				}
			}
			
			CirAttributeTreeNode child = new CirAttributeTreeNode(this, type, attribute);
			this.ou_edges.add(child.in_edge);
			return child;
		}
	}
	
	/* getters */
	/**
	 * @return the tree where this node is created
	 */
	public CirAttributeTree get_tree() { return this.tree; }
	/**
	 * @return the attribute that the node specifies
	 */
	public CirAttribute get_attribute() { return this.state.get_attribute(); }
	/**
	 * @return the evaluation state of the attribute that the node specifies
	 */
	public CirAttributeState get_state() { return this.state; }
	/**
	 * @return the edge from its parent to this node or null if it is a root
	 */
	public CirAttributeTreeEdge get_in_edge() { return this.in_edge; }	
	/**
	 * @return the set of edges pointing from this node to its child in tree
	 */
	public Iterable<CirAttributeTreeEdge> get_ou_edges() { return this.ou_edges; }
	/**
	 * @return the number of output edges from this node to its children
	 */
	public int number_of_ou_edges() { return this.ou_edges.size(); }
	/**
	 * @param k
	 * @return the kth edge from this node to its kth child 
	 * @throws IndexOutOfBoundsException
	 */
	public CirAttributeTreeEdge get_ou_edge(int k) throws IndexOutOfBoundsException {
		return this.ou_edges.get(k);
	}
	
	/* inferred */
	/**
	 * @return whether the node is a root without any parent
	 */
	public boolean is_root() { return this.in_edge == null; }
	/**
	 * @return whether the node is a leaf without any children
	 */
	public boolean is_leaf() { return this.ou_edges.isEmpty(); }
	/**
	 * @return the parent of this node or null when it is root
	 */
	public CirAttributeTreeNode get_parent() {
		if(this.in_edge == null) {
			return null;
		}
		else {
			return this.in_edge.get_source();
		}
	}
	/**
	 * @return the number of children created under this node
	 */
	public int number_of_children() { return this.ou_edges.size(); }
	/**
	 * @param k
	 * @return the kth child created under this node
	 * @throws IndexOutOfBoundsException
	 */
	public CirAttributeTreeNode get_child(int k) throws IndexOutOfBoundsException {
		return this.ou_edges.get(k).get_target();
	}
	/**
	 * @return the list of nodes from root until this node
	 */
	public List<CirAttributeTreeNode> get_pred_nodes() {
		List<CirAttributeTreeNode> pred_nodes = new ArrayList<CirAttributeTreeNode>();
		CirAttributeTreeNode node = this;
		while(node != null) {
			pred_nodes.add(node);
			node = node.get_parent();
		}
		
		for(int k = 0; k < pred_nodes.size() / 2; k++) {
			int i = k;
			int j = pred_nodes.size() - 1 - k;
			CirAttributeTreeNode ni = pred_nodes.get(i);
			CirAttributeTreeNode nj = pred_nodes.get(j);
			pred_nodes.set(i, nj);
			pred_nodes.set(j, ni);
		}
		
		return pred_nodes;
	}
	/**
	 * @return the set of nodes under this parent (included)
	 */
	public Collection<CirAttributeTreeNode> get_post_nodes() {
		Queue<CirAttributeTreeNode> queue = new LinkedList<CirAttributeTreeNode>();
		Set<CirAttributeTreeNode> records = new HashSet<CirAttributeTreeNode>();
		queue.add(this);
		while(!queue.isEmpty()) {
			CirAttributeTreeNode node = queue.poll();
			records.add(node);
			for(CirAttributeTreeEdge edge : node.ou_edges) {
				queue.add(edge.get_target());
			}
		}
		return records;
	}
	
}
