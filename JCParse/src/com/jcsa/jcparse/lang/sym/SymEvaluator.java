package com.jcsa.jcparse.lang.sym;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * To evaluate the symbolic expression.
 * 
 * @author yukimula
 *
 */
public class SymEvaluator {
	
	/** contextual data **/
	private SymEvaluateContext context;
	
	/**
	 * create an evaluator with empty contextual data
	 */
	public SymEvaluator() {
		this.context = new SymEvaluateContext();
	}
	
	/**
	 * @return the contextual data for symbolic evaluation
	 */
	public SymEvaluateContext get_context() { return this.context; }
	
	/* evaluation methods */
	/**
	 * @param source
	 * @return the symbolic result evaluated from source
	 * @throws Exception
	 */
	private SymNode eval(SymNode source) throws Exception {
		/* 1. syntax-directed translation */
		SymNode output;
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(this.context.has_state(source))
			output = this.context.get_state(source);
		else if(source instanceof SymIdentifier)
			output = this.eval_identifier((SymIdentifier) source);
		else if(source instanceof SymConstant)
			output = this.eval_constant((SymConstant) source);
		else if(source instanceof SymLiteral)
			output = this.eval_literal((SymLiteral) source);
		else if(source instanceof SymField)
			output = this.eval_field((SymField) source);
		else if(source instanceof SymFieldExpression)
			output = this.eval_field_expression((SymFieldExpression) source);
		else if(source instanceof SymInitializerList)
			output = this.eval_initializer_list((SymInitializerList) source);
		else if(source instanceof SymArgumentList)
			output = this.eval_argument_list((SymArgumentList) source);
		else if(source instanceof SymFunCallExpression)
			output = this.eval_fun_call_expression((SymFunCallExpression) source);
		else if(source instanceof SymUnaryExpression) {
			switch(((SymUnaryExpression) source).get_operator()) {
			case positive:		
				output = this.eval_positive_expression((SymUnaryExpression) source); 
				break;
			case negative:
				output = this.eval_negative_expression((SymUnaryExpression) source);
				break;
			case bit_not:
				output = this.eval_bitws_rsv_expression((SymUnaryExpression) source);
				break;
			case logic_not:
				output = this.eval_logic_not_expression((SymUnaryExpression) source);
				break;
			case address_of:
				output = this.eval_address_of_expression((SymUnaryExpression) source);
				break;
			case dereference:
				output = this.eval_dereference_expression((SymUnaryExpression) source);
				break;
			case assign:
				output = this.eval_cast_expression((SymUnaryExpression) source);
				break;
			default: throw new IllegalArgumentException("Unsupport operator");
			}
		}
		else if(source instanceof SymMultiExpression) {
			switch(((SymMultiExpression) source).get_operator()) {
			case arith_add:	
				output = this.eval_arith_add_expression((SymMultiExpression) source); 
				break;
			case arith_mul:
				output = this.eval_arith_mul_expression((SymMultiExpression) source);
				break;
			case bit_and:
				output = this.eval_bitws_and_expression((SymMultiExpression) source);
				break;
			case bit_or:
				output = this.eval_bitws_ior_expression((SymMultiExpression) source);
				break;
			case bit_xor:
				output = this.eval_bitws_xor_expression((SymMultiExpression) source);
				break;
			case logic_and:
				output = this.eval_logic_and_expression((SymMultiExpression) source);
				break;
			case logic_or:
				output = this.eval_logic_ior_expression((SymMultiExpression) source);
				break;
			default: throw new IllegalArgumentException("Invalid operator");
			}
		}
		else if(source instanceof SymBinaryExpression) {
			switch(((SymBinaryExpression) source).get_operator()) {
			case arith_sub:
				output = this.eval_arith_sub_expression((SymBinaryExpression) source);
				break;
			case arith_div:
				output = this.eval_arith_div_expression((SymBinaryExpression) source);
				break;
			case arith_mod:
				output = this.eval_arith_mod_expression((SymBinaryExpression) source);
				break;
			case left_shift:
				output = this.eval_bitws_lsh_expression((SymBinaryExpression) source);
				break;
			case righ_shift:
				output = this.eval_bitws_rsh_expression((SymBinaryExpression) source);
				break;
			case greater_tn:
				output = this.eval_greater_tn_expression((SymBinaryExpression) source);
				break;
			case greater_eq:
				output = this.eval_greater_eq_expression((SymBinaryExpression) source);
				break;
			case smaller_tn:
				output = this.eval_smaller_tn_expression((SymBinaryExpression) source);
				break;
			case smaller_eq:
				output = this.eval_smaller_eq_expression((SymBinaryExpression) source);
				break;
			case equal_with:
				output = this.eval_equal_with_expression((SymBinaryExpression) source);
				break;
			case not_equals:
				output = this.eval_not_equals_expression((SymBinaryExpression) source);
				break;
			default: throw new IllegalArgumentException("Invalid operator");
			}
		}
		else
			throw new IllegalArgumentException("Unsupport: " + source);
		
		/* 2. contextual evaluation and debugging */
		if(this.context.has_state(output)) {
			output = this.context.get_state(output);
		}
		this.context.debug_table.put(source, output);
		
		/* 3. return the final symbolic result */
		return output;
	}
	private SymNode eval_identifier(SymIdentifier source) throws Exception {
		return source.clone();
	}
	private SymNode eval_constant(SymConstant source) throws Exception {
		return source.clone();
	}
	private SymNode eval_literal(SymLiteral source) throws Exception {
		return source.clone();
	}
	private SymNode eval_field(SymField source) throws Exception {
		return source.clone();
	}
	private SymNode eval_field_expression(SymFieldExpression source) throws Exception {
		SymNode body = this.eval(source.get_body());
		SymNode field = this.eval(source.get_field());
		SymNode result = new SymFieldExpression(source.get_data_type());
		result.add_child(body);
		result.add_child(field);
		return result;
	}
	private SymNode eval_initializer_list(SymInitializerList source) throws Exception {
		SymNode result = new SymInitializerList();
		for(SymNode child : source.get_children()) {
			result.add_child(this.eval(child));
		}
		return result;
	}
	private SymNode eval_argument_list(SymArgumentList source) throws Exception {
		SymNode arguments = new SymArgumentList();
		for(int k = 0; k < source.number_of_arguments(); k++) {
			arguments.add_child(this.eval(source.get_argument(k)));
		}
		return arguments;
	}
	private SymNode eval_fun_call_expression(SymFunCallExpression source) throws Exception {
		SymNode function = this.eval(source.get_function());
		SymNode arguments = this.eval(source.get_argument_list());
		SymFunCallExpression invoke_expression = new SymFunCallExpression(source.get_data_type());
		invoke_expression.add_child(function);
		invoke_expression.add_child(arguments);
		if(function instanceof SymIdentifier) {
			return this.context.invoke(invoke_expression);
		}
		else {
			return invoke_expression;
		}
	}
	private SymNode eval_positive_expression(SymUnaryExpression source) throws Exception {
		return this.eval(source.get_operand());
	}
	private SymNode eval_negative_expression(SymUnaryExpression source) throws Exception {
		/** 0. recursively evaluate the operand **/
		SymNode operand = this.eval(source.get_operand()), output;
		
		/** 1. constant ==> -constant **/
		if(operand instanceof SymConstant) {
			Object value = ((SymConstant) operand).number_of();
			CConstant constant = new CConstant();
			if(value instanceof Long) {
				constant.set_long(-((Long) value).longValue());
			}
			else {
				constant.set_double(-((Double) value).doubleValue());
			}
			output = new SymConstant(source.get_data_type(), constant);
		}
		/** 2. -operand ==> operand **/
		else if(operand instanceof SymUnaryExpression) {
			if(((SymUnaryExpression) operand).get_operator() == COperator.negative) {
				output = ((SymUnaryExpression) operand).get_operand().clone();
			}
			else {
				output = null;
			}
		}
		/** 3. x - y ==> y - x **/
		else if(operand instanceof SymBinaryExpression) {
			if(((SymBinaryExpression) operand).get_operator() == COperator.arith_sub) {
				output = new SymBinaryExpression(source.get_data_type(), COperator.arith_sub);
				output.add_child(((SymBinaryExpression) operand).get_roperand());
				output.add_child(((SymBinaryExpression) operand).get_loperand());
			}
			else {
				output = null;
			}
		}
		else {
			output = null;
		}
		
		/** 5. general induction **/
		if(output == null) {
			output = new SymUnaryExpression(source.get_data_type(), COperator.negative);
			output.add_child(operand);
		}
		return output;
	}
	private SymNode eval_bitws_rsv_expression(SymUnaryExpression source) throws Exception {
		/** 0. recursively evaluate the operand **/
		SymNode operand = this.eval(source.get_operand()), output;
		
		/** 1. constant ==> ~constant **/
		if(operand instanceof SymConstant) {
			long value = ((SymConstant) operand).integer_of();
			CConstant constant = new CConstant();
			constant.set_ldouble(~value);
			output = new SymConstant(source.get_data_type(), constant);
		}
		/** 2. ~operand ==> operand **/
		else if(operand instanceof SymUnaryExpression) {
			if(((SymUnaryExpression) operand).get_operator() == COperator.bit_not) {
				output = ((SymUnaryExpression) operand).get_operand().clone();
			}
			else {
				output = null;
			}
		}
		else {
			output = null;
		}
		
		/** 3. general induction **/
		if(output == null) {
			output = new SymUnaryExpression(source.get_data_type(), COperator.bit_not);
			output.add_child(operand);
		}
		return output;
	}
	private SymNode eval_logic_not_expression(SymUnaryExpression source) throws Exception {
		/** 0. recursively evaluate the operand **/
		SymNode operand = this.eval(source.get_operand()), output;
		
		/** 1. simplification **/
		if(operand instanceof SymConstant) {
			boolean value = ((SymConstant) operand).boolean_of();
			CConstant constant = new CConstant();
			constant.set_bool(!value);
			output = new SymConstant(source.get_data_type(), constant);
		}
		else if(operand instanceof SymUnaryExpression) {
			if(((SymUnaryExpression) operand).get_operator() == COperator.logic_not) {
				output = ((SymUnaryExpression) operand).get_operand().clone();
			}
			else {
				output = null;
			}
		}
		else if(operand instanceof SymBinaryExpression) {
			SymNode loperand = ((SymBinaryExpression) operand).get_loperand();
			SymNode roperand = ((SymBinaryExpression) operand).get_roperand();
			loperand = loperand.clone(); roperand = roperand.clone();
			switch(((SymBinaryExpression) operand).get_operator()) {
			case greater_tn:
			{
				output = new SymBinaryExpression(source.get_data_type(), COperator.smaller_eq);
				output.add_child(loperand);
				output.add_child(roperand);
			}
			break;
			case greater_eq:
			{
				output = new SymBinaryExpression(source.get_data_type(), COperator.smaller_tn);
				output.add_child(loperand);
				output.add_child(roperand);
			}
			break;
			case smaller_tn:
			{
				output = new SymBinaryExpression(source.get_data_type(), COperator.smaller_eq);
				output.add_child(roperand);
				output.add_child(loperand);
			}
			break;
			case smaller_eq:
			{
				output = new SymBinaryExpression(source.get_data_type(), COperator.smaller_tn);
				output.add_child(roperand);
				output.add_child(loperand);
			}
			break;
			case equal_with:
			{
				output = new SymBinaryExpression(source.get_data_type(), COperator.not_equals);
				output.add_child(loperand);
				output.add_child(roperand);
			}
			break;
			case not_equals:
			{
				output = new SymBinaryExpression(source.get_data_type(), COperator.equal_with);
				output.add_child(loperand);
				output.add_child(roperand);
			}
			break;
			default:	
			{
				output = null; 
			}
			break;
			}
		}
		else {
			output = null;
		}
		
		/** 3. general induction **/
		if(output == null) {
			output = new SymUnaryExpression(source.get_data_type(), COperator.logic_not);
			output.add_child(operand);
		}
		return output;
	}
	private SymNode eval_address_of_expression(SymUnaryExpression source) throws Exception {
		SymNode operand = this.eval(source.get_operand());
		if(operand instanceof SymUnaryExpression) {
			if(((SymUnaryExpression) operand).get_operator() == COperator.dereference) {
				return ((SymUnaryExpression) operand).get_operand().clone();
			}
		}
		
		SymNode result = new SymUnaryExpression(source.get_data_type(), COperator.address_of);
		result.add_child(operand);
		return result;
	}
	private SymNode eval_dereference_expression(SymUnaryExpression source) throws Exception {
		SymNode operand = this.eval(source.get_operand());
		if(operand instanceof SymUnaryExpression) {
			if(((SymUnaryExpression) operand).get_operator() == COperator.address_of) {
				return ((SymUnaryExpression) operand).get_operand().clone();
			}
		}
		
		SymNode result = new SymUnaryExpression(source.get_data_type(), COperator.dereference);
		result.add_child(operand);
		return result;
	}
	private SymNode eval_cast_void_expression(CType cast_type, SymNode operand) throws Exception {
		SymNode result = new SymUnaryExpression(cast_type, COperator.assign);
		result.add_child(operand); 
		return result;
	}
	private SymNode eval_cast_bool_expression(CType cast_type, SymNode operand) throws Exception {
		if(operand instanceof SymConstant) {
			boolean value = ((SymConstant) operand).boolean_of();
			CConstant constant = new CConstant();
			constant.set_bool(value);
			return new SymConstant(cast_type, constant);
		}
		else if(operand instanceof SymUnaryExpression) {
			if(((SymUnaryExpression) operand).get_operator() == COperator.address_of) {
				CConstant constant = new CConstant();
				constant.set_bool(true);
				return new SymConstant(cast_type, constant);
			}
			else {
				SymNode result = new SymUnaryExpression(cast_type, COperator.assign);
				result.add_child(operand); 
				return result;
			}
		}
		else {
			SymNode result = new SymUnaryExpression(cast_type, COperator.assign);
			result.add_child(operand); 
			return result;
		}
	}
	private SymNode eval_cast_char_expression(CType cast_type, SymNode operand) throws Exception {
		if(operand instanceof SymConstant) {
			char value = (char) ((SymConstant) operand).integer_of().longValue();
			CConstant constant = new CConstant();
			constant.set_char(value);
			return new SymConstant(cast_type, constant);
		}
		else {
			SymNode result = new SymUnaryExpression(cast_type, COperator.assign);
			result.add_child(operand); 
			return result;
		}
	}
	private SymNode eval_cast_int_expression(CType cast_type, SymNode operand) throws Exception {
		if(operand instanceof SymConstant) {
			int value = (int) ((SymConstant) operand).integer_of().longValue();
			CConstant constant = new CConstant();
			constant.set_int(value);
			return new SymConstant(cast_type, constant);
		}
		else {
			SymNode result = new SymUnaryExpression(cast_type, COperator.assign);
			result.add_child(operand); 
			return result;
		}
	}
	private SymNode eval_cast_long_expression(CType cast_type, SymNode operand) throws Exception {
		if(operand instanceof SymConstant) {
			long value = ((SymConstant) operand).integer_of().longValue();
			CConstant constant = new CConstant();
			constant.set_long(value);
			return new SymConstant(cast_type, constant);
		}
		else {
			SymNode result = new SymUnaryExpression(cast_type, COperator.assign);
			result.add_child(operand); 
			return result;
		}
	}
	private SymNode eval_cast_float_expression(CType cast_type, SymNode operand) throws Exception {
		if(operand instanceof SymConstant) {
			double value = ((SymConstant) operand).double_of().doubleValue();
			CConstant constant = new CConstant();
			constant.set_float((float) value);
			return new SymConstant(cast_type, constant);
		}
		else {
			SymNode result = new SymUnaryExpression(cast_type, COperator.assign);
			result.add_child(operand); 
			return result;
		}
	}
	private SymNode eval_cast_double_expression(CType cast_type, SymNode operand) throws Exception {
		if(operand instanceof SymConstant) {
			double value = ((SymConstant) operand).double_of().doubleValue();
			CConstant constant = new CConstant();
			constant.set_double(value);
			return new SymConstant(cast_type, constant);
		}
		else {
			SymNode result = new SymUnaryExpression(cast_type, COperator.assign);
			result.add_child(operand); 
			return result;
		}
	}
	private SymNode eval_cast_expression(SymUnaryExpression source) throws Exception {
		/** 1. recursively evaluation **/
		CType cast_type = 
				CTypeAnalyzer.get_value_type(source.get_data_type());
		SymNode operand = this.eval(source.get_operand());
		
		/** 2. type-directed casting **/
		if(cast_type instanceof CBasicType) {
			switch(((CBasicType) cast_type).get_tag()) {
			case c_void:	return this.eval_cast_void_expression(cast_type, operand);
			case c_bool:	return this.eval_cast_bool_expression(cast_type, operand);
			case c_char:	
			case c_uchar:	return this.eval_cast_char_expression(cast_type, operand);
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:	return this.eval_cast_int_expression(cast_type, operand);
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:	return this.eval_cast_long_expression(cast_type, operand);
			case c_float:	return this.eval_cast_float_expression(cast_type, operand);
			case c_double:
			case c_ldouble:	return this.eval_cast_double_expression(cast_type, operand);
			default: 		return this.eval_cast_void_expression(cast_type, operand);
			}
		}
		else if(cast_type instanceof CPointerType) {
			SymNode result = new SymUnaryExpression(cast_type, COperator.assign);
			result.add_child(operand); 
			return result;
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + cast_type);
		}
	}
	private SymNode eval_arith_add_expression(SymMultiExpression source) throws Exception {
		/** 1. recursively evaluate the operands **/
		List<SymNode> operands = new ArrayList<SymNode>();
		for(SymNode child : source.get_children()) {
			SymNode operand = this.eval(child);
			if(operand instanceof SymMultiExpression) {
				if(((SymMultiExpression) operand).get_operator() == COperator.arith_add) {
					for(SymNode operand_child : operand.get_children()) 
						operands.add(operand_child.clone());
				}
				else {
					operands.add(operand);
				}
			}
			else {
				operands.add(operand);
			}
		}
		
		/** 2. divide into constant and variables **/
		CConstant init_constant = new CConstant();
		init_constant.set_long(0L);
		SymConstant constant = new SymConstant(
				source.get_data_type(), init_constant);
		List<SymNode> variables = new ArrayList<SymNode>();
		for(SymNode operand : operands) {
			if(operand instanceof SymConstant) {
				constant = this.arith_add(source.get_data_type(), 
								constant, (SymConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		
		/** 3. generating symbolic result **/
		if(variables.isEmpty()) {
			return constant;
		}
		else if(variables.size() == 1 && this.is_zero(constant)) {
			return variables.get(0);
		}
		else {
			SymNode result = new SymMultiExpression(
					source.get_data_type(), COperator.arith_add);
			for(SymNode variable : variables) {
				result.add_child(variable);
			}
			if(!this.is_zero(constant))
				result.add_child(constant);
			return result;
		}
	}
	private SymNode eval_arith_mul_expression(SymMultiExpression source) throws Exception {
		/** 1. recursively evaluate the operands **/
		List<SymNode> operands = new ArrayList<SymNode>();
		for(SymNode child : source.get_children()) {
			SymNode operand = this.eval(child);
			if(operand instanceof SymMultiExpression) {
				if(((SymMultiExpression) operand).get_operator() == COperator.arith_mul) {
					for(SymNode operand_child : operand.get_children()) 
						operands.add(operand_child.clone());
				}
				else {
					operands.add(operand);
				}
			}
			else {
				operands.add(operand);
			}
		}
		
		/** 2. divide into constant and variables **/
		CConstant init_constant = new CConstant();
		init_constant.set_long(1L);
		SymConstant constant = new SymConstant(
				source.get_data_type(), init_constant);
		List<SymNode> variables = new ArrayList<SymNode>();
		for(SymNode operand : operands) {
			if(operand instanceof SymConstant) {
				constant = this.arith_mul(source.get_data_type(), 
								constant, (SymConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		
		/** 3. generating symbolic result **/
		if(variables.isEmpty() || this.is_zero(constant)) {
			return constant;
		}
		else if(variables.size() == 1 && this.is_one(constant)) {
			return variables.get(0);
		}
		else {
			SymNode result = new SymMultiExpression(
					source.get_data_type(), COperator.arith_mul);
			for(SymNode variable : variables) {
				result.add_child(variable);
			}
			if(!this.is_one(constant))
				result.add_child(constant);
			return result;
		}
	}
	private SymNode eval_bitws_and_expression(SymMultiExpression source) throws Exception {
		/** 1. recursively evaluate the operands **/
		List<SymNode> operands = new ArrayList<SymNode>();
		for(SymNode child : source.get_children()) {
			SymNode operand = this.eval(child);
			if(operand instanceof SymMultiExpression) {
				if(((SymMultiExpression) operand).get_operator() == COperator.bit_and) {
					for(SymNode operand_child : operand.get_children()) 
						operands.add(operand_child.clone());
				}
				else {
					operands.add(operand);
				}
			}
			else {
				operands.add(operand);
			}
		}
		
		/** 2. divide into constant and variables **/
		CConstant init_constant = new CConstant();
		init_constant.set_long(~0L);
		SymConstant constant = new SymConstant(
				source.get_data_type(), init_constant);
		List<SymNode> variables = new ArrayList<SymNode>();
		for(SymNode operand : operands) {
			if(operand instanceof SymConstant) {
				constant = this.bitws_and(source.get_data_type(), 
								constant, (SymConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		
		/** 3. generating the symbolic result **/
		if(variables.isEmpty() || this.is_zero(constant)) {
			return constant;
		}
		else if(variables.size() == 1 && this.is_rsv_zero(constant)) {
			return variables.get(0);
		}
		else {
			SymNode result = new SymMultiExpression(
					source.get_data_type(), COperator.bit_and);
			for(SymNode variable : variables) {
				result.add_child(variable);
			}
			if(!this.is_rsv_zero(constant)) {
				result.add_child(constant);
			}
			return result;
		}
	}
	private SymNode eval_bitws_ior_expression(SymMultiExpression source) throws Exception {
		/** 1. recursively evaluate the operands **/
		List<SymNode> operands = new ArrayList<SymNode>();
		for(SymNode child : source.get_children()) {
			SymNode operand = this.eval(child);
			if(operand instanceof SymMultiExpression) {
				if(((SymMultiExpression) operand).get_operator() == COperator.bit_or) {
					for(SymNode operand_child : operand.get_children()) 
						operands.add(operand_child.clone());
				}
				else {
					operands.add(operand);
				}
			}
			else {
				operands.add(operand);
			}
		}
		
		/** 2. divide into constant and variables **/
		CConstant init_constant = new CConstant();
		init_constant.set_long(0L);
		SymConstant constant = new SymConstant(
				source.get_data_type(), init_constant);
		List<SymNode> variables = new ArrayList<SymNode>();
		for(SymNode operand : operands) {
			if(operand instanceof SymConstant) {
				constant = this.bitws_ior(source.get_data_type(), 
								constant, (SymConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		
		/** 3. generating the symbolic result **/
		if(variables.isEmpty() || this.is_rsv_zero(constant)) {
			return constant;
		}
		else if(variables.size() == 1 && this.is_zero(constant)) {
			return variables.get(0);
		}
		else {
			SymNode result = new SymMultiExpression(
					source.get_data_type(), COperator.bit_or);
			for(SymNode variable : variables) {
				result.add_child(variable);
			}
			if(!this.is_zero(constant)) {
				result.add_child(constant);
			}
			return result;
		}
	}
	private SymNode eval_bitws_xor_expression(SymMultiExpression source) throws Exception {
		/** 1. recursively evaluate the operands **/
		List<SymNode> operands = new ArrayList<SymNode>();
		for(SymNode child : source.get_children()) {
			SymNode operand = this.eval(child);
			if(operand instanceof SymMultiExpression) {
				if(((SymMultiExpression) operand).get_operator() == COperator.bit_xor) {
					for(SymNode operand_child : operand.get_children()) 
						operands.add(operand_child.clone());
				}
				else {
					operands.add(operand);
				}
			}
			else {
				operands.add(operand);
			}
		}
		
		/** 2. divide into constant and variables **/
		CConstant init_constant = new CConstant();
		init_constant.set_long(0L);
		SymConstant constant = new SymConstant(
				source.get_data_type(), init_constant);
		List<SymNode> variables = new ArrayList<SymNode>();
		for(SymNode operand : operands) {
			if(operand instanceof SymConstant) {
				constant = this.bitws_xor(source.get_data_type(), 
								constant, (SymConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		
		/** 3. generating symbolic result **/
		if(variables.isEmpty()) {
			return constant;
		}
		else if(variables.size() == 1 && this.is_zero(constant)) {
			return variables.get(0);
		}
		else {
			SymNode result = new SymMultiExpression(
					source.get_data_type(), COperator.bit_xor);
			for(SymNode variable : variables) {
				result.add_child(variable);
			}
			if(!this.is_zero(constant)) {
				result.add_child(constant);
			}
			return result;
		}
	}
	private SymNode eval_logic_and_expression(SymMultiExpression source) throws Exception {
		/** 1. recursively evaluate the operands **/
		List<SymNode> operands = new ArrayList<SymNode>();
		for(SymNode child : source.get_children()) {
			SymNode operand = this.eval(child);
			if(operand instanceof SymMultiExpression) {
				if(((SymMultiExpression) operand).get_operator() == COperator.logic_and) {
					for(SymNode operand_child : operand.get_children()) 
						operands.add(operand_child.clone());
				}
				else {
					operands.add(operand);
				}
			}
			else {
				operands.add(operand);
			}
		}
		
		/** 2. divide into constant and variables **/
		CConstant init_constant = new CConstant();
		init_constant.set_bool(true);
		SymConstant constant = new SymConstant(
				source.get_data_type(), init_constant);
		List<SymNode> variables = new ArrayList<SymNode>();
		for(SymNode operand : operands) {
			if(operand instanceof SymConstant) {
				constant = this.logic_and(source.get_data_type(), 
								constant, (SymConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		
		/** 3. generating symbolic result **/
		if(variables.isEmpty() || !constant.boolean_of()) {
			return constant;
		}
		else if(variables.size() == 1) {
			return variables.get(0);
		}
		else {
			SymNode result = new SymMultiExpression(
					source.get_data_type(), COperator.logic_and);
			for(SymNode variable : variables) {
				result.add_child(variable);
			}
			return result;
		}
	}
	private SymNode eval_logic_ior_expression(SymMultiExpression source) throws Exception {
		/** 1. recursively evaluate the operands **/
		List<SymNode> operands = new ArrayList<SymNode>();
		for(SymNode child : source.get_children()) {
			SymNode operand = this.eval(child);
			if(operand instanceof SymMultiExpression) {
				if(((SymMultiExpression) operand).get_operator() == COperator.logic_or) {
					for(SymNode operand_child : operand.get_children()) 
						operands.add(operand_child.clone());
				}
				else {
					operands.add(operand);
				}
			}
			else {
				operands.add(operand);
			}
		}
		
		/** 2. divide into constant and variables **/
		CConstant init_constant = new CConstant();
		init_constant.set_bool(false);
		SymConstant constant = new SymConstant(
				source.get_data_type(), init_constant);
		List<SymNode> variables = new ArrayList<SymNode>();
		for(SymNode operand : operands) {
			if(operand instanceof SymConstant) {
				constant = this.logic_ior(source.get_data_type(), 
								constant, (SymConstant) operand);
			}
			else {
				variables.add(operand);
			}
		}
		
		/** 3. generating symbolic result **/
		if(variables.isEmpty() || constant.boolean_of()) {
			return constant;
		}
		else if(variables.size() == 1) {
			return variables.get(0);
		}
		else {
			SymNode result = new SymMultiExpression(
					source.get_data_type(), COperator.logic_or);
			for(SymNode variable : variables) {
				result.add_child(variable);
			}
			return result;
		}
	}
	private SymNode eval_arith_sub_expression(SymBinaryExpression source) throws Exception {
		SymNode loperand = this.eval(source.get_loperand());
		SymNode roperand = this.eval(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			if(roperand instanceof SymConstant) {
				return this.arith_sub(source.get_data_type(), 
						(SymConstant) loperand, (SymConstant) roperand);
			}
		}
		
		SymNode result = new SymBinaryExpression(
				source.get_data_type(), COperator.arith_sub);
		result.add_child(loperand);
		result.add_child(roperand);
		return result;
	}
	private SymNode eval_arith_div_expression(SymBinaryExpression source) throws Exception {
		SymNode loperand = this.eval(source.get_loperand());
		SymNode roperand = this.eval(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			if(roperand instanceof SymConstant) {
				return this.arith_div(source.get_data_type(), 
						(SymConstant) loperand, (SymConstant) roperand);
			}
			else if(this.is_zero((SymConstant) loperand)) {
				
			}
		}
		else if(roperand instanceof SymConstant) {
			if(this.is_one((SymConstant) roperand)) {
				return loperand;
			}
			else if(this.is_neg_one((SymConstant) roperand)) {
				SymNode result = new SymUnaryExpression(
						source.get_data_type(), COperator.negative);
				result.add_child(loperand);
				return result;
			}
		}
		
		SymNode result = new SymBinaryExpression(
				source.get_data_type(), COperator.arith_div);
		result.add_child(loperand);
		result.add_child(roperand);
		return result;
	}
	private SymNode eval_arith_mod_expression(SymBinaryExpression source) throws Exception {
		SymNode loperand = this.eval(source.get_loperand());
		SymNode roperand = this.eval(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			if(roperand instanceof SymConstant) {
				return this.arith_mod(source.get_data_type(), 
						(SymConstant) loperand, (SymConstant) roperand);
			}
			else if(this.is_zero((SymConstant) loperand) || 
					this.is_one((SymConstant) loperand)) {
				return loperand;
			}
		}
		else if(roperand instanceof SymConstant) {
			if(this.is_neg_one((SymConstant) roperand) || 
					this.is_one((SymConstant) roperand)) {
				CConstant constant = new CConstant();
				constant.set_long(0L);
				return new SymConstant(source.get_data_type(), constant);
			}
		}
		
		SymNode result = new SymBinaryExpression(
				source.get_data_type(), COperator.arith_mod);
		result.add_child(loperand);
		result.add_child(roperand);
		return result;
	}
	private SymNode eval_bitws_lsh_expression(SymBinaryExpression source) throws Exception {
		SymNode loperand = this.eval(source.get_loperand());
		SymNode roperand = this.eval(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			if(roperand instanceof SymConstant) {
				return this.bitws_lsh(source.get_data_type(), 
						(SymConstant) loperand, (SymConstant) roperand);
			}
			else if(this.is_zero((SymConstant) loperand)) {
				CConstant constant = new CConstant();
				constant.set_long(0L);
				return new SymConstant(source.get_data_type(), constant);
			}
		}
		else if(roperand instanceof SymConstant) {
			if(this.is_zero((SymConstant) roperand)) {
				return loperand;
			}
		}
		
		SymNode result = new SymBinaryExpression(
				source.get_data_type(), COperator.left_shift);
		result.add_child(loperand);
		result.add_child(roperand);
		return result;
	}
	private SymNode eval_bitws_rsh_expression(SymBinaryExpression source) throws Exception {
		SymNode loperand = this.eval(source.get_loperand());
		SymNode roperand = this.eval(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			if(roperand instanceof SymConstant) {
				return this.bitws_rsh(source.get_data_type(), 
						(SymConstant) loperand, (SymConstant) roperand);
			}
			else if(this.is_zero((SymConstant) loperand)) {
				CConstant constant = new CConstant();
				constant.set_long(0L);
				return new SymConstant(source.get_data_type(), constant);
			}
		}
		else if(roperand instanceof SymConstant) {
			if(this.is_zero((SymConstant) roperand)) {
				return loperand;
			}
		}
		
		SymNode result = new SymBinaryExpression(
				source.get_data_type(), COperator.righ_shift);
		result.add_child(loperand);
		result.add_child(roperand);
		return result;
	}
	private SymNode eval_greater_tn_expression(SymBinaryExpression source) throws Exception {
		SymNode loperand = this.eval(source.get_loperand());
		SymNode roperand = this.eval(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			if(roperand instanceof SymConstant) {
				return this.greater_tn(source.get_data_type(), 
						(SymConstant) loperand, (SymConstant) roperand);
			}
		}
		
		SymNode result = new SymBinaryExpression(
				source.get_data_type(), COperator.smaller_tn);
		result.add_child(roperand);
		result.add_child(loperand);
		return result;
	}
	private SymNode eval_greater_eq_expression(SymBinaryExpression source) throws Exception {
		SymNode loperand = this.eval(source.get_loperand());
		SymNode roperand = this.eval(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			if(roperand instanceof SymConstant) {
				return this.greater_eq(source.get_data_type(), 
						(SymConstant) loperand, (SymConstant) roperand);
			}
		}
		
		SymNode result = new SymBinaryExpression(
				source.get_data_type(), COperator.smaller_eq);
		result.add_child(roperand);
		result.add_child(loperand);
		return result;
	}
	private SymNode eval_smaller_tn_expression(SymBinaryExpression source) throws Exception {
		SymNode loperand = this.eval(source.get_loperand());
		SymNode roperand = this.eval(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			if(roperand instanceof SymConstant) {
				return this.smaller_tn(source.get_data_type(), 
						(SymConstant) loperand, (SymConstant) roperand);
			}
		}
		
		SymNode result = new SymBinaryExpression(
				source.get_data_type(), COperator.smaller_tn);
		result.add_child(loperand);
		result.add_child(roperand);
		return result;
	}
	private SymNode eval_smaller_eq_expression(SymBinaryExpression source) throws Exception {
		SymNode loperand = this.eval(source.get_loperand());
		SymNode roperand = this.eval(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			if(roperand instanceof SymConstant) {
				return this.smaller_eq(source.get_data_type(), 
						(SymConstant) loperand, (SymConstant) roperand);
			}
		}
		
		SymNode result = new SymBinaryExpression(
				source.get_data_type(), COperator.smaller_eq);
		result.add_child(loperand);
		result.add_child(roperand);
		return result;
	}
	private SymNode eval_equal_with_expression(SymBinaryExpression source) throws Exception {
		SymNode loperand = this.eval(source.get_loperand());
		SymNode roperand = this.eval(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			if(roperand instanceof SymConstant) {
				return this.equal_with(source.get_data_type(), 
						(SymConstant) loperand, (SymConstant) roperand);
			}
		}
		
		SymNode result = new SymBinaryExpression(
				source.get_data_type(), COperator.equal_with);
		result.add_child(roperand);
		result.add_child(loperand);
		return result;
	}
	private SymNode eval_not_equals_expression(SymBinaryExpression source) throws Exception {
		SymNode loperand = this.eval(source.get_loperand());
		SymNode roperand = this.eval(source.get_roperand());
		
		if(loperand instanceof SymConstant) {
			if(roperand instanceof SymConstant) {
				return this.not_equals(source.get_data_type(), 
						(SymConstant) loperand, (SymConstant) roperand);
			}
		}
		
		SymNode result = new SymBinaryExpression(
				source.get_data_type(), COperator.not_equals);
		result.add_child(roperand);
		result.add_child(loperand);
		return result;
	}
	
	/* computation */
	/**
	 * @param constant
	 * @return whether the constant is zero
	 */
	private boolean is_zero(SymConstant constant) {
		Object value = constant.number_of();
		if(value instanceof Long) {
			return ((Long) value).longValue() == 0L;
		}
		else {
			return ((Double) value).doubleValue() == 0.0;
		}
	}
	/**
	 * @param data_type
	 * @param loperand
	 * @param roperand
	 * @return x + y
	 */
	private SymConstant arith_add(CType data_type, 
			SymConstant loperand, SymConstant roperand) {
		Object lvalue = loperand.number_of();
		Object rvalue = roperand.number_of();
		CConstant constant = new CConstant();
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				constant.set_long(x + y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				constant.set_double(x + y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				constant.set_double(x + y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				constant.set_double(x + y);
			}
		}
		
		return new SymConstant(data_type, constant);
	}
	private SymConstant arith_mul(CType data_type, SymConstant loperand, SymConstant roperand) {
		Object lvalue = loperand.number_of();
		Object rvalue = roperand.number_of();
		CConstant constant = new CConstant();
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				constant.set_long(x * y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				constant.set_double(x * y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				constant.set_double(x * y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				constant.set_double(x * y);
			}
		}
		
		return new SymConstant(data_type, constant);
	}
	private boolean is_one(SymConstant constant) {
		Object value = constant.number_of();
		if(value instanceof Long) {
			return ((Long) value).longValue() == 1L;
		}
		else {
			return ((Double) value).doubleValue() == 1.0;
		}
	}
	private SymConstant bitws_and(CType data_type, SymConstant loperand, SymConstant roperand) {
		long x = loperand.integer_of();
		long y = roperand.integer_of();
		CConstant constant = new CConstant();
		constant.set_long(x & y);
		return new SymConstant(data_type, constant);
	}
	private boolean is_rsv_zero(SymConstant constant) {
		return constant.integer_of() == ~0L;
	}
	private SymConstant bitws_ior(CType data_type, SymConstant loperand, SymConstant roperand) {
		long x = loperand.integer_of();
		long y = roperand.integer_of();
		CConstant constant = new CConstant();
		constant.set_long(x | y);
		return new SymConstant(data_type, constant);
	}
	private SymConstant bitws_xor(CType data_type, SymConstant loperand, SymConstant roperand) {
		long x = loperand.integer_of();
		long y = roperand.integer_of();
		CConstant constant = new CConstant();
		constant.set_long(x ^ y);
		return new SymConstant(data_type, constant);
	}
	private SymConstant logic_and(CType data_type, SymConstant loperand, SymConstant roperand) {
		boolean x = loperand.boolean_of();
		boolean y = roperand.boolean_of();
		CConstant constant = new CConstant();
		constant.set_bool(x && y);
		return new SymConstant(data_type, constant);
	}
	private SymConstant logic_ior(CType data_type, SymConstant loperand, SymConstant roperand) {
		boolean x = loperand.boolean_of();
		boolean y = roperand.boolean_of();
		CConstant constant = new CConstant();
		constant.set_bool(x || y);
		return new SymConstant(data_type, constant);
	}
	private SymConstant arith_sub(CType data_type, SymConstant loperand, SymConstant roperand) {
		Object lvalue = loperand.number_of();
		Object rvalue = roperand.number_of();
		CConstant constant = new CConstant();
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				constant.set_long(x - y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				constant.set_double(x - y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				constant.set_double(x - y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				constant.set_double(x - y);
			}
		}
		
		return new SymConstant(data_type, constant);
	}
	private SymConstant arith_div(CType data_type, SymConstant loperand, SymConstant roperand) {
		Object lvalue = loperand.number_of();
		Object rvalue = roperand.number_of();
		CConstant constant = new CConstant();
		
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				constant.set_long(x / y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				constant.set_double(x / y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				constant.set_double(x / y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				constant.set_double(x / y);
			}
		}
		
		return new SymConstant(data_type, constant);
	}
	private boolean is_neg_one(SymConstant constant) {
		Object value = constant.number_of();
		if(value instanceof Long) {
			return ((Long) value).longValue() == -1L;
		}
		else {
			return ((Double) value).doubleValue() == -1.0;
		}
	}
	private SymConstant arith_mod(CType data_type, SymConstant loperand, SymConstant roperand) {
		long x = loperand.integer_of();
		long y = roperand.integer_of();
		CConstant constant = new CConstant();
		constant.set_long(x % y);
		return new SymConstant(data_type, constant);
	}
	private SymConstant bitws_lsh(CType data_type, SymConstant loperand, SymConstant roperand) {
		long x = loperand.integer_of();
		long y = roperand.integer_of();
		CConstant constant = new CConstant();
		constant.set_long(x << y);
		return new SymConstant(data_type, constant);
	}
	private SymConstant bitws_rsh(CType data_type, SymConstant loperand, SymConstant roperand) {
		long x = loperand.integer_of();
		long y = roperand.integer_of();
		CConstant constant = new CConstant();
		constant.set_long(x >> y);
		return new SymConstant(data_type, constant);
	}
	private SymConstant greater_tn(CType data_type, SymConstant loperand, SymConstant roperand) {
		Object lvalue = loperand.number_of();
		Object rvalue = roperand.number_of();
		
		boolean result;
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result = (x > y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result = (x > y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result = (x > y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result = (x > y);
			}
		}
		
		CConstant constant = new CConstant();
		constant.set_bool(result);
		return new SymConstant(data_type, constant);
	}
	private SymConstant greater_eq(CType data_type, SymConstant loperand, SymConstant roperand) {
		Object lvalue = loperand.number_of();
		Object rvalue = roperand.number_of();
		
		boolean result;
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result = (x >= y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result = (x >= y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result = (x >= y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result = (x >= y);
			}
		}
		
		CConstant constant = new CConstant();
		constant.set_bool(result);
		return new SymConstant(data_type, constant);
	}
	private SymConstant smaller_tn(CType data_type, SymConstant loperand, SymConstant roperand) {
		Object lvalue = loperand.number_of();
		Object rvalue = roperand.number_of();
		
		boolean result;
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result = (x < y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result = (x < y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result = (x < y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result = (x < y);
			}
		}
		
		CConstant constant = new CConstant();
		constant.set_bool(result);
		return new SymConstant(data_type, constant);
	}
	private SymConstant smaller_eq(CType data_type, SymConstant loperand, SymConstant roperand) {
		Object lvalue = loperand.number_of();
		Object rvalue = roperand.number_of();
		
		boolean result;
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result = (x <= y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result = (x <= y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result = (x <= y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result = (x <= y);
			}
		}
		
		CConstant constant = new CConstant();
		constant.set_bool(result);
		return new SymConstant(data_type, constant);
	}
	private SymConstant equal_with(CType data_type, SymConstant loperand, SymConstant roperand) {
		Object lvalue = loperand.number_of();
		Object rvalue = roperand.number_of();
		
		boolean result;
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result = (x == y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result = (x == y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result = (x == y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result = (x == y);
			}
		}
		
		CConstant constant = new CConstant();
		constant.set_bool(result);
		return new SymConstant(data_type, constant);
	}
	private SymConstant not_equals(CType data_type, SymConstant loperand, SymConstant roperand) {
		Object lvalue = loperand.number_of();
		Object rvalue = roperand.number_of();
		
		boolean result;
		if(lvalue instanceof Long) {
			long x = ((Long) lvalue).longValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result = (x != y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result = (x != y);
			}
		}
		else {
			double x = ((Double) lvalue).doubleValue();
			if(rvalue instanceof Long) {
				long y = ((Long) rvalue).longValue();
				result = (x != y);
			}
			else {
				double y = ((Double) rvalue).doubleValue();
				result = (x != y);
			}
		}
		
		CConstant constant = new CConstant();
		constant.set_bool(result);
		return new SymConstant(data_type, constant);
	}
	
	/* public API */
	/**
	 * @param source
	 * @param solutions to maintain the mapping from each node in 
	 * 		  source to their corresponding result
	 * @return symbolic result from evaluation
	 * @throws Exception
	 */
	public SymNode evaluate(SymNode source, Map<SymNode, SymNode> solutions) throws Exception {
		this.context.debug_table.clear();
		SymNode target = this.eval(source);
		if(solutions != null) {
			solutions.clear();
			for(SymNode x : this.context.debug_table.keySet()) {
				SymNode y = this.context.debug_table.get(x);
				solutions.put(x, y);
			}
		}
		return target;
	}
	/**
	 * @param source
	 * @return symbolic result without recording the debuging information.
	 * @throws Exception
	 */
	public SymNode evaluate(SymNode source) throws Exception {
		return this.evaluate(source, null);
	}
	
}
