package com.jcsa.jcparse.parse.symbol;

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
import com.jcsa.jcparse.lang.symbol.SymbolArgumentList;
import com.jcsa.jcparse.lang.symbol.SymbolBasicExpression;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolCallExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolFieldExpression;
import com.jcsa.jcparse.lang.symbol.SymbolInitializerList;
import com.jcsa.jcparse.lang.symbol.SymbolUnaryExpression;

/**
 * It is used to optimize symbolic expression.
 * 
 * @author yukimula
 *
 */
public class SymbolEvaluator {
	
	/* definition */
	private SymbolStateContexts context;
	private SymbolEvaluator() { }
	private static SymbolEvaluator evaluator = new SymbolEvaluator();
	
	/* evaluation methods */
	/**
	 * @param source
	 * @return get the solution by source, source.get_source & source.generate_code()
	 * @throws Exception
	 */
	private SymbolExpression get_solution(SymbolExpression source) throws Exception {
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
	private SymbolExpression evaluate(SymbolExpression source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else {
			SymbolExpression result = this.get_solution(source);
			if(result != null) {
				return result;
			}
			else {
				SymbolExpression target;
				if(source instanceof SymbolBasicExpression) {
					target = this.eval_basic_expression((SymbolBasicExpression) source);
				}
				else if(source instanceof SymbolBinaryExpression) {
					target = this.eval_binary_expression((SymbolBinaryExpression) source);
				}
				else if(source instanceof SymbolUnaryExpression) {
					target = this.eval_unary_expression((SymbolUnaryExpression) source);
				}
				else if(source instanceof SymbolCallExpression) {
					target = this.eval_call_expression((SymbolCallExpression) source);
				}
				else if(source instanceof SymbolFieldExpression) {
					target = this.eval_field_expression((SymbolFieldExpression) source);
				}
				else if(source instanceof SymbolInitializerList) {
					target = this.eval_initializer_list((SymbolInitializerList) source);
				}
				else {
					throw new IllegalArgumentException(source.generate_code(false));
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
	public static SymbolExpression evaluate_on(SymbolExpression source, SymbolStateContexts contexts) throws Exception {
		evaluator.context = contexts;
		return evaluator.evaluate(source);
	}
	/**
	 * @param source
	 * @param context
	 * @return the result evaluated from the evaluator without contextual information
	 * @throws Exception
	 */
	public static SymbolExpression evaluate_on(SymbolExpression source) throws Exception {
		evaluator.context = null;
		return evaluator.evaluate(source);
	}
	
	/* implementation methods */
	private SymbolExpression eval_basic_expression(SymbolBasicExpression source) throws Exception {
		return (SymbolExpression) source.clone();
	}
	private SymbolExpression eval_call_expression(SymbolCallExpression source) throws Exception {
		SymbolExpression function = this.evaluate(source.get_function());
		List<Object> arguments = new ArrayList<Object>();
		SymbolArgumentList alist = source.get_argument_list();
		for(int k = 0; k < alist.number_of_arguments(); k++) {
			arguments.add(this.evaluate(alist.get_argument(k)));
		}
		SymbolCallExpression target = (SymbolCallExpression) SymbolFactory.call_expression(function, arguments);
		if(this.context != null)
			return this.context.invocate(target);
		else
			return target;
	}
	private SymbolExpression eval_field_expression(SymbolFieldExpression source) throws Exception {
		SymbolExpression body = this.evaluate(source.get_body());
		String field = source.get_field().get_name();
		return SymbolFactory.field_expression(body, field);
	}
	private SymbolExpression eval_initializer_list(SymbolInitializerList source) throws Exception {
		List<Object> elements = new ArrayList<Object>();
		for(int k = 0; k < source.number_of_elements(); k++) {
			elements.add(this.evaluate(source.get_element(k)));
		}
		return SymbolFactory.initializer_list(elements);
	}
	private SymbolExpression eval_binary_expression(SymbolBinaryExpression source) throws Exception {
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
		default: throw new IllegalArgumentException(source.generate_code(false));
		}
	}
	private SymbolExpression eval_unary_expression(SymbolUnaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();
		switch(operator) {
		case negative:		return this.eval_arith_neg(source);
		case bit_not:		return this.eval_bitws_rsv(source);
		case logic_not:		return this.eval_logic_not(source);
		case address_of:	return this.eval_address_of(source);
		case dereference:	return this.eval_dereference(source);
		case assign:		return this.eval_type_cast(source);
		default: throw new IllegalArgumentException(source.generate_code(false));
		}
	}
	
	/* unary expression parts */
	private SymbolExpression eval_arith_neg(SymbolUnaryExpression source) throws Exception {
		SymbolExpression operand = this.evaluate(source.get_operand());
		CType data_type = source.get_data_type();
		if(operand instanceof SymbolConstant) {
			return SymbolComputation.arith_neg((SymbolConstant) operand);
		}
		else if(operand instanceof SymbolUnaryExpression) {
			COperator operator = 
					((SymbolUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.negative) {
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			else {
				return SymbolFactory.arith_neg(operand);
			}
		}
		else if(operand instanceof SymbolBinaryExpression) {
			COperator operator = 
					((SymbolBinaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.arith_sub) {
				return SymbolFactory.arith_sub(data_type, 
						((SymbolBinaryExpression) operand).get_roperand(), 
						((SymbolBinaryExpression) operand).get_loperand());
			}
			else {
				return SymbolFactory.arith_neg(operand);
			}
		}
		else {
			return SymbolFactory.arith_neg(operand);
		}
	}
	private SymbolExpression eval_bitws_rsv(SymbolUnaryExpression source) throws Exception {
		SymbolExpression operand = this.evaluate(source.get_operand());
		
		if(operand instanceof SymbolConstant) {
			return SymbolComputation.bitws_rsv((SymbolConstant) operand);
		}
		else if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.bit_not) {
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			else {
				return SymbolFactory.bitws_rsv(operand);
			}
		}
		else {
			return SymbolFactory.bitws_rsv(operand);
		}
	}
	private SymbolExpression eval_logic_not(SymbolUnaryExpression source) throws Exception {
		SymbolExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SymbolConstant) {
			return SymbolComputation.logic_not((SymbolConstant) operand);
		}
		else if(operand instanceof SymbolUnaryExpression) {
			COperator operator = 
					((SymbolUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.logic_not) {
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			else {
				return SymbolFactory.sym_condition(operand, false);
			}
		}
		else if(operand instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) operand).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) operand).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) operand).get_roperand();
			switch(operator) {
			case logic_and:
			{
				loperand = this.evaluate(SymbolFactory.sym_condition(loperand, false));
				roperand = this.evaluate(SymbolFactory.sym_condition(roperand, false));
				return SymbolFactory.logic_ior(loperand, roperand);
			}
			case logic_or:
			{
				loperand = this.evaluate(SymbolFactory.sym_condition(loperand, false));
				roperand = this.evaluate(SymbolFactory.sym_condition(roperand, false));
				return SymbolFactory.logic_and(loperand, roperand);
			}
			case greater_tn:
			{
				return SymbolFactory.smaller_eq(loperand, roperand);
			}
			case greater_eq:
			{
				return SymbolFactory.smaller_tn(loperand, roperand);
			}
			case smaller_tn:
			{
				return SymbolFactory.greater_eq(loperand, roperand);
			}
			case smaller_eq:
			{
				return SymbolFactory.greater_tn(loperand, roperand);
			}
			case equal_with:
			{
				return SymbolFactory.not_equals(loperand, roperand);
			}
			case not_equals:
			{
				return SymbolFactory.equal_with(loperand, roperand);
			}
			default: 
			{
				return SymbolFactory.sym_condition(operand, false);
			}
			}
		}
		else {
			return SymbolFactory.sym_condition(operand, false);
		}
	}
	private SymbolExpression eval_address_of(SymbolUnaryExpression source) throws Exception {
		SymbolExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SymbolUnaryExpression) {
			COperator operator = 
					((SymbolUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.dereference) {
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			else {
				return SymbolFactory.address_of(operand);
			}
		}
		else {
			return SymbolFactory.address_of(operand);
		}
	}
	private SymbolExpression eval_dereference(SymbolUnaryExpression source) throws Exception {
		SymbolExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SymbolUnaryExpression) {
			COperator operator = 
					((SymbolUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.address_of) {
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			else {
				return SymbolFactory.dereference(operand);
			}
		}
		else {
			return SymbolFactory.dereference(operand);
		}
	}
	private SymbolExpression eval_type_cast(SymbolUnaryExpression source) throws Exception {
		SymbolExpression operand = this.evaluate(source.get_operand());
		CType data_type = source.get_data_type();
		if(operand instanceof SymbolConstant) {
			CConstant constant = new CConstant();
			if(data_type instanceof CBasicType) {
				switch(((CBasicType) data_type).get_tag()) {
				case c_bool:	constant.set_bool(((SymbolConstant) operand).get_bool()); break;
				case c_char:	
				case c_uchar:	constant.set_char(((SymbolConstant) operand).get_char()); break;
				case c_short:
				case c_ushort:	constant.set_int(((SymbolConstant) operand).get_short()); break;
				case c_int:
				case c_uint:	constant.set_int(((SymbolConstant) operand).get_int()); break;
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:	constant.set_long(((SymbolConstant) operand).get_long()); break;
				case c_float:	constant.set_float(((SymbolConstant) operand).get_float()); break;
				case c_double:
				case c_ldouble:	constant.set_double(((SymbolConstant) operand).get_double()); break;
				default: throw new IllegalArgumentException(data_type.generate_code());
				}
			}
			else if(data_type instanceof CArrayType
					|| data_type instanceof CPointerType) {
				constant.set_long(((SymbolConstant) operand).get_long());
			}
			else if(data_type instanceof CEnumType) {
				constant.set_int(((SymbolConstant) operand).get_int());
			}
			else {
				throw new IllegalArgumentException(data_type.generate_code());
			}
			return SymbolFactory.sym_expression(constant);
		}
		else {
			return SymbolFactory.cast_expression(data_type, operand);
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
	private void get_operands_in_as(SymbolExpression source, 
			Collection<SymbolExpression> loperands, 
			Collection<SymbolExpression> roperands) throws Exception {
		if(source instanceof SymbolBinaryExpression) {
			COperator operator = 
					((SymbolBinaryExpression) source).get_operator().get_operator();
			if(operator == COperator.arith_add) {
				this.get_operands_in_as(((SymbolBinaryExpression) source).get_loperand(), loperands, roperands);
				this.get_operands_in_as(((SymbolBinaryExpression) source).get_roperand(), loperands, roperands);
			}
			else if(operator == COperator.arith_sub) {
				this.get_operands_in_as(((SymbolBinaryExpression) source).get_loperand(), loperands, roperands);
				this.get_operands_in_as(((SymbolBinaryExpression) source).get_roperand(), roperands, loperands);
			}
			else {
				loperands.add(source);
			}
		}
		else if(source instanceof SymbolUnaryExpression) {
			COperator operator = 
					((SymbolUnaryExpression) source).get_operator().get_operator();
			if(operator == COperator.negative) {
				this.get_operands_in_as(((SymbolUnaryExpression) source).get_operand(), roperands, loperands);
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
	private List<SymbolExpression> sov_operands_in_as(List<SymbolExpression> operands) throws Exception {
		List<SymbolExpression> new_operands = new ArrayList<SymbolExpression>();
		SymbolConstant constant = (SymbolConstant) SymbolFactory.sym_expression(Integer.valueOf(0));
		for(SymbolExpression operand : operands) {
			SymbolExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SymbolConstant) {
				constant = SymbolComputation.arith_add(
						constant, (SymbolConstant) new_operand);
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
	private void rem_operands_in_as(List<SymbolExpression> loperands, List<SymbolExpression> roperands) throws Exception {
		Set<SymbolExpression> lremoves = new HashSet<SymbolExpression>();
		Set<SymbolExpression> rremoves = new HashSet<SymbolExpression>();
		for(SymbolExpression loperand : loperands) {
			String x = loperand.generate_code(false);
			for(SymbolExpression roperand : roperands) {
				if(!rremoves.contains(roperand)) {
					String y = roperand.generate_code(false);
					if(y.equals(x)) {
						lremoves.add(loperand);
						rremoves.add(roperand);
					}
				}
			}
		}
		for(SymbolExpression loperand : lremoves) loperands.remove(loperand);
		for(SymbolExpression roperand : rremoves) roperands.remove(roperand);
	}
	/**
	 * @param operands
	 * @return x1 + x2 + ... + xN
	 * @throws Exception
	 */
	private SymbolExpression acc_operands_in_as(CType type, List<SymbolExpression> operands) throws Exception {
		SymbolExpression expression = null;
		for(SymbolExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = SymbolFactory.arith_add(type, expression, operand);
			}
		}
		return expression;	/* null if the operands are of empty */
	}
	private SymbolExpression com_operands_in_as(CType type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(loperand == null) {
			if(roperand == null) {
				return SymbolFactory.sym_expression(Long.valueOf(0));
			}
			else {
				return SymbolFactory.arith_neg(roperand);
			}
		}
		else {
			if(roperand == null) {
				return loperand;
			}
			else {
				return SymbolFactory.arith_sub(type, loperand, roperand);
			}
		}
	}
	private SymbolExpression eval_arith_add(SymbolBinaryExpression source) throws Exception {
		/* declarations */
		CType type = source.get_data_type();
		List<SymbolExpression> loperands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> roperands = new ArrayList<SymbolExpression>();
		
		/* 1. divide operands into left and right groups */
		this.get_operands_in_as(source, loperands, roperands);
		
		/* 2. solve the operands and rebuild operand groups */
		loperands = this.sov_operands_in_as(loperands);
		roperands = this.sov_operands_in_as(roperands);
		
		/* 3. composite both constants in left and right */
		SymbolExpression lconstant = loperands.remove(loperands.size() - 1);
		SymbolExpression rconstant = roperands.remove(roperands.size() - 1);
		SymbolConstant constant = SymbolComputation.arith_sub(
					(SymbolConstant) lconstant, (SymbolConstant) rconstant);
		if(!constant.compare(0)) { loperands.add(constant); }
		
		/* 4. remove duplicated numbers in left and right */
		this.rem_operands_in_as(loperands, roperands);
		SymbolExpression loperand = this.acc_operands_in_as(type, loperands);
		SymbolExpression roperand = this.acc_operands_in_as(type, roperands);
		
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
	private void get_operands_in_md(SymbolExpression source,
			Collection<SymbolExpression> loperands, 
			Collection<SymbolExpression> roperands) throws Exception {
		if(source instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) 
					source).get_operator().get_operator();
			if(operator == COperator.negative) {
				loperands.add(((SymbolUnaryExpression) source).get_operand());
				roperands.add(SymbolFactory.sym_expression(Long.valueOf(-1L)));
			}
			else {
				loperands.add(source);
			}
		}
		else if(source instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) 
					source).get_operator().get_operator();
			if(operator == COperator.arith_mul) {
				this.get_operands_in_md(((SymbolBinaryExpression) source).get_loperand(), loperands, roperands);
				this.get_operands_in_md(((SymbolBinaryExpression) source).get_roperand(), loperands, roperands);
			}
			else if(operator == COperator.arith_div) {
				this.get_operands_in_md(((SymbolBinaryExpression) source).get_loperand(), loperands, roperands);
				this.get_operands_in_md(((SymbolBinaryExpression) source).get_roperand(), roperands, loperands);
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
	private List<SymbolExpression> sov_operands_in_md(List<SymbolExpression> operands) throws Exception {
		List<SymbolExpression> new_operands = new ArrayList<SymbolExpression>();
		SymbolConstant constant = (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(1L));
		for(SymbolExpression operand : operands) {
			SymbolExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SymbolConstant) {
				constant = SymbolComputation.arith_mul(
						constant, (SymbolConstant) new_operand);
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
	private SymbolConstant[] get_constant_in_md(CType type, SymbolConstant lconstant, SymbolConstant rconstant) throws Exception {
		type = CTypeAnalyzer.get_value_type(type);
		if(CTypeAnalyzer.is_boolean(type) || CTypeAnalyzer.is_integer(type) || CTypeAnalyzer.is_pointer(type)) {
			long x = lconstant.get_long();
			long y = rconstant.get_long();
			/* compute the greatest common divisor */
			long gcd = this.gcd(x, y);
			x = x / gcd; y = y / gcd;
			if(y < 0) { y = -y; x = -x; }
			return new SymbolConstant[] { 
					(SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(x)),
					(SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(y))
			};
		}
		else if(CTypeAnalyzer.is_real(type)) {
			double x = lconstant.get_double();
			double y = rconstant.get_double();
			return new SymbolConstant[] {
					(SymbolConstant) SymbolFactory.sym_expression(Double.valueOf(x / y)),
					(SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(1L))
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
	private void rem_operands_in_md(List<SymbolExpression> loperands, List<SymbolExpression> roperands) throws Exception {
		Set<SymbolExpression> lremoves = new HashSet<SymbolExpression>();
		Set<SymbolExpression> rremoves = new HashSet<SymbolExpression>();
		for(SymbolExpression loperand : loperands) {
			String x = loperand.generate_code(false);
			for(SymbolExpression roperand : roperands) {
				if(!rremoves.contains(roperand)) {
					String y = roperand.generate_code(false);
					if(y.equals(x)) {
						lremoves.add(loperand);
						rremoves.add(roperand);
					}
				}
			}
		}
		for(SymbolExpression loperand : lremoves) loperands.remove(loperand);
		for(SymbolExpression roperand : rremoves) roperands.remove(roperand);
	}
	/**
	 * @param operands
	 * @return x1 + x2 + ... + xN
	 * @throws Exception
	 */
	private SymbolExpression acc_operands_in_md(CType type, List<SymbolExpression> operands) throws Exception {
		SymbolExpression expression = null;
		for(SymbolExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = SymbolFactory.arith_mul(type, expression, operand);
			}
		}
		return expression;	/* null if the operands are of empty */
	}
	private SymbolExpression com_operands_in_md(CType type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(loperand == null) {
			if(roperand == null) {
				return SymbolFactory.sym_expression(Long.valueOf(1));
			}
			else {
				return SymbolFactory.arith_div(type, SymbolFactory.sym_expression(Long.valueOf(1)), roperand);
			}
		}
		else {
			if(roperand == null) {
				return loperand;
			}
			else {
				return SymbolFactory.arith_div(type, loperand, roperand);
			}
		}
	}
	private SymbolExpression eval_arith_mul(SymbolBinaryExpression source) throws Exception {
		/* declarations */
		CType type = source.get_data_type();
		List<SymbolExpression> loperands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> roperands = new ArrayList<SymbolExpression>();
		
		/* 1. divide the operands into left and right group */
		this.get_operands_in_md(source, loperands, roperands);
		
		/* 2. solve the operands in the left and right group */
		loperands = this.sov_operands_in_md(loperands);
		roperands = this.sov_operands_in_md(roperands);
		
		/* 3. obtain the constant parts in the both groups */
		SymbolConstant lconstant = (SymbolConstant) loperands.remove(loperands.size() - 1);
		SymbolConstant rconstant = (SymbolConstant) roperands.remove(roperands.size() - 1);
		SymbolConstant[] constants = this.get_constant_in_md(type, lconstant, rconstant);
		lconstant = constants[0]; rconstant = constants[1];
		if(lconstant.compare(0)) { return SymbolFactory.sym_expression(0L); }
		if(!lconstant.compare(1)) { loperands.add(lconstant); }
		if(!rconstant.compare(1)) { roperands.add(rconstant); }
		
		/* 4. remove the duplicated operands in left & right */
		this.rem_operands_in_md(loperands, roperands);
		SymbolExpression loperand = this.acc_operands_in_md(type, loperands);
		SymbolExpression roperand = this.acc_operands_in_md(type, roperands);
		
		/* 5. generate the arithmetic division composition */
		return this.com_operands_in_md(type, loperand, roperand);
	}
	
	/* arithmetic MOD */
	private SymbolExpression eval_arith_mod(SymbolBinaryExpression source) throws Exception {
		/* 1. getters */
		CType data_type = source.get_data_type();
		SymbolExpression loperand = this.evaluate(source.get_loperand());
		SymbolExpression roperand = this.evaluate(source.get_roperand());
		
		/* 2. partial evaluation */
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			if(roperand instanceof SymbolConstant) {
				SymbolConstant rconstant = (SymbolConstant) roperand;
				return SymbolComputation.arith_mod(lconstant, rconstant);
			}
			else {
				if(lconstant.compare(0) || lconstant.compare(1)) {
					return lconstant;
				}
				else {
					return SymbolFactory.arith_mod(data_type, loperand, roperand);
				}
			}
		}
		else {
			if(roperand instanceof SymbolConstant) {
				SymbolConstant rconstant = (SymbolConstant) roperand;
				if(rconstant.compare(1) || rconstant.compare(-1)) {
					return SymbolFactory.sym_expression(Long.valueOf(0));
				}
				else {
					return SymbolFactory.arith_mod(data_type, loperand, roperand);
				}
			}
			else {
				return SymbolFactory.arith_mod(data_type, loperand, roperand);
			}
		}
	}
	
	/* bitwise operations for &, |, ^, &&, || */
	private void get_operands_in_xx(SymbolExpression source, List<SymbolExpression> operands, COperator operator) throws Exception {
		if(source instanceof SymbolBinaryExpression) {
			COperator operator2 = ((SymbolBinaryExpression) source).get_operator().get_operator();
			if(operator == operator2) {
				this.get_operands_in_xx(((SymbolBinaryExpression) source).get_loperand(), operands, operator);
				this.get_operands_in_xx(((SymbolBinaryExpression) source).get_roperand(), operands, operator);
			}
			else {
				operands.add(source);
			}
		}
		else {
			operands.add(source);
		}
	}
	private List<SymbolExpression> sov_operands_in_xx(List<SymbolExpression> operands, SymbolConstant constant, COperator operator) throws Exception {
		List<SymbolExpression> new_operands = new ArrayList<SymbolExpression>();
		for(SymbolExpression operand : operands) {
			SymbolExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SymbolConstant) {
				switch(operator) {
				case bit_and:	constant = SymbolComputation.bitws_and(constant, (SymbolConstant) new_operand); break;
				case bit_or:	constant = SymbolComputation.bitws_ior(constant, (SymbolConstant) new_operand); break;
				case bit_xor:	constant = SymbolComputation.bitws_xor(constant, (SymbolConstant) new_operand); break;
				case logic_and:	constant = SymbolComputation.logic_and(constant, (SymbolConstant) new_operand); break;
				case logic_or:	constant = SymbolComputation.logic_ior(constant, (SymbolConstant) new_operand); break;
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
	private SymbolExpression acc_operands_in_xx(CType data_type, COperator operator, List<SymbolExpression> operands) throws Exception {
		SymbolExpression expression = null;
		for(SymbolExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				switch(operator) {
				case bit_and:	expression = SymbolFactory.bitws_and(data_type, expression, operand); break;
				case bit_or:	expression = SymbolFactory.bitws_ior(data_type, expression, operand); break;
				case bit_xor:	expression = SymbolFactory.bitws_xor(data_type, expression, operand); break;
				case logic_and:	expression = SymbolFactory.logic_and(expression, operand); break;
				case logic_or:	expression = SymbolFactory.logic_ior(expression, operand); break;
				default: throw new IllegalArgumentException("Invalid: " + operator);
				}
			}
		}
		return expression;
	}
	private SymbolExpression eval_bitws_and(SymbolBinaryExpression source) throws Exception {
		/* getters */
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		List<SymbolExpression> operands = new ArrayList<SymbolExpression>();
		COperator operator = source.get_operator().get_operator();
		
		/* obtain solved operands using initial constant */
		SymbolConstant constant = (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(~0L));
		this.get_operands_in_xx(source, operands, operator);
		operands = this.sov_operands_in_xx(operands, constant, operator);
		
		/* rebuild constant and partial evaluation */
		constant = (SymbolConstant) operands.remove(operands.size() - 1);
		if(constant.compare(0L)) return SymbolFactory.sym_expression(Long.valueOf(0L)); 
		else if(!constant.compare(~0L) || operands.isEmpty()) operands.add(constant);
		
		return this.acc_operands_in_xx(data_type, operator, operands);
	}
	private SymbolExpression eval_bitws_ior(SymbolBinaryExpression source) throws Exception {
		/* getters */
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		List<SymbolExpression> operands = new ArrayList<SymbolExpression>();
		COperator operator = source.get_operator().get_operator();
		
		/* obtain solved operands using initial constant */
		SymbolConstant constant = (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(0L));
		this.get_operands_in_xx(source, operands, operator);
		operands = this.sov_operands_in_xx(operands, constant, operator);
		
		/* rebuild constant and partial evaluation */
		constant = (SymbolConstant) operands.remove(operands.size() - 1);
		if(constant.compare(~0L)) return SymbolFactory.sym_expression(Long.valueOf(~0L)); 
		else if(!constant.compare(0L) || operands.isEmpty()) operands.add(constant);
		
		return this.acc_operands_in_xx(data_type, operator, operands);
	}
	private SymbolExpression eval_bitws_xor(SymbolBinaryExpression source) throws Exception {
		/* getters */
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		List<SymbolExpression> operands = new ArrayList<SymbolExpression>();
		COperator operator = source.get_operator().get_operator();
		
		/* obtain solved operands using initial constant */
		SymbolConstant constant = (SymbolConstant) SymbolFactory.sym_expression(Long.valueOf(0L));
		this.get_operands_in_xx(source, operands, operator);
		operands = this.sov_operands_in_xx(operands, constant, operator);
		
		/* rebuild constant and partial evaluation */
		constant = (SymbolConstant) operands.remove(operands.size() - 1);
		if(!constant.compare(0L) || operands.isEmpty()) operands.add(constant);
		
		return this.acc_operands_in_xx(data_type, operator, operands);
	}
	private SymbolExpression eval_logic_and(SymbolBinaryExpression source) throws Exception {
		/* getters */
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		List<SymbolExpression> operands = new ArrayList<SymbolExpression>();
		COperator operator = source.get_operator().get_operator();
		
		/* obtain solved operands using initial constant */
		SymbolConstant constant = (SymbolConstant) SymbolFactory.sym_expression(Boolean.TRUE);
		this.get_operands_in_xx(source, operands, operator);
		operands = this.sov_operands_in_xx(operands, constant, operator);
		
		/* rebuild constant and partial evaluation */
		constant = (SymbolConstant) operands.remove(operands.size() - 1);
		if(!constant.get_bool()) return SymbolFactory.sym_expression(Boolean.FALSE); 
		if(operands.isEmpty()) operands.add(constant);
		
		return this.acc_operands_in_xx(data_type, operator, operands);
	}
	private SymbolExpression eval_logic_ior(SymbolBinaryExpression source) throws Exception {
		/* getters */
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		List<SymbolExpression> operands = new ArrayList<SymbolExpression>();
		COperator operator = source.get_operator().get_operator();
		
		/* obtain solved operands using initial constant */
		SymbolConstant constant = (SymbolConstant) SymbolFactory.sym_expression(Boolean.FALSE);
		this.get_operands_in_xx(source, operands, operator);
		operands = this.sov_operands_in_xx(operands, constant, operator);
		
		/* rebuild constant and partial evaluation */
		constant = (SymbolConstant) operands.remove(operands.size() - 1);
		if(constant.get_bool()) return SymbolFactory.sym_expression(Boolean.TRUE); 
		if(operands.isEmpty()) operands.add(constant);
		
		return this.acc_operands_in_xx(data_type, operator, operands);
	}
	
	/* <<, >>, <, <=, >, >=, ==, != */
	private SymbolExpression eval_bitws_lsh(SymbolBinaryExpression source) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		SymbolExpression loperand = this.evaluate(source.get_loperand());
		SymbolExpression roperand = this.evaluate(source.get_roperand());
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			if(roperand instanceof SymbolConstant) {
				SymbolConstant rconstant = (SymbolConstant) roperand;
				return SymbolComputation.bitws_lsh(lconstant, rconstant);
			}
			else {
				if(lconstant.compare(0)) {
					return loperand;
				}
				else {
					return SymbolFactory.bitws_lsh(data_type, loperand, roperand);
				}
			}
		}
		else {
			if(roperand instanceof SymbolConstant) {
				SymbolConstant rconstant = (SymbolConstant) roperand;
				if(rconstant.compare(0)) {
					return loperand;
				}
				else {
					return SymbolFactory.bitws_lsh(data_type, loperand, roperand);
				}
			}
			else {
				return SymbolFactory.bitws_lsh(data_type, loperand, roperand);
			}
		}
	}
	private SymbolExpression eval_bitws_rsh(SymbolBinaryExpression source) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		SymbolExpression loperand = this.evaluate(source.get_loperand());
		SymbolExpression roperand = this.evaluate(source.get_roperand());
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			if(roperand instanceof SymbolConstant) {
				SymbolConstant rconstant = (SymbolConstant) roperand;
				return SymbolComputation.bitws_rsh(lconstant, rconstant);
			}
			else {
				if(lconstant.compare(0)) {
					return loperand;
				}
				else {
					return SymbolFactory.bitws_rsh(data_type, loperand, roperand);
				}
			}
		}
		else {
			if(roperand instanceof SymbolConstant) {
				SymbolConstant rconstant = (SymbolConstant) roperand;
				if(rconstant.compare(0)) {
					return loperand;
				}
				else {
					return SymbolFactory.bitws_rsh(data_type, loperand, roperand);
				}
			}
			else {
				return SymbolFactory.bitws_rsh(data_type, loperand, roperand);
			}
		}
	}
	private SymbolExpression part_smaller_tn(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		/* type-based partial evaluation */
		CType ltype = CTypeAnalyzer.get_value_type(loperand.get_data_type());
		if(CTypeAnalyzer.is_boolean(ltype)) {
			if(roperand instanceof SymbolConstant) {
				Object number = ((SymbolConstant) roperand).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value > 1) {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
					else if(value <= 0) {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
					else {
						return SymbolFactory.logic_not(loperand);
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value > 1) {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
					else if(value <= 0) {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
					else {
						return SymbolFactory.logic_not(loperand);
					}
				}
			}
		}
		else if(CTypeAnalyzer.is_unsigned(ltype)) {
			if(roperand instanceof SymbolConstant) {
				Object number = ((SymbolConstant) roperand).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value <= 0) {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
					else if(value == 1) {
						return SymbolFactory.equal_with(loperand, Integer.valueOf(0));
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value <= 0) {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
					else if(value <= 1) {
						return SymbolFactory.equal_with(loperand, Integer.valueOf(0));
					}
				}
			}
		}
		
		CType rtype = CTypeAnalyzer.get_value_type(roperand.get_data_type());
		if(CTypeAnalyzer.is_boolean(rtype)) {
			if(loperand instanceof SymbolConstant) {
				Object number = ((SymbolConstant) loperand).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value < 0) {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
					else if(value >= 1) {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
					else {
						return roperand;
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value < 0) {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
					else if(value >= 1) {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
					else {
						return roperand;
					}
				}
			}
		}
		else if(CTypeAnalyzer.is_unsigned(rtype)) {
			if(loperand instanceof SymbolConstant) {
				Object number = ((SymbolConstant) loperand).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value < 0) {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
					else if(value == 0) {
						return SymbolFactory.not_equals(roperand, Integer.valueOf(0));
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value < 0) {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
					else if(value == 0) {
						return SymbolFactory.not_equals(roperand, Integer.valueOf(0));
					}
				}
			}
		}
		
		return SymbolFactory.smaller_tn(loperand, roperand);
	}
	private SymbolExpression eval_greater_tn(SymbolBinaryExpression source) throws Exception {
		SymbolExpression loperand = this.evaluate(source.get_loperand());
		SymbolExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			if(roperand instanceof SymbolConstant) {
				SymbolConstant rconstant = (SymbolConstant) roperand;
				return SymbolComputation.greater_tn(lconstant, rconstant);
			}
		}
		
		return this.part_smaller_tn(roperand, loperand);
	}
	private SymbolExpression eval_smaller_tn(SymbolBinaryExpression source) throws Exception {
		SymbolExpression loperand = this.evaluate(source.get_loperand());
		SymbolExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			if(roperand instanceof SymbolConstant) {
				SymbolConstant rconstant = (SymbolConstant) roperand;
				return SymbolComputation.smaller_tn(lconstant, rconstant);
			}
		}
		
		return this.part_smaller_tn(loperand, roperand);
	}
	private SymbolExpression part_smaller_eq(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		CType ltype = CTypeAnalyzer.get_value_type(loperand.get_data_type());
		if(CTypeAnalyzer.is_boolean(ltype)) {
			if(roperand instanceof SymbolConstant) {
				Object number = ((SymbolConstant) roperand).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value < 0) {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
					else if(value >= 1) {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
					else {
						return SymbolFactory.logic_not(loperand);
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value < 0) {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
					else if(value >= 1) {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
					else {
						return SymbolFactory.logic_not(loperand);
					}
				}
			}
		}
		else if(CTypeAnalyzer.is_unsigned(ltype)) {
			if(roperand instanceof SymbolConstant) {
				Object number = ((SymbolConstant) roperand).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value < 0) {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
					else if(value == 0) {
						return SymbolFactory.equal_with(loperand, Integer.valueOf(0));
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value < 0) {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
					else if(value == 0) {
						return SymbolFactory.equal_with(loperand, Integer.valueOf(0));
					}
				}
			}
		}
		
		CType rtype = CTypeAnalyzer.get_value_type(roperand.get_data_type());
		if(CTypeAnalyzer.is_boolean(rtype)) {
			if(loperand instanceof SymbolConstant) {
				Object number = ((SymbolConstant) loperand).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value <= 0) {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
					else if(value > 1) {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
					else {
						return loperand;
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value <= 0) {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
					else if(value > 1) {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
					else {
						return loperand;
					}
				}
			}
		}
		else if(CTypeAnalyzer.is_unsigned(rtype)) {
			if(loperand instanceof SymbolConstant) {
				Object number = ((SymbolConstant) loperand).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value <= 0) {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value <= 0) {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
				}
			}
		}
		
		return SymbolFactory.smaller_eq(loperand, roperand);
	}
	private SymbolExpression eval_greater_eq(SymbolBinaryExpression source) throws Exception {
		SymbolExpression loperand = this.evaluate(source.get_loperand());
		SymbolExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			if(roperand instanceof SymbolConstant) {
				SymbolConstant rconstant = (SymbolConstant) roperand;
				return SymbolComputation.greater_eq(lconstant, rconstant);
			}
		}
		
		return this.part_smaller_eq(roperand, loperand);
	}
	private SymbolExpression eval_smaller_eq(SymbolBinaryExpression source) throws Exception {
		SymbolExpression loperand = this.evaluate(source.get_loperand());
		SymbolExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			if(roperand instanceof SymbolConstant) {
				SymbolConstant rconstant = (SymbolConstant) roperand;
				return SymbolComputation.smaller_eq(lconstant, rconstant);
			}
		}
		
		return this.part_smaller_eq(loperand, roperand);
	}
	private SymbolExpression part_equal_with(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(CTypeAnalyzer.is_boolean(CTypeAnalyzer.get_value_type(loperand.get_data_type()))) {
			if(roperand instanceof SymbolConstant) {
				Object number = ((SymbolConstant) roperand).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value == 0) {
						return SymbolFactory.logic_not(loperand);
					}
					else if(value == 1) {
						return loperand;
					}
					else {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value == 0) {
						return SymbolFactory.logic_not(loperand);
					}
					else if(value == 1) {
						return loperand;
					}
					else {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
				}
			}
		}
		if(CTypeAnalyzer.is_boolean(CTypeAnalyzer.get_value_type(roperand.get_data_type()))) {
			if(loperand instanceof SymbolConstant) {
				Object number = ((SymbolConstant) loperand).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value == 0) {
						return SymbolFactory.logic_not(roperand);
					}
					else if(value == 1) {
						return roperand;
					}
					else {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value == 0) {
						return SymbolFactory.logic_not(roperand);
					}
					else if(value == 1) {
						return roperand;
					}
					else {
						return SymbolFactory.sym_expression(Boolean.FALSE);
					}
				}
			}
		}
		return SymbolFactory.equal_with(loperand, roperand);
	}
	private SymbolExpression eval_equal_with(SymbolBinaryExpression source) throws Exception {
		SymbolExpression loperand = this.evaluate(source.get_loperand());
		SymbolExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			if(roperand instanceof SymbolConstant) {
				SymbolConstant rconstant = (SymbolConstant) roperand;
				return SymbolComputation.equal_with(lconstant, rconstant);
			}
		}
		
		return this.part_equal_with(loperand, roperand);
	}
	private SymbolExpression part_not_equals(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(CTypeAnalyzer.is_boolean(CTypeAnalyzer.get_value_type(loperand.get_data_type()))) {
			if(roperand instanceof SymbolConstant) {
				Object number = ((SymbolConstant) roperand).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value == 1) {
						return SymbolFactory.logic_not(loperand);
					}
					else if(value == 0) {
						return loperand;
					}
					else {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value == 1) {
						return SymbolFactory.logic_not(loperand);
					}
					else if(value == 0) {
						return loperand;
					}
					else {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
				}
			}
		}
		if(CTypeAnalyzer.is_boolean(CTypeAnalyzer.get_value_type(roperand.get_data_type()))) {
			if(loperand instanceof SymbolConstant) {
				Object number = ((SymbolConstant) loperand).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value == 1) {
						return SymbolFactory.logic_not(roperand);
					}
					else if(value == 0) {
						return roperand;
					}
					else {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value == 1) {
						return SymbolFactory.logic_not(roperand);
					}
					else if(value == 0) {
						return roperand;
					}
					else {
						return SymbolFactory.sym_expression(Boolean.TRUE);
					}
				}
			}
		}
		return SymbolFactory.not_equals(loperand, roperand);
	}
	private SymbolExpression eval_not_equals(SymbolBinaryExpression source) throws Exception {
		SymbolExpression loperand = this.evaluate(source.get_loperand());
		SymbolExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SymbolConstant) {
			SymbolConstant lconstant = (SymbolConstant) loperand;
			if(roperand instanceof SymbolConstant) {
				SymbolConstant rconstant = (SymbolConstant) roperand;
				return SymbolComputation.not_equals(lconstant, rconstant);
			}
		}
		
		return this.part_not_equals(loperand, roperand);
	}
	
}
