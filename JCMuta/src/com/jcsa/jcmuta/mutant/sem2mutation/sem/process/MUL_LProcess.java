package com.jcsa.jcmuta.mutant.sem2mutation.sem.process;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.StateErrorProcess;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class MUL_LProcess extends StateErrorProcess {
	
	private CirComputeExpression get_expression(CirNode target) {
		return (CirComputeExpression) target;
	}
	
	private CirExpression get_roperand(CirNode target) throws Exception {
		return this.get_expression(target).get_operand(0);
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
		CirExpression roperand = this.get_roperand(target);
		Object rconstant = this.get_constant(roperand);
		
		if(this.is_zero(rconstant)) {
			/** equivalent mutation **/
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(roperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_mut_refer(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression roperand = this.get_roperand(target);
		Object rconstant = this.get_constant(roperand);
		
		if(this.is_zero(rconstant)) {
			/** equivalent mutation **/
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(roperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_not_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression roperand = this.get_roperand(target);
		Object rconstant = this.get_constant(roperand);
		
		if(this.is_zero(rconstant)) {
			/** equivalent mutation **/
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(roperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_inc_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression roperand = this.get_roperand(target);
		Object rconstant = this.get_constant(roperand);
		
		if(rconstant != null) {
			if(this.is_positive(rconstant)) {
				this.error_assertions.add(source_assertion.get_assertions().inc_value(expression));
			}
			else if(this.is_negative(rconstant)) {
				this.error_assertions.add(source_assertion.get_assertions().dec_value(expression));
			}
			else { /** equivalent mutation **/ }
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(roperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_dec_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression roperand = this.get_roperand(target);
		Object rconstant = this.get_constant(roperand);
		
		if(rconstant != null) {
			if(this.is_positive(rconstant)) {
				this.error_assertions.add(source_assertion.get_assertions().dec_value(expression));
			}
			else if(this.is_negative(rconstant)) {
				this.error_assertions.add(source_assertion.get_assertions().inc_value(expression));
			}
			else { /** equivalent mutation **/ }
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(roperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_neg_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression roperand = this.get_roperand(target);
		Object rconstant = this.get_constant(roperand);
		
		if(this.is_zero(rconstant)) {
			/** equivalent mutation **/
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(roperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().neg_value(expression));
		}
	}

	@Override
	protected void process_rsv_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression roperand = this.get_roperand(target);
		Object rconstant = this.get_constant(roperand);
		
		if(this.is_zero(rconstant)) {
			/** equivalent mutation **/
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(roperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_set_bool(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression roperand = this.get_roperand(target);
		Object lconstant = source_assertion.get_operand(1);
		Object rconstant = this.get_constant(roperand);
		Object result = this.mul(lconstant, rconstant);
		
		if(this.is_zero(rconstant)) {
			/** equivalent mutation **/
		}
		else if(result != null) {
			if(result instanceof Long) {
				this.error_assertions.add(source_assertion.get_assertions().
						set_value(expression, ((Long) result).longValue()));
			}
			else {
				this.error_assertions.add(source_assertion.get_assertions().
						set_value(expression, ((Double) result).doubleValue()));
			}
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(roperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_set_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression roperand = this.get_roperand(target);
		Object lconstant = source_assertion.get_operand(1);
		Object rconstant = this.get_constant(roperand);
		Object result = this.mul(lconstant, rconstant);
		
		if(this.is_zero(rconstant)) {
			/** equivalent mutation **/
		}
		else if(result != null) {
			if(result instanceof Long) {
				this.error_assertions.add(source_assertion.get_assertions().
						set_value(expression, ((Long) result).longValue()));
			}
			else {
				this.error_assertions.add(source_assertion.get_assertions().
						set_value(expression, ((Double) result).doubleValue()));
			}
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(roperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_dif_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression roperand = this.get_roperand(target);
		Object lconstant = source_assertion.get_operand(1);
		Object rconstant = this.get_constant(roperand);
		Object difference = this.mul(lconstant, rconstant);
		
		if(this.is_zero(rconstant)) {
			/** equivalent mutation **/
		}
		else if(difference != null) {
			if(difference instanceof Long) {
				if(((Long) difference).longValue() != 0)
					this.error_assertions.add(source_assertion.get_assertions().
							diff_value(expression, ((Long) difference).longValue()));
			}
			else {
				if(((Double) difference).doubleValue() != 0)
					this.error_assertions.add(source_assertion.get_assertions().
							diff_value(expression, ((Double) difference).doubleValue()));
			}
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(roperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

}
