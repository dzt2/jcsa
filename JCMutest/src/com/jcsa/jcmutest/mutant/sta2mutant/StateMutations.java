package com.jcsa.jcmutest.mutant.sta2mutant;

import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.lang.symbol.eval.SymbolContext;

public class StateMutations {
	
	/* definitions */
	/** {true, false} **/
	public static final SymbolExpression bool_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@BoolValue");
	/** true **/
	public static final SymbolExpression true_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@TrueValue");
	/** false **/
	public static final SymbolExpression fals_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@FalsValue");
	/** integer or double **/
	public static final SymbolExpression numb_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NumbValue");
	/** { x | x > 0 } **/
	public static final SymbolExpression post_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@PostValue");
	/** { x | x < 0 } **/
	public static final SymbolExpression negt_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NegtValue");
	/** 0 **/
	public static final SymbolExpression zero_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@ZeroValue");
	/** { x | x <= 0 } **/
	public static final SymbolExpression npos_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NposValue");
	/** { x | x >= 0 } **/
	public static final SymbolExpression nneg_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnegValue");
	/** { x | x != 0 } **/
	public static final SymbolExpression nzro_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NzroValue");
	/** address value in pointer **/
	public static final SymbolExpression addr_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@AddrValue");
	/** null **/
	public static final SymbolExpression null_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NullValue");
	/** {p | p != null} **/
	public static final SymbolExpression nnul_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnulValue");
	/** abstract value of the exception **/
	public static final SymbolExpression trap_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@Exception");	
	
	/* type classifier */
	/**
	 * @param type
	 * @return void
	 */
	public 	static	boolean	is_void(CType type) { return SymbolFactory.is_void(type); }
	/**
	 * @param type
	 * @return boolean
	 */
	public 	static 	boolean	is_bool(CType type) { return SymbolFactory.is_bool(type); }
	/**
	 * @param type
	 * @return {char | uchar}
	 */
	public	static	boolean	is_char(CType type)	{ return SymbolFactory.is_char(type); }
	/**
	 * @param type
	 * @return {uchar | ushort | uint | ulong | ullong}
	 */
	public	static	boolean	is_usig(CType type)	{ return SymbolFactory.is_usig(type); }
	/**
	 * @param type
	 * @return {char | short | int | long | llong | enum}
	 */
	public	static	boolean	is_sign(CType type)	{ return SymbolFactory.is_sign(type); }
	/**
	 * @param type
	 * @return {char|uchar|short|ushort|int|uint|long|ulong|llong|ullong|enum}
	 */
	public	static	boolean	is_numb(CType type)	{ return SymbolFactory.is_numb(type); }
	/**
	 * @param type
	 * @return {float|double|ldouble}
	 */
	public	static	boolean	is_real(CType type)	{ return SymbolFactory.is_real(type); }
	/**
	 * @param type
	 * @return {array|point}
	 */
	public	static	boolean	is_addr(CType type)	{ return SymbolFactory.is_addr(type); }
	/**
	 * @param type
	 * @return {struct|union|function}
	 */
	public	static	boolean	is_auto(CType type)	{ return SymbolFactory.is_auto(type); }
	
	/* symbolic computation */
	/**
	 * @param expression
	 * @return whether the expression is trapping
	 */
	private static 	boolean	is_trap_value(SymbolNode expression) {
		if(expression instanceof SymbolIdentifier) {
			return expression.equals(trap_value);
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression contains trap
	 */
	public 	static 	boolean	has_trap_value(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(expression);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				if(is_trap_value(parent)) {
					return true;
				}
				else {
					for(SymbolNode child : parent.get_children()) {
						queue.add(child);
					}
				}
			}
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is abstract domain
	 */
	private	static	boolean	is_abst_value(SymbolNode expression) {
		if(expression instanceof SymbolIdentifier) {
			return 	expression.equals(bool_value) || expression.equals(true_value) || expression.equals(fals_value) ||
					expression.equals(numb_value) || expression.equals(post_value) || expression.equals(negt_value) ||
					expression.equals(zero_value) || expression.equals(npos_value) || expression.equals(nneg_value) ||
					expression.equals(nzro_value) || expression.equals(addr_value) || expression.equals(null_value) ||
					expression.equals(nnul_value);
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression contains abstract domain
	 */
	public 	static	boolean	has_abst_value(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(expression);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				if(is_abst_value(parent)) {
					return true;
				}
				else {
					for(SymbolNode child : parent.get_children()) {
						queue.add(child);
					}
				}
			}
			return false;
		}
	}
	/**
	 * @param expression
	 * @param in_context
	 * @param ou_context
	 * @return the expression being evaluated from input and output state
	 * @throws Exception
	 */
	public	static	SymbolExpression	evaluate(SymbolExpression expression, 
			SymbolContext in_context, SymbolContext ou_context) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(has_trap_value(expression)) { return trap_value; }
		else if(has_abst_value(expression)) { return expression; }
		else {
			try {
				return expression.evaluate(in_context, ou_context);
			}
			catch(ArithmeticException ex)  	{ return trap_value; }
		}
	}
	
}
