package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * add_stmt(stmt) in which the statement is executed even though it should NOT
 * be executed in the testing with program of the correct version.
 * 
 * @author yukimula
 *
 */
public class SedAddStatementError extends SedStatementError {

	protected SedAddStatementError(CirStatement statement, 
			CirStatement orig_statement) throws Exception {
		super(statement, orig_statement);
	}

	@Override
	protected String generate_content() throws Exception {
		return "add_cstmt(" + this.get_orig_statement().generate_code() + ")";
	}

}
