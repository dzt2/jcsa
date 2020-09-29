package com.jcsa.jcmutest.mutant.cir2mutant.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;

/**
 * It provides the graphical structural model to describe the detection
 * process of CirMutation (including the constraints as well as the state
 * errors it causes during the process of software testing).
 * 
 * @author yukimula
 *
 */
public class CirMutationGraph {
	
	/* definitions */
	/** used to create constraints, state errors as well 
	 * 	as the cir-mutations in source code **/
	private CirMutations cir_mutations;
	/** mutation used as the root to generate the graph **/
	private CirMutation root_mutation;
	/** constraints required for reaching the root mutation **/
	private List<CirConstraint> path_constraints;
	/** mapping from each mutation to their unique node **/
	private Map<CirMutation, CirMutationNode> nodes;
	
	/* constructor */
	/**
	 * create an empty graph for error propagation analysis of the root mutation 
	 * @param cir_mutations
	 * @param root_mutation
	 * @throws Exception
	 */
	public CirMutationGraph(CirMutations cir_mutations, CirMutation root_mutation) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else if(root_mutation == null)
			throw new IllegalArgumentException("Invalid root_mutation: null");
		else {
			this.cir_mutations = new CirMutations(cir_mutations.get_cir_tree());
			this.root_mutation = this.get_unique_mutation(root_mutation);
			this.path_constraints = new ArrayList<CirConstraint>();
			this.nodes = new HashMap<CirMutation, CirMutationNode>();
		}
	}
	/**
	 * @param mutation
	 * @return the unique instance of mutation under this graph
	 * @throws Exception
	 */
	private CirMutation get_unique_mutation(CirMutation mutation) throws Exception {
		return this.cir_mutations.new_mutation(mutation.get_constraint(), mutation.get_state_error());
	}
	
	/* getters */
	/**
	 * @return the interface to create node and mutations in this graph
	 */
	public CirMutations get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the root node in the graph
	 */
	public CirMutationNode get_root_node() { return this.nodes.get(this.root_mutation); }
	/**
	 * @return mutation of the root node 
	 */
	public CirMutation get_root_mutation() { return this.root_mutation; }
	/**
	 * @return constraints required for reaching the statement of root node
	 */
	public Iterable<CirConstraint> get_path_constraints() { return this.path_constraints; }
	/**
	 * @param cir_mutation
	 * @return whether there is a node w.r.t. the mutation
	 */
	public boolean has_node(CirMutation cir_mutation) { return this.nodes.containsKey(cir_mutation); }
	/**
	 * @param cir_mutation
	 * @return the node w.r.t. the mutation in the graph
	 * @throws Exception
	 */
	public CirMutationNode get_node(CirMutation cir_mutation) throws Exception {
		cir_mutation = this.get_unique_mutation(cir_mutation);
		if(this.nodes.containsKey(cir_mutation))
			return this.nodes.get(cir_mutation);
		else
			throw new IllegalArgumentException(cir_mutation.toString());
	}
	/**
	 * @return all the nodes created in this graph
	 */
	public Iterable<CirMutationNode> get_nodes() { return this.nodes.values(); }
	/**
	 * @param cir_mutation
	 * @return create a node w.r.t. the mutation in the graph
	 * @throws Exception
	 */
	public CirMutationNode new_node(CirMutation cir_mutation) throws Exception {
		cir_mutation = this.get_unique_mutation(cir_mutation);
		if(!this.nodes.containsKey(cir_mutation)) {
			this.nodes.put(cir_mutation, new CirMutationNode(this, cir_mutation));
		}
		return this.nodes.get(cir_mutation);
	}
	
}
