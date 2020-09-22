package com.jcsa.jcparse.test.backup;

import com.jcsa.jcparse.lang.astree.AstNode;
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
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * The basic data unit fetched from instrumental file is a tuple, which describes the
 * state of locations being instrumented during the execution process, or denoted as 
 * <code>(type, location, value)</code> in which:<br>
 * 	1. <b>InstrumentalUnit.type</b> defines the type of the location being injected;<br>
 * 	2. <b>InstrumentalUnit.location</b> refers to the <code>AstNode</code> in which
 * 	   the instrumental method is injected and watched its value in testing.<br>
 * 	3. <b>InstrumentalUnit.value</b> describes the value hold by the point.<br>
 * 
 * @author yukimula
 *
 */
public class InstrumentalUnit {
	
	/* definitions */
	/** type of the location being instrumented **/
	private InstrumentalType type;
	/** the location being instrumented **/
	private AstNode location;
	/** the value hold at that point during instrumentation **/
	private Object value;
	
	/* type inference */
	/**
	 * @param location
	 * @return the [parent, child] of the context where the location is in,
	 * 		   used to determine the type of location.
	 */
	private static AstNode[] get_parent_child(AstNode location) {
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
		if(parent == null)
			throw new IllegalArgumentException("Unsupport: " + location);
		else
			return new AstNode[] { parent, child };
	}
	/**
	 * @param location
	 * @return whether the location is of function type
	 */
	private static boolean is_function(AstNode location) {
		return location instanceof AstFunctionDefinition;
	}
	/**
	 * @param location
	 * @return whether the location is of statement type
	 */
	private static boolean is_statement(AstNode location) {
		return location instanceof AstStatement;
	}
	/**
	 * @param location
	 * @return whether the location is of sequence type
	 */
	private static boolean is_sequence(AstNode location) {
		return location instanceof AstLiteral
				|| location instanceof AstCommaExpression
				|| location instanceof AstInitializerBody
				|| location instanceof AstArgumentList;
	}
	/**
	 * @param location
	 * @return whether the location is of assignment type
	 */
	private static boolean is_assignment(AstNode location) {
		if(location instanceof AstInitDeclarator)
			return true;
		else if(location instanceof AstAssignExpression
				|| location instanceof AstArithAssignExpression
				|| location instanceof AstBitwiseAssignExpression
				|| location instanceof AstShiftAssignExpression)
			return true;
		else
			return false;
	}
	/**
	 * @param location
	 * @return whether the location is taken used as a condition
	 */
	private static boolean is_condition(AstNode location) {
		AstNode[] parent_child = get_parent_child(location);
		AstNode parent = parent_child[0], child = parent_child[1];
		if(parent instanceof AstConditionalExpression) 
			return ((AstConditionalExpression) parent).get_condition() == child;
		else if(parent instanceof AstIfStatement) 
			return ((AstIfStatement) parent).get_condition() == child;
		else if(parent instanceof AstWhileStatement)
			return ((AstWhileStatement) parent).get_condition() == child;
		else if(parent instanceof AstDoWhileStatement)
			return ((AstDoWhileStatement) parent).get_condition() == child;
		else if(parent instanceof AstExpressionStatement) {
			AstNode parent_parent = parent.get_parent();
			if(parent_parent instanceof AstForStatement) 
				return ((AstForStatement) parent_parent).get_condition() == child;
			else 
				return false;
		}
		else if(parent instanceof AstLogicUnaryExpression) 
			return ((AstLogicUnaryExpression) parent).get_operand() == child;
		else if(parent instanceof AstLogicBinaryExpression) 
			return (((AstLogicBinaryExpression) parent).get_loperand() == child)
					|| (((AstLogicBinaryExpression) parent).get_roperand() == child);
		else 
			return false;
	}
	/**
	 * @param location
	 * @return whether the location is of reference (not instrumented)
	 */
	private static boolean is_reference(AstNode location) {
		AstNode[] parent_child = get_parent_child(location);
		AstNode parent = parent_child[0], child = parent_child[1];
		if(parent instanceof AstIncreUnaryExpression)
			return ((AstIncreUnaryExpression) parent).get_operand() == child;
		else if(parent instanceof AstIncrePostfixExpression)
			return ((AstIncrePostfixExpression) parent).get_operand() == child;
		else if(parent instanceof AstInitDeclarator)
			return ((AstInitDeclarator) parent).get_declarator() == child;
		else if(parent instanceof AstAssignExpression
				|| parent instanceof AstArithAssignExpression
				|| parent instanceof AstBitwiseAssignExpression
				|| parent instanceof AstShiftAssignExpression)
			return ((AstBinaryExpression) parent).get_loperand() == child;
		else if(parent instanceof AstPointUnaryExpression) {
			if(((AstPointUnaryExpression) parent).
					get_operator().get_operator() == COperator.address_of)
				return ((AstPointUnaryExpression) parent).get_operand() == child;
			else
				return false;
		}
		else if(parent instanceof AstFieldExpression) {
			if(((AstFieldExpression) parent).
					get_operator().get_punctuator() == CPunctuator.dot) 
				return ((AstFieldExpression) parent).get_body() == child;
			else
				return false;
		}
		else
			return false;
	}
	/**
	 * @param location
	 * @return the type of the location being instrumented
	 * @throws IllegalArgumentException
	 */
	public static InstrumentalType type_of(AstNode location) throws IllegalArgumentException {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(is_function(location))
			return InstrumentalType.function;
		else if(is_statement(location))
			return InstrumentalType.statement;
		else if(is_sequence(location))
			return InstrumentalType.sequence;
		else if(is_condition(location))
			return InstrumentalType.condition;
		else if(is_assignment(location))
			return InstrumentalType.assignment;
		else if(is_reference(location))
			return InstrumentalType.reference;
		else if(location instanceof AstExpression)
			return InstrumentalType.expression;
		else
			throw new IllegalArgumentException("Unsupport: " + location);
	}
	
	/* constructor */
	/**
	 * create a non-valued instrumental unit w.r.t. the location and specified type
	 * @param location
	 * @throws IllegalArgumentException
	 */
	protected InstrumentalUnit(AstNode location) throws IllegalArgumentException {
		this.type = InstrumentalUnit.type_of(location);
		this.location = location;
		this.value = null;
	}
	
	/* getters */
	/**
	 * @return type of the location being instrumented
	 */
	public InstrumentalType get_type() { return this.type; }
	/**
	 * @return the location being instrumented
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return whether the instrumental unit is evaluated
	 */
	public boolean has_value() { return this.value != null; } 
	/**
	 * @return the value hold at that point during instrumentation
	 */
	public Object get_value() { return this.value; }
	/**
	 * set the value hold by the instrumental data unit.
	 * @param value
	 */
	protected void set_value(Object value) { this.value = value; }
	
	/* value getters */
	/**
	 * @return get the boolean value of the value
	 * @throws Exception
	 */
	public boolean get_bool() throws Exception {
		if(this.value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		}
		else if(value instanceof Character) {
			return ((Character) value).charValue() != 0;
		}
		else if(value instanceof Short) {
			return ((Short) value).shortValue() != 0;
		}
		else if(value instanceof Integer) {
			return ((Integer) value).intValue() != 0;
		}
		else if(value instanceof Long) {
			return ((Long) value).longValue() != 0L;
		}
		else if(value instanceof Float) {
			return ((Float) value).floatValue() != 0.0f;
		}
		else if(value instanceof Double) {
			return ((Double) value).doubleValue() != 0.0;
		}
		else {
			throw new IllegalArgumentException("Invalid: " + value);
		}
	}
	
}
