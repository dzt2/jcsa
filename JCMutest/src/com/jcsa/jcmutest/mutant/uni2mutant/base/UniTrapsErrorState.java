package com.jcsa.jcmutest.mutant.uni2mutant.base;


/**
 * 	trp_stmt(statement; exception, exception)
 * 	
 * 	@author yukimula
 *
 */
public class UniTrapsErrorState extends UniPathErrorState {
	
	/**
	 * @param _store	the statement-location to throw exception
	 * @throws Exception
	 */
	protected UniTrapsErrorState(UniAbstractStore _store) throws Exception {
		super(UniAbstractClass.trp_stmt, _store, 
				UniAbstractStates.trap_value, UniAbstractStates.trap_value);
	}

}
