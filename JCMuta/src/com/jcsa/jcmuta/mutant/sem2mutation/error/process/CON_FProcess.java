package com.jcsa.jcmuta.mutant.sem2mutation.error.process;

import com.jcsa.jcmuta.mutant.sem2mutation.error.StateErrorProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class CON_FProcess extends StateErrorProcess {

	@Override
	protected void process_active(SemanticAssertion source_assertion, CirNode target) throws Exception {
		this.throw_error_propagation(source_assertion, target);
	}

	@Override
	protected void process_disactive(SemanticAssertion source_assertion, CirNode target) throws Exception {
		this.throw_error_propagation(source_assertion, target);
	}
	
	private void exec_on_true(SemanticAssertion source_assertion, CirStatement statement) throws Exception {
		if(!(statement instanceof CirTagStatement)) {
			this.error_assertions.add(source_assertion.get_assertions().disactive(statement));
		}
	}
	
	private void exec_on_false(SemanticAssertion source_assertion, CirStatement statement) throws Exception {
		if(!(statement instanceof CirTagStatement)) {
			this.error_assertions.add(source_assertion.get_assertions().active(statement));
		}
	}
	
	@Override
	protected void process_mut_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		/** undecidable **/
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
		this.process_mut_value(source_assertion, target);
	}

	@Override
	protected void process_dec_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		this.process_mut_value(source_assertion, target);
	}

	@Override
	protected void process_neg_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		/** equivalent mutation **/
	}

	@Override
	protected void process_rsv_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		this.exec_on_true(source_assertion, (CirStatement) target);
	}

	@Override
	protected void process_set_bool(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirStatement statement = (CirStatement) target;
		Boolean value = (Boolean) source_assertion.get_operand(1);
		
		if(value) {
			this.exec_on_true(source_assertion, statement);
		}
		else {
			this.exec_on_false(source_assertion, statement);
		}
	}

	@Override
	protected void process_set_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		CirStatement statement = (CirStatement) target;
		Object value = this.cast_to_numeric_value(source_assertion.get_operand(1));
		
		boolean bool_value;
		if(value instanceof Long) {
			bool_value = ((Long) value).longValue() != 0;
		}
		else {
			bool_value = ((Double) value).doubleValue() != 0;
		}
		
		if(bool_value) {
			this.exec_on_true(source_assertion, statement);
		}
		else {
			this.exec_on_false(source_assertion, statement);
		}
	}

	@Override
	protected void process_dif_value(SemanticAssertion source_assertion, CirNode target) throws Exception {
		this.process_mut_value(source_assertion, target);
	}

}
