package com.jcsa.jcparse.lang.irlang.graph;

import java.util.LinkedList;
import java.util.NoSuchElementException;


/**
 * The sequence of execution flows from source to target.
 * 
 * @author yukimula
 *
 */
public class CirExecutionPath {
	
	/* definitions */
	/** the node from which the path starts **/
	private CirExecution source;
	/** the node to which the path finished **/
	private CirExecution target;
	/** the sequence of flows from source to target **/
	private LinkedList<CirExecutionFlow> flows;
	
	/* constructor */
	/**
	 * create an empty path from source to source without any flows
	 * @param source
	 * @throws IllegalArgumentException
	 */
	public CirExecutionPath(CirExecution source) throws IllegalArgumentException {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else {
			this.source = source;
			this.target = source;
			this.flows = new LinkedList<CirExecutionFlow>();
		}
	}
	
	/* getters */
	/**
	 * @return the node from which the path starts
	 */
	public CirExecution get_source() { return this.source; }
	/**
	 * @return the node to which the path finished
	 */
	public CirExecution get_target() { return this.target; }
	/**
	 * @return the sequence of flows from source to target
	 */
	public Iterable<CirExecutionFlow> get_flows() { return this.flows; }
	/**
	 * @return the length of the execution path from source to target
	 */
	public int length() { return this.flows.size(); }
	/**
	 * @return whether the path is empty of which length is zero
	 */
	public boolean is_empty() { return this.flows.isEmpty(); }
	/**
	 * @param k
	 * @return the kth execution flow in the path
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecutionFlow get_flow(int k) throws IndexOutOfBoundsException {
		return this.flows.get(k);
	}
	/**
	 * @param k
	 * @return the kth node in the path (0 for source and n for target)
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecution get_node(int k) throws IndexOutOfBoundsException {
		if(k < 0)
			throw new IndexOutOfBoundsException("Invalid k: " + k);
		else if(k < this.flows.size())
			return this.flows.get(k).get_source();
		else if(k == this.flows.size())
			return this.target;
		else
			throw new IndexOutOfBoundsException("Invalid k: " + k);
	}
	/**
	 * @param flow
	 * @return whether there is flow w.r.t. to the given path
	 */
	public boolean has_flow(CirExecutionFlow flow) {
		for(CirExecutionFlow path_flow : this.flows) {
			if(flow.get_type() == path_flow.get_type() && 
					flow.get_source() == path_flow.get_source() && 
					flow.get_target() == path_flow.get_target()) {
				return true;
			}
		}
		return false;
	}
	
	/* setters */
	/**
	 * append the flow of which source matches with the current target of the node
	 * @param flow
	 * @throws IllegalArgumentException
	 */
	public void addFinal(CirExecutionFlow flow) throws IllegalArgumentException {
		if(flow == null)
			throw new IllegalArgumentException("Invalid flow as null");
		else if(flow.get_source() != this.target)
			throw new IllegalArgumentException("Not matched: " + this.target + " to " + flow.get_source());
		else {
			this.flows.addLast(flow);
			this.target = flow.get_target();
		}
	}
	/**
	 * insert the flow on the head of which target matches with the source of the path.
	 * @param flow
	 * @throws IllegalArgumentException
	 */
	public void addFirst(CirExecutionFlow flow) throws IllegalArgumentException {
		if(flow == null)
			throw new IllegalArgumentException("Invalid flow as null");
		else if(flow.get_target() != this.source)
			throw new IllegalArgumentException("Not matched: " + this.source + " from " + flow.get_target());
		else {
			this.flows.addFirst(flow);
			this.source = flow.get_source();
		}
	}
	/**
	 * @return remove the first flow in the path and update source
	 * @throws NoSuchElementException
	 */
	public CirExecutionFlow delFirst() throws NoSuchElementException {
		CirExecutionFlow flow = this.flows.removeFirst();
		this.source = flow.get_target();
		return flow;
	}
	/**
	 * @return remove the final flow in the path and update target
	 * @throws NoSuchElementException
	 */
	public CirExecutionFlow delFinal() throws NoSuchElementException {
		CirExecutionFlow flow = this.flows.removeLast();
		this.target = flow.get_source();
		return flow;
	}
	/**
	 * clear the flows and set target to the source in the path
	 */
	public void clearToFirst() {
		this.flows.clear();
		this.target = this.source;
	}
	/**
	 * clear the flows and set source to the target in the path
	 */
	public void clearToFinal() {
		this.flows.clear();
		this.source = this.target;
	}
	
	/* universal */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for(CirExecutionFlow flow : this.flows) {
			if(!flow.is_valid_flow()) {
				buffer.append(flow.get_source() + " ~~");
				buffer.append(flow.get_type() + "~~ ");
			}
			else {
				buffer.append(flow.get_source() + " --");
				buffer.append(flow.get_type() + "-- ");
			}
		}
		buffer.append(this.target.toString());
		return buffer.toString();
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof CirExecutionPath) {
			CirExecutionPath path = (CirExecutionPath) obj;
			if(this.source == path.source && this.target == path.target && this.flows.size() == path.flows.size()) {
				for(int k = 0; k < this.flows.size(); k++) {
					CirExecutionFlow x = this.flows.get(k), y = path.flows.get(k);
					if(x.get_source() == y.get_source() && x.get_target() == y.get_target() && x.get_type() == y.get_type()) {
						continue;
					}
					else {
						return false;
					}
				}
				return true;
			}
			else {
				return false;
			}
		}
		else
			return false;
	}
	public CirExecutionPath clone() {
		CirExecutionPath path = new CirExecutionPath(this.source);
		path.target = this.target;
		path.flows.addAll(this.flows);
		return path;
	}
	
}
