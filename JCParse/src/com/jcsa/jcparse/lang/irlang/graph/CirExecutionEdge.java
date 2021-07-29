package com.jcsa.jcparse.lang.irlang.graph;


/**
 * It represents an instance of execution flow in the CirExecutionPath.
 * 
 * @author yukimula
 *
 */
public class CirExecutionEdge {
	
	/* attributes */
	/** the execution path where this edge is defined **/
	private CirExecutionPath path;
	/** the execution flow that this edge corresponds **/
	private CirExecutionFlow flow;
	/** the object to annotate this edge (as state) **/
	private Object annotation;
	
	/* constructor */
	/**
	 * create an edge of the flow in the context of the path
	 * @param path
	 * @param flow
	 * @throws IllegalArgumentException
	 */
	protected CirExecutionEdge(CirExecutionPath path,
			CirExecutionFlow flow) throws IllegalArgumentException {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else if(flow == null) {
			throw new IllegalArgumentException("Invalid flow: null");
		}
		else {
			this.path = path; this.flow = flow; this.annotation = null;
		}
	}
	
	/* getters */
	/**
	 * @return the execution path where this edge is defined
	 */
	public CirExecutionPath get_path() { return this.path; }
	/**
	 * @return the execution flow that this edge corresponds
	 */
	public CirExecutionFlow get_flow() { return this.flow; }
	/**
	 * @return the object to annotate this edge (as state)
	 */
	public Object get_annotation() { return this.annotation; }
	/**
	 * @return the type of execution flow of this edge
	 */
	public CirExecutionFlowType get_type() { return this.flow.get_type(); }
	/**
	 * @return the source node of the execution flow of the edge
	 */
	public CirExecution get_source() { return this.flow.get_source(); }
	/**
	 * @return the target node of the execution flow of the edge
	 */
	public CirExecution get_target() { return this.flow.get_target(); }
	/**
	 * @return whether the flow is virtual flow
	 */
	public boolean is_virtual_edge() { return this.flow.is_virtual(); }
	/**
	 * @return whether the flow is actual flow
	 */
	public boolean is_acutal_edge() { return this.flow.is_actual(); }
	
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
	
	/* setters */
	/**
	 * delete this edge from the path
	 */
	protected void delete() {
		this.path = null;
		this.flow = null;
		this.annotation = null;
	}
	/**
	 * set the annotation of the edge in the path
	 * @param annotation
	 */
	protected void set_annotation(Object annotation) {
		this.annotation = annotation;
	}
	
}
