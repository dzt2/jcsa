package com.jcsa.jcmuta.mutant.sem2mutation.sem.process;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.StateErrorProcess;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class CAS_EProcess extends StateErrorProcess {
	
	private CirExpression get_expression(CirNode target) throws Exception {
		return (CirExpression) target;
	}
	
	private CType get_source_type(SemanticAssertion source_assertion) throws Exception {
		CirNode source = source_assertion.get_location();
		return CTypeAnalyzer.get_value_type(this.get_expression(source).get_data_type());
	}
	
	private CType get_target_type(CirNode target) throws Exception {
		return CTypeAnalyzer.get_value_type(this.get_expression(target).get_data_type());
	}
	
	private int get_type_precision(CType type) throws Exception {
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_void:	return 0;
			case c_bool: 	return 1;
			case c_char:
			case c_uchar:	return 2;
			case c_short:
			case c_ushort:	return 3;
			case c_int:
			case c_uint:	return 4;
			case c_long:
			case c_ulong:	return 5;
			case c_llong:
			case c_ullong:	return 6;
			case c_float:	return 7;
			case c_double:	return 8;
			case c_ldouble:	return 9;
			default: throw new IllegalArgumentException("Invalid type");
			}
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			return 6;
		}
		else {
			return -1;
		}
	}
	
	@Override
	protected void process_active(SemanticAssertion source_assertion, CirNode target) throws Exception {
		this.throw_error_propagation(source_assertion, target);
	}

	@Override
	protected void process_disactive(SemanticAssertion source_assertion, CirNode target) throws Exception {
		this.throw_error_propagation(source_assertion, target);
	}

	@Override
	protected void process_mut_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CType source_type = this.get_source_type(source_assertion);
		CType target_type = this.get_target_type(target);
		
		if(CTypeAnalyzer.is_void(target_type)) { /** equivalent **/ }
		else if(CTypeAnalyzer.is_boolean(target_type)) {
			this.error_assertions.add(source_assertion.get_assertions().not_value(expression));
		}
		else if(CTypeAnalyzer.is_number(target_type) || CTypeAnalyzer.is_pointer(target_type)) {
			int source_precision = this.get_type_precision(source_type);
			int target_precision = this.get_type_precision(target_type);
			
			/** extension **/
			if(source_precision <= target_precision) {
				this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
			}
			/** narrowing **/
			else {
				this.const_assertions.add(source_assertion.
						get_assertions().in_range(expression, SemanticMutationParser.Overflow));
				this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
			}
		}
		else {
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_mut_refer(SemanticAssertion source_assertion, CirNode target) throws Exception {
		this.process_mut_value(source_assertion, target);
	}

	@Override
	protected void process_not_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		this.process_mut_value(source_assertion, target);
	}

	@Override
	protected void process_inc_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CType target_type = this.get_target_type(target);
		
		if(CTypeAnalyzer.is_void(target_type)) { /** equivalent **/ }
		else if(CTypeAnalyzer.is_boolean(target_type)) {
			this.error_assertions.add(source_assertion.get_assertions().set_value(expression, true));
		}
		else if(CTypeAnalyzer.is_number(target_type) || CTypeAnalyzer.is_pointer(target_type)) {
			this.error_assertions.add(source_assertion.get_assertions().inc_value(expression));
		}
		else {
			// throw new IllegalArgumentException("Invalid type: " + target_type);
		}
	}

	@Override
	protected void process_dec_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CType target_type = this.get_target_type(target);
		
		if(CTypeAnalyzer.is_void(target_type)) { /** equivalent **/ }
		else if(CTypeAnalyzer.is_boolean(target_type)) {
			this.error_assertions.add(source_assertion.get_assertions().set_value(expression, true));
		}
		else if(CTypeAnalyzer.is_number(target_type) || CTypeAnalyzer.is_pointer(target_type)) {
			this.error_assertions.add(source_assertion.get_assertions().dec_value(expression));
		}
		else {
			// throw new IllegalArgumentException("Invalid type: " + target_type);
		}
	}

	@Override
	protected void process_neg_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CType target_type = this.get_target_type(target);
		
		if(CTypeAnalyzer.is_void(target_type)) { /** equivalent **/ }
		else if(CTypeAnalyzer.is_boolean(target_type)) {
			this.error_assertions.add(source_assertion.get_assertions().set_value(expression, true));
		}
		else if(CTypeAnalyzer.is_number(target_type) || CTypeAnalyzer.is_pointer(target_type)) {
			this.error_assertions.add(source_assertion.get_assertions().neg_value(expression));
		}
		else {
			// throw new IllegalArgumentException("Invalid type: " + target_type);
		}
	}

	@Override
	protected void process_rsv_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CType target_type = this.get_target_type(target);
		
		if(CTypeAnalyzer.is_void(target_type)) { /** equivalent **/ }
		else if(CTypeAnalyzer.is_boolean(target_type)) {
			this.error_assertions.add(source_assertion.get_assertions().set_value(expression, true));
		}
		else if(CTypeAnalyzer.is_number(target_type) || CTypeAnalyzer.is_pointer(target_type)) {
			this.error_assertions.add(source_assertion.get_assertions().rsv_value(expression));
		}
		else {
			// throw new IllegalArgumentException("Invalid type: " + target_type);
		}
	}

	@Override
	protected void process_set_bool(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CType target_type = this.get_target_type(target);
		Boolean value = (Boolean) source_assertion.get_operand(1);
		
		if(CTypeAnalyzer.is_void(target_type)) { /** equivalent **/ }
		else if(CTypeAnalyzer.is_boolean(target_type)) {
			this.error_assertions.add(source_assertion.get_assertions().
					set_value(expression, value.booleanValue()));
		}
		else if(CTypeAnalyzer.is_number(target_type) || CTypeAnalyzer.is_pointer(target_type)) {
			if(value)
				this.error_assertions.add(source_assertion.get_assertions().set_value(expression, 1));
			else
				this.error_assertions.add(source_assertion.get_assertions().set_value(expression, 0));
		}
		else {
			// throw new IllegalArgumentException("Invalid target type: " + target_type);
		}
	}

	@Override
	protected void process_set_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CType target_type = this.get_target_type(target);
		Object value = this.cast_to_numeric_value(source_assertion.get_operand(1));
		
		if(CTypeAnalyzer.is_void(target_type)) { /** equivalent **/ }
		else if(CTypeAnalyzer.is_boolean(target_type)) {
			boolean bool_value;
			if(value instanceof Long) {
				bool_value = ((Long) value).longValue() != 0;
			}
			else {
				bool_value = ((Double) value).doubleValue() != 0;
			}
			this.error_assertions.add(source_assertion.
					get_assertions().set_value(expression, bool_value));
		}
		else if(CTypeAnalyzer.is_integer(target_type)) {
			long int_value;
			if(value instanceof Long) {
				int_value = ((Long) value).longValue();
			}
			else {
				int_value = ((Double) value).longValue();
			}
			this.error_assertions.add(source_assertion.
					get_assertions().set_value(expression, int_value));
		}
		else if(CTypeAnalyzer.is_real(target_type)) {
			double double_value;
			if(value instanceof Long) {
				double_value = ((Long) value).doubleValue();
			}
			else {
				double_value = ((Double) value).doubleValue();
			}
			this.error_assertions.add(source_assertion.
					get_assertions().set_value(expression, double_value));
		}
		else if(CTypeAnalyzer.is_pointer(target_type)) {
			long int_value;
			if(value instanceof Long) {
				int_value = ((Long) value).longValue();
			}
			else {
				int_value = ((Double) value).longValue();
			}
			this.error_assertions.add(source_assertion.
					get_assertions().set_value(expression, int_value));
		}
		else {
			// throw new IllegalArgumentException("Invalid target type: " + target_type);
		}
	}

	@Override
	protected void process_dif_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CType target_type = this.get_target_type(target);
		Object value = this.cast_to_numeric_value(source_assertion.get_operand(1));
		
		if(CTypeAnalyzer.is_void(target_type)) { /** equivalent **/ }
		else if(CTypeAnalyzer.is_boolean(target_type)) {
			/** equivalent mutation may without effects! **/
		}
		else if(CTypeAnalyzer.is_integer(target_type)) {
			long difference;
			if(value instanceof Long) {
				difference = ((Long) value).longValue();
			}
			else {
				difference = ((Double) value).longValue();
			}
			
			if(difference != 0) {
				this.error_assertions.add(source_assertion.get_assertions().diff_value(expression, difference));
			}
		}
		else if(CTypeAnalyzer.is_real(target_type)) {
			double difference;
			if(value instanceof Long) {
				difference = ((Long) value).doubleValue();
			}
			else {
				difference = ((Double) value).doubleValue();
			}
			
			if(difference != 0) {
				this.error_assertions.add(source_assertion.get_assertions().diff_value(expression, difference));
			}
		}
		else if(CTypeAnalyzer.is_pointer(target_type)) {
			long difference;
			if(value instanceof Long) {
				difference = ((Long) value).longValue();
			}
			else {
				difference = ((Double) value).longValue();
			}
			
			if(difference != 0) {
				this.error_assertions.add(source_assertion.get_assertions().diff_value(expression, difference));
			}
		}
		else {
			// throw new IllegalArgumentException("Invalid target type: " + target_type);
		}
	}

}
