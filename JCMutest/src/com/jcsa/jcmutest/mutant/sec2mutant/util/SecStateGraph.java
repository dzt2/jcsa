package com.jcsa.jcmutest.mutant.sec2mutant.util;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.util.apis.SecInfectionBuild;
import com.jcsa.jcmutest.mutant.sec2mutant.util.apis.SecPathFinder;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.irlang.CirTree;

/**
 * The state graph describes the constraints as well as the state errors that
 * are required for killing a mutation.
 * 
 * @author yukimula
 *
 */
public class SecStateGraph {
	
	/* attributes */
	/** C-intermediate representation on which the mutation is injected **/
	private CirTree cir_tree;
	/** the mutation that the graph describes **/
	private Mutant mutant;
	/** the state nodes created in this graph **/
	private List<SecStateNode> nodes;
	/**
	 * create an empty state graph for the mutation under the program being tested
	 * @param cir_tree
	 * @param mutant
	 * @throws Exception
	 */
	public SecStateGraph(CirTree cir_tree, Mutant mutant) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant as null");
		else {
			this.cir_tree = cir_tree;
			this.mutant = mutant;
			this.nodes = new ArrayList<SecStateNode>();
		}
	}
	
	/* getters */
	/**
	 * @return C-intermediate representation on which the mutation is injected
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @return the mutation that the graph describes
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the mutation that the graph describes
	 */
	public AstMutation get_mutation() { return this.mutant.get_mutation(); }
	/**
	 * @return the number of nodes created in this graph
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * @return the set of nodes created in this graph
	 */
	public Iterable<SecStateNode> get_nodes() { return this.nodes; }
	/**
	 * @return whether the graph contains the node that reaches the faulty statement
	 */
	public boolean has_reach_node() { return !this.nodes.isEmpty(); }
	/**
	 * @return the node that describes reaching the faulty statement of the mutant
	 */
	public SecStateNode get_reach_node() { 
		if(this.nodes.isEmpty())
			return null;
		else
			return this.nodes.get(0);
	}
	
	/* setters */
	/**
	 * @param desc
	 * @return create a new node w.r.t. the description
	 * @throws Exception
	 */
	public SecStateNode new_node(SecDescription desc) throws Exception {
		SecStateNode node = new SecStateNode(this, desc);
		this.nodes.add(node);
		return node;
	}
	/**
	 * remove all the nodes from the graph
	 */
	public void clear() {
		for(SecStateNode node : this.nodes) {
			node.delete();
		}
		this.nodes.clear();
	}
	/**
	 * construct the path, reach-node and infection part
	 * @param dependence_graph
	 * @throws Exception
	 */
	public void initialize(CDependGraph dependence_graph) throws Exception {
		this.clear(); SecInfectionBuild.builder.build(this);
		SecPathFinder.finder.find_path(this, dependence_graph);
	}
	
}
