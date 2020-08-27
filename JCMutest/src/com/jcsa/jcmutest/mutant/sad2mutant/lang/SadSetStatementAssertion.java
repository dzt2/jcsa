package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * seed#source:set_stmt(target) ==> set target as the next statement for source
 * @author yukimula
 *
 */
public class SadSetStatementAssertion extends SadMutStatementAssertion {

	protected SadSetStatementAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the original statement where the mutation is seeded
	 */
	public SadStatement get_source_statement() {
		return this.get_location();
	}
	
	/**
	 * @return the statement being set as next one to be executed
	 */
	public SadStatement get_target_statement() {
		return (SadStatement) this.get_child(1);
	}

	@Override
	protected String generate_content() throws Exception {
		return "set_stmt(" + this.get_target_statement().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadSetStatementAssertion(this.get_cir_source());
	}
	
}
