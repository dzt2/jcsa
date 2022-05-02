package com.jcsa.jcmutest.mutant.ctx2mutant.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextStates;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstSeedMutantState;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParsers;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.program.AstCirTree;

public class ContextMutationTree {
	
	/* definitions */
	/** the program in which the contextual mutation tree is defined **/
	private	AstCirTree 									program;
	/** the list of tree nodes created in this graphical model **/
	private	List<ContextMutationNode> 					nodes;
	/** the mapping from execution state to the correspond node **/
	private	Map<AstContextState, ContextMutationNode>	index;
	/** the mapping from mutant to its initial mutation state **/
	private	Map<Mutant, AstSeedMutantState>				mutants;
	
	/* constructor */
	/**
	 * It creates an empty tree graph
	 * @param program
	 * @throws Exception
	 */
	private ContextMutationTree(AstCirTree program) throws Exception {
		if(program == null) {
			throw new IllegalArgumentException("Invalid program: null");
		}
		else {
			this.program = program;
			this.nodes = new ArrayList<ContextMutationNode>();
			this.index = new HashMap<AstContextState, ContextMutationNode>();
			this.mutants = new HashMap<Mutant, AstSeedMutantState>();
		}
	}
	/**
	 * It creates a tree representing state-subsumption graph of the input mutants
	 * @param program
	 * @param mutants
	 * @throws Exception
	 */
	public static ContextMutationTree parse(AstCirTree program, Iterable<Mutant> mutants) throws Exception {
		if(program == null) {
			throw new IllegalArgumentException("Invalid program: null");
		}
		else if(mutants == null) {
			throw new IllegalArgumentException("Invalid mutants: null");
		}
		else {
			ContextMutationTree tree = new ContextMutationTree(program);
			Set<ContextMutationNode> records = new HashSet<ContextMutationNode>();
			int total = 0, error = 0;
			
			for(Mutant mutant : mutants) {
				ContextMutation mutation; total++;
				try {
					mutation = ContextMutationParsers.parse(mutant);
				}
				catch(Exception ex) {
					error++; continue;
				}
				
				ContextMutationNode root = tree.get_tree_node(mutation.get_mutation_state());
				tree.mutants.put(mutant, mutation.get_mutation_state());
				for(int k = 0; k < mutation.number_of_infection_pairs(); k++) {
					ContextMutationNode ichild = tree.get_tree_node(mutation.get_infection_state(k));
					ContextMutationNode pchild = tree.get_tree_node(mutation.get_ini_error_state(k));
					root.connect(ichild); root.connect(pchild);
				}
				
				Queue<ContextMutationNode> queue = new LinkedList<ContextMutationNode>();
				for(ContextMutationEdge edge : root.get_ou_edges()) queue.add(edge.get_target());
				
				while(!queue.isEmpty()) {
					ContextMutationNode node = queue.poll();
					if(!records.contains(node)) {
						records.add(node);
						Iterable<AstContextState> ou_states = AstContextStates.extend(node.get_state());
						for(AstContextState ou_state : ou_states) {
							ContextMutationNode target = tree.get_tree_node(ou_state);
							node.connect(target); queue.add(target);
						}
					}
				}
			}
			
			double error_rate = 100 * ((double) error) / ((double) total);
			System.out.println("\t\tTotal = " + total + "; Error = " + error + " (" + error_rate + "%)");
			return tree;
		}
	}
	
	/* getters */
	/**
	 * @return the program in which the contextual mutation tree is defined
	 */
	public AstCirTree	get_program() 	{ return this.program; }
	/**
	 * @return the AST of program under test
	 */
	public AstTree		get_ast_tree() 	{ return this.program.get_ast_tree(); }
	/**
	 * @return the CIR of program under test
	 */
	public CirTree		get_cir_tree()	{ return this.program.get_cir_tree(); }
	/**
	 * @return the tree nodes created under the tree
	 */
	public Iterable<ContextMutationNode> get_tree_nodes() { return this.nodes; }
	/**
	 * @return the number of nodes
	 */
	public int number_of_tree_nodes() { return this.nodes.size(); }
	/**
	 * @param id
	 * @return the kth tree node under this tree
	 * @throws IndexOutOfBoundsException
	 */
	public ContextMutationNode get_tree_node(int id) throws IndexOutOfBoundsException { return this.nodes.get(id); }
	/**
	 * @param state
	 * @return the tree node w.r.t. the state as representative
	 * @throws Exception
	 */
	public ContextMutationNode get_tree_node(AstContextState state) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else {
			if(!this.index.containsKey(state)) {
				ContextMutationNode node = new ContextMutationNode(this, this.nodes.size(), state);
				this.nodes.add(node);
				this.index.put(state, node);
			}
			return this.index.get(state);
		}
	}
	/**
	 * @return the set of mutants of which seed-states are created under the tree
	 */
	public Iterable<Mutant> get_mutants() { return this.mutants.keySet(); }
	/**
	 * @param mutant
	 * @return whether the mutant corresponds to any tree node
	 */
	public boolean has_tree_node_of(Mutant mutant) { return this.mutants.containsKey(mutant); }
	/**
	 * @param mutant
	 * @return the tree node representing the seed-mutation state of the input
	 * @throws Exception
	 */
	public ContextMutationNode get_tree_node_of(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else if(this.mutants.containsKey(mutant)) {
			return this.get_tree_node(this.mutants.get(mutant));
		}
		else {
			throw new IllegalArgumentException("Undefined: " + mutant);
		}
	}
	
	
}
