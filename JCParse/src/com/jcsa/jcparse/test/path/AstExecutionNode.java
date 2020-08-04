package com.jcsa.jcparse.test.path;

/**
 * [path, index, unit](in_flow, ou_flow)
 * @author yukimula
 *
 */
public class AstExecutionNode {
	
	/* attributes */
	/** the execution path in which the node is created **/
	private AstExecutionPath path;
	/** the index of the node in the execution path **/
	private int index;
	/** the unit that defines the actions performed in the node **/
	private AstExecutionUnit unit;
	/** the flow that directly points to this node **/
	protected AstExecutionFlow in_flow;
	/** the flow that is directly pointed from this **/
	protected AstExecutionFlow ou_flow;
	
	/* constructor */
	/**
	 * an isolated node in the path
	 * @param path the execution path in which the node is created
	 * @param index the index of the node in the execution path
	 * @param unit the unit that defines the actions performed in the node
	 * @throws IllegalArgumentException
	 */
	protected AstExecutionNode(AstExecutionPath path, 
			int index, AstExecutionUnit unit) throws IllegalArgumentException {
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
	 * @return the execution path in which the node is created
	 */
	public AstExecutionPath get_path() { return this.path; }
	/**
	 * @return the index of the node in the execution path
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the unit that defines the actions performed in the node
	 */
	public AstExecutionUnit get_unit() { return this.unit; }
	/**
	 * @return whether there is a flow pointing to this node
	 */
	public boolean has_in_flow() { return this.in_flow != null; }
	/**
	 * @return whether there is a flow point from this node
	 */
	public boolean has_ou_flow() { return this.ou_flow != null; }
	/**
	 * @return the flow that directly points to this node
	 */
	public AstExecutionFlow get_in_flow() { return this.in_flow; }
	/**
	 * @return the flow that is directly pointed from this
	 */
	public AstExecutionFlow get_ou_flow() { return this.ou_flow; }
	
}
