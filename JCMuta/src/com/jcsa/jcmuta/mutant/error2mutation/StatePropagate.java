package com.jcsa.jcmuta.mutant.error2mutation;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * Used to propagate the state errors in program under test.
 * @author yukimula
 *
 */
public abstract class StatePropagate {
	
	public StatePropagate() { }
	
	/**
	 * Propagate from the source to the target w.r.t. the given location
	 * @param source
	 * @param cir_target
	 * @return
	 * @throws Exception
	 */
	public abstract StateErrorNode propagate(StateErrorNode source, CirNode cir_target) throws Exception;
	
}
