package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * Each line in instrumental file is a tuple as:<br>
 * 	[list, index, type, location, state]
 * @author yukimula
 *
 */
public class InstrumentLine {
	
	/* attributes */
	/** the list of instrumental lines **/
	private InstrumentList list;
	/** the index of the line in the list **/
	private int index;
	/** the type of the instrumental line **/
	private InstrumentType type;
	/** the location on which the line is defined **/
	private AstNode location;
	/** the byte-sequence describing the state of the location **/
	private byte[] state;
	
	/* constructor */
	/**
	 * @param location
	 * @param state 
	 * @return the type of the instrumental line
	 * @throws IllegalArgumentException
	 */
	private InstrumentType type_of(AstNode location, byte[] state) throws IllegalArgumentException {
		if(location instanceof AstExpression) {
			return InstrumentType.evaluate;
		}
		else if(location instanceof AstStatement) {
			for(byte value : state) {
				if(value != 0) {
					return InstrumentType.end_stmt;
				}
			}
			return InstrumentType.beg_stmt;
		}
		else {
			throw new IllegalArgumentException("Invalid location: " + location);
		}
	}
	/**
	 * @param list the list of instrumental lines 
	 * @param index the index of the line in the list
	 * @param location the location on which the line is defined
	 * @param state the byte-sequence describing the state of the location
	 * @throws IllegalArgumentException
	 */
	protected InstrumentLine(InstrumentList list, int index, AstNode 
			location, byte[] state) throws IllegalArgumentException {
		if(list == null)
			throw new IllegalArgumentException("Invalid list: null");
		else if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(state == null || state.length == 0)
			throw new IllegalArgumentException("Invalid state as null");
		else {
			this.list = list;
			this.index = index;
			this.type = this.type_of(location, state);
			this.location = location;
			this.state = state;
		}
	}
	
	/* getters */
	/**
	 * @return the list of instrumental lines
	 */
	public InstrumentList get_list() { return this.list; }
	/**
	 * @return the index of the line in the list
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the type of the instrumental line
	 */
	public InstrumentType get_type() { return this.type; }
	/**
	 * @return the location on which the line is defined
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return the byte-sequence describing the state of the location
	 */
	public byte[] get_state() { return this.state; }
	
}
