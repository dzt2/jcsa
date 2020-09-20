package com.jcsa.jcparse.test.inst;

import java.io.InputStream;
import java.nio.ByteBuffer;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;


/**
 * Each unit represents a time-point during execution, where the instrumentation
 * occurs. More specifically, an instrumental unit describes how the location in 
 * AST is instrumented and when it occurs during testing. The unit is defined as
 * a tuple of <code>(flag, location, value?)</code> where:<br>
 * 	1. <code>boolean flag</code> is true when this unit refers to the point when
 *     the <code>location</code> start to be evaluated or false to the time point
 *     where the evaluation on that <code>location</code> just completed.<br>
 *  2. <code>AstNode location</code> is the location of AST being instrumented.<br>
 *  3. <code>Object value</code> records the state hold as the value of location
 *     especially for location of <code>AstExpression</code>.<br>
 *  
 * @author yukimula
 *
 */
public class InstrumentalUnit {
	
	/* attributes */
	/** true refers to when execution gets into the location; and false to
	 *  when the execution gets out from the location. **/
	private boolean flag;
	/** the location evaluated, either expression, statement or function **/
	private AstNode location;
	/** the value hold by the location (as expression) when out from it **/
	private Object value;
	
	/* getters */
	/**
	 * @return whether the unit refers to the time point when the location
	 * 		   starts being instrumented.
	 */
	public boolean is_beg() { return this.flag; }
	/**
	 * @return whether the unit refers to the time point when the location
	 * 		   has been evaluated after.
	 */
	public boolean is_end() { return !this.flag; }
	/**
	 * @return the location being instrumented, being expression, statement
	 * 		   or the function definition (init_declarator, declarator...).
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return whether there is value to describe the state of the location
	 * 		   when it is an expression at the point of end.
	 */
	public boolean has_value() { return this.value != null; }
	/**
	 * @return the value hold by the location (as expression) when out from
	 * 		   it where this.flag is false && this.location is AstExpression.
	 */
	public Object get_value() { return this.value; }
	/**
	 * set the value hold by the location at the point of the unit corresponds
	 * @param value
	 */
	private void set_value(Object value) { this.value = value; }
	
	/* constructor */
	/**
	 * @param flag true refers to when execution gets into the location; and false to
	 * 		  when the execution gets out from the location.
	 * @param location being evaluated, either expression, statement or function
	 * @param value hold by the location (as expression) when it is out from it
	 * @throws IllegalArgumentException
	 */
	private InstrumentalUnit(boolean flag, AstNode location) throws IllegalArgumentException {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else {
			this.flag = flag;
			this.location = location;
			this.value = null;
		}
	}
	/**
	 * @param location
	 * @return begin of a function definition being called
	 * @throws IllegalArgumentException
	 */
	protected static InstrumentalUnit beg_func(AstFunctionDefinition 
				location) throws IllegalArgumentException {
		return new InstrumentalUnit(true, location);
	}
	/**
	 * @param location
	 * @return begin of a function definition being called
	 * @throws IllegalArgumentException
	 */
	protected static InstrumentalUnit end_func(AstFunctionDefinition 
				location) throws IllegalArgumentException {
		return new InstrumentalUnit(false, location);
	}
	/**
	 * @param location
	 * @return begin of the statement being executed
	 * @throws IllegalArgumentException
	 */
	protected static InstrumentalUnit beg_stmt(AstStatement 
				location) throws IllegalArgumentException {
		return new InstrumentalUnit(true, location);
	}
	/**
	 * @param location
	 * @return end of the statement being executed
	 * @throws IllegalArgumentException
	 */
	protected static InstrumentalUnit end_stmt(AstStatement 
				location) throws IllegalArgumentException {
		return new InstrumentalUnit(false, location);
	}
	/**
	 * @param location
	 * @return begin of the statement being executed
	 * @throws IllegalArgumentException
	 */
	protected static InstrumentalUnit beg_expr(AstExpression 
				location) throws IllegalArgumentException {
		return new InstrumentalUnit(true, location);
	}
	/**
	 * @param location
	 * @return begin of the statement being executed
	 * @throws IllegalArgumentException
	 */
	protected static InstrumentalUnit end_expr(AstExpression location, 
			Object value) throws IllegalArgumentException {
		InstrumentalUnit unit = new 
				InstrumentalUnit(false, location);
		unit.set_value(value);
		return unit;
	}
	
	/* parsing methods */
	/**
	 * @param content
	 * @return whether the content is all of zeros
	 */
	private static boolean is_zero(byte[] content) {
		for(byte element : content) {
			if(element != 0) {
				return false;
			}
		}
		return true;
	}
	/**
	 * @param template
	 * @param ast_tree
	 * @param stream
	 * @return the next instrumental line read from stream or null 
	 * 		   when the stream reaches the end of file.
	 * @throws Exception
	 */
	public static InstrumentalUnit read(CRunTemplate template,
			AstTree ast_tree, InputStream stream) throws Exception {
		if(template == null)
			throw new IllegalArgumentException("Invalid template");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree");
		else if(stream == null)
			throw new IllegalArgumentException("Invalid stream: null");
		else {
			/* 0. declarations */
			AstNode location; byte[] content; ByteBuffer buffer; int length;
			
			/* 1. read the identifier of AstNode */
			buffer = template.read(stream, CBasicTypeImpl.int_type);
			if(buffer == null) return null;	/* end-of-file */
			location = ast_tree.get_node(buffer.getInt());
			
			/* 2. read the length of content from file */
			buffer = template.read(stream, CBasicTypeImpl.uint_type);
			length = buffer.getInt();
			
			/* 3. read the byte content sequence from file */
			content = new byte[length]; stream.read(content);
			
			/* 4.1. generate the line from statement content */
			if(location instanceof AstStatement) {
				if(is_zero(content)) {
					return InstrumentalUnit.beg_stmt((AstStatement) location);
				}
				else {
					return InstrumentalUnit.end_stmt((AstStatement) location);
				}
			}
			/* 4.2. generate the line from expression value */
			else if(location instanceof AstExpression) {
				content = template.cast_bytes(content);
				CType type = ((AstExpression) location).get_value_type();
				Object value = template.generate_value(type, content);
				return InstrumentalUnit.
							end_expr((AstExpression) location, value);
			}
			/* 4.3. otherwise, invalid case for instrumental file */
			else {
				throw new IllegalArgumentException("Unsupport: " + location);
			}
		}
	}
	
	
}
