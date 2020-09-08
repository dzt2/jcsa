package com.jcsa.jcparse.test.inst;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

public class InstrumentalNode {
	
	/* definitions */
	/** the list where the token is created **/
	private InstrumentalList list;
	/** the index of the node in its list **/
	private int index;
	/** the type of the instrumental line **/
	private InstrumentalType type;
	/** the location where instrumentation is seeded **/
	private AstNode location;
	/** the byte sequence describes the value of the 
	 *  location if it's an expression and evaluated **/
	private byte[] value;
	
	/* constructor */
	private boolean is_zero(byte[] value) {
		for(byte x : value) {
			if(x != 0) {
				return false;
			}
		}
		return true;
	}
	protected InstrumentalNode(InstrumentalList list,
			int index, AstNode location, byte[] value) throws Exception {
		if(list == null)
			throw new IllegalArgumentException("Invalid list: null");
		else if(index < 0)
			throw new IllegalArgumentException("Invalid index: " + index);
		else if(value == null || value.length == 0)
			throw new IllegalArgumentException("Invalid value: null");
		else if(location instanceof AstExpression) {
			this.list = list;
			this.index = index;
			this.type = InstrumentalType.evaluate;
			this.location = location;
			this.value = value;
		}
		else if(location instanceof AstStatement) {
			this.list = list;
			this.index = index;
			this.location = location;
			this.value = null;
			this.type = this.is_zero(value) ? 
						InstrumentalType.beg_stmt : 
						InstrumentalType.end_stmt;
		}
		else 
			throw new IllegalArgumentException(location.getClass().getName());
	}
	
	/* getters */
	/**
	 * @return the list where the token is created
	 */
	public InstrumentalList get_list() { return this.list; }
	/**
	 * @return the index of the node in its list
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the type of the instrumental line
	 */
	public InstrumentalType get_type() { return this.type; }
	/**
	 * @return the location where instrumentation is seeded
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return whether the node contains value for its expression
	 */
	public boolean has_value() { return this.value != null; }
	/**
	 * @return  the byte sequence describes the value of the 
	 *  		location if it's an expression and evaluated
	 */
	public byte[] get_value() { return this.value; }
	/**
	 * @return the instrumental node occur just before this one
	 */
	public InstrumentalNode get_prev_node() {
		if(index <= 0) {
			return null;
		}
		else {
			return this.list.get_node(this.index - 1);
		}
	}
	/**
	 * @return the instrumental node occur just after this one
	 */
	public InstrumentalNode get_next_node() {
		if(index >= this.list.length() - 1) {
			return null;
		}
		else {
			return this.list.get_node(this.index + 1);
		}
	}
	
	/* setters */
	/**
	 * remove this node from the list
	 */
	protected void delete() {
		this.list = null;
		this.index = -1;
		this.type = null;
		this.location = null;
		this.value = null;
	}
	
}
