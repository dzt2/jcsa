package com.jcsa.jcmutest.mutant.sad2mutant.muta;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcparse.lang.irlang.CirTree;

public class SadGraph {
	
	/** the C-intermediate representation tree **/
	private CirTree tree;
	/** the mapping from assertion to node **/
	private Map<String, SadVertex> vertices;
	protected SadGraph(CirTree tree) throws Exception {
		this.tree = tree;
		this.vertices = new HashMap<String, SadVertex>();
	}
	
	/* getters */
	/**
	 * @return the tree of C-intermediate representation.
	 */ 
	public CirTree get_cir_tree() { return this.tree; }
	/**
	 * @return the number of nodes in the graph
	 */
	public int size() { return this.vertices.size(); }
	/**
	 * @return the set of nodes in the graph
	 */
	public Iterable<SadVertex> get_vertices() {
		return this.vertices.values();
	}
	/**
	 * @param assertion
	 * @return the node w.r.t. the symbolic assertion
	 * @throws Exception
	 */
	public SadVertex get_vertex(SadAssertion assertion) throws Exception {
		String key = assertion.toString();
		if(!this.vertices.containsKey(key)) {
			this.vertices.put(key, new SadVertex(this, assertion));
		}
		return this.vertices.get(key);
	}
	/**
	 * remove all the old nodes in the graph
	 */
	protected void clear() {
		for(SadVertex vertex : this.vertices.values()) {
			vertex.delete();
		}
		this.vertices.clear();
	}
	
}
