package com.jcsa.jcmutest.mutant.sel2mutant.apis;

import java.util.List;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.cons.SelConstraint;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.desc.SelDescription;

public interface SegExternExtender {
	
	/**
	 * create the constraints and errors that will be influenced by the prev-errors.
	 * @param prev_errors
	 * @param constraints
	 * @param next_errors
	 * @throws Exception
	 */
	public void propagate(Iterable<SelDescription> prev_errors, 
			List<SelConstraint> constraints, 
			List<SelDescription> next_errors) throws Exception;
	
}
