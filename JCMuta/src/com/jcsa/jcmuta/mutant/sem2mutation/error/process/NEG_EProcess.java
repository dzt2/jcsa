package com.jcsa.jcmuta.mutant.sem2mutation.error.process;

import com.jcsa.jcmuta.mutant.sem2mutation.error.StateErrorProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class NEG_EProcess extends StateErrorProcess {

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
		CirExpression expression = (CirExpression) target;
		CirExpression operand = (CirExpression) source_assertion.get_location();
		this.const_assertions.add(source_assertion.get_assertions().not_equals(operand, 0));
		this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
	}

	@Override
	protected void process_mut_refer(SemanticAssertion source_assertion, CirNode target) throws Exception {
		this.process_mut_value(source_assertion, target);
	}

	@Override
	protected void process_not_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = (CirExpression) target;
		CirExpression operand = (CirExpression) source_assertion.get_location();
		this.const_assertions.add(source_assertion.get_assertions().not_equals(operand, false));
		this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
	}

	@Override
	protected void process_inc_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = (CirExpression) target;
		CirExpression operand = (CirExpression) source_assertion.get_location();
		this.const_assertions.add(source_assertion.get_assertions().not_equals(operand, 0));
		this.error_assertions.add(source_assertion.get_assertions().dec_value(expression));
	}

	@Override
	protected void process_dec_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = (CirExpression) target;
		CirExpression operand = (CirExpression) source_assertion.get_location();
		this.const_assertions.add(source_assertion.get_assertions().not_equals(operand, 0));
		this.error_assertions.add(source_assertion.get_assertions().inc_value(expression));
	}

	@Override
	protected void process_neg_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = (CirExpression) target;
		CirExpression operand = (CirExpression) source_assertion.get_location();
		this.const_assertions.add(source_assertion.get_assertions().not_equals(operand, 0));
		this.error_assertions.add(source_assertion.get_assertions().neg_value(expression));
	}

	@Override
	protected void process_rsv_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = (CirExpression) target;
		CirExpression operand = (CirExpression) source_assertion.get_location();
		this.const_assertions.add(source_assertion.get_assertions().not_equals(operand, 0));
		this.error_assertions.add(source_assertion.get_assertions().rsv_value(expression));
	}

	@Override
	protected void process_set_bool(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = (CirExpression) target;
		Boolean value = (Boolean) source_assertion.get_operand(1);
		
		if(value.booleanValue()) {
			this.error_assertions.add(source_assertion.get_assertions().set_value(expression, -1));
		}
	}

	@Override
	protected void process_set_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = (CirExpression) target;
		Object value = this.cast_to_numeric_value(source_assertion.get_operand(1));
		
		if(value instanceof Long) {
			long val = ((Long) value).longValue();
			if(val != 0) {
				this.error_assertions.add(source_assertion.get_assertions().set_value(expression, -val));
			}
		}
		else {
			double val = ((Double) value).doubleValue();
			if(val != 0) {
				this.error_assertions.add(source_assertion.get_assertions().set_value(expression, -val));
			}
		}
	}

	@Override
	protected void process_dif_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = (CirExpression) target;
		Object difference = this.cast_to_numeric_value(source_assertion.get_operand(1));
		
		if(difference instanceof Long) {
			long val = ((Long) difference).longValue();
			if(val != 0) {
				this.error_assertions.add(source_assertion.get_assertions().diff_value(expression, -val));
			}
		}
		else {
			double val = ((Double) difference).doubleValue();
			if(val != 0) {
				this.error_assertions.add(source_assertion.get_assertions().diff_value(expression, -val));
			}
		}
	}

}
