package com.jcsa.jcparse.test.inst;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * 	The unit in instrumental file describes the state of an abstract syntactic node
 * 	at a time-point during the program testing, in which:<br>
 * 	<br>
 * 	1. <i>tag</i>: the tag that defines the time-point at which the unit occurs;<br>
 * 	2. <i>location</i>: the abstract syntactic node where the instrument occurs;<br>
 * 	3. <i>value</i>: the byte-string describing the state hold by this location;<br>
 * 	<br>
 * 
 * 	@author yukimula
 *
 */
public class InstrumentalUnit {
	
	/* definition */
	/** the tag that defines the time-point at which the unit occurs **/
	private InstrumentalTag tag;
	/** the abstract syntactic node where the instrument occurs **/
	private AstNode location;
	/** the byte-string describing the state hold by this location **/
	private byte[] value;
	/**
	 * @param tag the tag that defines the time-point at which the unit occurs
	 * @param location the abstract syntactic node where the instrument occurs
	 * @param value the byte-string describing the state hold by this location
	 * @throws Exception 
	 */
	protected InstrumentalUnit(InstrumentalTag tag, 
				AstNode location) throws Exception {
		if(tag == null)
			throw new IllegalArgumentException("Invalid tag: null");
		else if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else {
			this.tag = tag;
			this.location = location;
			this.value = null;
		}
	}
	
	/* getters */
	/**
	 * @return the tag that defines the time-point at which the unit occurs
	 */
	public InstrumentalTag get_tag() { return this.tag; }
	/**
	 * @return whether the time-point is the start of the node
	 */
	public boolean is_begin() { 
		return this.tag == InstrumentalTag.beg || this.tag == InstrumentalTag.pas; 
	}
	/**
	 * @return whether the time-point is the end of the node
	 */
	public boolean is_end() {
		return this.tag == InstrumentalTag.end || this.tag == InstrumentalTag.pas; 
	}
	/**
	 * @return the abstract syntactic node where the instrument occurs
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return the byte-string describing the state hold by this location
	 */
	public byte[] get_value() { return this.value; }
	/**
	 * @return whether there is value hold by the (expression) node in the 
	 * 		   instrumental unit
	 */
	public boolean has_value() { return this.value != null; }
	@Override
	public String toString() {
		String ast_type = this.location.getClass().getSimpleName();
		ast_type = ast_type.substring(3, ast_type.length() - 4).strip();
		return this.tag + "::" + ast_type + "[" + location.get_key() + "]";
	}
	
	/* protected methods */
	/**
	 * update the tag of the unit
	 * @param tag
	 */
	protected void set_tag(InstrumentalTag tag) { this.tag = tag; }
	/**
	 * @param value the byte-string being set to the value of the expression
	 */
	protected void set_value(byte[] value) { this.value = value; }
	/**
	 * @param unit beg or end of some location (ignoring its value)
	 * @return whether the time-point of this unit matches with the specified point.
	 */
	protected boolean match(InstrumentalUnit unit) {
		if(unit == null) {
			return false;
		}
		else if(unit.location == this.location) {
			switch(this.tag) {
			case beg:	return unit.is_begin();
			case end:	return unit.is_end();
			default:	return true;
			}
		}
		else {
			return false;
		}
	}
	
}
