package com.jcsa.jcmutest.mutant.uni2mutant.base;

import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
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
import com.jcsa.jcparse.parse.parser3.SymbolContext;


/**
 * 	It defines the basis and atoms used to define UniAbstractState.
 * 	
 * 	@author yukimula
 *
 */
public final class UniAbstractStates {
	
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
	
	/* data type classification */
	/**
	 * @param data_type
	 * @return null if the original data type is invalid
	 */ 
	private static CType get_normalized_type(CType data_type) {
		if(data_type == null) {
			return CBasicTypeImpl.void_type;	
		}
		else {
			try {
				return CTypeAnalyzer.get_value_type(data_type);
			}
			catch(Exception ex) {
				return CBasicTypeImpl.void_type;
			}
		}
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
	
	/* symbolic evaluations */
	/**
	 * @param source
	 * @return whether the expression is a trap-value
	 */
	private static boolean 			is_trap_value(SymbolNode source) {
		if(source == null) {
			return false;
		}
		else if(source instanceof SymbolIdentifier) {
			return source.equals(trap_value);
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression encloses any trap-values
	 */
	public static boolean 			has_trap_value(SymbolExpression expression) {
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
	 * @param source
	 * @return whether the source is any abstract value defined in this class
	 */
	private static boolean 			is_abst_value(SymbolNode source) {
		if(source == null) {
			return false;
		}
		else if(source instanceof SymbolIdentifier) {
			return source.equals(bool_value) || source.equals(true_value) || source.equals(fals_value) || source.equals(numb_value) ||
					source.equals(post_value) || source.equals(negt_value) || source.equals(zero_value) || source.equals(npos_value) ||
					source.equals(nneg_value) || source.equals(nzro_value) || source.equals(addr_value) || source.equals(null_value) ||
					source.equals(nnul_value);
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression ecloses any abstract values defined
	 */
	public static boolean 			has_abst_value(SymbolExpression expression) {
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
	 * @param in_context
	 * @param ou_context
	 * @return	to evaluate the expression or not if enclosing abstract value
	 * @throws Exception
	 */
	public static SymbolExpression	evaluate(SymbolExpression expression, SymbolContext in_context, SymbolContext ou_context) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(has_trap_value(expression)) { return trap_value; }
		else if(has_abst_value(expression)) { return expression; }
		else { 
			try {
				return expression.evaluate(in_context, ou_context);
			}
			catch(ArithmeticException ex) {
				return trap_value;
			}
		}
	}
	
	/* factory */
	/**
	 * @param store		the statement where the coverage is required to be met
	 * @param min_times	the minimal times for running state statement-locations
	 * @param max_times	the maximal times for running state statement-locations
	 * @return			cov_time(location.statement; min_times, max_times)
	 * @throws Exception
	 */
	public static UniCoverTimesState	cov_time(UniAbstractStore store, int min_times, int max_times) throws Exception {
		if(store == null || !store.is_statement()) {
			throw new IllegalArgumentException("Invalid store: " + store);
		}
		else if(min_times > max_times || max_times < 0) {
			throw new IllegalArgumentException(min_times + " --> " + max_times);
		}
		else {
			return new UniCoverTimesState(store, min_times, max_times);
		}
	}
	/**
	 * @param store		the statement where the condition is required to be met
	 * @param condition	the symbolic constraint being evaluated at the location
	 * @param must_need	True (always being met); False (being met at least once)
	 * @return			eva_cond(statement; condition, must_need)
	 * @throws Exception
	 */
	public static UniConstraintState	eva_cond(UniAbstractStore store, Object condition, boolean must_need) throws Exception {
		if(store == null || !store.is_statement()) {
			throw new IllegalArgumentException("Invalid store: " + store);
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new UniConstraintState(store, condition, must_need);
		}
	}
	/**
	 * @param store		the statement where the mutation is injected
	 * @param mutant	the syntactic mutation is injected in location
	 * @return			sed_muta(statement; mutant_ID, class_operator)
	 * @throws Exception	
	 */
	public static UniSeedMutantState	sed_muta(UniAbstractStore store, Mutant mutant) throws Exception {
		if(store == null || !store.is_statement()) {
			throw new IllegalArgumentException("Invalid store: " + store);
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			return new UniSeedMutantState(store, mutant);
		}
	}
	/**
	 * @param store		the statement where the path-error is arised
	 * @param muta_exec	whether the statement is executed in mutated version
	 * @return			mut_stmt(statement; !muta_exec, muta_exec)
	 * @throws Exception
	 */
	public static UniBlockErrorState	mut_stmt(UniAbstractStore store, boolean muta_exec) throws Exception {
		if(store == null || !store.is_statement()) {
			throw new IllegalArgumentException("Invalid store: " + store);
		}
		else {
			return new UniBlockErrorState(store, muta_exec);
		}
	}
	/**
	 * @param store		the statement where the path-error is arised
	 * @param orig_next	the next statement being executed in the original version
	 * @param muta_next	the next statement being executed in the mutated version
	 * @return				mut_flow(location.statement; orig_next, muta_next)
	 * @throws Exception
	 */
	public static UniFlowsErrorState	mut_flow(UniAbstractStore store, CirExecution orig_next, CirExecution muta_next) throws Exception {
		if(store == null || !store.is_statement()) {
			throw new IllegalArgumentException("Invalid store: " + store);
		}
		else if(orig_next == null) {
			throw new IllegalArgumentException("Invalid original_next");
		}
		else if(muta_next == null) {
			throw new IllegalArgumentException("Invalid mutation_next");
		}
		else {
			return new UniFlowsErrorState(store, orig_next, muta_next);
		}
	}
	/**
	 * @param store
	 * @return
	 * @throws Exception
	 */
	public static UniTrapsErrorState	trp_stmt(UniAbstractStore store) throws Exception {
		if(store == null || !store.is_statement()) {
			throw new IllegalArgumentException("Invalid store: " + store);
		}
		else {
			return new UniTrapsErrorState(store);
		}
	}
	/**
	 * @param store	the expression where the data error is injected
	 * @param value	the value to replace with the original expression
	 * @return
	 * @throws Exception
	 */
	public static UniValueErrorState	set_expr(UniAbstractStore store, Object value) throws Exception {
		if(store == null || !store.is_expression()) {
			throw new IllegalArgumentException("Invalid store: " + store);
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}
		else {
			/* 1. it localizes the expression location for seeding errors */
			CirExpression expression = (CirExpression) store.get_cir_location();
			SymbolExpression orig_value, muta_value;
			
			/* 2. it generates the values for being mutated in expression */
			switch(store.get_store_class()) {
			case bool_expr:
			{
				orig_value = SymbolFactory.sym_condition(expression, true);
				muta_value = SymbolFactory.sym_condition(value, true);
				break;
			}
			case cdef_expr:
			{
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				orig_value = SymbolFactory.sym_expression(statement.get_rvalue());
				muta_value = SymbolFactory.sym_expression(value);
				break;
			}
			case argv_expr:
			case used_expr:
			case vdef_expr:
			{
				orig_value = SymbolFactory.sym_expression(expression);
				muta_value = SymbolFactory.sym_expression(value);
				break;
			}
			default:	throw new IllegalArgumentException(store.toString());
			}
			return new UniValueErrorState(store, orig_value, muta_value);
		}
	}
	/**
	 * @param store
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static UniIncreErrorState	inc_expr(UniAbstractStore store, Object value) throws Exception {
		if(store == null || !store.is_expression()) {
			throw new IllegalArgumentException("Invalid store: " + store);
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}
		else {
			/* 1. it localizes the expression location for seeding errors */
			CirExpression expression = (CirExpression) store.get_cir_location();
			SymbolExpression orig_value, muta_value;
			
			/* 2. it generates the values for being mutated in expression */
			switch(store.get_store_class()) {
			case cdef_expr:
			{
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				orig_value = SymbolFactory.sym_expression(statement.get_rvalue());
				muta_value = SymbolFactory.sym_expression(value);
				break;
			}
			case argv_expr:
			case used_expr:
			case vdef_expr:
			{
				orig_value = SymbolFactory.sym_expression(expression);
				muta_value = SymbolFactory.sym_expression(value);
				break;
			}
			default:	throw new IllegalArgumentException(store.toString());
			}
			return new UniIncreErrorState(store, orig_value, muta_value);
		}
	}
	/**
	 * @param store
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static UniBixorErrorState	xor_expr(UniAbstractStore store, Object value) throws Exception {
		if(store == null || !store.is_expression()) {
			throw new IllegalArgumentException("Invalid store: " + store);
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}
		else {
			/* 1. it localizes the expression location for seeding errors */
			CirExpression expression = (CirExpression) store.get_cir_location();
			SymbolExpression orig_value, muta_value;
			
			/* 2. it generates the values for being mutated in expression */
			switch(store.get_store_class()) {
			case cdef_expr:
			{
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				orig_value = SymbolFactory.sym_expression(statement.get_rvalue());
				muta_value = SymbolFactory.sym_expression(value);
				break;
			}
			case argv_expr:
			case used_expr:
			case vdef_expr:
			{
				orig_value = SymbolFactory.sym_expression(expression);
				muta_value = SymbolFactory.sym_expression(value);
				break;
			}
			default:	throw new IllegalArgumentException(store.toString());
			}
			return new UniBixorErrorState(store, orig_value, muta_value);
		}
	}
	
}
