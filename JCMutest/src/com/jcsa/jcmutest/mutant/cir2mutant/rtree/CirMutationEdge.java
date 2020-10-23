package com.jcsa.jcmutest.mutant.cir2mutant.rtree;

import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;

/**
 * The edge connects two tree nodes in two node in error propagation graph.
 * 
 * @author yukimula
 *
 */
public class CirMutationEdge {
	
	/* definitions */
	/** the constraints required for the error propagation from source to
	 * 	the target in both different nodes of different statements. **/
	private Set<CirConstraint> constraints;
	/** the tree node as source node from which error propagate to target **/
	private CirMutationTreeNode source;
	/** the tree node as target node to which error propagate from source **/
	private CirMutationTreeNode target;
	/**
	 * create an error propagation edge between nodes in different statement.
	 * @param source
	 * @param target
	 * @param constraints
	 * @throws Exception
	 */
	protected CirMutationEdge(CirMutationTreeNode source, 
			CirMutationTreeNode target, Iterable<CirConstraint> constraints) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else if(constraints == null)
			throw new IllegalArgumentException("Invalid constraints.");
		else {
			this.source = source;
			this.target = target;
			this.constraints = new HashSet<CirConstraint>();
			for(CirConstraint constraint : constraints) {
				this.constraints.add(constraint);
			}
		}
	}
	
	/* getters */
	/**
	 * @return the tree node as source node from which error propagate to target
	 */
	public CirMutationTreeNode get_source() { return this.source; }
	/**
	 * @return the tree node as target node to which error propagate from source
	 */
	public CirMutationTreeNode get_target() { return this.target; }
	/**
	 * @return the constraints required for the error propagation from source to
	 * 	the target in both different nodes of different statements. 
	 */
	public Iterable<CirConstraint> get_constraints() { return this.constraints; }
	public CirMutationTree get_source_tree() { return this.source.get_tree(); }
	public CirMutationTree get_target_tree() { return this.target.get_tree(); }
	public CirMutationNode get_source_node() { return this.source.get_tree().get_statement_node(); }
	public CirMutationNode get_target_node() { return this.target.get_tree().get_statement_node(); }
	
}
