package com.jcsa.jcmutest.mutant.sel2mutant.apis;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public interface SegReachNodeFinder {
	
	/**
	 * @param mutation
	 * @return find the statement where the mutation is executed, used as 
	 * 		   to create the reach-node in SeGraph
	 * @throws Exception
	 */
	public CirStatement locate_mutation(AstMutation mutation) throws Exception;
	
}
