package com.jcsa.jcparse.lang.irlang.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * The execution path defines a sequence of flows connecting from the source to the target such that:
 * flows[0].source == path.source; flows[-1].target == path.target; flows[k].target == flows[k + 1].source;
 * @author yukimula
 *
 */
public class CirExecutionPath {
	
	/* definitions */
	/** the statement from which the path starts **/
	private CirExecution source;
	/** the statement until which the path ends **/
	private CirExecution target;
	/** the sequence of edges connecting from the source to target **/
	private List<CirExecutionEdge> edges;
	/**
	 * create an empty path starting from the execution
	 * @param execution
	 * @throws IllegalArgumentException
	 */
	protected CirExecutionPath(CirExecution execution) throws IllegalArgumentException {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else {
			this.source = execution;
			this.target = execution;
			this.edges = new ArrayList<CirExecutionEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the statement from which the path starts
	 */
	public CirExecution get_source() { return this.source; }
	/**
	 * @return the statement until which the path ends
	 */
	public CirExecution get_target() { return this.target; }
	/**
	 * @return the number of edges in the execution path
	 */
	public int length() { return this.edges.size(); }
	/**
	 * @return whether the path is empty
	 */
	public boolean is_empty() { return this.edges.isEmpty(); }
	/**
	 * @return the sequence of edges in the path
	 */
	public Iterable<CirExecutionEdge> get_edges() { return this.edges; }
	/**
	 * @param k
	 * @return the kth execution edge in the path
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecutionEdge get_edge(int k) throws IndexOutOfBoundsException {
		return this.edges.get(k);
	}
	/**
	 * @param k
	 * @return the kth node in the path
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecution get_node(int k) throws IndexOutOfBoundsException {
		if(k < 0)
			throw new IndexOutOfBoundsException("Invalid k: " + k);
		else if(k < this.edges.size())
			return this.edges.get(k).get_source();
		else if(k == this.edges.size())
			return this.target;
		else
			throw new IndexOutOfBoundsException("Invalid k: " + k);
	}
	
	/* setters */
	/**
	 * clear the edges in the path
	 */
	protected void clc() {
		for(CirExecutionEdge edge : this.edges)
			edge.delete();
		this.edges.clear();
		this.target = this.source;
	}
	/**
	 * add the flow into the tail of the path
	 * @param flow
	 * @throws IndexOutOfBoundsException
	 */
	protected CirExecutionEdge add(CirExecutionFlow flow) throws IllegalArgumentException {
		if(flow == null || flow.get_source() == this.target)
			throw new IllegalArgumentException("Invalid: " + flow);
		else {
			CirExecutionEdge edge = new CirExecutionEdge(this, this.edges.size(), flow);
			this.edges.add(edge);
			this.target = flow.get_target();
			return edge;
		}
	}
	/**
	 * @return remove the final edge from the path
	 * @throws IndexOutOfBoundsException
	 */
	protected CirExecutionEdge pop() throws IndexOutOfBoundsException {
		if(this.edges.isEmpty())
			throw new IndexOutOfBoundsException("Empty path cannot be pop");
		else {
			CirExecutionEdge edge = this.get_edge(this.edges.size() - 1);
			this.target = edge.get_flow().get_source();
			return edge;
		}
	}
	
	/* universal */
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
			if(path.source == this.source && path.target == this.target && path.length() == this.length()) {
				for(int k = 0; k < this.edges.size(); k++) {
					if(!path.edges.get(k).equals(this.edges.get(k)))
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
	
	/* implication */
	/**
	 * @return the edge of calling flow that generates the context of path.target
	 * 		   or null if there is not such a calling flow.
	 */
	protected CirExecutionEdge get_call_flow_of_target() throws Exception {
		Stack<CirExecutionFlow> call_stack = new Stack<CirExecutionFlow>();
		for(int k = this.length() - 1; k >= 0; k--) {
			CirExecutionEdge edge = this.edges.get(k);
			if(edge.get_type() == CirExecutionFlowType.retr_flow) {
				call_stack.push(edge.get_flow());
			}
			else if(edge.get_type() == CirExecutionFlowType.call_flow) {
				if(call_stack.isEmpty())
					return edge;
				else {
					CirExecutionFlow call_flow = edge.get_flow();
					CirExecutionFlow retr_flow = call_stack.pop();
					if(!CirExecutionFlow.match_call_retr_flow(call_flow, retr_flow)) {
						throw new RuntimeException("Unmatched: " + call_flow + " ==> " + retr_flow);
					}
				}
			}
		}
		return null;
	}
	
}
