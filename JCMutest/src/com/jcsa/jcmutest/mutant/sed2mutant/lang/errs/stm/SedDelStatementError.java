package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.stm;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * del_stmt(orig_stmt): the original statement is not executed even though it should
 * have been executed by the same test in the original version of program for test.
 * @author yukimula
 *
 */
public class SedDelStatementError extends SedStatementError {

	public SedDelStatementError(CirStatement location, CirStatement orig_statement) {
		super(location, orig_statement);
	}

	@Override
	protected String generate_content() throws Exception {
		return "del_stmt(" + this.get_orig_statement().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedDelStatementError(
				this.get_location().get_cir_statement(),
				this.get_orig_statement().get_cir_statement());
	}

}
