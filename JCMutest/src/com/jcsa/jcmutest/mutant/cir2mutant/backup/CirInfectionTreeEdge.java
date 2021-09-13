package com.jcsa.jcmutest.mutant.cir2mutant.backup;

/**
 * The edge connecting from parent to its child node in CirInfectionTree.
 * 
 * @author yukimula
 *
 */
public class CirInfectionTreeEdge {
	
	/* attributes */
	private CirInfectionTreeFlow type;
	private CirInfectionTreeNode source;
	private CirInfectionTreeNode target;
	
	/* constructor */
	/**
	 * create an edge from source to target with specified edge_type
	 * @param edge_type
	 * @param source
	 * @param target
	 * @throws IllegalArgumentException
	 */
	protected CirInfectionTreeEdge(CirInfectionTreeFlow edge_type,
			CirInfectionTreeNode source,
			CirInfectionTreeNode target) throws IllegalArgumentException {
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
		if(this.type == CirInfectionTreeFlow.execution) {
			if(this.source.get_type() == CirInfectionTreeType.pre_condition) {
				if(this.target.get_type() == CirInfectionTreeType.pre_condition
					|| this.target.get_type() == CirInfectionTreeType.mid_condition) {
					return;		/* pass through the validation */
				}
			}
			throw new IllegalArgumentException(this.source.get_type() + " --> " + this.target.get_type());
		}
		else if(this.type == CirInfectionTreeFlow.infection) {
			if(this.source.get_type() == CirInfectionTreeType.mid_condition
				&& this.target.get_type() == CirInfectionTreeType.mid_condition) {
				return;			/* pass through the validation */
			}
			throw new IllegalArgumentException(this.source.get_type() + " --> " + this.target.get_type());
		}
		else if(this.type == CirInfectionTreeFlow.propagate) {
			if(this.source.get_type() == CirInfectionTreeType.mid_condition
				|| this.source.get_type() == CirInfectionTreeType.nex_condition) {
				if(this.target.get_type() == CirInfectionTreeType.nex_condition) {
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
	public CirInfectionTreeFlow get_type() { return this.type; }
	/**
	 * @return the node from which the edge points to target
	 */
	public CirInfectionTreeNode get_source() { return this.source; }
	/**
	 * @return the node to which the edge points from source
	 */
	public CirInfectionTreeNode get_target() { return this.target; }
	
	
}
