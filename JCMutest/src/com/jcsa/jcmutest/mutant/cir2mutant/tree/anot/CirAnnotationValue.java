package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It defines the abstract value used to describe value annotated on store unit.
 * 
 * @author yukimula
 *
 */
public final class CirAnnotationValue {
	
	/* constant abstract value domain */
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
	
	/* safety symbolic evaluation */
	/** the abstract value to denote the case in which an arithmetic exception is thrown during symbolic computation **/
	public static final SymbolExpression expt_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@Exception");
	/**
	 * @param expression
	 * @param context
	 * @return 	It evaluates the expression under the context and return its results (if arithmetic exception is thrown
	 * 			during analysis, the method simply returns CirAnnotationValue.expt_value to denote an exception occurs).
	 * @throws Exception
	 */
	public static SymbolExpression safe_evaluate(SymbolExpression expression, SymbolProcess context) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			try {
				return expression.evaluate(context);
			}
			catch(ArithmeticException ex) {
				return expt_value;
			}
		}
	}
	
	/* symbolic difference create */
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return muta_value - orig_value
	 * @throws Exception
	 */
	protected static SymbolExpression sub_difference(CirExpression expression, Object orig_value, Object muta_value) throws Exception {
		return SymbolFactory.arith_sub(expression.get_data_type(), muta_value, orig_value);
	}
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return abs(muta_value) - abs(orig_value)
	 * @throws Exception
	 */
	protected static SymbolExpression ext_difference(CirExpression expression, Object orig_value, Object muta_value) throws Exception {
		CType type = (new CTypeFactory()).get_variable_function_type(CBasicTypeImpl.double_type);
		SymbolExpression function = SymbolFactory.identifier(type, "fabs");
		List<Object> arguments = new ArrayList<Object>();
		
		arguments.clear(); 
		arguments.add(muta_value);
		muta_value = SymbolFactory.call_expression(function, arguments);
		
		arguments.clear();
		arguments.add(orig_value);
		orig_value = SymbolFactory.call_expression(function, arguments);
		
		return SymbolFactory.arith_sub(CBasicTypeImpl.double_type, muta_value, orig_value);
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return muta_value ^ orig_value
	 * @throws Exception
	 */
	protected static SymbolExpression xor_difference(CirExpression expression, Object orig_value, Object muta_value) throws Exception {
		return SymbolFactory.bitws_xor(expression.get_data_type(), muta_value, orig_value);
	}
	
	/* value coverage analysis */
	/**
	 * It determines the coverage domains by the concrete values as input
	 * @param values
	 * @param domains
	 * @throws Exception
	 */
	private static void get_coverage_in_boolean(Iterable<SymbolExpression> values, Collection<SymbolExpression> domains) throws Exception {
		for(SymbolExpression value : values) {
			if(value instanceof SymbolConstant) {
				if(((SymbolConstant) value).get_bool()) {
					domains.add(true_value);
				}
				else {
					domains.add(fals_value);
				}
			}
		}
	}
	/**
	 * It determines the coverage domains by the concrete values as input 
	 * @param values
	 * @param domains
	 * @throws Exception
	 */
	private static void get_coverage_in_usigned(Iterable<SymbolExpression> values, Collection<SymbolExpression> domains) throws Exception {
		for(SymbolExpression value : values) {
			if(value instanceof SymbolConstant) {
				if(((SymbolConstant) value).get_long() == 0) {
					domains.add(zero_value);
				}
				else {
					domains.add(post_value);
				}
			}
		}
	}
	/**
	 * @param values
	 * @param domains
	 * @throws Exception
	 */
	private static void get_coverage_in_integer(Iterable<SymbolExpression> values, Collection<SymbolExpression> domains) throws Exception {
		for(SymbolExpression value : values) {
			if(value instanceof SymbolConstant) {
				if(((SymbolConstant) value).get_long() > 0L) {
					domains.add(post_value);
				}
				else if(((SymbolConstant) value).get_long() < 0L) {
					domains.add(negt_value);
				}
				else {
					domains.add(zero_value);
				}
			}
		}
	}
	/**
	 * @param values
	 * @param domains
	 * @throws Exception
	 */
	private static void get_coverage_in_doubles(Iterable<SymbolExpression> values, Collection<SymbolExpression> domains) throws Exception {
		for(SymbolExpression value : values) {
			if(value instanceof SymbolConstant) {
				if(((SymbolConstant) value).get_double() > 0) {
					domains.add(post_value);
				}
				else if(((SymbolConstant) value).get_double() < 0) {
					domains.add(negt_value);
				}
				else {
					domains.add(zero_value);
				}
			}
		}
	}
	/**
	 * @param values
	 * @param domains
	 * @throws Exception
	 */
	private static void get_coverage_in_address(Iterable<SymbolExpression> values, Collection<SymbolExpression> domains) throws Exception {
		for(SymbolExpression value : values) {
			if(value instanceof SymbolConstant) {
				if(((SymbolConstant) value).get_long() == 0) {
					domains.add(null_value);
				}
				else {
					domains.add(nnul_value);
				}
			}
		}
	}
	/**
	 * @param expression
	 * @param values
	 * @return it collects the domains covered by the values in the given expression
	 * @throws Exception
	 */
	private static void get_coverage_in(CirExpression expression, Iterable<SymbolExpression> values, Collection<SymbolExpression> domains) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(values == null) {
			throw new IllegalArgumentException("Invalid values as: null");
		}
		else {
			domains.clear();
			if(CirMutations.is_boolean(expression)) {
				CirAnnotationValue.get_coverage_in_boolean(values, domains);
			}
			else if(CirMutations.is_usigned(expression)) {
				CirAnnotationValue.get_coverage_in_usigned(values, domains);
			}
			else if(CirMutations.is_integer(expression)) {
				CirAnnotationValue.get_coverage_in_integer(values, domains);
			}
			else if(CirMutations.is_doubles(expression)) {
				CirAnnotationValue.get_coverage_in_doubles(values, domains);
			}
			else if(CirMutations.is_address(expression)) {
				CirAnnotationValue.get_coverage_in_address(values, domains);
			}
			else { /* no of coverage domain is generated in other data type */ }
		}
	}
	
	/* coverage summarization */
	private static void sum_coverage_in_boolean(Collection<SymbolExpression> domains, Collection<SymbolExpression> scopes) throws Exception {
		if(domains.contains(true_value)) {
			if(domains.contains(fals_value)) {
				scopes.add(bool_value);
			}
			else {
				scopes.add(true_value);
				scopes.add(bool_value);
			}
		}
		else {
			if(domains.contains(fals_value)) {
				scopes.add(fals_value);
				scopes.add(bool_value);
			}
			else { /* none domain is found*/ }
		}
	}
	private static void sum_coverage_in_usigned(Collection<SymbolExpression> domains, Collection<SymbolExpression> scopes) throws Exception {
		if(domains.contains(post_value)) {
			if(domains.contains(zero_value)) {
				scopes.add(npos_value);
			}
			else {
				scopes.add(post_value);
				scopes.add(npos_value);
			}
		}
		else {
			if(domains.contains(zero_value)) {
				scopes.add(npos_value);
				scopes.add(zero_value);
			}
			else { /* none domains is found */ }
		}
	}
	private static void sum_coverage_in_numeric(Collection<SymbolExpression> domains, Collection<SymbolExpression> scopes) throws Exception {
		if(domains.contains(post_value)) {
			if(domains.contains(negt_value)) {
				if(domains.contains(zero_value)) {		/* +, -, 0 */
					scopes.add(numb_value);
				}
				else {									/* +, - */
					scopes.add(nzro_value);
					scopes.add(numb_value);
				}
			}
			else {
				if(domains.contains(zero_value)) {		/* +, 0 */
					scopes.add(nneg_value);
					scopes.add(numb_value);
				}
				else {									/* + */
					scopes.add(post_value);
					scopes.add(nneg_value);
					scopes.add(nzro_value);
					scopes.add(numb_value);
				}
			}
		}
		else {
			if(domains.contains(negt_value)) {
				if(domains.contains(zero_value)) {		/* -, 0 */
					scopes.add(npos_value);
					scopes.add(numb_value);
				}
				else {									/* - */
					scopes.add(negt_value);
					scopes.add(npos_value);
					scopes.add(nzro_value);
					scopes.add(numb_value);
				}
			}
			else {
				if(domains.contains(zero_value)) {		/* 0 */
					scopes.add(zero_value);
					scopes.add(npos_value);
					scopes.add(nneg_value);
					scopes.add(numb_value);
				}
				else { /* no domain is covered anyway */ }
			}
		}
	}
	private static void sum_coverage_in_address(Collection<SymbolExpression> domains, Collection<SymbolExpression> scopes) throws Exception {
		if(domains.contains(null_value)) {
			if(domains.contains(nnul_value)) {
				scopes.add(addr_value);
			}
			else {
				scopes.add(null_value);
				scopes.add(addr_value);
			}
		}
		else {
			if(domains.contains(nnul_value)) {
				scopes.add(nnul_value);
				scopes.add(addr_value);
			}
			else { /* none of domain is found */ }
		}
	}
	private static void sum_coverage_in(CirExpression expression, 
			Collection<SymbolExpression> domains, Collection<SymbolExpression> scopes) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(domains == null) {
			throw new IllegalArgumentException("Invalid domains as: null");
		}
		else if(scopes == null) {
			throw new IllegalArgumentException("Invalid scopes as: null");
		}
		else {
			scopes.clear();
			if(CirMutations.is_boolean(expression)) {
				CirAnnotationValue.sum_coverage_in_boolean(domains, scopes);
			}
			else if(CirMutations.is_usigned(expression)) {
				CirAnnotationValue.sum_coverage_in_usigned(domains, scopes);
			}
			else if(CirMutations.is_integer(expression)) {
				CirAnnotationValue.sum_coverage_in_numeric(domains, scopes);
			}
			else if(CirMutations.is_doubles(expression)) {
				CirAnnotationValue.sum_coverage_in_numeric(domains, scopes);
			}
			else if(CirMutations.is_address(expression)) {
				CirAnnotationValue.sum_coverage_in_address(domains, scopes);
			}
			else { /* no of coverage domain is generated in other data type */ }
		}
	}
	
	/* coverage domain abstraction */
	/**
	 * It abstracts the value domains from the concrete values of the given expression
	 * @param expression
	 * @param values
	 * @return
	 * @throws Exception
	 */
	protected static Collection<SymbolExpression> find_scopes(CirExpression expression, Iterable<SymbolExpression> values) throws Exception {
		Set<SymbolExpression> domains = new HashSet<SymbolExpression>();
		Set<SymbolExpression> scopes = new HashSet<SymbolExpression>();
		CirAnnotationValue.get_coverage_in(expression, values, domains);
		CirAnnotationValue.sum_coverage_in(expression, domains, scopes);
		return scopes;
	}
	
}
