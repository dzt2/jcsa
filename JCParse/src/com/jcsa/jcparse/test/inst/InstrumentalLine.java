package com.jcsa.jcparse.test.inst;

import java.io.InputStream;
import java.nio.ByteBuffer;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * Each line in instrumental file is a tuple, describing the state hold
 * by the location in C source code, including:<br>
 * 
 * 	(1) <code>location: AstNode</code>: the statement or expression being
 * 		instrumented during testing process.<br>
 * 
 * 	(2)	<code>value: Object</code>: the Java-Object describes the value
 * 		of the location (as expression) being fetched in testing, or
 * 		boolean for statement to tag its beginning or end time.<br>
 * 
 * @author yukimula
 *
 */
public class InstrumentalLine {
	
	/* definition */
	/** true --> end of location; false --> start of location **/
	private boolean flag;
	/** the type of the location being instrumented **/
	private InstrumentalType type;
	/** the location in which the instrumental method is injected **/
	private AstNode location;
	/** the Java-Object describes the value hold by the location as
	 *  an expression or the boolean false as the beginning of the
	 *  statement or true to its end **/
	private Object value;
	
	/* constructor */
	/**
	 * create an instrumental line w.r.t. the location and value
	 * @param location
	 * @param value
	 * @throws IllegalArgumentException
	 */
	protected InstrumentalLine(boolean flag, AstNode location, Object value) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else {
			this.flag = flag;
			this.type = this.type_of(location);
			this.location = location;
			this.value = value;
		}
	}
	
	/* getters */
	/**
	 * @return whether the line is the start of the location
	 */
	public boolean is_beg() { return !this.flag; }
	/**
	 * @return whether the line is the end of the location
	 */
	public boolean is_end() { return this.flag; }
	/**
	 * @return the type of the location being instrumented
	 */
	public InstrumentalType get_type() { return this.type; }
	/**
	 * @return the location in which the instrumental method is injected
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return whether there is a value w.r.t. the line
	 */
	public boolean has_value() { return this.value != null; }
	/**
	 * @return the Java-Object describes the value hold by the location as
	 *  	   an expression or the boolean false as the beginning of the
	 *  	   statement or true to its end point.
	 */
	public Object get_value() { return this.value; }
	/**
	 * set the value of the line
	 * @param value
	 */
	protected void set_value(Object value) { this.value = value; }
	
	/* parser */
	private static boolean is_non_zero(byte[] content) {
		for(byte value : content) {
			if(value != 0) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @param template
	 * @param ast_tree
	 * @param stream
	 * @return read the next line from the stream of the instrumental 
	 * 		   file or null when stream reaches the end of file (EOF)
	 * @throws Exception
	 */
	public static InstrumentalLine read(CRunTemplate template,
			AstTree ast_tree, InputStream stream) throws Exception {
		if(template == null)
			throw new IllegalArgumentException("Invalid template: null");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(stream == null)
			throw new IllegalArgumentException("Invalid stream as null");
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
			
			/* 4.1. generate line for (statement, boolean) */
			if(location instanceof AstStatement) {
				return new InstrumentalLine(is_non_zero(content), location, null);
			}
			/* 4.2. generate line for (expression, value) */
			else if(location instanceof AstExpression) {
				CType type = CTypeAnalyzer.get_value_type(
						((AstExpression) location).get_value_type());
				return new InstrumentalLine(true, location, 
						template.generate_value(type, content));
			}
			/* 4.3. otherwise, throw exception to the users */
			else {
				throw new IllegalArgumentException(location.toString());
			}
		}
	}
	
	/* type inference */
	private AstNode[] get_parent_child(AstNode location) {
		AstNode child = location;
		AstNode parent = location.get_parent();
		while(parent != null) {
			if(parent instanceof AstParanthExpression
				|| parent instanceof AstConstExpression
				|| parent instanceof AstInitializer) {
				child = parent;
				parent = parent.get_parent();
			}
			else {
				break;
			}
		}
		return new AstNode[] { parent, child };
	}
	private boolean is_function(AstNode location) {
		return location instanceof AstFunctionDefinition;
	}
	private boolean is_statement(AstNode location) {
		return location instanceof AstStatement;
	}
	private boolean is_assignment(AstNode location) {
		if(location instanceof AstAssignExpression
			|| location instanceof AstArithAssignExpression
			|| location instanceof AstBitwiseAssignExpression
			|| location instanceof AstShiftAssignExpression) {
			return true;
		}
		else if(location instanceof AstIncreUnaryExpression
				|| location instanceof AstIncrePostfixExpression
				|| location instanceof AstInitDeclarator) {
			return true;
		}
		else {
			return false;
		}
	}
	private boolean is_sequence(AstNode location) {
		return location instanceof AstCommaExpression
				|| location instanceof AstLiteral
				|| location instanceof AstInitializerBody
				|| location instanceof AstArgumentList;
	}
	private boolean is_condition(AstNode location) {
		AstNode[] parent_child = this.get_parent_child(location);
		AstNode parent = parent_child[0], parent_parent;
		AstNode child = parent_child[1];
		if(parent instanceof AstConditionalExpression) {
			return ((AstConditionalExpression) parent).get_condition() == child;
		}
		else if(parent instanceof AstIfStatement) {
			return ((AstIfStatement) parent).get_condition() == child;
		}
		else if(parent instanceof AstWhileStatement) {
			return ((AstWhileStatement) parent).get_condition() == child;
		}
		else if(parent instanceof AstDoWhileStatement) {
			return ((AstDoWhileStatement) parent).get_condition() == child;
		}
		else if(parent instanceof AstExpressionStatement) {
			parent_parent = parent.get_parent();
			if(parent_parent instanceof AstForStatement) {
				return ((AstForStatement) parent_parent).get_condition() == parent;
			}
			else {
				return false;
			}
		}
		else if(parent instanceof AstLogicUnaryExpression) {
			return ((AstLogicUnaryExpression) parent).get_operand() == child;
		}
		else if(parent instanceof AstLogicBinaryExpression) {
			return ((AstLogicBinaryExpression) parent).get_loperand() == child ||
					((AstLogicBinaryExpression) parent).get_roperand() == child;
		}
		else {
			return false;
		}
	}
	private boolean is_reference(AstNode location) {
		AstNode[] parent_child = this.get_parent_child(location);
		AstNode parent = parent_child[0];
		AstNode child = parent_child[1];
		if(location instanceof AstDeclarator) {
			return true;
		}
		else if(parent instanceof AstAssignExpression
			|| parent instanceof AstArithAssignExpression
			|| parent instanceof AstBitwiseAssignExpression
			|| parent instanceof AstShiftAssignExpression) {
			return ((AstBinaryExpression) parent).get_loperand() == child;
		}
		else if(parent instanceof AstIncreUnaryExpression) {
			return ((AstIncreUnaryExpression) parent).get_operand() == child;
		}
		else if(parent instanceof AstIncrePostfixExpression) {
			return ((AstIncrePostfixExpression) parent).get_operand() == child;
		}
		else if(parent instanceof AstFieldExpression) {
			if(((AstFieldExpression) parent).
					get_operator().get_punctuator() == CPunctuator.dot) {
				return ((AstFieldExpression) parent).get_body() == child;
			}
			else {
				return false;
			}
		}
		else if(parent instanceof AstPointUnaryExpression) {
			if(((AstPointUnaryExpression) parent).
					get_operator().get_operator() == COperator.dereference) {
				return ((AstPointUnaryExpression) parent).get_operand() == child;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	private InstrumentalType type_of(AstNode location) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(this.is_function(location))
			return InstrumentalType.function;
		else if(this.is_statement(location))
			return InstrumentalType.statement;
		else if(this.is_assignment(location))
			return InstrumentalType.assignment;
		else if(this.is_sequence(location))
			return InstrumentalType.sequence;
		else if(this.is_condition(location))
			return InstrumentalType.condition;
		else if(this.is_reference(location))
			return InstrumentalType.reference;
		else if(location instanceof AstExpression)
			return InstrumentalType.expression;
		else
			throw new IllegalArgumentException("Unsupport: " + location);
	}
	
	@Override
	public String toString() {
		if(this.flag)
			return "end::" + this.location.getClass().getSimpleName() + "::" + this.value;
		else
			return "beg::" + this.location.getClass().getSimpleName() + "::" + this.value;
	}
	
}
