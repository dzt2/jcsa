package com.jcsa.jcparse.flwa.influence;

/**
 * The edge represents the influence relationship between program elements, which can be an expression
 * or statement such that the source directly influences on the target (or target relies on the source).
 * 
 * @author yukimula
 *
 */
public class CInfluenceEdge {
	
	/* properties */
	/** the type of influence **/
	private CInfluenceEdgeType type;
	/** the program element that influences on the target **/
	private CInfluenceNode source;
	/** the program element that relies on the source **/
	private CInfluenceNode target;
	
	/* constructor */
	/**
	 * create an influence edge from source to target with specified type such that
	 * the source directly influences on the target.
	 * @param type
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected CInfluenceEdge(CInfluenceEdgeType type, 
			CInfluenceNode source, CInfluenceNode target) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			this.type = type; this.source = source; this.target = target;
		}
	}
	
	/* getters */
	/**
	 * get the type of the influence
	 * @return
	 */
	public CInfluenceEdgeType get_type() { return this.type; }
	/**
	 * get the node directly influences on another
	 * @return
	 */
	public CInfluenceNode get_source() { return this.source; }
	/**
	 * get the node directly influenced by another (or depends on)
	 * @return
	 */
	public CInfluenceNode get_target() { return this.target; }
	
}
