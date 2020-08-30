package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;

/**
 * assert#label: del_stmt(label)
 * @author yukimula
 *
 */
public class SedDelStatementError extends SedStatementError {

	@Override
	protected String generate_content() throws Exception {
		return "del_stmt(" + this.get_orig_statement().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedDelStatementError();
	}

}
