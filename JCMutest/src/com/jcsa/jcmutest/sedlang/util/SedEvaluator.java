package com.jcsa.jcmutest.sedlang.util;

import com.jcsa.jcmutest.sedlang.lang.expr.SedBinaryExpression;
import com.jcsa.jcmutest.sedlang.lang.expr.SedCallExpression;
import com.jcsa.jcmutest.sedlang.lang.expr.SedConstant;
import com.jcsa.jcmutest.sedlang.lang.expr.SedDefaultValue;
import com.jcsa.jcmutest.sedlang.lang.expr.SedExpression;
import com.jcsa.jcmutest.sedlang.lang.expr.SedFieldExpression;
import com.jcsa.jcmutest.sedlang.lang.expr.SedIdExpression;
import com.jcsa.jcmutest.sedlang.lang.expr.SedInitializerList;
import com.jcsa.jcmutest.sedlang.lang.expr.SedLiteral;
import com.jcsa.jcmutest.sedlang.lang.expr.SedUnaryExpression;
import com.jcsa.jcmutest.sedlang.lang.token.SedArgumentList;
import com.jcsa.jcmutest.sedlang.lang.token.SedField;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It provides the interface to evaluate the symbolic expression.
 * 
 * @author yukimula
 *
 */
public class SedEvaluator {
	
	/* definition */
	private SedEvalScope scope;
	public SedEvaluator() {
		this.scope = new SedEvalScope();
	}
	
	/* scope controller */
	/**
	 * @return get the scope of the evaluator
	 */
	public SedEvalScope get_scope() { return this.scope; }
	/**
	 * push new scope under the current scope as its child w.r.t. key
	 * @param key
	 * @throws Exception
	 */
	public void push_scope(Object key) throws Exception {
		this.scope = this.scope.get_child(key);
	}
	/**
	 * remove the current scope and recover its parent with a key
	 * @param key
	 * @throws Exception
	 */
	public void pop_scope(Object key) throws Exception {
		if(this.scope.is_root())
			throw new IllegalArgumentException("Root scope reached");
		else if(this.scope.get_scope_key() != key)
			throw new IllegalArgumentException("Unable to match key");
		else {
			this.scope = this.scope.get_parent();
		}
	}
	
