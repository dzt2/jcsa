package com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecTrpStatementError extends SecStatementError {

	public SecTrpStatementError(CirStatement statement, CirStatement orig_statement)
			throws Exception {
		super(statement, SecKeywords.trp_stmt, orig_statement);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_statement().generate_code() + ")";
	}

}
