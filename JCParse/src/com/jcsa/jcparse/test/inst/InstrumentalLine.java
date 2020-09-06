package com.jcsa.jcparse.test.inst;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * Each line from the instrumental data file contains three parts:<br>
 * 	1. location: where the instrumentation is injected in source code;<br>
 * 	2. type: the type of the instrumentation methods, either the start
 * 			 or end of a statement, or value of an expression.<br>
 * 	3. value: the bytes sequence that defines the value of expression.<br>
 * @author yukimula
 *
 */
public class InstrumentalLine {
	
	/* definitions */
	/** the type of the instrumentation **/
	private InstrumentalType type;
	/** location where instrumentation is seeded **/
	private AstNode location;
	/** the byte sequence that defines value of the expression or null **/
	private byte[] value;
	/**
	 * create an instrumental line read from the instrumental file
	 * @param location statement or expression
	 * @param value byte sequence following the id
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
			this.type = InstrumentalType.pas_expr;
			this.location = location;
			this.value = value;
		}
		else if(location instanceof AstStatement) {
			boolean beg_or_end = true;
			for(int k = 0; k < value.length; k++) {
				if(value[k] != 0) {
					beg_or_end = false; 
					break;
				}
			}
			this.type = beg_or_end ? InstrumentalType.
					beg_stmt : InstrumentalType.end_stmt;
			this.location = location;
			this.value = null;
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + location);
		}
	}
	
	/* getters */
	/**
	 * @return the type of the instrumentation
	 */
	public InstrumentalType get_type() { return this.type; }
	/**
	 * @return location where instrumentation is seeded
	 */
	public AstNode get_location() { return this.location;  }
	/**
	 * @return whether the line defines the value
	 */
	public boolean has_value() { return this.value != null; }
	/**
	 * @return the byte sequence that defines value of the expression or null
	 */
	public byte[] get_value() { return this.value; }
	
}
