package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.astree.AstNode;

public class AstExecutionNode {
	
	/* attributes */
	/** the path in which the node is created **/
	private AstExecutionPath path;
	/** the index of the node in the path **/
	private int index;
	/** the type of the node in its path **/
	private AstExecutionType type;
	/** the location in which the node occurs **/
	private AstNode ast_location;
	/** the bytes that record the status of the node {end_expr} **/
	private byte[] bytes_status;
	/** the edge pointing to this node or null when it is head **/
	private AstExecutionEdge prev_edge;
	/** the edge pointing from this node or null if it is tail **/
	private AstExecutionEdge next_edge;
	
	/* constructor */
	/**
	 * @param path the path in which the node is created
	 * @param index the index of the node in the path
	 * @param type the type of the node in its path
	 * @param ast_location the location in which the node occurs
	 * @param bytes_status the bytes that record the status of the node {end_expr}
	 * @throws IllegalArgumentException
	 */
	protected AstExecutionNode(AstExecutionPath path, int index,
			AstExecutionType type, AstNode ast_location,
			byte[] bytes_status) throws IllegalArgumentException {
		if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(ast_location == null)
			throw new IllegalArgumentException("Invalid ast_location");
		else if(bytes_status == null)
			throw new IllegalArgumentException("Invalid bytes_status");
		else {
			this.path = path;
			this.index = index;
			this.type = type;
			this.ast_location = ast_location;
			this.bytes_status = bytes_status;
			this.prev_edge = null;
			this.next_edge = null;
		}
	}
	
	/* getters */
	/**
	 * @return the path in which the node is created
	 */
	public AstExecutionPath get_path() { return this.path; }
	/**
	 * @return the index of the node in the path
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the type of the node in its path
	 */
	public AstExecutionType get_type() { return this.type; }
	/**
	 * @return the location in which the node occurs
	 */
	public AstNode get_ast_location() { return this.ast_location; }
	/**
	 * @return the bytes that record the status of the node {end_expr}
	 */
	public byte[] get_bytes_status() { return this.bytes_status; }
	/**
	 * @return whether the node is the first node in the path
	 */
	public boolean is_head() { return this.prev_edge == null; }
	/**
	 * @return whether the node is the final node in the path
	 */
	public boolean is_tail() { return this.next_edge == null; }
	/**
	 * @return the edge pointing to this node or null when it is head
	 */
	public AstExecutionEdge get_prev_edge() { return this.prev_edge; }
	/**
	 * @return the edge pointing from this node or null if it is tail
	 */
	public AstExecutionEdge get_next_edge() { return this.next_edge; }
	/**
	 * @return the node directly leading to this node or null if it
	 * 			is the head node in the path.
	 */
	public AstExecutionNode get_prev_node() { 
		if(this.prev_edge == null)
			return null;
		else
			return this.prev_edge.get_source();
	}
	/**
	 * @return the node directly lead from this node or null if it
	 * 			is the tail node in the path.
	 */
	public AstExecutionNode get_next_node() {
		if(this.next_edge == null)
			return null;
		else
			return this.next_edge.get_target();
	}
	
}
