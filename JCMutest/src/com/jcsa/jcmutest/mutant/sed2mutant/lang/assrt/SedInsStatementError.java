package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;

/**
 * assert#label: ins_stmt(label)
 * @author yukimula
 *
 */
public class SedInsStatementError extends SedStatementError {

	@Override
	protected String generate_content() throws Exception {
		return "ins_stmt(" + this.get_orig_statement().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedInsStatementError();
	}

}
