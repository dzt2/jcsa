package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * seed#statement:del_stmt()
 * @author yukimula
 *
 */
public class SadDelStatementAssertion extends SadMutStatementAssertion {

	protected SadDelStatementAssertion(CirNode source) {
		super(source);
	}

	@Override
	protected String generate_content() throws Exception {
		return "del_stmt(" + this.get_location().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadDelStatementAssertion(this.get_cir_source());
	}

}
