package com.jcsa.jcmutest.mutant.sym2mutant.base;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

public abstract class SymStateError extends SymInstance {
	
	/** the location in CIR code where the state error occurs. **/
	private CirNode location;
	
	/**
	 * @param type
	 * @param execution
	 * @param location
	 * @throws IllegalArgumentException
	 */
	protected SymStateError(SymInstanceType type, CirExecution execution, CirNode location) throws IllegalArgumentException {
		super(type, execution);
		if(location == null)
			throw new IllegalArgumentException("Invalid location as null");
		else
			this.location = location;
	}
	
	/**
	 * @return the location in CIR code where the error occurs
	 */
	public CirNode get_location() { return this.location; } 
	
}
