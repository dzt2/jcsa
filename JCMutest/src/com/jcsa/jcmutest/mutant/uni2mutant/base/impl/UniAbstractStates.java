package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.lang.symbol.SymbolProcess;


/**
 * 	It performs the construction and generation of UniAbstractState from various
 * 	state-location based on CirNode or AstNode.
 * 
 * 	@author yukimula
 *
 */
public final class UniAbstractStates {
	
	/* abstract-values */
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
	
	/* value-verification */
	/**
	 * @param node
	 * @return whether the node is 
	 */
	private static boolean is_trap_value(SymbolNode node) {
		if(node instanceof SymbolIdentifier)
			return node.equals(trap_value);
		else
			return false;
	}
	/**
	 * @param expression
	 * @return whether the symbolic expression contains trap_value
	 */
	public static boolean has_trap_value(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(expression);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				if(is_trap_value(parent)) { return true; }
				for(SymbolNode child : parent.get_children()) {
					queue.add(child);
				}
			}
			return false;
		}
	}
	/**
	 * @param node
	 * @return whether the node is any abstract value defined in
	 */
	private static boolean is_abst_value(SymbolNode node) {
		if(node instanceof SymbolIdentifier) {
			return node.equals(bool_value) || node.equals(true_value) || node.equals(fals_value) || 
					node.equals(numb_value) || node.equals(post_value) || node.equals(negt_value) || 
					node.equals(zero_value) || node.equals(npos_value) || node.equals(nneg_value) ||
					node.equals(nzro_value) || node.equals(addr_value) || node.equals(null_value) ||
					node.equals(nnul_value);
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the symbolic expression contains any abstract values
	 */
	public static boolean has_abst_value(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(expression);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				if(is_abst_value(parent)) { return true; }
				for(SymbolNode child : parent.get_children()) {
					queue.add(child);
				}
			}
			return false;
		}
	}
	/**
	 * @param expression
	 * @param context
	 * @return return trap_value if arithmetic-exception arises during computation
	 * @throws Exception
	 */
	public static SymbolExpression b_evaluate(SymbolExpression expression, 
			SymbolProcess in_context, SymbolProcess ou_context) throws Exception {
		if(expression == null) {												/* input-validate */
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(has_trap_value(expression)) {
			return trap_value;
		}
		else if(has_abst_value(expression)) {
			return expression;
		}
		else {
			try {
				return expression.evaluate(in_context, ou_context);
			}
			catch(ArithmeticException ex) {
				return trap_value;
			}
		}
	}
	/**
	 * @param expression
	 * @param context
	 * @return return trap_value if arithmetic-exception arises during computation
	 * @throws Exception
	 */
	public static SymbolExpression i_evaluate(SymbolExpression expression, 
			SymbolProcess i_context) throws Exception {
		return b_evaluate(expression, i_context, null);
	}
	/**
	 * @param expression
	 * @param o_context		the state to preserve outputs
	 * @return	return trap_value if arithmetic-exception arises during computation
	 * @throws Exception
	 */
	public static SymbolExpression o_evaluate(SymbolExpression expression, 
			SymbolProcess o_context) throws Exception {
		return b_evaluate(expression, null, o_context);
	}
	/**
	 * @param expression
	 * @param context
	 * @return return trap_value if arithmetic-exception arises during computation
	 * @throws Exception
	 */
	public static SymbolExpression evaluate(SymbolExpression expression) throws Exception {
		return b_evaluate(expression, null, null);
	}
	
	/* type-classification */
	/**
	 * @param data_type
	 * @return the normalized data type
	 */
	public static CType get_normalized_type(CType data_type) {
		return SymbolFactory.get_type(data_type);
	}
	/**
	 * @param data_type
	 * @return whether the data type is void
	 */
	public static boolean is_void(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_void:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is boolean
	 */
	public static boolean is_boolean(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_bool:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return true iff. {uchar|ushort|uint|ulong}
	 */
	public static boolean is_usigned(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_uchar:
			case c_ushort:
			case c_uint:
			case c_ulong:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return {char|uchar|short|ushort|int|uint|long|ulong|llong|ullong|enum}
	 */
	public static boolean is_integer(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:	return true;
			default:		return false;
			}
		}
		else if(data_type instanceof CEnumType) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return {char|uchar|short|ushort|int|uint|long|ulong|llong|ullong|enum}
	 */
	public static boolean is_doubles(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_float:
			case c_double:
			case c_ldouble: return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is integer or real
	 */
	public static boolean is_numeric(CType data_type) {
		return is_integer(data_type) || is_doubles(data_type);
	}
	/**
	 * @param data_type
	 * @return whether the data type is a address pointer
	 */
	public static boolean is_address(CType data_type) {
		data_type = get_normalized_type(data_type);
		return data_type instanceof CArrayType || 
				data_type instanceof CPointerType;
	}
	
	/* location classifier */
	/**
	 * @param expression
	 * @return
	 */
	public static boolean is_void(CirExpression expression) {
		return is_void(expression.get_data_type());
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
	
	/* factory methods (CIR-based) */
	/**
	 * @param statement	the statement-location to be executed in C-intermediate representation point
	 * @param min_times	the minimal times that the statement is required to be executed for
	 * @param max_times	the maximal times that the statement is required to be executed until
	 * @return			cov_time(statement; min_times, max_times)
	 * @throws Exception
	 */
	public static UniCoverTimesState	cov_time(CirStatement statement, int min_times, int max_times) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(min_times > max_times || max_times < 0) {
			throw new IllegalArgumentException(min_times + "::" + max_times);
		}
		else {
			UniAbstractStore store = UniAbstractStore.cir_node(statement);
			return new UniCoverTimesState(store, min_times, max_times);
		}
	}
	/**
	 * @param statement	the statement-location in which the symbolic condition is evaluated
	 * @param condition	the symbolic condition to be evaluated and needs be satisfied there
	 * @param must_need	True {always be satisfied}; False {be satisfied at least one time};
	 * @return			eva_bool(statement; condition, must_need)
	 * @throws Exception
	 */
	public static UniConstraintState	eva_bool(CirStatement statement, Object condition, boolean must_need) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			UniAbstractStore store = UniAbstractStore.cir_node(statement);
			return new UniConstraintState(store, condition, must_need);
		}
	}
	/**
	 * @param statement	the statement, in which the syntactic mutation is injected
	 * @param mutant	the syntactic variant to be injected in the statement node
	 * @return			sed_muta(statement; mutant_ID, class_operator)
	 * @throws Exception
	 */
	public static UniSeedMutantState	sed_muta(CirStatement statement, Mutant mutant) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			UniAbstractStore store = UniAbstractStore.cir_node(statement);
			return new UniSeedMutantState(store, mutant);
		}
	}
	/**
	 * @param statement	the statement of which execution will be mutated
	 * @param muta_exec	True if the statement is incorrectly executed in mutant
	 * @return			mut_stmt(statement; !muta_exec, muta_exec)
	 * @throws Exception
	 */
	public static UniBlockErrorState	mut_stmt(CirStatement statement, boolean muta_exec) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else {
			UniAbstractStore store = UniAbstractStore.cir_node(statement);
			return new UniBlockErrorState(store, muta_exec);
		}
	}
	/**
	 * @param source	the statement from which the incorrect control flow is introduced
	 * @param orig_next	the orignal statement to be executed next to the source
	 * @param muta_next	the mutated statement to be executed next to the source
	 * @return			mut_flow(source; orig_next, muta_next)
	 * @throws Exception
	 */
	public static UniFlowsErrorState	mut_flow(CirStatement source, CirStatement orig_next, CirStatement muta_next) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(orig_next == null) {
			throw new IllegalArgumentException("Invalid orig_next: null");
		}
		else if(muta_next == null) {
			throw new IllegalArgumentException("Invalid muta_next: null");
		}
		else {
			UniAbstractStore store = UniAbstractStore.cir_node(source);
			return new UniFlowsErrorState(store, orig_next.execution_of(), muta_next.execution_of());
		}
	}
	/**
	 * @param statement	the statement where the trapping arises
	 * @return			trp_stmt(statement; exception, exception)
	 * @throws Exception
	 */
	public static UniTrapsErrorState	trp_stmt(CirStatement statement) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else {
			UniAbstractStore store = UniAbstractStore.cir_node(statement);
			return new UniTrapsErrorState(store);
		}
	}
	/**
	 * @param expression	the expression of which value will be replaced by
	 * @param value			the value to replace the original expression given
	 * @return				set_expr(expression; orig_value, muta_value)
	 * @throws Exception
	 */
	public static UniValueErrorState	set_expr(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else {
			SymbolExpression orig_value, muta_value;
			UniAbstractStore store = UniAbstractStore.cir_node(expression);
			if(UniAbstractStates.is_assigned(expression)) {
				CirAssignStatement statement = (CirAssignStatement) expression.statement_of();
				orig_value = SymbolFactory.sym_expression(statement.get_rvalue());
				muta_value = SymbolFactory.sym_expression(value);
			}
			else if(UniAbstractStates.is_boolean(expression)) {
				orig_value = SymbolFactory.sym_condition(expression, true);
				muta_value = SymbolFactory.sym_condition(value, true);
			}
			else {
				orig_value = SymbolFactory.sym_expression(expression);
				muta_value = SymbolFactory.sym_expression(value);
			}
			return new UniValueErrorState(store, orig_value, muta_value);
		}
	}
	/**
	 * @param expression	the expression of which value will be incremented by
	 * @param value			the value to increment the original expression given
	 * @return				inc_expr(expression; orig_value, muta_value)
	 * @throws Exception
	 */
	public static UniIncreErrorState	inc_expr(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(UniAbstractStates.is_numeric(expression) || UniAbstractStates.is_address(expression)) {
			SymbolExpression orig_value, muta_value;
			UniAbstractStore store = UniAbstractStore.cir_node(expression);
			if(UniAbstractStates.is_assigned(expression)) {
				CirAssignStatement statement = (CirAssignStatement) expression.statement_of();
				orig_value = SymbolFactory.sym_expression(statement.get_rvalue());
				muta_value = SymbolFactory.sym_expression(value);
			}
			else {
				orig_value = SymbolFactory.sym_expression(expression);
				muta_value = SymbolFactory.sym_expression(value);
			}
			return new UniIncreErrorState(store, orig_value, muta_value);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + expression);
		}
	}
	/**
	 * @param expression	the expression of which value will be incremented by
	 * @param value			the value to increment the original expression given
	 * @return				xor_expr(expression; orig_value, muta_value)
	 * @throws Exception
	 */
	public static UniBixorErrorState	xor_expr(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(UniAbstractStates.is_integer(expression)) {
			SymbolExpression orig_value, muta_value;
			UniAbstractStore store = UniAbstractStore.cir_node(expression);
			if(UniAbstractStates.is_assigned(expression)) {
				CirAssignStatement statement = (CirAssignStatement) expression.statement_of();
				orig_value = SymbolFactory.sym_expression(statement.get_rvalue());
				muta_value = SymbolFactory.sym_expression(value);
			}
			else {
				orig_value = SymbolFactory.sym_expression(expression);
				muta_value = SymbolFactory.sym_expression(value);
			}
			return new UniBixorErrorState(store, orig_value, muta_value);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + expression);
		}
	}
	
	
	
	
}
