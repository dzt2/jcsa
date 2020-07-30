package com.jcsa.jcparse.flwa.dynamics;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * Each element in execution path in form of AstNode describes:<br>
 * 	(1) data_value: the data result evaluated from an expression (when it is completed) node 
 * 		and was recorded during the testing process.<br>
 * 	(2)	beg_stmt | end_stmt: the statement node is started or completed at that point.<br>
 * 
 * @author yukimula
 *
 */
public class AstPathElement {
	
	/** the path in which the element is created **/
	private AstPath path;
	/** the index of the element in execution path **/
	private int index;
	/** the type of the element in AST execution path **/
	private AstPathElementType type;
	/** the AST-node of which state is described by this element **/
	private AstNode node;
	/** the byte sequence that describes the status of the element **/
	private byte[] status;
	
	/**
	 * create an element in execution path in form of AST-node
	 * @param path the execution path in which the element is created
	 * @param index the index of the element in execution path
	 * @param node the AST-node of which state is described by this element
	 * @param status the byte sequence that describes the status of the element
	 * @throws Exception
	 */
	protected AstPathElement(AstPath path, int index, AstNode node, byte[] status) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else if(status == null)
			throw new IllegalArgumentException("Invalid status: null");
		else if(node instanceof AstStatement) {
			this.path = path;
			this.index = index;
			if(status[0] == 0) {
				this.type = AstPathElementType.beg_stmt;
			}
			else {
				this.type = AstPathElementType.end_stmt;
			}
			this.node = node;
			this.status = new byte[0];
		}
		else if(node instanceof AstExpression) {
			this.path = path;
			this.index = index;
			this.type = AstPathElementType.data_value;
			this.node = node;
			this.status = status;
		}
		else
			throw new IllegalArgumentException("Invalid node: " + node);
	}
	
	/* getters */
	/**
	 * @return the path in which the element is created
	 */
	public AstPath get_path() { return this.path; }
	/**
	 * @return the index of the element in execution path
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the type of the element in AST execution path
	 */
	public AstPathElementType get_type() { return this.type; }
	/**
	 * @return the AST-node of which state is described by this element
	 */
	public AstNode get_ast_location() { return this.node; }
	/**
	 * @return byte sequence that describes the status of the element
	 */
	public byte[] get_ast_status() { return this.status; }
	
}
