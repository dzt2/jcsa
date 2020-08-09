package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

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
	private byte[] value;
	
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
	protected InstrumentalLine(InstrumentalTag tag, AstNode location) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {
			this.tag = tag;
			this.location = location;
			this.value = null;
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
	/**
	 * @param value set the value hold by the expression in the instrument
	 */
	protected void set_value(byte[] value) throws Exception { 
		if(value == null || value.length == 0)
			throw new IllegalArgumentException("Invalid value: null");
		this.value = value; 
	}
	
	/* factory methods */
	public InstrumentalLine call_fun(AstFunctionDefinition location) throws Exception {
		return new InstrumentalLine(InstrumentalTag.call_fun, location);
	}
	public InstrumentalLine exit_fun(AstFunctionDefinition location) throws Exception {
		return new InstrumentalLine(InstrumentalTag.exit_fun, location);
	}
	public InstrumentalLine beg_stmt(AstStatement location) throws Exception {
		return new InstrumentalLine(InstrumentalTag.beg_stmt, location);
	}
	public InstrumentalLine end_stmt(AstStatement location) throws Exception {
		return new InstrumentalLine(InstrumentalTag.end_stmt, location);
	}
	public InstrumentalLine execute(AstStatement location) throws Exception {
		return new InstrumentalLine(InstrumentalTag.execute, location);
	}
	public InstrumentalLine beg_expr(AstExpression location) throws Exception {
		return new InstrumentalLine(InstrumentalTag.beg_expr, location);
	}
	public InstrumentalLine end_expr(AstExpression location, byte[] value) throws Exception {
		InstrumentalLine line = new InstrumentalLine(
				InstrumentalTag.end_expr, location);
		line.set_value(value); return line;
	}
	public InstrumentalLine evaluate(AstExpression location, byte[] value) throws Exception {
		InstrumentalLine line = new InstrumentalLine(
				InstrumentalTag.evaluate, location);
		line.set_value(value); return line;
	}
	
}
