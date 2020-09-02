package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.stm;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * add_stmt(orig_stmt): the statement is executed though it should NOT be executed
 * in the correct version of program under the same test.
 * @author yukimula
 *
 */
public class SedAddStatementError extends SedStatementError {

	public SedAddStatementError(CirStatement location, CirStatement orig_statement) {
		super(location, orig_statement);
	}

	@Override
	protected String generate_content() throws Exception {
		return "add_stmt(" + this.get_orig_statement().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedAddStatementError(
				this.get_location().get_cir_statement(),
				this.get_orig_statement().get_cir_statement());
	}

}
