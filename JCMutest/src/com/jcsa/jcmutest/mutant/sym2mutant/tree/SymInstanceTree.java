package com.jcsa.jcmutest.mutant.sym2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymInstanceUtils;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymTreeUtils;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * Create a symbolic instance tree for killing a particular mutation.
 * 
 * @author yukimula
 *
 */
public class SymInstanceTree {
	
	/* definitions */
	private CirTree cir_tree;
	private Mutant mutant;
	private SymInstanceTreeNode root;
	private SymInstanceTree(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.cir_tree = mutant.get_space().get_cir_tree();
			this.mutant = mutant;
			this.root = this.new_root();
		}
	}
	private SymInstanceTreeNode new_root() throws Exception {
		CirFunction main_function = this.cir_tree.get_function_call_graph().get_main_function();
		if(main_function == null) {
			AstNode ast_location = this.mutant.get_mutation().get_location();
			ast_location = ast_location.get_tree().function_of(ast_location);
			for(CirFunction function : this.cir_tree.get_function_call_graph().get_functions()) {
				if(function.get_definition().get_ast_source() == ast_location) {
					main_function = function; break;
				}
			}
		}
		return new SymInstanceTreeNode(this, SymInstanceUtils.stmt_constraint(
				main_function.get_flow_graph().get_entry(), COperator.greater_eq, 1));
	}
	
	/* getters */
	/**
	 * @return mutation to be killed under the symbolic tree
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the C-intermediate representation where the mutation is defined
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @return the root node of the symbolic tree 
	 */
	public SymInstanceTreeNode get_root() { return this.root; }
	
	/* inference */
	/**
	 * @param tree_node
	 * @param infection_edges
	 */
	private void get_infection_edges(SymInstanceTreeNode tree_node, 
				Collection<SymInstanceTreeEdge> infection_edges) {
		if(tree_node.is_state_error()) {
			infection_edges.add(tree_node.get_in_edge());
		}
		else {
			for(SymInstanceTreeEdge edge : tree_node.get_ou_edges()) {
				this.get_infection_edges(edge.get_child(), infection_edges);
			}
		}
	}
	/**
	 * @return the collection of tree edges referring to the infection phases (from reaching node to initial error node)
	 */
	public Collection<SymInstanceTreeEdge> get_infection_edges() {
		Collection<SymInstanceTreeEdge> infection_edges = new ArrayList<SymInstanceTreeEdge>();
		this.get_infection_edges(this.root, infection_edges);
		return infection_edges;
	}
	/**
	 * collect all the paths reachable based on abstract state
	 * @param edge
	 * @param paths
	 * @throws Exception
	 */
	private void get_reachable_state_paths(SymInstanceTreeEdge edge, Collection<List<SymInstanceTreeEdge>> paths) {
		Boolean result = edge.get_abstract_state().get_evaluation_result();
		if(result == null || result.booleanValue()) {
			this.get_reachable_state_paths(edge.get_child(), paths);
		}
		else {
			paths.add(edge.get_child().get_prev_path());
		}
	}
	/**
	 * collect all the paths reachable based on abstract state 
	 * @param node
	 * @param paths
	 * @throws Exception
	 */
	private void get_reachable_state_paths(SymInstanceTreeNode node, Collection<List<SymInstanceTreeEdge>> paths) {
		if(node.is_leaf()) {
			paths.add(node.get_prev_path());
		}
		else {
			Boolean result = node.get_abstract_state().get_evaluation_result();
			if(result == null || result.booleanValue()) {
				for(SymInstanceTreeEdge edge : node.get_ou_edges()) {
					this.get_reachable_state_paths(edge, paths);
				}
			}
			else {
				paths.add(node.get_prev_path());
			}
		}
	}
	/**
	 * @return the set of all paths reachable based on abstract state
	 */
	public Collection<List<SymInstanceTreeEdge>> get_reachable_state_paths() {
		Collection<List<SymInstanceTreeEdge>> paths = new ArrayList<List<SymInstanceTreeEdge>>();
		this.get_reachable_state_paths(this.root, paths);
		return paths;
	}
	/**
	 * collect all the paths reachable based on concrete states accumulated
	 * @param edge
	 * @param paths
	 */
	private void get_reachable_status_paths(SymInstanceTreeEdge edge, Collection<List<SymInstanceTreeEdge>> paths) {
		Boolean result = edge.get_concrete_status().get_evaluation_result();
		if(result == null || result.booleanValue()) {
			this.get_reachable_status_paths(edge.get_child(), paths);
		}
		else {
			paths.add(edge.get_child().get_prev_path());
		}
	}
	/**
	 * collect all the paths reachable based on concrete states accumulated
	 * @param node
	 * @param paths
	 */
	private void get_reachable_status_paths(SymInstanceTreeNode node, Collection<List<SymInstanceTreeEdge>> paths) {
		if(node.is_leaf()) {
			paths.add(node.get_prev_path());
		}
		else {
			Boolean result = node.get_concrete_status().get_evaluation_result();
			if(result == null || result.booleanValue()) {
				for(SymInstanceTreeEdge edge : node.get_ou_edges()) {
					this.get_reachable_status_paths(edge, paths);
				}
			}
			else {
				paths.add(node.get_prev_path());
			}
		}
	}
	/**
	 * @return collect all the paths reachable based on concrete status accumulated
	 */
	public Collection<List<SymInstanceTreeEdge>> get_reachable_status_paths() {
		Collection<List<SymInstanceTreeEdge>> paths = new ArrayList<List<SymInstanceTreeEdge>>();
		this.get_reachable_status_paths(this.root, paths);
		return paths;
	}
	private void clear_status(SymInstanceTreeNode node) {
		node.get_concrete_status().clear();
		for(SymInstanceTreeEdge edge : node.get_ou_edges()) {
			this.clear_status(edge);
		}
	}
	private void clear_status(SymInstanceTreeEdge edge) {
		edge.get_concrete_status().clear();
		this.clear_status(edge.get_child());
	}
	/**
	 * clear all the status under the root
	 */
	public void clear_status() {
		this.clear_status(this.root);
	}
	
	/* creators */
	/**
	 * @param mutant mutation being killed
	 * @param propagation_distance maximal distance for error propagation
	 * @param dependence_graph
	 * @return
	 * @throws Exception
	 */
	public static SymInstanceTree new_tree(Mutant mutant, int propagation_distance, CDependGraph dependence_graph) throws Exception {
		SymInstanceTree tree = new SymInstanceTree(mutant);
		SymTreeUtils.construct_tree(tree, propagation_distance, dependence_graph);
		return tree;
	}
	
	/* evaluation methods */
	/**
	 * clear the status records and update statically
	 * @return
	 * @throws Exception
	 */
	public void evaluate() throws Exception {
		this.clear_status();
		SymTreeUtils.static_evaluate(this);
	}
	/**
	 * @param path
	 * @throws Exception
	 */
	public void evaluate(CStatePath path) throws Exception {
		SymTreeUtils.dynamic_evaluate(this, path);
	}
	
}
