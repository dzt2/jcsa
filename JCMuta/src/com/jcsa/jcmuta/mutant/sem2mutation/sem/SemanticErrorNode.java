package com.jcsa.jcmuta.mutant.sem2mutation.sem;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SemanticErrorNode {
	
	/* constructor */
	private SemanticErrorGraph graph;
	private int id;
	private List<SemanticAssertion> assertions;
	private List<SemanticErrorEdge> in, ou;
	protected SemanticErrorNode(SemanticErrorGraph graph, int id,
			Iterable<SemanticAssertion> error_assertions) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(error_assertions == null)
			throw new IllegalArgumentException("Invalid assertions");
		else {
			this.graph = graph; this.id = id;
			this.assertions = new ArrayList<SemanticAssertion>();
			for(SemanticAssertion error_assertion : error_assertions) {
				if(error_assertion.is_state_error()) this.assertions.add(error_assertion);
				else throw new IllegalArgumentException("Invalid error assertion as null");
			}
			this.in = new ArrayList<SemanticErrorEdge>();
			this.ou = new ArrayList<SemanticErrorEdge>();
		}
	}
	
	/* getters */
	/**
	 * get the graph of semantic errors
	 * @return
	 */
	public SemanticErrorGraph get_graph() { return this.graph; }
	/**
	 * get the integer ID of the node in the graph
	 * @return
	 */
	public int get_id() { return this.id; }
	/**
	 * get the assertions describing this error node
	 * @return
	 */
	public Iterable<SemanticAssertion> get_assertions() { return this.assertions; }
	/**
	 * get the location where the error occurs
	 * @return
	 */
	public CirNode get_location() {
		if(this.assertions.isEmpty()) return null;
		else return this.assertions.get(0).get_location();
	}
	/**
	 * whether the error node is empty without impacts on any program point
	 * @return
	 */
	public boolean is_empty() { return this.assertions.isEmpty(); }
	/**
	 * get the edges generate this error
	 * @return
	 */
	public Iterable<SemanticErrorEdge> get_in_edges() { return in; }
	/**
	 * get the edges that this error generates others
	 * @return
	 */
	public Iterable<SemanticErrorEdge> get_ou_edges() { return ou; }
	/**
	 * link this node to the target with respect to the propagation constraints
	 * @param target
	 * @param constraint_assertions
	 * @return
	 * @throws Exception
	 */
	protected SemanticErrorEdge link_to(SemanticErrorNode target, 
			Iterable<SemanticAssertion> constraint_assertions) throws Exception {
		/* get the existing edge if there is */
		for(SemanticErrorEdge edge : this.ou) 
			if(edge.get_target() == target) return edge;
		
		/* create a new edge from this node to target */
		SemanticErrorEdge edge = new SemanticErrorEdge(
					constraint_assertions, this, target);
		this.ou.add(edge); target.in.add(edge); return edge;
	}
	/**
	 * extend the assertions in this error node
	 * @throws Exception
	 */
	protected void extend() throws Exception {
		this.assertions = SemanticErrorExtension.extend(this.assertions);
	}
	
}
