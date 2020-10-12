package com.jcsa.jcmutest.mutant.cir2mutant.ptree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
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
	 * @return library used to create cir-mutaions for tree nodes
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
		CirMutationTrees trees = new CirMutationTrees(cir_tree, mutant);
		for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
			trees.trees.add(new CirMutationTree(trees, cir_mutation, dominance_graph));
		}
		return trees;
	}
	
	/* analysis methods */
	/**
	 * @param node
	 * @param contexts
	 * @return the detection level as the analysis result of the tree-node
	 * @throws Exception
	 */
	private CirDetectionLevel get_result_at(CirMutationTreeNode 
				node, CStateContexts contexts) throws Exception {
		CirMutation mutation = node.optimize_by(contexts);
		if(mutation.get_constraint().satisfiable()) {
			if(mutation.get_state_error().influencable()) {
				return CirDetectionLevel.infected;
			}
			else {
				return CirDetectionLevel.not_infected;
			}
		}
		else {
			return CirDetectionLevel.not_satisfied;
		}
	}
	/**
	 * @param path
	 * @return mapping from tree node to the detection level of each occurrence
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, List<CirDetectionLevel>> analyze(CStatePath path) throws Exception {
		/* 1. initialization */
		Map<CirMutationTreeNode, List<CirDetectionLevel>> results = 
				new HashMap<CirMutationTreeNode, List<CirDetectionLevel>>();
		Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
		for(CirMutationTree tree : this.trees) { queue.add(tree.get_root()); }
		while(!queue.isEmpty()) {
			CirMutationTreeNode node = queue.poll();
			for(CirMutationTreeNode child : node.get_children()) {
				queue.add(child);
			}
			results.put(node, new ArrayList<CirDetectionLevel>());
		}
		
		/* 2. path-insensitive */
		if(path == null) {
			for(CirMutationTreeNode tree_node : results.keySet()) {
				results.get(tree_node).add(this.get_result_at(tree_node, null));
			}
		}
		
		/* 3. path sensitive */
		else {
			CStateContexts contexts = new CStateContexts();
			for(CStateNode state_node : path.get_nodes()) {
				contexts.accumulate(state_node);
				for(CirMutationTreeNode tree_node : results.keySet()) {
					if(tree_node.get_cir_mutation().get_statement() == state_node.get_statement()) {
						results.get(tree_node).add(this.get_result_at(tree_node, contexts));
					}
				}
			}
		}
		
		/* 4. set empty results as not_reached */
		for(CirMutationTreeNode tree_node : results.keySet()) {
			if(results.get(tree_node).isEmpty()) {
				results.get(tree_node).add(CirDetectionLevel.not_reached);
			}
		}
		
		/* 5. end of all */	return results;
	}
	
	
	
	
}
