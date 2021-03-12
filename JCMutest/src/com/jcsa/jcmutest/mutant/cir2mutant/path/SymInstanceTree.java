package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It maintains the abstract extension tree from program entry to the mutated points and propagation nodes
 * annotated with instances that describe necessary conditions required for killing a mutant in hierarchy
 * structure of tree.
 * 
 * @author yukimula
 *
 */
public class SymInstanceTree {
	
	/* definitions */
	/** the mutation used as test objective **/
	private Mutant mutant;
	/** used to produce new symbolic instances **/
	private CirMutations cir_mutations;
	/** the root node w.r.t. coverage of program entry as node_instance **/
	private SymInstanceTreeNode root;
	
	/* constructor */
	/**
	 * create an empty tree for describing the abstract symbolic path
	 * annotated with symbolic instances to describe its conditions 
	 * for killing the given target mutant in hierarchical model.
	 * @param mutant
	 * @throws Exception
	 */
	protected SymInstanceTree(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.mutant = mutant;
			this.cir_mutations = new CirMutations(mutant.get_space().get_cir_tree());
			this.init_root();
		}
	}
	/**
	 * create the root node w.r.t. coverage of program entry
	 * @throws Exception
	 */
	private void init_root() throws Exception {
		/* create the root node w.r.t. the main function entry or local entry */
		CirTree cir_tree = this.cir_mutations.get_cir_tree();
		CirFunction function = cir_tree.get_function_call_graph().get_main_function();
		if(function == null) {
			for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
				CirExecution mut_execution = cir_mutation.get_execution();
				function = mut_execution.get_graph().get_function();
				break;
			}
		}
		this.root = new SymInstanceTreeNode(this, this.cir_mutations.expression_constraint(
				function.get_flow_graph().get_entry().get_statement(), Boolean.TRUE, true));
	}
	
	/* getters */
	/**
	 * @return the mutation used as test objective
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return used to produce new symbolic instances
	 */
	public CirMutations get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the root node created in the tree
	 */
	public SymInstanceTreeNode get_root() {
		return this.root;
	}
	
	/* inferring */
	/**
	 * collect mutation nodes into the output nodes
	 * @param node
	 * @param nodes
	 */
	private void collect_mutation_nodes(SymInstanceTreeNode node, Collection<SymInstanceTreeNode> nodes) {
		if(node.get_node_state().is_state_error()) {
			nodes.add(node.get_parent());
		}
		else {
			for(SymInstanceTreeNode child : node.get_children()) {
				this.collect_mutation_nodes(child, nodes);
			}
		}
	}
	/**
	 * @return collection of tree nodes that represent the coverage of faulty statement(s)
	 */
	public Collection<SymInstanceTreeNode> get_mutation_nodes() {
		Collection<SymInstanceTreeNode> mutation_nodes = 
						new HashSet<SymInstanceTreeNode>();
		this.collect_mutation_nodes(this.root, mutation_nodes);
		return mutation_nodes;
	}
	/**
	 * @return collect paths reachable from root to last reachable node
	 */
	public Collection<List<SymInstanceTreeNode>> get_reachable_paths() {
		return SymInstanceTreeUtil.utils.collect_reachable_paths(this);
	}
	
	/* setters */
	/**
	 * reset all the state of nodes in the subtree based on tree_node as root
	 * @param tree_node
	 */
	private void reset_from(SymInstanceTreeNode tree_node) {
		if(!tree_node.is_root()) {
			tree_node.get_edge_state().reset();
		}
		tree_node.get_node_state().reset();
		for(SymInstanceTreeNode child : tree_node.get_children()) {
			this.reset_from(child);
		}
	}
	/**
	 * reset the states of all the tree nodes in the model
	 */
	public void reset() { this.reset_from(this.root); }
	/**
	 * Perform static evaluation to update its states
	 * @throws Exception
	 */
	public void evaluate() throws Exception {
		SymInstanceTreeUtil.utils.static_evaluations(this);
	}
	/**
	 * dynamic evaluation to update its state
	 * @param test_path
	 * @throws Exception
	 */
	public void evaluate(CStatePath test_path) throws Exception {
		SymInstanceTreeUtil.utils.dynamic_evaluation(this, test_path);
	}
	
	/* static APIs */
	/**
	 * @param mutant
	 * @param max_distance
	 * @param dependence_graph
	 * @return symbolic tree for describing context conditions required for killing mutation with a maximal
	 * 		   distance of error propagation after the infection is performed.
	 * @throws Exception
	 */
	public static SymInstanceTree new_tree(Mutant mutant, int max_distance, 
						CDependGraph dependence_graph) throws Exception {
		return SymInstanceTreeUtil.utils.build_sym_instance_tree(mutant, max_distance, dependence_graph);
	}
	
}
