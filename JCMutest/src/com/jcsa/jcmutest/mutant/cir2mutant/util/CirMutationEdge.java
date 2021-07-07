package com.jcsa.jcmutest.mutant.cir2mutant.util;

public class CirMutationEdge {
	
	/* definitions */
	/** the type of mutation edge **/
	private CirMutationType type;
	/** the node depending on the target **/
	private CirMutationNode source;
	/** the node that source depends on **/
	private CirMutationNode target;
	
	/* constructor */
	/**
	 * @param type
	 * @param source
	 * @param target
	 * @throws IllegalArgumentException
	 */
	protected CirMutationEdge(CirMutationType type, CirMutationNode source, 
				CirMutationNode target) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type as null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			this.type = type;
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return the type of mutation edge
	 */
	public CirMutationType get_type() { return this.type; }
	/**
	 * @return the node depending on the target
	 */
	public CirMutationNode get_source() { return this.source; }
	/**
	 * @return the node that source depends on
	 */
	public CirMutationNode get_target() { return this.target; }
	
}
