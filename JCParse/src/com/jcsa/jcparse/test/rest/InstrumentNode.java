package com.jcsa.jcparse.test.rest;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * Each node in instrumental analysis represents a node in abstract syntax tree,
 * which can be either a statement or expression, of which the latter record the
 * value evaluated at some point of the testing process.
 * 
 * @author yukimula
 *
 */
public class InstrumentNode {
	
	/** the list in which the node is created **/
	private InstrumentList list;
	/** the index of the node in its list **/
	private int index;
	/** node that directly leads to this node or null **/
	protected InstrumentNode prev;
	/** node that this node directly leads to or null **/
	protected InstrumentNode next;
	/** type of the instrumental node **/
	private InstrumentType type;
	/** the location where the instrumentation is performed **/
	private AstNode ast_location;
	/** the status of the node recording during test process **/
	private byte[] bytes_status;
	
	/* constructor */
	/**
	 * @param list the list in which the node is created
	 * @param index the index of this node in its list
	 * @param ast_location the location where the instrumentation is performed
	 * @param bytes_status the status of the node recording during test process
	 * @throws Exception
	 */
	protected InstrumentNode(InstrumentList list, int index, AstNode 
			ast_location, byte[] bytes_status) throws Exception {
		if(list == null)
			throw new IllegalArgumentException("Invalid list: null");
		else if(ast_location == null)
			throw new IllegalArgumentException("Invalid ast_location");
		else if(bytes_status == null || bytes_status.length == 0)
			throw new IllegalArgumentException("Invalid bytes_status");
		else if(ast_location instanceof AstStatement) {
			this.list = list;
			this.index = index;
			this.ast_location = ast_location;
			this.bytes_status = new byte[0];
			this.type = InstrumentType.prev_stmt;
			for(byte value : bytes_status) {
				if(value != 0) {
					this.type = InstrumentType.post_stmt;
					break;
				}
			}
			this.prev = null;
			this.next = null;
		}
		else if(ast_location instanceof AstExpression) {
			this.list = list;
			this.index = index;
			this.ast_location = ast_location;
			this.bytes_status = bytes_status;
			this.type = InstrumentType.eval_expr;
			this.prev = null;
			this.next = null;
		}
		else 
			throw new IllegalArgumentException("Unknown: " + ast_location);
	}
	
	/* getters */
	/** 
	 * @return the list in which the node is created
	 */
	public InstrumentList get_list() { return this.list; }
	/**
	 * @return the index of the node in its list 
	 */
	public int get_index() { return this.index; }
	/**
	 * @return type of the instrumental node
	 */
	public InstrumentType get_type() { return this.type; }
	/**
	 * @return the location where the instrumentation is performed
	 */
	public AstNode get_ast_location() { return this.ast_location; }
	/**
	 * @return the status of the node recording during test process
	 */
	public byte[] get_bytes_status() { return this.bytes_status; }
	/**
	 * @return node that directly leads to this node or null if it is head
	 */
	public InstrumentNode get_prev_node() { return this.prev; }
	/**
	 * @return node that this node directly leads to or null if it is tail
	 */
	public InstrumentNode get_next_node() { return this.next; }
	/**
	 * @return whether the node is the first node in the list
	 */
	public boolean is_head() { return this.prev == null; }
	/**
	 * @return whether the node is the final node in the list
	 */
	public boolean is_tail() { return this.next == null; }
	
}
