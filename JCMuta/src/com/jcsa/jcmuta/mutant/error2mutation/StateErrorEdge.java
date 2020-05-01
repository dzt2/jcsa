package com.jcsa.jcmuta.mutant.error2mutation;

import com.jcsa.jcparse.lang.symb.StateConstraints;

/**
 * Error propagation relationship
 * @author yukimula
 *
 */
public class StateErrorEdge {
	
	/* attributes */
	/** error that causes another **/
	private StateErrorNode source;
	/** error that caused by another **/
	private StateErrorNode target;
	/** constraints required for causing propagation **/
	private StateConstraints constraints;
	
	/* constructor */
	/**
	 * create a propagation from source to target
	 * @param source
	 * @param target
	 * @throws IllegalArgumentException
	 */
	protected StateErrorEdge(StateErrorNode source, StateErrorNode target, StateConstraints constraints) throws IllegalArgumentException {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else if(constraints == null)
			throw new IllegalArgumentException("Invalid constraints");
		else {
			this.source = source; this.target = target;
			this.constraints = constraints;
		}
	}
	
	/* getters */
	/**
	 * get the error that causes another
	 * @return
	 */
	public StateErrorNode get_source() { return this.source; }
	/**
	 * get the errors caused by another
	 * @return
	 */
	public StateErrorNode get_target() { return this.target; }
	/**
	 * get the constraints for causing error propagation
	 * @return
	 */
	public StateConstraints get_constraints() { return this.constraints; }
	
}
