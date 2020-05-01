package com.jcsa.jcparse.lang.symb.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.SymBinaryExpression;
import com.jcsa.jcparse.lang.symb.SymConstant;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;
import com.jcsa.jcparse.lang.symb.SymMultiExpression;
import com.jcsa.jcparse.lang.symb.SymUnaryExpression;

/**
 * x + y		==> expand
 * @author yukimula
 *
 */
public class StandardSymEvaluator extends AbstractSymEvaluator {
	
	/**
	 * create an evaluator instance
	 * @return
	 */
	public static StandardSymEvaluator new_evaluator() { return new StandardSymEvaluator(); }
	
	/* translate the multi-expression into compressed format */
	/**
	 * divide the operands of expression into constants and variables
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private Map<Integer, List<SymExpression>> divide(SymMultiExpression expression) throws Exception {
		Map<Integer, List<SymExpression>> results = new HashMap<Integer, List<SymExpression>>();
		results.put(0, new ArrayList<SymExpression>()); results.put(1, new ArrayList<SymExpression>());
		for(int k = 0; k < expression.number_of_operands(); k++) {
			SymExpression operand = (SymExpression) expression.get_operand(k).copy();
			if(operand instanceof SymConstant) 
				results.get(0).add(operand);
			else results.get(1).add(operand);
		}
		return results;
	}
	/***
	 * perform computation to get summary
	 * @param constants
	 * @return
	 * @throws Exception
	 */
	private SymConstant merge_arith_add(Iterable<SymExpression> constants) throws Exception {
		double summary = 0; boolean is_floating = false;
		
		for(SymExpression operand : constants) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				double value = 0;
				if(constant.get_bool().booleanValue()) value++;
				summary = summary + value;
			}
			break;
			case c_long:
			{
				double value = constant.get_long().doubleValue();
				summary = summary + value;
			}
			break;
			case c_double:
			{
				double value = constant.get_double().doubleValue();
				summary = summary + value;
			}
			break;
			default: throw new IllegalArgumentException("Invalid constant: " + constant);
			}
		}
		
		if(is_floating)
			return SymFactory.new_constant(summary);
		else return SymFactory.new_constant((long) summary);
	}
	/***
	 * perform computation to get summary
	 * @param constants
	 * @return
	 * @throws Exception
	 */
	private SymConstant merge_arith_mul(Iterable<SymExpression> constants) throws Exception {
		double summary = 1; boolean is_floating = false;
		
		for(SymExpression operand : constants) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				double value = 0;
				if(constant.get_bool().booleanValue()) value++;
				summary = summary * value;
			}
			break;
			case c_long:
			{
				double value = constant.get_long().doubleValue();
				summary = summary * value;
			}
			break;
			case c_double:
			{
				double value = constant.get_double().doubleValue();
				summary = summary * value;
			}
			break;
			default: throw new IllegalArgumentException("Invalid constant: " + constant);
			}
		}
		
		if(is_floating)
			return SymFactory.new_constant(summary);
		else return SymFactory.new_constant((long) summary);
	}
	/***
	 * perform computation to get summary
	 * @param constants
	 * @return
	 * @throws Exception
	 */
	private SymConstant merge_bitws_and(Iterable<SymExpression> constants) throws Exception {
		long summary = ~0L; 
		
		for(SymExpression operand : constants) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				long value = 0;
				if(constant.get_bool().booleanValue()) value++;
				summary = summary & value;
			}
			break;
			case c_long:
			{
				long value = constant.get_long().longValue();
				summary = summary & value;
			}
			break;
			default: throw new IllegalArgumentException("Invalid constant: " + constant);
			}
		}
		
		return SymFactory.new_constant(summary);
	}
	/***
	 * perform computation to get summary
	 * @param constants
	 * @return
	 * @throws Exception
	 */
	private SymConstant merge_bitws_ior(Iterable<SymExpression> constants) throws Exception {
		long summary = 0L; 
		
		for(SymExpression operand : constants) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				long value = 0;
				if(constant.get_bool().booleanValue()) value++;
				summary = summary | value;
			}
			break;
			case c_long:
			{
				long value = constant.get_long().longValue();
				summary = summary | value;
			}
			break;
			default: throw new IllegalArgumentException("Invalid constant: " + constant);
			}
		}
		
		return SymFactory.new_constant(summary);
	}
	/***
	 * perform computation to get summary
	 * @param constants
	 * @return
	 * @throws Exception
	 */
	private SymConstant merge_bitws_xor(Iterable<SymExpression> constants) throws Exception {
		long summary = 0L; 
		
		for(SymExpression operand : constants) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				long value = 0;
				if(constant.get_bool().booleanValue()) value++;
				summary = summary ^ value;
			}
			break;
			case c_long:
			{
				long value = constant.get_long().longValue();
				summary = summary ^ value;
			}
			break;
			default: throw new IllegalArgumentException("Invalid constant: " + constant);
			}
		}
		
		return SymFactory.new_constant(summary);
	}
	/***
	 * perform computation to get summary
	 * @param constants
	 * @return
	 * @throws Exception
	 */
	private SymConstant merge_logic_and(Iterable<SymExpression> constants) throws Exception {
		boolean summary = true;
		
		for(SymExpression operand : constants) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				summary = summary && constant.get_bool().booleanValue();
			}
			break;
			case c_long:
			{
				summary = summary && (constant.get_long().longValue() != 0L);
			}
			break;
			case c_double:
			{
				summary = summary && (constant.get_double().doubleValue() != 0.0);
			}
			break;
			default: throw new IllegalArgumentException("Invalid constant: " + constant);
			}
		}
		
		return SymFactory.new_constant(summary);
	}
	/***
	 * perform computation to get summary
	 * @param constants
	 * @return
	 * @throws Exception
	 */
	private SymConstant merge_logic_ior(Iterable<SymExpression> constants) throws Exception {
		boolean summary = true;
		
		for(SymExpression operand : constants) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				summary = summary || constant.get_bool().booleanValue();
			}
			break;
			case c_long:
			{
				summary = summary || (constant.get_long().longValue() != 0L);
			}
			break;
			case c_double:
			{
				summary = summary || (constant.get_double().doubleValue() != 0.0);
			}
			break;
			default: throw new IllegalArgumentException("Invalid constant: " + constant);
			}
		}
		
		return SymFactory.new_constant(summary);
	}
	/**
	 * merge constants into one summary
	 * @param operator
	 * @param constants
	 * @return
	 * @throws Exception
	 */
	private SymConstant merge(COperator operator, Iterable<SymExpression> constants) throws Exception {
		switch(operator) {
		case arith_add:	return this.merge_arith_add(constants);
		case arith_mul:	return this.merge_arith_mul(constants);
		case bit_and:	return this.merge_bitws_and(constants);
		case bit_or:	return this.merge_bitws_ior(constants);
		case bit_xor:	return this.merge_bitws_xor(constants);
		case logic_and:	return this.merge_logic_and(constants);
		case logic_or:	return this.merge_logic_ior(constants);
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	/**
	 * whether the constant matches with the value
	 * @param constant
	 * @param value
	 * @return
	 * @throws Exception
	 */
	private boolean match_value(CConstant constant, long value) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:
		{
			long expr = 0L;
			if(constant.get_bool().booleanValue()) expr++;
			return expr == value;
		}
		case c_long:
		{
			return constant.get_long().longValue() == value;
		}
		case c_double:
		{
			return constant.get_double().doubleValue() == value;
		}
		default: throw new IllegalArgumentException("Invalid constant");
		}
	}
	/**
	 * whether the constant can decide the value of expression
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	private boolean decidable(COperator operator, SymConstant constant) throws Exception {
		switch(operator) {
		case arith_add:
		{
			return false;
		}
		case arith_mul:
		{
			return this.match_value(constant.get_constant(), 0);
		}
		case bit_and:
		{
			return this.match_value(constant.get_constant(), 0);
		}
		case bit_or:
		{
			return this.match_value(constant.get_constant(), ~0L);
		}
		case bit_xor:
		{
			return false;
		}
		case logic_and:
		{
			return this.match_value(constant.get_constant(), 0);
		}
		case logic_or:
		{
			return this.match_value(constant.get_constant(), 1);
		}
		default: throw new IllegalArgumentException("Invalid operator");
		}
	}
	/**
	 * whether the constant can be ignored in the expression
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	private boolean ignorable(COperator operator, SymConstant constant) throws Exception {
		switch(operator) {
		case arith_add:
		{
			return this.match_value(constant.get_constant(), 0);
		}
		case arith_mul:
		{
			return this.match_value(constant.get_constant(), 1);
		}
		case bit_and:
		{
			return this.match_value(constant.get_constant(), ~0L);
		}
		case bit_or:
		{
			return this.match_value(constant.get_constant(), 0);
		}
		case bit_xor:
		{
			return this.match_value(constant.get_constant(), 0);
		}
		case logic_and:
		{
			return this.match_value(constant.get_constant(), 1);
		}
		case logic_or:
		{
			return this.match_value(constant.get_constant(), 0);
		}
		default: throw new IllegalArgumentException("Invalid operator");
		}
	}
	/**
	 * ==> constant
	 * ==> variable
	 * ==> constant + {variable}+
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymExpression standardize(SymMultiExpression expression) throws Exception {
		/* 1. divide the operands into constants and variables */
		Map<Integer, List<SymExpression>> divide_set = this.divide(expression);
		List<SymExpression> constants = divide_set.get(0), variables = divide_set.get(1);
		
		/* 2. merge the constants into constant */
		SymConstant constant = this.merge(expression.get_operator(), constants);
		
		/* 3. generate the result */
		COperator operator = expression.get_operator();
		List<SymExpression> operands = new ArrayList<SymExpression>();
		if(variables.isEmpty() || this.decidable(operator, constant)) {
			operands.add(constant);
		}
		else {
			if(!this.ignorable(operator, constant)) operands.add(constant);
			for(SymExpression variable : variables) operands.add(variable);
		}
		
		/* 4. create result */
		if(operands.size() > 1) {
			SymMultiExpression result = SymFactory.new_multiple_expression(
					expression.get_data_type(), expression.get_operator());
			for(SymExpression operand : operands) result.add_operand(operand);
			return result;
		}
		else {
			return operands.get(0);
		}
	}
	/**
	 * extend the multiple expression by one-layer
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymExpression extend_multiple_expression(SymMultiExpression expression) throws Exception {
		List<SymExpression> operands = new ArrayList<SymExpression>();
		
		/* extend the expressions under the operands */
		for(int i = 0; i < expression.number_of_operands(); i++) {
			SymExpression operand = this.evaluate(expression.get_operand(i));
			if(operand instanceof SymMultiExpression) {
				SymMultiExpression moperand = (SymMultiExpression) operand;
				if(moperand.get_operator() == expression.get_operator()) {
					for(int j = 0; j < moperand.number_of_operands(); j++) {
						SymExpression moperand_j = (SymExpression) moperand.get_operand(j).copy();
						operands.add(moperand_j);
					}
				}
				else {
					operands.add(operand);
				}
			}
			else {
				operands.add(operand);
			}
		}
		
		SymMultiExpression result = SymFactory.new_multiple_expression(
				expression.get_data_type(), expression.get_operator());
		for(SymExpression operand : operands) result.add_operand(operand);
		return this.standardize(result);
	}
	
	@Override
	protected SymExpression arith_add_expression(SymMultiExpression expression) throws Exception {
		return this.extend_multiple_expression(expression);
	}

	@Override
	protected SymExpression arith_mul_expression(SymMultiExpression expression) throws Exception {
		return this.extend_multiple_expression(expression);
	}

	@Override
	protected SymExpression bitws_and_expression(SymMultiExpression expression) throws Exception {
		return this.extend_multiple_expression(expression);
	}

	@Override
	protected SymExpression bitws_ior_expression(SymMultiExpression expression) throws Exception {
		return this.extend_multiple_expression(expression);
	}

	@Override
	protected SymExpression bitws_xor_expression(SymMultiExpression expression) throws Exception {
		return this.extend_multiple_expression(expression);
	}

	@Override
	protected SymExpression logic_and_expression(SymMultiExpression expression) throws Exception {
		return this.extend_multiple_expression(expression);
	}

	@Override
	protected SymExpression logic_ior_expression(SymMultiExpression expression) throws Exception {
		return this.extend_multiple_expression(expression);
	}
	
	@Override
	protected SymExpression arith_sub_expression(SymBinaryExpression expression) throws Exception {
		SymExpression loperand = this.evaluate(expression.get_loperand());
		SymExpression roperand = this.evaluate(expression.get_roperand());
		
		roperand = SymFactory.new_unary_expression(
				roperand.get_data_type(), COperator.negative, roperand);
		roperand = this.evaluate(roperand);
		
		SymMultiExpression result = SymFactory.new_multiple_expression(
				expression.get_data_type(), COperator.arith_add);
		result.add_operand(loperand); result.add_operand(roperand);
		return this.evaluate(result);
	}
	
	/**
	 * get the numeric value describing the constant
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	private Object get_numeric_value(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:	
			if(constant.get_bool().booleanValue())
				return Long.valueOf(1L);
			else return Long.valueOf(0);
		case c_long:
			return constant.get_long();
		case c_double:
			return constant.get_double();
		default: throw new IllegalArgumentException("Invalid constant");
		}
	}
	
	/**
	 * x / y
	 * @param lconstant
	 * @param rconstant
	 * @return
	 * @throws Exception
	 */
	private SymConstant div_by(CConstant lconstant, CConstant rconstant) throws Exception {
		Object lvalue = this.get_numeric_value(lconstant);
		Object rvalue = this.get_numeric_value(rconstant);
		
		if(lvalue instanceof Long) {
			long lval = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval / rval);
			}
			else {
				double rval = ((Double) rvalue).doubleValue();
				return SymFactory.new_constant(lval / rval);
			}
		}
		else {
			double lval = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval / rval);
			}
			else {
				double rval = ((Double) rvalue).doubleValue();
				return SymFactory.new_constant(lval / rval);
			}
		}
	}
	@Override
	protected SymExpression arith_div_expression(SymBinaryExpression expression) throws Exception {
		SymExpression loperand = this.evaluate(expression.get_loperand());
		SymExpression roperand = this.evaluate(expression.get_roperand());
		
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		CConstant lconstant = null, rconstant = null;
		if(loperand instanceof SymConstant) lconstant = ((SymConstant) loperand).get_constant();
		if(roperand instanceof SymConstant) rconstant = ((SymConstant) roperand).get_constant();
		
		if(lconstant != null && rconstant != null) {
			return this.div_by(lconstant, rconstant);
		}
		else if(lconstant != null) {
			if(this.match_value(lconstant, 0))
				return loperand;
			else {
				return SymFactory.new_binary_expression(
						expression.get_data_type(), COperator.arith_div, loperand, roperand);
			}
		}
		else if(rconstant != null) {
			if(this.match_value(rconstant, 1))
				return loperand;
			else if(this.match_value(rconstant, -1))
				return this.evaluate(SymFactory.new_unary_expression(data_type, COperator.negative, loperand));
			else {
				return SymFactory.new_binary_expression(
						expression.get_data_type(), COperator.arith_div, loperand, roperand);
			}
		}
		else {
			return SymFactory.new_binary_expression(
					expression.get_data_type(), COperator.arith_div, loperand, roperand);
		}
	}
	
	/**
	 * x % y
	 * @param lconstant
	 * @param rconstant
	 * @return
	 * @throws Exception
	 */
	private SymConstant mod_by(CConstant lconstant, CConstant rconstant) throws Exception {
		Object lvalue = this.get_numeric_value(lconstant);
		Object rvalue = this.get_numeric_value(rconstant);
		
		if(lvalue instanceof Long) {
			long lval = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval % rval);
			}
			else {
				double rval = ((Double) rvalue).doubleValue();
				return SymFactory.new_constant(lval % rval);
			}
		}
		else {
			double lval = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval % rval);
			}
			else {
				double rval = ((Double) rvalue).doubleValue();
				return SymFactory.new_constant(lval % rval);
			}
		}
	}
	@Override
	protected SymExpression arith_mod_expression(SymBinaryExpression expression) throws Exception {
		SymExpression loperand = this.evaluate(expression.get_loperand());
		SymExpression roperand = this.evaluate(expression.get_roperand());
		
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		CConstant lconstant = null, rconstant = null;
		if(loperand instanceof SymConstant) lconstant = ((SymConstant) loperand).get_constant();
		if(roperand instanceof SymConstant) rconstant = ((SymConstant) roperand).get_constant();
		
		if(lconstant != null && rconstant != null) {
			return this.mod_by(lconstant, rconstant);
		}
		else if(lconstant != null) {
			if(this.match_value(lconstant, 1L) || this.match_value(lconstant, 0))
				return loperand;
			else {
				return SymFactory.new_binary_expression(data_type, COperator.arith_mod, loperand, roperand);
			}
		}
		else if(rconstant != null) {
			if(this.match_value(rconstant, 1) || this.match_value(rconstant, -1))
				return SymFactory.new_constant(0L);
			else {
				return SymFactory.new_binary_expression(data_type, COperator.arith_mod, loperand, roperand);
			}
		}
		else {
			return SymFactory.new_binary_expression(data_type, COperator.arith_mod, loperand, roperand);
		}
	}
	
	/**
	 * x << y
	 * @param lconstant
	 * @param rconstant
	 * @return
	 * @throws Exception
	 */
	private SymConstant lsh_by(CConstant lconstant, CConstant rconstant) throws Exception {
		Object lvalue = this.get_numeric_value(lconstant);
		Object rvalue = this.get_numeric_value(rconstant);
		
		if(lvalue instanceof Long) {
			long lval = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval << rval);
			}
			else {
				throw new IllegalArgumentException("No double allowed");
			}
		}
		else { throw new IllegalArgumentException("No double allowed"); }
	}
	@Override
	protected SymExpression bitws_lsh_expression(SymBinaryExpression expression) throws Exception {
		SymExpression loperand = this.evaluate(expression.get_loperand());
		SymExpression roperand = this.evaluate(expression.get_roperand());
		
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		CConstant lconstant = null, rconstant = null;
		if(loperand instanceof SymConstant) lconstant = ((SymConstant) loperand).get_constant();
		if(roperand instanceof SymConstant) rconstant = ((SymConstant) roperand).get_constant();
		
		if(lconstant != null && rconstant != null) {
			return this.lsh_by(lconstant, rconstant);
		}
		else if(lconstant != null) {
			if(this.match_value(lconstant, 0))
				return loperand;
			else {
				return SymFactory.new_binary_expression(data_type, COperator.left_shift, loperand, roperand);
			}
		}
		else if(rconstant != null) {
			if(this.match_value(rconstant, 0))
				return loperand;
			else {
				return SymFactory.new_binary_expression(data_type, COperator.left_shift, loperand, roperand);
			}
		}
		else {
			return SymFactory.new_binary_expression(data_type, COperator.left_shift, loperand, roperand);
		}
	}
	
	/**
	 * x << y
	 * @param lconstant
	 * @param rconstant
	 * @return
	 * @throws Exception
	 */
	private SymConstant rsh_by(CConstant lconstant, CConstant rconstant) throws Exception {
		Object lvalue = this.get_numeric_value(lconstant);
		Object rvalue = this.get_numeric_value(rconstant);
		
		if(lvalue instanceof Long) {
			long lval = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval >> rval);
			}
			else {
				throw new IllegalArgumentException("No double allowed");
			}
		}
		else { throw new IllegalArgumentException("No double allowed"); }
	}
	@Override
	protected SymExpression bitws_rsh_expression(SymBinaryExpression expression) throws Exception {
		SymExpression loperand = this.evaluate(expression.get_loperand());
		SymExpression roperand = this.evaluate(expression.get_roperand());
		
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		CConstant lconstant = null, rconstant = null;
		if(loperand instanceof SymConstant) lconstant = ((SymConstant) loperand).get_constant();
		if(roperand instanceof SymConstant) rconstant = ((SymConstant) roperand).get_constant();
		
		if(lconstant != null && rconstant != null) {
			return this.rsh_by(lconstant, rconstant);
		}
		else if(lconstant != null) {
			if(this.match_value(lconstant, 0))
				return loperand;
			else {
				return SymFactory.new_binary_expression(data_type, COperator.righ_shift, loperand, roperand);
			}
		}
		else if(rconstant != null) {
			if(this.match_value(rconstant, 0))
				return loperand;
			else {
				return SymFactory.new_binary_expression(data_type, COperator.righ_shift, loperand, roperand);
			}
		}
		else {
			return SymFactory.new_binary_expression(data_type, COperator.righ_shift, loperand, roperand);
		}
	}
	
	/**
	 * x > y
	 * @param lconstant
	 * @param rconstant
	 * @return
	 * @throws Exception
	 */
	private SymConstant greater_tn(CConstant lconstant, CConstant rconstant) throws Exception {
		Object lvalue = this.get_numeric_value(lconstant);
		Object rvalue = this.get_numeric_value(rconstant);
		
		if(lvalue instanceof Long) {
			long lval = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval > rval);
			}
			else {
				double rval = ((Double) rvalue).doubleValue();
				return SymFactory.new_constant(lval > rval);
			}
		}
		else {
			double lval = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval > rval);
			}
			else {
				double rval = ((Double) rvalue).doubleValue();
				return SymFactory.new_constant(lval > rval);
			}
		}
	}
	@Override
	protected SymExpression greater_tn_expression(SymBinaryExpression expression) throws Exception {
		SymExpression loperand = this.evaluate(expression.get_loperand());
		SymExpression roperand = this.evaluate(expression.get_roperand());
		CType data_type = CBasicTypeImpl.bool_type;
		CConstant lconstant = null, rconstant = null;
		if(loperand instanceof SymConstant) lconstant = ((SymConstant) loperand).get_constant();
		if(roperand instanceof SymConstant) rconstant = ((SymConstant) roperand).get_constant();
		
		if(lconstant != null && rconstant != null) {
			return this.greater_tn(lconstant, rconstant);
		}
		else {
			loperand = SymFactory.new_binary_expression(
					loperand.get_data_type(), COperator.arith_sub, loperand, roperand);
			loperand = this.evaluate(loperand);
			
			if(loperand instanceof SymConstant) {
				lconstant = ((SymConstant) loperand).get_constant();
				rconstant = new CConstant(); rconstant.set_long(0);
				return this.greater_tn(lconstant, rconstant);
			}
			else {
				roperand = SymFactory.new_constant(0L);
				return SymFactory.new_binary_expression(data_type, COperator.greater_tn, loperand, roperand);
			}
		}
	}
	
	@Override
	protected SymExpression smaller_tn_expression(SymBinaryExpression expression) throws Exception {
		SymExpression loperand = this.evaluate(expression.get_roperand());
		SymExpression roperand = this.evaluate(expression.get_loperand());
		CType data_type = CBasicTypeImpl.bool_type;
		CConstant lconstant = null, rconstant = null;
		if(loperand instanceof SymConstant) lconstant = ((SymConstant) loperand).get_constant();
		if(roperand instanceof SymConstant) rconstant = ((SymConstant) roperand).get_constant();
		
		if(lconstant != null && rconstant != null) {
			return this.greater_tn(lconstant, rconstant);
		}
		else {
			loperand = SymFactory.new_binary_expression(
					loperand.get_data_type(), COperator.arith_sub, loperand, roperand);
			loperand = this.evaluate(loperand);
			
			if(loperand instanceof SymConstant) {
				lconstant = ((SymConstant) loperand).get_constant();
				rconstant = new CConstant(); rconstant.set_long(0);
				return this.greater_tn(lconstant, rconstant);
			}
			else {
				roperand = SymFactory.new_constant(0L);
				return SymFactory.new_binary_expression(data_type, COperator.greater_tn, loperand, roperand);
			}
		}
	}
	
	private SymConstant greater_eq(CConstant lconstant, CConstant rconstant) throws Exception {
		Object lvalue = this.get_numeric_value(lconstant);
		Object rvalue = this.get_numeric_value(rconstant);
		
		if(lvalue instanceof Long) {
			long lval = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval >= rval);
			}
			else {
				double rval = ((Double) rvalue).doubleValue();
				return SymFactory.new_constant(lval >= rval);
			}
		}
		else {
			double lval = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval >= rval);
			}
			else {
				double rval = ((Double) rvalue).doubleValue();
				return SymFactory.new_constant(lval >= rval);
			}
		}
	}
	@Override
	protected SymExpression greater_eq_expression(SymBinaryExpression expression) throws Exception {
		SymExpression loperand = this.evaluate(expression.get_loperand());
		SymExpression roperand = this.evaluate(expression.get_roperand());
		CType data_type = CBasicTypeImpl.bool_type;
		CConstant lconstant = null, rconstant = null;
		if(loperand instanceof SymConstant) lconstant = ((SymConstant) loperand).get_constant();
		if(roperand instanceof SymConstant) rconstant = ((SymConstant) roperand).get_constant();
		
		if(lconstant != null && rconstant != null) {
			return this.greater_eq(lconstant, rconstant);
		}
		else {
			loperand = SymFactory.new_binary_expression(
					loperand.get_data_type(), COperator.arith_sub, loperand, roperand);
			loperand = this.evaluate(loperand);
			
			if(loperand instanceof SymConstant) {
				lconstant = ((SymConstant) loperand).get_constant();
				rconstant = new CConstant(); rconstant.set_long(0);
				return this.greater_eq(lconstant, rconstant);
			}
			else {
				roperand = SymFactory.new_constant(0L);
				SymExpression condition1 = SymFactory.
						new_binary_expression(data_type, COperator.greater_tn, loperand, roperand);
				SymExpression condition2 = SymFactory.
						new_binary_expression(data_type, COperator.equal_with, 
								(SymExpression) loperand.copy(), (SymExpression) roperand.copy());
				SymMultiExpression result = SymFactory.new_multiple_expression(data_type, COperator.logic_or);
				result.add_operand(condition1); result.add_operand(condition2); return result;
			}
		}
	}
	
	@Override
	protected SymExpression smaller_eq_expression(SymBinaryExpression expression) throws Exception {
		SymExpression loperand = this.evaluate(expression.get_roperand());
		SymExpression roperand = this.evaluate(expression.get_loperand());
		CType data_type = CBasicTypeImpl.bool_type;
		CConstant lconstant = null, rconstant = null;
		if(loperand instanceof SymConstant) lconstant = ((SymConstant) loperand).get_constant();
		if(roperand instanceof SymConstant) rconstant = ((SymConstant) roperand).get_constant();
		
		if(lconstant != null && rconstant != null) {
			return this.greater_eq(lconstant, rconstant);
		}
		else {
			loperand = SymFactory.new_binary_expression(
					loperand.get_data_type(), COperator.arith_sub, loperand, roperand);
			loperand = this.evaluate(loperand);
			
			if(loperand instanceof SymConstant) {
				lconstant = ((SymConstant) loperand).get_constant();
				rconstant = new CConstant(); rconstant.set_long(0);
				return this.greater_eq(lconstant, rconstant);
			}
			else {
				roperand = SymFactory.new_constant(0L);
				SymExpression condition1 = SymFactory.
						new_binary_expression(data_type, COperator.greater_tn, loperand, roperand);
				SymExpression condition2 = SymFactory.
						new_binary_expression(data_type, COperator.equal_with, 
								(SymExpression) loperand.copy(), (SymExpression) roperand.copy());
				SymMultiExpression result = SymFactory.new_multiple_expression(data_type, COperator.logic_or);
				result.add_operand(condition1); result.add_operand(condition2); return result;
			}
		}
	}
	
	private SymConstant equal_with(CConstant lconstant, CConstant rconstant) throws Exception {
		Object lvalue = this.get_numeric_value(lconstant);
		Object rvalue = this.get_numeric_value(rconstant);
		
		if(lvalue instanceof Long) {
			long lval = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval == rval);
			}
			else {
				double rval = ((Double) rvalue).doubleValue();
				return SymFactory.new_constant(lval == rval);
			}
		}
		else {
			double lval = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval == rval);
			}
			else {
				double rval = ((Double) rvalue).doubleValue();
				return SymFactory.new_constant(lval == rval);
			}
		}
	}
	@Override
	protected SymExpression equal_with_expression(SymBinaryExpression expression) throws Exception {
		SymExpression loperand = this.evaluate(expression.get_loperand());
		SymExpression roperand = this.evaluate(expression.get_roperand());
		CType data_type = CBasicTypeImpl.bool_type;
		CConstant lconstant = null, rconstant = null;
		if(loperand instanceof SymConstant) lconstant = ((SymConstant) loperand).get_constant();
		if(roperand instanceof SymConstant) rconstant = ((SymConstant) roperand).get_constant();
		
		if(lconstant != null && rconstant != null) {
			return this.equal_with(lconstant, rconstant);
		}
		else {
			loperand = SymFactory.new_binary_expression(
					loperand.get_data_type(), COperator.arith_sub, loperand, roperand);
			loperand = this.evaluate(loperand);
			
			if(loperand instanceof SymConstant) {
				lconstant = ((SymConstant) loperand).get_constant();
				rconstant = new CConstant(); rconstant.set_long(0);
				return this.equal_with(lconstant, rconstant);
			}
			else {
				roperand = SymFactory.new_constant(0L);
				return SymFactory.new_binary_expression(data_type, COperator.equal_with, loperand, roperand);
			}
		}
	}
	
	private SymConstant not_equals(CConstant lconstant, CConstant rconstant) throws Exception {
		Object lvalue = this.get_numeric_value(lconstant);
		Object rvalue = this.get_numeric_value(rconstant);
		
		if(lvalue instanceof Long) {
			long lval = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval != rval);
			}
			else {
				double rval = ((Double) rvalue).doubleValue();
				return SymFactory.new_constant(lval != rval);
			}
		}
		else {
			double lval = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long rval = ((Long) rvalue).longValue();
				return SymFactory.new_constant(lval != rval);
			}
			else {
				double rval = ((Double) rvalue).doubleValue();
				return SymFactory.new_constant(lval != rval);
			}
		}
	}
	@Override
	protected SymExpression not_equals_expression(SymBinaryExpression expression) throws Exception {
		SymExpression loperand = this.evaluate(expression.get_loperand());
		SymExpression roperand = this.evaluate(expression.get_roperand());
		CType data_type = CBasicTypeImpl.bool_type;
		CConstant lconstant = null, rconstant = null;
		if(loperand instanceof SymConstant) lconstant = ((SymConstant) loperand).get_constant();
		if(roperand instanceof SymConstant) rconstant = ((SymConstant) roperand).get_constant();
		
		if(lconstant != null && rconstant != null) {
			return this.not_equals(lconstant, rconstant);
		}
		else {
			loperand = SymFactory.new_binary_expression(
					loperand.get_data_type(), COperator.arith_sub, loperand, roperand);
			loperand = this.evaluate(loperand);
			
			if(loperand instanceof SymConstant) {
				lconstant = ((SymConstant) loperand).get_constant();
				rconstant = new CConstant(); rconstant.set_long(0);
				return this.not_equals(lconstant, rconstant);
			}
			else {
				roperand = SymFactory.new_constant(0L);
				SymExpression condition = SymFactory.
						new_binary_expression(data_type, COperator.equal_with, loperand, roperand);
				return SymFactory.new_unary_expression(data_type, COperator.logic_not, condition);
			}
		}
	}

	@Override
	protected SymExpression negative_expression(SymUnaryExpression expression) throws Exception {
		SymExpression loperand = SymFactory.new_constant(-1L);
		SymExpression roperand = this.evaluate(expression.get_operand());
		
		SymMultiExpression result = SymFactory.
				new_multiple_expression(expression.get_data_type(), COperator.arith_mul);
		result.add_operand(loperand); result.add_operand(roperand);
		return this.evaluate(result);
	}

	private SymConstant bit_rsv(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:
			if(constant.get_bool().booleanValue())
				return SymFactory.new_constant(~1L);
			else return SymFactory.new_constant(~0L);
		case c_long:
			return SymFactory.new_constant(~constant.get_long().longValue());
		default: throw new IllegalArgumentException("Invalid type");
		}
	}
	@Override
	protected SymExpression bitws_rsv_expression(SymUnaryExpression expression) throws Exception {
		SymExpression operand = this.evaluate(expression.get_operand());
		if(operand instanceof SymConstant) {
			CConstant constant = ((SymConstant) operand).get_constant();
			return this.bit_rsv(constant);
		}
		else if(operand instanceof SymUnaryExpression) {
			SymUnaryExpression uoperand = (SymUnaryExpression) operand;
			if(uoperand.get_operator() == COperator.bit_not)
				return (SymExpression) uoperand.get_operand().copy();
			else {
				return SymFactory.new_unary_expression(
						expression.get_data_type(), COperator.bit_not, operand);
			}
		}
		else {
			return SymFactory.new_unary_expression(
					expression.get_data_type(), COperator.bit_not, operand);
		}
	}
	
	private SymConstant logic_not(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return SymFactory.new_constant(!constant.get_bool().booleanValue());
		case c_long:	return SymFactory.new_constant(constant.get_long().longValue() == 0L);
		case c_double:	return SymFactory.new_constant(constant.get_double().doubleValue() == 0.0);
		default: throw new IllegalArgumentException("Invalid type");
		}
	}
	@Override
	protected SymExpression logic_not_expression(SymUnaryExpression expression) throws Exception {
		SymExpression operand = this.evaluate(expression.get_operand());
		if(operand instanceof SymConstant) {
			CConstant constant = ((SymConstant) operand).get_constant();
			return this.logic_not(constant);
		}
		else if(operand instanceof SymUnaryExpression) {
			SymUnaryExpression uoperand = (SymUnaryExpression) operand;
			if(uoperand.get_operator() == COperator.logic_not)
				return (SymExpression) uoperand.get_operand().copy();
			else {
				return SymFactory.new_unary_expression(
						expression.get_data_type(), COperator.logic_not, operand);
			}
		}
		else {
			return SymFactory.new_unary_expression(
					expression.get_data_type(), COperator.logic_not, operand);
		}
	}

	@Override
	protected SymExpression address_of_expression(SymUnaryExpression expression) throws Exception {
		SymExpression operand = this.evaluate(expression.get_operand());
		if(operand instanceof SymUnaryExpression) {
			SymUnaryExpression uoperand = (SymUnaryExpression) operand;
			if(uoperand.get_operator() == COperator.dereference)
				return (SymExpression) uoperand.get_operand().copy();
			else {
				return SymFactory.new_unary_expression(
						expression.get_data_type(), COperator.address_of, operand);
			}
		}
		else {
			return SymFactory.new_unary_expression(
					expression.get_data_type(), COperator.address_of, operand);
		}
	}

	@Override
	protected SymExpression de_reference_expression(SymUnaryExpression expression) throws Exception {
		SymExpression operand = this.evaluate(expression.get_operand());
		if(operand instanceof SymUnaryExpression) {
			SymUnaryExpression uoperand = (SymUnaryExpression) operand;
			if(uoperand.get_operator() == COperator.address_of)
				return (SymExpression) uoperand.get_operand().copy();
			else {
				return SymFactory.new_unary_expression(
						expression.get_data_type(), COperator.dereference, operand);
			}
		}
		else {
			return SymFactory.new_unary_expression(
					expression.get_data_type(), COperator.dereference, operand);
		}
	}
	
	@Override
	protected SymExpression cast_expression(SymUnaryExpression expression) throws Exception {
		SymExpression operand = this.evaluate(expression.get_operand());
		return operand;
	}

}
