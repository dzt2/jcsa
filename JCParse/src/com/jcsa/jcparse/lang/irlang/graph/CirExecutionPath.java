package com.jcsa.jcparse.lang.irlang.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

/**
 * 	The execution path is defined as a sequence of execution flows with annotations, denoted as path.flows such that:<br>
 * 		1. path.source == flows[0].source;
 * 		2. path.target == flows[n - 1].target;
 * 		3. flows[k].target == flows[k + 1].source for any k >= 0 and k < flows.length - 1
 * 	in which n is the flows.length
 * 	@author yukimula
 *
 */
public class CirExecutionPath {
	
	/* definitions */
	/** the source from which the path starts **/
	private CirExecution source;
	/** the target at which the path terminates **/
	private CirExecution target;
	/** the sequence of execution flows performed in the path:
	 * 	1. path.source == flows[0].source
	 * 	2. path.target == flows[-1].target
	 * 	3. flows[k].target == flows[k + 1].source **/
	private LinkedList<CirExecutionEdge> edges;
	/**
	 * create an empty path starting from the execution of statement as given
	 * @param execution
	 * @throws IllegalArgumentException
	 */
	public CirExecutionPath(CirExecution execution) throws IllegalArgumentException {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else {
			this.source = execution;
			this.target = execution;
			this.edges = new LinkedList<CirExecutionEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the source from which the path starts
	 */
	public CirExecution get_source() { return this.source; }
	/**
	 * @return the target at which the path terminates
	 */
	public CirExecution get_target() { return this.target; }
	/**
	 * @return the sequence of execution flows performed in the path
	 */
	public Iterable<CirExecutionEdge> get_edges() { return edges; }
	/**
	 * @return whether the path is empty without any edges in
	 */
	public boolean is_empty() { return this.edges.isEmpty(); }
	/**
	 * @return the length of the path is the number of its edges
	 */
	public int length() { return this.edges.size(); }
	/**
	 * @param k
	 * @return the kth edge in the sequence of edges in the path
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecutionEdge get_edge(int k) throws IndexOutOfBoundsException { return this.edges.get(k); }
	/**
	 * @param k in [0, path.length]
	 * @return nodes[0] == path.source; nodes[n] == path.target; nodes[k] == edges[k].source;
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
	 * clear the edges in the path and move path.target to path.source
	 */
	public void clc_to_first() {
		for(CirExecutionEdge edge : this.edges)
			edge.delete();
		this.edges.clear();
		this.target = this.source;
	}
	/**
	 * clear the edges in the path and move path.source to path.target
	 */
	public void clc_to_final() {
		for(CirExecutionEdge edge : this.edges) 
			edge.delete();
		this.edges.clear();
		this.source = this.target;
	}
	/**
	 * @param flow required: flow.target == path.source
	 * @return insert the flow before the source of the path
	 * @throws IllegalArgumentException
	 */
	public CirExecutionEdge add_first(CirExecutionFlow flow) throws IllegalArgumentException {
		if(flow == null || flow.get_target() != this.source)
			throw new IllegalArgumentException("Invalid flow: " + flow + " before " + this.source);
		else {
			CirExecutionEdge edge = new CirExecutionEdge(this, flow);
			this.edges.addFirst(edge);
			this.source = flow.get_source();
			return edge;
		}
	}
	/**
	 * @param flow required: flow.source == path.target
	 * @return add the next flow into the path
	 * @throws IllegalArgumentException
	 */
	public CirExecutionEdge add_final(CirExecutionFlow flow) throws IllegalArgumentException {
		if(flow == null || flow.get_source() != this.target)
			throw new IllegalArgumentException("Invalid flow: " + flow + " since " + this.target);
		else {
			CirExecutionEdge edge = new CirExecutionEdge(this, flow);
			this.edges.addLast(edge);
			this.target = flow.get_target();
			return edge;
		}
	}
	/**
	 * @return remove the first edge in the head of the path and update path.source
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecutionEdge del_first() throws IndexOutOfBoundsException {
		if(this.edges.isEmpty())
			throw new IndexOutOfBoundsException("Empty path cannot be deleted");
		else {
			CirExecutionEdge edge = this.edges.removeFirst();
			this.source = edge.get_flow().get_target();
			return edge;
		}
	}
	/**
	 * @return remove the final edge in the head of the path and update path.target
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecutionEdge del_final() throws IndexOutOfBoundsException {
		if(this.edges.isEmpty())
			throw new IndexOutOfBoundsException("Empty path cannot be deleted");
		else {
			CirExecutionEdge edge = this.edges.removeLast();
			this.target = edge.get_flow().get_source();
			return edge;
		}
	}
	/**
	 * @return the first edge in the path or null if the path is empty
	 */
	public CirExecutionEdge get_first() {
		if(this.edges.isEmpty())
			return null;
		else
			return this.edges.getFirst();
	}
	/**
	 * @return the final edge in the path or null if the path is empty
	 */
	public CirExecutionEdge get_final() {
		if(this.edges.isEmpty())
			return null;
		else
			return this.edges.getLast();
	}
	/**
	 * @return the first flow in the path or null if the path is empty
	 */
	public CirExecutionFlow get_first_flow() {
		if(this.edges.isEmpty())
			return null;
		else
			return this.edges.getFirst().get_flow();
	}
	/**
	 * @return the final flow in the path or null if the path is empty
	 */
	public CirExecutionFlow get_final_flow() {
		if(this.edges.isEmpty())
			return null;
		else
			return this.edges.getLast().get_flow();
	}
	
	/* universals */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for(CirExecutionEdge edge : this.edges) {
			buffer.append(edge.get_flow().get_source().toString());
			buffer.append(" <" + edge.get_flow().get_type() + "> ");
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
					if(!path.edges.get(k).get_flow().equals(this.edges.get(k).get_flow()))
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
			path.add_final(edge.get_flow());
		}
		return path;
	}
	
	/* implication */
	/**
	 * @return the edge of call flow that generates the context in which the path.target is executed
	 * 		   or null if the path.target is on the top of the calling stack.
	 */
	public CirExecutionEdge final_call_edge() throws Exception {
		Stack<CirExecutionFlow> call_stack = new Stack<CirExecutionFlow>();
		Iterator<CirExecutionEdge> iterator = this.edges.descendingIterator();
		while(iterator.hasNext()) {
			CirExecutionEdge edge = iterator.next();
			if(edge.get_type() == CirExecutionFlowType.retr_flow) {
				call_stack.push(edge.get_flow());
			}
			else if(edge.get_type() == CirExecutionFlowType.call_flow) {
				if(call_stack.isEmpty()) {
					return edge;
				}
				else {
					CirExecutionFlow call_flow = edge.get_flow();
					CirExecutionFlow retr_flow = call_stack.pop();
					if(!CirExecutionFlow.match_call_retr_flow(call_flow, retr_flow))
						throw new RuntimeException(call_flow + " ==> " + retr_flow);
				}
			}
		}
		return null;
	}
	/**
	 * @return the call flow that generates the context in which the path.target is executed
	 * @throws Exception
	 */
	public CirExecutionFlow final_call_flow() throws Exception {
		CirExecutionEdge edge = this.final_call_edge();
		if(edge == null)
			return null;
		else
			return edge.get_flow();
	}
	/**
	 * @return the edge of return flow that corresponds to context where the path.source is executed
	 * @throws Exception
	 */
	public CirExecutionEdge first_retr_edge() throws Exception {
		Stack<CirExecutionFlow> call_stack = new Stack<CirExecutionFlow>();
		Iterator<CirExecutionEdge> iterator = this.edges.iterator();
		while(iterator.hasNext()) {
			CirExecutionEdge edge = iterator.next();
			if(edge.get_type() == CirExecutionFlowType.call_flow) {
				call_stack.push(edge.get_flow());
			}
			else if(edge.get_type() == CirExecutionFlowType.retr_flow) {
				if(call_stack.isEmpty())
					return edge;
				else {
					CirExecutionFlow call_flow = call_stack.pop();
					CirExecutionFlow retr_flow = edge.get_flow();
					if(!CirExecutionFlow.match_call_retr_flow(call_flow, retr_flow))
						throw new RuntimeException(call_flow + " ==> " + retr_flow);
				}
			}
		}
		return null;
	}
	/**
	 * @return the return flow that corresponds to the context where the path.source is executed
	 * @throws Exception
	 */
	public CirExecutionFlow first_retr_flow() throws Exception {
		CirExecutionEdge edge = this.first_retr_edge();
		if(edge == null)
			return null;
		else
			return edge.get_flow();
	}
	/**
	 * @param flow
	 * @return whether there is edge in the path w.r.t. the given flow
	 */
	public boolean has_edge_of(CirExecutionFlow flow) {
		if(flow == null)
			return false;
		else {
			for(CirExecutionEdge edge : this.edges) {
				if(edge.get_flow().equals(flow))
					return true;
			}
			return false;
		}
	}
	/**
	 * @return the iterator returns the edges in reversed sequence in the path
	 */
	public Iterator<CirExecutionEdge> get_reverse_edges() {
		return this.edges.descendingIterator();
	}
	
}
