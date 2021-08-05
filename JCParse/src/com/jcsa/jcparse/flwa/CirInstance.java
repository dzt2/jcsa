package com.jcsa.jcparse.flwa;

import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;

/**
 * The execution instance of a statement or its flow can be defined based on following tuple:<br>
 * <code>(context, element, state, graph)</code><br>
 * in which:<br>
 * (1) <i>context</i>: it specifies the context in which the element of statement or flow is executed
 * 		used to distinguish the instances referring to the same element in different contexts.<br>
 * (2) <i>element</i>: the element that the instance refers to as in the data flow analysis.<br>
 * (3) <i>state</i>: the state hold by the instance during the execution of the program in analysis.<br>
 * (4) <i>graph</i>: the directed graph where the instance is created as a node or flow.<br>
 * @author yukimula
 *
 */
public abstract class CirInstance {

	/* properties  */
	/** the directed graph where the instance is created **/
	private CirInstanceGraph graph;
	/** the context where the instance is defined when it refers to some element in program **/
	private Object context;
	/** the program element, either a statement execution or flow, that the instance represents **/
	private Object element;
	/** the state hold by the instance of statement or flow that will be used during analysis **/
	private Object state;

	/* constructor */
	/**
	 * create the instance of statement or flow in the graph with respect to the program element
	 * in the specified context, of which state is initialized as null.
	 * @param graph
	 * @param context
	 * @param element
	 * @throws Exception
	 */
	protected CirInstance(CirInstanceGraph graph, Object context, Object element) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("invalid graph: null");
		else if(context == null)
			throw new IllegalArgumentException("invalid context: null");
		else if(element == null)
			throw new IllegalArgumentException("invalid element: null");
		else {
			this.graph = graph; this.context = context;
			this.element = element; this.state = null;
		}
	}

	/* getters */
	/**
	 * get the directed graph where the instance of node or flow is created
	 * @return
	 */
	public CirInstanceGraph get_graph() { return this.graph; }
	/**
	 * get the context of the program element being defined
	 * @return
	 */
	public Object get_context() { return this.context; }
	/**
	 * get the program element which the instance represents, which can be:<br>
	 * (1) <code>CirInstanceNode	|--> CirExecution</code><br>
	 * (2) <code>CirInstanceNode	|--> CirExecutionType</code><br>
	 * (3) <code>CirInstanceEdge	|--> CirExecutionFlow</code><br>
	 * (4) <code>CirInstanceEdge	|--> CirExecutionFlowType</code><br>
	 * @return
	 */
	public Object get_element() { return this.element; }
	/**
	 * whether the element refers to a real program element or only
	 * created to represents a set of them virtually.
	 * @return
	 */
	public abstract boolean is_virtual();
	/**
	 * get the state hold by the instance in the data flow analysis
	 * @return
	 */
	public Object get_state() { return this.state; }
	/**
	 * set and update the state hold by the instance in analysis
	 * @param state
	 */
	public void set_state(Object state) { this.state = state; }

}
