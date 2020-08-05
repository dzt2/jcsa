package com.jcsa.jcparse.test.path;

/**
 * 	[path, index, in_flow, unit, {ou_flow}]
 * 	
 * 	@author yukimula
 *
 */
public class AstExecutionNode {
	
	/* attributes and constructor */
	/** the path of the node **/
	private AstExecutionPath path;
	/** the index of the node in path **/
	private int index;
	/** the unit that defines the action performed in this node **/
	private AstExecutionUnit unit;
	/** the flow directly point to this node or null **/
	private AstExecutionFlow in_flow;
	/** the flow directly point from prior to this node or null **/
	private AstExecutionFlow ou_flow;
	
	/**
	 * @param path the path of the node
	 * @param index the index of the node in path
	 * @param unit the unit that defines the action performed in this node
	 * @throws IllegalArgumentException
	 */
	protected AstExecutionNode(AstExecutionPath path, int index,
			AstExecutionUnit unit) throws IllegalArgumentException {
		if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else if(unit == null)
			throw new IllegalArgumentException("Invalid unit: null");
		else {
			this.path = path;
			this.index = index;
			this.unit = unit;
			this.in_flow = null;
			this.ou_flow = null;
		}
	}
	
	/* getters */
	/**
	 * @return the path of the node 
	 */
	public AstExecutionPath get_path() { return this.path; }
	/**
	 * @return the index of the node in path
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the unit that defines the action performed in this node
	 */
	public AstExecutionUnit get_unit() { return this.unit; }
	/**
	 * @return whether there is an input-flow pointing to this node
	 */
	public boolean has_in_flow() { return this.in_flow != null; }
	/**
	 * @return the flow directly point to this node or null
	 */
	public AstExecutionFlow get_in_flow() { return this.in_flow; }
	/**
	 * @return whether there is output-flow pointing from this node
	 */
	public boolean has_ou_flow() { return this.ou_flow != null; }
	/**
	 * @return the flow directly point from prior to this node or null 
	 */
	public AstExecutionFlow get_ou_flow() { return this.ou_flow; }
	
	/* setter */
	/**
	 * @param flow_type
	 * @param target
	 * @return the flow that connects this node to the target node
	 * @throws IllegalArgumentException
	 */
	public AstExecutionFlow connect(AstExecutionFlowType flow_type, 
			AstExecutionNode target) throws IllegalArgumentException {
		if(target == null || target.in_flow != null)
			throw new IllegalArgumentException("Invalid target: " + target);
		else if(this.ou_flow != null)
			throw new IllegalArgumentException("Invalid source: " + this);
		else {
			AstExecutionFlow flow = new AstExecutionFlow(flow_type, this, target);
			this.ou_flow = flow;
			target.in_flow = flow;
			return flow;
		}
	}
	
}
