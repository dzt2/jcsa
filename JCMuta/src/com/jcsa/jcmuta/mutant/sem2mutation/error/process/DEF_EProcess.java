package com.jcsa.jcmuta.mutant.sem2mutation.error.process;

import com.jcsa.jcmuta.mutant.sem2mutation.error.StateErrorProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;

public class DEF_EProcess extends StateErrorProcess {
	
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
		CirDeferExpression expression = (CirDeferExpression) target;
		this.error_assertions.add(source_assertion.get_assertions().mut_refer(expression));
	}

	@Override
	protected void process_mut_refer(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirDeferExpression expression = (CirDeferExpression) target;
		this.error_assertions.add(source_assertion.get_assertions().mut_refer(expression));
	}

	@Override
	protected void process_not_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirDeferExpression expression = (CirDeferExpression) target;
		this.error_assertions.add(source_assertion.get_assertions().mut_refer(expression));
	}

	@Override
	protected void process_inc_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirDeferExpression expression = (CirDeferExpression) target;
		this.error_assertions.add(source_assertion.get_assertions().mut_refer(expression));
	}

	@Override
	protected void process_dec_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirDeferExpression expression = (CirDeferExpression) target;
		this.error_assertions.add(source_assertion.get_assertions().mut_refer(expression));
	}

	@Override
	protected void process_neg_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirDeferExpression expression = (CirDeferExpression) target;
		this.error_assertions.add(source_assertion.get_assertions().mut_refer(expression));
	}

	@Override
	protected void process_rsv_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirDeferExpression expression = (CirDeferExpression) target;
		this.error_assertions.add(source_assertion.get_assertions().mut_refer(expression));
	}

	@Override
	protected void process_set_bool(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirDeferExpression expression = (CirDeferExpression) target;
		this.error_assertions.add(source_assertion.get_assertions().mut_refer(expression));
	}

	@Override
	protected void process_set_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirDeferExpression expression = (CirDeferExpression) target;
		this.error_assertions.add(source_assertion.get_assertions().mut_refer(expression));
	}

	@Override
	protected void process_dif_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirDeferExpression expression = (CirDeferExpression) target;
		this.error_assertions.add(source_assertion.get_assertions().mut_refer(expression));
	}

}
