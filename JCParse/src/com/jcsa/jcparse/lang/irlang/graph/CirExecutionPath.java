package com.jcsa.jcparse.lang.irlang.graph;

import java.util.Stack;

/**
 * It maintains the sequence of execution flows, as well as the program state
 * connected with them.
 * 
 * @author yukimula
 *
 */
public class CirExecutionPath {
	
	/* definitions */
	/** the first node from which the path linked to the others **/
	private CirExecution source;
	/** the final node to which the path linked from the others **/
	private CirExecution target;
	/** the sequence of executional flows from source to target **/
	private Stack<CirExecutionFlow> flows;
	/** the sequence of program state hold at each flow in path
	 *  where the first state in it refers to initial state before
	 *  any flow in the path was actually executed. **/
	private Stack<Object> flow_states;
	
	/* constructor */
	/**
	 * create an execution path starting from the source with an initial state
	 * @param source
	 * @param init_state
	 * @throws IllegalArgumentException
	 */
	public CirExecutionPath(CirExecution source, Object 
			init_state) throws IllegalArgumentException {
		if(source == null)
			throw new IllegalArgumentException("Invalid source");
		else {
			this.source = source;
			this.target = source;
			this.flow_states = new Stack<Object>();
			this.flows = new Stack<CirExecutionFlow>();
			this.flow_states.push(init_state);
		}
	}
	
	/* getters */
	/**
	 * @return the first node from which the path linked to the others
	 */
	public CirExecution get_source() { return this.source; }
	/**
	 * @return the final node to which the path linked from the others
	 */
	public CirExecution get_target() { return this.target; }
	/**
	 * @return the state hold before any flows in the path are executed
	 */
	public Object get_init_state() { return this.flow_states.get(0); }
	/**
	 * @return the sate hold after all the flows in path were executed
	 */
	public Object get_last_state() { return this.flow_states.peek(); }
	/**
	 * @return the sequence of executional flows from source to target
	 */
	public Iterable<CirExecutionFlow> get_flows() { return this.flows; }
	/**
	 * @return the length of the path is the number of flows in it
	 */
	public int length() { return this.flows.size(); }
	/**
	 * @param k [0, this.length() - 1]
	 * @return the kth flow in the path
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public CirExecutionFlow get_flow(int k) throws ArrayIndexOutOfBoundsException {
		return this.flows.get(k);
	}
	/**
	 * @param k [0, this.length() - 1]
	 * @return the state of the kth flow in the path
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public Object get_flow_state(int k) throws ArrayIndexOutOfBoundsException {
		return this.flow_states.get(k + 1);
	}
	
	/* setters */
	/**
	 * remove all the flows from the path and updating its state
	 */
	public void clear() {
		Object init_state = this.get_init_state();
		this.flows.clear();
		this.flow_states.clear();
		this.flow_states.push(init_state);
		this.target = this.source;
	}
	/**
	 * add a flow at the tail of the path, of which source shall match the current
	 * target of the path
	 * @param flow
	 * @param state
	 * @throws IllegalArgumentException
	 */
	public void add(CirExecutionFlow flow, Object state) throws IllegalArgumentException {
		if(flow == null || flow.get_source() != this.target) 
			throw new IllegalArgumentException("Invalid: " + flow);
		else {
			this.flows.push(flow);
			this.flow_states.push(state);
			this.target = flow.get_target();
		}
	}
	
	
	
	
	
}
