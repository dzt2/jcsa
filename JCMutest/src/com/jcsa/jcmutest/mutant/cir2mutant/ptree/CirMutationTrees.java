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
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
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
	
	/* implications */
	/**
	 * @param statement where the concrete interpretation is performed
	 * @param contexts state hold before the statement is completed.
	 * @return mapping from each node to the concrete interpretation of the mutation at the contexts
	 * 		   {no concrete mutation refers to any node that is not matched with the statement}
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, CirMutation> interpret(CirStatement 
			statement, CStateContexts contexts) throws Exception {
		Map<CirMutationTreeNode, CirMutation> results = new HashMap<CirMutationTreeNode, CirMutation>();
		
		Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
		for(CirMutationTree tree : this.trees) { 
			if(tree.get_cir_statement() == statement) {
				queue.add(tree.get_root());
			}
		}
		
		while(!queue.isEmpty()) {
			CirMutationTreeNode node = queue.poll();
			for(CirMutationTreeNode child : node.get_children()) {
				queue.add(child);
			}
			results.put(node, node.optimize_by(contexts));
		}
		
		return results;
	}
	/**
	 * @param path the execution path records the state hold at each point during testing
	 * @return mapping from tree node to each of its concrete occurrence in the path
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, List<CirMutation>> interpret(CStatePath path) throws Exception {
		Map<CirMutationTreeNode, List<CirMutation>> results = new HashMap<CirMutationTreeNode, List<CirMutation>>();
		
		/* 1. initialization */
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
			for(CirMutationTreeNode node : results.keySet()) {
				results.get(node).add(node.optimize_by(null));
			}
		}
		
		/* 3. path-sensitive */
		else {
			CStateContexts contexts = new CStateContexts();
			for(CStateNode state_node : path.get_nodes()) {
				contexts.accumulate(state_node);
				CirStatement statement = state_node.get_statement();				
				for(CirMutationTree tree : this.trees) {
					if(tree.get_cir_statement() == statement) {
						Map<CirMutationTreeNode, CirMutation> 
							buffer = tree.interpret(contexts);
						for(CirMutationTreeNode node : buffer.keySet()) {
							CirMutation result = buffer.get(node);
							results.get(node).add(result);
						}
					}
				}
			}
		}
		
		return results;
	}
	/**
	 * @return context insensitive interpretation
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, List<CirMutation>> interpret() throws Exception {
		return this.interpret(null);
	}
	/**
	 * @param mutation
	 * @return the detection level of the concrete mutation in testing
	 * @throws Exception
	 */
	private CirDetectionLevel analyze_for(CirMutation mutation) throws Exception {
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
	 * @return mapping for each tree node to the detection level of each of its execution
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, List<CirDetectionLevel>> analyze(CStatePath path) throws Exception {
		Map<CirMutationTreeNode, List<CirDetectionLevel>> results = 
				new HashMap<CirMutationTreeNode, List<CirDetectionLevel>>();
		
		/* 1. initialization */
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
			for(CirMutationTreeNode node : results.keySet()) {
				results.get(node).add(this.analyze_for(node.optimize_by(null)));
			}
		}
		/* 3. path-sensitive */
		else {
			CStateContexts contexts = new CStateContexts();
			for(CStateNode state_node : path.get_nodes()) {
				contexts.accumulate(state_node);
				CirStatement statement = state_node.get_statement();				
				for(CirMutationTree tree : this.trees) {
					if(tree.get_cir_statement() == statement) {
						Map<CirMutationTreeNode, CirMutation> 
							buffer = tree.interpret(contexts);
						for(CirMutationTreeNode node : buffer.keySet()) {
							CirMutation result = buffer.get(node);
							results.get(node).add(this.analyze_for(result));
						}
					}
				}
			}
		}
		
		/* 4. empty checking */
		for(CirMutationTreeNode node : results.keySet()) {
			if(results.get(node).isEmpty())
				results.get(node).add(CirDetectionLevel.not_reached);
		}
		
		return results;
	}
	
}
