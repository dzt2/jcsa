package com.jcsa.jcmutest.mutant.sad2mutant.eval;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadBinaryExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadCallExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadConstant;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadDefaultValue;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFieldExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadIdExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadInitializerList;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadLiteral;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadMultiExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadUnaryExpression;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CEnumerator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * It performs the symbolic evaluation.
 * 
 * @author yukimula
 *
 */
public class SadEvaluator {
	
	/* definitions */
	private SadCallScope scope;
	private SadEvaluator() { }
	private static final SadEvaluator evaluator = new SadEvaluator();
	
	/* basic methods */
	/**
	 * @param constant
	 * @return cast the constant as boolean
	 * @throws Exception
	 */
	private boolean cast_to_bool(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:		return constant.get_bool().booleanValue();
		case c_char:
		case c_uchar:		return constant.get_char().charValue() != '\0';
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return constant.get_integer().intValue() != 0;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return constant.get_long().longValue() != 0L;
		case c_float:		return constant.get_float().floatValue() != 0.0f;
		case c_double:
		case c_ldouble:		return constant.get_double().doubleValue() != 0.0;
		default: throw new IllegalArgumentException("Unable to cast boolean");
		}
	}
	/**
	 * @param constant
	 * @return cast the constant into long value
	 * @throws Exception
	 */
	private long cast_to_long(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:		return constant.get_bool().booleanValue() ? 1 : 0;
		case c_char:
		case c_uchar:		return (long) constant.get_char().charValue();
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return (long) constant.get_integer().intValue();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return (long) constant.get_long().longValue();
		case c_float:		return (long) constant.get_float().floatValue();
		case c_double:
		case c_ldouble:		return (long) constant.get_double().doubleValue();
		default: 			throw new IllegalArgumentException("Unable to cast boolean");
		}
	}
	/**
	 * @param constant
	 * @return Long | Double
	 * @throws Exception
	 */
	private Object cast_to_number(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:		return Long.valueOf(constant.get_bool().booleanValue() ? 1 : 0);
		case c_char:
		case c_uchar:		return Long.valueOf(constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:		return Long.valueOf(constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:		return Long.valueOf(constant.get_long().longValue());
		case c_float:		return Double.valueOf(constant.get_float().floatValue());
		case c_double:
		case c_ldouble:		return Double.valueOf(constant.get_double().doubleValue());
		default: 			throw new IllegalArgumentException("Unable to cast boolean");
		}
	}
	/**
	 * @param source
	 * @param operator
	 * @return the set of operands directly used under the operator of multiple expression
	 * @throws Exception
	 */
	private Iterable<SadExpression> extend(SadMultiExpression source, COperator operator) throws Exception {
		List<SadExpression> operands = new ArrayList<SadExpression>();
		for(int i = 0; i < source.number_of_operands(); i++) {
			SadExpression operand = this.eval(source.get_operand(i));
			if(operand instanceof SadMultiExpression) {
				SadMultiExpression m_operand = (SadMultiExpression) operand;
				if(m_operand.get_operator().get_operator() == operator) {
					for(int j = 0; j < m_operand.number_of_operands(); j++) {
						operands.add((SadExpression) m_operand.get_operand(j).clone());
					}
				}
				else {
					operands.add((SadExpression) m_operand.clone());
				}
			}
			else {
				operands.add((SadExpression) operand.clone());
			}
		}
		return operands;
	}
	/**
	 * @param operator	{-, ~, !}
	 * @param operand	
	 * @return as operator(operand)
	 * @throws Exception
	 */
	private CConstant compute(COperator operator, CConstant operand) throws Exception {
		CConstant result = new CConstant();
		switch(operator) {
		case negative:
		{
			Object number = this.cast_to_number(operand);
			if(number instanceof Long) {
				result.set_long(-(((Long) number).longValue()));
			}
			else {
				result.set_double(-(((Double) number).doubleValue()));
			}
			break;
		}
		case bit_not:
		{
			result.set_long(~(this.cast_to_long(operand)));
		}
		break;
		case logic_not:
		{
			result.set_bool(!(this.cast_to_bool(operand)));
		}
		break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return result;
	}
	/**
	 * @param operator	{+, -, *, /, %, &, |, ^, <<, >>, <, <=, >, >=, ==, !=}
	 * @param loperand
	 * @param roperand
	 * @return loperand x roperand
	 * @throws Exception
	 */
	private CConstant compute(COperator operator, CConstant loperand, CConstant roperand) throws Exception {
		CConstant result = new CConstant();
		switch(operator) {
		case arith_add:
		{
			Object lvalue = this.cast_to_number(loperand);
			Object rvalue = this.cast_to_number(roperand);
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_long(x + y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_double(x + y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_double(x + y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_double(x + y);
				}
			}
			break;
		}
		case arith_sub:
		{
			Object lvalue = this.cast_to_number(loperand);
			Object rvalue = this.cast_to_number(roperand);
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_long(x - y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_double(x - y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_double(x - y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_double(x - y);
				}
			}
			break;
		}
		case arith_mul:
		{
			Object lvalue = this.cast_to_number(loperand);
			Object rvalue = this.cast_to_number(roperand);
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_long(x * y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_double(x * y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_double(x * y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_double(x * y);
				}
			}
			break;
		}
		case arith_div:
		{
			Object lvalue = this.cast_to_number(loperand);
			Object rvalue = this.cast_to_number(roperand);
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_long(x / y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_double(x / y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_double(x / y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_double(x / y);
				}
			}
			break;
		}
		case arith_mod:
		{
			long lvalue = this.cast_to_long(loperand);
			long rvalue = this.cast_to_long(roperand);
			result.set_long(lvalue % rvalue);
			break;
		}
		case bit_and:
		{
			long lvalue = this.cast_to_long(loperand);
			long rvalue = this.cast_to_long(roperand);
			result.set_long(lvalue & rvalue);
			break;
		}
		case bit_or:
		{
			long lvalue = this.cast_to_long(loperand);
			long rvalue = this.cast_to_long(roperand);
			result.set_long(lvalue | rvalue);
			break;
		}
		case bit_xor:
		{
			long lvalue = this.cast_to_long(loperand);
			long rvalue = this.cast_to_long(roperand);
			result.set_long(lvalue ^ rvalue);
			break;
		}
		case left_shift:
		{
			long lvalue = this.cast_to_long(loperand);
			long rvalue = this.cast_to_long(roperand);
			result.set_long(lvalue << rvalue);
			break;
		}
		case righ_shift:
		{
			long lvalue = this.cast_to_long(loperand);
			long rvalue = this.cast_to_long(roperand);
			result.set_long(lvalue >> rvalue);
			break;
		}
		case logic_and:
		{
			boolean lvalue = this.cast_to_bool(loperand);
			boolean rvalue = this.cast_to_bool(roperand);
			result.set_bool(lvalue && rvalue);
			break;
		}
		case logic_or:
		{
			boolean lvalue = this.cast_to_bool(loperand);
			boolean rvalue = this.cast_to_bool(roperand);
			result.set_bool(lvalue || rvalue);
			break;
		}
		case greater_tn:
		{
			Object lvalue = this.cast_to_number(loperand);
			Object rvalue = this.cast_to_number(roperand);
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_bool(x > y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_bool(x > y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_bool(x > y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_bool(x > y);
				}
			}
			break;
		}
		case greater_eq:
		{
			Object lvalue = this.cast_to_number(loperand);
			Object rvalue = this.cast_to_number(roperand);
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_bool(x >= y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_bool(x >= y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_bool(x >= y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_bool(x >= y);
				}
			}
			break;
		}
		case smaller_tn:
		{
			Object lvalue = this.cast_to_number(loperand);
			Object rvalue = this.cast_to_number(roperand);
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_bool(x < y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_bool(x < y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_bool(x < y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_bool(x < y);
				}
			}
			break;
		}
		case smaller_eq:
		{
			Object lvalue = this.cast_to_number(loperand);
			Object rvalue = this.cast_to_number(roperand);
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_bool(x <= y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_bool(x <= y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_bool(x <= y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_bool(x <= y);
				}
			}
			break;
		}
		case equal_with:
		{
			Object lvalue = this.cast_to_number(loperand);
			Object rvalue = this.cast_to_number(roperand);
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_bool(x == y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_bool(x == y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_bool(x == y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_bool(x == y);
				}
			}
			break;
		}
		case not_equals:
		{
			Object lvalue = this.cast_to_number(loperand);
			Object rvalue = this.cast_to_number(roperand);
			if(lvalue instanceof Long) {
				long x = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_bool(x != y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_bool(x != y);
				}
			}
			else {
				double x = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long y = ((Long) rvalue).longValue();
					result.set_bool(x != y);
				}
				else {
					double y = ((Double) rvalue).doubleValue();
					result.set_bool(x != y);
				}
			}
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return result;
	}
	/**
	 * @param operator {+, *,, &, |, ^, &&, ||}
	 * @return
	 * @throws Exception
	 */
	private CConstant init_value(COperator operator) throws Exception {
		CConstant constant = new CConstant();
		switch(operator) {
		case arith_add:		constant.set_long(0L); 	break;
		case arith_mul:		constant.set_long(1L); 	break;
		case bit_and:		constant.set_long(~1L); break;
		case bit_or:		constant.set_long(0L);	break;
		case bit_xor:		constant.set_long(0L);	break;
		case logic_and:		constant.set_bool(true);break;
		case logic_or:		constant.set_bool(false);break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return constant;
	}
	/**
	 * @param source
	 * @return constant::variables
	 * @throws Exception
	 */
	private List<SadExpression> divide_eval_multi_expr(SadMultiExpression source) throws Exception {
		/* 1. extend into the operands of arithmetic addition */
		COperator operator = source.get_operator().get_operator();
		Iterable<SadExpression> operands = this.extend(source, operator);
		
		/* 2. accumulate the operands into constant part and variable part */
		List<SadExpression> variables = new ArrayList<SadExpression>();
		CConstant constant = this.init_value(operator);
		for(SadExpression operand : operands) {
			if(operand instanceof SadConstant) {
				constant = this.compute(operator, constant, 
						((SadConstant) operand).get_constant());
			}
			else {
				variables.add(operand);
			}
		}
		
		/* 3. return divisions */	
		List<SadExpression> results = new ArrayList<SadExpression>();
		results.add(SadFactory.constant(constant));
		for(SadExpression variable : variables) {
			results.add(variable);
		}
		return results;
	}
	/**
	 * @param constant
	 * @param value
	 * @return whether the constant equals with value
	 * @throws Exception
	 */
	private boolean compare(CConstant constant, long value) throws Exception {
		CConstant roperand = new CConstant(); roperand.set_long(value);
		return this.cast_to_bool(this.
					compute(COperator.equal_with, constant, roperand));
	}
	
	/* syntax-directed algorithms */
	private SadExpression eval(SadExpression source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else {
			String key = source.generate_code();
			if(this.scope != null && this.scope.has(key)) {
				return (SadExpression) this.scope.get(key).clone();
			}
			else if(source instanceof SadIdExpression) 
				return this.eval_id_expression((SadIdExpression) source);
			else if(source instanceof SadConstant)
				return this.eval_constant((SadConstant) source);
			else if(source instanceof SadLiteral)
				return this.eval_literal((SadLiteral) source);
			else if(source instanceof SadDefaultValue)
				return this.eval_default_value((SadDefaultValue) source);
			else if(source instanceof SadUnaryExpression) {
				switch(((SadUnaryExpression) source).get_operator().get_operator()) {
				case positive:		return this.eval_arith_pos((SadUnaryExpression) source);
				case negative:		return this.eval_arith_neg((SadUnaryExpression) source);
				case bit_not:		return this.eval_bitws_rsv((SadUnaryExpression) source);
				case logic_not:		return this.eval_logic_not((SadUnaryExpression) source);
				case address_of:	return this.eval_address_of((SadUnaryExpression) source);
				case dereference:	return this.eval_dereference((SadUnaryExpression) source);
				case assign:		return this.eval_type_cast((SadUnaryExpression) source);
				default: throw new IllegalArgumentException(source.generate_code());
				}
			}
			else if(source instanceof SadMultiExpression) {
				switch(((SadMultiExpression) source).get_operator().get_operator()) {
				case arith_add:		return this.eval_arith_add((SadMultiExpression) source); 
				case arith_mul:		return this.eval_arith_mul((SadMultiExpression) source); 
				case bit_and:		return this.eval_bitws_and((SadMultiExpression) source); 
				case bit_or:		return this.eval_bitws_ior((SadMultiExpression) source); 
				case bit_xor:		return this.eval_bitws_xor((SadMultiExpression) source); 
				case logic_and:		return this.eval_logic_and((SadMultiExpression) source); 
				case logic_or:		return this.eval_logic_ior((SadMultiExpression) source); 
				default: throw new IllegalArgumentException(source.generate_code());
				}
			}
			else if(source instanceof SadBinaryExpression) {
				switch(((SadBinaryExpression) source).get_operator().get_operator()) {
				case arith_sub:		return this.eval_arith_sub((SadBinaryExpression) source);
				case arith_div:		return this.eval_arith_div((SadBinaryExpression) source);
				case arith_mod:		return this.eval_arith_mod((SadBinaryExpression) source);
				case left_shift:	return this.eval_bitws_lsh((SadBinaryExpression) source);
				case righ_shift:	return this.eval_bitws_rsh((SadBinaryExpression) source);
				case greater_tn:	return this.eval_greater_tn((SadBinaryExpression) source);
				case greater_eq:	return this.eval_greater_eq((SadBinaryExpression) source);
				case smaller_tn:	return this.eval_smaller_tn((SadBinaryExpression) source);
				case smaller_eq:	return this.eval_smaller_eq((SadBinaryExpression) source);
				case equal_with:	return this.eval_equal_with((SadBinaryExpression) source);
				case not_equals:	return this.eval_not_equals((SadBinaryExpression) source);
				default: 			throw new IllegalArgumentException(source.generate_code());
				}
			}
			else if(source instanceof SadFieldExpression)
				return this.eval_field_expression((SadFieldExpression) source);
			else if(source instanceof SadInitializerList)
				return this.eval_initializer_list((SadInitializerList) source);
			else if(source instanceof SadCallExpression)
				return this.eval_call_expression((SadCallExpression) source);
			else {
				throw new IllegalArgumentException("Unsupport: " + source);
			}
		}
	}
	
	/* basic expression ant others */
	private SadExpression eval_constant(SadConstant source) throws Exception {
		return (SadExpression) source.clone();
	}
	private SadExpression eval_id_expression(SadIdExpression source) throws Exception {
		return (SadExpression) source.clone();
	}
	private SadExpression eval_literal(SadLiteral source) throws Exception {
		return (SadExpression) source.clone();
	}
	private SadExpression eval_default_value(SadDefaultValue source) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_bool:		return SadFactory.constant(false);
			case c_char:
			case c_uchar:		return SadFactory.constant('\0');
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:		return SadFactory.constant(0);
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:		return SadFactory.constant(0L);
			case c_float:		return SadFactory.constant(0.0f);
			case c_double:
			case c_ldouble:		return SadFactory.constant(0.0);
			default: 			return (SadExpression) source.clone();
			}
		}
		else if(data_type instanceof CEnumerator) {
			return SadFactory.constant(0);
		}
		else {
			return (SadExpression) source.clone();
		}
	}
	private SadExpression eval_arith_pos(SadUnaryExpression source) throws Exception {
		return this.eval(source.get_operand());
	}
	private SadExpression eval_arith_neg(SadUnaryExpression source) throws Exception {
		SadExpression operand = this.eval(source.get_operand());
		if(operand instanceof SadConstant) {
			CConstant constant = this.compute(COperator.negative, 
						((SadConstant) operand).get_constant());
			return SadFactory.constant(constant);
		}
		else if(operand instanceof SadUnaryExpression) {
			if(((SadUnaryExpression) operand).get_operator().get_operator() == COperator.negative) {
				return (SadExpression) ((SadUnaryExpression) operand).get_operand().clone();
			}
			else {
				return SadFactory.arith_neg(source.get_data_type(), operand);
			}
		}
		else if(operand instanceof SadBinaryExpression) {
			if(((SadBinaryExpression) operand).get_operator().get_operator() == COperator.arith_sub) {
				SadExpression roperand = ((SadBinaryExpression) operand).get_loperand();
				SadExpression loperand = ((SadBinaryExpression) operand).get_roperand();
				return SadFactory.arith_sub(source.get_data_type(), 
						(SadExpression) loperand.clone(), (SadExpression) roperand.clone());
			}
			else {
				return SadFactory.arith_neg(source.get_data_type(), operand);
			}
		}
		else {
			return SadFactory.arith_neg(source.get_data_type(), operand);
		}
	}
	private SadExpression eval_bitws_rsv(SadUnaryExpression source) throws Exception {
		SadExpression operand = this.eval(source.get_operand());
		if(operand instanceof SadConstant) {
			CConstant constant = this.compute(COperator.bit_not, 
						((SadConstant) operand).get_constant());
			return SadFactory.constant(constant);
		}
		else if(operand instanceof SadUnaryExpression) {
			if(((SadUnaryExpression) operand).get_operator().get_operator() == COperator.bit_not) {
				return (SadExpression) ((SadUnaryExpression) operand).get_operand().clone();
			}
			else {
				return SadFactory.bitws_rsv(source.get_data_type(), operand);
			}
		}
		else {
			return SadFactory.bitws_rsv(source.get_data_type(), operand);
		}
	}
	private SadExpression eval_logic_not(SadUnaryExpression source) throws Exception {
		SadExpression operand = this.eval(source.get_operand());
		if(operand instanceof SadConstant) {
			CConstant constant = this.compute(COperator.logic_not, 
					((SadConstant) operand).get_constant());
			return SadFactory.constant(constant);
		}
		else if(operand instanceof SadUnaryExpression) {
			if(((SadUnaryExpression) operand).get_operator().get_operator() == COperator.logic_not) {
				return (SadExpression) ((SadUnaryExpression) operand).get_operand().clone();
			}
			else {
				return SadFactory.logic_not(source.get_data_type(), operand);
			}
		}
		else if(operand instanceof SadBinaryExpression) {
			SadExpression loperand = (SadExpression) ((SadBinaryExpression) operand).get_loperand().clone();
			SadExpression roperand = (SadExpression) ((SadBinaryExpression) operand).get_roperand().clone();
			
			switch(((SadBinaryExpression) operand).get_operator().get_operator()) {
			case greater_tn:	return SadFactory.smaller_eq(source.get_data_type(), loperand, roperand);
			case greater_eq:	return SadFactory.smaller_tn(source.get_data_type(), loperand, roperand);
			case smaller_tn:	return SadFactory.smaller_eq(source.get_data_type(), roperand, loperand);
			case smaller_eq:	return SadFactory.smaller_tn(source.get_data_type(), roperand, loperand);
			case equal_with:	return SadFactory.not_equals(source.get_data_type(), loperand, roperand);
			case not_equals:	return SadFactory.equal_with(source.get_data_type(), loperand, roperand);
			default:			return SadFactory.logic_not(source.get_data_type(), operand);
			}
		}
		else {
			return SadFactory.logic_not(source.get_data_type(), operand);
		}
	}
	private SadExpression eval_address_of(SadUnaryExpression source) throws Exception {
		SadExpression operand = this.eval(source.get_operand());
		if(operand instanceof SadUnaryExpression) {
			if(((SadUnaryExpression) operand).get_operator().get_operator() == COperator.dereference) {
				return (SadExpression) ((SadUnaryExpression) operand).get_operand().clone();
			}
			else {
				return SadFactory.address_of(source.get_data_type(), operand);
			}
		}
		else {
			return SadFactory.address_of(source.get_data_type(), operand);
		}
	}
	private SadExpression eval_dereference(SadUnaryExpression source) throws Exception {
		SadExpression operand = this.eval(source.get_operand());
		if(operand instanceof SadUnaryExpression) {
			if(((SadUnaryExpression) operand).get_operator().get_operator() == COperator.address_of) {
				return (SadExpression) ((SadUnaryExpression) operand).get_operand().clone();
			}
			else {
				return SadFactory.dereference(source.get_data_type(), operand);
			}
		}
		else {
			return SadFactory.dereference(source.get_data_type(), operand);
		}
	}
	private SadExpression eval_type_cast(SadUnaryExpression source) throws Exception {
		SadExpression operand = this.eval(source.get_operand());
		if(operand instanceof SadConstant) {
			CConstant constant = ((SadConstant) operand).get_constant();
			Object orig_value = this.cast_to_number(constant);
			CType cast_type = CTypeAnalyzer.get_value_type(source.get_data_type());
			if(orig_value instanceof Long) {
				long value = ((Long) orig_value).longValue();
				if(cast_type instanceof CBasicType) {
					switch(((CBasicType) cast_type).get_tag()) {
					case c_bool:
					{
						return SadFactory.constant(value != 0);
					}
					case c_char:
					case c_uchar:
					{
						return SadFactory.constant((char) value);
					}
					case c_short:
					case c_ushort:
					case c_int:
					case c_uint:
					{
						return SadFactory.constant((int) value);
					}
					case c_long:
					case c_ulong:
					case c_llong:
					case c_ullong:
					{
						return SadFactory.constant((long) value);
					}
					case c_float:
					{
						return SadFactory.constant((float) value);
					}
					case c_double:
					case c_ldouble:
					{
						return SadFactory.constant((double) value);
					}
					default: return SadFactory.type_cast(source.get_data_type(), operand);
					}
				}
				else if(cast_type instanceof CEnumType) {
					return SadFactory.constant((int) value);
				}
				else {
					return SadFactory.type_cast(source.get_data_type(), operand);
				}
			}
			else {
				double value = ((Double) orig_value).doubleValue();
				if(cast_type instanceof CBasicType) {
					switch(((CBasicType) cast_type).get_tag()) {
					case c_bool:
					{
						return SadFactory.constant(value != 0);
					}
					case c_char:
					case c_uchar:
					{
						return SadFactory.constant((char) value);
					}
					case c_short:
					case c_ushort:
					case c_int:
					case c_uint:
					{
						return SadFactory.constant((int) value);
					}
					case c_long:
					case c_ulong:
					case c_llong:
					case c_ullong:
					{
						return SadFactory.constant((long) value);
					}
					case c_float:
					{
						return SadFactory.constant((float) value);
					}
					case c_double:
					case c_ldouble:
					{
						return SadFactory.constant((double) value);
					}
					default: return SadFactory.type_cast(source.get_data_type(), operand);
					}
				}
				else if(cast_type instanceof CEnumType) {
					return SadFactory.constant((int) value);
				}
				else {
					return SadFactory.type_cast(source.get_data_type(), operand);
				}
			}
		}
		else {
			return SadFactory.type_cast(source.get_data_type(), operand);
		}
	}
	private SadExpression eval_field_expression(SadFieldExpression source) throws Exception {
		SadExpression body = this.eval(source.get_body());
		String field = source.get_field().get_name();
		return SadFactory.field_expression(source.get_data_type(), body, field);
	}
	private SadExpression eval_initializer_list(SadInitializerList source) throws Exception {
		List<SadExpression> operands = new ArrayList<SadExpression>();
		for(int k = 0; k < source.number_of_elements(); k++) {
			operands.add(this.eval(source.get_element(k)));
		}
		return SadFactory.initializer_list(source.get_data_type(), operands);
	}
	private SadExpression eval_call_expression(SadCallExpression source) throws Exception {
		SadExpression function = this.eval(source.get_function());
		List<SadExpression> list = new ArrayList<SadExpression>();
		for(int k =0; k < source.
				get_argument_list().number_of_arguments(); k++) {
			list.add(this.eval(source.get_argument_list().get_argument(k)));
		}
		SadCallExpression result = SadFactory.
					call_expression(source.get_data_type(), function, list);
		
		if(this.scope != null) { 
			return this.scope.invocate(result); 
		}
		else {
			return result;
		}
	}
	
	/* multiple expressions */
	private SadExpression eval_arith_add(SadMultiExpression source) throws Exception {
		/* divide into constant::variables */
		List<SadExpression> operands = this.divide_eval_multi_expr(source);
		SadConstant constant = (SadConstant) operands.remove(0);
		
		/* construct the arithmetic addition result */
		if(operands.isEmpty()) {
			return constant;
		}
		else {
			if(!this.compare(constant.get_constant(), 0L)) {
				operands.add(constant);
			}
			
			if(operands.size() == 1) {
				return operands.get(0);
			}
			else {
				return SadFactory.arith_add(source.get_data_type(), operands);
			}
		}
	}
	private SadExpression eval_arith_mul(SadMultiExpression source) throws Exception {
		/* divide into constant::variables */
		List<SadExpression> operands = this.divide_eval_multi_expr(source);
		SadConstant constant = (SadConstant) operands.remove(0);
		
		/* construct the arithmetic multiplication */
		if(operands.isEmpty() || this.compare(constant.get_constant(), 0L)) {
			return constant;	/* determine as zero */
		}
		else {
			/* ignore the constant if it is 1 since 1 * any == any */
			if(!this.compare(constant.get_constant(), 1L)) {
				operands.add(constant);
			}
			
			if(operands.size() == 1) {
				return operands.get(0);
			}
			else {
				return SadFactory.arith_mul(source.get_data_type(), operands);
			}
		}
	}
	private SadExpression eval_bitws_and(SadMultiExpression source) throws Exception {
		/* divide into constant::variables */
		List<SadExpression> operands = this.divide_eval_multi_expr(source);
		SadConstant constant = (SadConstant) operands.remove(0);
		
		/* construct the bitwise and expression */
		if(operands.isEmpty() || this.compare(constant.get_constant(), 0L)) {
			return constant;	/* determine as zero */
		}
		else {
			if(!this.compare(constant.get_constant(), ~0L)) {
				operands.add(constant);
			}
			
			if(operands.size() == 1) {
				return operands.get(0);
			}
			else {
				return SadFactory.bitws_and(source.get_data_type(), operands);
			}
		}
	}
	private SadExpression eval_bitws_ior(SadMultiExpression source) throws Exception {
		/* divide into constant::variables */
		List<SadExpression> operands = this.divide_eval_multi_expr(source);
		SadConstant constant = (SadConstant) operands.remove(0);
		
		/* construct the bitwise or expression */
		if(operands.isEmpty() || this.compare(constant.get_constant(), ~1L)) {
			return constant;	/* masking all the bits as one in the code */
		}
		else {
			if(!this.compare(constant.get_constant(), 0L)) {
				operands.add(constant);
			}
			
			if(operands.size() == 1) {
				return operands.get(0);
			}
			else {
				return SadFactory.bitws_ior(source.get_data_type(), operands);
			}
		}
	}
	private SadExpression eval_bitws_xor(SadMultiExpression source) throws Exception {
		/* divide into constant::variables */
		List<SadExpression> operands = this.divide_eval_multi_expr(source);
		SadConstant constant = (SadConstant) operands.remove(0);
		
		/* construct the bitwise expression */
		if(operands.isEmpty()) {
			return constant;
		}
		else {
			if(!this.compare(constant.get_constant(), 0L)) {
				operands.add(constant);
			}
			
			if(operands.size() == 1) {
				return operands.get(0);
			}
			else {
				return SadFactory.bitws_ior(source.get_data_type(), operands);
			}
		}
	}
	private SadExpression eval_logic_and(SadMultiExpression source) throws Exception {
		/* divide into constant::variables */
		List<SadExpression> operands = this.divide_eval_multi_expr(source);
		SadConstant constant = (SadConstant) operands.remove(0);
		
		/* construct the bitwise expression */
		if(operands.isEmpty() || !this.cast_to_bool(constant.get_constant())) {
			return constant;	/* determine as false */
		}
		else if(operands.size() == 1) {
			return operands.get(0);
		}
		else {
			return SadFactory.logic_and(source.get_data_type(), operands);
		}
	}
	private SadExpression eval_logic_ior(SadMultiExpression source) throws Exception {
		/* divide into constant::variables */
		List<SadExpression> operands = this.divide_eval_multi_expr(source);
		SadConstant constant = (SadConstant) operands.remove(0);
		
		/* construct the bitwise expression */
		if(operands.isEmpty() || this.cast_to_bool(constant.get_constant())) {
			return constant;	/* determine as true */
		}
		else if(operands.size() == 1) {
			return operands.get(0);
		}
		else {
			return SadFactory.logic_ior(source.get_data_type(), operands);
		}
	}
	
	/* binary expression */
	private SadExpression eval_arith_sub(SadBinaryExpression source) throws Exception {
		SadExpression loperand = this.eval(source.get_loperand());
		SadExpression roperand = this.eval(source.get_roperand());
		if(loperand instanceof SadConstant && roperand instanceof SadConstant) {
			CConstant lvalue = ((SadConstant) loperand).get_constant();
			CConstant rvalue = ((SadConstant) roperand).get_constant();
			CConstant result = this.compute(COperator.arith_sub, lvalue, rvalue);
			return SadFactory.constant(result);
		}
		else {
			return SadFactory.arith_sub(source.get_data_type(), loperand, roperand);
		}
	}
	private SadExpression eval_arith_div(SadBinaryExpression source) throws Exception {
		SadExpression loperand = this.eval(source.get_loperand());
		SadExpression roperand = this.eval(source.get_roperand());
		if(loperand instanceof SadConstant && roperand instanceof SadConstant) {
			CConstant lvalue = ((SadConstant) loperand).get_constant();
			CConstant rvalue = ((SadConstant) roperand).get_constant();
			CConstant result = this.compute(COperator.arith_div, lvalue, rvalue);
			return SadFactory.constant(result);
		}
		else {
			if(loperand instanceof SadConstant) {
				if(this.compare(((SadConstant) loperand).get_constant(), 0L)) {
					return loperand;
				}
			}
			
			if(roperand instanceof SadConstant) {
				if(this.compare(((SadConstant) roperand).get_constant(), 1L)) {
					return loperand;
				}
				else if(this.compare(((SadConstant) roperand).get_constant(), -1L)) {
					return SadFactory.arith_neg(source.get_data_type(), loperand);
				}
			}
			
			return SadFactory.arith_div(source.get_data_type(), loperand, roperand);
		}
	}
	private SadExpression eval_arith_mod(SadBinaryExpression source) throws Exception {
		SadExpression loperand = this.eval(source.get_loperand());
		SadExpression roperand = this.eval(source.get_roperand());
		if(loperand instanceof SadConstant && roperand instanceof SadConstant) {
			CConstant lvalue = ((SadConstant) loperand).get_constant();
			CConstant rvalue = ((SadConstant) roperand).get_constant();
			CConstant result = this.compute(COperator.arith_mod, lvalue, rvalue);
			return SadFactory.constant(result);
		}
		else {
			if(roperand instanceof SadConstant) {
				if(this.compare(((SadConstant) roperand).get_constant(), 1L)
					|| this.compare(((SadConstant) roperand).get_constant(), 01)) {
					return SadFactory.constant(0L);
				}
			}
			
			if(loperand instanceof SadConstant) {
				if(this.compare(((SadConstant) loperand).get_constant(), 0L)) {
					return SadFactory.constant(0L);
				}
				else if(this.compare(((SadConstant) loperand).get_constant(), 1L)
						|| this.compare(((SadConstant) loperand).get_constant(), -1L)) {
					return SadFactory.constant(1L);
				}
			}
			
			return SadFactory.arith_mod(source.get_data_type(), loperand, roperand);
		}
	}
	private SadExpression eval_bitws_lsh(SadBinaryExpression source) throws Exception {
		SadExpression loperand = this.eval(source.get_loperand());
		SadExpression roperand = this.eval(source.get_roperand());
		if(loperand instanceof SadConstant && roperand instanceof SadConstant) {
			CConstant lvalue = ((SadConstant) loperand).get_constant();
			CConstant rvalue = ((SadConstant) roperand).get_constant();
			CConstant result = this.compute(COperator.left_shift, lvalue, rvalue);
			return SadFactory.constant(result);
		}
		else {
			if(roperand instanceof SadConstant) {
				if(this.compare(((SadConstant) roperand).get_constant(), 0L)) {
					return loperand;
				}
			}
			
			if(loperand instanceof SadConstant) {
				if(this.compare(((SadConstant) loperand).get_constant(), 0L)) {
					return loperand;
				}
			}
			
			return SadFactory.bitws_lsh(source.get_data_type(), loperand, roperand);
		}
	}
	private SadExpression eval_bitws_rsh(SadBinaryExpression source) throws Exception {
		SadExpression loperand = this.eval(source.get_loperand());
		SadExpression roperand = this.eval(source.get_roperand());
		if(loperand instanceof SadConstant && roperand instanceof SadConstant) {
			CConstant lvalue = ((SadConstant) loperand).get_constant();
			CConstant rvalue = ((SadConstant) roperand).get_constant();
			CConstant result = this.compute(COperator.righ_shift, lvalue, rvalue);
			return SadFactory.constant(result);
		}
		else {
			if(roperand instanceof SadConstant) {
				if(this.compare(((SadConstant) roperand).get_constant(), 0L)) {
					return loperand;
				}
			}
			
			if(loperand instanceof SadConstant) {
				if(this.compare(((SadConstant) loperand).get_constant(), 0L)) {
					return loperand;
				}
			}
			
			return SadFactory.bitws_rsh(source.get_data_type(), loperand, roperand);
		}
	}
	private SadExpression eval_greater_tn(SadBinaryExpression source) throws Exception {
		SadExpression loperand = this.eval(source.get_loperand());
		SadExpression roperand = this.eval(source.get_roperand());
		if(loperand instanceof SadConstant && roperand instanceof SadConstant) {
			CConstant lvalue = ((SadConstant) loperand).get_constant();
			CConstant rvalue = ((SadConstant) roperand).get_constant();
			CConstant result = this.compute(source.
						get_operator().get_operator(), lvalue, rvalue);
			return SadFactory.constant(result);
		}
		else {
			return SadFactory.smaller_tn(source.get_data_type(), roperand, loperand);
		}
	}
	private SadExpression eval_greater_eq(SadBinaryExpression source) throws Exception {
		SadExpression loperand = this.eval(source.get_loperand());
		SadExpression roperand = this.eval(source.get_roperand());
		if(loperand instanceof SadConstant && roperand instanceof SadConstant) {
			CConstant lvalue = ((SadConstant) loperand).get_constant();
			CConstant rvalue = ((SadConstant) roperand).get_constant();
			CConstant result = this.compute(source.
						get_operator().get_operator(), lvalue, rvalue);
			return SadFactory.constant(result);
		}
		else {
			return SadFactory.smaller_eq(source.get_data_type(), roperand, loperand);
		}
	}
	private SadExpression eval_smaller_tn(SadBinaryExpression source) throws Exception {
		SadExpression loperand = this.eval(source.get_loperand());
		SadExpression roperand = this.eval(source.get_roperand());
		if(loperand instanceof SadConstant && roperand instanceof SadConstant) {
			CConstant lvalue = ((SadConstant) loperand).get_constant();
			CConstant rvalue = ((SadConstant) roperand).get_constant();
			CConstant result = this.compute(source.
						get_operator().get_operator(), lvalue, rvalue);
			return SadFactory.constant(result);
		}
		else {
			return SadFactory.smaller_tn(source.get_data_type(), loperand, roperand);
		}
	}
	private SadExpression eval_smaller_eq(SadBinaryExpression source) throws Exception {
		SadExpression loperand = this.eval(source.get_loperand());
		SadExpression roperand = this.eval(source.get_roperand());
		if(loperand instanceof SadConstant && roperand instanceof SadConstant) {
			CConstant lvalue = ((SadConstant) loperand).get_constant();
			CConstant rvalue = ((SadConstant) roperand).get_constant();
			CConstant result = this.compute(source.
						get_operator().get_operator(), lvalue, rvalue);
			return SadFactory.constant(result);
		}
		else {
			return SadFactory.smaller_eq(source.get_data_type(), loperand, roperand);
		}
	}
	private SadExpression eval_equal_with(SadBinaryExpression source) throws Exception {
		SadExpression loperand = this.eval(source.get_loperand());
		SadExpression roperand = this.eval(source.get_roperand());
		if(loperand instanceof SadConstant && roperand instanceof SadConstant) {
			CConstant lvalue = ((SadConstant) loperand).get_constant();
			CConstant rvalue = ((SadConstant) roperand).get_constant();
			CConstant result = this.compute(source.
						get_operator().get_operator(), lvalue, rvalue);
			return SadFactory.constant(result);
		}
		else {
			return SadFactory.equal_with(source.get_data_type(), loperand, roperand);
		}
	}
	private SadExpression eval_not_equals(SadBinaryExpression source) throws Exception {
		SadExpression loperand = this.eval(source.get_loperand());
		SadExpression roperand = this.eval(source.get_roperand());
		if(loperand instanceof SadConstant && roperand instanceof SadConstant) {
			CConstant lvalue = ((SadConstant) loperand).get_constant();
			CConstant rvalue = ((SadConstant) roperand).get_constant();
			CConstant result = this.compute(source.
						get_operator().get_operator(), lvalue, rvalue);
			return SadFactory.constant(result);
		}
		else {
			return SadFactory.not_equals(source.get_data_type(), loperand, roperand);
		}
	}
	
	/* evaluation methods */
	/**
	 * @param source
	 * @param scope
	 * @return evaluate the symbolic expression within the scope
	 * @throws Exception
	 */
	public static SadExpression evaluate(SadExpression source, SadCallScope scope) throws Exception {
		evaluator.scope = scope;
		return evaluator.eval(source);
	}
	/**
	 * @param source
	 * @return evaluate the symbolic expression without contextual information
	 * @throws Exception
	 */
	public static SadExpression evaluate(SadExpression source) throws Exception {
		return evaluate(source, null);
	}
	
}
