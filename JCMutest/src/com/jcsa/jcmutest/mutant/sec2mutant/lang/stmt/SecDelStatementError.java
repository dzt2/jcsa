package com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * add_statement(orig_stmt): the orig_stmt is NOT executed during testing even
 * though it should have been when tested against original program.
 * 
 * @author yukimula
 *
 */
public class SecDelStatementError extends SecStatementError {

	public SecDelStatementError(CirStatement statement, CirStatement orig_statement)
			throws Exception {
		super(statement, SecKeywords.del_statement, orig_statement);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_statement().generate_code() + ")";
	}

}

