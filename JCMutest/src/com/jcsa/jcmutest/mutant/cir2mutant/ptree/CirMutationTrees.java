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
		if(mutant.has_cir_mutations()) {
			for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
				trees.trees.add(new CirMutationTree(trees, cir_mutation, dominance_graph));
			}
		}
		return trees;
	}
	
	/* analysis methods */
	/**
	 * @param path
	 * @return mapping from each tree node to its concrete value hold at each of its occurrences
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, List<CirMutation>> interpret(CStatePath path) throws Exception {
		/* 1. initialization */
		Map<CirMutationTreeNode, List<CirMutation>> results = 
				new HashMap<CirMutationTreeNode, List<CirMutation>>();
		Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
		for(CirMutationTree tree : this.trees) { queue.add(tree.get_root()); }
		while(!queue.isEmpty()) {
			CirMutationTreeNode node = queue.poll();
			for(CirMutationTreeNode child : node.get_children()) {
				queue.add(child);
			}
			results.put(node, new ArrayList<CirMutation>());
		}
		
		/* 2. path-insensitive */
		if(path == null) {
			for(CirMutationTreeNode tree_node : results.keySet()) {
				results.get(tree_node).add(tree_node.optimize_by(null));
			}
		}
		
		/* 3. path sensitive */
		else {
			CStateContexts contexts = new CStateContexts();
			for(CStateNode state_node : path.get_nodes()) {
				contexts.accumulate(state_node);
				for(CirMutationTreeNode tree_node : results.keySet()) {
					if(tree_node.get_cir_mutation().get_statement() == state_node.get_statement()) {
						results.get(tree_node).add(tree_node.optimize_by(contexts));
					}
				}
			}
		}
		
		/* 4. end of all */	return results;
	}
	/**
	 * @param node
	 * @param contexts
	 * @return the detection level as the analysis result of the tree-node
	 * @throws Exception
	 */
	private CirDetectionLevel get_result_at(CirMutationTreeNode 
				node, CStateContexts contexts) throws Exception {
		CirMutation mutation = this.
				cir_mutations.optimize(node.get_cir_mutation(), contexts);
		Boolean cons_valid = mutation.get_constraint().validate(contexts);
		Boolean error_valid = mutation.get_state_error().validate(contexts);
		if(cons_valid == null) {
			if(error_valid == null) {
				return CirDetectionLevel.part_infectable;
			}
			else if(error_valid.booleanValue()) {
				return CirDetectionLevel.post_infectable;
			}
			else {
				return CirDetectionLevel.not_infected;
			}
		}
		else if(cons_valid.booleanValue()) {
			if(error_valid == null) {
				return CirDetectionLevel.prev_infectable;
			}
			else if(error_valid.booleanValue()) {
				return CirDetectionLevel.infected;
			}
			else {
				return CirDetectionLevel.post_infectable;
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
				results.get(tree_node).add(CirDetectionLevel.not_reachable);
			}
		}
		
		/* 5. end of all */	return results;
	}
	/**
	 * @param levels
	 * @return the summarized result of the detection levels as given
	 * @throws Exception
	 */
	private CirDetectionLevel get_summary_of(Iterable<CirDetectionLevel> levels) throws Exception {
		CirDetectionLevel result = CirDetectionLevel.not_reachable;
		
		for(CirDetectionLevel level : levels) {
			switch(level) {
			case not_satisfied:
			{
				if(result == CirDetectionLevel.not_reachable
					|| result == CirDetectionLevel.not_satisfied) {
					result = level;
				}
				break;
			}
			case not_infected:
			{
				if(result == CirDetectionLevel.not_reachable
					|| result == CirDetectionLevel.not_satisfied
					|| result == CirDetectionLevel.not_infected) {
					result = level;
				}
				break;
			}
			case part_infectable:
			{
				if(result == CirDetectionLevel.not_reachable
					|| result == CirDetectionLevel.not_satisfied
					|| result == CirDetectionLevel.not_infected
					|| result == CirDetectionLevel.part_infectable) {
					result = level;
				}
				break;
			}
			case prev_infectable:
			{
				if(result == CirDetectionLevel.not_reachable
					|| result == CirDetectionLevel.not_satisfied
					|| result == CirDetectionLevel.not_infected
					|| result == CirDetectionLevel.part_infectable) {
					result = level;
				}
				break;
			}
			case post_infectable:
			{
				if(result == CirDetectionLevel.not_reachable
					|| result == CirDetectionLevel.not_satisfied
					|| result == CirDetectionLevel.not_infected
					|| result == CirDetectionLevel.part_infectable) {
					result = level;
				}
				break;
			}
			case infected:
			{
				result = level;
				break;
			}
			default: break;
			}
		}
		
		return result;
	}
	/**
	 * @param path
	 * @return mapping from each tree node to the maximal detection level it occurs
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, CirDetectionLevel> summarize(CStatePath path) throws Exception {
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
		
		/* 4. construct the summary result */
		Map<CirMutationTreeNode, CirDetectionLevel> summary = 
				new HashMap<CirMutationTreeNode, CirDetectionLevel>();
		for(CirMutationTreeNode tree_node : results.keySet()) {
			summary.put(tree_node, this.get_summary_of(results.get(tree_node)));
		}
		return summary;
	}
	
}
