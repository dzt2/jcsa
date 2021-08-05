package com.jcsa.jcparse.parse.symbol;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolCallExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFieldExpression;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolInitializerList;
import com.jcsa.jcparse.lang.symbol.SymbolLiteral;
import com.jcsa.jcparse.lang.symbol.SymbolUnaryExpression;
import com.jcsa.jcparse.parse.symbol.process.SymbolInvoker;


/**
 * It implements the computation and simplification of symbolic expression on one-layer.
 *
 * @author yukimula
 *
 */
class SymbolComputer {

	/* definitions */
	/** for which the computational unit serves to simplify or compute expressions **/
	private SymbolEvaluator evaluator;
	/**
	 * create a computational unit for symbolic computation
	 * @param evaluator
	 * @throws Exception
	 */
	protected SymbolComputer(SymbolEvaluator evaluator) {
		if(evaluator == null) {
			throw new IllegalArgumentException("Invalid evaluator: null");
		}
		else {
			this.evaluator = evaluator;
		}
	}

	/* basic operations */
	/**
	 * @param reference
	 * @return read the symbolic value stored for the given reference or itself if not defined in context
	 * @throws Exception
	 */
	protected SymbolExpression compute_loads(SymbolExpression reference) throws Exception {
		if(this.evaluator.get_symbol_process() == null) {
			return null;
		}
		else {
			return this.evaluator.get_symbol_process().get_data_stack().load(reference);
		}
	}
	/**
	 * @param expression
	 * @return compute the basic expression to simplify
	 * @throws Exception
	 */
	protected SymbolExpression compute_basic(SymbolExpression expression) throws Exception {
		return expression;
	}

