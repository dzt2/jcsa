package com.jcsa.jcmutest.mutant.sel2mutant.apis;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.cons.SelConstraint;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.cons.SelExecutionConstraint;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.desc.SelDescription;

public interface SegInfectGenerator {
	
	/**
	 * generate the infection module from the mutation and
	 * the reach node as it gives
	 * @param mutation the mutation that requires the infection
	 * @param reach_node the node that reaches the faulty statement
	 * @param constraints the constraints required for infecting program
	 * @param init_errors the initial errors caused when constraints met
	 * @throws Exception
	 */
	public void infect(AstMutation mutation, 
			SelExecutionConstraint reach_node,
			List<SelConstraint> constraints,
			List<SelDescription> init_errors) throws Exception;
	
}
