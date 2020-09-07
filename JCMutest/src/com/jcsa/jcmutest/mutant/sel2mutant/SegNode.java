package com.jcsa.jcmutest.mutant.sel2mutant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.cons.SelConstraint;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.desc.SelDescription;

/**
 * The node contains all the errors and description on one location in C
 * intermediate representation.
 * <br>
 * @author yukimula
 *
 */
public class SegNode {
	
	/* definition */
	/** the graph where the node is created **/
	private SeGraph graph;
	/** the description that defines this node **/
	private SelDescription source_description;
	/** the set of errors extended from source **/
	private List<SelDescription> extension_set;
	/** the edges extended from others to this **/
	private List<SegEdge> in_edges;
	/** the edges extended from this to others **/
	private List<SegEdge> ou_edges;
	
	/* constructor */
	/**
	 * create an isolated node in graph w.r.t. the source description
	 * @param graph
	 * @param description
	 * @throws Exception
	 */
	protected SegNode(SeGraph graph, SelDescription description) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(description == null)
			throw new IllegalArgumentException("Invalid description");
		else {
			this.graph = graph;
			this.source_description = description;
			this.extension_set = new ArrayList<SelDescription>();
			this.in_edges = new LinkedList<SegEdge>();
			this.ou_edges = new LinkedList<SegEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the graph where the node is created
	 */
	public SeGraph get_graph() { return this.graph; }
	/**
	 * @return the description that defines this node in unique way
	 */
	public SelDescription get_description() { return this.source_description; }
	/**
	 * @return the set of descriptions implied from source description
	 */
	public Iterable<SelDescription> get_descriptions() { return this.extension_set; }
	/**
	 * @return the set of edges from others to generate this node
	 */
	public Iterable<SegEdge> get_in_edges() { return this.in_edges; }
	/**
	 * @return the set of edges to which this node is generated
	 */
	public Iterable<SegEdge> get_ou_edges() { return this.ou_edges; }
	
	/* setters */
	/**
	 * remove this node from the graph
	 */
	protected void delete() {
		this.graph = null;
		this.source_description = null;
		this.extension_set.clear();
		this.extension_set = null;
		this.in_edges.clear();
		this.ou_edges.clear();
		this.in_edges = null;
		this.ou_edges = null;
	}
	/**
	 * @param constraint
	 * @param target
	 * @return link this source to the target w.r.t. the constraint
	 * @throws Exception
	 */
	public SegEdge link(SelConstraint constraint, SegNode target) throws Exception {
		if(target == null)
			throw new IllegalArgumentException("Invalid target");
		else {
			for(SegEdge edge : this.ou_edges) {
				if(edge.get_target() == target) {
					return edge;
				}
			}
			SegEdge edge = new SegEdge(constraint, this, target);
			this.ou_edges.add(edge);
			target.in_edges.add(edge);
			return edge;
		}
	}
	
	/* visitors */
	
	
}
