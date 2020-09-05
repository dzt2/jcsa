package com.jcsa.jcmutest.sedlang.lang.serr;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedDelStatementError extends SedStatementError {

	public SedDelStatementError(CirStatement statement, CirStatement orig_statement)
			throws Exception {
		super(statement, SedKeywords.del_stmt, orig_statement);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_statement().generate_code() + ")";
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedDelStatementError(
				this.get_statement().get_cir_statement(),
				this.get_orig_statement().get_cir_statement());
	}

}
