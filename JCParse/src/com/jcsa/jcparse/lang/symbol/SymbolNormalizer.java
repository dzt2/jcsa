package com.jcsa.jcparse.lang.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It implements the normalization of reference structural to typed name.
 * 
 * @author yukimula
 *
 */
final class SymbolNormalizer {
	
	/* constructor and singleton */
	/** maps from generated name to corresponding replaced **/
	private	Map<SymbolIdentifier, SymbolExpression> names;
	/** maps from used expressions to replaced identifiers **/
	private Map<SymbolExpression, SymbolIdentifier> index;
	private	Set<String> spec_names;
	/**
	 * private constructor for singleton mode in normalize
	 */
	private SymbolNormalizer() {
		this.names = new HashMap<SymbolIdentifier, SymbolExpression>();
		this.index = new HashMap<SymbolExpression, SymbolIdentifier>();
		
		this.spec_names = new HashSet<String>();
		this.spec_names.add("return");
		this.spec_names.add("if");
		this.spec_names.add("case");
		this.spec_names.add("switch");
		this.spec_names.add("for");
		this.spec_names.add("while");
		this.spec_names.add("do");
		this.spec_names.add("@stmt");
		this.spec_names.add("@ast");
		this.spec_names.add("default");
	}
	/** single instance **/
	private static final SymbolNormalizer normalizer = new SymbolNormalizer();
	
