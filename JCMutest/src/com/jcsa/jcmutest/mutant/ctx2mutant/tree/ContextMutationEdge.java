package com.jcsa.jcmutest.mutant.ctx2mutant.tree;

public class ContextMutationEdge {
	
	/** the source node in contextual mutation tree **/
	private	ContextMutationNode source;
	/** the target node in contextual mutation tree **/
	private ContextMutationNode target;
	
	/**
	 * It creates an edge from source to target in the contextual mutation
	 * @param source
	 * @param target
	 * @throws IllegalArgumentException
	 */
	protected ContextMutationEdge(ContextMutationNode source,
			ContextMutationNode target) throws IllegalArgumentException {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else { this.source = source; this.target = target; }
	}
	
	/**
	 * @return the source node in contextual mutation tree
	 */
	public ContextMutationNode get_source() { return this.source; }
	
	/**
	 * @return the target node in contextual mutation tree
	 */
	public ContextMutationNode get_target() { return this.target; }
	
}
