package com.jcsa.jcmutest.selang.util;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.selang.lang.expr.SedBinaryExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedCallExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedConstant;
import com.jcsa.jcmutest.selang.lang.expr.SedDefaultValue;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedFieldExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedIdExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedInitializerList;
import com.jcsa.jcmutest.selang.lang.expr.SedLiteral;
import com.jcsa.jcmutest.selang.lang.expr.SedUnaryExpression;
import com.jcsa.jcmutest.selang.lang.tokn.SedArgumentList;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
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
		/* 1. get function and arguments */
		SedExpression function = this.evaluate(source.get_function());
		SedArgumentList old_argument_list = source.get_argument_list();
		List<Object> arguments = new ArrayList<Object>();
		for(int k = 0; k < old_argument_list.number_of_arguments(); k++) {
			arguments.add(this.evaluate(old_argument_list.get_argument(k)));
		}
		
		/* 2. create initial calling expression */
		SedCallExpression call_expr = SedFactory.call_expression(
						source.get_data_type(), function, arguments);
		
		/* 3. invocate the call-expression and get result */
		SedExpression expression = this.scope.invocate(call_expr);
		expression.set_cir_expression(
				source.get_cir_expression(), source.get_data_type());
		
		/* 4. end of all */	return expression;
	}
	private SedExpression eval_field_expression(SedFieldExpression source) throws Exception {
		SedExpression expression = SedFactory.field_expression(
				source.get_data_type(), 
				this.evaluate(source.get_body()), 
				source.get_field().get_name());
		expression.set_cir_expression(
				source.get_cir_expression(), source.get_data_type());
		return expression;
	}
	private SedExpression eval_initializer_list(SedInitializerList source) throws Exception {
		List<Object> elements = new ArrayList<Object>();
		for(int k = 0; k < source.number_of_elements(); k++) {
			elements.add(this.evaluate(source.get_element(k)));
		}
		
		SedExpression expression = SedFactory.
				initializer_list(source.get_data_type(), elements);
		expression.set_cir_expression(
				source.get_cir_expression(), source.get_data_type());
		return expression;
	}
	
	/* unary expression evaluation */
	private SedExpression eval_unary_expression(SedUnaryExpression source) throws Exception {
		switch(source.get_operator().get_operator()) {
		case negative:		return this.eval_arith_neg(source);
		case bit_not:		return this.eval_bitws_rsv(source);
		case logic_not:		return this.eval_logic_not(source);
		case address_of:	return this.eval_address_of(source);
		case dereference:	return this.eval_dereference(source);
		case assign:		return this.eval_type_cast(source);
		default: throw new IllegalArgumentException(source.generate_code());
		}
	}
	private SedExpression eval_arith_neg(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand()), result;
		if(operand instanceof SedConstant) {
			result = SedComputation.arith_neg((SedConstant) operand);
		}
		else if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.negative) {
				result = ((SedUnaryExpression) operand).get_operand();
			}
			else {
				result = SedFactory.arith_neg(source.get_data_type(), operand);
			}
		}
		else if(operand instanceof SedBinaryExpression) {
			COperator operator = ((SedBinaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.arith_sub) {
				result = SedFactory.arith_sub(source.get_data_type(), 
						((SedBinaryExpression) operand).get_roperand(), 
						((SedBinaryExpression) operand).get_loperand());
			}
			else {
				result = SedFactory.arith_neg(source.get_data_type(), operand);
			}
		}
		else {
			result = SedFactory.arith_neg(source.get_data_type(), operand);
		}
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_bitws_rsv(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand()), result;
		if(operand instanceof SedConstant) {
			result = SedComputation.bitws_rsv((SedConstant) operand);
		}
		else if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.bit_not) {
				result = ((SedUnaryExpression) operand).get_operand();
			}
			else {
				result = SedFactory.bitws_rsv(source.get_data_type(), operand);
			}
		}
		else {
			result = SedFactory.bitws_rsv(source.get_data_type(), operand);
		}
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_logic_not(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand()), result;
		if(operand instanceof SedConstant) {
			result = SedComputation.logic_not((SedConstant) operand);
		}
		else if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.logic_not) {
				result = ((SedUnaryExpression) operand).get_operand();
			}
			else {
				result = SedFactory.logic_not(operand);
			}
		}
		else {
			result = SedFactory.logic_not(operand);
		}
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_address_of(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand()), result;
		if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.dereference) {
				result = ((SedUnaryExpression) operand).get_operand();
			}
			else {
				result = SedFactory.address_of(source.get_data_type(), operand);
			}
		}
		else {
			result = SedFactory.address_of(source.get_data_type(), operand);
		}
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_dereference(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand()), result;
		if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.address_of) {
				result = ((SedUnaryExpression) operand).get_operand();
			}
			else {
				result = SedFactory.dereference(source.get_data_type(), operand);
			}
		}
		else {
			result = SedFactory.dereference(source.get_data_type(), operand);
		}
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_type_cast(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand()), result;
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		if(operand instanceof SedConstant) {
			if(data_type instanceof CBasicType) {
				switch(((CBasicType) data_type).get_tag()) {
				case c_bool:
				{
					result = (SedExpression) SedFactory.fetch(Boolean.valueOf(
							SedComputation.get_bool((SedConstant) operand)));
					break;
				}
				case c_char:
				case c_uchar:
				{
					result = (SedExpression) SedFactory.fetch(Character.valueOf(
							SedComputation.get_char((SedConstant) operand)));
					break;
				}
				case c_short:
				case c_ushort:
				{
					result = (SedExpression) SedFactory.fetch(Short.valueOf(
							SedComputation.get_short((SedConstant) operand)));
					break;
				}
				case c_int:
				case c_uint:
				{
					result = (SedExpression) SedFactory.fetch(Integer.valueOf(
							SedComputation.get_int((SedConstant) operand)));
					break;
				}
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:
				{
					result = (SedExpression) SedFactory.fetch(Long.valueOf(
							SedComputation.get_long((SedConstant) operand)));
					break;
				}
				case c_float:
				{
					result = (SedExpression) SedFactory.fetch(Float.valueOf(
							SedComputation.get_float((SedConstant) operand)));
					break;
				}
				case c_double:
				case c_ldouble:
				{
					result = (SedExpression) SedFactory.fetch(Double.valueOf(
							SedComputation.get_double((SedConstant) operand)));
					break;
				}
				default: 
				{
					result = SedFactory.type_cast(data_type, operand); break;
				}
				}
			}
			else if(data_type instanceof CArrayType
					|| data_type instanceof CPointerType
					|| data_type instanceof CFunctionType) {
				result = (SedExpression) SedFactory.fetch(Long.valueOf(
						SedComputation.get_long((SedConstant) operand)));
			}
			else if(data_type instanceof CEnumType) {
				result = (SedExpression) SedFactory.fetch(Integer.valueOf(
						SedComputation.get_int((SedConstant) operand)));
			}
			else {
				result = SedFactory.type_cast(data_type, operand);
			}
		}
		else {
			result = SedFactory.type_cast(data_type, operand);
		}
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	
	/* binary expression evaluation */
	private SedExpression eval_binary_expression(SedBinaryExpression source) throws Exception {
		switch(source.get_operator().get_operator()) {
		case arith_add:		return this.eval_arith_add_or_sub(source);
		case arith_sub:		return this.eval_arith_add_or_sub(source);
		case arith_mul:		return this.eval_arith_mul_or_div(source);
		case arith_div:		return this.eval_arith_mul_or_div(source);
		case arith_mod:		return this.eval_arith_mod(source);
		case bit_and:		return this.eval_bitws_and(source);
		case bit_or:		return this.eval_bitws_ior(source);
		case bit_xor:		return this.eval_bitws_xor(source);
		case left_shift:	return this.eval_bitws_lsh(source);
		case righ_shift:	return this.eval_bitws_rsh(source);
		case logic_and:		return this.eval_logic_and(source);
		case logic_or:		return this.eval_logic_ior(source);
		case greater_tn:	return this.eval_greater_tn(source);
		case greater_eq:	return this.eval_greater_eq(source);
		case smaller_tn:	return this.eval_smaller_tn(source);
		case smaller_eq:	return this.eval_smaller_eq(source);
		case equal_with:	return this.eval_equal_with(source);
		case not_equals:	return this.eval_not_equals(source);
		default: throw new IllegalArgumentException(source.generate_code());
		}
	}
	
	/* {+, -} */
	/**
	 * collect the loperands and roperands in arithmetic addition and subtract
	 * @param source
	 * @param loperands
	 * @param roperands
	 * @throws Exception
	 */
	private void get_operands_as(SedExpression source, 
			List<SedExpression> loperands, 
			List<SedExpression> roperands) throws Exception {
		if(source instanceof SedBinaryExpression) {
			COperator operator = ((SedBinaryExpression) source).get_operator().get_operator();
			if(operator == COperator.arith_add) {
				this.get_operands_as(((SedBinaryExpression) source).get_loperand(), loperands, roperands);
				this.get_operands_as(((SedBinaryExpression) source).get_roperand(), loperands, roperands);
			}
			else if(operator == COperator.arith_sub) {
				this.get_operands_as(((SedBinaryExpression) source).get_loperand(), loperands, roperands);
				this.get_operands_as(((SedBinaryExpression) source).get_roperand(), roperands, loperands);
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
	 * @return compute the operands and accumulate the constants part
	 * @throws Exception
	 */
	private List<SedExpression> acc_operands_as(List<SedExpression> operands) throws Exception {
		List<SedExpression> new_operands = new ArrayList<SedExpression>();
		SedConstant constant = (SedConstant) SedFactory.fetch(Integer.valueOf(0));
		for(SedExpression operand : operands) {
			SedExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SedConstant) {
				constant = SedComputation.arith_add(constant, (SedConstant) new_operand);
			}
			else {
				new_operands.add(new_operand);
			}
		}
		new_operands.add(constant); return new_operands;
	}
	/**
	 * @param operands
	 * @return op1 + op2 + op3 + ... + opN or null
	 * @throws Exception
	 */
	private SedExpression con_operands_as(CType type, List<SedExpression> operands) throws Exception {
		SedExpression expression = null;
		for(SedExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = SedFactory.arith_add(type, expression, operand);
			}
		}
		return expression;
	}
	private SedExpression eval_arith_add_or_sub(SedBinaryExpression source) throws Exception {
		List<SedExpression> loperands = new ArrayList<SedExpression>();
		List<SedExpression> roperands = new ArrayList<SedExpression>();
		this.get_operands_as(source, loperands, roperands);
		
		loperands = this.acc_operands_as(loperands);
		roperands = this.acc_operands_as(roperands);
		
		SedConstant lconstant = (SedConstant) loperands.remove(loperands.size() - 1);
		SedConstant rconstant = (SedConstant) roperands.remove(roperands.size() - 1);
		SedConstant constant = SedComputation.arith_sub(lconstant, rconstant);
		if(!SedComputation.compare(constant, 0)) loperands.add(constant);
		
		SedExpression loperand = this.con_operands_as(source.get_data_type(), loperands);
		SedExpression roperand = this.con_operands_as(source.get_data_type(), roperands);
		
		SedExpression result;
		if(loperand == null) {
			if(roperand == null) {
				result = (SedExpression) SedFactory.fetch(Integer.valueOf(0));
			}
			else {
				result = SedFactory.arith_neg(source.get_data_type(), roperand);
			}
		}
		else {
			if(roperand == null) {
				result = loperand;
			}
			else {
				result = SedFactory.arith_sub(source.get_data_type(), loperand, roperand);
			}
		}
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	
	/* {*, /} */
	/**
	 * collect the operands in division and divisor
	 * @param source
	 * @param loperands
	 * @param roperands
	 * @throws Exception
	 */
	private void get_operands_md(SedExpression source,
			List<SedExpression> loperands, 
			List<SedExpression> roperands) throws Exception {
		if(source instanceof SedBinaryExpression) {
			COperator operator = ((SedBinaryExpression) source).get_operator().get_operator();
			if(operator == COperator.arith_mul) {
				this.get_operands_as(((SedBinaryExpression) source).get_loperand(), loperands, roperands);
				this.get_operands_as(((SedBinaryExpression) source).get_roperand(), loperands, roperands);
			}
			else if(operator == COperator.arith_div) {
				this.get_operands_as(((SedBinaryExpression) source).get_loperand(), loperands, roperands);
				this.get_operands_as(((SedBinaryExpression) source).get_roperand(), roperands, loperands);
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
	 * @return compute the operands and accumulate the constants part
	 * @throws Exception
	 */
	private List<SedExpression> acc_operands_md(List<SedExpression> operands) throws Exception {
		List<SedExpression> new_operands = new ArrayList<SedExpression>();
		SedConstant constant = (SedConstant) SedFactory.fetch(Integer.valueOf(1));
		for(SedExpression operand : operands) {
			SedExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SedConstant) {
				constant = SedComputation.arith_mul(constant, (SedConstant) new_operand);
			}
			else {
				new_operands.add(new_operand);
			}
		}
		new_operands.add(constant); return new_operands;
	}
	/**
	 * @param type
	 * @param operands
	 * @return x1 * x2 * x3 * ... * xN
	 * @throws Exception
	 */
	private SedExpression con_operands_md(CType type, List<SedExpression> operands) throws Exception {
		SedExpression expression = null;
		for(SedExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = SedFactory.arith_mul(type, expression, operand);
			}
		}
		return expression;
	}
	private SedExpression eval_arith_mul_or_div(SedBinaryExpression source) throws Exception {
		List<SedExpression> loperands = new ArrayList<SedExpression>();
		List<SedExpression> roperands = new ArrayList<SedExpression>();
		this.get_operands_md(source, loperands, roperands);
		
		loperands = this.acc_operands_md(loperands);
		roperands = this.acc_operands_md(roperands);
		
		SedConstant lconstant = (SedConstant) loperands.remove(loperands.size() - 1);
		SedConstant rconstant = (SedConstant) roperands.remove(roperands.size() - 1);
		if(SedComputation.compare(lconstant, 0)) {
			SedExpression result = (SedExpression) SedFactory.fetch(Integer.valueOf(0));
			result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
			return result;
		}
		if(!SedComputation.compare(lconstant, 1)) loperands.add(lconstant);
		if(!SedComputation.compare(rconstant, 1)) roperands.add(rconstant);
		
		SedExpression loperand = this.con_operands_md(source.get_data_type(), loperands);
		SedExpression roperand = this.con_operands_md(source.get_data_type(), roperands);
		
		SedExpression result;
		if(loperand == null) {
			if(roperand == null) {
				result = (SedExpression) SedFactory.fetch(Integer.valueOf(1));
			}
			else {
				loperand = (SedExpression) SedFactory.fetch(Integer.valueOf(1));
				result = SedFactory.arith_div(source.get_data_type(), loperand, roperand);
			}
		}
		else {
			if(roperand == null) {
				result = loperand;
			}
			else {
				result = SedFactory.arith_div(source.get_data_type(), loperand, roperand);
			}
		}
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	
	/* {%} */
	private SedExpression eval_arith_mod(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand()), result = null;
		
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			result = SedComputation.
					arith_mod((SedConstant) loperand, (SedConstant) roperand);
		}
		
		if(result == null && loperand instanceof SedConstant) {
			if(SedComputation.compare((SedConstant) loperand, 0)) {
				result = (SedExpression) SedFactory.fetch(Integer.valueOf(0));
			}
			else if(SedComputation.compare((SedConstant) loperand, 1)
					|| SedComputation.compare((SedConstant) loperand, -1)) {
				result = (SedExpression) SedFactory.fetch(Integer.valueOf(1));
			}
		}
		
		if(result == null && roperand instanceof SedConstant) {
			if(SedComputation.compare((SedConstant) roperand, 1)
				|| SedComputation.compare((SedConstant) roperand, -1)) {
				result = (SedExpression) SedFactory.fetch(Integer.valueOf(0));
			}
		}
		
		if(result == null) {
			result = SedFactory.arith_mod(source.get_data_type(), loperand, roperand);
		}
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	
	/* {&, |, ^} */
	private void get_operands_bt(SedExpression source, COperator operator,
			List<SedExpression> operands) throws Exception {
		if(source instanceof SedBinaryExpression) {
			if(operator == ((SedBinaryExpression) source).get_operator().get_operator()) {
				this.get_operands_bt(((SedBinaryExpression) source).get_loperand(), operator, operands);
				this.get_operands_bt(((SedBinaryExpression) source).get_roperand(), operator, operands);
			}
			else {
				operands.add(source);
			}
		}
		else {
			operands.add(source);
		}
	}
	private List<SedExpression> acc_operands_ba(CType data_type, 
			List<SedExpression> operands, COperator operator) throws Exception {
		List<SedExpression> new_operands = new ArrayList<SedExpression>();
		SedConstant constant = (SedConstant) SedFactory.fetch(Long.valueOf(~0L));
		for(SedExpression operand : operands) {
			SedExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SedConstant) {
				constant = SedComputation.bitws_and(constant, (SedConstant) new_operand);
			}
			else {
				new_operands.add(new_operand);
			}
		}
		new_operands.add(constant); return new_operands;
	}
	private List<SedExpression> acc_operands_bi(CType data_type, 
			List<SedExpression> operands, COperator operator) throws Exception {
		List<SedExpression> new_operands = new ArrayList<SedExpression>();
		SedConstant constant = (SedConstant) SedFactory.fetch(Long.valueOf(0));
		for(SedExpression operand : operands) {
			SedExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SedConstant) {
				constant = SedComputation.bitws_ior(constant, (SedConstant) new_operand);
			}
			else {
				new_operands.add(new_operand);
			}
		}
		new_operands.add(constant); return new_operands;
	}
	private List<SedExpression> acc_operands_bx(CType data_type, 
			List<SedExpression> operands, COperator operator) throws Exception {
		List<SedExpression> new_operands = new ArrayList<SedExpression>();
		SedConstant constant = (SedConstant) SedFactory.fetch(Long.valueOf(0));
		for(SedExpression operand : operands) {
			SedExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SedConstant) {
				constant = SedComputation.bitws_xor(constant, (SedConstant) new_operand);
			}
			else {
				new_operands.add(new_operand);
			}
		}
		new_operands.add(constant); return new_operands;
	}
	private SedExpression conc_operands_ba(CType type, List<SedExpression> operands) throws Exception {
		SedExpression expression = null;
		for(SedExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = SedFactory.bitws_and(type, expression, operand);
			}
		}
		return expression;
	}
	private SedExpression conc_operands_bi(CType type, List<SedExpression> operands) throws Exception {
		SedExpression expression = null;
		for(SedExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = SedFactory.bitws_ior(type, expression, operand);
			}
		}
		return expression;
	}
	private SedExpression conc_operands_bx(CType type, List<SedExpression> operands) throws Exception {
		SedExpression expression = null;
		for(SedExpression operand : operands) {
			if(expression == null) {
				expression = operand;
			}
			else {
				expression = SedFactory.bitws_xor(type, expression, operand);
			}
		}
		return expression;
	}
	private SedExpression eval_bitws_and(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.get_operands_bt(source, source.get_operator().get_operator(), operands);
		operands = this.acc_operands_ba(
				source.get_data_type(), operands, source.get_operator().get_operator());
		SedExpression result = null;
		
		SedConstant constant = (SedConstant) operands.remove(operands.size() - 1);
		if(result == null && SedComputation.compare(constant, 0L)) {
			result = (SedExpression) SedFactory.fetch(Long.valueOf(0));
		}
		if(!SedComputation.compare(constant, -1L)) { operands.add(constant); }
		
		if(result == null) {
			result = this.conc_operands_ba(source.get_data_type(), operands);
		}
		
		if(result == null) {
			result = constant;
		}
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_bitws_ior(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.get_operands_bt(source, source.get_operator().get_operator(), operands);
		operands = this.acc_operands_bi(
				source.get_data_type(), operands, source.get_operator().get_operator());
		SedExpression result = null;
		
		SedConstant constant = (SedConstant) operands.remove(operands.size() - 1);
		if(SedComputation.compare(constant, ~0)) {
			result = (SedExpression) SedFactory.fetch(Long.valueOf(~0L));
		}
		if(!SedComputation.compare(constant, 0L)) { operands.add(constant); }
		
		if(result == null) {
			result = this.conc_operands_bi(source.get_data_type(), operands);
		}
		if(result == null) { result = constant; }
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_bitws_xor(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.get_operands_bt(source, source.get_operator().get_operator(), operands);
		operands = this.acc_operands_bx(
				source.get_data_type(), operands, source.get_operator().get_operator());
		SedExpression result = null;
		
		SedConstant constant = (SedConstant) operands.remove(operands.size() - 1);
		if(!SedComputation.compare(constant, 0L)) { operands.add(constant); }
		
		result = this.conc_operands_bx(source.get_data_type(), operands);
		if(result == null) { result = constant; }
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_bitws_lsh(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		SedExpression result = null;
		
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			result = SedComputation.bitws_lsh((SedConstant) loperand, (SedConstant) roperand);
		}
		
		if(result == null && loperand instanceof SedConstant) {
			if(SedComputation.compare((SedConstant) loperand, 0)) {
				result = loperand;
			}
		}
		
		if(result == null && roperand instanceof SedConstant) {
			if(SedComputation.compare((SedConstant) roperand, 0)) {
				result = loperand;
			}
		}
		
		if(result == null) {
			result = SedFactory.bitws_lsh(source.get_data_type(), loperand, roperand);
		}
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_bitws_rsh(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		SedExpression result = null;
		
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			result = SedComputation.bitws_rsh((SedConstant) loperand, (SedConstant) roperand);
		}
		
		if(result == null && loperand instanceof SedConstant) {
			if(SedComputation.compare((SedConstant) loperand, 0)) {
				result = loperand;
			}
		}
		
		if(result == null && roperand instanceof SedConstant) {
			if(SedComputation.compare((SedConstant) roperand, 0)) {
				result = loperand;
			}
		}
		
		if(result == null) {
			result = SedFactory.bitws_rsh(source.get_data_type(), loperand, roperand);
		}
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	
	/* {&&, ||} */
	private SedExpression eval_logic_and(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.get_operands_bt(source, COperator.logic_and, operands);
		SedExpression result;
		
		List<SedExpression> new_operands = new ArrayList<SedExpression>();
		for(SedExpression operand : operands) {
			SedExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SedConstant) {
				if(!SedComputation.get_bool((SedConstant) new_operand)) {
					result = (SedExpression) SedFactory.fetch(Boolean.FALSE);
					result.set_cir_expression(source.
							get_cir_expression(), source.get_data_type());
					return result;
				}
			}
			else {
				new_operands.add(new_operand);
			}
		}
		
		if(new_operands.isEmpty()) {
			result = (SedExpression) SedFactory.fetch(Boolean.TRUE);
		}
		else {
			result = null;
			for(SedExpression operand : new_operands) {
				if(result == null) {
					result = operand;
				}
				else {
					result = SedFactory.logic_and(result, operand);
				}
			}
		}
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_logic_ior(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.get_operands_bt(source, COperator.logic_or, operands);
		SedExpression result;
		
		List<SedExpression> new_operands = new ArrayList<SedExpression>();
		for(SedExpression operand : operands) {
			SedExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SedConstant) {
				if(!SedComputation.get_bool((SedConstant) new_operand)) {
					result = (SedExpression) SedFactory.fetch(Boolean.TRUE);
					result.set_cir_expression(source.
							get_cir_expression(), source.get_data_type());
					return result;
				}
			}
			else {
				new_operands.add(new_operand);
			}
		}
		
		if(new_operands.isEmpty()) {
			result = (SedExpression) SedFactory.fetch(Boolean.FALSE);
		}
		else {
			result = null;
			for(SedExpression operand : new_operands) {
				if(result == null) {
					result = operand;
				}
				else {
					result = SedFactory.logic_ior(result, operand);
				}
			}
		}
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	
	/* relational */
	private SedExpression eval_greater_tn(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		SedExpression result = null;
		
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				result = SedComputation.greater_tn((SedConstant) loperand, (SedConstant) roperand);
			}
		}
		
		if(result == null) {
			result = SedFactory.smaller_tn(roperand, loperand);
		}
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_greater_eq(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		SedExpression result = null;
		
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				result = SedComputation.greater_eq((SedConstant) loperand, (SedConstant) roperand);
			}
		}
		
		if(result == null) {
			result = SedFactory.smaller_eq(roperand, loperand);
		}
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_smaller_tn(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		SedExpression result = null;
		
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				result = SedComputation.smaller_tn((SedConstant) loperand, (SedConstant) roperand);
			}
		}
		
		if(result == null) {
			result = SedFactory.smaller_tn(loperand, roperand);
		}
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_smaller_eq(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		SedExpression result = null;
		
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				result = SedComputation.smaller_eq((SedConstant) loperand, (SedConstant) roperand);
			}
		}
		
		if(result == null) {
			result = SedFactory.smaller_eq(loperand, roperand);
		}
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_equal_with(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		SedExpression result = null;
		
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				result = SedComputation.equal_with((SedConstant) loperand, (SedConstant) roperand);
			}
		}
		
		if(result == null) {
			result = SedFactory.equal_with(loperand, roperand);
		}
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	private SedExpression eval_not_equals(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		SedExpression result = null;
		
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				result = SedComputation.not_equals((SedConstant) loperand, (SedConstant) roperand);
			}
		}
		
		if(result == null) {
			result = SedFactory.not_equals(loperand, roperand);
		}
		
		result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
		return result;
	}
	
}
