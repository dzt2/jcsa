package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.prop;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public interface SecPropagation {
	
	/**
	 * @param statement the statement where the target is in
	 * @param location location to which the error propagate
	 * @param error source error that propagates to others
	 * @param propagations constraint-error pairs to preserve
	 * 		  propagation data from source error to targets
	 * @throws Exception
	 */
	public void propagate(CirStatement statement,
			CirNode location, SecStateError error,
			Collection<SecInfectPair> propagations) throws Exception;
	
}
