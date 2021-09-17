package com.jcsa.jcmutest.mutant.cir2mutant.stat.anot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It specifies the abstract symbolic value representing the domain of values
 * that are annotated with store units in the program.
 * 
 * @author yukimula
 *
 */
final class CirValueScope {
	
	/* definitions */
	/** {true, false} **/
	protected static final SymbolExpression bool_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@BoolValue");
	/** true **/
	protected static final SymbolExpression true_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@TrueValue");
	/** false **/
	protected static final SymbolExpression fals_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@FalsValue");
	/** integer or double **/
	protected static final SymbolExpression numb_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NumbValue");
	/** { x | x > 0 } **/
	protected static final SymbolExpression post_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@PostValue");
	/** { x | x < 0 } **/
	protected static final SymbolExpression negt_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NegtValue");
	/** 0 **/
	protected static final SymbolExpression zero_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@ZeroValue");
	/** { x | x <= 0 } **/
	protected static final SymbolExpression npos_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NposValue");
	/** { x | x >= 0 } **/
	protected static final SymbolExpression nneg_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnegValue");
	/** { x | x != 0 } **/
	protected static final SymbolExpression nzro_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NzroValue");
	/** address value in pointer **/
	protected static final SymbolExpression addr_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@AddrValue");
	/** null **/
	protected static final SymbolExpression null_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NullValue");
	/** {p | p != null} **/
	protected static final SymbolExpression nnul_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnulValue");
	
