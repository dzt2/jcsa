package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * The edge in the CirMutationTree to link a parent to a child
 * @author yukimula
 *
 */
public class CirMutationTreeEdge {
	
	/* attributes */
	private CirMutationTreeFlow type;
	private CirMutationTreeNode	source;
	private CirMutationTreeNode target;
	
	/* constructor */
	/**
	 * create an edge from source to target using specified type
	 * @param type
	 * @param source
	 * @param target
	 * @throws IllegalArgumentException
	 */
	protected CirMutationTreeEdge(CirMutationTreeFlow type, CirMutationTreeNode 
			source, CirMutationTreeNode target) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type as null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else { this.type = type; this.source = source; this.target = target; }
	}
	
	/* getters */
	/**
	 * @return the type of the edge to link a parent to its child
	 */
	public CirMutationTreeFlow get_edge_type() { return this.type; }
	/**
	 * @return the parent or source node being connected to target
	 */
	public CirMutationTreeNode get_source() { return this.source; }
	/**
	 * @return the child or target node being linked from parent
	 */
	public CirMutationTreeNode get_target() { return this.target; }
	
}
