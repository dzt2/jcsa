package com.jcsa.jcmuta.mutant.sem2mutation.sem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticInference;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutation;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SemanticErrorGraph {
	
	/* constructor */
	private SemanticMutation mutation;
	private List<SemanticErrorNode> nodes;
	protected SemanticErrorGraph(SemanticMutation mutation) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			this.mutation = mutation;
			this.nodes = new ArrayList<SemanticErrorNode>();
			this.initialize();
		}
	}
	private void initialize() throws Exception {
		SemanticErrorNode mutant_node = this.new_node();
		if(mutation.number_of_infections() > 0) {
			for(SemanticInference inference : mutation.get_infections()) {
				List<SemanticErrorNode> error_nodes = this.new_nodes(inference.get_post_conditions());
				for(SemanticErrorNode error_node : error_nodes) {
					mutant_node.link_to(error_node, inference.get_prev_conditions());
				}
			}
		}
		else {
			SemanticErrorNode error_node = this.new_node();
			List<SemanticAssertion> constraint_assertions = new ArrayList<SemanticAssertion>();
			constraint_assertions.add(mutation.get_reachability());
			mutant_node.link_to(error_node, constraint_assertions);
		}
	}
	
	/* creator */
	private SemanticErrorNode new_node() throws Exception {
		SemanticErrorNode node = new SemanticErrorNode(this, 
				nodes.size(), new ArrayList<SemanticAssertion>());
		this.nodes.add(node); return node;
	}
	protected List<SemanticErrorNode> new_nodes(Iterable<SemanticAssertion> assertions) throws Exception {
		if(assertions == null)
			throw new IllegalArgumentException("Invalid assertions: null");
		else {
			Map<CirNode, List<SemanticAssertion>> map = 
					new HashMap<CirNode, List<SemanticAssertion>>();
			
			for(SemanticAssertion assertion : assertions) {
				if(assertion.is_state_error()) {
					CirNode location = assertion.get_location();
					if(!map.containsKey(location)) {
						map.put(location, new ArrayList<SemanticAssertion>());
					}
					map.get(location).add(assertion);
				}
				else {
					throw new IllegalArgumentException("Invalid assertion");
				}
			}
			
			List<SemanticErrorNode> error_nodes = new ArrayList<SemanticErrorNode>();
			for(CirNode location : map.keySet()) {
				SemanticErrorNode node = new SemanticErrorNode(this, nodes.size(), map.get(location));
				if(!node.is_empty()) { error_nodes.add(node); this.nodes.add(node); }
			}
			return error_nodes;
		}
	}
	
	/* getters */
	/**
	 * get the mutation that the graph describes as its feature
	 * @return
	 */
	public SemanticMutation get_mutation() { return this.mutation; }
	/**
	 * get the number of state error nodes in the graph
	 * @return
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * get the empty error node entry representing the mutation itself
	 * @return
	 */
	public SemanticErrorNode get_entry() { return this.nodes.get(0); }
	/**
	 * get the edge from the entry to the direct error points
	 * @return
	 */
	public Iterable<SemanticErrorEdge> get_infection_edges() { 
		return this.nodes.get(0).get_ou_edges(); 
	}
	/**
	 * get all the nodes within the semantic error graph
	 * @return
	 */
	public Iterable<SemanticErrorNode> get_nodes() { return this.nodes; }
	
}
