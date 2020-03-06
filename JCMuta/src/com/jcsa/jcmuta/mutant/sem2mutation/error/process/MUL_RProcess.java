package com.jcsa.jcmuta.mutant.sem2mutation.error.process;

import com.jcsa.jcmuta.mutant.sem2mutation.error.StateErrorProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class MUL_RProcess extends StateErrorProcess {
	
	private CirComputeExpression get_expression(CirNode target) {
		return (CirComputeExpression) target;
	}
	
	private CirExpression get_loperand(CirNode target) throws Exception {
		return this.get_expression(target).get_operand(0);
	}
	
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
		CirExpression loperand = this.get_loperand(target);
		Object lconstant = this.get_constant(loperand);
		
		if(this.is_zero(lconstant)) {
			/** equivalent mutation **/
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_mut_refer(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression loperand = this.get_loperand(target);
		Object lconstant = this.get_constant(loperand);
		
		if(this.is_zero(lconstant)) {
			/** equivalent mutation **/
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_not_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression loperand = this.get_loperand(target);
		Object lconstant = this.get_constant(loperand);
		
		if(this.is_zero(lconstant)) {
			/** equivalent mutation **/
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_inc_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression loperand = this.get_loperand(target);
		Object lconstant = this.get_constant(loperand);
		
		if(lconstant != null) {
			if(this.is_positive(lconstant)) {
				this.error_assertions.add(source_assertion.get_assertions().inc_value(expression));
			}
			else if(this.is_negative(lconstant)) {
				this.error_assertions.add(source_assertion.get_assertions().dec_value(expression));
			}
			else { /** equivalent mutation **/ }
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_dec_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression loperand = this.get_loperand(target);
		Object lconstant = this.get_constant(loperand);
		
		if(lconstant != null) {
			if(this.is_positive(lconstant)) {
				this.error_assertions.add(source_assertion.get_assertions().dec_value(expression));
			}
			else if(this.is_negative(lconstant)) {
				this.error_assertions.add(source_assertion.get_assertions().inc_value(expression));
			}
			else { /** equivalent mutation **/ }
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_neg_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression loperand = this.get_loperand(target);
		Object lconstant = this.get_constant(loperand);
		
		if(this.is_zero(lconstant)) {
			/** equivalent mutation **/
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().neg_value(expression));
		}
	}

	@Override
	protected void process_rsv_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression loperand = this.get_loperand(target);
		Object lconstant = this.get_constant(loperand);
		
		if(this.is_zero(lconstant)) {
			/** equivalent mutation **/
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_set_bool(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression loperand = this.get_loperand(target);
		Object rconstant = source_assertion.get_operand(1);
		Object lconstant = this.get_constant(loperand);
		Object result = this.mul(lconstant, rconstant);
		
		if(this.is_zero(lconstant)) {
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
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_set_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression loperand = this.get_loperand(target);
		Object rconstant = source_assertion.get_operand(1);
		Object lconstant = this.get_constant(loperand);
		Object result = this.mul(lconstant, rconstant);
		
		if(this.is_zero(lconstant)) {
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
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_dif_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression loperand = this.get_loperand(target);
		Object rconstant = source_assertion.get_operand(1);
		Object lconstant = this.get_constant(loperand);
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
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

}
