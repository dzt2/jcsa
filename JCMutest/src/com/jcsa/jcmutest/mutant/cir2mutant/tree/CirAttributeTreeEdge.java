package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * It connects from the source attribute to the child attribute in the 
 * sequence of a symbolic execution of killing a mutation.
 * 
 * @author yukimula
 *
 */
public class CirAttributeTreeEdge {
	
	private CirAttributeTreeType type;
	private CirAttributeTreeNode source;
	private CirAttributeTreeNode target;
	protected CirAttributeTreeEdge(CirAttributeTreeType type,
			CirAttributeTreeNode source,
			CirAttributeTreeNode target) throws IllegalArgumentException {
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
			this.type = type; this.source = source; this.target = target;
		}
	}
	
	/* getters */
	/**
	 * @return the type of the edge to link from attribute parent to its child
	 */
	public CirAttributeTreeType get_type() { return this.type; }
	/**
	 * @return the source attribute linked from this edge to this child
	 */
	public CirAttributeTreeNode get_source() { return this.source; }
	/**
	 * @return the target attribute linked from its parent to the child
	 */
	public CirAttributeTreeNode get_target() { return this.target; }
	
}
