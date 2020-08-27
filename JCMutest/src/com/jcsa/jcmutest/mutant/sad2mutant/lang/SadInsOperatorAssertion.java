package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * seed#stmt:ins_oprt(expr, operator) :: expr --> operator(expr)
 * @author dzt2
 *
 */
public class SadInsOperatorAssertion extends SadMutExpressionAssertion {

	protected SadInsOperatorAssertion(CirNode source) {
		super(source);
	}
	
	public SadOperator get_ins_operator() {
		return (SadOperator) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return "ins_oprt(" + this.get_orig_expression().generate_code() + 
					", " + this.get_ins_operator().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadInsOperatorAssertion(this.get_cir_source());
	}

}