	/* basic method & operation */
	/**
	 * @param expression
	 * @return the unique name of the expression to be replaced with
	 * @throws Exception
	 */
	private	SymbolIdentifier get_unique_name(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			if(!this.index.containsKey(expression)) {
				CType type = SymbolFactory.get_type(expression.get_data_type());
				String name;
				if(type instanceof CBasicType) {
					name = ((CBasicType) type).get_tag().name();
					if(name.startsWith("c_")) {
						name = name.substring(2).strip();
					}
				}
				else if(type instanceof CArrayType) {
					name = "array";
				}
				else if(type instanceof CPointerType) {
					name = "point";
				}
				else if(type instanceof CStructType) {
					name = ((CStructType) type).get_name();
					if(name == null) {
						name = "struct";
					}
					else if(name.startsWith("struct")) {
						name = name.substring(6).strip();
					}
					if(name.isEmpty()) {
						name = "struct";
					}
				}
				else if(type instanceof CUnionType) {
					name = ((CStructType) type).get_name();
					if(name == null) {
						name = "union";
					}
					else if(name.startsWith("union")) {
						name = name.substring(5).strip();
					}
					if(name.isEmpty()) {
						name = "union";
					}
				}
				else if(type instanceof CEnumType) {
					name = "int";
				}
				else {
					name = "auto";
				}
				int code = 0;
				while(true) {
					SymbolIdentifier id = SymbolFactory.identifier(type, name, code);
					if(!this.names.containsKey(id)) {
						this.names.put(id, expression);
						this.index.put(expression, id);
						break;
					}
					code++;
				}
			}
			return this.index.get(expression);
		}
	}
	/**
	 * It updates the map from expressions to the replaced identifiers
	 * @param index
	 * @throws Exception
	 */
	private	void	set_output_maps(Map<SymbolExpression, SymbolIdentifier> index) throws Exception {
		if(index != null) {
			index.clear();
			for(SymbolExpression source : this.index.keySet()) {
				SymbolIdentifier target = this.index.get(source);
				index.put(source, target);
			}
		}
	}
	/**
	 * It clears the maps between source expressions and identifiers
	 */
	private void 	clear_states() { this.names.clear(); this.index.clear(); }
	
	/* normalization methods */
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	SymbolExpression	norm(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(expression instanceof SymbolIdentifier) {
			return this.norm_identifier((SymbolIdentifier) expression);
		}
		else if(expression instanceof SymbolConstant) {
			return this.norm_constant((SymbolConstant) expression);
		}
		else if(expression instanceof SymbolLiteral) {
			return this.norm_literal((SymbolLiteral) expression);
		}
		else if(expression instanceof SymbolUnaryExpression) {
			return this.norm_unary_expression((SymbolUnaryExpression) expression);
		}
		else if(expression instanceof SymbolBinaryExpression) {
			return this.norm_binary_expression((SymbolBinaryExpression) expression);
		}
		else if(expression instanceof SymbolCallExpression) {
			return this.norm_call_expression((SymbolCallExpression) expression);
		}
		else if(expression instanceof SymbolCastExpression) {
			return this.norm_cast_expression((SymbolCastExpression) expression);
		}
		else if(expression instanceof SymbolConditionExpression) {
			return this.norm_cond_expression((SymbolConditionExpression) expression);
		}
		else if(expression instanceof SymbolFieldExpression) {
			return this.norm_field_expression((SymbolFieldExpression) expression);
		}
		else if(expression instanceof SymbolInitializerList) {
			return this.norm_init_list((SymbolInitializerList) expression);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	private	SymbolExpression	norm_identifier(SymbolIdentifier expression) throws Exception {
		String name = expression.get_name();
		if(!this.spec_names.contains(name)) {
			return this.get_unique_name(expression);
		}
		return expression;
	}
	private	SymbolExpression	norm_constant(SymbolConstant expression) throws Exception { return expression; }
	private	SymbolExpression	norm_literal(SymbolLiteral expression) throws Exception { return expression; }
	private	SymbolExpression	norm_unary_expression(SymbolUnaryExpression expression) throws Exception {
		COperator operator = expression.get_coperator();
		switch(operator) {
		case negative:
		{
			return SymbolFactory.arith_neg(this.norm(expression.get_operand()));
		}
		case bit_not:
		{
			return SymbolFactory.bitws_rsv(this.norm(expression.get_operand()));
		}
		case logic_not:
		{
			return SymbolFactory.logic_not(this.norm(expression.get_operand()));
		}
		case address_of:
		{
			return SymbolFactory.address_of(this.norm(expression.get_operand()));
		}
		case dereference:
		{
			return this.get_unique_name(expression);
		}
		default:	throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	private	SymbolExpression	norm_binary_expression(SymbolBinaryExpression expression) throws Exception {
		COperator operator = expression.get_coperator();
		SymbolExpression loperand = this.norm(expression.get_loperand());
		SymbolExpression roperand = this.norm(expression.get_roperand());
		CType type = expression.get_data_type();
		switch(operator) {
		case arith_add:		return SymbolFactory.arith_add(type, loperand, roperand);
		case arith_sub:		return SymbolFactory.arith_sub(type, loperand, roperand);
		case arith_mul:		return SymbolFactory.arith_mul(type, loperand, roperand);
		case arith_div:		return SymbolFactory.arith_div(type, loperand, roperand);
		case arith_mod:		return SymbolFactory.arith_mod(type, loperand, roperand);
		case bit_and:		return SymbolFactory.bitws_and(type, loperand, roperand);
		case bit_or:		return SymbolFactory.bitws_ior(type, loperand, roperand);
		case bit_xor:		return SymbolFactory.bitws_xor(type, loperand, roperand);
		case left_shift:	return SymbolFactory.bitws_lsh(type, loperand, roperand);
		case righ_shift:	return SymbolFactory.bitws_rsh(type, loperand, roperand);
		case logic_and:		return SymbolFactory.logic_and(loperand, roperand);
		case logic_or:		return SymbolFactory.logic_ior(loperand, roperand);
		case positive:		return SymbolFactory.logic_imp(loperand, roperand);
		case greater_tn:	return SymbolFactory.greater_tn(loperand, roperand);
		case greater_eq:	return SymbolFactory.greater_eq(loperand, roperand);
		case smaller_tn:	return SymbolFactory.smaller_tn(loperand, roperand);
		case smaller_eq:	return SymbolFactory.smaller_eq(loperand, roperand);
		case equal_with:	return SymbolFactory.equal_with(loperand, roperand);
		case not_equals:	return SymbolFactory.not_equals(loperand, roperand);
		case assign:		return SymbolFactory.exp_assign(loperand, roperand);
		case increment:		return SymbolFactory.imp_assign(loperand, roperand);
		default:	throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	private	SymbolExpression	norm_call_expression(SymbolCallExpression expression) throws Exception {
		SymbolExpression function = expression.get_function();
		List<Object> arguments = new ArrayList<Object>();
		SymbolArgumentList alist = expression.get_argument_list();
		for(int k = 0; k < alist.number_of_arguments(); k++) {
			arguments.add(this.norm(alist.get_argument(k)));
		}
		return SymbolFactory.call_expression(function, arguments);
	}
	private	SymbolExpression	norm_cast_expression(SymbolCastExpression expression) throws Exception {
		return SymbolFactory.cast_expression(expression.get_data_type(), this.norm(expression.get_operand()));
	}
	private	SymbolExpression	norm_field_expression(SymbolFieldExpression expression) throws Exception {
		return SymbolFactory.field_expression(this.norm(expression.get_body()), expression.get_field().get_name());
	}
	private	SymbolExpression	norm_init_list(SymbolInitializerList expression) throws Exception {
		List<Object> elements = new ArrayList<Object>();
		for(int k = 0; k < expression.number_of_elements(); k++) {
			elements.add(this.norm(expression.get_element(k)));
		}
		return SymbolFactory.initializer_list(elements);
	}
	private	SymbolExpression	norm_cond_expression(SymbolConditionExpression expression) throws Exception {
		SymbolExpression condition = this.norm(expression.get_condition());
		SymbolExpression t_operand = this.norm(expression.get_t_operand());
		SymbolExpression f_operand = this.norm(expression.get_f_operand());
		return SymbolFactory.cond_expr(expression.get_data_type(), condition, t_operand, f_operand);
	}
	
	/* public normalization */
	/**
	 * @param expression	the expression to be normalized
	 * @param reset			true to reset the source-name index
	 * @param index			to preserve map from soruce to identifier name (unique)
	 * @return 				the expression as normalized version of input source
	 * @throws Exception
	 */
	protected static SymbolExpression normalize(SymbolExpression expression, 
			boolean reset, Map<SymbolExpression, SymbolIdentifier> index) throws Exception {
		if(reset) { normalizer.clear_states(); }
		expression = normalizer.norm(expression);
		normalizer.set_output_maps(index);
		return expression;
	}
	
}
