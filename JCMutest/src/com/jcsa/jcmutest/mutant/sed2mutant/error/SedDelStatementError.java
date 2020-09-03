package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * del_stmt(stmt): the statement is NOT executed even though it should be
 * executed in the testing with the program of correct version.
 * 
 * @author yukimula
 *
 */
public class SedDelStatementError extends SedStatementError {

	protected SedDelStatementError(CirStatement statement, 
			CirStatement orig_statement) throws Exception {
		super(statement, orig_statement);
	}

	@Override
	protected String generate_content() throws Exception {
		return "del_cstmt(" + this.get_orig_statement().generate_code() + ")";
	}

}
