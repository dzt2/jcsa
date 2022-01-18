package com.jcsa.jcmutest.mutant.sta2mutant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParsers;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
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
		if(expression == null) {												/* input-validate */
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(has_trap_value(expression)) {									/* trap at this point */
			return trap_value;
		}
		else {																	/* otherwise, compute */
			try {
				expression = expression.evaluate(context);
			}
			catch(ArithmeticException ex) {
				expression = trap_value;
			}
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
	
	/* type classifier */
	/**
	 * @param data_type
	 * @return
	 */
	public static boolean is_void(CType data_type) {
		try {
			return CTypeAnalyzer.is_void(data_type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is a boolean
	 */
	public static boolean is_boolean(CType data_type) {
		try {
			return CTypeAnalyzer.is_boolean(data_type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is a unsigned
	 */
	public static boolean is_usigned(CType data_type) {
		try {
			if(CTypeAnalyzer.is_integer(data_type)) {
				return CTypeAnalyzer.is_unsigned(data_type);
			}
			else {
				return false;
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is a integer
	 */
	public static boolean is_integer(CType data_type) {
		try {
			return CTypeAnalyzer.is_integer(data_type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is real
	 */
	public static boolean is_doubles(CType data_type) {
		try {
			return CTypeAnalyzer.is_real(data_type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is integer or real
	 */
	public static boolean is_numeric(CType data_type) {
		try {
			return CTypeAnalyzer.is_integer(data_type) || CTypeAnalyzer.is_real(data_type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is a address pointer
	 */
	public static boolean is_address(CType data_type) {
		try {
			return CTypeAnalyzer.is_pointer(data_type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/* location classifier */
	/**
	 * @param expression
	 * @return
	 */
	public static boolean is_void(CirExpression expression) {
		return CirMutations.is_void(expression.get_data_type());
	}
	/**
	 * @param location
	 * @return whether the expression is a boolean
	 */
	public static boolean is_boolean(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(is_boolean(expression.get_data_type())) {
			return true;
		}
		else if(expression.get_parent() instanceof CirStatement) {
			CirStatement statement = expression.statement_of();
			return statement instanceof CirIfStatement || statement instanceof CirCaseStatement;
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a unsigned integer
	 */
	public static boolean is_usigned(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_usigned(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is an integer
	 */
	public static boolean is_integer(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_integer(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a real
	 */
	public static boolean is_doubles(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_doubles(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a real or integer
	 */
	public static boolean is_numeric(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_integer(expression) || is_doubles(expression);
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a pointer
	 */
	public static boolean is_address(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_address(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a reference defined in left-side of assignment
	 */
	public static boolean is_assigned(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(expression.get_parent() instanceof CirAssignStatement) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			return statement.get_lvalue() == expression;
		}
		else {
			return false;
		}
	}
	
	/* mutation create */
	/**
	 * It creates a RIP-model of state mutation into some given point.
	 * @param point
	 * @param istate
	 * @param pstate
	 * @throws Exception
	 */
	public static StateMutation new_mutation(CirExecution point, 
			CirConditionState istate, CirAbstErrorState pstate) throws Exception {
		return new StateMutation(point, istate, pstate);
	}
	/**
	 * It parses the syntactic mutation to a set of state mutations in terms of
	 * C-intermediate representative of the program under analysis and testing.
	 * 
	 * @param mutant		syntactic mutation
	 * @return				the set of state mutations or empty for failure
	 * @throws Exception
	 */
	public static Collection<StateMutation> parse(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			try {
				return StateMutationParsers.parse(mutant.
						get_space().get_cir_tree(), mutant.get_mutation());
			}
			catch(Exception ex) {
				// ex.printStackTrace();
				return new ArrayList<StateMutation>();
			}
		}
	}
	
}
