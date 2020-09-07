package com.jcsa.jcmutest.mutant.sel2mutant.apis;

import java.util.List;

import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public interface SegReachPathFinder {
	
	/**
	 * collect the flows that are necessary for reaching the end
	 * @param end
	 * @param flows
	 * @throws Exception
	 */
	public void find_path(CirStatement end, List<CirExecutionFlow> flows) throws Exception;
	
}
