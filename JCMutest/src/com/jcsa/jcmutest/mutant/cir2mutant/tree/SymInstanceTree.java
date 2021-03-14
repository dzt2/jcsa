package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * 	It maintains the abstract extension tree from program entry to the mutated points and propagation nodes
 * 	annotated with instances that describe necessary conditions required for killing a mutant in hierarchy
 * 	structure of tree.
 * 	
 * 	@author yukimula
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
	/** the sequence of global states evaluated and created for testing **/
	protected List<SymInstanceState> global_states;
	
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
			this.global_states = new ArrayList<SymInstanceState>();
			this.init_root();
		}
	}
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
	/** 
	 * @return the collection of global states evaluated during testing in sequence of when they are evaluated.
	 */
	public Iterable<SymInstanceState> get_global_states() {
		return this.global_states;
	}
	/**
	 * @return the collection of tree nodes that describe the coverage of faulty statements.
	 */
	public Collection<SymInstanceTreeNode> get_mutation_nodes() {
		Queue<SymInstanceTreeNode> queue = new LinkedList<SymInstanceTreeNode>();
		Set<SymInstanceTreeNode> mutation_nodes = new HashSet<SymInstanceTreeNode>();
		
		queue.add(this.root);
		while(!queue.isEmpty()) {
			SymInstanceTreeNode node = queue.poll();
			if(node.get_node_status().is_state_error()) {
				mutation_nodes.add(node.get_parent());
			}
			else {
				for(SymInstanceTreeNode child : node.get_children()) {
					queue.add(child);
				}
			}
		}
		
		return mutation_nodes;
	}
	/**
	 * @return the set of paths from root to the last reachable nodes in the tree.
	 */
	public Collection<List<SymInstanceTreeNode>> get_reachable_paths() {
		return SymInstanceTreeUtils.utils.collect_reachable_paths(this);
	}
	
	/* evaluation */
	/**
	 * reset the status in tree nodes and global state list.
	 */
	public void reset() {
		Queue<SymInstanceTreeNode> queue = 
				new LinkedList<SymInstanceTreeNode>();
		queue.add(this.root);
		
		while(!queue.isEmpty()) {
			SymInstanceTreeNode node = queue.poll();
			if(node.has_edge_status()) {
				node.get_edge_status().reset();
			}
			node.get_node_status().reset();
			
			for(SymInstanceTreeNode child : node.get_children()) {
				queue.add(child);
			}
		}
		
		this.global_states.clear();
	}
	/**
	 * @param status
	 * @param contexts
	 * @return perform evaluation on instance in given status and update global sequence of states
	 * @throws Exception
	 */
	protected SymInstanceState eval_on(SymInstanceStatus status, 
				SymbolStateContexts contexts) throws Exception {
		if(status == null)
			throw new IllegalArgumentException("Invalid status: null");
		else {
			SymInstanceState state = status.add_state(this.cir_mutations, contexts);
			this.global_states.add(state);
			return state;
		}
	}
	/**
	 * perform static evaluation on states in this tree nodes
	 * @throws Exception
	 */
	public void evaluate() throws Exception {
		SymInstanceTreeUtils.utils.static_evaluations(this);
	}
	/**
	 * perform dynamic evaluation on states in the tree nodes
	 * @param test_path
	 * @throws Exception
	 */
	public void evaluate(CStatePath test_path) throws Exception {
		SymInstanceTreeUtils.utils.dynamic_evaluation(this, test_path);
	}
	
	/* utility */
	/**
	 * @param mutant
	 * @param max_distance
	 * @param dependence_graph
	 * @return the symbolic tree to define nodes annotated with symbolic instances required for killing mutation
	 * 		   with specified distance of error propagation from mutation nodes and over the dependence graph.
	 * @throws Exception
	 */
	public static SymInstanceTree new_tree(Mutant mutant, int max_distance, 
			CDependGraph dependence_graph) throws Exception {
		return SymInstanceTreeUtils.utils.build_sym_instance_tree(mutant, max_distance, dependence_graph);
	}
	
}
