package com.jcsa.jcmutest.mutant.cir2mutant.tree;

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


/**
 * It specifies the abstract symbolic value for value in CirAnnotation.
 * 
 * @author yukimula
 *
 */
final class CirAnnotationValue {
	
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
	
	/* differential generations */
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return abs(muta_value) - abs(orig_value)
	 * @throws Exception
	 */
	protected static SymbolExpression ext_differ(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value as null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value as null");
		}
		else {
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
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return muta_value - orig_value
	 * @throws Exception
	 */
	protected static SymbolExpression sub_differ(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value as null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value as null");
		}
		else {
			return SymbolFactory.arith_sub(orig_value.get_data_type(), muta_value, orig_value);
		}
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return muta_value ^ orig_value
	 * @throws Exception
	 */
	protected static SymbolExpression xor_differ(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value as null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value as null");
		}
		else {
			return SymbolFactory.bitws_xor(orig_value.get_data_type(), orig_value, muta_value);
		}
	}
	
	/* domain coverage analysis */
	/**
	 * true --> TRUE_SCOPE; false --> FALS_SCOPE; otherwise --> BOOL_SCOPE;
	 * @param value
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_scopes_in_bool(SymbolExpression value, Collection<SymbolExpression> scopes) throws Exception {
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
	/**
	 * {x != 0} --> POST_SCOPE; {x == 0} --> ZERO_SCOPE; otherwise --> NNEG_SCOPE;
	 * @param value
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_scopes_in_usig(SymbolExpression value, Collection<SymbolExpression> scopes) throws Exception {
		if(value instanceof SymbolConstant) {
			if(((SymbolConstant) value).get_long() != 0) {
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
	/**
	 * {x > 0} --> POST; {x < 0} --> NEGT; {x == 0} --> ZERO; otherwise --> NUMB;
	 * @param value
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_scopes_in_sign(SymbolExpression value, Collection<SymbolExpression> scopes) throws Exception {
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
	/**
	 * {x > 0} --> POST; {x < 0} --> NEGT; {x == 0} --> ZERO; otherwise --> NUMB;
	 * @param value
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_scopes_in_real(SymbolExpression value, Collection<SymbolExpression> scopes) throws Exception {
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
	/**
	 * {x != 0} --> NNUL; {x == 0} --> NULL; otherwise --> ADDR;
	 * @param value
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_scopes_in_addr(SymbolExpression value, Collection<SymbolExpression> scopes) throws Exception {
		if(value instanceof SymbolConstant) {
			if(((SymbolConstant) value).get_long() != 0) {
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
	/**
	 * None
	 * @param value
	 * @param scopes
	 * @throws Exception
	 */
	private static void get_scopes_in_auto(SymbolExpression value, Collection<SymbolExpression> scopes) throws Exception { }
	/**
	 * @param expression
	 * @param values
	 * @return the set of abstract scope covered by the concrete values 
	 * @throws Exception
	 */
	protected static Collection<SymbolExpression> get_scopes_in(CirExpression expression, Collection<SymbolExpression> values) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			Set<SymbolExpression> scopes = new HashSet<SymbolExpression>();
			if(CirMutations.is_boolean(expression)) {
				for(SymbolExpression value : values) {
					CirAnnotationValue.get_scopes_in_bool(value, scopes);
				}
			}
			else if(CirMutations.is_usigned(expression)) {
				for(SymbolExpression value : values) {
					CirAnnotationValue.get_scopes_in_usig(value, scopes);
				}
			}
			else if(CirMutations.is_integer(expression)) {
				for(SymbolExpression value : values) {
					CirAnnotationValue.get_scopes_in_sign(value, scopes);
				}
			}
			else if(CirMutations.is_doubles(expression)) {
				for(SymbolExpression value : values) {
					CirAnnotationValue.get_scopes_in_real(value, scopes);
				}
			}
			else if(CirMutations.is_address(expression)) {
				for(SymbolExpression value : values) {
					CirAnnotationValue.get_scopes_in_addr(value, scopes);
				}
			}
			else {
				for(SymbolExpression value : values) {
					CirAnnotationValue.get_scopes_in_auto(value, scopes);
				}
			}
			return scopes;
		}
	}
	/**
	 * @param expression
	 * @param values
	 * @return the set of abstract scope covered by the concrete values 
	 * @throws Exception
	 */
	protected static Collection<SymbolExpression> get_scopes_in(CType data_type, Collection<SymbolExpression> values) throws Exception {
		if(data_type == null) {
			throw new IllegalArgumentException("Invalid data_type: null");
		}
		else {
			Set<SymbolExpression> scopes = new HashSet<SymbolExpression>();
			if(CirMutations.is_boolean(data_type)) {
				for(SymbolExpression value : values) {
					CirAnnotationValue.get_scopes_in_bool(value, scopes);
				}
			}
			else if(CirMutations.is_usigned(data_type)) {
				for(SymbolExpression value : values) {
					CirAnnotationValue.get_scopes_in_usig(value, scopes);
				}
			}
			else if(CirMutations.is_integer(data_type)) {
				for(SymbolExpression value : values) {
					CirAnnotationValue.get_scopes_in_sign(value, scopes);
				}
			}
			else if(CirMutations.is_doubles(data_type)) {
				for(SymbolExpression value : values) {
					CirAnnotationValue.get_scopes_in_real(value, scopes);
				}
			}
			else if(CirMutations.is_address(data_type)) {
				for(SymbolExpression value : values) {
					CirAnnotationValue.get_scopes_in_addr(value, scopes);
				}
			}
			else {
				for(SymbolExpression value : values) {
					CirAnnotationValue.get_scopes_in_auto(value, scopes);
				}
			}
			return scopes;
		}
	}
	
	/* domain summarize analysis */
	/**
	 * @param in_scopes
	 * @param ou_scopes
	 * @throws Exception
	 */
	private static void sum_scopes_in_bool(Collection<SymbolExpression> in_scopes, Collection<SymbolExpression> ou_scopes) throws Exception {
		if(in_scopes.contains(true_value)) {
			if(in_scopes.contains(fals_value)) {
				ou_scopes.add(bool_value);
			}
			else {
				ou_scopes.add(true_value);
				ou_scopes.add(bool_value);
			}
		}
		else {
			if(in_scopes.contains(fals_value)) {
				ou_scopes.add(fals_value);
				ou_scopes.add(bool_value);
			}
			else {
				ou_scopes.add(bool_value);
			}
		}
	}
	/**
	 * @param in_scopes
	 * @param ou_scopes
	 * @throws Exception
	 */
	private static void sum_scopes_in_usig(Collection<SymbolExpression> in_scopes, Collection<SymbolExpression> ou_scopes) throws Exception {
		if(in_scopes.contains(post_value)) {
			if(in_scopes.contains(zero_value)) {
				ou_scopes.add(nneg_value);
				ou_scopes.add(numb_value);
			}
			else {
				ou_scopes.add(post_value);
				ou_scopes.add(nneg_value);
				ou_scopes.add(numb_value);
			}
		}
		else {
			if(in_scopes.contains(zero_value)) {
				ou_scopes.add(zero_value);
				ou_scopes.add(nneg_value);
				ou_scopes.add(numb_value);
			}
			else {
				ou_scopes.add(nneg_value);
				ou_scopes.add(numb_value);
			}
		}
	}
	/**
	 * @param in_scopes
	 * @param ou_scopes
	 * @throws Exception
	 */
	private static void sum_scopes_in_numb(Collection<SymbolExpression> in_scopes, Collection<SymbolExpression> ou_scopes) throws Exception {
		if(in_scopes.contains(post_value)) {
			if(in_scopes.contains(negt_value)) {
				if(in_scopes.contains(zero_value)) {		/* {+, -, 0} */
					ou_scopes.add(numb_value);
				}
				else {										/* {+, -} */
					ou_scopes.add(nzro_value);
					ou_scopes.add(numb_value);
				}
			}
			else {
				if(in_scopes.contains(zero_value)) {		/* {+, 0} */
					ou_scopes.add(nneg_value);
					ou_scopes.add(numb_value);
				}
				else {										/* {+} */
					ou_scopes.add(post_value);
					ou_scopes.add(nzro_value);
					ou_scopes.add(nneg_value);
					ou_scopes.add(numb_value);
				}
			}
		}
		else {
			if(in_scopes.contains(negt_value)) {
				if(in_scopes.contains(zero_value)) {		/* {-, 0} */
					ou_scopes.add(npos_value);
					ou_scopes.add(numb_value);
				}
				else {										/* {-} */
					ou_scopes.add(negt_value);
					ou_scopes.add(nzro_value);
					ou_scopes.add(npos_value);
					ou_scopes.add(numb_value);
				}
			}
			else {
				if(in_scopes.contains(zero_value)) {		/* {0} */
					ou_scopes.add(zero_value);
					ou_scopes.add(npos_value);
					ou_scopes.add(nneg_value);
					ou_scopes.add(numb_value);
				}
				else {
					ou_scopes.add(numb_value);
				}
			}
		}
	}
	/**
	 * @param in_scopes
	 * @param ou_scopes
	 * @throws Exception
	 */
	private static void sum_scopes_in_addr(Collection<SymbolExpression> in_scopes, Collection<SymbolExpression> ou_scopes) throws Exception {
		if(in_scopes.contains(nnul_value)) {
			if(in_scopes.contains(null_value)) {
				ou_scopes.add(addr_value);
			}
			else {
				ou_scopes.add(nnul_value);
				ou_scopes.add(addr_value);
			}
		}
		else {
			if(in_scopes.contains(null_value)) {
				ou_scopes.add(null_value);
				ou_scopes.add(addr_value);
			}
			else {
				ou_scopes.add(addr_value);
			}
		}
	}
	/**
	 * @param in_scopes
	 * @param ou_scopes
	 * @throws Exception
	 */
	private static void sum_scopes_in_auto(Collection<SymbolExpression> in_scopes, Collection<SymbolExpression> ou_scopes) throws Exception { }
	/**
	 * @param expression
	 * @param scopes
	 * @return
	 * @throws Exception
	 */
	protected static Collection<SymbolExpression> sum_scopes_in(CirExpression expression, Collection<SymbolExpression> in_scopes) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			Set<SymbolExpression> ou_scopes = new HashSet<SymbolExpression>();
			if(in_scopes == null || in_scopes.isEmpty()) { /* no summarize */ }
			else if(CirMutations.is_boolean(expression)) {
				CirAnnotationValue.sum_scopes_in_bool(in_scopes, ou_scopes);
			}
			else if(CirMutations.is_usigned(expression)) {
				CirAnnotationValue.sum_scopes_in_usig(in_scopes, ou_scopes);
			}
			else if(CirMutations.is_integer(expression)) {
				CirAnnotationValue.sum_scopes_in_numb(in_scopes, ou_scopes);
			}
			else if(CirMutations.is_doubles(expression)) {
				CirAnnotationValue.sum_scopes_in_numb(in_scopes, ou_scopes);
			}
			else if(CirMutations.is_address(expression)) {
				CirAnnotationValue.sum_scopes_in_addr(in_scopes, ou_scopes);
			}
			else {
				CirAnnotationValue.sum_scopes_in_auto(in_scopes, ou_scopes);
			}
			return ou_scopes;
		}
	}
	/**
	 * @param expression
	 * @param scopes
	 * @return
	 * @throws Exception
	 */
	protected static Collection<SymbolExpression> sum_scopes_in(CType data_type, Collection<SymbolExpression> in_scopes) throws Exception {
		if(data_type == null) {
			throw new IllegalArgumentException("Invalid data_type: null");
		}
		else {
			Set<SymbolExpression> ou_scopes = new HashSet<SymbolExpression>();
			if(in_scopes == null || in_scopes.isEmpty()) { /* no summarize */ }
			else if(CirMutations.is_boolean(data_type)) {
				CirAnnotationValue.sum_scopes_in_bool(in_scopes, ou_scopes);
			}
			else if(CirMutations.is_usigned(data_type)) {
				CirAnnotationValue.sum_scopes_in_usig(in_scopes, ou_scopes);
			}
			else if(CirMutations.is_integer(data_type)) {
				CirAnnotationValue.sum_scopes_in_numb(in_scopes, ou_scopes);
			}
			else if(CirMutations.is_doubles(data_type)) {
				CirAnnotationValue.sum_scopes_in_numb(in_scopes, ou_scopes);
			}
			else if(CirMutations.is_address(data_type)) {
				CirAnnotationValue.sum_scopes_in_addr(in_scopes, ou_scopes);
			}
			else {
				CirAnnotationValue.sum_scopes_in_auto(in_scopes, ou_scopes);
			}
			return ou_scopes;
		}
	}
	
	/* domain subsumption analysis */
	/**
	 * @param value
	 * @param scopes
	 * @throws Exception
	 */
	private static void nex_scopes_in_bool(SymbolExpression value, Collection<SymbolExpression> scopes) throws Exception {
		if(value instanceof SymbolConstant) {
			if(((SymbolConstant) value).get_bool()) {
				scopes.add(true_value);
			}
			else {
				scopes.add(fals_value);
			}
		}
		else if(value == true_value) {
			scopes.add(bool_value);
		}
		else if(value == fals_value) {
			scopes.add(bool_value);
		}
		else if(value == bool_value) { }
		else {
			scopes.add(bool_value);
		}
	}
	/**
	 * @param value
	 * @param scopes
	 * @throws Exception
	 */
	private static void nex_scopes_in_usig(SymbolExpression value, Collection<SymbolExpression> scopes) throws Exception {
		if(value instanceof SymbolConstant) {
			if(((SymbolConstant) value).get_long() == 0) {
				scopes.add(zero_value);
			}
			else {
				scopes.add(post_value);
			}
		}
		else if(value == post_value) {
			scopes.add(npos_value);
		}
		else if(value == zero_value) {
			scopes.add(npos_value);
		}
		else if(value == npos_value) {
			scopes.add(numb_value);
		}
		else if(value == numb_value) { }
		else {
			scopes.add(npos_value);
		}
	}
	/**
	 * @param value
	 * @param scopes
	 * @throws Exception
	 */
	private static void nex_scopes_in_sign(SymbolExpression value, Collection<SymbolExpression> scopes) throws Exception {
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
		else if(value == post_value) {
			scopes.add(nzro_value);
			scopes.add(nneg_value);
		}
		else if(value == zero_value) {
			scopes.add(npos_value);
			scopes.add(nneg_value);
		}
		else if(value == negt_value) {
			scopes.add(nzro_value);
			scopes.add(npos_value);
		}
		else if(value == npos_value) {
			scopes.add(numb_value);
		}
		else if(value == nzro_value) {
			scopes.add(numb_value);
		}
		else if(value == nneg_value) {
			scopes.add(numb_value);
		}
		else if(value == numb_value) { }
		else {
			scopes.add(numb_value);
		}
	}
	/**
	 * @param value
	 * @param scopes
	 * @throws Exception
	 */
	private static void nex_scopes_in_real(SymbolExpression value, Collection<SymbolExpression> scopes) throws Exception {
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
		else if(value == post_value) {
			scopes.add(nzro_value);
			scopes.add(nneg_value);
		}
		else if(value == zero_value) {
			scopes.add(npos_value);
			scopes.add(nneg_value);
		}
		else if(value == negt_value) {
			scopes.add(nzro_value);
			scopes.add(npos_value);
		}
		else if(value == npos_value) {
			scopes.add(numb_value);
		}
		else if(value == nzro_value) {
			scopes.add(numb_value);
		}
		else if(value == nneg_value) {
			scopes.add(numb_value);
		}
		else if(value == numb_value) { }
		else {
			scopes.add(numb_value);
		}
	}
	/**
	 * @param value
	 * @param scopes
	 * @throws Exception
	 */
	private static void nex_scopes_in_addr(SymbolExpression value, Collection<SymbolExpression> scopes) throws Exception {
		if(value instanceof SymbolConstant) {
			if(((SymbolConstant) value).get_long() == 0) {
				scopes.add(null_value);
			}
			else {
				scopes.add(nnul_value);
			}
		}
		else if(value == null_value) {
			scopes.add(addr_value);
		}
		else if(value == nnul_value) {
			scopes.add(addr_value);
		}
		else if(value == addr_value) { }
		else {
			scopes.add(addr_value);
		}
	}
	/**
	 * @param value
	 * @param scopes
	 * @throws Exception
	 */
	private static void nex_scopes_in_auto(SymbolExpression value, Collection<SymbolExpression> scopes) throws Exception { }
	/**
	 * @param expression
	 * @param value
	 * @return the set of abstract scopes dominated by the expression and its value
	 * @throws Exception
	 */
	protected static Collection<SymbolExpression> nex_scopes_in(CirExpression expression, SymbolExpression value) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as null");
		}
		else {
			Set<SymbolExpression> scopes = new HashSet<SymbolExpression>();
			if(CirMutations.is_boolean(expression)) {
				CirAnnotationValue.nex_scopes_in_bool(value, scopes);
			}
			else if(CirMutations.is_usigned(expression)) {
				CirAnnotationValue.nex_scopes_in_usig(value, scopes);
			}
			else if(CirMutations.is_integer(expression)) {
				CirAnnotationValue.nex_scopes_in_sign(value, scopes);
			}
			else if(CirMutations.is_doubles(expression)) {
				CirAnnotationValue.nex_scopes_in_real(value, scopes);
			}
			else if(CirMutations.is_address(expression)) {
				CirAnnotationValue.nex_scopes_in_addr(value, scopes);
			}
			else {
				CirAnnotationValue.nex_scopes_in_auto(value, scopes);
			}
			return scopes;
		}
	}
	
}
