package com.jcsa.jcmutest.mutant.cir2mutant.stat.tree;

/**
 * The edge that connects from parent to child in CirMutationTree.
 * 
 * @author yukimula
 *
 */
public class CirMutationTreeEdge {
	
	/* definitions */
	private CirMutationTreeFlow edge_type;
	private CirMutationTreeNode	source, target;
	
	/* constructor */
	/**
	 * It creates an edge linking from source to target with specified edge type
	 * @param edge_type
	 * @param source
	 * @param target
	 * @throws IllegalArgumentException
	 */
	protected CirMutationTreeEdge(CirMutationTreeFlow edge_type, 
			CirMutationTreeNode source, 
			CirMutationTreeNode target) throws IllegalArgumentException {
		if(edge_type == null) {
			throw new IllegalArgumentException("Invalid edge_type: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source as: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target as: null");
		}
		else {
			this.edge_type = edge_type; 
			this.source = source; 
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return the type of the edge linking two CirMutationTreeNode
	 */
	public CirMutationTreeFlow get_edge_type() { return this.edge_type; }
	/**
	 * @return parent that occurs prior to its child as pre_condition
	 */
	public CirMutationTreeNode get_source() { return this.source; }
	/**
	 * @return child which occurs next to its parent as pos_condition
	 */
	public CirMutationTreeNode get_target() { return this.target; }
	
}
