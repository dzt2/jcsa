package com.jcsa.jcmutest.mutant.cir2mutant.trees;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;

/**
 * It represents a node in CirInfectionNode, referring to a unique CirAttribute
 * and a set of evaluation status to record its external features in the tree.
 * @author yukimula
 *
 */
public class CirInfectionNode {
	
	/* attributes */
	/** the state infection tree where the node is defined **/
	private CirInfectionTree		tree;
	/** the type defining at which step in RIP process the node is located **/
	private CirInfectionNodeType	node_type;
	/** the data state to record the evaluation of the node **/
	private CirInfectionNodeData	data_state;
	/** the edge from its parent to this node or null when it is root **/
	private CirInfectionEdge		in_edge;
	/** the set of edges from this node to its children or empty when it is leaf **/
	private List<CirInfectionEdge>	ou_edges;
	
	/* constructor */
	/**
	 * create an isolated node in the tree w.r.t the given attribute and process-type
	 * @param tree
	 * @param type
	 * @param attribute
	 * @throws Exception
	 */
	private CirInfectionNode(CirInfectionTree tree, 
			CirInfectionNodeType type,
			CirAttribute attribute) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute as null");
		}
		else {
			this.tree = tree;
			this.node_type = type;
			this.data_state = new CirInfectionNodeData(attribute);
			this.in_edge = null;
			this.ou_edges = new ArrayList<CirInfectionEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the state infection tree where the node is defined
	 */
	public CirInfectionTree get_tree() { return this.tree; }
	/**
	 * @return the type defining at which step in RIP process the node is located
	 */
	public CirInfectionNodeType get_type() { return this.node_type; }
	/**
	 * @return the attribute defined by this node in the context of infection tree
	 */
	public CirAttribute get_attribute() { return this.data_state.get_attribute(); }
	/**
	 * @return the execution point in CFG of program where the attribute of node is evaluated
	 */
	public CirExecution get_execution() { return this.data_state.get_attribute().get_execution(); }
	/**
	 * @return the data state to record the evaluation of the node
	 */
	public CirInfectionNodeData get_data() { return this.data_state; }
	/**
	 * @return the edge from its parent to this node or null when it is root
	 */
	public CirInfectionEdge get_in_edge() { return this.in_edge; }
	/**
	 * @return the set of edges from this node to its children or empty when it is leaf
	 */
	public Iterable<CirInfectionEdge> get_ou_edges() { return this.ou_edges; }
	
	/* implication */
	/**
	 * @return whether the node is a root without any parent
	 */
	public boolean is_root() { return this.in_edge == null; }
	/**
	 * @return whether the node is a leaf without and children
	 */
	public boolean is_leaf() { return this.ou_edges.isEmpty(); }
	/**
	 * @return the number of children created under this node
	 */
	public int get_ou_degree() { return this.ou_edges.size(); }
	/**
	 * @return the parent (unique) of this node or null if it is root
	 */
	public CirInfectionNode get_parent() {
		if(this.in_edge == null) {
			return null;
		}
		else {
			return this.in_edge.get_source();
		}
	}
	/**
	 * @param k
	 * @return the kth child created under this node
	 * @throws IndexOutOfBoundsException
	 */
	public CirInfectionNode get_child(int k) throws IndexOutOfBoundsException {
		return this.ou_edges.get(k).get_target();
	}
	/**
	 * @return the sequence of edges from root until this node 
	 */
	public Iterable<CirInfectionEdge> get_root_path() {
		/* collect the reversed sequence of edges to root */
		List<CirInfectionEdge> path = new ArrayList<CirInfectionEdge>();
		CirInfectionNode node = this;
		while(!node.is_root()) {
			path.add(node.get_in_edge());
			node = node.get_parent();
		}
		
		/* reverse the path edges so from root to this node */
		for(int k = 0; k < path.size() / 2; k++) {
			int i = k;
			int j = path.size() - 1 - k;
			CirInfectionEdge ei = path.get(i);
			CirInfectionEdge ej = path.get(j);
			path.set(i, ej);
			path.set(j, ei);
		}
		return path;
	}
	
	/* generators */
	/**
	 * @param tree
	 * @return create a root node w.r.t. the mutant of the root in the program
	 * @throws Exception
	 */
	protected static CirInfectionNode new_root(CirInfectionTree tree) throws Exception {
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
				CirAttribute attribute = CirAttribute.new_cover_count(entry, 1);
				return new CirInfectionNode(tree, CirInfectionNodeType.pre_condition, attribute);
			}
		}
	}
	/**
	 * 
	 * @param node_type
	 * @param node_attribute
	 * @param edge_type
	 * @return  link the node (as parent) to a child w.r.t. the node type and
	 * 			using the edge with specified edge_type in the parameters and
	 * 			return the child from this node to it using a specified edge.
	 * @throws Exception
	 */
	protected CirInfectionNode link_to(CirInfectionNodeType node_type,
			CirAttribute node_attribute, CirInfectionEdgeType edge_type) throws Exception {
		if(node_type == null) {
			throw new IllegalArgumentException("Invalid node_type: null");
		}
		else if(edge_type == null) {
			throw new IllegalArgumentException("Invalid edge_type: null");
		}
		else if(node_attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			/* find the existing edge from this node to a child w.r.t. 
			 * given attribute to avoid duplicated construction */
			for(CirInfectionEdge ou_edge : this.ou_edges) {
				CirInfectionNode child = ou_edge.get_target();
				if(child.get_attribute().equals(node_attribute)) {
					return child;
				}
			}
			
			/* otherwise, create a new child from this node alternative */
			CirInfectionNode child = new CirInfectionNode(this.tree, node_type, node_attribute);
			child.in_edge = new CirInfectionEdge(edge_type, this, child);
			this.ou_edges.add(child.in_edge);
			return child;
		}
	}
	
}
