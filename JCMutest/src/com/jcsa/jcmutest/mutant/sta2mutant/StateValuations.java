package com.jcsa.jcmutest.mutant.sta2mutant;

import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * This class defines the constant values used to describe execution states
 * (abstract) in mutation analysis.
 * 
 * @author yukimula
 *
 */
public final class StateValuations {
	
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
	
	/* exception-included symbolic computation */
	/**
	 * @param root
	 * @return whether the node has trap_value among it.
	 */
	private static boolean has_trap_value(SymbolNode root) {
		Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
		queue.add(root); SymbolNode parent;
		while(!queue.isEmpty()) {
			parent = queue.poll();
			if(parent.is_leaf()) {
				if(parent instanceof SymbolIdentifier
					&& parent.equals(trap_value))
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
	/**
	 * @param expression
	 * @param context
	 * @return trap_value iff. arithmetic operation occurs
	 * @throws Exception
	 */
	private static SymbolExpression compute(SymbolExpression expression, SymbolProcess context) throws Exception {
		/* input-validate */
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		
		/* normalization */
		try {
			expression = expression.evaluate(context);
		}
		catch(ArithmeticException ex) {
			expression = trap_value;
		}
		
		/* reconstruction */
		if(has_trap_value(expression)) {
			return trap_value;
		}
		else {
			return expression;
		}
	}
	/**
	 * @param expression
	 * @param context
	 * @return optimized expression from the context or trap_value
	 * @throws Exception
	 */
	public static SymbolExpression evaluate(SymbolExpression expression, SymbolProcess context) throws Exception {
		return compute(expression, context);
	}
	/**
	 * @param expression
	 * @return optimized expression from the context or trap_value
	 * @throws Exception
	 */
	public static SymbolExpression evaluate(SymbolExpression expression) throws Exception {
		return compute(expression, null);
	}
	
}
