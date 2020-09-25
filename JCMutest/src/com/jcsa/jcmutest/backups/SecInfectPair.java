package com.jcsa.jcmutest.backups;

/**
 * [constraint, init_error] as a basic branch in infection module.
 * @author yukimula
 *
 */
public class SecInfectPair {
	
	private SecConstraint constraint;
	private SecStateError init_error;
	public SecInfectPair(SecConstraint constraint, SecStateError init_error) throws Exception { 
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint");
		else if(init_error == null)
			throw new IllegalArgumentException("Invalid init-error");
		else {
			this.constraint = constraint;
			this.init_error = init_error;
		}
	}
	
	/**
	 * @return constraint that are required for infecting initial error
	 */
	public SecConstraint get_constraint() { return this.constraint; }
	
	/**
	 * @return initial error infected when the constraint is satisfied
	 */
	public SecStateError get_init_error() { return this.init_error; }
	
}
