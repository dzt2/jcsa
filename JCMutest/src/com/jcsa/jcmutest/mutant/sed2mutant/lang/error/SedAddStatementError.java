package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;

/**
 * add_stmt(statement)
 * @author dzt2
 *
 */
public class SedAddStatementError extends SedStatementError {

	@Override
	protected SedNode clone_self() {
		return new SedAddStatementError();
	}

	@Override
	public String generate_code() throws Exception {
		return "add_stmt(" + this.get_orig_statement().generate_code() + ")";
	}

}
