package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * The unit in the node of execution path is a tuple as:<br>
 * 	<code>(type, location, status)</code><br>
 * 
 * @author yukimula
 *
 */
public class AstExecutionUnit {
	
	/* attributes */
	/** the node where the unit is performed **/
	protected AstExecutionNode node;
	/** type of the execution unit **/
	private AstExecutionType type;
	/** the AST node that the unit is performed **/
	private AstNode location;
	/** the status recorded for the location if it is end_expr **/
	private byte[] status;
	
	/* constructor */
	/**
	 * @param type type of the execution unit
	 * @param location the AST node that the unit is performed
	 * @param status the status recorded for the location if it is end_expr
	 * @throws IllegalArgumentException
	 */
	protected AstExecutionUnit(AstExecutionType type, AstNode 
			location, byte[] status) throws IllegalArgumentException {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(location == null)
			throw new IllegalArgumentException("Invalid location");
		else if(status == null)
			throw new IllegalArgumentException("Invalid status: null");
		else {
			this.type = type;
			this.location = location;
			if(type == AstExecutionType.end_expr) {
				this.status = status;
			}
			else {
				this.status = new byte[0];
			}
		}
	}
	
	/* getters */
	/**
	 * @return the node where the unit is performed
	 */
	public AstExecutionNode get_node() { return this.node; }
	/**
	 * @return type of the execution unit
	 */
	public AstExecutionType get_type() { return this.type; }
	/**
	 * @return the AST node that the unit is performed
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return the status recorded for the location if it is end_expr
	 */
	public byte[] get_status() { return this.status; }
	
}
