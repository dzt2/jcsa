package com.jcsa.jcmuta.mutant.sem2mutation.sem.process;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.StateErrorProcess;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class MOD_RProcess extends StateErrorProcess {
	
	private CirComputeExpression get_expression(CirNode target) {
		return (CirComputeExpression) target;
	}
	
	private CirExpression get_loperand(CirNode target) throws Exception {
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
		CirExpression loperand = this.get_loperand(target);
		Object lconstant = this.get_constant(loperand);
		
		if(lconstant != null) {
			if(this.is_zero(lconstant) || 
				this.is_one(lconstant) || 
				this.is_neg_one(lconstant)) {
				/** equivalent mutation **/
			}
			else {
				this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
			}
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
		
		if(lconstant != null) {
			if(this.is_zero(lconstant) || 
				this.is_one(lconstant) || 
				this.is_neg_one(lconstant)) {
				/** equivalent mutation **/
			}
			else {
				this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
			}
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_not_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		this.error_assertions.add(source_assertion.get_assertions().trapping());
	}

	@Override
	protected void process_inc_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression loperand = this.get_loperand(target);
		Object lconstant = this.get_constant(loperand);
		
		if(lconstant != null) {
			if(this.is_zero(lconstant) || 
				this.is_one(lconstant) || 
				this.is_neg_one(lconstant)) {
				/** equivalent mutation **/
			}
			else {
				this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
			}
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
			if(this.is_zero(lconstant) || 
				this.is_one(lconstant) || 
				this.is_neg_one(lconstant)) {
				/** equivalent mutation **/
			}
			else {
				this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
			}
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

	@Override
	protected void process_neg_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		/** equivalent mutation **/
	}

	@Override
	protected void process_rsv_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression loperand = this.get_loperand(target);
		Object lconstant = this.get_constant(loperand);
		
		if(lconstant != null) {
			if(this.is_zero(lconstant) || 
				this.is_one(lconstant) || 
				this.is_neg_one(lconstant)) {
				/** equivalent mutation **/
			}
			else {
				this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
			}
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
		boolean value = (boolean) source_assertion.get_operand(1);
		
		if(value) {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().set_value(expression, 0));
		}
		else {
			this.error_assertions.add(source_assertion.get_assertions().trapping());
		}
	}

	@Override
	protected void process_set_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirExpression expression = this.get_expression(target);
		CirExpression loperand = this.get_loperand(target);
		Object lconstant = this.get_constant(loperand);
		Object rconstant = source_assertion.get_operand(1);
		
		if(this.is_zero(rconstant)) {
			this.error_assertions.add(source_assertion.get_assertions().trapping());
		}
		else if(lconstant != null) {
			Object result = this.mod(lconstant, rconstant);
			
			if(result != null) {
				this.error_assertions.add(source_assertion.get_assertions().
						set_value(expression, ((Long) result).longValue()));
			}
			else {
				this.error_assertions.add(
					source_assertion.get_assertions().mut_value(expression));
			}
		}
		else if(this.is_one(rconstant) || this.is_neg_one(rconstant)) {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().set_value(expression, 0));
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
		Object lconstant = this.get_constant(loperand);
		
		if(lconstant != null) {
			if(this.is_zero(lconstant) || 
				this.is_one(lconstant) || 
				this.is_neg_one(lconstant)) {
				/** equivalent mutation **/
			}
			else {
				this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
			}
		}
		else {
			this.const_assertions.add(source_assertion.get_assertions().not_equals(loperand, 0));
			this.error_assertions.add(source_assertion.get_assertions().mut_value(expression));
		}
	}

}
