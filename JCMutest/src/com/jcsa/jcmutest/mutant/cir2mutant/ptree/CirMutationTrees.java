package com.jcsa.jcmutest.mutant.cir2mutant.ptree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.flwa.dominate.CDominanceGraph;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.test.state.CStateContexts;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * Error propagation trees for each mutant in C.
 * 
 * @author yukimula
 *
 */
public class CirMutationTrees {
	
	/* definitions */
	/** library used to create cir-mutaions for tree nodes **/
	private CirMutations cir_mutations;
	/** the mutation injected in abstract syntactic tree **/
	private Mutant mutant;
	/** the set of trees generated from each cir-mutation of the mutant **/
	private List<CirMutationTree> trees;
	
	/* getters */
	/**
	 * @return library used to create cir-mutations for tree nodes
	 */
	public CirMutations get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the mutation injected in abstract syntactic tree
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the set of trees generated from each cir-mutation of the mutant
	 */
	public AstMutation get_ast_mutation() { return this.mutant.get_mutation(); }
	/**
	 * @return the set of trees w.r.t. each cir-mutation of the mutant
	 */
	public Iterable<CirMutationTree> get_trees() { return this.trees; }
	
	/* constructors */
	private CirMutationTrees(CirTree cir_tree, Mutant mutant) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.cir_mutations = new CirMutations(cir_tree);
			this.mutant = mutant;
			this.trees = new ArrayList<CirMutationTree>();
		}
	}
	/**
	 * the trees of cir-mutations w.r.t. the source mutant in specified code
	 * @param cir_tree
	 * @param mutant
	 * @throws Exception
	 */
	public static CirMutationTrees new_trees(CirTree cir_tree, Mutant 
			mutant, CDominanceGraph dominance_graph) throws Exception {
		CirMutationTreeUtils.tree_node_id = 0;
		CirMutationTrees trees = new CirMutationTrees(cir_tree, mutant);
		if(mutant.has_cir_mutations()) {
			for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
				trees.trees.add(new CirMutationTree(trees, CirMutationTreeUtils.
								tree_node_id++, cir_mutation, dominance_graph));
			}
		}
		return trees;
	}
	
	/* concrete state interpretation */
	/**
	 * @param path the execution path of the program being executed during testing
	 * @return mapping from cir-mutation tree node to the concrete mutations generated from
	 *  	   the state contexts obtained during testing process.
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, List<CirMutation>> con_interpret(CStatePath path) throws Exception {
		if(path == null) 
			throw new IllegalArgumentException("Invalid path: null");
		else {
			/* 1. initialization */
			Map<CirMutationTreeNode, List<CirMutation>> results = 
					new HashMap<CirMutationTreeNode, List<CirMutation>>();
			Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
			for(CirMutationTree tree : this.trees) { queue.add(tree.get_root()); }
			while(!queue.isEmpty()) {
				CirMutationTreeNode tree_node = queue.poll();
				for(CirMutationTreeNode child : tree_node.get_children()) {
					queue.add(child);
				}
				results.put(tree_node, new LinkedList<CirMutation>());
			}
			
			/* 2. accumulate the state on path and concrete mutation */
			CStateContexts contexts = new CStateContexts();
			for(CStateNode state_node : path.get_nodes()) {
				contexts.accumulate(state_node);
				for(CirMutationTree tree : this.trees) {
					if(tree.get_root_mutation().get_statement() == state_node.get_statement()) {
						tree.execute_and_update(state_node.get_statement(), contexts, results);
					}
				}
			}
			
			/* 3. end of all */	return results;
		}
	}
	/**
	 * @return mapping from cir-mutation tree node to the concrete mutation generated from
	 *  	   the static analysis without using execution path information.
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, CirMutation> con_interpret() throws Exception {
		Map<CirMutationTreeNode, CirMutation> results = 
				new HashMap<CirMutationTreeNode, CirMutation>();
		Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
		for(CirMutationTree tree : this.trees) { queue.add(tree.get_root()); }
		while(!queue.isEmpty()) {
			CirMutationTreeNode tree_node = queue.poll();
			for(CirMutationTreeNode child : tree_node.get_children()) {
				queue.add(child);
			}
			results.put(tree_node, this.cir_mutations.
					optimize(tree_node.get_cir_mutation(), null));
		}
		return results;
	}
	
	/* abstract state interpretation */
	/**
	 * @param path the execution path of the program being executed during testing
	 * @return mapping from cir-mutation tree node to the abstract mutation state generated
	 * 		   from the state contexts obtained during testing process.
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, CirMutationStatus> abs_interpret(CStatePath path) throws Exception {
		if(path == null) 
			throw new IllegalArgumentException("Invalid path: null");
		else {
			/* 1. initialization */
			Map<CirMutationTreeNode, CirMutationStatus> results = 
					new HashMap<CirMutationTreeNode, CirMutationStatus>();
			Map<CirMutationTreeNode, List<CirMutation>> conc_results = this.con_interpret(path);
			
			/* 2. translation */
			for(CirMutationTreeNode tree_node : conc_results.keySet()) {
				List<CirMutation> conc_mutations = conc_results.get(tree_node);
				CirMutationStatus status = new CirMutationStatus(tree_node);
				for(CirMutation conc_mutation : conc_mutations) {
					status.append_concrete_mutation(conc_mutation);
				}
				results.put(tree_node, status);
			}
			
			/* 3. end of all */	return results;
		}
	}
	/**
	 * @return 
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, CirMutationStatus> abs_interpret() throws Exception {
		Map<CirMutationTreeNode, CirMutation> conc_results = this.con_interpret();
		Map<CirMutationTreeNode, CirMutationStatus> results = 
				new HashMap<CirMutationTreeNode, CirMutationStatus>();
		for(CirMutationTreeNode tree_node : conc_results.keySet()) {
			CirMutation conc_mutation = conc_results.get(tree_node);
			CirMutationStatus status = new CirMutationStatus(tree_node);
			status.append_concrete_mutation(conc_mutation);
			results.put(tree_node, status);
		}
		return results;
	}
	
}