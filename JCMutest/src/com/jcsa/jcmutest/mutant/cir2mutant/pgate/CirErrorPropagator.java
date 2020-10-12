package com.jcsa.jcmutest.mutant.cir2mutant.pgate;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirStateError;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * It implements the error propagation through locations in C-intermediate
 * representation program.
 * 
 * @author yukimula
 *
 */
public interface CirErrorPropagator {
	
	/**
	 * @param cir_mutations the library used to create constraint & state errors
	 * @param error the source error that causes the other errors in the program
	 * @param source_location from which the source error influences on others
	 * @param target_location to which the new errors are generated from source
	 * @param propagations mapping from target errors to constraints required
	 * @throws Exception
	 */
	public void propagate(CirMutations cir_mutations, CirStateError error,
			CirNode source_location, CirNode target_location,
			Map<CirStateError, CirConstraint> propagations) throws Exception;
	
}
