package com.jcsa.jcmutest.mutant.cir2mutant.trees;

/**
 * It represents an edge from parent to child in CirInfectionEdge, which defines 
 * the relationships between CirInfectionNode.
 * 
 * @author yukimula
 *
 */
public class CirInfectionEdge {
	
	/* attributes */
	/** type of the edge using RIP process frameworks **/	
	private CirInfectionEdgeType type;
	/** the node from which the edge points to target **/
	private CirInfectionNode	source;
	/** the node to which the edge points from source **/
	private CirInfectionNode	target;
	
	/* constructor */
	/**
	 * create an edge from source to target with specified edge_type
	 * @param edge_type
	 * @param source
	 * @param target
	 * @throws IllegalArgumentException
	 */
	protected CirInfectionEdge(CirInfectionEdgeType edge_type,
			CirInfectionNode source,
			CirInfectionNode target) throws IllegalArgumentException {
		if(edge_type == null) {
			throw new IllegalArgumentException("Invalid edge_type: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target as null");
		}
		else {
			this.type = edge_type;
			this.source = source;
			this.target = target;
			this.validate_edge_type();
		}
	}
	/**
	 * validate whether the edge_type is correct
	 * @throws IllegalArgumentException
	 */
	private void validate_edge_type() throws IllegalArgumentException {
		if(this.type == CirInfectionEdgeType.execution) {
			if(this.source.get_type() == CirInfectionNodeType.pre_condition) {
				if(this.target.get_type() == CirInfectionNodeType.pre_condition
					|| this.target.get_type() == CirInfectionNodeType.mid_condition) {
					return;		/* pass through the validation */
				}
			}
			throw new IllegalArgumentException(this.source.get_type() + " --> " + this.target.get_type());
		}
		else if(this.type == CirInfectionEdgeType.infection) {
			if(this.source.get_type() == CirInfectionNodeType.mid_condition
				&& this.target.get_type() == CirInfectionNodeType.mid_condition) {
				return;			/* pass through the validation */
			}
			throw new IllegalArgumentException(this.source.get_type() + " --> " + this.target.get_type());
		}
		else if(this.type == CirInfectionEdgeType.propagate) {
			if(this.source.get_type() == CirInfectionNodeType.mid_condition
				|| this.source.get_type() == CirInfectionNodeType.nex_condition) {
				if(this.target.get_type() == CirInfectionNodeType.nex_condition) {
					return;		/* pass through the validation */
				}
			}
			throw new IllegalArgumentException(this.source.get_type() + " --> " + this.target.get_type());
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + this.type);
		}
	}
	
	/* getters */
	/**
	 * @return type of the edge using RIP process frameworks
	 */
	public CirInfectionEdgeType get_type() { return this.type; }
	/**
	 * @return the node from which the edge points to target
	 */
	public CirInfectionNode get_source() { return this.source; }
	/**
	 * @return the node to which the edge points from source
	 */
	public CirInfectionNode get_target() { return this.target; }

}
