package com.jcsa.jcparse.lang.sym;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.test.state.CStateContexts;

/**
 * It is used to evaluate the value of symbolic expression in a given context.
 * 
 * @author yukimula
 *
 */
public class SymEvaluator {
	
	/* definition */
	private CStateContexts context;
	private SymEvaluator() { }
	private static SymEvaluator evaluator = new SymEvaluator();
	
	/* evaluation methods */
	/**
	 * @param source
	 * @return get the solution by source, source.get_source & source.generate_code()
	 * @throws Exception
	 */
	private SymExpression get_solution(SymExpression source) throws Exception {
		if(context == null) {
			return null;
		}
		else if(this.context.has(source)) {
			return this.context.get(source);
		}
		else {
			return null;
		}
	}
	/**
	 * evaluate the result of the symbolic expression
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private SymExpression evaluate(SymExpression source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else {
			SymExpression result = this.get_solution(source);
			if(result != null) {
				return result;
			}
			else {
				SymExpression target;
				if(source instanceof SymBasicExpression) {
					target = this.eval_basic_expression((SymBasicExpression) source);
				}
				else if(source instanceof SymBinaryExpression) {
					target = this.eval_binary_expression((SymBinaryExpression) source);
				}
				else if(source instanceof SymUnaryExpression) {
					target = this.eval_unary_expression((SymUnaryExpression) source);
				}
				else if(source instanceof SymCallExpression) {
					target = this.eval_call_expression((SymCallExpression) source);
				}
				else if(source instanceof SymFieldExpression) {
					target = this.eval_field_expression((SymFieldExpression) source);
				}
				else if(source instanceof SymInitializerList) {
					target = this.eval_initializer_list((SymInitializerList) source);
				}
				else {
					throw new IllegalArgumentException(source.generate_code());
				}
				target.set_source(source.get_source());
				return target;
			}
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return the result evaluated from the evaluator.
	 * @throws Exception
	 */
	public static SymExpression evaluate_on(SymExpression source, CStateContexts contexts) throws Exception {
		evaluator.context = contexts;
		return evaluator.evaluate(source);
	}
	/**
	 * @param source
	 * @param context
	 * @return the result evaluated from the evaluator without contextual information
	 * @throws Exception
	 */
	public static SymExpression evaluate_on(SymExpression source) throws Exception {
		evaluator.context = null;
		return evaluator.evaluate(source);
	}
	
