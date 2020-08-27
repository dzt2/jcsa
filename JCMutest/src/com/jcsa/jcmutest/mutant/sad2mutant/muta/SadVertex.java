package com.jcsa.jcmutest.mutant.sad2mutant.muta;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * Each vertex in cause-effect graph describes a requirement that 
 * needs to be hold in testing such that a test requirement is met.
 * @author yukimula
 *
 */
public class SadVertex {
	
	/* definition */
	/** cause-effect graph to create this node **/
	private SadGraph graph;
	/** the assertion expected to be met in this node **/
	private SadAssertion assertion;
	/** the cause-effect relations to this node as the effect **/
	private List<SadRelation> in;
	/** the cause-effect relations from this node as the cause **/
	private List<SadRelation> ou;
	/**
	 * create an isolated node w.r.t. the symbolic assertion
	 * @param graph
	 * @param assertion
	 * @throws Exception
	 */
	protected SadVertex(SadGraph graph, SadAssertion assertion) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(assertion == null)
			throw new IllegalArgumentException("Invalid assertion: null");
		else {
			this.graph = graph;
			this.assertion = assertion;
			this.in = new LinkedList<SadRelation>();
			this.ou = new LinkedList<SadRelation>();
		}
	}
	
	/* getters */
	/**
	 * @return cause-effect graph to create this node
	 */
	public SadGraph get_graph() {
		return this.graph;
	}
	/**
	 * @return the assertion expected to be met in this node
	 */
	public SadAssertion get_assertion() {
		return this.assertion;
	}
	/**
	 * @return the loocation where the assertion of the node is taken
	 */
	public CirStatement get_location() {
		return (CirStatement) this.assertion.get_location().get_cir_source();
	}
	/**
	 * @return the cause-effect relations to this node as the effect
	 */
	public Iterable<SadRelation> get_in_relations() {
		return this.in;
	}
	/**
	 * @return the cause-effect relations from this node as the cause
	 */
	public Iterable<SadRelation> get_ou_relations() {
		return this.ou;
	}
	/**
	 * @param constraint
	 * @param target
	 * @return connect the source with the target w.r.t. the constraint provided
	 * @throws Exception
	 */
	public SadRelation link(SadAssertion constraint, SadVertex target) throws Exception {
		for(SadRelation relation : this.ou) {
			if(relation.get_target() == target) {
				return relation;
			}
		}
		SadRelation relation = new SadRelation(constraint, this, target);
		this.ou.add(relation);
		target.in.add(relation);
		return relation;
	}
	/**
	 * remove this vertex from the graph
	 */
	protected void delete() {
		this.graph = null;
		this.assertion = null;
		this.in = null;
		this.ou = null;
	}
	
}
