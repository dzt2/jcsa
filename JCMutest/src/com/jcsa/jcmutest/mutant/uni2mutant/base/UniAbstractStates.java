package com.jcsa.jcmutest.mutant.uni2mutant.base;

import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
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
	private static boolean 	is_trap_value(SymbolNode source) {
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
	public static boolean 	has_trap_value(SymbolExpression expression) {
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
	private static boolean 	is_abst_value(SymbolNode source) {
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
	public static boolean 	has_abst_value(SymbolExpression expression) {
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
	public static SymbolExpression evaluate(SymbolExpression expression, SymbolContext in_context, SymbolContext ou_context) throws Exception {
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
	
	/* CIR-based factory */
	/**
	 * @param location
	 * @return it derives CirExpression enclosed in the input for representation
	 * @throws Exception
	 */
	private static CirExpression		find_expression(CirNode location) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location as null");
		}
		else if(location instanceof CirExpression) {
			return (CirExpression) location;
		}
		else if(location instanceof CirArgumentList) {
			return find_expression(location.get_parent());
		}
		else if(location instanceof CirCallStatement) {
			CirExecution call_execution = location.execution_of();
			CirExecution wait_execution = call_execution.get_graph().
						get_execution(call_execution.get_id() + 1);
			return find_expression(wait_execution.get_statement());
		}
		else if(location instanceof CirAssignStatement) {
			return ((CirAssignStatement) location).get_lvalue();
		}
		else if(location instanceof CirIfStatement) {
			return ((CirIfStatement) location).get_condition();
		}
		else if(location instanceof CirCaseStatement) {
			return ((CirCaseStatement) location).get_condition();
		}
		else {
			throw new IllegalArgumentException(location.getClass().getSimpleName());
		}
	}
	/**
	 * @param location	the statement (location) where the coverage is required
	 * @param min_times	the minimal times for running state statement-locations
	 * @param max_times	the maximal times for running state statement-locations
	 * @return			cov_time(location.statement; min_times, max_times)
	 * @throws Exception
	 */
	public static UniCoverTimesState	cov_time(CirNode location, int min_times, int max_times) throws Exception {
		if(location == null || location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(min_times > max_times || max_times < 0) {
			throw new IllegalArgumentException(min_times + " --> " + max_times);
		}
		else {
			UniAbstractStore store = UniAbstractStore.new_node(location.execution_of());
			return new UniCoverTimesState(store, min_times, max_times);
		}
	}
	/**
	 * @param location	the statement (location) where the condition is asserted
	 * @param condition	the condition to be evaluated at a given statement-point
	 * @param must_need	true (be satisfied always); false (satisfied at only one)
	 * @return			eva_cond(location.statement; condition, must_need)
	 * @throws Exception
	 */
	public static UniConstraintState	eva_cond(CirNode location, Object condition, boolean must_need) throws Exception {
		if(location == null || location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			UniAbstractStore store = UniAbstractStore.new_node(location.execution_of());
			return new UniConstraintState(store, condition, must_need);
		}
	}
	/**
	 * @param location	the statement (location) where the mutation is injected.
	 * @param mutant	the synactic alteration being injected in that location.
	 * @return			sed_muta(location.statement; mutant_ID, clas_oprt)
	 * @throws Exception
	 */
	public static UniSeedMutantState	sed_muta(CirNode location, Mutant mutant) throws Exception {
		if(location == null || location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			UniAbstractStore store = UniAbstractStore.new_node(location.execution_of());
			return new UniSeedMutantState(store, mutant);
		}
	}
	/**
	 * @param location	the statement (location) where the path-error is arised.
	 * @param muta_exec	True (if incorrectly executed) False (or otherwise).
	 * @return			mut_stmt(location.statement; !muta_exec, muta_exec)
	 * @throws Exception
	 */
	public static UniBlockErrorState	mut_stmt(CirNode location, boolean muta_exec) throws Exception {
		if(location == null || location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {
			UniAbstractStore store = UniAbstractStore.new_node(location.execution_of());
			return new UniBlockErrorState(store, muta_exec);
		}
	}
	/**
	 * @param location		the statement (location) where the path-error is arised.
	 * @param original_next	the next statement being executed in the original version
	 * @param mutation_next	the next statement being executed in the mutated version
	 * @return				mut_flow(location.statement; orig_next, muta_next)
	 * @throws Exception
	 */
	public static UniFlowsErrorState	mut_flow(CirNode location, 
			CirExecution original_next, CirExecution mutation_next) throws Exception {
		if(location == null || location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(original_next == null) {
			throw new IllegalArgumentException("Invalid original_next");
		}
		else if(mutation_next == null) {
			throw new IllegalArgumentException("Invalid mutation_next");
		}
		else {
			UniAbstractStore store = UniAbstractStore.new_node(location.execution_of());
			return new UniFlowsErrorState(store, original_next, mutation_next);
		}
	}
	/**
	 * @param location	the statement (location) where the trapping should arise
	 * @return			trp_stmt(location.statement; exception, exception)
	 * @throws Exception
	 */
	public static UniTrapsErrorState	trp_stmt(CirNode location) throws Exception {
		if(location == null || location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {
			UniAbstractStore store = UniAbstractStore.new_node(location.execution_of());
			return new UniTrapsErrorState(store);
		}
	}
	/**
	 * @param location	the expression (location) where the data error is arised
	 * @param value		the incorrect value to replace the original expression
	 * @return			set_expr(expression; orig_value, muta_value)
	 * @throws Exception
	 */
	public static UniValueErrorState	set_expr(CirNode location, Object value) throws Exception {
		if(location == null || location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as: null");
		}
		else {
			/* 1. it localizes the expression location for seeding errors */
			CirExpression expression = find_expression(location);
			UniAbstractStore store = UniAbstractStore.new_node(expression);
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
	 * @param location		the expression (location) where the data error is arised
	 * @param difference	the difference to be incremented to the given expression
	 * @return				inc_expr(expression; orig_value, difference)
	 * @throws Exception
	 */
	public static UniIncreErrorState	inc_expr(CirNode location, Object difference) throws Exception {
		if(location == null || location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			/* 1. it localizes the expression location for seeding errors */
			CirExpression expression = find_expression(location);
			UniAbstractStore store = UniAbstractStore.new_node(expression);
			SymbolExpression orig_value, muta_value;
			
			/* 2. it generates the base_value and difference values */
			switch(store.get_store_class()) {
			case cdef_expr:
			{
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				orig_value = SymbolFactory.sym_expression(statement.get_rvalue());
				muta_value = SymbolFactory.sym_expression(difference);
			}
			case argv_expr:
			case used_expr:
			{
				orig_value = SymbolFactory.sym_expression(expression);
				muta_value = SymbolFactory.sym_expression(difference);
				break;
			}
			default:	throw new IllegalArgumentException("Invalid: " + store);
			}
			return new UniIncreErrorState(store, orig_value, muta_value);
		}
	}
	/**
	 * @param location		the expression (location) where the data error is arised
	 * @param difference	the difference to be incremented to the given expression
	 * @return				xor_expr(expression; orig_value, difference)
	 * @throws Exception
	 */
	public static UniBixorErrorState	xor_expr(CirNode location, Object difference) throws Exception {
		if(location == null || location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			/* 1. it localizes the expression location for seeding errors */
			CirExpression expression = find_expression(location);
			UniAbstractStore store = UniAbstractStore.new_node(expression);
			SymbolExpression orig_value, muta_value;
			
			/* 2. it generates the base_value and difference values */
			switch(store.get_store_class()) {
			case cdef_expr:
			{
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				orig_value = SymbolFactory.sym_expression(statement.get_rvalue());
				muta_value = SymbolFactory.sym_expression(difference);
			}
			case argv_expr:
			case used_expr:
			{
				orig_value = SymbolFactory.sym_expression(expression);
				muta_value = SymbolFactory.sym_expression(difference);
				break;
			}
			default:	throw new IllegalArgumentException("Invalid: " + store);
			}
			return new UniBixorErrorState(store, orig_value, muta_value);
		}
	}
	/**
	 * @param ast_location	the syntactic location to represent the expression being assigned
	 * @param cir_location	the C-intermediate location to represent expression being defined
	 * @param value
	 * @return				set_expr(vdef_expression; orig_expr, muta_expr)
	 * @throws Exception
	 */
	public static UniValueErrorState	set_vdef(AstNode ast_location, CirNode cir_location, Object value) throws Exception {
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(cir_location == null || cir_location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}
		else {
			/* 1. it localizes the expression location for seeding errors */
			CirExpression expression = find_expression(cir_location);
			UniAbstractStore store = UniAbstractStore.new_vdef(ast_location, expression);
			SymbolExpression orig_value, muta_value;
			
			switch(store.get_store_class()) {
			case cdef_expr:
			{
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				orig_value = SymbolFactory.sym_expression(statement.get_rvalue());
				muta_value = SymbolFactory.sym_expression(value);
				break;
			}
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
	 * @param ast_location
	 * @param cir_location
	 * @param difference
	 * @return
	 * @throws Exception
	 */
	public static UniIncreErrorState	inc_vdef(AstNode ast_location, CirNode cir_location, Object difference) throws Exception {
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(cir_location == null || cir_location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			/* 1. it localizes the expression location for seeding errors */
			CirExpression expression = find_expression(cir_location);
			UniAbstractStore store = UniAbstractStore.new_vdef(ast_location, expression);
			SymbolExpression orig_value, muta_value;
			
			switch(store.get_store_class()) {
			case cdef_expr:
			{
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				orig_value = SymbolFactory.sym_expression(statement.get_rvalue());
				muta_value = SymbolFactory.sym_expression(difference);
				break;
			}
			case vdef_expr:
			{
				orig_value = SymbolFactory.sym_expression(expression);
				muta_value = SymbolFactory.sym_expression(difference);
				break;
			}
			default:	throw new IllegalArgumentException(store.toString());
			}
			return new UniIncreErrorState(store, orig_value, muta_value);
		}
	}
	/**
	 * @param ast_location
	 * @param cir_location
	 * @param difference
	 * @return
	 * @throws Exception
	 */
	public static UniBixorErrorState	xor_vdef(AstNode ast_location, CirNode cir_location, Object difference) throws Exception {
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(cir_location == null || cir_location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else {
			/* 1. it localizes the expression location for seeding errors */
			CirExpression expression = find_expression(cir_location);
			UniAbstractStore store = UniAbstractStore.new_vdef(ast_location, expression);
			SymbolExpression orig_value, muta_value;
			
			switch(store.get_store_class()) {
			case cdef_expr:
			{
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				orig_value = SymbolFactory.sym_expression(statement.get_rvalue());
				muta_value = SymbolFactory.sym_expression(difference);
				break;
			}
			case vdef_expr:
			{
				orig_value = SymbolFactory.sym_expression(expression);
				muta_value = SymbolFactory.sym_expression(difference);
				break;
			}
			default:	throw new IllegalArgumentException(store.toString());
			}
			return new UniBixorErrorState(store, orig_value, muta_value);
		}
	}
	
}