	/* evaluation methods */
	/**
	 * @param source
	 * @return the expression evaluated from the source
	 * @throws Exception
	 */
	public SedExpression evaluate(SedExpression source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else {
			String key = source.generate_code();
			if(this.scope.has(key)) 
				return this.scope.get(key);
			else if(source instanceof SedIdExpression)
				return this.eval_id_expression((SedIdExpression) source);
			else if(source instanceof SedConstant)
				return this.eval_constant((SedConstant) source);
			else if(source instanceof SedLiteral)
				return this.eval_literal((SedLiteral) source);
			else if(source instanceof SedDefaultValue)
				return this.eval_default_value((SedDefaultValue) source);
			else if(source instanceof SedUnaryExpression)
				return this.eval_unary_expression((SedUnaryExpression) source);
			else if(source instanceof SedCallExpression)
				return this.eval_call_expression((SedCallExpression) source);
			else if(source instanceof SedFieldExpression)
				return this.eval_field_expression((SedFieldExpression) source);
			else if(source instanceof SedInitializerList)
				return this.eval_initializer_list((SedInitializerList) source);
			else if(source instanceof SedBinaryExpression)
				return this.eval_binary_expression((SedBinaryExpression) source);
			else
				throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	
	/* basic and other expression */
	private SedExpression eval_id_expression(SedIdExpression source) throws Exception {
		return (SedExpression) source.clone();
	}
	private SedExpression eval_constant(SedConstant source) throws Exception {
		return (SedExpression) source.clone();
	}
	private SedExpression eval_literal(SedLiteral source) throws Exception {
		return (SedExpression) source.clone();
	}
	private SedExpression eval_default_value(SedDefaultValue source) throws Exception {
		return (SedExpression) source.clone();
	}
	private SedExpression eval_call_expression(SedCallExpression source) throws Exception {
		SedExpression function = this.evaluate(source.get_function());
		SedArgumentList old_argument_list = source.get_argument_list();
		SedArgumentList new_argument_list = new SedArgumentList();
		for(int k = 0; k < old_argument_list.number_of_arguments(); k++) {
			new_argument_list.add_child(
					this.evaluate(old_argument_list.get_argument(k)));
		}
		SedCallExpression target = new SedCallExpression(
				source.get_cir_expression(), source.get_data_type());
		target.add_child(function); target.add_child(new_argument_list);
		return this.scope.invocate(target);
	}
	private SedExpression eval_field_expression(SedFieldExpression source) throws Exception {
		SedFieldExpression result = new SedFieldExpression(
				source.get_cir_expression(), source.get_data_type());
		result.add_child(this.evaluate(source.get_body()));
		result.add_child(new SedField(source.get_field().get_name()));
		return result;
	}
	private SedExpression eval_initializer_list(SedInitializerList source) throws Exception {
		SedInitializerList list = new SedInitializerList(
				source.get_cir_expression(), source.get_data_type());
		for(int k = 0; k < source.number_of_elements(); k++) {
			list.add_child(this.evaluate(source.get_element(k)));
		}
		return list;
	}
	
	/* unary expression category */
	private CConstant arith_neg(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	
			result.set_int(constant.get_bool()?-1:0);	break;
		case c_char:	
		case c_uchar:	
			result.set_int(-constant.get_char().charValue()); break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
			result.set_int(-constant.get_integer().intValue()); break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
			result.set_long(-constant.get_long().longValue()); break;
		case c_float:
			result.set_float(-constant.get_float().floatValue()); break;
		case c_double:
		case c_ldouble:
			result.set_double(-constant.get_double().doubleValue()); break;
		default: throw new IllegalArgumentException("Invalid constant.");
		}
		return result;
	}
	private SedExpression eval_arith_neg(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SedConstant) {
			CConstant constant = this.arith_neg(
					((SedConstant) operand).get_constant());
			return new SedConstant(source.get_cir_expression(), 
						source.get_data_type(), constant);
		}
		else if(operand instanceof SedUnaryExpression) {
			COperator operator = 
					((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.negative) {
				return ((SedUnaryExpression) operand).get_operand();
			}
			else {
				SedUnaryExpression expression = 
						new SedUnaryExpression(
								source.get_cir_expression(), 
								source.get_data_type(), 
								COperator.negative);
				expression.add_child(operand);
				return expression;
			}
		}
		else if(operand instanceof SedBinaryExpression) {
			COperator operator = 
					((SedBinaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.arith_sub) {
				SedExpression expression = new SedBinaryExpression(
						source.get_cir_expression(), 
						source.get_data_type(), COperator.arith_sub);
				expression.add_child(((SedBinaryExpression) operand).get_roperand());
				expression.add_child(((SedBinaryExpression) operand).get_loperand());
				return expression;
			}
			else {
				SedUnaryExpression expression = 
						new SedUnaryExpression(
								source.get_cir_expression(), 
								source.get_data_type(), 
								COperator.negative);
				expression.add_child(operand);
				return expression;
			}
		}
		else {
			SedUnaryExpression expression = 
					new SedUnaryExpression(
							source.get_cir_expression(), 
							source.get_data_type(), 
							COperator.negative);
			expression.add_child(operand);
			return expression;
		}
	}
	private CConstant bitws_rsv(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	
			result.set_int(constant.get_bool()?~1:~0);	break;
		case c_char:	
		case c_uchar:	
			result.set_int(~constant.get_char().charValue()); break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
			result.set_int(~constant.get_integer().intValue()); break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
			result.set_long(~constant.get_long().longValue()); break;
		default: throw new IllegalArgumentException("Invalid constant.");
		}
		return result;
	}
	private SedExpression eval_bitws_rsv(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SedConstant) {
			CConstant constant = 
					this.bitws_rsv(((SedConstant) operand).get_constant());
			return new SedConstant(source.
					get_cir_expression(), source.get_data_type(), constant);
		}
		else if(operand instanceof SedUnaryExpression) {
			COperator operator = 
					((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.bit_not) {
				return ((SedUnaryExpression) operand).get_operand();
			}
			else {
				SedExpression expression = new SedUnaryExpression(
						source.get_cir_expression(), source.get_data_type(),
						COperator.bit_not);
				expression.add_child(operand);
				return expression;
			}
		}
		else {
			SedExpression expression = new SedUnaryExpression(
					source.get_cir_expression(), source.get_data_type(),
					COperator.bit_not);
			expression.add_child(operand);
			return expression;
		}
	}
	private CConstant logic_not(CConstant constant) throws Exception {
		CConstant result = new CConstant();
		switch(constant.get_type().get_tag()) {
		case c_bool:	
			result.set_bool(!constant.get_bool());	break;
		case c_char:	
		case c_uchar:	
			result.set_bool(constant.get_char().charValue() == 0); break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
			result.set_bool(constant.get_integer().intValue() == 0); break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
			result.set_bool(constant.get_long().longValue() == 0); break;
		default: throw new IllegalArgumentException("Invalid constant.");
		}
		return result;
	}
	private SedExpression eval_logic_not(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SedConstant) {
			CConstant constant = 
					this.logic_not(((SedConstant) operand).get_constant());
			return new SedConstant(source.
					get_cir_expression(), source.get_data_type(), constant);
		}
		else if(operand instanceof SedUnaryExpression) {
			COperator operator = 
					((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.logic_not) {
				return ((SedUnaryExpression) operand).get_operand();
			}
			else {
				SedExpression expression = new SedUnaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.logic_not);
				expression.add_child(operand);
				return expression;
			}
		}
		else {
			SedExpression expression = new SedUnaryExpression(
					source.get_cir_expression(),
					source.get_data_type(), COperator.logic_not);
			expression.add_child(operand);
			return expression;
		}
	}
	private SedExpression eval_address_of(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SedUnaryExpression) {
			COperator operator = 
					((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.dereference) {
				return ((SedUnaryExpression) operand).get_operand();
			}
			else {
				SedExpression expression = new SedUnaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.address_of);
				expression.add_child(operand);
				return expression;
			}
		}
		else {
			SedExpression expression = new SedUnaryExpression(
					source.get_cir_expression(),
					source.get_data_type(), COperator.address_of);
			expression.add_child(operand);
			return expression;
		}
	}
	private SedExpression eval_dereference(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SedUnaryExpression) {
			COperator operator = 
					((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.address_of) {
				return ((SedUnaryExpression) operand).get_operand();
			}
			else {
				SedExpression expression = new SedUnaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.dereference);
				expression.add_child(operand);
				return expression;
			}
		}
		else {
			SedExpression expression = new SedUnaryExpression(
					source.get_cir_expression(),
					source.get_data_type(), COperator.dereference);
			expression.add_child(operand);
			return expression;
		}
	}
	private SedExpression eval_type_cast(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand());
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		if(operand instanceof SedConstant) {
			CConstant constant = new CConstant();
			if(data_type instanceof CBasicType) {
				switch(((CBasicType) data_type).get_tag()) {
				case c_bool:	constant.set_bool(((SedConstant) operand).get_bool()); break;
				case c_char:
				case c_uchar:	constant.set_char(((SedConstant) operand).get_char()); break;
				case c_short:
				case c_ushort:	constant.set_int(((SedConstant) operand).get_short()); break;
				case c_int:
				case c_uint:	constant.set_int(((SedConstant) operand).get_int()); break;
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:	constant.set_long(((SedConstant) operand).get_long()); break;
				case c_float:	constant.set_float(((SedConstant) operand).get_float()); break;
				case c_double:
				case c_ldouble:	constant.set_double(((SedConstant) operand).get_double()); break;
				default: throw new IllegalArgumentException(data_type.generate_code());
				}
			}
			else if(data_type instanceof CEnumType) {
				constant.set_int(((SedConstant) operand).get_int());
			}
			else if(data_type instanceof CArrayType
					|| data_type instanceof CPointerType) {
				constant.set_long(((SedConstant) operand).get_long());
			}
			else {
				throw new IllegalArgumentException(data_type.generate_code());
			}
			return new SedConstant(source.get_cir_expression(), data_type, constant);
		}
		else {
			SedExpression expression = new SedUnaryExpression(source.
					get_cir_expression(), source.get_data_type(), COperator.assign);
			expression.add_child(operand);
			return expression;
		}
	}
	private SedExpression eval_unary_expression(SedUnaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case positive:		return this.evaluate(source.get_operand());
		case negative:		return this.eval_arith_neg(source);
		case bit_not:		return this.eval_bitws_rsv(source);
		case logic_not:		return this.eval_logic_not(source);
		case address_of:	return this.eval_address_of(source);
		case dereference:	return this.eval_dereference(source);
		case assign:		return this.eval_type_cast(source);
		default: throw new IllegalArgumentException(source.generate_code());
		}
	}
	
	/* binary expression category */
	private SedExpression eval_binary_expression(SedBinaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		case logic_and:
		case logic_or:
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
		default: throw new IllegalArgumentException(source.generate_code());
		}
	}
	
	/* {+, *, &, |, ^, &&, ||} */
	
	
	
	
}