	/* unary operations */
	/**
	 * @param operand
	 * @return 	Computational Rules:
	 * 			(1) constant		==>	-(constant)
	 * 			(2)	-expression		==> expression
	 * 			(3)	~expression		==>	expression + 1
	 * 			(4)	x - y			==>	y - x
	 * @throws Exception
	 */
	protected SymbolExpression compute_arith_neg(SymbolExpression operand) throws Exception {
		if(operand instanceof SymbolConstant) {
			CBasicType type = ((SymbolConstant) operand).get_constant().get_type();
			Object result;
			switch(type.get_tag()) {
			case c_bool:
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:									/* -(operand.integer) */
			{
				result = Integer.valueOf(-((SymbolConstant) operand).get_int());	break;
			}
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:									/* -(operand.long) */
			{
				result = Long.valueOf(-((SymbolConstant) operand).get_long());		break;
			}
			case c_float:
			case c_double:
			case c_ldouble:									/* -(operand.double) */
			{
				result = Double.valueOf(-((SymbolConstant) operand).get_double());	break;
			}
			default:
			{
				throw new IllegalArgumentException(type.generate_code());
			}
			}
			return this.evaluator.get_symbol_factory().parse_to_constant(result);
		}
		else if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
			switch(operator) {
			case negative:									/* -(-X) --> X */
			{
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			case bit_not:									/* -(~X) --> X + 1 */
			{
				SymbolExpression loperand = ((SymbolUnaryExpression) operand).get_operand();
				SymbolExpression roperand = this.evaluator.get_symbol_factory().parse_to_constant(Integer.valueOf(1));
				return this.evaluator.get_symbol_factory().new_arith_add(operand.get_data_type(), loperand, roperand);
			}
			default:
			{
				return this.evaluator.get_symbol_factory().new_arith_neg(operand);
			}
			}
		}
		else if(operand instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) operand).get_operator().get_operator();
			switch(operator) {
			case arith_sub:									/* -(x - y) --> y - x */
			{
				SymbolExpression loperand = ((SymbolBinaryExpression) operand).get_loperand();
				SymbolExpression roperand = ((SymbolBinaryExpression) operand).get_roperand();
				return this.evaluator.get_symbol_factory().new_arith_sub(operand.get_data_type(), roperand, loperand);
			}
			default:
			{
				return this.evaluator.get_symbol_factory().new_arith_neg(operand);
			}
			}
		}
		else {
			return this.evaluator.get_symbol_factory().new_arith_neg(operand);
		}
	}
	/**
	 * @param operand
	 * @return 	Compute ~operand based on following rule:
	 * 			1.	constant	--> ~(constant.integer)
	 * 			2.	~expression	-->	expression
	 * 			3.	-expression	-->	expression - 1
	 * 			4.	x & y		--> ~x | ~y
	 * 			5.	x | y		-->	~x & ~y
	 * 			6.	otherwise	--> ~operand
	 * @throws Exception
	 */
	protected SymbolExpression compute_bitws_rsv(SymbolExpression operand) throws Exception {
		if(operand instanceof SymbolConstant) {
			CBasicType type = ((SymbolConstant) operand).get_constant().get_type();
			Object result;
			switch(type.get_tag()) {
			case c_bool:
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			{
				result = Integer.valueOf(~((SymbolConstant) operand).get_int());	break;
			}
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			{
				result = Long.valueOf(~((SymbolConstant) operand).get_long());		break;
			}
			default:
			{
				throw new IllegalArgumentException("Invalid type: " + type.generate_code());
			}
			}
			return this.evaluator.get_symbol_factory().parse_to_constant(result);
		}
		else if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
			switch(operator) {
			case negative:
			{
				SymbolExpression loperand = ((SymbolUnaryExpression) operand).get_operand();
				SymbolExpression roperand = this.evaluator.get_symbol_factory().parse_to_constant(Integer.valueOf(1));
				return this.evaluator.get_symbol_factory().new_arith_sub(operand.get_data_type(), loperand, roperand);
			}
			case bit_not:
			{
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			default:
			{
				return this.evaluator.get_symbol_factory().new_bitws_rsv(operand);
			}
			}
		}
		else if(operand instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) operand).get_operator().get_operator();
			switch(operator) {
			case bit_and:
			{
				SymbolExpression loperand = this.compute_bitws_rsv(((SymbolBinaryExpression) operand).get_loperand());
				SymbolExpression roperand = this.compute_bitws_rsv(((SymbolBinaryExpression) operand).get_roperand());
				return this.evaluator.get_symbol_factory().new_bitws_ior(operand.get_data_type(), loperand, roperand);
			}
			case bit_or:
			{
				SymbolExpression loperand = this.compute_bitws_rsv(((SymbolBinaryExpression) operand).get_loperand());
				SymbolExpression roperand = this.compute_bitws_rsv(((SymbolBinaryExpression) operand).get_roperand());
				return this.evaluator.get_symbol_factory().new_bitws_and(operand.get_data_type(), loperand, roperand);
			}
			default:
			{
				return this.evaluator.get_symbol_factory().new_bitws_rsv(operand);
			}
			}
		}
		else {
			return this.evaluator.get_symbol_factory().new_bitws_rsv(operand);
		}
	}
	/**
	 * @param operand
	 * @return	Compute !operand as following:
	 * 			(1)	constant	-->	!(constant.bool)
	 * 			(2)	-expression	-->	expression == 0
	 * 			(3)	~expression	-->	expression == -1
	 * 			(4)	!expression	-->	expression
	 * 			(5)	&expression	-->	false
	 * 			(6)	x && y		-->	!x || !y
	 * 			(7)	x || y		-->	!x && !y
	 * 			(8)	x < y		--> x >= y
	 * 			......
	 * 			(n) !operand
	 * @throws Exception
	 */
	protected SymbolExpression compute_logic_not(SymbolExpression operand) throws Exception {
		if(operand instanceof SymbolConstant) {
			Boolean result = Boolean.valueOf(!((SymbolConstant) operand).get_bool());
			return this.evaluator.get_symbol_factory().parse_to_constant(result);
		}
		else if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
			switch(operator) {
			case negative:
			{
				SymbolExpression loperand = ((SymbolUnaryExpression) operand).get_operand();
				SymbolExpression roperand = this.evaluator.get_symbol_factory().parse_to_constant(Integer.valueOf(0));
				return this.evaluator.get_symbol_factory().new_equal_with(loperand, roperand);
			}
			case bit_not:
			{
				SymbolExpression loperand = ((SymbolUnaryExpression) operand).get_operand();
				SymbolExpression roperand = this.evaluator.get_symbol_factory().parse_to_constant(Integer.valueOf(-1));
				return this.evaluator.get_symbol_factory().new_equal_with(loperand, roperand);
			}
			case logic_not:
			{
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			case address_of:
			{
				return this.evaluator.get_symbol_factory().parse_to_constant(Boolean.FALSE);
			}
			default:
			{
				return this.evaluator.get_symbol_factory().parse_to_condition(operand, false);
			}
			}
		}
		else if(operand instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) operand).get_operator().get_operator();
			switch(operator) {
			case logic_and:
			{
				SymbolExpression loperand = this.compute_logic_not(((SymbolBinaryExpression) operand).get_loperand());
				SymbolExpression roperand = this.compute_logic_not(((SymbolBinaryExpression) operand).get_roperand());
				return this.evaluator.get_symbol_factory().new_logic_ior(loperand, roperand);
			}
			case logic_or:
			{
				SymbolExpression loperand = this.compute_logic_not(((SymbolBinaryExpression) operand).get_loperand());
				SymbolExpression roperand = this.compute_logic_not(((SymbolBinaryExpression) operand).get_roperand());
				return this.evaluator.get_symbol_factory().new_logic_and(loperand, roperand);
			}
			default:
			{
				return this.evaluator.get_symbol_factory().parse_to_condition(operand, false);
			}
			}
		}
		else {
			return this.evaluator.get_symbol_factory().parse_to_condition(operand, false);
		}
	}
	/**
	 * @param operand
	 * @return	compute &operand based on:
	 * 			1. *expression 	--> expression
	 * 			2. otherwise	-->	&operand
	 * @throws Exception
	 */
	protected SymbolExpression compute_address_of(SymbolExpression operand) throws Exception {
		if(operand.is_reference()) {
			if(operand instanceof SymbolUnaryExpression) {
				if(((SymbolUnaryExpression) operand).get_operator().get_operator() == COperator.dereference) {
					return ((SymbolUnaryExpression) operand).get_operand();
				}
				else {
					return this.evaluator.get_symbol_factory().new_address_of(operand);
				}
			}
			else {
				return this.evaluator.get_symbol_factory().new_address_of(operand);
			}
		}
		else {
			throw new IllegalArgumentException("Not a reference: " + operand);
		}
	}
	/**
	 * @param operand
	 * @return	compute *operand as:
	 * 			1. &expression 	--> expression
	 * 			2. otherwise	--> *operand
	 * @throws Exception
	 */
	protected SymbolExpression compute_dereference(SymbolExpression operand) throws Exception {
		if(operand instanceof SymbolUnaryExpression) {
			if(((SymbolUnaryExpression) operand).get_operator().get_operator() == COperator.address_of) {
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			else {
				return this.evaluator.get_symbol_factory().new_dereference(operand);
			}
		}
		else {
			return this.evaluator.get_symbol_factory().new_dereference(operand);
		}
	}
	/**
	 * @param type
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression compute_type_cast(CType type, SymbolExpression operand) throws Exception {
		if(operand instanceof SymbolConstant) {
			type = CTypeAnalyzer.get_value_type(type);
			if(type instanceof CBasicType) {
				switch(((CBasicType) type).get_tag()) {
				case c_bool:	return this.evaluator.get_symbol_factory().new_constant(((SymbolConstant) operand).get_bool());
				case c_char:
				case c_uchar:	return this.evaluator.get_symbol_factory().new_constant(((SymbolConstant) operand).get_char());
				case c_short:
				case c_ushort:
				case c_int:
				case c_uint:	return this.evaluator.get_symbol_factory().new_constant(((SymbolConstant) operand).get_int());
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:	return this.evaluator.get_symbol_factory().new_constant(((SymbolConstant) operand).get_long());
				case c_float:	return this.evaluator.get_symbol_factory().new_constant(((SymbolConstant) operand).get_float());
				case c_double:
				case c_ldouble:	return this.evaluator.get_symbol_factory().new_constant(((SymbolConstant) operand).get_double());
				default: 		return this.evaluator.get_symbol_factory().new_dereference(operand);
				}
			}
			else if(type instanceof CEnumType) {
				return this.evaluator.get_symbol_factory().new_constant(((SymbolConstant) operand).get_int());
			}
			else {
				return this.evaluator.get_symbol_factory().new_type_casting(type, operand);
			}
		}
		else {
			return this.evaluator.get_symbol_factory().new_type_casting(type, operand);
		}
	}

	/* special operation */
	/**
	 * @param elements
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression compute_initializer_list(Iterable<SymbolExpression> elements) throws Exception {
		ArrayList<Object> objects = new ArrayList<>();
		for(SymbolExpression element : elements) {
			objects.add(element);
		}
		return this.evaluator.get_symbol_factory().new_initializer_list(objects);
	}
	/**
	 * @param body
	 * @param field
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression compute_field_expression(SymbolExpression body, String field) throws Exception {
		return this.evaluator.get_symbol_factory().new_field_expression(body, field);
	}
	/**
	 * @param function
	 * @param arguments
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression compute_call_expression(SymbolExpression function, Iterable<SymbolExpression> arguments) throws Exception {
		ArrayList<Object> argument_objects = new ArrayList<>();
		for(SymbolExpression argument : arguments) argument_objects.add(argument);
		SymbolCallExpression call_expression = this.evaluator.get_symbol_factory().new_call_expression(function, argument_objects);
		if(this.evaluator.get_symbol_process() != null) {
			for(SymbolInvoker invoker : this.evaluator.get_symbol_process().get_invokers()) {
				SymbolExpression result = invoker.invoke(call_expression);
				if(result != null) return result;
			}
			return call_expression;
		}
		else {
			return call_expression;
		}
	}

	/* arithmetic accumulation (+ and -) */
	/**
	 * @param constant
	 * @return constant == 0
	 */
	private boolean is_zero_constant(SymbolConstant constant) {
		Object number = constant.get_number();
		if(number instanceof Long) {
			return ((Long) number).longValue() == 0L;
		}
		else {
			return ((Double) number).doubleValue() == 0.0;
		}
	}
	/**
	 * @param lconstant
	 * @param rconstant
	 * @return lconstant + rconstant
	 * @throws Exception
	 */
	private SymbolConstant compute_const_add(SymbolConstant lconstant, SymbolConstant rconstant) throws Exception {
		Object lnumber = lconstant.get_number(), rnumber = rconstant.get_number();
		if(lnumber instanceof Long) {
			long lvalue = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long rvalue = ((Long) rnumber).longValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue + rvalue);
			}
			else {
				double rvalue = ((Double) rnumber).doubleValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue + rvalue);
			}
		}
		else {
			double lvalue = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long rvalue = ((Long) rnumber).longValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue + rvalue);
			}
			else {
				double rvalue = ((Double) rnumber).doubleValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue + rvalue);
			}
		}
	}
	/**
	 * @param lconstant
	 * @param rconstant
	 * @return lconstant - rconstant
	 * @throws Exception
	 */
	private SymbolConstant compute_const_sub(SymbolConstant lconstant, SymbolConstant rconstant) throws Exception {
		Object lnumber = lconstant.get_number(), rnumber = rconstant.get_number();
		if(lnumber instanceof Long) {
			long lvalue = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long rvalue = ((Long) rnumber).longValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue - rvalue);
			}
			else {
				double rvalue = ((Double) rnumber).doubleValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue - rvalue);
			}
		}
		else {
			double lvalue = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long rvalue = ((Long) rnumber).longValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue - rvalue);
			}
			else {
				double rvalue = ((Double) rnumber).doubleValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue - rvalue);
			}
		}
	}
	/**
	 * @param expression	{+, -, otherwise}
	 * @param pos_operands	the collection to preserve positive parts of accumulation
	 * @param neg_operands	the collection to preserve negative parts of accumulation
	 * @throws Exception
	 */
	private void extend_operands_in_accumulation(SymbolExpression expression,
			List<SymbolExpression> pos_operands, List<SymbolExpression> neg_operands) throws Exception {
		if(expression instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.negative) {
				this.extend_operands_in_accumulation(((SymbolUnaryExpression) expression).get_operand(), neg_operands, pos_operands);
			}
			else {
				pos_operands.add(expression);
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.arith_add) {
				this.extend_operands_in_accumulation(((SymbolBinaryExpression) expression).get_loperand(), pos_operands, neg_operands);
				this.extend_operands_in_accumulation(((SymbolBinaryExpression) expression).get_roperand(), pos_operands, neg_operands);
			}
			else if(operator == COperator.arith_sub) {
				this.extend_operands_in_accumulation(((SymbolBinaryExpression) expression).get_loperand(), pos_operands, neg_operands);
				this.extend_operands_in_accumulation(((SymbolBinaryExpression) expression).get_roperand(), neg_operands, pos_operands);
			}
			else {
				pos_operands.add(expression);
			}
		}
		else {
			pos_operands.add(expression);
		}
	}
	/**
	 * @param operands
	 * @param variables the collection to preserve the non-constant expressions
	 * @return constant additional under considerations
	 * @throws Exception
	 */
	private SymbolConstant update_operands_in_accumulation(
			Iterable<SymbolExpression> operands, List<SymbolExpression> variables) throws Exception {
		SymbolConstant constant = this.evaluator.get_symbol_factory().new_constant(0L);
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				constant = this.compute_const_add(constant, (SymbolConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		return constant;
	}
	/**
	 * remove the common part between positive and negative parts
	 * @param pos_operands
	 * @param neg_operands
	 * @throws Exception
	 */
	private void digest_operands_in_accumulation(List<SymbolExpression> pos_operands, List<SymbolExpression> neg_operands) throws Exception {
		SymbolExpression pos_remove, neg_remove;

		/* repeat until none of operands matched between */
		do {
			/* 1. initialize the operands to be removed */
			pos_remove = null;
			neg_remove = null;

			/* 2. find the first matched pairs */
			for(SymbolExpression pos_operand : pos_operands) {
				for(SymbolExpression neg_operand : neg_operands) {
					if(pos_operand.equals(neg_operand)) {
						pos_remove = pos_operand;
						neg_remove = neg_operand;
						break;
					}
				}
				if(pos_remove != null) { break; }
			}

			/* 3. removed the matched pair from operands */
			if(pos_remove != null) {
				pos_operands.remove(pos_remove);
				neg_operands.remove(neg_remove);
			}
		} while(pos_remove != null);
	}
	/**
	 * @param data_type
	 * @param constant
	 * @param pos_operands
	 * @param neg_operands
	 * @return	construct the standard structure of accumulation based on following rules:
	 * 			(1)	IF 	pos_operands is empty && neg_operands is empty:
	 * 					return constant;
	 * 			(2) EIF	pos_operands is empty && neg_operands is not empty:
	 * 					if constant == 0:
	 * 						return -(n1 + n2 + n3 + ... + nk)
	 * 					else:
	 * 						return constant - (n1 + n2 + n3 + ... + nk)
	 * 			(3)	EIF pos_operands is not empty && neg_operands is empty:
	 * 					if constant == 0:
	 * 						return (p1 + p2 + p3 + ... + pk)
	 * 					else:
	 * 						return constant + p1 + p2 + ... + pk
	 * 			(4) ELSE:
	 * 					if constant == 0:
	 * 						return (p1 + p2 + p3 + ... + pk) - (n1 + n2 + n3 + ... + nk)
	 * 					else:
	 * 						return (constant + p1 + p2 + p3 + ... + pk) - (n1 + n2 + n3 + ... + nk)
	 * @throws Exception
	 */
	private SymbolExpression integrate_in_accumulation(CType data_type, SymbolConstant constant,
			Iterable<SymbolExpression> pos_operands, Iterable<SymbolExpression> neg_operands) throws Exception {
		/* construct accumulated parts for both positive and negative operands */
		SymbolExpression pos_expression = null, neg_expression = null;
		for(SymbolExpression pos_operand : pos_operands) {
			if(pos_expression == null) {
				pos_expression = pos_operand;
			}
			else {
				pos_expression = this.evaluator.get_symbol_factory().new_arith_add(data_type, pos_expression, pos_operand);
			}
		}
		for(SymbolExpression neg_operand : neg_operands) {
			if(neg_expression == null) {
				neg_expression = neg_operand;
			}
			else {
				neg_expression = this.evaluator.get_symbol_factory().new_arith_add(data_type, neg_expression, neg_operand);
			}
		}

		/* standard construction */
		if(pos_expression == null) {
			if(neg_expression == null) {
				return constant;
			}
			else {
				if(this.is_zero_constant(constant)) {
					return this.evaluator.get_symbol_factory().new_arith_neg(neg_expression);
				}
				else {
					return this.evaluator.get_symbol_factory().new_arith_sub(data_type, constant, neg_expression);
				}
			}
		}
		else {
			if(neg_expression == null) {
				if(this.is_zero_constant(constant)) {
					return pos_expression;
				}
				else {
					return this.evaluator.get_symbol_factory().new_arith_add(data_type, pos_expression, constant);
				}
			}
			else {
				if(this.is_zero_constant(constant)) {
					return this.evaluator.get_symbol_factory().new_arith_sub(data_type, pos_expression, neg_expression);
				}
				else {
					pos_expression = this.evaluator.get_symbol_factory().new_arith_add(data_type, pos_expression, constant);
					return this.evaluator.get_symbol_factory().new_arith_sub(data_type, pos_expression, neg_expression);
				}
			}
		}
	}
	/**
	 * @param expression either x + y or x - y as accumulation category
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression compute_arith_add_or_sub(SymbolExpression expression) throws Exception {
		/* 1. extend the operands into buffers */
		List<SymbolExpression> pos_operands = new ArrayList<>();
		List<SymbolExpression> neg_operands = new ArrayList<>();
		this.extend_operands_in_accumulation(expression, pos_operands, neg_operands);

		/* 2. update the constant and variables in operands list */
		List<SymbolExpression> pos_variables = new ArrayList<>();
		List<SymbolExpression> neg_variables = new ArrayList<>();
		SymbolConstant lconstant = this.update_operands_in_accumulation(pos_operands, pos_variables);
		SymbolConstant rconstant = this.update_operands_in_accumulation(neg_operands, neg_variables);


		/* 3. simplify the positive and negative variables */
		this.digest_operands_in_accumulation(pos_variables, neg_variables);
		SymbolConstant constant = this.compute_const_sub(lconstant, rconstant);
		pos_operands = pos_variables; neg_operands = neg_variables;

		/* 4. construct accumulation format */
		return this.integrate_in_accumulation(expression.get_data_type(), constant, pos_operands, neg_operands);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand + roperand
	 * @throws Exception
	 */
	protected SymbolExpression compute_arith_add(CType data_type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		return this.compute_arith_add_or_sub(this.evaluator.get_symbol_factory().new_arith_add(data_type, loperand, roperand));
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand - roperand
	 * @throws Exception
	 */
	protected SymbolExpression compute_arith_sub(CType data_type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		return this.compute_arith_add_or_sub(this.evaluator.get_symbol_factory().new_arith_sub(data_type, loperand, roperand));
	}

	/* arithmetic production (* and /) */
	/**
	 * @param constant
	 * @return constant == +1
	 */
	private boolean is_pone_constant(SymbolConstant constant) {
		Object number = constant.get_number();
		if(number instanceof Long) {
			return ((Long) number).longValue() == 1L;
		}
		else {
			return ((Double) number).doubleValue() == 1.0;
		}
	}
	/**
	 * @param constant
	 * @return constant == -1
	 */
	private boolean is_none_constant(SymbolConstant constant) {
		Object number = constant.get_number();
		if(number instanceof Long) {
			return ((Long) number).longValue() == -1L;
		}
		else {
			return ((Double) number).doubleValue() == -1.0;
		}
	}
	/**
	 * @param lconstant
	 * @param rconstant
	 * @return lconstant * rconstant
	 * @throws Exception
	 */
	private SymbolConstant compute_const_mul(SymbolConstant lconstant, SymbolConstant rconstant) throws Exception {
		Object lnumber = lconstant.get_number(), rnumber = rconstant.get_number();
		if(lnumber instanceof Long) {
			long lvalue = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long rvalue = ((Long) rnumber).longValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue * rvalue);
			}
			else {
				double rvalue = ((Double) rnumber).doubleValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue * rvalue);
			}
		}
		else {
			double lvalue = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long rvalue = ((Long) rnumber).longValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue * rvalue);
			}
			else {
				double rvalue = ((Double) rnumber).doubleValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue * rvalue);
			}
		}
	}
	/**
	 * @param expression
	 * @param did_operands 	to preserve the operands in divided part
	 * @param div_operands	to preserve the operands in divisor part
	 * @throws Exception
	 */
	private void extend_operands_in_production(SymbolExpression expression,
			List<SymbolExpression> did_operands, List<SymbolExpression> div_operands) throws Exception {
		if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.arith_mul) {
				this.extend_operands_in_production(((SymbolBinaryExpression) expression).get_loperand(), did_operands, div_operands);
				this.extend_operands_in_production(((SymbolBinaryExpression) expression).get_roperand(), did_operands, div_operands);
			}
			else if(operator == COperator.arith_div) {
				this.extend_operands_in_production(((SymbolBinaryExpression) expression).get_loperand(), did_operands, div_operands);
				this.extend_operands_in_production(((SymbolBinaryExpression) expression).get_roperand(), div_operands, did_operands);
			}
			else {
				did_operands.add(expression);
			}
		}
		else {
			did_operands.add(expression);
		}
	}
	/**
	 * @param operands
	 * @param variables to preserve the non-constant part of the expression
	 * @return constant derived from production operands
	 * @throws Exception
	 */
	private SymbolConstant update_operands_in_production(Iterable<SymbolExpression> operands, List<SymbolExpression> variables) throws Exception {
		SymbolConstant constant = this.evaluator.get_symbol_factory().new_constant(1L);
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				constant = this.compute_const_mul(constant, (SymbolConstant) operand);
			}
			else if(operand instanceof SymbolUnaryExpression) {
				COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
				if(operator == COperator.negative) {
					variables.add(((SymbolUnaryExpression) operand).get_operand());
					constant = this.compute_const_mul(constant, this.evaluator.get_symbol_factory().new_constant(-1));
				}
				else {
					variables.add(operand);
				}
			}
			else {
				variables.add(operand);
			}
		}
		return constant;
	}
	/**
	 * @param pos_operands
	 * @param neg_operands
	 * @throws Exception
	 */
	private void digest_operands_in_production(List<SymbolExpression> did_operands, List<SymbolExpression> div_operands) throws Exception {
		SymbolExpression did_remove, div_remove;

		/* repeat until none of operands matched between */
		do {
			/* 1. initialize the operands to be removed */
			did_remove = null;
			div_remove = null;

			/* 2. find the first matched pairs */
			for(SymbolExpression did_operand : did_operands) {
				for(SymbolExpression div_operand : div_operands) {
					if(did_operand.equals(div_operand)) {
						did_remove = did_operand;
						div_remove = div_operand;
						break;
					}
				}
				if(did_remove != null) { break; }
			}

			/* 3. removed the matched pair from operands */
			if(did_remove != null) {
				did_operands.remove(did_remove);
				div_operands.remove(div_remove);
			}
		} while(did_remove != null);
	}
	/**
	 * @param x
	 * @param y
	 * @return the greatest common divisor between x and y
	 */
	private long gcd(long x, long y) {
		x = Math.abs(x);
		y = Math.abs(y);
		if(x < y) {
			long temp = x;
			x = y;
			y = temp;
		}
		while(x % y != 0) {
			long r = x % y;
			x = y;
			y = r;
		}
		return y;
	}
	/**
	 * @param data_type
	 * @param did_constant
	 * @param div_constant
	 * @return [did_constant, div_constant]
	 * @throws Exception
	 */
	private SymbolConstant[] translate_constants_in_production(CType data_type, SymbolConstant did_constant, SymbolConstant div_constant) throws Exception {
		data_type = CTypeAnalyzer.get_value_type(data_type);
		if(CTypeAnalyzer.is_real(data_type)) {
			double did_value = did_constant.get_double();
			double div_value = div_constant.get_double();
			did_constant = this.evaluator.get_symbol_factory().new_constant(did_value / div_value);
			div_constant = this.evaluator.get_symbol_factory().new_constant(1L);
		}
		else {
			long did_value = did_constant.get_long();
			long div_value = div_constant.get_long();
			long great_common_divisor = this.gcd(did_value, div_value);
			did_value = did_value / great_common_divisor;
			div_value = div_value / great_common_divisor;
			if(did_value < 0) {
				if(div_value < 0) {
					did_value = Math.abs(did_value);
					div_value = Math.abs(div_value);
				}
				else {
					did_value = -Math.abs(did_value);
					div_value = Math.abs(div_value);
				}
			}
			else {
				if(div_value < 0) {
					did_value = -Math.abs(did_value);
					div_value = Math.abs(div_value);
				}
				else {
					did_value = Math.abs(did_value);
					div_value = Math.abs(div_value);
				}
			}
			did_constant = this.evaluator.get_symbol_factory().new_constant(did_value);
			div_constant = this.evaluator.get_symbol_factory().new_constant(div_value);
		}
		return new SymbolConstant[] { did_constant, div_constant };
	}
	/**
	 * @param data_type
	 * @param constant
	 * @param operands
	 * @return const * x1 * x2 * ... * xn
	 * @throws Exception
	 */
	private SymbolExpression integrate_operands_in_production(CType data_type, SymbolConstant constant, Iterable<SymbolExpression> operands) throws Exception {
		SymbolExpression expression = null;
		for(SymbolExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = this.evaluator.get_symbol_factory().new_arith_mul(data_type, expression, operand);
			}
		}
		if(expression == null) {
			return constant;
		}
		else if(this.is_pone_constant(constant)) {
			return expression;
		}
		else if(this.is_none_constant(constant)) {
			return this.evaluator.get_symbol_factory().new_arith_neg(expression);
		}
		else {
			return this.evaluator.get_symbol_factory().new_arith_mul(data_type, constant, expression);
		}
	}
	/**
	 * @param data_type
	 * @param did_constant
	 * @param did_operands
	 * @param div_constant
	 * @param div_operands
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression integrate_in_production(CType data_type,
			SymbolConstant did_constant, Iterable<SymbolExpression> did_operands,
			SymbolConstant div_constant, Iterable<SymbolExpression> div_operands) throws Exception {
		if(this.is_zero_constant(div_constant)) {
			throw new ArithmeticException("Divided by zero at: " + div_constant);
		}
		else if(this.is_zero_constant(did_constant)) {
			return did_constant;
		}
		else {
			SymbolExpression did_expression = this.integrate_operands_in_production(data_type, did_constant, did_operands);
			SymbolExpression div_expression = this.integrate_operands_in_production(data_type, div_constant, div_operands);

			if(div_expression instanceof SymbolConstant) {
				if(this.is_pone_constant((SymbolConstant) div_expression)) {
					return did_expression;
				}
				else if(this.is_none_constant((SymbolConstant) div_expression)) {
					return this.evaluator.get_symbol_factory().new_arith_neg(did_expression);
				}
			}

			return this.evaluator.get_symbol_factory().new_arith_div(data_type, did_expression, div_expression);
		}
	}
	/**
	 * @param expression x * y or x / y
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression compute_arith_mul_and_div(SymbolExpression expression) throws Exception {
		/* 1. extends the operands in the production */
		List<SymbolExpression> did_operands = new ArrayList<>();
		List<SymbolExpression> div_operands = new ArrayList<>();
		this.extend_operands_in_production(expression, did_operands, div_operands);

		/* 2. update the variables and collect constant */
		List<SymbolExpression> did_variables = new ArrayList<>();
		List<SymbolExpression> div_variables = new ArrayList<>();
		SymbolConstant did_constant = this.update_operands_in_production(did_operands, did_variables);
		SymbolConstant div_constant = this.update_operands_in_production(div_operands, div_variables);

		/* 3. standardize the constants and variables */
		this.digest_operands_in_production(did_variables, div_variables);
		SymbolConstant[] constants = this.translate_constants_in_production(
					expression.get_data_type(), did_constant, div_constant);
		did_constant = constants[0];
		div_constant = constants[1];
		did_operands = did_variables;
		div_operands = div_variables;

		/* 4. integrate the production */
		return this.integrate_in_production(expression.get_data_type(), did_constant, did_operands, div_constant, div_operands);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand * roperand
	 * @throws Exception
	 */
	protected SymbolExpression compute_arith_mul(CType data_type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		return this.compute_arith_mul_and_div(this.evaluator.get_symbol_factory().new_arith_mul(data_type, loperand, roperand));
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand / roperand
	 * @throws Exception
	 */
	protected SymbolExpression compute_arith_div(CType data_type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		return this.compute_arith_mul_and_div(this.evaluator.get_symbol_factory().new_arith_div(data_type, loperand, roperand));
	}

	/* arithmetic remainder (%) */
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand % roperand
	 * @throws Exception
	 */
	protected SymbolExpression compute_arith_mod(CType data_type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(roperand instanceof SymbolConstant) {
			long rvalue = ((SymbolConstant) roperand).get_long().longValue();
			if(rvalue == 0L) {
				throw new ArithmeticException("Remainded by zero for " + loperand + " % " + roperand);
			}
			else if(rvalue == 1L || rvalue == -1L) {
				return this.evaluator.get_symbol_factory().new_constant(0);
			}
		}

		if(loperand instanceof SymbolConstant) {
			long lvalue = ((SymbolConstant) loperand).get_long().longValue();
			if(lvalue == 0L) {
				return this.evaluator.get_symbol_factory().new_constant(0);
			}
			else if(lvalue == 1) {
				return this.evaluator.get_symbol_factory().new_constant(1);
			}
		}

		if(loperand instanceof SymbolConstant) {
			if(roperand instanceof SymbolConstant) {
				long lvalue = ((SymbolConstant) loperand).get_long().longValue();
				long rvalue = ((SymbolConstant) roperand).get_long().longValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue % rvalue);
			}
		}

		return this.evaluator.get_symbol_factory().new_arith_mod(data_type, loperand, roperand);
	}

	/* bitwise and by & */
	/**
	 * @param lconstant
	 * @param rconstant
	 * @return lconstant & rconstant
	 */
	private SymbolConstant compute_const_bitws_and(SymbolConstant lconstant, SymbolConstant rconstant) throws Exception {
		long lvalue = lconstant.get_long().longValue(), rvalue = rconstant.get_long().longValue();
		return this.evaluator.get_symbol_factory().new_constant(lvalue & rvalue);
	}
	/**
	 * @param expression
	 * @param operands	to preserve the operands for bitwise-and (&)
	 * @throws Exception
	 */
	private void extend_operands_in_bitws_and(SymbolExpression expression, List<SymbolExpression> operands) throws Exception {
		if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.bit_and) {
				this.extend_operands_in_bitws_and(((SymbolBinaryExpression) expression).get_loperand(), operands);
				this.extend_operands_in_bitws_and(((SymbolBinaryExpression) expression).get_roperand(), operands);
			}
			else {
				operands.add(expression);
			}
		}
		else {
			operands.add(expression);
		}
	}
	/**
	 * @param operands
	 * @param variables to preserve non-constant operands in &
	 * @return to accumulate the constants in bitwise-and(&)
	 * @throws Exception
	 */
	private SymbolConstant update_operands_in_bitws_and(Iterable<SymbolExpression> operands, List<SymbolExpression> variables) throws Exception {
		SymbolConstant constant = this.evaluator.get_symbol_factory().new_constant(-1L);
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				constant = this.compute_const_bitws_and(constant, (SymbolConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		return constant;
	}
	/**
	 * remove duplicated operands for bitwise-operations
	 * @param operands
	 * @throws Exception
	 */
	private void digest_operands_in_bitws_and(List<SymbolExpression> operands) throws Exception {
		SymbolExpression removed_operand;
		do {
			removed_operand = null;

			for(int i = 0; i < operands.size(); i++) {
				for(int j = i + 1; j < operands.size(); j++) {
					if(operands.get(i).equals(operands.get(j))) {
						removed_operand = operands.get(i);
						break;
					}
				}
				if(removed_operand != null) { break; }
			}

			if(removed_operand != null) {
				operands.remove(removed_operand);
			}
		} while(removed_operand != null);
	}
	/**
	 * @param data_type
	 * @param constant
	 * @param operands
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression integrate_operands_in_bitws_and(CType data_type,
			SymbolConstant constant, Iterable<SymbolExpression> operands) throws Exception {
		SymbolExpression expression = null;
		for(SymbolExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = this.evaluator.get_symbol_factory().new_bitws_and(data_type, expression, operand);
			}
		}

		if(expression == null) {
			return constant;
		}
		else if(this.is_zero_constant(constant)) {
			return constant;
		}
		else if(this.is_none_constant(constant)) {
			return expression;
		}
		else {
			return this.evaluator.get_symbol_factory().new_bitws_and(data_type, constant, expression);
		}
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand & roperand
	 * @throws Exception
	 */
	protected SymbolExpression compute_bitws_and(CType data_type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		/* 1. extend operands */
		List<SymbolExpression> operands = new ArrayList<>();
		this.extend_operands_in_bitws_and(loperand, operands);
		this.extend_operands_in_bitws_and(roperand, operands);

		/* 2. update the operands into variables and constant */
		List<SymbolExpression> variables = new ArrayList<>();
		SymbolConstant constant = this.update_operands_in_bitws_and(operands, variables);
		this.digest_operands_in_bitws_and(variables);
		operands = variables;

		/* 3. integrate operands into expression (&) */
		return this.integrate_operands_in_bitws_and(data_type, constant, operands);
	}

	/* bitwise ior by | */
	/**
	 * @param lconstant
	 * @param rconstant
	 * @return lconstant | rconstant
	 * @throws Exception
	 */
	private SymbolConstant compute_const_bitws_ior(SymbolConstant lconstant, SymbolConstant rconstant) throws Exception {
		long lvalue = lconstant.get_long().longValue(), rvalue = rconstant.get_long().longValue();
		return this.evaluator.get_symbol_factory().new_constant(lvalue | rvalue);
	}
	/**
	 * @param expression
	 * @param operands	to preserve the operands in bitwise-ior(|)
	 * @throws Exception
	 */
	private void extend_operands_in_bitws_ior(SymbolExpression expression, List<SymbolExpression> operands) throws Exception {
		if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.bit_or) {
				this.extend_operands_in_bitws_ior(((SymbolBinaryExpression) expression).get_loperand(), operands);
				this.extend_operands_in_bitws_ior(((SymbolBinaryExpression) expression).get_roperand(), operands);
			}
			else {
				operands.add(expression);
			}
		}
		else {
			operands.add(expression);
		}
	}
	/**
	 * @param operands
	 * @param variables to preserve non-const operands
	 * @return to accumulate constants in |
	 * @throws Exception
	 */
	private SymbolConstant update_operands_in_bitws_ior(Iterable<SymbolExpression> operands, List<SymbolExpression> variables) throws Exception {
		SymbolConstant constant = this.evaluator.get_symbol_factory().new_constant(0L);
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				constant = this.compute_const_bitws_ior(constant, (SymbolConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		return constant;
	}
	/**
	 * @param operands
	 * @throws Exception
	 */
	private void digest_operands_in_bitws_ior(List<SymbolExpression> operands) throws Exception {
		SymbolExpression removed_operand;
		do {
			removed_operand = null;

			for(int i = 0; i < operands.size(); i++) {
				for(int j = i + 1; j < operands.size(); j++) {
					if(operands.get(i).equals(operands.get(j))) {
						removed_operand = operands.get(i);
						break;
					}
				}
				if(removed_operand != null) { break; }
			}

			if(removed_operand != null) {
				operands.remove(removed_operand);
			}
		} while(removed_operand != null);
	}
	/**
	 * @param data_type
	 * @param constant
	 * @param operands
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression integrate_operands_in_bitws_ior(CType data_type,
			SymbolConstant constant, Iterable<SymbolExpression> operands) throws Exception {
		SymbolExpression expression = null;
		for(SymbolExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = this.evaluator.get_symbol_factory().new_bitws_ior(data_type, expression, operand);
			}
		}

		if(expression == null) {
			return constant;
		}
		else if(this.is_zero_constant(constant)) {
			return expression;
		}
		else if(this.is_none_constant(constant)) {
			return constant;
		}
		else {
			return this.evaluator.get_symbol_factory().new_bitws_ior(data_type, constant, expression);
		}
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand | roperand
	 * @throws Exception
	 */
	protected SymbolExpression compute_bitws_ior(CType data_type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		/* 1. extend operands */
		List<SymbolExpression> operands = new ArrayList<>();
		this.extend_operands_in_bitws_ior(loperand, operands);
		this.extend_operands_in_bitws_ior(roperand, operands);

		/* 2. update the operands into variables and constant */
		List<SymbolExpression> variables = new ArrayList<>();
		SymbolConstant constant = this.update_operands_in_bitws_ior(operands, variables);
		this.digest_operands_in_bitws_ior(variables);
		operands = variables;

		/* 3. integrate operands into expression (&) */
		return this.integrate_operands_in_bitws_ior(data_type, constant, operands);
	}

	/* bitwise xor by ^ */
	/**
	 * @param lconstant
	 * @param rconstant
	 * @return lconstant ^ rconstant
	 * @throws Exception
	 */
	private SymbolConstant compute_const_bitws_xor(SymbolConstant lconstant, SymbolConstant rconstant) throws Exception {
		long lvalue = lconstant.get_long().longValue(), rvalue = rconstant.get_long().longValue();
		return this.evaluator.get_symbol_factory().new_constant(lvalue ^ rvalue);
	}
	/**
	 * @param expression
	 * @param operands
	 * @throws Exception
	 */
	private void extend_operands_in_bitws_xor(SymbolExpression expression, List<SymbolExpression> operands) throws Exception {
		if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.bit_xor) {
				this.extend_operands_in_bitws_xor(((SymbolBinaryExpression) expression).get_loperand(), operands);
				this.extend_operands_in_bitws_xor(((SymbolBinaryExpression) expression).get_roperand(), operands);
			}
			else {
				operands.add(expression);
			}
		}
		else {
			operands.add(expression);
		}
	}
	/**
	 * @param operands
	 * @param variables
	 * @return
	 * @throws Exception
	 */
	private SymbolConstant update_operands_in_bitws_xor(Iterable<SymbolExpression> operands, List<SymbolExpression> variables) throws Exception {
		SymbolConstant constant = this.evaluator.get_symbol_factory().new_constant(0L);
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				constant = this.compute_const_bitws_xor(constant, (SymbolConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		return constant;
	}
	/**
	 * @param operands
	 * @throws Exception
	 */
	private void digest_operands_in_bitws_xor(List<SymbolExpression> operands) throws Exception {
		SymbolExpression removed_operand;
		do {
			removed_operand = null;
			int removed_i = -1, removed_j = -1;

			for(int i = 0; i < operands.size(); i++) {
				for(int j = i + 1; j < operands.size(); j++) {
					if(operands.get(i).equals(operands.get(j))) {
						removed_operand = operands.get(i);
						removed_i = i; removed_j = j;
						break;
					}
				}
				if(removed_operand != null) { break; }
			}

			if(removed_operand != null) {
				operands.remove(removed_j);
				operands.remove(removed_i);
			}
		} while(removed_operand != null);
	}
	/**
	 * @param data_type
	 * @param constant
	 * @param operands
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression integrate_operands_in_bitws_xor(CType data_type,
			SymbolConstant constant, Iterable<SymbolExpression> operands) throws Exception {
		SymbolExpression expression = null;
		for(SymbolExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = this.evaluator.get_symbol_factory().new_bitws_xor(data_type, expression, operand);
			}
		}

		if(expression == null) {
			return constant;
		}
		else if(this.is_zero_constant(constant)) {
			return expression;
		}
		else if(this.is_none_constant(constant)) {
			return this.evaluator.get_symbol_factory().new_bitws_rsv(expression);
		}
		else {
			return this.evaluator.get_symbol_factory().new_bitws_xor(data_type, constant, expression);
		}
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return loperand ^ roperand
	 * @throws Exception
	 */
	protected SymbolExpression compute_bitws_xor(CType data_type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		/* 1. extend operands */
		List<SymbolExpression> operands = new ArrayList<>();
		this.extend_operands_in_bitws_xor(loperand, operands);
		this.extend_operands_in_bitws_xor(roperand, operands);

		/* 2. update the operands into variables and constant */
		List<SymbolExpression> variables = new ArrayList<>();
		SymbolConstant constant = this.update_operands_in_bitws_xor(operands, variables);
		this.digest_operands_in_bitws_xor(variables);
		operands = variables;

		/* 3. integrate operands into expression (&) */
		return this.integrate_operands_in_bitws_xor(data_type, constant, operands);
	}

	/* bitwise shifting (<<, >>) */
	/** the maximal limit of shifting operation **/
	private static final int MAX_SHIFT_WINDOW = 32;
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return	compute x << y based on:
	 * 			(1)	x == 0 --> 0
	 * 			(2) y == 0 --> x
	 * 			(3) constant
	 * 			(4) otherwise
	 * @throws Exception
	 */
	protected SymbolExpression compute_bitws_lsh(CType data_type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(loperand instanceof SymbolConstant) {
			long lvalue = ((SymbolConstant) loperand).get_long().longValue();
			if(lvalue == 0L) {
				return this.evaluator.get_symbol_factory().new_constant(0);
			}
		}

		if(roperand instanceof SymbolConstant) {
			long rvalue = ((SymbolConstant) roperand).get_long().longValue();
			if(rvalue == 0) {
				return loperand;
			}
			else if(rvalue >= MAX_SHIFT_WINDOW) {
				return this.evaluator.get_symbol_factory().new_constant(0);
			}
		}

		if(loperand instanceof SymbolConstant) {
			if(roperand instanceof SymbolConstant) {
				long lvalue = ((SymbolConstant) loperand).get_long().longValue();
				long rvalue = ((SymbolConstant) roperand).get_long().longValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue << rvalue);
			}
		}

		return this.evaluator.get_symbol_factory().new_bitws_lsh(data_type, loperand, roperand);
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return	compute x << y based on:
	 * 			(1)	x == 0 --> 0
	 * 			(2) y == 0 --> x
	 * 			(3) constant
	 * 			(4) otherwise
	 * @throws Exception
	 */
	protected SymbolExpression compute_bitws_rsh(CType data_type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(loperand instanceof SymbolConstant) {
			long lvalue = ((SymbolConstant) loperand).get_long().longValue();
			if(lvalue == 0L) {
				return this.evaluator.get_symbol_factory().new_constant(0);
			}
		}

		if(roperand instanceof SymbolConstant) {
			long rvalue = ((SymbolConstant) roperand).get_long().longValue();
			if(rvalue == 0) {
				return loperand;
			}
			else if(rvalue >= MAX_SHIFT_WINDOW) {
				return this.evaluator.get_symbol_factory().new_constant(0);
			}
		}

		if(loperand instanceof SymbolConstant) {
			if(roperand instanceof SymbolConstant) {
				long lvalue = ((SymbolConstant) loperand).get_long().longValue();
				long rvalue = ((SymbolConstant) roperand).get_long().longValue();
				return this.evaluator.get_symbol_factory().new_constant(lvalue >> rvalue);
			}
		}

		return this.evaluator.get_symbol_factory().new_bitws_rsh(data_type, loperand, roperand);
	}

	/* logical and by && */
	/**
	 * @param lconstant
	 * @param rconstant
	 * @return
	 * @throws Exception
	 */
	private SymbolConstant compute_const_logic_and(SymbolConstant lconstant, SymbolConstant rconstant) throws Exception {
		return this.evaluator.get_symbol_factory().new_constant(lconstant.get_bool() && rconstant.get_bool());
	}
	/**
	 * @param expression
	 * @param operands	to preserve the operands in logical conjunction
	 * @throws Exception
	 */
	private void extend_operands_in_logic_and(SymbolExpression expression, List<SymbolExpression> operands) throws Exception {
		if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.logic_and) {
				this.extend_operands_in_logic_and(((SymbolBinaryExpression) expression).get_loperand(), operands);
				this.extend_operands_in_logic_and(((SymbolBinaryExpression) expression).get_roperand(), operands);
			}
			else {
				operands.add(expression);
			}
		}
		else {
			operands.add(expression);
		}
	}
	/**
	 * @param operands
	 * @param variables
	 * @return
	 * @throws Exception
	 */
	private SymbolConstant update_operands_in_logic_and(Iterable<SymbolExpression> operands,
			List<SymbolExpression> variables) throws Exception {
		SymbolConstant constant = this.evaluator.get_symbol_factory().new_constant(true);
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				constant = this.compute_const_logic_and(constant, (SymbolConstant) operand);
			}
			else {
				variables.add(this.evaluator.get_symbol_factory().parse_to_condition(operand, true));
			}
		}
		return constant;
	}
	/**
	 * removed duplicated conditions from expression
	 * @param operands
	 * @throws Exception
	 */
	private void digest_operands_in_logic_and(List<SymbolExpression> operands) throws Exception {
		SymbolExpression removed_operand;
		do {
			removed_operand = null;

			for(int i = 0; i < operands.size(); i++) {
				for(int j = i + 1; j < operands.size(); j++) {
					if(operands.get(i).equals(operands.get(j))) {
						removed_operand = operands.get(i);
						break;
					}
				}
				if(removed_operand != null) { break; }
			}

			if(removed_operand != null) {
				operands.remove(removed_operand);
			}
		} while(removed_operand != null);
	}
	/**
	 * @param constant
	 * @param operands
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression integrate_operands_in_logic_and(SymbolConstant constant, Iterable<SymbolExpression> operands) throws Exception {
		if(constant.get_bool()) {
			SymbolExpression expression = null;
			for(SymbolExpression operand : operands) {
				if(expression == null) {
					expression = operand;
				}
				else {
					expression = this.evaluator.get_symbol_factory().new_logic_and(expression, operand);
				}
			}
			if(expression == null) {
				return this.evaluator.get_symbol_factory().new_constant(true);
			}
			else {
				return expression;
			}
		}
		else {
			return this.evaluator.get_symbol_factory().new_constant(false);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression compute_logic_and(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		/* extend operands in logical */
		List<SymbolExpression> operands = new ArrayList<>();
		this.extend_operands_in_logic_and(loperand, operands);
		this.extend_operands_in_logic_and(roperand, operands);

		/* update constant and variables */
		List<SymbolExpression> variables = new ArrayList<>();
		SymbolConstant constant = this.update_operands_in_logic_and(operands, variables);
		this.digest_operands_in_logic_and(variables);
		operands = variables;

		/* integrate on */	return this.integrate_operands_in_logic_and(constant, operands);
	}

	/* logical ior by || */
	/**
	 * @param lconstant
	 * @param rconstant
	 * @return lconstant || rconstant
	 * @throws Exception
	 */
	private SymbolConstant compute_const_logic_ior(SymbolConstant lconstant, SymbolConstant rconstant) throws Exception {
		return this.evaluator.get_symbol_factory().new_constant(lconstant.get_bool() || rconstant.get_bool());
	}
	/**
	 * @param expression
	 * @param operands
	 * @throws Exception
	 */
	private void extend_operands_in_logic_ior(SymbolExpression expression, List<SymbolExpression> operands) throws Exception {
		if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.logic_or) {
				this.extend_operands_in_logic_ior(((SymbolBinaryExpression) expression).get_loperand(), operands);
				this.extend_operands_in_logic_ior(((SymbolBinaryExpression) expression).get_roperand(), operands);
			}
			else {
				operands.add(expression);
			}
		}
		else {
			operands.add(expression);
		}
	}
	/**
	 * @param operands
	 * @param variables
	 * @return
	 * @throws Exception
	 */
	private SymbolConstant update_operands_in_logic_ior(Iterable<SymbolExpression> operands,
			List<SymbolExpression> variables) throws Exception {
		SymbolConstant constant = this.evaluator.get_symbol_factory().new_constant(false);
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				constant = this.compute_const_logic_ior(constant, (SymbolConstant) operand);
			}
			else {
				variables.add(this.evaluator.get_symbol_factory().parse_to_condition(operand, true));
			}
		}
		return constant;
	}
	/**
	 * @param operands
	 * @throws Exception
	 */
	private void digest_operands_in_logic_ior(List<SymbolExpression> operands) throws Exception {
		SymbolExpression removed_operand;
		do {
			removed_operand = null;

			for(int i = 0; i < operands.size(); i++) {
				for(int j = i + 1; j < operands.size(); j++) {
					if(operands.get(i).equals(operands.get(j))) {
						removed_operand = operands.get(i);
						break;
					}
				}
				if(removed_operand != null) { break; }
			}

			if(removed_operand != null) {
				operands.remove(removed_operand);
			}
		} while(removed_operand != null);
	}
	/**
	 * @param constant
	 * @param operands
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression integrate_operands_in_logic_ior(SymbolConstant constant, Iterable<SymbolExpression> operands) throws Exception {
		if(!constant.get_bool()) {
			SymbolExpression expression = null;
			for(SymbolExpression operand : operands) {
				if(expression == null) {
					expression = operand;
				}
				else {
					expression = this.evaluator.get_symbol_factory().new_logic_ior(expression, operand);
				}
			}
			if(expression == null) {
				return this.evaluator.get_symbol_factory().new_constant(false);
			}
			else {
				return expression;
			}
		}
		else {
			return this.evaluator.get_symbol_factory().new_constant(true);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression compute_logic_ior(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		/* extend operands in logical */
		List<SymbolExpression> operands = new ArrayList<>();
		this.extend_operands_in_logic_ior(loperand, operands);
		this.extend_operands_in_logic_ior(roperand, operands);

		/* update constant and variables */
		List<SymbolExpression> variables = new ArrayList<>();
		SymbolConstant constant = this.update_operands_in_logic_ior(operands, variables);
		this.digest_operands_in_logic_ior(variables);
		operands = variables;

		/* integrate on */	return this.integrate_operands_in_logic_ior(constant, operands);
	}

	/* relational expression part */
	/**
	 * @param loperand
	 * @param roperand
	 * @return	compute loperand < roperand based on:
	 * @throws Exception
	 */
	protected SymbolExpression compute_smaller_tn(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		SymbolExpression expression = this.compute_arith_sub(loperand.get_data_type(), loperand, roperand);
		if(expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) expression).get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				return this.evaluator.get_symbol_factory().new_constant(value < 0L);
			}
			else {
				double value = ((Double) number).doubleValue();
				return this.evaluator.get_symbol_factory().new_constant(value < 0.0);
			}
		}
		else {
			SymbolValueDomain domain = this.compute_domain(expression);
			if(domain != null) {
				if(!domain.is_minimal_infinite() && domain.get_minimal_value() >= 0) {
					return this.evaluator.get_symbol_factory().new_constant(false);
				}
				else if(!domain.is_maximal_infinite() && domain.get_maximal_value() < 0) {
					return this.evaluator.get_symbol_factory().new_constant(true);
				}
			}
			return this.evaluator.get_symbol_factory().new_smaller_tn(expression, 0);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return	compute loperand <= roperand based on:
	 * @throws Exception
	 */
	protected SymbolExpression compute_smaller_eq(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		SymbolExpression expression = this.compute_arith_sub(loperand.get_data_type(), loperand, roperand);
		if(expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) expression).get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				return this.evaluator.get_symbol_factory().new_constant(value <= 0L);
			}
			else {
				double value = ((Double) number).doubleValue();
				return this.evaluator.get_symbol_factory().new_constant(value <= 0.0);
			}
		}
		else {
			SymbolValueDomain domain = this.compute_domain(expression);
			if(domain != null) {
				if(!domain.is_minimal_infinite() && domain.get_minimal_value() > 0) {
					return this.evaluator.get_symbol_factory().new_constant(false);
				}
				else if(!domain.is_maximal_infinite() && domain.get_maximal_value() <= 0) {
					return this.evaluator.get_symbol_factory().new_constant(true);
				}
			}
			return this.evaluator.get_symbol_factory().new_smaller_eq(expression, 0);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return	compute loperand == roperand based on:
	 * @throws Exception
	 */
	protected SymbolExpression compute_equal_with(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		SymbolExpression expression = this.compute_arith_sub(loperand.get_data_type(), loperand, roperand);
		if(expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) expression).get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				return this.evaluator.get_symbol_factory().new_constant(value == 0L);
			}
			else {
				double value = ((Double) number).doubleValue();
				return this.evaluator.get_symbol_factory().new_constant(value == 0.0);
			}
		}
		else {
			return this.evaluator.get_symbol_factory().new_equal_with(expression, 0);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return	compute loperand != roperand based on:
	 * @throws Exception
	 */
	protected SymbolExpression compute_not_equals(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		SymbolExpression expression = this.compute_arith_sub(loperand.get_data_type(), loperand, roperand);
		if(expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) expression).get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				return this.evaluator.get_symbol_factory().new_constant(value != 0L);
			}
			else {
				double value = ((Double) number).doubleValue();
				return this.evaluator.get_symbol_factory().new_constant(value != 0.0);
			}
		}
		else {
			SymbolValueDomain domain = this.compute_domain(expression);
			if(domain != null) {
				if(!domain.is_minimal_infinite() && domain.get_minimal_value() > 0) {
					return this.evaluator.get_symbol_factory().new_constant(true);
				}
				else if(!domain.is_maximal_infinite() && domain.get_maximal_value() < 0) {
					return this.evaluator.get_symbol_factory().new_constant(true);
				}
			}
			return this.evaluator.get_symbol_factory().new_not_equals(expression, 0);
		}
	}

	/* value domain analysis */
	/**
	 * @param expression
	 * @return compute the domain of the expression or null if not numeric
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(expression instanceof SymbolIdentifier) {
			return this.compute_domain_identifier((SymbolIdentifier) expression);
		}
		else if(expression instanceof SymbolConstant) {
			return this.compute_domain_constant((SymbolConstant) expression);
		}
		else if(expression instanceof SymbolLiteral) {
			return this.compute_domain_literal((SymbolLiteral) expression);
		}
		else if(expression instanceof SymbolInitializerList) {
			return this.compute_domain_initializer_list((SymbolInitializerList) expression);
		}
		else if(expression instanceof SymbolFieldExpression) {
			return this.compute_domain_field_expression((SymbolFieldExpression) expression);
		}
		else if(expression instanceof SymbolCallExpression) {
			return this.compute_domain_call_expression((SymbolCallExpression) expression);
		}
		else if(expression instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) expression).get_operator().get_operator();
			switch(operator) {
			case negative:		return this.compute_domain_arith_neg((SymbolUnaryExpression) expression);
			case bit_not:		return this.compute_domain_bitws_rsv((SymbolUnaryExpression) expression);
			case logic_not:		return this.compute_domain_logic_not((SymbolUnaryExpression) expression);
			case address_of:	return this.compute_domain_address_of((SymbolUnaryExpression) expression);
			case dereference:	return this.compute_domain_dereference((SymbolUnaryExpression) expression);
			case assign:		return this.compute_domain_type_casting((SymbolUnaryExpression) expression);
			default: 			throw new IllegalArgumentException("Unsupport: " + operator);
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			switch(operator) {
			case arith_add:		return this.compute_domain_arith_add((SymbolBinaryExpression) expression);
			case arith_sub:		return this.compute_domain_arith_sub((SymbolBinaryExpression) expression);
			case arith_mul:		return this.compute_domain_arith_mul((SymbolBinaryExpression) expression);
			case arith_div:		return this.compute_domain_arith_div((SymbolBinaryExpression) expression);
			case arith_mod:
			case bit_and:
			case bit_or:
			case bit_xor:
			case left_shift:
			case righ_shift:
			case logic_and:
			case logic_or:
			case smaller_tn:
			case smaller_eq:
			case greater_tn:
			case greater_eq:
			case equal_with:
			case not_equals:	return SymbolValueDomain.domain_of_type(expression.get_data_type());
			default:			throw new IllegalArgumentException("Unsupport: " + operator);
			}
		}
		else {
			throw new IllegalArgumentException(expression.getClass().getSimpleName());
		}
	}
	/**
	 * @param expression
	 * @return compute by type
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_identifier(SymbolIdentifier expression) throws Exception {
		return SymbolValueDomain.domain_of_type(expression.get_data_type());
	}
	/**
	 * @param expression
	 * @return [const, const]
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_constant(SymbolConstant expression) throws Exception {
		return new SymbolValueDomain(expression.get_double(), expression.get_double());
	}
	/**
	 * @param expression
	 * @return null
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_literal(SymbolLiteral expression) throws Exception {
		return null;
	}
	/**
	 * @param expression
	 * @return null
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_initializer_list(SymbolInitializerList expression) throws Exception {
		return null;
	}
	/**
	 * @param expression
	 * @return by type
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_field_expression(SymbolFieldExpression expression) throws Exception {
		return SymbolValueDomain.domain_of_type(expression.get_data_type());
	}
	/**
	 * @param expression
	 * @return function-based specialization
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_call_expression(SymbolCallExpression expression) throws Exception {
		SymbolExpression function = expression.get_function();
		if(function instanceof SymbolIdentifier) {
			String name = function.generate_code(true);
			/* implement your function specialization here... */
			if(name.equals("sqrt") || name.equals("fabs") || name.equals("abs")
				|| name.equals("printf") || name.equals("fprintf")) {
				return new SymbolValueDomain(0.0, null);
			}
		}
		return SymbolValueDomain.domain_of_type(expression.get_data_type());
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_arith_neg(SymbolUnaryExpression expression) throws Exception {
		SymbolValueDomain sdomain = this.compute_domain(expression.get_operand());
		if(sdomain == null) {
			return SymbolValueDomain.domain_of_type(expression.get_data_type());
		}
		else {
			Double minimal_value, maximal_value;
			if(sdomain.is_maximal_infinite()) {
				minimal_value = null;
			}
			else {
				minimal_value = -sdomain.get_maximal_value();
			}
			if(sdomain.is_minimal_infinite()) {
				maximal_value = null;
			}
			else {
				maximal_value = -sdomain.get_minimal_value();
			}
			return new SymbolValueDomain(minimal_value, maximal_value);
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_bitws_rsv(SymbolUnaryExpression expression) throws Exception {
		SymbolValueDomain sdomain = this.compute_domain(expression.get_operand());
		if(sdomain == null) {
			return SymbolValueDomain.domain_of_type(expression.get_data_type());
		}
		else {
			long minimal_value, maximal_value;
			if(sdomain.is_minimal_infinite()) {
				minimal_value = Long.MIN_VALUE;
			}
			else {
				minimal_value = sdomain.get_minimal_value().longValue();
			}
			if(sdomain.is_maximal_infinite()) {
				maximal_value = Long.MAX_VALUE;
			}
			else {
				maximal_value = sdomain.get_maximal_value().longValue();
			}
			return new SymbolValueDomain((double) ~maximal_value, (double) ~minimal_value);
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_logic_not(SymbolUnaryExpression expression) throws Exception {
		SymbolValueDomain sdomain = this.compute_domain(expression.get_operand());
		if(sdomain == null) {
			return SymbolValueDomain.domain_of_type(CBasicTypeImpl.bool_type);
		}
		else {
			long minimal_value, maximal_value;
			if(sdomain.is_minimal_infinite()) {
				minimal_value = Long.MIN_VALUE;
			}
			else {
				minimal_value = sdomain.get_minimal_value().longValue();
			}
			if(sdomain.is_maximal_infinite()) {
				maximal_value = Long.MAX_VALUE;
			}
			else {
				maximal_value = sdomain.get_maximal_value().longValue();
			}

			boolean has_true, has_false;
			has_true = (0 >= minimal_value) && (0 <= maximal_value);
			has_false = (minimal_value != 0) || (maximal_value != 0);
			if(has_true) {
				if(has_false) {
					return new SymbolValueDomain(0.0, 1.0);
				}
				else {
					return new SymbolValueDomain(1.0, 1.0);
				}
			}
			else {
				if(has_false) {
					return new SymbolValueDomain(0.0, 0.0);
				}
				else {
					return null;
				}
			}
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_address_of(SymbolUnaryExpression expression) throws Exception {
		return new SymbolValueDomain((double) 0, (double) Long.MAX_VALUE);
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_dereference(SymbolUnaryExpression expression) throws Exception {
		return SymbolValueDomain.domain_of_type(expression.get_data_type());
	}
	/**
	 * @param expression
	 * @return by type
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_type_casting(SymbolUnaryExpression expression) throws Exception {
		SymbolValueDomain domain1 = this.compute_domain(expression.get_operand());
		SymbolValueDomain domain2 = SymbolValueDomain.domain_of_type(expression.get_data_type());
		if(domain1 == null) {
			return domain2;
		}
		else if(domain2 == null) {
			return domain1;
		}
		else {
			Double minimal_value1 = domain1.get_minimal_value();
			Double maximal_value1 = domain1.get_maximal_value();
			Double minimal_value2 = domain2.get_minimal_value();
			Double maximal_value2 = domain2.get_maximal_value();
			Double minimal_value, maximal_value;
			if(minimal_value1 == null) {
				if(minimal_value2 == null) {
					minimal_value = null;
				}
				else {
					minimal_value = minimal_value2;
				}
			}
			else {
				if(minimal_value2 == null) {
					minimal_value = minimal_value1;
				}
				else {
					minimal_value = Math.max(minimal_value1, minimal_value2);
				}
			}

			if(maximal_value1 == null) {
				if(maximal_value2 == null) {
					maximal_value = null;
				}
				else {
					maximal_value = maximal_value2;
				}
			}
			else {
				if(maximal_value2 == null) {
					maximal_value = maximal_value1;
				}
				else {
					maximal_value = Math.min(maximal_value1, maximal_value2);
				}
			}
			return new SymbolValueDomain(minimal_value, maximal_value);
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_arith_add(SymbolBinaryExpression expression) throws Exception {
		SymbolValueDomain ldomain = this.compute_domain(expression.get_loperand());
		SymbolValueDomain rdomain = this.compute_domain(expression.get_roperand());
		if(ldomain == null || rdomain == null) {
			return SymbolValueDomain.domain_of_type(expression.get_data_type());
		}
		else {
			Double minimal_value;
			if(ldomain.is_minimal_infinite() || rdomain.is_minimal_infinite()) {
				minimal_value = null;
			}
			else {
				minimal_value = ldomain.get_minimal_value() + rdomain.get_minimal_value();
			}

			Double maximal_value;
			if(ldomain.is_maximal_infinite() || rdomain.is_maximal_infinite()) {
				maximal_value = null;
			}
			else {
				maximal_value = ldomain.get_maximal_value() + rdomain.get_maximal_value();
			}

			return new SymbolValueDomain(minimal_value, maximal_value);
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_arith_sub(SymbolBinaryExpression expression) throws Exception {
		SymbolValueDomain ldomain = this.compute_domain(expression.get_loperand());
		SymbolValueDomain rdomain = this.compute_domain(expression.get_roperand());
		if(ldomain == null || rdomain == null) {
			return SymbolValueDomain.domain_of_type(expression.get_data_type());
		}
		else {
			Double minimal_value;
			if(ldomain.is_minimal_infinite() || rdomain.is_maximal_infinite()) {
				minimal_value = null;
			}
			else {
				minimal_value = ldomain.get_minimal_value() - rdomain.get_maximal_value();
			}

			Double maximal_value;
			if(ldomain.is_maximal_infinite() || rdomain.is_minimal_infinite()) {
				maximal_value = null;
			}
			else {
				maximal_value = ldomain.get_maximal_value() - rdomain.get_minimal_value();
			}

			return new SymbolValueDomain(minimal_value, maximal_value);
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_arith_mul(SymbolBinaryExpression expression) throws Exception {
		SymbolValueDomain ldomain = this.compute_domain(expression.get_loperand());
		SymbolValueDomain rdomain = this.compute_domain(expression.get_roperand());
		if(ldomain == null || rdomain == null) {
			return SymbolValueDomain.domain_of_type(expression.get_data_type());
		}
		else {
			Double minimal_value;
			if(ldomain.is_minimal_infinite() || rdomain.is_minimal_infinite()) {
				minimal_value = null;
			}
			else {
				minimal_value = ldomain.get_minimal_value() * rdomain.get_minimal_value();
			}

			Double maximal_value;
			if(ldomain.is_maximal_infinite() || rdomain.is_maximal_infinite()) {
				maximal_value = null;
			}
			else {
				maximal_value = ldomain.get_maximal_value() * rdomain.get_maximal_value();
			}

			return new SymbolValueDomain(minimal_value, maximal_value);
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolValueDomain compute_domain_arith_div(SymbolBinaryExpression expression) throws Exception {
		return SymbolValueDomain.domain_of_type(expression.get_data_type());
	}

}
