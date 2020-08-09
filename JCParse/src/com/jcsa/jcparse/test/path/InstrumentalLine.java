package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * 	Each line in instrumental file is a tuple as 
 * 	<code>(tag, location, value)</code> in which:<br>
 * 	1. tag describes the type of the location and its semantic;<br>
 * 	2. location refers to a AstNode where instrument is seeded;<br>
 * 	3. value is a byte-string describing the value hold by the
 * 	   location when location is a AstExpression.<br>
 * 	
 * @author yukimula
 *
 */
public class InstrumentalLine {
	
	/* attributes */
	/** the tag defines the line **/
	private InstrumentalTag tag;
	/** the location in which the instrument is seeded **/
	private AstNode location;
	/** the value hold at the location (expression) when the instrument is executed **/
	protected byte[] value;
	
	/* constructor */
	/**
	 * @param tag the tag that defines the line
	 * @param location in which the instrument is seeded
	 * @param value hold at the location (expression) when the instrument is executed
	 * @throws Exception
	 */
	protected InstrumentalLine(AstNode location, byte[] value) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(value == null || value.length == 0) {
			throw new IllegalArgumentException("Invalid value as null");
		}
		else if(location instanceof AstExpression) {
			this.tag = InstrumentalTag.evaluate;
			this.location = location;
			this.value = value;
		}
		else if(location instanceof AstStatement) {
			boolean is_beginning = true;
			for(byte item : value) {
				if(item != 0) {
					is_beginning = false;
				}
			}
			
			if(is_beginning)
				this.tag = InstrumentalTag.beg_stmt;
			else
				this.tag = InstrumentalTag.end_stmt;
			this.location = location;
			this.value = null;
		}
		else {
			throw new IllegalArgumentException("Invalid node: " + location);
		}
	}
	/**
	 * @param tag the tag that defines the line
	 * @param location in which the instrument is seeded
	 * @param value hold at the location (expression) when the instrument is executed
	 */
	protected InstrumentalLine(InstrumentalTag tag, AstNode 
				location, byte[] value) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {
			this.tag = tag;
			this.location = location;
			this.value = value;
		}
	}
	
	/* getters */
	/**
	 * @return the tag that defines the instrumental line
	 */
	public InstrumentalTag get_tag() { return this.tag; }
	/**
	 * @return the location in which the instrument is seeded
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return whether the line describes the value hold by the location
	 */
	public boolean has_value() { return this.value != null; }
	/**
	 * @return value hold at the location (expression) when the instrument 
	 * 		   was executed
	 */
	public byte[] get_value() { return this.value; }
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.tag.toString()).append("::");
		String ast_type = this.location.getClass().getSimpleName();
		ast_type = ast_type.substring(3, ast_type.length() - 4).strip();
		buffer.append(ast_type + "[" + this.location.get_key() + "]");
		if(this.value != null) {
			buffer.append("::{");
			for(byte item : this.value) {
				buffer.append(" " + item);
			}
			buffer.append(" }");
		}
		return buffer.toString();
	}
	/**
	 * @param line
	 * @return whether this line matches with the line
	 */
	public boolean match(InstrumentalLine line) {
		if(line == null) {
			return false;
		}
		else if(line.location == this.location) {
			if(this.tag == InstrumentalTag.execute) {
				return line.tag == InstrumentalTag.beg_stmt
						|| line.tag == InstrumentalTag.end_stmt
						|| line.tag == InstrumentalTag.execute;
			}
			else if(this.tag == InstrumentalTag.beg_stmt) {
				return line.tag == InstrumentalTag.beg_stmt
						|| line.tag == InstrumentalTag.execute;
			}
			else if(this.tag == InstrumentalTag.end_stmt) {
				return line.tag == InstrumentalTag.end_stmt
						|| line.tag == InstrumentalTag.execute;
			}
			else if(this.tag == InstrumentalTag.beg_expr) {
				return line.tag == InstrumentalTag.beg_expr
						|| line.tag == InstrumentalTag.evaluate;
			}
			else if(this.tag == InstrumentalTag.end_expr) {
				return line.tag == InstrumentalTag.end_expr
						|| line.tag == InstrumentalTag.evaluate;
			}
			else if(this.tag == InstrumentalTag.evaluate) {
				return line.tag == InstrumentalTag.beg_expr
						|| line.tag == InstrumentalTag.evaluate
						|| line.tag == InstrumentalTag.end_expr;
			}
			else {
				return this.tag == line.tag;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @return whether the node is the entry of the location
	 */
	public boolean is_entry() {
		switch(this.tag) {
		case call_fun:
		case beg_stmt:
		case beg_expr:
		case beg_node:
		case execute:
		case evaluate:	return true;
		default: return false;
		}
	}
	/**
	 * @return whether the node is the exit of the location
	 */
	public boolean is_exit() {
		switch(this.tag) {
		case exit_fun:
		case end_stmt:
		case end_expr:
		case end_node:
		case execute:
		case evaluate:	return true;
		default: return false;
		}
	}
	
}
