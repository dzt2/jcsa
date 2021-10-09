package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
 * It denotes a node w.r.t. a CirAttribute defined in CirAttributeTree for 
 * representing an execution point during killing a mutation.
 * 
 * @author yukimula
 *
 */
public class CirAttributeNode {
	
	/* definitions */
	private CirAttributeTree		tree;
	private CirAttributeData		data;
	private CirAttributeNode		parent;
	private List<CirAttributeNode>	children;
	private CirAttributeNode(CirAttributeTree tree, CirAttribute attribute) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			this.tree = tree;
			this.data = new CirAttributeData(attribute);
			this.parent = null;
			this.children = new ArrayList<CirAttributeNode>();
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
	public CirAttribute get_attribute() { return this.data.get_attribute(); }
	/**
	 * @return the evaluation state of the attribute that the node specifies
	 */
	public CirAttributeData get_data() { return this.data; }
	/**
	 * @return the parent of this node or null if the node is a root
	 */
	public CirAttributeNode get_parent() { return this.parent; }
	/**
	 * @return the child nodes created under this node
	 */
	public Iterable<CirAttributeNode> get_children() { return this.children; }
	
	/* inferred */
	/**
	 * @return whether the node is a root without any parent
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return whether the node is a leaf without any children
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/**
	 * @return the number of children created under this node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child created under this node
	 * @throws IndexOutOfBoundsException
	 */
	public CirAttributeNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	/**
	 * @return the sequence of nodes from tree root until this one
	 */
	public List<CirAttributeNode> get_pred_nodes() {
		List<CirAttributeNode> path = new ArrayList<CirAttributeNode>();
		CirAttributeNode node = this;
		while(node != null) {
			path.add(node);
			node = node.get_parent();
		}
		
		for(int k = 0; k < path.size() / 2; k++) {
			int i = k, j = path.size() - 1 - k;
			CirAttributeNode ni = path.get(i);
			CirAttributeNode nj = path.get(j);
			path.set(i, nj); path.set(j, ni);
		}
		return path;
	}
	/**
	 * @return the set of nodes created under this one as root of the subtree
	 */
	public Collection<CirAttributeNode> get_post_nodes() {
		Queue<CirAttributeNode> queue = new LinkedList<CirAttributeNode>();
		HashSet<CirAttributeNode> nodes = new HashSet<CirAttributeNode>();
		
		queue.add(this);
		while(!queue.isEmpty()) {
			CirAttributeNode node = queue.poll();
			nodes.add(node);
			for(CirAttributeNode child : node.get_children()) {
				queue.add(child);
			}
		}
		return nodes;
	}
	
	/* constructor */
	/**
	 * It creates a root of the tree using the coverage attribute on program entry
	 * @param tree
	 * @return
	 * @throws Exception
	 */
	protected static CirAttributeNode new_root(CirAttributeTree tree) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else {
			/* 1. declarations */
			Mutant mutant = tree.get_mutant(); CirExecution entry;
			CirTree cir_tree = mutant.get_space().get_cir_tree();
			CirFunction main_function = cir_tree.get_function_call_graph().get_main_function();
			
			/* 2. extract the program entry */
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
			entry = main_function.get_flow_graph().get_entry();
			
			/* 3. generate the root node for covering the program entry */
			return new CirAttributeNode(tree, CirAttribute.new_cover_count(entry, 1));
		}
	}
	/**
	 * It creates a child node under this node using unique attribute under subtree
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	protected CirAttributeNode new_child(CirAttribute attribute) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			Queue<CirAttributeNode> queue = new LinkedList<CirAttributeNode>();
			queue.add(this);
			while(!queue.isEmpty()) {
				CirAttributeNode node = queue.poll();
				if(node.get_attribute().equals(attribute)) {
					return node;
				}
				else {
					for(CirAttributeNode child : node.get_children()) {
						queue.add(child);
					}
				}
			}
			CirAttributeNode child = new CirAttributeNode(this.tree, attribute);
			this.children.add(child); child.parent = this;
			return child;
		}
	}
	
}
