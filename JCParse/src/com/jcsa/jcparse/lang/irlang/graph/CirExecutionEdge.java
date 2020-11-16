package com.jcsa.jcparse.lang.irlang.graph;

/**
 * The edge in execution path corresponds to an execution flow, as well as the 
 * annotation it correlated with.
 * 
 * @author yukimula
 *
 */
public class CirExecutionEdge {
	
	/* definitions */
	/** the path in which the execution edge is created **/
	private CirExecutionPath path;
	/** the index of the execution edge in the sequence **/
	private int index;
	/** the execution flow to which the edge correspond **/
	private CirExecutionFlow flow;
	/** the annotation correlated with the flow **/
	private Object annotation;
	
	/* constructor */
	/**
	 * create an execution edge in the path w.r.t. the flow as given
	 * @param path
	 * @param index
	 * @param flow
	 * @throws IllegalArgumentException
	 */
	protected CirExecutionEdge(CirExecutionPath path, int index, 
			CirExecutionFlow flow) throws IllegalArgumentException {
		if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else if(index < 0)
			throw new IllegalArgumentException("Invalid index as " + index);
		else if(flow == null)
			throw new IllegalArgumentException("Invalid flow: null");
		else {
			this.path = path;
			this.index = index;
			this.flow = flow;
			this.annotation = null;
		}
	}
	
	/* getters */
	/**
	 * @return the path in which the execution edge is created
	 */
	public CirExecutionPath get_path() { return this.path; }
	/**
	 * @return the index of the execution edge in the path
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the execution flow to which the edge corresponds
	 */
	public CirExecutionFlow get_flow() { return this.flow; }
	/**
	 * @return the annotation correlated with the flow in the edge
	 */
	public Object get_annotation() { return this.annotation; }
	/**
	 * set the annotation correlated with the flow in the edge
	 * @param annotation
	 */
	public void set_annotation(Object annotation) { this.annotation = annotation; }
	
	/* implication */
	/**
	 * @return the source statement from which the edge points to target
	 */
	public CirExecution get_source() { return this.flow.get_source(); }
	/**
	 * @return the target statement to which the edge points from source
	 */
	public CirExecution get_target() { return this.flow.get_target(); }
	/**
	 * @return the type of the execution flow
	 */
	public CirExecutionFlowType get_type() { return this.flow.get_type(); }
	/**
	 * @return whether the execution flow is virtual
	 */
	public boolean is_virtual() { return this.flow.is_virtual(); }
	/**
	 * @return whether the execution flow is actual (not virtual)
	 */
	public boolean is_actual() { return this.flow.is_actual(); }
	
	/* universal */
	@Override
	public String toString() { return this.flow.toString(); }
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof CirExecutionEdge)
			return ((CirExecutionEdge) obj).flow.equals(this.flow);
		else
			return false;
	}
	/**
	 * delete this edge from the path
	 */
	protected void delete() {
		this.path = null;
		this.index = -1;
		this.flow = null;
		this.annotation = null;
	}
	
}
