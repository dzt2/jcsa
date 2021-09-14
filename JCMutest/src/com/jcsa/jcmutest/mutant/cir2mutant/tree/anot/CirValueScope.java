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
 * It defines the abstract values used in defining annotated value in feature.
 * 
 * @author yukimula
 *
 */
public final class CirValueScope {
	
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
	
	/* difference constructions */
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return muta_value - orig_value
	 * @throws Exception
	 */
	protected static SymbolExpression sub_difference(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		return SymbolFactory.arith_sub(orig_value.get_data_type(), muta_value, orig_value);
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return muta_value ^ orig_value
	 * @throws Exception
	 */
	protected static SymbolExpression xor_difference(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		return SymbolFactory.bitws_xor(orig_value.get_data_type(), muta_value, orig_value);
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return abs(muta_value) - abs(orig_value)
	 * @throws Exception
	 */
	protected static SymbolExpression ext_difference(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		CType type = (new CTypeFactory()).get_variable_function_type(CBasicTypeImpl.double_type);
		SymbolExpression function = SymbolFactory.identifier(type, "fabs");
		List<Object> arguments = new ArrayList<Object>();
		
		arguments.clear(); 
		arguments.add(muta_value);
		muta_value = SymbolFactory.call_expression(function, arguments);
		
		arguments.clear();
		arguments.add(orig_value);
		orig_value = SymbolFactory.call_expression(function, arguments);
		return SymbolFactory.call_expression(function, arguments);
	}
	
	/* abstract value analysis */
	/**
	 * It collects the abstract values defining the boolean scope covered by input values and update the scopes as output
	 * @param values
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_scopes_in_boolean(Iterable<SymbolExpression> values, Collection<SymbolExpression> scopes) throws Exception {
		for(SymbolExpression value : values) {
			if(value instanceof SymbolConstant) {
				if(((SymbolConstant) value).get_bool()) {
					scopes.add(true_value);
				}
				else {
					scopes.add(fals_value);
				}
			}
			else {
				scopes.add(bool_value);
			}
		}
	}
	/**
	 * It collects the abstract values defining the unsigned scope covered by input values and update the scopes as output
	 * @param values
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_scopes_in_usigned(Iterable<SymbolExpression> values, Collection<SymbolExpression> scopes) throws Exception {
		for(SymbolExpression value : values) {
			if(value instanceof SymbolConstant) {
				if(((SymbolConstant) value).get_long() == 0L) {
					scopes.add(zero_value);
				}
				else {
					scopes.add(post_value);
				}
			}
			else {
				scopes.add(npos_value);
			}
		}
	}
	/**
	 * It collects the abstract values defining the integer scope covered by input values and update the scopes as output
	 * @param values
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_scopes_in_integer(Iterable<SymbolExpression> values, Collection<SymbolExpression> scopes) throws Exception {
		for(SymbolExpression value : values) {
			if(value instanceof SymbolConstant) {
				if(((SymbolConstant) value).get_long() > 0) {
					scopes.add(post_value);
				}
				else if(((SymbolConstant) value).get_long() < 0) {
					scopes.add(negt_value);
				}
				else {
					scopes.add(zero_value);
				}
			}
			else {
				scopes.add(numb_value);
			}
		}
	}
	/**
	 * It collects the abstract values defining the real scope covered by input values and update the scopes as output
	 * @param values
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_scopes_in_doubles(Iterable<SymbolExpression> values, Collection<SymbolExpression> scopes) throws Exception {
		for(SymbolExpression value : values) {
			if(value instanceof SymbolConstant) {
				if(((SymbolConstant) value).get_double() > 0) {
					scopes.add(post_value);
				}
				else if(((SymbolConstant) value).get_double() < 0) {
					scopes.add(negt_value);
				}
				else {
					scopes.add(zero_value);
				}
			}
			else {
				scopes.add(numb_value);
			}
		}
	}
	/**
	 * It collects the abstract values defining the address scope covered by input values and update the scopes as output
	 * @param values
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_scopes_in_address(Iterable<SymbolExpression> values, Collection<SymbolExpression> scopes) throws Exception {
		for(SymbolExpression value : values) {
			if(value instanceof SymbolConstant) {
				if(((SymbolConstant) value).get_long() == 0L) {
					scopes.add(null_value);
				}
				else {
					scopes.add(nnul_value);
				}
			}
			else {
				scopes.add(addr_value);
			}
		}
	}
	/**
	 * It collects the abstract values defining the scope of specified expression value 
	 * @param values
	 * @param scopes
	 * @throws Exception
	 */
	protected static void get_scopes_in(CirExpression expression, 
			Collection<SymbolExpression> values, 
			Collection<SymbolExpression> scopes) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(values == null || values.isEmpty()) { 
			return; 			/* no input value is provided */
		}						
		else if(scopes == null) {
			throw new IllegalArgumentException("No output is established");
		}
		else if(values.contains(expt_value)) { 			
			scopes.add(expt_value); 
		}			
		else if(CirMutations.is_boolean(expression)) {
			CirValueScope.get_scopes_in_boolean(values, scopes);
		}
		else if(CirMutations.is_usigned(expression)) {
			CirValueScope.get_scopes_in_usigned(values, scopes);
		}
		else if(CirMutations.is_integer(expression)) {
			CirValueScope.get_scopes_in_integer(values, scopes);
		}
		else if(CirMutations.is_doubles(expression)) {
			CirValueScope.get_scopes_in_doubles(values, scopes);
		}
		else if(CirMutations.is_address(expression)) {
			CirValueScope.get_scopes_in_address(values, scopes);
		}
		else { 
			/* none of value abstract values are found to define the scope */ 
		}
	}
	/**
	 * It generates the subsumed scopes from the input scopes using static logical inference in boolean case.
	 * @param scopes
	 * @param domains
	 * @throws Exception
	 */
	private static void sum_scopes_in_boolean(Collection<SymbolExpression> scopes, Collection<SymbolExpression> domains) throws Exception {
		if(scopes.contains(true_value)) {
			if(scopes.contains(fals_value)) {
				domains.add(bool_value);
			}
			else {
				domains.add(true_value);
			}
		}
		else {
			if(scopes.contains(fals_value)) {
				domains.add(fals_value);
			}
			else { /* none of concrete values */ }
		}
		
		if(!scopes.isEmpty()) { domains.add(bool_value); }
	}
	/**
	 * It generates the subsumed scopes from the input scopes using static logical inference in unsigned case. 
	 * @param scopes
	 * @param domains
	 * @throws Exception
	 */
	private static void sum_scopes_in_usigned(Collection<SymbolExpression> scopes, Collection<SymbolExpression> domains) throws Exception {
		if(scopes.contains(post_value)) {
			if(scopes.contains(zero_value)) {
				domains.add(npos_value);
			}
			else {
				domains.add(post_value);
			}
		}
		else {
			if(scopes.contains(zero_value)) {
				domains.add(zero_value);
			}
			else { /* none of concrete values */ }
		}
		
		if(!scopes.isEmpty()) { domains.add(npos_value); }
	}
	/**
	 * It generates the subsumed scopes from the input scopes using static logical inference in integer or real case. 
	 * @param scopes
	 * @param domains
	 * @throws Exception
	 */
	private static void sum_scopes_in_numeric(Collection<SymbolExpression> scopes, Collection<SymbolExpression> domains) throws Exception {
		if(scopes.contains(post_value)) {
			if(scopes.contains(negt_value)) {
				if(scopes.contains(zero_value)) {			/* {+, -, 0} */
					domains.add(numb_value);
				}
				else {										/* {+, -} */
					domains.add(nzro_value);
				}
			}
			else {
				if(scopes.contains(zero_value)) {			/* {+, 0} */
					domains.add(nneg_value);
				}
				else {										/* {+} */
					domains.add(post_value);
					domains.add(nneg_value);
					domains.add(nzro_value);
				}
			}
		}
		else {
			if(scopes.contains(negt_value)) {
				if(scopes.contains(zero_value)) {			/* {-, 0} */
					domains.add(npos_value);
				}
				else {										/* {-} */
					domains.add(negt_value);
					domains.add(npos_value);
					domains.add(nzro_value);
				}
			}
			else {
				if(scopes.contains(zero_value)) {			/* {0} */
					domains.add(zero_value);
					domains.add(npos_value);
					domains.add(nneg_value);
				}
				else { /* none of concrete values */ }
			}
		}
		
		if(!scopes.isEmpty()) { domains.add(numb_value); }
	}
	/**
	 * It generates the subsumed scopes from the input scopes using static logical inference in address case. 
	 * @param scopes
	 * @param domains
	 * @throws Exception
	 */
	private static void sum_scopes_in_address(Collection<SymbolExpression> scopes, Collection<SymbolExpression> domains) throws Exception {
		if(scopes.contains(null_value)) {
			if(scopes.contains(nnul_value)) {
				domains.add(addr_value);
			}
			else {
				domains.add(null_value);
			}
		}
		else {
			if(scopes.contains(nnul_value)) {
				domains.add(nnul_value);
			}
			else { /* none of concrete values */ }
		}
		
		if(!scopes.isEmpty()) { domains.add(addr_value); }
	}
	/**
	 * It generates the subsumed domains from the input scopes using static logical inference in specified type of expression
	 * @param expression
	 * @param scopes
	 * @param domains
	 * @throws Exception
	 */
	private static void sum_scopes_in(CirExpression expression, 
			Collection<SymbolExpression> scopes, Collection<SymbolExpression> domains) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(scopes == null || scopes.isEmpty()) {
			return;	/* none of input scopes are provided to summarize from */
		}
		else if(domains == null) {
			throw new IllegalArgumentException("No output is established");
		}
		else if(scopes.contains(expt_value)) {
			domains.add(expt_value);	/* throw exception is all you need */
		}
		else if(CirMutations.is_boolean(expression)) {
			CirValueScope.sum_scopes_in_boolean(scopes, domains);
		}
		else if(CirMutations.is_usigned(expression)) {
			CirValueScope.sum_scopes_in_usigned(scopes, domains);
		}
		else if(CirMutations.is_numeric(expression)) {
			CirValueScope.sum_scopes_in_numeric(scopes, domains);
		}
		else if(CirMutations.is_address(expression)) {
			CirValueScope.sum_scopes_in_address(scopes, domains);
		}
		else { 
			/* none of value abstract values are found to define the scope */  
		}
	}
	/**
	 * It generates the domains in form of abstract values that are used to define the scope covered by input values at specified location
	 * @param expression
	 * @param values
	 * @return
	 * @throws Exception
	 */
	protected static Collection<SymbolExpression> get_abs_values_from(
			CirExpression expression, Collection<SymbolExpression> values) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(values == null) {
			throw new IllegalArgumentException("No inputs are established");
		}
		else {
			Set<SymbolExpression> scopes = new HashSet<SymbolExpression>();
			Set<SymbolExpression> domains = new HashSet<SymbolExpression>();
			CirValueScope.get_scopes_in(expression, values, scopes);
			CirValueScope.sum_scopes_in(expression, scopes, domains);
			return domains;
		}
	}
	
}
