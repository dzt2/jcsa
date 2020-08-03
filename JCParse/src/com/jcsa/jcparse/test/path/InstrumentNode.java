package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * The node in instrumental list is a tuple, defined as:<br>
 * <code>(type, location, status)</code>
 * @author yukimula
 *
 */
public class InstrumentNode {
	
	/* attributes */
	/** the list of instrumental node **/
	private InstrumentList list;
	/** the index of the node in list **/
	private int index;
	/** the type of instrumental node **/
	private InstrumentType type;
	/** location that the node describes **/
	private AstNode location;
	/** the bytes status hold by the node **/
	private byte[] status;
	
	/* constructor */
	/**
	 * @param location
	 * @param status
	 * @return the type of the instrumental node w.r.t. the given input
	 * @throws IllegalArgumentException
	 */
	private InstrumentType type_of(AstNode location, byte[] status) throws IllegalArgumentException {
		if(location instanceof AstStatement) {
			for(byte value : status) {
				if(value != 0) {
					return InstrumentType.end_stmt;
				}
			}
			return InstrumentType.beg_stmt;
		}
		else if(location instanceof AstExpression) {
			return InstrumentType.evaluate;
		}
		else {
			throw new IllegalArgumentException("Invalid location: " + location);
		}
	}
	/**
	 * @param list the list where the node is created
	 * @param index the index of the node in the list
	 * @param location the location on which the node describes
	 * @param status which records the byte-status of the node
	 * @throws IllegalArgumentException
	 */
	protected InstrumentNode(InstrumentList list, int index, 
			AstNode location, byte[] status) throws IllegalArgumentException {
		if(list == null)
			throw new IllegalArgumentException("Invalid list: null");
		else if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(status == null || status.length < 1)
			throw new IllegalArgumentException("Invalid status: null");
		else {
			this.list = list;
			this.index = index;
			this.type = this.type_of(location, status);
			this.location = location;
			if(this.type == InstrumentType.evaluate) {
				this.status = status;
			}
			else {
				this.status = new byte[0];
			}
		}
	}
	
	/* getters */
	/**
	 * @return the list of instrumental node
	 */
	public InstrumentList get_list() { return this.list; }
	/**
	 * @return the index of the node in list 
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the type of instrumental node
	 */
	public InstrumentType get_type() { return this.type; }
	/**
	 * @return location that the node describes 
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return the bytes status hold by the node
	 */
	public byte[] get_status() { return this.status; }
	
}
