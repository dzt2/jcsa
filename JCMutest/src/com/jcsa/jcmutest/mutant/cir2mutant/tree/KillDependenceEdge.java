package com.jcsa.jcmutest.mutant.cir2mutant.tree;

/**
 * The dependence edge in killable dependence graph. 
 * 
 * @author yukimula
 *
 */
public class KillDependenceEdge {
	
	/* definitions */
	/** the type of the dependence edge **/
	private KillDependenceType type;
	/** the condition depends on target **/
	private KillDependenceNode source;
	/** the condition depends by source **/
	private KillDependenceNode target;
	
	/* constructor */
	/**
	 * @param type		the type of the dependence edge
	 * @param source	the condition depends on target
	 * @param target	the condition depends by source 
	 * @throws IllegalArgumentException
	 */
	protected KillDependenceEdge(KillDependenceType type, KillDependenceNode 
			source, KillDependenceNode target) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid edge_type: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source as: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target as: null");
		}
		else { this.type = type; this.source = source; this.target = target; }
	}
	
	/* getters */
	/**
	 * @return the type of the dependence edge
	 */
	public KillDependenceType get_type() { return this.type; }
	/**
	 * @return the condition depends on target
	 */
	public KillDependenceNode get_source() { return this.source; }
	/**
	 * @return the condition depends by source 
	 */
	public KillDependenceNode get_target() { return this.target; }
	
}
