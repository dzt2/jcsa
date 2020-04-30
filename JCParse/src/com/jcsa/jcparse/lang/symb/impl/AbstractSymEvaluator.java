package com.jcsa.jcparse.lang.symb.impl;

import java.util.Map;

import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.symb.SymAddress;
import com.jcsa.jcparse.lang.symb.SymArgumentList;
import com.jcsa.jcparse.lang.symb.SymConstant;
import com.jcsa.jcparse.lang.symb.SymDefaultValue;
import com.jcsa.jcparse.lang.symb.SymEvaluator;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;
import com.jcsa.jcparse.lang.symb.SymFieldExpression;
import com.jcsa.jcparse.lang.symb.SymInvocateExpression;
import com.jcsa.jcparse.lang.symb.SymLiteral;
import com.jcsa.jcparse.lang.symb.SymSequenceExpression;
import com.jcsa.jcparse.lang.symb.SymUnaryExpression;

/**
 * Implement some of the general translation in program.<br>
 * 	context 	==> get solution			<br>
 * 	address		==> copy					<br>
 * 	default		==> copy					<br>
 * 	constant	==> bool | long | double	<br>
 * 	literal		==> copy					<br>
 * 	field		==> recursive				<br>
 * 	invocate	==> recursive				<br>
 * 	sequence	==> recursive				<br>
 * 	positive	==> operand					<br>
 * @author yukimula
 *
 */
public abstract class AbstractSymEvaluator extends SymEvaluator {
	
	protected static final CTypeFactory tfactory = new CTypeFactory();

	@Override
	protected SymExpression find_in_context(SymExpression expression) throws Exception {
		Object value = this.context.get(expression.toString());
		if(value == null) {
			Map<String, Object> backup = this.context;
			this.context = null;
			SymExpression result = this.evaluate(expression);
			this.context = backup;
			return result;
		}
		else if(value instanceof Boolean) {
			return SymFactory.new_constant(((Boolean) value).booleanValue());
		}
		else if(value instanceof Character) {
			return SymFactory.new_constant((long) ((Character) value).charValue());
		}
		else if(value instanceof Short) {
			return SymFactory.new_constant(((Short) value).longValue());
		}
		else if(value instanceof Integer) {
			return SymFactory.new_constant(((Integer) value).longValue());
		}
		else if(value instanceof Long) {
			return SymFactory.new_constant(((Long) value).longValue());
		}
		else if(value instanceof Float) {
			return SymFactory.new_constant(((Float) value).doubleValue());
		}
		else if(value instanceof Double) {
			return SymFactory.new_constant(((Double) value).doubleValue());
		}
		else if(value instanceof String) {
			return SymFactory.new_address((String) value, 
					tfactory.get_pointer_type(expression.get_data_type()));
		}
		else if(value instanceof CConstant) {
			CConstant constant = (CConstant) value;
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				return SymFactory.new_constant(constant.get_bool().booleanValue());
			}
			case c_char:
			case c_uchar:
			{
				return SymFactory.new_constant((long) constant.get_char().charValue());
			}
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			{
				return SymFactory.new_constant(constant.get_integer().longValue());
			}
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			{
				return SymFactory.new_constant(constant.get_long().longValue());
			}
			case c_float:
			{
				return SymFactory.new_constant(constant.get_float().doubleValue());
			}
			case c_double:
			case c_ldouble:
			{
				return SymFactory.new_constant(constant.get_double().doubleValue());
			}
			default: throw new IllegalArgumentException("Invalid constant: null");
			}
		}
		else if(value instanceof SymExpression) {
			Map<String, Object> backup = this.context;
			this.context = null;
			SymExpression result = this.evaluate((SymExpression) value);
			this.context = backup;
			return result;
		}
		else {
			throw new IllegalArgumentException("Unknown value: " + value);
		}
	}

	@Override
	protected SymExpression address_expression(SymAddress expression) throws Exception {
		return (SymExpression) expression.copy();
	}

	@Override
	protected SymExpression constant_expression(SymConstant expression) throws Exception {
		CConstant constant = expression.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:
		{
			return SymFactory.new_constant(constant.get_bool().booleanValue());
		}
		case c_char:
		case c_uchar:
		{
			return SymFactory.new_constant((long) constant.get_char().charValue());
		}
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
		{
			return SymFactory.new_constant(constant.get_integer().longValue());
		}
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
		{
			return SymFactory.new_constant(constant.get_long().longValue());
		}
		case c_float:
		{
			return SymFactory.new_constant(constant.get_float().doubleValue());
		}
		case c_double:
		case c_ldouble:
		{
			return SymFactory.new_constant(constant.get_double().doubleValue());
		}
		default: throw new IllegalArgumentException("Invalid constant: null");
		}
	}

	@Override
	protected SymExpression default_expression(SymDefaultValue expression) throws Exception {
		return (SymExpression) expression.copy();
	}

	@Override
	protected SymExpression literal_expression(SymLiteral expression) throws Exception {
		return (SymExpression) expression.copy();
	}

	@Override
	protected SymExpression field_expression(SymFieldExpression expression) throws Exception {
		SymExpression body = this.evaluate(expression.get_body());
		String field_name = expression.get_field().get_name();
		return SymFactory.new_field_expression(expression.get_data_type(), body, field_name);
	}

	@Override
	protected SymExpression invocate_expression(SymInvocateExpression expression) throws Exception {
		SymExpression function = this.evaluate(expression.get_function());
		SymInvocateExpression result = SymFactory.
				new_invocate_expression(expression.get_data_type(), function);
		
		SymArgumentList arguments = expression.get_argument_list();
		for(int k = 0; k < arguments.number_of_arguments(); k++) {
			SymExpression argument = this.evaluate(arguments.get_argument(k));
			result.get_argument_list().add_argument(argument);
		}
		
		return result;
	}

	@Override
	protected SymExpression sequence_expression(SymSequenceExpression expression) throws Exception {
		SymSequenceExpression result = SymFactory.new_sequence_expression();
		
		for(int k = 0; k < expression.number_of_elements(); k++) {
			SymExpression element = this.evaluate(expression.get_element(k));
			result.add_element(element);
		}
		
		return result;
	}
	
	@Override
	protected SymExpression positive_expression(SymUnaryExpression expression) throws Exception {
		return this.evaluate(expression.get_operand());
	}
	
}
