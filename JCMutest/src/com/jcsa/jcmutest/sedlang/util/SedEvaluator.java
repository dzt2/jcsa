package com.jcsa.jcmutest.sedlang.util;

import java.util.ArrayList;
import java.util.List;

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
import com.jcsa.jcparse.lang.ctype.CFunctionType;
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
		SedExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SedConstant) {
			SedConstant result = SedComputation.arith_neg((SedConstant) operand);
			return new SedConstant(source.get_cir_expression(), 
					source.get_data_type(), result.get_constant());
		}
		else if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.negative) {
				return ((SedUnaryExpression) operand).get_operand();
			}
			else {
				SedExpression expression = new SedUnaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.negative);
				expression.add_child(operand); return expression;
			}
		}
		else if(operand instanceof SedBinaryExpression) {
			COperator operator = ((SedBinaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.arith_sub) {
				SedExpression expression = new SedBinaryExpression(
						source.get_cir_expression(), 
						source.get_data_type(), COperator.arith_sub);
				expression.add_child(((SedBinaryExpression) operand).get_roperand());
				expression.add_child(((SedBinaryExpression) operand).get_loperand());
				return expression;
			}
			else {
				SedExpression expression = new SedUnaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.negative);
				expression.add_child(operand); return expression;
			}
		}
		else {
			SedExpression expression = new SedUnaryExpression(
					source.get_cir_expression(),
					source.get_data_type(), COperator.negative);
			expression.add_child(operand); return expression;
		}
	}
	private SedExpression eval_bitws_rsv(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SedConstant) {
			SedConstant result = SedComputation.bitws_rsv((SedConstant) operand);
			return new SedConstant(source.get_cir_expression(), 
					source.get_data_type(), result.get_constant());
		}
		else if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.bit_not) {
				return ((SedUnaryExpression) operand).get_operand();
			}
			else {
				SedExpression expression = new SedUnaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.bit_not);
				expression.add_child(operand); return expression;
			}
		}
		else {
			SedExpression expression = new SedUnaryExpression(
					source.get_cir_expression(),
					source.get_data_type(), COperator.bit_not);
			expression.add_child(operand); return expression;
		}
	}
	private SedExpression eval_logic_not(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SedConstant) {
			SedConstant result = SedComputation.logic_not((SedConstant) operand);
			return new SedConstant(source.get_cir_expression(), 
					source.get_data_type(), result.get_constant());
		}
		else if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.logic_not) {
				return ((SedUnaryExpression) operand).get_operand();
			}
			else {
				SedExpression expression = new SedUnaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.logic_not);
				expression.add_child(operand); return expression;
			}
		}
		else {
			SedExpression expression = new SedUnaryExpression(
					source.get_cir_expression(),
					source.get_data_type(), COperator.logic_not);
			expression.add_child(operand); return expression;
		}
	}
	private SedExpression eval_address_of(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.dereference) {
				return ((SedUnaryExpression) operand).get_operand();
			}
			else {
				SedExpression expression = new SedUnaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.address_of);
				expression.add_child(operand); return expression;
			}
		}
		else {
			SedExpression expression = new SedUnaryExpression(
					source.get_cir_expression(),
					source.get_data_type(), COperator.address_of);
			expression.add_child(operand); return expression;
		}
	}
	private SedExpression eval_dereference(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand());
		if(operand instanceof SedUnaryExpression) {
			COperator operator = ((SedUnaryExpression) operand).get_operator().get_operator();
			if(operator == COperator.address_of) {
				return ((SedUnaryExpression) operand).get_operand();
			}
			else {
				SedExpression expression = new SedUnaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.dereference);
				expression.add_child(operand); return expression;
			}
		}
		else {
			SedExpression expression = new SedUnaryExpression(
					source.get_cir_expression(),
					source.get_data_type(), COperator.dereference);
			expression.add_child(operand); return expression;
		}
	}
	private SedExpression eval_type_cast(SedUnaryExpression source) throws Exception {
		SedExpression operand = this.evaluate(source.get_operand());
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		if(operand instanceof SedConstant) {
			if(data_type instanceof CBasicType) {
				switch(((CBasicType) data_type).get_tag()) {
				case c_bool:
				{
					CConstant constant = new CConstant();
					constant.set_bool(SedComputation.get_bool((SedConstant) operand));
					return new SedConstant(
							source.get_cir_expression(), source.get_data_type(), constant);
				}
				case c_char:
				case c_uchar:
				{
					CConstant constant = new CConstant();
					constant.set_char(SedComputation.get_char((SedConstant) operand));
					return new SedConstant(
							source.get_cir_expression(), source.get_data_type(), constant);
				}
				case c_short:
				case c_ushort:
				{
					CConstant constant = new CConstant();
					constant.set_int(SedComputation.get_short((SedConstant) operand));
					return new SedConstant(
							source.get_cir_expression(), source.get_data_type(), constant);
				}
				case c_int:
				case c_uint:
				{
					CConstant constant = new CConstant();
					constant.set_int(SedComputation.get_int((SedConstant) operand));
					return new SedConstant(
							source.get_cir_expression(), source.get_data_type(), constant);
				}
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:
				{
					CConstant constant = new CConstant();
					constant.set_long(SedComputation.get_long((SedConstant) operand));
					return new SedConstant(
							source.get_cir_expression(), source.get_data_type(), constant);
				}
				case c_float:
				{
					CConstant constant = new CConstant();
					constant.set_float(SedComputation.get_float((SedConstant) operand));
					return new SedConstant(
							source.get_cir_expression(), source.get_data_type(), constant);
				}
				case c_double:
				case c_ldouble:
				{
					CConstant constant = new CConstant();
					constant.set_double(SedComputation.get_double((SedConstant) operand));
					return new SedConstant(
							source.get_cir_expression(), source.get_data_type(), constant);
				}
				default: 
				{
					SedExpression expression = new SedUnaryExpression(
							source.get_cir_expression(),
							source.get_data_type(), COperator.assign);
					expression.add_child(operand); return expression;
				}
				}
			}
			else if(data_type instanceof CArrayType
					|| data_type instanceof CPointerType
					|| data_type instanceof CFunctionType) {
				CConstant constant = new CConstant();
				constant.set_long(SedComputation.get_long((SedConstant) operand));
				return new SedConstant(
						source.get_cir_expression(), source.get_data_type(), constant);
			}
			else if(data_type instanceof CEnumType) {
				CConstant constant = new CConstant();
				constant.set_int(SedComputation.get_int((SedConstant) operand));
				return new SedConstant(
						source.get_cir_expression(), source.get_data_type(), constant);
			}
			else {
				SedExpression expression = new SedUnaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.assign);
				expression.add_child(operand); return expression;
			}
		}
		else {
			SedExpression expression = new SedUnaryExpression(
					source.get_cir_expression(),
					source.get_data_type(), COperator.assign);
			expression.add_child(operand); return expression;
		}
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
		SedConstant constant = (SedConstant) SedParser.fetch(Integer.valueOf(0));
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
		
		if(loperand == null) {
			if(roperand == null) {
				CConstant value = new CConstant(); value.set_int(0);
				return new SedConstant(source.
						get_cir_expression(), source.get_data_type(), value);
			}
			else {
				SedExpression expression = new SedUnaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.negative);
				expression.add_child(roperand);
				return this.evaluate(expression);
			}
		}
		else {
			if(roperand == null) {
				loperand.set_cir_expression(source.get_cir_expression(), source.get_data_type());
				return loperand;
			}
			else {
				SedExpression expression = new SedBinaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.arith_sub);
				expression.add_child(loperand);
				expression.add_child(roperand);
				return expression;
			}
		}
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
		SedConstant constant = (SedConstant) SedParser.fetch(Integer.valueOf(1));
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
			CConstant constant = new CConstant(); constant.set_int(0);
			return new SedConstant(source.get_cir_expression(), source.get_data_type(), constant);
		}
		if(!SedComputation.compare(lconstant, 1)) loperands.add(lconstant);
		if(!SedComputation.compare(rconstant, 1)) roperands.add(rconstant);
		
		SedExpression loperand = this.con_operands_md(source.get_data_type(), loperands);
		SedExpression roperand = this.con_operands_md(source.get_data_type(), roperands);
		
		if(loperand == null) {
			if(roperand == null) {
				CConstant constant = new CConstant(); constant.set_int(1);
				return new SedConstant(source.get_cir_expression(), source.get_data_type(), constant);
			}
			else {
				CConstant constant = new CConstant(); constant.set_int(1);
				loperand = new SedConstant(null, source.get_data_type(), constant);
				SedExpression expression = new SedBinaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.arith_div);
				expression.add_child(loperand);
				expression.add_child(roperand);
				return expression;
			}
		}
		else {
			if(roperand == null) {
				loperand.set_cir_expression(source.get_cir_expression(), source.get_data_type());
				return loperand;
			}
			else {
				SedExpression expression = new SedBinaryExpression(
						source.get_cir_expression(),
						source.get_data_type(), COperator.arith_div);
				expression.add_child(loperand);
				expression.add_child(roperand);
				return expression;
			}
		}
	}
	
	/* {%} */
	private SedExpression eval_arith_mod(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			SedConstant result = SedComputation.arith_mod(
					(SedConstant) loperand, (SedConstant) roperand);
			result.set_cir_expression(source.
					get_cir_expression(), source.get_data_type());
			return result;
		}
		
		if(loperand instanceof SedConstant) {
			if(SedComputation.compare((SedConstant) loperand, 0)) {
				CConstant constant = new CConstant(); constant.set_int(0);
				return new SedConstant(source.get_cir_expression(),
										source.get_data_type(), constant);
			}
			else if(SedComputation.compare((SedConstant) loperand, 1)
					|| SedComputation.compare((SedConstant) loperand, -1)) {
				CConstant constant = new CConstant(); constant.set_int(1);
				return new SedConstant(source.get_cir_expression(),
										source.get_data_type(), constant);
			}
		}
		
		if(roperand instanceof SedConstant) {
			if(SedComputation.compare((SedConstant) roperand, 1)
				|| SedComputation.compare((SedConstant) roperand, -1)) {
				CConstant constant = new CConstant(); constant.set_int(0);
				return new SedConstant(source.get_cir_expression(),
										source.get_data_type(), constant);
			}
		}
		
		SedExpression expression = new SedBinaryExpression(
				source.get_cir_expression(),
				source.get_data_type(), COperator.arith_mod);
		expression.add_child(loperand);
		expression.add_child(roperand); return expression;
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
		SedConstant constant = (SedConstant) SedParser.fetch(Long.valueOf(~0L));
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
		SedConstant constant = (SedConstant) SedParser.fetch(Long.valueOf(0));
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
		SedConstant constant = (SedConstant) SedParser.fetch(Long.valueOf(0));
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
		
		SedConstant constant = (SedConstant) operands.remove(operands.size() - 1);
		if(SedComputation.compare(constant, 0L)) {
			return new SedConstant(source.get_cir_expression(), 
					source.get_data_type(), constant.get_constant());
		}
		if(!SedComputation.compare(constant, -1L)) { operands.add(constant); }
		
		SedExpression expression = this.conc_operands_ba(source.get_data_type(), operands);
		if(expression == null) {
			constant.set_cir_expression(source.get_cir_expression(), source.get_data_type());
			return constant;
		}
		else {
			expression.set_cir_expression(source.get_cir_expression(), source.get_data_type());
			return expression;
		}
	}
	private SedExpression eval_bitws_ior(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.get_operands_bt(source, source.get_operator().get_operator(), operands);
		operands = this.acc_operands_bi(
				source.get_data_type(), operands, source.get_operator().get_operator());
		
		SedConstant constant = (SedConstant) operands.remove(operands.size() - 1);
		if(SedComputation.compare(constant, ~0)) {
			return new SedConstant(source.get_cir_expression(), 
					source.get_data_type(), constant.get_constant());
		}
		if(!SedComputation.compare(constant, 0L)) { operands.add(constant); }
		
		SedExpression expression = this.conc_operands_bi(source.get_data_type(), operands);
		if(expression == null) {
			constant.set_cir_expression(source.get_cir_expression(), source.get_data_type());
			return constant;
		}
		else {
			expression.set_cir_expression(source.get_cir_expression(), source.get_data_type());
			return expression;
		}
	}
	private SedExpression eval_bitws_xor(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.get_operands_bt(source, source.get_operator().get_operator(), operands);
		operands = this.acc_operands_bx(
				source.get_data_type(), operands, source.get_operator().get_operator());
		
		SedConstant constant = (SedConstant) operands.remove(operands.size() - 1);
		if(!SedComputation.compare(constant, 0L)) { operands.add(constant); }
		
		SedExpression expression = this.conc_operands_bx(source.get_data_type(), operands);
		if(expression == null) {
			constant.set_cir_expression(source.get_cir_expression(), source.get_data_type());
			return constant;
		}
		else {
			expression.set_cir_expression(source.get_cir_expression(), source.get_data_type());
			return expression;
		}
	}
	private SedExpression eval_bitws_lsh(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			SedConstant result = 
					SedComputation.bitws_lsh((SedConstant) loperand, (SedConstant) roperand);
			result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
			return result;
		}
		
		if(loperand instanceof SedConstant) {
			if(SedComputation.compare((SedConstant) loperand, 0)) {
				loperand.set_cir_expression(source.get_cir_expression(), source.get_data_type());
				return loperand;
			}
		}
		
		if(roperand instanceof SedConstant) {
			if(SedComputation.compare((SedConstant) roperand, 0)) {
				loperand.set_cir_expression(source.get_cir_expression(), source.get_data_type());
				return loperand;
			}
		}
		
		SedExpression expression = new SedBinaryExpression(
				source.get_cir_expression(),
				source.get_data_type(), COperator.left_shift);
		expression.add_child(loperand);
		expression.add_child(roperand); return expression;
	}
	private SedExpression eval_bitws_rsh(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SedConstant && roperand instanceof SedConstant) {
			SedConstant result = 
					SedComputation.bitws_rsh((SedConstant) loperand, (SedConstant) roperand);
			result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
			return result;
		}
		
		if(loperand instanceof SedConstant) {
			if(SedComputation.compare((SedConstant) loperand, 0)) {
				loperand.set_cir_expression(source.get_cir_expression(), source.get_data_type());
				return loperand;
			}
		}
		
		if(roperand instanceof SedConstant) {
			if(SedComputation.compare((SedConstant) roperand, 0)) {
				loperand.set_cir_expression(source.get_cir_expression(), source.get_data_type());
				return loperand;
			}
		}
		
		SedExpression expression = new SedBinaryExpression(
				source.get_cir_expression(),
				source.get_data_type(), COperator.righ_shift);
		expression.add_child(loperand);
		expression.add_child(roperand); return expression;
	}
	
	/* {&&, ||} */
	private SedExpression eval_logic_and(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.get_operands_bt(source, COperator.logic_and, operands);
		
		List<SedExpression> new_operands = new ArrayList<SedExpression>();
		for(SedExpression operand : operands) {
			SedExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SedConstant) {
				if(!SedComputation.get_bool((SedConstant) new_operand)) {
					CConstant constant = new CConstant();
					constant.set_bool(false);
					return new SedConstant(source.get_cir_expression(),
							source.get_data_type(), constant);
				}
			}
			else {
				new_operands.add(new_operand);
			}
		}
		
		if(new_operands.isEmpty()) {
			CConstant constant = new CConstant();
			constant.set_bool(true);
			return new SedConstant(source.get_cir_expression(),
					source.get_data_type(), constant);
		}
		else {
			SedExpression expression = null;
			for(SedExpression operand : new_operands) {
				if(expression == null) {
					expression = operand;
				}
				else {
					expression = SedFactory.logic_and(expression, operand);
				}
			}
			expression.set_cir_expression(source.get_cir_expression(), source.get_data_type());
			return expression;
		}
	}
	private SedExpression eval_logic_ior(SedBinaryExpression source) throws Exception {
		List<SedExpression> operands = new ArrayList<SedExpression>();
		this.get_operands_bt(source, COperator.logic_or, operands);
		
		List<SedExpression> new_operands = new ArrayList<SedExpression>();
		for(SedExpression operand : operands) {
			SedExpression new_operand = this.evaluate(operand);
			if(new_operand instanceof SedConstant) {
				if(SedComputation.get_bool((SedConstant) new_operand)) {
					CConstant constant = new CConstant();
					constant.set_bool(true);
					return new SedConstant(source.get_cir_expression(),
							source.get_data_type(), constant);
				}
			}
			else {
				new_operands.add(new_operand);
			}
		}
		
		if(new_operands.isEmpty()) {
			CConstant constant = new CConstant();
			constant.set_bool(false);
			return new SedConstant(source.get_cir_expression(),
					source.get_data_type(), constant);
		}
		else {
			SedExpression expression = null;
			for(SedExpression operand : new_operands) {
				if(expression == null) {
					expression = operand;
				}
				else {
					expression = SedFactory.logic_ior(expression, operand);
				}
			}
			expression.set_cir_expression(source.get_cir_expression(), source.get_data_type());
			return expression;
		}
	}
	
	/* relational */
	private SedExpression eval_greater_tn(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				SedConstant result = SedComputation.greater_tn(
						(SedConstant) loperand, (SedConstant) roperand);
				result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
				return result;
			}
		}
		
		SedExpression expression = new SedBinaryExpression(
				source.get_cir_expression(),
				source.get_data_type(), COperator.smaller_tn);
		expression.add_child(roperand);
		expression.add_child(loperand);
		return expression;
	}
	private SedExpression eval_greater_eq(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				SedConstant result = SedComputation.greater_eq(
						(SedConstant) loperand, (SedConstant) roperand);
				result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
				return result;
			}
		}
		
		SedExpression expression = new SedBinaryExpression(
				source.get_cir_expression(),
				source.get_data_type(), COperator.smaller_eq);
		expression.add_child(roperand);
		expression.add_child(loperand);
		return expression;
	}
	private SedExpression eval_smaller_tn(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				SedConstant result = SedComputation.smaller_tn(
						(SedConstant) loperand, (SedConstant) roperand);
				result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
				return result;
			}
		}
		
		SedExpression expression = new SedBinaryExpression(
				source.get_cir_expression(),
				source.get_data_type(), COperator.smaller_tn);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	private SedExpression eval_smaller_eq(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				SedConstant result = SedComputation.smaller_eq(
						(SedConstant) loperand, (SedConstant) roperand);
				result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
				return result;
			}
		}
		
		SedExpression expression = new SedBinaryExpression(
				source.get_cir_expression(),
				source.get_data_type(), COperator.smaller_eq);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	private SedExpression eval_equal_with(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				SedConstant result = SedComputation.equal_with(
						(SedConstant) loperand, (SedConstant) roperand);
				result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
				return result;
			}
		}
		
		SedExpression expression = new SedBinaryExpression(
				source.get_cir_expression(),
				source.get_data_type(), COperator.equal_with);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	private SedExpression eval_not_equals(SedBinaryExpression source) throws Exception {
		SedExpression loperand = this.evaluate(source.get_loperand());
		SedExpression roperand = this.evaluate(source.get_roperand());
		
		if(loperand instanceof SedConstant) {
			if(roperand instanceof SedConstant) {
				SedConstant result = SedComputation.not_equals(
						(SedConstant) loperand, (SedConstant) roperand);
				result.set_cir_expression(source.get_cir_expression(), source.get_data_type());
				return result;
			}
		}
		
		SedExpression expression = new SedBinaryExpression(
				source.get_cir_expression(),
				source.get_data_type(), COperator.not_equals);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	
}
