package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * seed#stmt:trap_stmt()
 * @author yukimula
 *
 */
public class SadTrapStatementAssertion extends SadMutStatementAssertion {

	protected SadTrapStatementAssertion(CirNode source) {
		super(source);
	}

	@Override
	protected String generate_content() throws Exception {
		return "trap_stmt()";
	}

	@Override
	protected SadNode clone_self() {
		return new SadTrapStatementAssertion(this.get_cir_source());
	}

}
