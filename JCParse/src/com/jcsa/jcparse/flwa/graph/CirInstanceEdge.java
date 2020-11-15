package com.jcsa.jcparse.flwa.graph;

import com.jcsa.jcparse.flwa.CirInstance;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;

public class CirInstanceEdge extends CirInstance {
	
	protected CirInstanceNode source;
	protected CirInstanceNode target;
	/**
	 * create an edge from the source to the target with respect to the 
	 * program flow in the specific context being described
	 * @param source
	 * @param target
	 * @param context
	 * @param element
	 * @throws Exception
	 */
	protected CirInstanceEdge(CirInstanceNode source, CirInstanceNode target, 
			Object context, CirExecutionFlow element) throws Exception {
		super(source.get_graph(), context, element);
		this.source = source; this.target = target;
	}
	/**
	 * create an edge from the source to the target with respect to an
	 * invalid execution flow that specified with its flow type.
	 * @param source
	 * @param target
	 * @param context
	 * @param flow_type
	 * @throws Exception
	 */
	protected CirInstanceEdge(CirInstanceNode source, CirInstanceNode target,
			Object context, CirExecutionFlowType flow_type) throws Exception {
		super(source.get_graph(), context, CirExecutionFlow.virtual_flow(
				flow_type, source.get_execution(), target.get_execution()));
		this.source = source; this.target = target;
	}
	
	/**
	 * get the program flow it represents
	 * @return null if it is virtual
	 */
	public CirExecutionFlow get_flow() {
		Object element = this.get_element();
		if(element instanceof CirExecutionFlow)
			return (CirExecutionFlow) element;
		else return null;
	}
	/**
	 * get the type of the execution flow
	 * @return
	 */
	public CirExecutionFlowType get_type() {
		Object element = this.get_element();
		if(element instanceof CirExecutionFlow)
			return ((CirExecutionFlow) element).get_type();
		else return (CirExecutionFlowType) element;
	}
	/**
	 * get the node from which the edge points
	 * @return
	 */
	public CirInstanceNode get_source() {
		return this.source;
	}
	/**
	 * get the node to which this edge refers
	 * @return
	 */
	public CirInstanceNode get_target() {
		return this.target;
	}

	@Override
	public boolean is_virtual() {
		return !this.get_flow().is_valid_flow();
	}
	
}
