package com.jcsa.jcmutest.mutant.cir2mutant.graph;

public class CirMutationEdge {
	
	/* definitions */
	/** the type of the error propagation edge **/
	private CirMutationFlow edge_type;
	/** the tree node (as leaf) from which the error propagates to another **/
	private CirMutationTreeNode source;
	/** the tree node (as root) to which the error propagates from another **/
	private CirMutationTreeNode target;
	
	/* constructor */
	/**
	 * create the error propagation edge from source to target with specified type
	 * @param type
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected CirMutationEdge(CirMutationFlow type, 
			CirMutationTreeNode source, 
			CirMutationTreeNode target) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(source == null || !source.is_leaf())
			throw new IllegalArgumentException("Invalid source: " + source.get_cir_mutation().get_state_error());
		else if(target == null || !target.is_root())
			throw new IllegalArgumentException("Invalid target: null");
		else {
			this.edge_type = type;
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return the type of the error propagation edge
	 */
	public CirMutationFlow get_type() { return this.edge_type; }
	/**
	 * @return the tree node (as leaf) from which the error propagates to another
	 */
	public CirMutationTreeNode get_source() { return this.source; }
	/**
	 * @return the tree node (as root) to which the error propagates from another 
	 */
	public CirMutationTreeNode get_target() { return this.target; }
	
}
