package com.jcsa.jcparse.test.path;

public class AstExecutionNode {
	
	/* attributes */
	/** the path where the node is created **/
	private AstExecutionPath path;
	/** the index of this node in its path **/
	private int index;
	/** the unit that defines action of this node **/
	private AstExecutionUnit unit;
	/** the edge directly leading to the node or null **/
	protected AstExecutionEdge prev_edge;
	/** the edge directly lead from this node or null **/
	protected AstExecutionEdge next_edge;
	
	/* constructor */
	/**
	 * @param path the path where the node is created
	 * @param index the index of this node in its path
	 * @param unit the unit that defines action of this node
	 * @throws IllegalArgumentException
	 */
	protected AstExecutionNode(AstExecutionPath path,
			int index, AstExecutionUnit unit) 
					throws IllegalArgumentException {
		if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else if(unit == null)
			throw new IllegalArgumentException("Invalid unit: null");
		else {
			this.path = path;
			this.index = index;
			this.unit = unit;
			this.prev_edge = null;
			this.next_edge = null;
		}
	}
	
	/* getters */
	/**
	 * @return the path where the node is created 
	 */
	public AstExecutionPath get_path() { return this.path; }
	/**
	 * @return the index of this node in its path
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the unit that defines action of this node
	 */
	public AstExecutionUnit get_unit() { return this.unit; }
	/**
	 * @return the edge directly leading to the node or null if it's head
	 */
	public AstExecutionEdge get_prev_edge() { return this.prev_edge; }
	/**
	 * @return the edge directly lead from this node or null if it's tail
	 */
	public AstExecutionEdge get_next_edge() { return this.next_edge; }
	/**
	 * @return whether it is the first node in the path
	 */
	public boolean is_head() { return this.prev_edge == null; }
	/** 
	 * @return whether it is the final node in the path
	 */
	public boolean is_tail() { return this.next_edge == null; }
	/**
	 * @return the node directly leads to this node or null
	 */
	public AstExecutionNode get_prev_node() {
		if(this.prev_edge == null) return null;
		else return this.prev_edge.get_source();
	}
	/**
	 * @return the node directly lead from the node or null
	 */
	public AstExecutionNode get_next_node() {
		if(this.next_edge == null) return null;
		else return this.next_edge.get_target();
	}
	
}
