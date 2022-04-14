package com.jcsa.jcparse.lang.program;

import com.jcsa.jcparse.lang.program.types.AstCirEdgeType;

/**
 * 	It represents the dependence edge from AstCirNode to AstCirNode.
 * 	
 * 	@author yukimula
 *
 */
public class AstCirEdge {
	
	/* definitions */
	/** the type of the dependence edge **/
	private	AstCirEdgeType	type;
	/** the source node from which the edge points **/
	private	AstCirNode		source;
	/** the target node to which the edge points **/
	private	AstCirNode		target;
	
	/* constructor */
	/**
	 * It creates a dependence edge from source to target with given type
	 * @param type
	 * @param source
	 * @param target
	 * @throws IllegalArgumentException
	 */
	protected	AstCirEdge(AstCirEdgeType type, AstCirNode source, 
			AstCirNode target) throws IllegalArgumentException {
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
	
	/* getters */
	/**
	 * @return the type of the dependence edge
	 */
	public 	AstCirEdgeType	get_type()		{ return this.type; }
	/**
	 * @return the source node from which the edge points
	 */
	public	AstCirNode		get_source()	{ return this.source; }
	/**
	 * @return the target node to which the edge points
	 */
	public	AstCirNode		get_target()	{ return this.target; }
	
}
