package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.Collection;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstance;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;


/**
 * It maintains the abstract state hold at each point in the execution path for killing a mutant,
 * with respect to a set of SymInstance and CirAnnotation.
 * @author yukimula
 *
 */
public class MutantKillingState {
	
	/* definitions */
	/** the mutant-killing-path as the basis of the state **/
	private MutantKillingPath path;
	/** the index of the execution flow to which it refers **/
	private CirExecutionEdge edge;
	/** collection of constraints or state errors recorded **/
	private Collection<SymInstance> instances;
	/** collection of annotations to abstract the instance **/
	private Collection<CirAnnotation> annotations;
	
	/* constructor */
	/**
	 * create an empty abstract state w.r.t. given flow in execution path as specified
	 * @param path
	 * @param index
	 * @throws Exception
	 */
	protected MutantKillingState(MutantKillingPath path, CirExecutionEdge edge) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path as null");
		else if(edge == null)
			throw new IllegalArgumentException("Invalid edge as null");
		else {
			this.path = path;
			this.edge = edge;
			this.instances = new ArrayList<SymInstance>();
			this.annotations = new ArrayList<CirAnnotation>();
		}
	}
	
	/* getters */
	/**
	 * @return the mutant-killing-path as the basis of the state
	 */
	public MutantKillingPath get_path() { return this.path; }
	/**
	 * @return the execution edge in path where it correspond to
	 * @throws Exception
	 */
	public CirExecutionEdge get_edge() throws Exception { return this.edge; }
	/**
	 * @return collection of constraints or state errors recorded
	 */
	public Iterable<SymInstance> get_instances() { return this.instances; }
	/**
	 * @return collection of annotations to abstract the instance
	 */
	public Iterable<CirAnnotation> get_annotations() { return this.annotations; }
	
	/**
	 * add instance or annotation into the state
	 * @param value
	 */
	public void add(Object value) {
		if(value != null) {
			if(value instanceof SymInstance) {
				if(!this.instances.contains(value))
					this.instances.add((SymInstance) value);
			}
			else if(value instanceof CirAnnotation) {
				if(!this.annotations.contains(value))
					this.annotations.add((CirAnnotation) value);
			}
		}
	}
	/**
	 * remove all the instances and annotations in the state
	 */
	public void clear() { this.instances.clear(); this.annotations.clear(); }
	
}
