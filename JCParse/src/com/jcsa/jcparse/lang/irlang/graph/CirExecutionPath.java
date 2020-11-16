package com.jcsa.jcparse.lang.irlang.graph;

import java.util.ArrayList;
import java.util.Stack;

/**
 * The execution path maintains a sequence of execution edge that connects from the source to the target.
 * @author yukimula
 *
 */
public class CirExecutionPath {
	
	/* definitions */
	/** the statement from which the path is executed until the target **/
	private CirExecution source;
	/** the statement until which the path is executed from the target **/
	private CirExecution target;
	/** the sequence of execution edges (with flow + annotation) in the path which connects from source to target **/
	private ArrayList<CirExecutionEdge> edges;
	/**
	 * create an empty path of which source and target is allocated to the execution
	 * @param source
	 * @throws IllegalArgumentException
	 */
	public CirExecutionPath(CirExecution source) throws IllegalArgumentException {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else {
			this.source = source;
			this.target = source;
			this.edges = new ArrayList<CirExecutionEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the statement from which the path is executed until the target
	 */
	public CirExecution get_source() { return this.source; }
	/**
	 * @return the statement until which the path is executed from the target
	 */
	public CirExecution get_target() { return this.target; }
	/**
	 * @return the sequence of execution edges (with flow + annotation) in the path which connects from source to target
	 */
	public Iterable<CirExecutionEdge> get_edges() { return this.edges; }
	/**
	 * @return the number of execution edges in the path
	 */
	public int length() { return this.edges.size(); }
	/**
	 * @return the execution path is empty if no edge is created among it
	 */
	public boolean is_empty() { return this.edges.isEmpty(); }
	/**
	 * @param k
	 * @return the kth execution edge in the path
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecutionEdge get_edge(int k) throws IndexOutOfBoundsException { return this.edges.get(k); }
	/**
	 * @param flow
	 * @return whether there is an execution edge in the path that corresponds to the flow
	 */
	public boolean has_edge_of(CirExecutionFlow flow) {
		for(CirExecutionEdge edge : this.edges) {
			if(edge.get_flow().equals(flow))
				return true;
		}
		return false;
	}
	
	/* universals */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for(CirExecutionEdge edge : this.edges) {
			buffer.append(edge.get_source().toString());
			buffer.append(" <" + edge.get_type() + "> ");
		}
		buffer.append(this.target.toString());
		return buffer.toString();
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof CirExecutionPath) {
			CirExecutionPath path = (CirExecutionPath) obj;
			if(path.source == this.source && path.target == this.target && path.edges.size() == this.edges.size()) {
				for(int k = 0; k < this.edges.size(); k++) {
					if(!this.edges.get(k).get_flow().equals(path.edges.get(k).get_flow()))
						return false;
				}
				return true;
			}
			else 
				return false;
		}
		else
			return false;
	}
	@Override
	public CirExecutionPath clone() {
		CirExecutionPath path = new CirExecutionPath(this.source);
		for(CirExecutionEdge edge : this.edges) {
			path.add(edge.get_flow());
		}
		return path;
	}
	
	/* setters */
	/**
	 * clear the path and set this.target <-- this.source
	 */
	protected void clr() { 
		for(CirExecutionEdge edge : this.edges) {
			edge.delete();
		}
		this.edges.clear();
		this.target = this.source;
	}
	/**
	 * append the flow in the tail of the path
	 * @param flow of which source required to match with path.target
	 * @return the execution edge w.r.t. the flow that is inserted in the tail of the path
	 * @throws IllegalArgumentException is thrown if flow is null or flow.source != path.target
	 */
	protected CirExecutionEdge add(CirExecutionFlow flow) throws IllegalArgumentException {
		if(flow == null || flow.get_source() != this.target)
			throw new IllegalArgumentException("Invalid flow as " + flow + " after " + this.target);
		else {
			CirExecutionEdge edge = new CirExecutionEdge(this, this.edges.size(), flow);
			this.edges.add(edge); this.target = edge.get_target();
			return edge;
		}
	}
	/**
	 * remove the last edge from the path
	 * @return
	 * @throws IndexOutOfBoundsException is thrown if the path is empty
	 */
	protected CirExecutionEdge pop() throws IndexOutOfBoundsException {
		CirExecutionEdge edge = this.edges.remove(this.edges.size() - 1);
		this.target = edge.get_source();
		return edge;
	}
	
	/* self-finder */
	/**
	 * @return the last valid calling edge that generates the context of the path.target being executed
	 */
	public CirExecutionEdge last_call_edge() {
		Stack<CirExecutionFlow> call_stack = new Stack<CirExecutionFlow>();
		for(int k = this.edges.size() - 1; k >= 0; k--) {
			CirExecutionEdge edge = this.edges.get(k);
			if(edge.get_type() == CirExecutionFlowType.retr_flow) {
				call_stack.push(edge.get_flow());
			}
			else if(edge.get_type() == CirExecutionFlowType.call_flow) {
				if(call_stack.isEmpty())
					return edge;
				else {
					CirExecutionFlow retr_flow = call_stack.pop();
					CirExecutionFlow call_flow = edge.get_flow();
					if(!CirExecutionFlow.match_call_retr_flow(call_flow, retr_flow))
						throw new IllegalArgumentException(call_flow + " not matched with " + retr_flow);
				}
			}
		}
		return null;
	}
	
}
