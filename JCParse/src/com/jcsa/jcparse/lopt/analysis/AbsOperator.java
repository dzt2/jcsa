package com.jcsa.jcparse.lopt.analysis;

import com.jcsa.jcparse.lopt.ingraph.CirInstanceEdge;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

/**
 * The operator is used to compute and update program state over the abstract value layer.
 * @author yukimula
 *
 */
public interface AbsOperator {
	
	/**
	 * Get the abstract representation of the initial state hold at statement in analysis
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	public AbsValue initial_value(CirInstanceNode exec_instance) throws Exception;
	
	/**
	 * Get the abstract representation of the initial state hold at the flow in analysis
	 * @param flow_instance
	 * @return
	 * @throws Exception
	 */
	public AbsValue initial_value(CirInstanceEdge flow_instance) throws Exception;
	
	/**
	 * Update the value of the flow based on the value hold by its source node (in forward
	 * data flow analysis) or its target node (in backward analysis).
	 * @param exec_instance
	 * @param forward whether it is used in forward (or backward) data flow analysis
	 * @return
	 * @throws Exception
	 */
	public AbsValue update_value(CirInstanceNode exec_instance, boolean forward) throws Exception;
	
	/**
	 * Update the value of the statement based on its input flows (in forward data flow
	 * analysis) or its output flows (in backward data flow analysis).
	 * @param flow_instance
	 * @param forward
	 * @return
	 * @throws Exception
	 */
	public AbsValue update_value(CirInstanceEdge flow_instance, boolean forward) throws Exception;
	
}
