package com.jcsa.jcmutest.mutant.sad2mutant.eval;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadCallExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadConstant;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadDefaultValue;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFieldExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadIdExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadInitializerList;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadLiteral;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadUnaryExpression;
import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * It is used to evaluate the value of symbolic assertion expression.
 * 
 * @author yukimula
 *
 */
public class SadEvaluator {
	
	/* definition */
	private SadEvalContext context;
	private SadEvaluator() { this.context = null; }
	private static final SadEvaluator evaluator = new SadEvaluator();
	
	/* context APIs */
	private void set_context(SadEvalContext context) {
		this.context = context;
	}
	private SadExpression get_from_context(String code) {
		if(this.context != null) {
			return this.context.get(code);
		}
		else {
			return null;
		}
	}
	private SadExpression invocate_in_context(SadExpression 
			function, List<SadExpression> arguments) throws Exception {
		if(this.context != null) {
			return this.context.invocate(function, arguments);
		}
		else {
			return null;
		}
	}
	/**
	 * @param constant
	 * @return long | double
	 * @throws Exception
	 */
	private Object get_number(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:
			if(constant.get_bool().booleanValue()) {
				return Long.valueOf(1);
			}
			else {
				return Long.valueOf(0);
			}
		case c_char:
		case c_uchar:
			return Long.valueOf(constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
			return Long.valueOf(constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
			return Long.valueOf(constant.get_long().longValue());
		case c_float:
			return Double.valueOf(constant.get_float().floatValue());
		case c_double:
		case c_ldouble:
			return Double.valueOf(constant.get_double().doubleValue());
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	
	/* evaluation methods */
	private SadExpression eval(SadExpression source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else {
			SadExpression result = this.get_from_context(source.generate_code());
			if(result != null) {
				return result;
			}
			/* TODO implement the evaluation algorithm */
			else {
				throw new IllegalArgumentException("Unsupport: " + source);
			}
		}
	}
	private SadExpression eval_id_expression(SadIdExpression source) throws Exception {
		return SadFactory.id_expression(source.get_data_type(), source.get_name());
	}
	private SadExpression eval_constant(SadConstant source) throws Exception {
		CConstant constant = source.get_constant();
		switch(source.get_constant().get_type().get_tag()) {
		case c_bool:
			return SadFactory.constant(constant.get_bool().booleanValue());
		case c_char:
		case c_uchar:
			return SadFactory.constant(constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
			return SadFactory.constant(constant.get_integer().intValue());
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
			return SadFactory.constant(constant.get_long().longValue());
		case c_float:
			return SadFactory.constant(constant.get_float().floatValue());
		case c_double:
		case c_ldouble:
			return SadFactory.constant(constant.get_double().doubleValue());
		default: throw new IllegalArgumentException("Invalid source: null");
		}
	}
	private SadExpression eval_literal(SadLiteral source) throws Exception {
		return SadFactory.literal(source.get_literal());
	}
	private SadExpression eval_default_value(SadDefaultValue source) throws Exception {
		return SadFactory.default_value(source.get_data_type());
	}
	private SadExpression eval_call_expression(SadCallExpression source) throws Exception {
		SadExpression function = this.eval(source.get_function());
		List<SadExpression> arguments = new ArrayList<SadExpression>();
		for(int k = 0; k < source.get_argument_list().number_of_arguments(); k++) {
			arguments.add(this.eval(source.get_argument_list().get_argument(k)));
		}
		SadExpression result = this.invocate_in_context(function, arguments);
		
		if(result == null) {
			result = SadFactory.call_expression(source.get_data_type(), function, arguments);
		}
		return result;
	}
	private SadExpression eval_field_expression(SadFieldExpression source) throws Exception {
		SadExpression body = this.eval(source.get_body());
		return SadFactory.field_expression(source.get_data_type(), 
							body, source.get_field().get_name());
	}
	private SadExpression eval_initializer_list(SadInitializerList source) throws Exception {
		List<SadExpression> operands = new ArrayList<SadExpression>();
		for(int k = 0; k < source.number_of_elements(); k++) {
			operands.add(this.eval(source.get_element(k)));
		}
		return SadFactory.initializer_list(source.get_data_type(), operands);
	}
	private SadExpression eval_arith_pos(SadUnaryExpression source) throws Exception {
		return this.eval(source.get_operand());
	}
	private SadExpression eval_arith_neg(SadUnaryExpression source) throws Exception {
		SadExpression operand = this.eval(source.get_operand());
		if(operand instanceof SadConstant) {
			Object number = this.get_number(((SadConstant) operand).get_constant());
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				return SadFactory.constant(-value);
			}
			else {
				double value = ((Double) number).doubleValue();
				return SadFactory.constant(-value);
			}
		}
		else {
			return SadFactory.arith_neg(source.get_data_type(), operand);
		}
	}
	
	
	
	
	
	
}
