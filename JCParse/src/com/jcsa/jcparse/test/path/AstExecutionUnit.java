package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * Unit in execution path is a tuple as {type, location, state}
 * @author yukimula
 *
 */
public class AstExecutionUnit {
	
	/* attributes */
	/** the type of the execution unit **/
	private AstExecutionType type;
	/** the location on which the unit is defined **/
	private AstNode location;
	/** the byte-sequence of state describing the unit **/
	private byte[] state;
	/**
	 * @param type the type of the execution unit
	 * @param location the location on which the unit is defined
	 * @throws IllegalArgumentException
	 */
	private AstExecutionUnit(AstExecutionType type, AstNode 
				location) throws IllegalArgumentException {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(location == null)
			throw new IllegalArgumentException("Invalid location as null");
		else {
			this.type = type;
			this.location = location;
			this.set_state(null);
		}
	}
	
	/* getters */
	/**
	 * @return the type of the execution unit
	 */
	public AstExecutionType get_type() { return this.type; }
	/**
	 * @return the location on which the unit is defined
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return the byte-sequence of state describing the unit
	 */
	public byte[] get_state() { return this.state; }
	/**
	 * @param state set byte-sequence states hold by the unit
	 */
	public void set_state(byte[] state) {
		if(state == null)
			this.state = new byte[0];
		else
			this.state = state;
	}
	/**
	 * @return the boolean value whether the state is zero
	 */
	public Boolean get_bool_state() {
		if(this.state.length == 0)
			return null;
		else {
			for(byte value : this.state) {
				if(value != 0) {
					return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		}
	}
	
	/* factory methods */
	/**
	 * @param location function_definition
	 * @return  
	 * @throws IllegalArgumentException
	 */
	public static AstExecutionUnit beg_func(AstNode location) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionType.beg_func, location);
	}
	/**
	 * @param location function_definition
	 * @return  
	 * @throws IllegalArgumentException
	 */
	public static AstExecutionUnit end_func(AstNode location) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionType.end_func, location);
	}
	/**
	 * @param location statement | init_declarator | init_declarator_list
	 * @return  
	 * @throws IllegalArgumentException
	 */
	public static AstExecutionUnit beg_stmt(AstNode location) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionType.beg_stmt, location);
	}
	/**
	 * @param location statement | init_declarator | init_declarator_list
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static AstExecutionUnit end_stmt(AstNode location) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionType.end_stmt, location);
	}
	/**
	 * @param location goto_statement
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static AstExecutionUnit execute(AstNode location) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionType.execute, location);
	}
	/**
	 * @param location expression | initializer | initializer_body | initializer_list
	 * @return 
	 * @throws IllegalArgumentException
	 */
	public static AstExecutionUnit beg_expr(AstNode location) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionType.beg_expr, location);
	}
	/**
	 * @param location expression | initializer | initializer_body | initializer_list
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static AstExecutionUnit end_expr(AstNode location) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionType.end_expr, location);
	}
	/**
	 * @param location declarator
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static AstExecutionUnit evaluate(AstNode location) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionType.evaluate, location);
	}
	public static AstExecutionUnit declare(AstNode location) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionType.declare, location);
	}
	
	@Override
	public String toString() {
		try {
			return this.type + "::\"" + this.location.generate_code() + 
						"\"#" + this.location.get_location().line_of();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