	/* safety symbolic evaluation */
	/** the abstract value to denote the case in which an arithmetic exception is thrown during symbolic computation **/
	protected static final SymbolExpression expt_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@Exception");
	/**
	 * @param expression
	 * @param context
	 * @return 	It evaluates the expression under the context and return its results (if arithmetic exception is thrown
	 * 			during analysis, the method simply returns CirAnnotationValue.expt_value to denote an exception occurs).
	 * @throws Exception
	 */
	protected static SymbolExpression safe_evaluate(SymbolExpression expression, SymbolProcess context) throws Exception {
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
	
	/* differential generations */
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return abs(muta_value) - abs(orig_value)
	 * @throws Exception
	 */
	protected static SymbolExpression ext_differentiate(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value as null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value as null");
		}
		else if(CirMutations.is_numeric(orig_value.get_data_type())) {
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
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_value);
		}
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return muta_value - orig_value
	 * @throws Exception
	 */
	protected static SymbolExpression sub_differentiate(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value as null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value as null");
		}
		else if(CirMutations.is_numeric(orig_value.get_data_type())) {
			return SymbolFactory.arith_sub(orig_value.get_data_type(), muta_value, orig_value);
		}
		else if(CirMutations.is_address(orig_value.get_data_type())) {
			return SymbolFactory.arith_sub(orig_value.get_data_type(), muta_value, orig_value);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_value);
		}
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return muta_value ^ orig_value
	 * @throws Exception
	 */
	protected static SymbolExpression xor_differentiate(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value as null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value as null");
		}
		else if(CirMutations.is_integer(orig_value.get_data_type())) {
			return SymbolFactory.bitws_xor(orig_value.get_data_type(), orig_value, muta_value);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_value);
		}
	}
	
	/* collect the abstract scopes */
	/**
	 * {true --> true_value; false --> fals_value} | --> bool_value
	 * @param values
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_value_scopes_in_bool(Collection<SymbolExpression> values, Collection<SymbolExpression> scopes) {
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
	 * value --> {post_value, zero_value} --> nneg_value
	 * @param values
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_value_scopes_in_usig(Collection<SymbolExpression> values, Collection<SymbolExpression> scopes) {
		for(SymbolExpression value : values) {
			if(value instanceof SymbolConstant) {
				if(((SymbolConstant) value).get_long() != 0L) {
					scopes.add(post_value);
				}
				else {
					scopes.add(zero_value);
				}
			}
			else {
				scopes.add(nneg_value);
			}
		}
	}
	/**
	 * value --> {post_value, zero_value, negt_value} --> numb_value
	 * @param values
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_value_scopes_in_sign(Collection<SymbolExpression> values, Collection<SymbolExpression> scopes) {
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
	 * value --> {post_value, zero_value, negt_value} --> numb_value
	 * @param values
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_value_scopes_in_real(Collection<SymbolExpression> values, Collection<SymbolExpression> scopes) {
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
	 * value --> {null_value, nnul_value} --> addr_value
	 * @param values
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_value_scopes_in_addr(Collection<SymbolExpression> values, Collection<SymbolExpression> scopes) {
		for(SymbolExpression value : values) {
			if(value instanceof SymbolConstant) {
				if(((SymbolConstant) value).get_long() != 0L) {
					scopes.add(nnul_value);
				}
				else {
					scopes.add(null_value);
				}
			}
			else {
				scopes.add(addr_value);
			}
		}
	}
	/**
	 * none
	 * @param values
	 * @param scopes
	 */
	private static void get_value_scopes_in_auto(Collection<SymbolExpression> values, Collection<SymbolExpression> scopes) { }
	/**
	 * the set of abstract values referring to any value in the input collection
	 * @param values
	 * @return
	 */
	protected static Collection<SymbolExpression> get_value_scopes_in(
			CirValueClass value_type, Collection<SymbolExpression> values) {
		Collection<SymbolExpression> scopes = new HashSet<SymbolExpression>();
		if(values == null || values.isEmpty() || value_type == null) { 
			/* none is collected from input in case of invalid inputs*/ 
		}
		else if(values.contains(expt_value)) { scopes.add(expt_value); }
		else {
			switch(value_type) {
			case bool:	get_value_scopes_in_bool(values, scopes); break;
			case usig:	get_value_scopes_in_usig(values, scopes); break;
			case sign:	get_value_scopes_in_sign(values, scopes); break;
			case real:	get_value_scopes_in_real(values, scopes); break;
			case addr:	get_value_scopes_in_addr(values, scopes); break;
			default:	get_value_scopes_in_auto(values, scopes); break;
			}
		}
		return scopes;
	}
	
	/* unifies the abstract scopes */
	/**
	 * @param scopes
	 * @param sub_scopes
	 */
	private static void sum_value_scopes_in_bool(Collection<SymbolExpression> scopes, Collection<SymbolExpression> sum_scopes) {
		if(scopes.contains(true_value)) {
			if(scopes.contains(fals_value)) {
				sum_scopes.add(bool_value);
			}
			else {
				sum_scopes.add(true_value);
			}
		}
		else {
			if(scopes.contains(fals_value)) {
				sum_scopes.add(fals_value);
			}
			else {
				sum_scopes.add(bool_value);
			}
		}
	}
	/**
	 * @param scopes
	 * @param sum_scopes
	 */
	private static void sum_value_scopes_in_usig(Collection<SymbolExpression> scopes, Collection<SymbolExpression> sum_scopes) {
		if(scopes.contains(post_value)) {
			if(scopes.contains(zero_value)) {
				sum_scopes.add(nneg_value);
			}
			else {
				sum_scopes.add(post_value);
			}
		}
		else {
			if(scopes.contains(zero_value)) {
				sum_scopes.add(zero_value);
			}
			else {
				sum_scopes.add(nneg_value);
			}
		}
	}
	/**
	 * @param scopes
	 * @param sum_scopes
	 */
	private static void sum_value_scopes_in_numb(Collection<SymbolExpression> scopes, Collection<SymbolExpression> sum_scopes) {
		if(scopes.contains(post_value)) {
			if(scopes.contains(negt_value)) {
				if(scopes.contains(zero_value)) {			/* +, -, 0 */
					sum_scopes.add(numb_value);
				}
				else {										/* +, - */
					sum_scopes.add(nzro_value);
				}
			}
			else {
				if(scopes.contains(zero_value)) {			/* +, 0 */
					sum_scopes.add(nneg_value);
				}
				else {										/* + */
					sum_scopes.add(post_value);
				}
			}
		}
		else {
			if(scopes.contains(negt_value)) {
				if(scopes.contains(zero_value)) {			/* -, 0 */
					sum_scopes.add(npos_value);
				}
				else {										/* - */
					sum_scopes.add(negt_value);
				}
			}
			else {
				if(scopes.contains(zero_value)) {			/* 0 */
					sum_scopes.add(zero_value);
				}
				else {
					sum_scopes.add(numb_value);
				}
			}
		}
	}
	/**
	 * @param scopes
	 * @param sum_scopes
	 */
	private static void sum_value_scopes_in_addr(Collection<SymbolExpression> scopes, Collection<SymbolExpression> sum_scopes) {
		if(scopes.contains(nnul_value)) {
			if(scopes.contains(null_value)) {
				sum_scopes.add(addr_value);
			}
			else {
				sum_scopes.add(nnul_value);
			}
		}
		else {
			if(scopes.contains(null_value)) {
				sum_scopes.add(null_value);
			}
			else {
				sum_scopes.add(addr_value);
			}
		}
	}
	/**
	 * @param scopes
	 * @param sum_scopes
	 */
	private static void sum_value_scopes_in_auto(Collection<SymbolExpression> scopes, Collection<SymbolExpression> sum_scopes) { }
	/**
	 * It summarizes the best precise abstract values from the input concrete values
	 * @param value_type
	 * @param values
	 * @return
	 */
	protected static Collection<SymbolExpression> sum_value_scopes_in(
			CirValueClass value_type, Collection<SymbolExpression> values) {
		Collection<SymbolExpression> sum_scopes = new HashSet<SymbolExpression>();
		if(values == null || values.isEmpty() || value_type == null) { 
			/* none is collected from input in case of invalid inputs*/ 
		}
		else if(values.contains(expt_value)) { sum_scopes.add(expt_value); }
		else {
			Collection<SymbolExpression> scopes = get_value_scopes_in(value_type, values);
			switch(value_type) {
			case bool:	sum_value_scopes_in_bool(scopes, sum_scopes); break;
			case usig:	sum_value_scopes_in_usig(scopes, sum_scopes); break;
			case sign:	sum_value_scopes_in_numb(scopes, sum_scopes); break;
			case real:	sum_value_scopes_in_numb(scopes, sum_scopes); break;
			case addr:	sum_value_scopes_in_addr(scopes, sum_scopes); break;
			default:	sum_value_scopes_in_auto(scopes, sum_scopes); break;
			}
		}
		return sum_scopes;
	}
	
}
