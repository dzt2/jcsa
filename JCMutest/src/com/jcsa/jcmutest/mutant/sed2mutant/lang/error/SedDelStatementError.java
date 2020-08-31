package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;

/**
 * del_stmt(statement)
 * @author dzt2
 *
 */
public class SedDelStatementError extends SedStatementError {

	@Override
	protected SedNode clone_self() {
		return new SedDelStatementError();
	}

	@Override
	public String generate_code() throws Exception {
		return "del_stmt(" + this.get_orig_statement().generate_code() + ")";
	}

}
