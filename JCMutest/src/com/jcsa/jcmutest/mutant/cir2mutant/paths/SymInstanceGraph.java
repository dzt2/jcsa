package com.jcsa.jcmutest.mutant.cir2mutant.paths;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstance;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * The symbolic instance graph as feature model for prediction and evaluation.
 * 
 * @author yukimula
 *
 */
public class SymInstanceGraph {
	
	/* definitions */
	/** the mutation that the instance graph describes **/
	private Mutant mutant;
	/** the library used to generate symbolic instance **/
	private CirMutations cir_mutations;
	/** the collection of symbolic nodes created in this graph **/
	private Collection<SymInstanceNode> nodes;
	/** mapping from symbolic instance in the nodes | edges of the graph to their status in testing **/
	private Map<SymInstance, SymInstanceStatus> status_table;
	/**
	 * create an empty graph to describe the structural features hold by the mutant
	 * @param mutant
	 * @throws Exception
	 */
	private SymInstanceGraph(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.mutant = mutant;
			this.cir_mutations = new CirMutations(mutant.get_space().get_cir_tree());
			this.nodes = new ArrayList<SymInstanceNode>();
			this.status_table = new HashMap<SymInstance, SymInstanceStatus>();
		}
	}
	
	/* getters */
	/**
	 * @return the mutant being described by this model
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the syntactic mutation defined by the mutant of this graph
	 */
	public AstMutation get_mutation() { return this.mutant.get_mutation(); }
	/**
	 * @return the library used to generate symbolic instance
	 */
	public CirMutations get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the number of nodes created in this graph
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * @return the collection of nodes created in this graph
	 */
	public Iterable<SymInstanceNode> get_nodes() { return this.nodes; }
	/**
	 * @param instance
	 * @return whether there is status w.r.t. the instance as given
	 */
	public boolean has_status(SymInstance instance) {
		return this.status_table.containsKey(instance);
	}
	/**
	 * @param instance
	 * @return the status w.r.t. the instance or null if no such instance exists
	 */
	public SymInstanceStatus get_status(SymInstance instance) {
		if(this.status_table.containsKey(instance))
			return this.status_table.get(instance);
		else
			return null;	/* no existing status */
	}
	
	/* setters */
	/**
	 * remove all the nodes, edges and their status in the graph
	 */
	protected void clear() {
		for(SymInstanceNode node : this.nodes) {
			node.delete();
		}
		this.nodes.clear(); this.status_table.clear();
	}
	/**
	 * register a new status for the given instance
	 * @param instance
	 */
	protected void register_status(SymInstance instance) throws Exception {
		if(instance != null) {
			if(!this.status_table.containsKey(instance))
				this.status_table.put(instance, new SymInstanceStatus(instance));
		}
	}
	/**
	 * create a new isolated node in the graph w.r.t. the execution
	 * @param execution
	 * @return
	 * @throws Exception
	 */
	protected SymInstanceNode new_node(CirExecution execution) throws Exception {
		SymInstanceNode node = new SymInstanceNode(this, execution);
		this.nodes.add(node); return node;
	}
	
}
