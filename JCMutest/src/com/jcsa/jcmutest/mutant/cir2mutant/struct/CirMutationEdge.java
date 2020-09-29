package com.jcsa.jcmutest.mutant.cir2mutant.struct;

/**
 * The edge links two nodes of cir-mutations in which the source is the 
 * cause while the target is the effect or consequence.
 * 
 * @author yukimula
 *
 */
public class CirMutationEdge {
	
	/* definitions */
	/** the type of the propagation edge **/
	private CirMutationFlow type;
	/** the node from which propagates to another **/
	private CirMutationNode source;
	/** the node to which propagates from another **/
	private CirMutationNode target;
	
	/* constructor */
	/**
	 * create a propagation edge from source to target with type
	 * @param type
	 * @param source
	 * @param target
	 * @throws IllegalArgumentException
	 */
	protected CirMutationEdge(CirMutationFlow type, CirMutationNode
			source, CirMutationNode target) throws IllegalArgumentException {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			this.type = type;
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return the type of the propagation from source to target
	 */
	public CirMutationFlow get_flow_type() { return this.type; }
	/**
	 * @return the source from which errors propagate to target
	 */
	public CirMutationNode get_source() { return this.source; }
	/**
	 * @return the target to which errors propagate from source
	 */
	public CirMutationNode get_target() { return this.target; }
	
}
