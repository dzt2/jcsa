package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * add_statement(orig_stmt): the orig_stmt is executed during testing even
 * though it should NOT be when tested against original program.
 * 
 * @author yukimula
 *
 */
public class SecAddStatementError extends SecStatementError {

	public SecAddStatementError(CirStatement statement, CirStatement orig_statement)
			throws Exception {
		super(statement, SecKeywords.add_statement, orig_statement);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_statement().generate_code() + ")";
	}

}
