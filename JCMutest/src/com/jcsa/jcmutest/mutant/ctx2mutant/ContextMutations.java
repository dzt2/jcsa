package com.jcsa.jcmutest.mutant.ctx2mutant;

import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.lang.symbol.eval.SymbolContext;

/**
 * 	It implements the interfaces for supporting the utility methods of Context
 * 	based mutation analysis.
 * 	
 * 	@author yukimula
 *
 */
public class ContextMutations {
	
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
	
	/* symbolic interfaces */
	/**
	 * @param node
	 * @return whether the node is a trap_value
	 */
	private	static	boolean is_trap_value(SymbolNode node) {
		if(node == null) { return false; }
		else if(node instanceof SymbolIdentifier) {
			return node.equals(trap_value);
		}
		else { return false; }
	}
	/**
	 * @param node
	 * @return whether the node is abstract domain expression
	 */
	private static	boolean	is_abst_value(SymbolNode node) {
		if(node == null) { return false; }
		else if(node instanceof SymbolIdentifier) {
			return 	node.equals(bool_value) || node.equals(true_value) || node.equals(fals_value) ||
					node.equals(numb_value) || node.equals(post_value) || node.equals(negt_value) ||
					node.equals(npos_value) || node.equals(nneg_value) || node.equals(zero_value) ||
					node.equals(nzro_value) || node.equals(addr_value) || node.equals(null_value) ||
					node.equals(nnul_value);
		}
		else { return false; }
	}
	/**
	 * @param source
	 * @return whether the source contains trap_value expression
	 */
	public	static	boolean	has_trap_value(SymbolExpression source) {
		if(source == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(source);
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
	 * @param source
	 * @return whether the source contains trap_value expression
	 */
	public	static	boolean	has_abst_value(SymbolExpression source) {
		if(source == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(source);
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
	 * @param expression	the symbolic expression to be evaluated based on state
	 * @param in_context	the input state from which the evaluation starts
	 * @param ou_context	the output state to preserve the result of evaluations
	 * @return				the expression evaluated from the state in arithmetic-safe way
	 * @throws Exception
	 */
	public	static	SymbolExpression evaluate(SymbolExpression expression, 
			SymbolContext in_context, SymbolContext ou_context) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			try {
				return expression.evaluate(in_context, ou_context);
			}
			catch(ArithmeticException ex) { return trap_value; }
		}
	}
	
	
	
	
}
