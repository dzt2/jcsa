package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * The edge from parent (source) to child (target)
 * @author yukimula
 *
 */
public class CirMutationEdge {
	
	/* attributes */
	private CirMutationFlow flow;
	private CirMutationNode source;
	private CirMutationNode target;
	
	/* constructor */
	/**
	 * @param flow		the type of the cir-mutation tree edge
	 * @param source	the parent 
	 * @param target	the child
	 * @throws IllegalArgumentException
	 */
	protected CirMutationEdge(CirMutationFlow flow, CirMutationNode source,
				CirMutationNode target) throws IllegalArgumentException {
		if(flow == null) {
			throw new IllegalArgumentException("Invalid flow: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			this.flow = flow;
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return the type of the cir-mutation tree edge
	 */
	public CirMutationFlow get_flow() { return this.flow; }
	/**
	 * @return the parent
	 */
	public CirMutationNode get_source() { return this.source; }
	/**
	 * @return the child
	 */
	public CirMutationNode get_target() { return this.target; }
	
}
