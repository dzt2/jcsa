package com.jcsa.jcmutest.mutant.sta2mutant.base;

/**
 * 	The dependence edge connecting AstContextNode in AstContextTree.
 * 	
 * 	@author yukimula
 *
 */
public class AstContextEdge {
	
	/** the type of the edge **/
	private	AstContextEdgeType 	type;
	/** the source from which the edge points **/
	private	AstContextNode	source;
	/** the target to which the edge points **/
	private	AstContextNode	target;
	
	/**
	 * It creates an edge from source to the target using given type
	 * @param type
	 * @param source
	 * @param target
	 * @throws IllegalArgumentException
	 */
	protected AstContextEdge(AstContextEdgeType type, AstContextNode source, 
			AstContextNode target) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
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
	
	/**
	 * @return the type of the edge
	 */
	public AstContextEdgeType get_type() { return this.type; }
	/**
	 * @return the source from which the edge points
	 */
	public AstContextNode get_source() { return this.source; }
	/**
	 * @return the target to which the edge points 
	 */
	public AstContextNode get_target() { return this.target; } 
	
}
