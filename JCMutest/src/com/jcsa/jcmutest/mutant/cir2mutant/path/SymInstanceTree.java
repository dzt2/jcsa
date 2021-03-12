package com.jcsa.jcmutest.mutant.cir2mutant.path;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;

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
	
}
