package com.jcsa.jcparse.test.path;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

/**
 * [type, location, state]
 * @author yukimula
 *
 */
public class AstExecutionUnit {
	
	/* attributes */
	/** the type of execution node in form of AstNode **/
	private AstExecutionUnitType type;
	/** the abstract syntax node on which node occurs **/
	private AstNode location;
	/** the byte sequence describing the state of the node {end_expr} **/
	private byte[] state;
	
	/* constructor */
	/**
	 * @param type the type of execution node in form of AstNode 
	 * @param location the abstract syntax node on which node occurs
	 * @throws IllegalArgumentException
	 */
	private AstExecutionUnit(AstExecutionUnitType type, AstNode location) throws IllegalArgumentException {
		if(type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else {
			this.type = type;
			this.location = location;
			this.state = new byte[0];
		}
	}
	
	/* getters */
	/**
	 * @return the type of execution node in form of AstNode
	 */
	public AstExecutionUnitType get_type() { return this.type; }
	/**
	 * @return the abstract syntax node on which node occurs
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return the byte sequence describing the state of the node {end_expr}
	 */
	public byte[] get_state() { return this.state; }
	/**
	 * @param state the byte sequence describing the state of the node {end_expr}
	 */
	public void set_state(byte[] state) {
		if(state == null)
			this.state = new byte[0];
		else 
			this.state = state;
	}
	/**
	 * @return boolean value of the state or null
	 */
	protected Boolean get_boolean_state() {
		if(this.state.length == 0)
			return null;	// unable to fetch boolean
		else {
			for(byte value : this.state)
				if(value != 0)
					return Boolean.TRUE;
			return Boolean.FALSE;
		}
	}
	
	/* creators */
	/**
	 * @param def
	 * @return beg_func function_definition
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit beg_func(AstFunctionDefinition def) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.beg_func, def);
	}
	/**
	 * @param def
	 * @return end_func function_definition
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit end_func(AstFunctionDefinition def) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.end_func, def);
	}
	/**
	 * @param statement
	 * @return beg_stmt statement
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit beg_stmt(AstStatement statement) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.beg_stmt, statement);
	}
	/**
	 * @param statement
	 * @return end_stmt statement
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit end_stmt(AstStatement statement) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.end_stmt, statement);
	}
	/**
	 * @param statement
	 * @return execute leaf_statement
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit execute(AstStatement statement) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.execute, statement);
	}
	/**
	 * @param expression
	 * @return beg_expr expression
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit beg_expr(AstExpression expression) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.beg_expr, expression);
	}
	/**
	 * @param expression
	 * @return end_expr expression
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit end_expr(AstExpression expression) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.end_expr, expression);
	}
	/**
	 * @param expression
	 * @return evaluate expression
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit evaluate(AstExpression expression) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.evaluate, expression);
	}
	/**
	 * @param declarator
	 * @return declare declarator
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit declare(AstDeclarator declarator) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.declare, declarator);
	}
	/**
	 * @param init_decl
	 * @return beg_stmt init_declarator
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit beg_stmt(AstInitDeclarator init_decl) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.beg_stmt, init_decl);
	}
	/**
	 * @param init_decl
	 * @return end_stmt init_declarator
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit end_stmt(AstInitDeclarator init_decl) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.end_stmt, init_decl);
	}
	/**
	 * @param initializer
	 * @return beg_expr initializer
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit beg_expr(AstInitializer initializer) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.beg_expr, initializer);
	}
	/**
	 * @param initializer
	 * @return end_expr initializer
	 * @throws IllegalArgumentException
	 */
	protected static AstExecutionUnit end_expr(AstInitializer initializer) throws IllegalArgumentException {
		return new AstExecutionUnit(AstExecutionUnitType.end_expr, initializer);
	}
	
}