	/* implementation methods */
	private SymExpression eval_basic_expression(SymBasicExpression source) throws Exception {
		return (SymExpression) source.clone();
	}
	private SymExpression eval_call_expression(SymCallExpression source) throws Exception {
		SymExpression function = this.evaluate(source.get_function());
		List<Object> arguments = new ArrayList<Object>();
		SymArgumentList alist = source.get_argument_list();
		for(int k = 0; k < alist.number_of_arguments(); k++) {
			arguments.add(this.evaluate(alist.get_argument(k)));
		}
		SymCallExpression target = SymFactory.call_expression(
				source.get_data_type(), function, arguments);
		if(this.context != null)
			return this.context.invocate(target);
		else
			return target;
	}
	private SymExpression eval_field_expression(SymFieldExpression source) throws Exception {
		SymExpression body = this.evaluate(source.get_body());
		String field = source.get_field().get_name();
		return SymFactory.field_expression(
				source.get_data_type(), body, field);
	}
	private SymExpression eval_initializer_list(SymInitializerList source) throws Exception {
		List<Object> elements = new ArrayList<Object>();
		for(int k = 0; k < source.number_of_elements(); k++) {
			elements.add(this.evaluate(source.get_element(k)));
		}
		return SymFactory.initializer_list(source.get_data_type(), elements);
	}
	private SymExpression eval_binary_expression(SymBinaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case arith_add:
		case arith_sub:		return this.eval_arith_add(source);
		case arith_mul:
		case arith_div:		return this.eval_arith_mul(source);
		case arith_mod:		return this.eval_arith_mod(source);
		case bit_and:		return this.eval_bitws_and(source);
		case bit_or:		return this.eval_bitws_ior(source);
		case bit_xor:		return this.eval_bitws_xor(source);
		case logic_and:		return this.eval_logic_and(source);
		case logic_or:		return this.eval_logic_ior(source);
		case left_shift:	return this.eval_bitws_lsh(source);
		case righ_shift:	return this.eval_bitws_rsh(source);
		case greater_tn:	return this.eval_greater_tn(source);
		case greater_eq:	return this.eval_greater_eq(source);
		case smaller_tn:	return this.eval_smaller_tn(source);
		case smaller_eq:	return this.eval_smaller_eq(source);
		case equal_with:	return this.eval_equal_with(source);
		case not_equals:	return this.eval_not_equals(source);
		default: throw new IllegalArgumentException(source.generate_code());
		}
	}
	private SymExpression eval_unary_expression(SymUnaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case negative:		return this.eval_arith_neg(source);
		case bit_not:		return this.eval_bitws_rsv(source);
		case logic_not:		return this.eval_logic_not(source);
		case address_of:	return this.eval_address_of(source);
		case dereference:	return this.eval_dereference(source);
		case assign:		return this.eval_type_cast(source);
		default: throw new IllegalArgumentException(source.generate_code());
		}
	}
	
	/* unary expression parts */
	private SymExpression eval_arith_neg(SymUnaryExpression source) throws Exception {
		SymExpression operand = this.evaluate(source.get_operand());
		CType data_type = source.get_data_type();
		if(operand instanceof SymConstant) {
			return SymComputation.arith_neg((SymConstant) operand);
		}
		else if(operand instanceof SymUnaryExpression) {
			COperator operator = 
					((SymUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.negative) {
				return ((SymUnaryExpression) operand).get_operand();
			}
			else {
				return SymFactory.arith_neg(data_type, operand);
			}
		}
		else if(operand instanceof SymBinaryExpression) {
			COperator operator = 
					((SymBinaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.arith_sub) {
				return SymFactory.arith_sub(data_type, 
						((SymBinaryExpression) operand).get_roperand(), 
						((SymBinaryExpression) operand).get_loperand());
			}
			else {
				return SymFactory.arith_neg(data_type, operand);
			}
		}
		else {
			return SymFactory.arith_neg(data_type, operand);
		}
	}
	private SymExpression eval_bitws_rsv(SymUnaryExpression source) throws Exception {
		CType data_type = source.get_data_type();
		SymExpression operand = this.evaluate(source.get_operand());
		
		if(operand instanceof SymConstant) {
			return SymComputation.bitws_rsv((SymConstant) operand);
		}
		else if(operand instanceof SymUnaryExpression) {
			COperator operator = ((SymUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.bit_not) {
				return ((SymUnaryExpression) operand).get_operand();
			}
			else {
				return SymFactory.bitws_rsv(data_type, operand);
			}
		}
		else {
			return SymFactory.bitws_rsv(data_type, operand);
		}
	}
	private SymExpression eval_logic_not(SymUnaryExpression source) throws Exception {
		SymExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SymConstant) {
			return SymComputation.logic_not((SymConstant) operand);
		}
		else if(operand instanceof SymUnaryExpression) {
			COperator operator = 
					((SymUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.logic_not) {
				return ((SymUnaryExpression) operand).get_operand();
			}
			else {
				return SymFactory.logic_not(operand);
			}
		}
		else {
			return SymFactory.logic_not(operand);
		}
	}
	private SymExpression eval_address_of(SymUnaryExpression source) throws Exception {
		SymExpression operand = this.evaluate(source.get_operand());
		CType data_type = source.get_data_type();
		if(operand instanceof SymUnaryExpression) {
			COperator operator = 
					((SymUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.dereference) {
				return ((SymUnaryExpression) operand).get_operand();
			}
			else {
				return SymFactory.address_of(data_type, operand);
			}
		}
		else {
			return SymFactory.address_of(data_type, operand);
		}
	}
	private SymExpression eval_dereference(SymUnaryExpression source) throws Exception {
		SymExpression operand = this.evaluate(source.get_operand());
		CType data_type = source.get_data_type();
		if(operand instanceof SymUnaryExpression) {
			COperator operator = 
					((SymUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.address_of) {
				return ((SymUnaryExpression) operand).get_operand();
			}
			else {
				return SymFactory.dereference(data_type, operand);
			}
		}
		else {
			return SymFactory.dereference(data_type, operand);
		}
	}
	private SymExpression eval_type_cast(SymUnaryExpression source) throws Exception {
		SymExpression operand = this.evaluate(source.get_operand());
		CType data_type = source.get_data_type();
		if(operand instanceof SymConstant) {
			CConstant constant = new CConstant();
			if(data_type instanceof CBasicType) {
				switch(((CBasicType) data_type).get_tag()) {
				case c_bool:	constant.set_bool(((SymConstant) operand).get_bool()); break;
				case c_char:	
				case c_uchar:	constant.set_char(((SymConstant) operand).get_char()); break;
				case c_short:
				case c_ushort:	constant.set_int(((SymConstant) operand).get_short()); break;
				case c_int:
				case c_uint:	constant.set_int(((SymConstant) operand).get_int()); break;
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:	constant.set_long(((SymConstant) operand).get_long()); break;
				case c_float:	constant.set_float(((SymConstant) operand).get_float()); break;
				case c_double:
				case c_ldouble:	constant.set_double(((SymConstant) operand).get_double()); break;
				default: throw new IllegalArgumentException(data_type.generate_code());
				}
			}
			else if(data_type instanceof CArrayType
					|| data_type instanceof CPointerType) {
				constant.set_long(((SymConstant) operand).get_long());
			}
			else if(data_type instanceof CEnumType) {
				constant.set_int(((SymConstant) operand).get_int());
			}
			else {
				throw new IllegalArgumentException(data_type.generate_code());
			}
			return SymFactory.new_constant(constant);
		}
		else {
			return SymFactory.type_cast(data_type, operand);
		}
	}
	
	/* arith add and arith sub */
	/**
	 * collect the operands in left and right-side for the given source
	 * @param source
	 * @param loperands
	 * @param roperands
	 * @throws Exception
	 */
	private void get_operands_in_as(SymExpression source, 
			Collection<SymExpression> loperands, 
			Collection<SymExpression> roperands) throws Exception {
		if(source instanceof SymBinaryExpression) {
			COperator operator = 
					((SymBinaryExpression) source).get_operator().get_operator();
			if(operator == COperator.arith_add) {
				this.get_operands_in_as(((SymBinaryExpression) source).get_loperand(), loperands, roperands);
				this.get_operands_in_as(((SymBinaryExpression) source).get_roperand(), loperands, roperands);
			}
			else if(operator == COperator.arith_sub) {
				this.get_operands_in_as(((SymBinaryExpression) source).get_loperand(), loperands, roperands);
				this.get_operands_in_as(((SymBinaryExpression) source).get_roperand(), roperands, loperands);
			}
			else {
				loperands.add(source);
			}
		}
		else if(source instanceof SymUnaryExpression) {
			COperator operator = 
					((SymUnaryExpression) source).get_operator().get_operator();
			if(operator == COperator.negative) {
				this.get_operands_in_as(((SymUnaryExpression) source).get_operand(), roperands, loperands);
			}
			else {
				loperands.add(source);
			}
		}
		else {
			loperands.add(source);
		}
	}
	/**
	 * [var1, var2, ..., varN, const]
	 * @param operands
	 * @throws Exception
	 */
	private List<SymExpression> sov_operands_in_as(List<SymExpression> operands) throws Exception {
		List<SymExpression> new_operands = new ArrayList<SymExpression>();
		SymConstant constant = SymFactory.new_constant(Integer.valueOf(0));
		for(SymExpression operand : operands) {
			SymExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SymConstant) {
				constant = SymComputation.arith_add(
						constant, (SymConstant) new_operand);
			}
			else {
				new_operands.add(new_operand);
			}
		}
		new_operands.add(constant);
		return new_operands;
	}
	/**
	 * remove the duplicated operands in left and right since EXX - EXX = 0
	 * @param loperands
	 * @param roperands
	 * @throws Exception
	 */
	private void rem_operands_in_as(List<SymExpression> loperands, List<SymExpression> roperands) throws Exception {
		Set<SymExpression> lremoves = new HashSet<SymExpression>();
		Set<SymExpression> rremoves = new HashSet<SymExpression>();
		for(SymExpression loperand : loperands) {
			String x = loperand.generate_code();
			for(SymExpression roperand : roperands) {
				if(!rremoves.contains(roperand)) {
					String y = roperand.generate_code();
					if(y.equals(x)) {
						lremoves.add(loperand);
						rremoves.add(roperand);
					}
				}
			}
		}
		for(SymExpression loperand : lremoves) loperands.remove(loperand);
		for(SymExpression roperand : rremoves) roperands.remove(roperand);
	}
	/**
	 * @param operands
	 * @return x1 + x2 + ... + xN
	 * @throws Exception
	 */
	private SymExpression acc_operands_in_as(CType type, List<SymExpression> operands) throws Exception {
		SymExpression expression = null;
		for(SymExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = SymFactory.arith_add(type, expression, operand);
			}
		}
		return expression;	/* null if the operands are of empty */
	}
	private SymExpression com_operands_in_as(CType type, SymExpression loperand, SymExpression roperand) throws Exception {
		if(loperand == null) {
			if(roperand == null) {
				return SymFactory.new_constant(Long.valueOf(0));
			}
			else {
				return SymFactory.arith_neg(type, roperand);
			}
		}
		else {
			if(roperand == null) {
				return loperand;
			}
			else {
				return SymFactory.arith_sub(type, loperand, roperand);
			}
		}
	}
	private SymExpression eval_arith_add(SymBinaryExpression source) throws Exception {
		/* declarations */
		CType type = source.get_data_type();
		List<SymExpression> loperands = new ArrayList<SymExpression>();
		List<SymExpression> roperands = new ArrayList<SymExpression>();
		
		/* 1. divide operands into left and right groups */
		this.get_operands_in_as(source, loperands, roperands);
		
		/* 2. solve the operands and rebuild operand groups */
		loperands = this.sov_operands_in_as(loperands);
		roperands = this.sov_operands_in_as(roperands);
		
		/* 3. composite both constants in left and right */
		SymExpression lconstant = loperands.remove(loperands.size() - 1);
		SymExpression rconstant = roperands.remove(roperands.size() - 1);
		SymConstant constant = SymComputation.arith_sub(
					(SymConstant) lconstant, (SymConstant) rconstant);
		if(!constant.compare(0)) { loperands.add(constant); }
		
		/* 4. remove duplicated numbers in left and right */
		this.rem_operands_in_as(loperands, roperands);
		SymExpression loperand = this.acc_operands_in_as(type, loperands);
		SymExpression roperand = this.acc_operands_in_as(type, roperands);
		
		/* 5. construct the arithmetic result expression */
		return this.com_operands_in_as(type, loperand, roperand);
	}
	
	/* arith mul and arith div */
	/**
	 * divides the operands in source into left and right groups
	 * @param source
	 * @param loperands
	 * @param roperands
	 * @throws Exception
	 */
	private void get_operands_in_md(SymExpression source,
			Collection<SymExpression> loperands, 
			Collection<SymExpression> roperands) throws Exception {
		if(source instanceof SymUnaryExpression) {
			COperator operator = ((SymUnaryExpression) 
					source).get_operator().get_operator();
			if(operator == COperator.negative) {
				loperands.add(((SymUnaryExpression) source).get_operand());
				roperands.add(SymFactory.new_constant(Long.valueOf(-1L)));
			}
			else {
				loperands.add(source);
			}
		}
		else if(source instanceof SymBinaryExpression) {
			COperator operator = ((SymBinaryExpression) 
					source).get_operator().get_operator();
			if(operator == COperator.arith_mul) {
				this.get_operands_in_md(((SymBinaryExpression) source).get_loperand(), loperands, roperands);
				this.get_operands_in_md(((SymBinaryExpression) source).get_roperand(), loperands, roperands);
			}
			else if(operator == COperator.arith_div) {
				this.get_operands_in_md(((SymBinaryExpression) source).get_loperand(), loperands, roperands);
				this.get_operands_in_md(((SymBinaryExpression) source).get_roperand(), roperands, loperands);
			}
			else {
				loperands.add(source);
			}
		}
		else {
			loperands.add(source);
		}
	}
	/**
	 * @param operands
	 * @return solve the operands in list and get constant
	 * @throws Exception
	 */
	private List<SymExpression> sov_operands_in_md(List<SymExpression> operands) throws Exception {
		List<SymExpression> new_operands = new ArrayList<SymExpression>();
		SymConstant constant = SymFactory.new_constant(Long.valueOf(1L));
		for(SymExpression operand : operands) {
			SymExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SymConstant) {
				constant = SymComputation.arith_mul(
						constant, (SymConstant) new_operand);
			}
			else {
				new_operands.add(new_operand);
			}
		}
		new_operands.add(constant);
		return new_operands;
	}
	/**
	 * @param x
	 * @param y
	 * @return greatest common divisor
	 */
	private long gcd(long x, long y) {
		long quotient, remainder, temp;
		x = Math.abs(x);
		y = Math.abs(y);
		
		if(x > y) {
			quotient = x;
			remainder = y;
		}
		else if(x < y) {
			quotient = y;
			remainder = x;
		}
		else {
			return x;
		}
		
		while(remainder > 0) {
			temp = remainder;
			remainder = quotient % remainder;
			quotient = temp;
		}
		
		return quotient;
	}
	/**
	 * @param type
	 * @param lconstant
	 * @param rconstant
	 * @return 
	 * @throws Exception
	 */
	private SymConstant[] get_constant_in_md(CType type, SymConstant lconstant, SymConstant rconstant) throws Exception {
		type = CTypeAnalyzer.get_value_type(type);
		if(CTypeAnalyzer.is_boolean(type) || CTypeAnalyzer.is_integer(type) || CTypeAnalyzer.is_pointer(type)) {
			long x = lconstant.get_long();
			long y = rconstant.get_long();
			/* compute the greatest common divisor */
			long gcd = this.gcd(x, y);
			x = x / gcd; y = y / gcd;
			if(y < 0) { y = -y; x = -x; }
			return new SymConstant[] { 
				SymFactory.new_constant(Long.valueOf(x)),
				SymFactory.new_constant(Long.valueOf(y))
			};
		}
		else if(CTypeAnalyzer.is_real(type)) {
			double x = lconstant.get_double();
			double y = rconstant.get_double();
			return new SymConstant[] {
				SymFactory.new_constant(Double.valueOf(x / y)),
				SymFactory.new_constant(Long.valueOf(1L))
			};
		}
		else {
			throw new IllegalArgumentException("Invalid: " + type.generate_code());
		}
	}
	/**
	 * remove the duplicated operands in left and right operands in division
	 * @param loperands
	 * @param roperands
	 * @throws Exception
	 */
	private void rem_operands_in_md(List<SymExpression> loperands, List<SymExpression> roperands) throws Exception {
		Set<SymExpression> lremoves = new HashSet<SymExpression>();
		Set<SymExpression> rremoves = new HashSet<SymExpression>();
		for(SymExpression loperand : loperands) {
			String x = loperand.generate_code();
			for(SymExpression roperand : roperands) {
				if(!rremoves.contains(roperand)) {
					String y = roperand.generate_code();
					if(y.equals(x)) {
						lremoves.add(loperand);
						rremoves.add(roperand);
					}
				}
			}
		}
		for(SymExpression loperand : lremoves) loperands.remove(loperand);
		for(SymExpression roperand : rremoves) roperands.remove(roperand);
	}
	/**
	 * @param operands
	 * @return x1 + x2 + ... + xN
	 * @throws Exception
	 */
	private SymExpression acc_operands_in_md(CType type, List<SymExpression> operands) throws Exception {
		SymExpression expression = null;
		for(SymExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = SymFactory.arith_mul(type, expression, operand);
			}
		}
		return expression;	/* null if the operands are of empty */
	}
	private SymExpression com_operands_in_md(CType type, SymExpression loperand, SymExpression roperand) throws Exception {
		if(loperand == null) {
			if(roperand == null) {
				return SymFactory.new_constant(Long.valueOf(1));
			}
			else {
				return SymFactory.arith_div(type, SymFactory.new_constant(Long.valueOf(1)), roperand);
			}
		}
		else {
			if(roperand == null) {
				return loperand;
			}
			else {
				return SymFactory.arith_div(type, loperand, roperand);
			}
		}
	}
	private SymExpression eval_arith_mul(SymBinaryExpression source) throws Exception {
		/* declarations */
		CType type = source.get_data_type();
		List<SymExpression> loperands = new ArrayList<SymExpression>();
		List<SymExpression> roperands = new ArrayList<SymExpression>();
		
		/* 1. divide the operands into left and right group */
		this.get_operands_in_md(source, loperands, roperands);
		
		/* 2. solve the operands in the left and right group */
		loperands = this.sov_operands_in_md(loperands);
		roperands = this.sov_operands_in_md(roperands);
		
		/* 3. obtain the constant parts in the both groups */
		SymConstant lconstant = (SymConstant) loperands.remove(loperands.size() - 1);
		SymConstant rconstant = (SymConstant) roperands.remove(roperands.size() - 1);
		SymConstant[] constants = this.get_constant_in_md(type, lconstant, rconstant);
		lconstant = constants[0]; rconstant = constants[1];
		if(lconstant.compare(0)) { return SymFactory.new_constant(0L); }
		if(!lconstant.compare(1)) { loperands.add(lconstant); }
		if(!rconstant.compare(1)) { roperands.add(rconstant); }
		
		/* 4. remove the duplicated operands in left & right */
		this.rem_operands_in_md(loperands, roperands);
		SymExpression loperand = this.acc_operands_in_md(type, loperands);
		SymExpression roperand = this.acc_operands_in_md(type, roperands);
		
		/* 5. generate the arithmetic division composition */
		return this.com_operands_in_md(type, loperand, roperand);
	}
	
	/* arithmetic MOD */
	private SymExpression eval_arith_mod(SymBinaryExpression source) throws Exception {
		/* 1. getters */
		CType data_type = source.get_data_type();
		SymExpression loperand = this.evaluate(source.get_loperand());
		SymExpression roperand = this.evaluate(source.get_roperand());
		
		/* 2. partial evaluation */
		if(loperand instanceof SymConstant) {
			SymConstant lconstant = (SymConstant) loperand;
			if(roperand instanceof SymConstant) {
				SymConstant rconstant = (SymConstant) roperand;
				return SymComputation.arith_mod(lconstant, rconstant);
			}
			else {
				if(lconstant.compare(0) || lconstant.compare(1)) {
					return lconstant;
				}
				else {
					return SymFactory.arith_mod(data_type, loperand, roperand);
				}
			}
		}
		else {
			if(roperand instanceof SymConstant) {
				SymConstant rconstant = (SymConstant) roperand;
				if(rconstant.compare(1) || rconstant.compare(-1)) {
					return SymFactory.new_constant(Long.valueOf(0));
				}
				else {
					return SymFactory.arith_mod(data_type, loperand, roperand);
				}
			}
			else {
				return SymFactory.arith_mod(data_type, loperand, roperand);
			}
		}
	}
	
	/* bitwise operations for &, |, ^, &&, || */
	private void get_operands_in_xx(SymExpression source, List<SymExpression> operands, COperator operator) throws Exception {
		if(source instanceof SymBinaryExpression) {
			COperator operator2 = ((SymBinaryExpression) source).get_operator().get_operator();
			if(operator == operator2) {
				this.get_operands_in_xx(((SymBinaryExpression) source).get_loperand(), operands, operator);
				this.get_operands_in_xx(((SymBinaryExpression) source).get_roperand(), operands, operator);
			}
			else {
				operands.add(source);
			}
		}
		else {
			operands.add(source);
		}
	}
	private List<SymExpression> sov_operands_in_xx(List<SymExpression> operands, SymConstant constant, COperator operator) throws Exception {
		List<SymExpression> new_operands = new ArrayList<SymExpression>();
		for(SymExpression operand : operands) {
			SymExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SymConstant) {
				switch(operator) {
				case bit_and:	constant = SymComputation.bitws_and(constant, (SymConstant) new_operand); break;
				case bit_or:	constant = SymComputation.bitws_ior(constant, (SymConstant) new_operand); break;
				case bit_xor:	constant = SymComputation.bitws_xor(constant, (SymConstant) new_operand); break;
				case logic_and:	constant = SymComputation.logic_and(constant, (SymConstant) new_operand); break;
				case logic_or:	constant = SymComputation.logic_ior(constant, (SymConstant) new_operand); break;
				default: throw new IllegalArgumentException("Invalid: " + operator);
				}
			}
			else {
				new_operands.add(new_operand);
			}
		}
		new_operands.add(constant);
		return new_operands;
	}
	private SymExpression acc_operands_in_xx(CType data_type, COperator operator, List<SymExpression> operands) throws Exception {
		SymExpression expression = null;
		for(SymExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				switch(operator) {
				case bit_and:	expression = SymFactory.bitws_and(data_type, expression, operand); break;
				case bit_or:	expression = SymFactory.bitws_ior(data_type, expression, operand); break;
				case bit_xor:	expression = SymFactory.bitws_xor(data_type, expression, operand); break;
				case logic_and:	expression = SymFactory.logic_and(expression, operand); break;
				case logic_or:	expression = SymFactory.logic_ior(expression, operand); break;
				default: throw new IllegalArgumentException("Invalid: " + operator);
				}
			}
		}
		return expression;
	}
	private SymExpression eval_bitws_and(SymBinaryExpression source) throws Exception {
		/* getters */
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		List<SymExpression> operands = new ArrayList<SymExpression>();
		COperator operator = source.get_operator().get_operator();
		
		/* obtain solved operands using initial constant */
		SymConstant constant = SymFactory.new_constant(Long.valueOf(~0L));
		this.get_operands_in_xx(source, operands, operator);
		operands = this.sov_operands_in_xx(operands, constant, operator);
		
		/* rebuild constant and partial evaluation */
		constant = (SymConstant) operands.remove(operands.size() - 1);
		if(constant.compare(0L)) return SymFactory.new_constant(Long.valueOf(0L)); 
		else if(!constant.compare(~0L) || operands.isEmpty()) operands.add(constant);
		
		return this.acc_operands_in_xx(data_type, operator, operands);
	}
	private SymExpression eval_bitws_ior(SymBinaryExpression source) throws Exception {
		/* getters */
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		List<SymExpression> operands = new ArrayList<SymExpression>();
		COperator operator = source.get_operator().get_operator();
		
		/* obtain solved operands using initial constant */
		SymConstant constant = SymFactory.new_constant(Long.valueOf(0L));
		this.get_operands_in_xx(source, operands, operator);
		operands = this.sov_operands_in_xx(operands, constant, operator);
		
		/* rebuild constant and partial evaluation */
		constant = (SymConstant) operands.remove(operands.size() - 1);
		if(constant.compare(~0L)) return SymFactory.new_constant(Long.valueOf(~0L)); 
		else if(!constant.compare(0L) || operands.isEmpty()) operands.add(constant);
		
		return this.acc_operands_in_xx(data_type, operator, operands);
	}
	private SymExpression eval_bitws_xor(SymBinaryExpression source) throws Exception {
		/* getters */
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		List<SymExpression> operands = new ArrayList<SymExpression>();
		COperator operator = source.get_operator().get_operator();
		
		/* obtain solved operands using initial constant */
		SymConstant constant = SymFactory.new_constant(Long.valueOf(0L));
		this.get_operands_in_xx(source, operands, operator);
		operands = this.sov_operands_in_xx(operands, constant, operator);
		
		/* rebuild constant and partial evaluation */
		constant = (SymConstant) operands.remove(operands.size() - 1);
		if(!constant.compare(0L) || operands.isEmpty()) operands.add(constant);
		
		return this.acc_operands_in_xx(data_type, operator, operands);
	}
	private SymExpression eval_logic_and(SymBinaryExpression source) throws Exception {
		/* getters */
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		List<SymExpression> operands = new ArrayList<SymExpression>();
		COperator operator = source.get_operator().get_operator();
		
		/* obtain solved operands using initial constant */
		SymConstant constant = SymFactory.new_constant(Boolean.TRUE);
		this.get_operands_in_xx(source, operands, operator);
		operands = this.sov_operands_in_xx(operands, constant, operator);
		
		/* rebuild constant and partial evaluation */
		constant = (SymConstant) operands.remove(operands.size() - 1);
		if(!constant.get_bool()) return SymFactory.new_constant(Boolean.FALSE); 
		if(operands.isEmpty()) operands.add(constant);
		
		return this.acc_operands_in_xx(data_type, operator, operands);
	}
	private SymExpression eval_logic_ior(SymBinaryExpression source) throws Exception {
		/* getters */
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		List<SymExpression> operands = new ArrayList<SymExpression>();
		COperator operator = source.get_operator().get_operator();
		
		/* obtain solved operands using initial constant */
		SymConstant constant = SymFactory.new_constant(Boolean.FALSE);
		this.get_operands_in_xx(source, operands, operator);
		operands = this.sov_operands_in_xx(operands, constant, operator);
		
		/* rebuild constant and partial evaluation */
		constant = (SymConstant) operands.remove(operands.size() - 1);
		if(constant.get_bool()) return SymFactory.new_constant(Boolean.TRUE); 
		if(operands.isEmpty()) operands.add(constant);
		
		return this.acc_operands_in_xx(data_type, operator, operands);
	}
	
	/* <<, >>, <, <=, >, >=, ==, != */
	private SymExpression eval_bitws_lsh(SymBinaryExpression source) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		SymExpression loperand = this.evaluate(source.get_loperand());
		SymExpression roperand = this.evaluate(source.get_roperand());
		if(loperand instanceof SymConstant) {
			SymConstant lconstant = (SymConstant) loperand;
			if(roperand instanceof SymConstant) {
				SymConstant rconstant = (SymConstant) roperand;
				return SymComputation.bitws_lsh(lconstant, rconstant);
			}
			else {
				if(lconstant.compare(0)) {
					return loperand;
				}
				else {
					return SymFactory.bitws_lsh(data_type, loperand, roperand);
				}
			}
		}
		else {
			if(roperand instanceof SymConstant) {
				SymConstant rconstant = (SymConstant) roperand;
				if(rconstant.compare(0)) {
					return loperand;
				}
				else {
					return SymFactory.bitws_lsh(data_type, loperand, roperand);
				}
			}
			else {
				return SymFactory.bitws_lsh(data_type, loperand, roperand);
			}
		}
	}
	private SymExpression eval_bitws_rsh(SymBinaryExpression source) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		SymExpression loperand = this.evaluate(source.get_loperand());
		SymExpression roperand = this.evaluate(source.get_roperand());
		if(loperand instanceof SymConstant) {
			SymConstant lconstant = (SymConstant) loperand;
			if(roperand instanceof SymConstant) {
				SymConstant rconstant = (SymConstant) roperand;
				return SymComputation.bitws_rsh(lconstant, rconstant);
			}
			else {
				if(lconstant.compare(0)) {
					return loperand;
				}
				else {
					return SymFactory.bitws_rsh(data_type, loperand, roperand);
				}
			}
		}
		else {
			if(roperand instanceof SymConstant) {
				SymConstant rconstant = (SymConstant) roperand;
				if(rconstant.compare(0)) {
					return loperand;
				}
				else {
					return SymFactory.bitws_rsh(data_type, loperand, roperand);
				}
			}
			else {
				return SymFactory.bitws_rsh(data_type, loperand, roperand);
			}
		}
	}
	private SymExpression eval_greater_tn(SymBinaryExpression source) throws Exception {
		SymExpression loperand = this.evaluate(source.get_loperand());
		SymExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			SymConstant lconstant = (SymConstant) loperand;
			if(roperand instanceof SymConstant) {
				SymConstant rconstant = (SymConstant) roperand;
				return SymComputation.greater_tn(lconstant, rconstant);
			}
		}
		
		return SymFactory.smaller_tn(roperand, loperand);
	}
	private SymExpression eval_greater_eq(SymBinaryExpression source) throws Exception {
		SymExpression loperand = this.evaluate(source.get_loperand());
		SymExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			SymConstant lconstant = (SymConstant) loperand;
			if(roperand instanceof SymConstant) {
				SymConstant rconstant = (SymConstant) roperand;
				return SymComputation.greater_eq(lconstant, rconstant);
			}
		}
		
		return SymFactory.smaller_eq(roperand, loperand);
	}
	private SymExpression eval_smaller_tn(SymBinaryExpression source) throws Exception {
		SymExpression loperand = this.evaluate(source.get_loperand());
		SymExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			SymConstant lconstant = (SymConstant) loperand;
			if(roperand instanceof SymConstant) {
				SymConstant rconstant = (SymConstant) roperand;
				return SymComputation.smaller_tn(lconstant, rconstant);
			}
		}
		
		return SymFactory.smaller_tn(loperand, roperand);
	}
	private SymExpression eval_smaller_eq(SymBinaryExpression source) throws Exception {
		SymExpression loperand = this.evaluate(source.get_loperand());
		SymExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			SymConstant lconstant = (SymConstant) loperand;
			if(roperand instanceof SymConstant) {
				SymConstant rconstant = (SymConstant) roperand;
				return SymComputation.smaller_eq(lconstant, rconstant);
			}
		}
		
		return SymFactory.smaller_eq(loperand, roperand);
	}
	private SymExpression eval_equal_with(SymBinaryExpression source) throws Exception {
		SymExpression loperand = this.evaluate(source.get_loperand());
		SymExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			SymConstant lconstant = (SymConstant) loperand;
			if(roperand instanceof SymConstant) {
				SymConstant rconstant = (SymConstant) roperand;
				return SymComputation.equal_with(lconstant, rconstant);
			}
		}
		
		return SymFactory.equal_with(loperand, roperand);
	}
	private SymExpression eval_not_equals(SymBinaryExpression source) throws Exception {
		SymExpression loperand = this.evaluate(source.get_loperand());
		SymExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			SymConstant lconstant = (SymConstant) loperand;
			if(roperand instanceof SymConstant) {
				SymConstant rconstant = (SymConstant) roperand;
				return SymComputation.not_equals(lconstant, rconstant);
			}
		}
		
		return SymFactory.not_equals(loperand, roperand);
	}
	
}
