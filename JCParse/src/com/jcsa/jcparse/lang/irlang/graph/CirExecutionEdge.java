package com.jcsa.jcparse.lang.irlang.graph;

/**
 * The execution edge is the flow in CirExecutionPath that connects from source to target via a flow,
 * in which an annotation is allowed to be binded with the edge together so to record the state of
 * the edge during execution.
 * 
 * @author yukimula
 *
 */
public class CirExecutionEdge {
	
	/* definitions */
	/** the execution path of the edge **/
	private CirExecutionPath path;
	/** the index of the edge in its path **/
	private int index;
	/** the flow to which the edge corresponds **/
	private CirExecutionFlow flow;
	/** the annotation taged on the edge of the path **/
	private Object annotation;
	
	/* constructor */
	/**
	 * @param path the execution path in which the edge is created
	 * @param index the index of the edge in its execution path.
	 * @param flow the execution flow to which the edge corresponds.
	 * @throws IllegalArgumentException
	 */
	protected CirExecutionEdge(CirExecutionPath path, int index, CirExecutionFlow flow) throws IllegalArgumentException {
		if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else if(index < 0)
			throw new IllegalArgumentException("Invalid index as null");
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
	 * @return the execution path of the edge
	 */
	public CirExecutionPath get_path() { return this.path; }
	/**
	 * @return the index of the edge in its path
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the flow to which the edge corresponds
	 */
	public CirExecutionFlow get_flow() { return this.flow; }
	/**
	 * @return the annotation taged on the edge of the path
	 */
	public Object get_annotation() { return this.annotation; }
	/**
	 * @param annotation set the annotation of the execution flow in the edge
	 */
	public void set_annotation(Object annotation) { this.annotation = annotation; }
	
	/* implication */
	public CirExecution get_source() { return this.flow.get_source(); }
	public CirExecution get_target() { return this.flow.get_target(); }
	public CirExecutionFlowType get_type() { return this.flow.get_type(); }
	@Override
	public String toString() { return this.flow.toString(); }
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	
	/* setters */
	/**
	 * remove this edge from the path
	 */
	protected void delete() {
		this.path = null;
		this.index = -1;
		this.flow = null;
		this.annotation = null;
	}
}
