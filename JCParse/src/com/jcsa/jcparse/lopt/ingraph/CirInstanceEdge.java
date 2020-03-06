package com.jcsa.jcparse.lopt.ingraph;

import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lopt.CirInstance;

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
	 * create a virtual edge from the source to the target with respect to
	 * the program flow type it imagine with.
	 * @param source
	 * @param target
	 * @param context
	 * @param element
	 * @throws Exception
	 */
	protected CirInstanceEdge(CirInstanceNode source, CirInstanceNode target, 
			Object context, CirExecutionFlowType element) throws Exception {
		super(source.get_graph(), context, element);
		this.source = source; this.target = target;
	}
	
	@Override
	public boolean is_virtual() { return this.get_element() instanceof CirExecutionFlowType; }
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
	
}
