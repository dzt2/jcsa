package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SelAddStatementError extends SelStatementError {

	protected SelAddStatementError(CirStatement statement, 
			CirStatement orig_statement) throws Exception {
		super(statement, SelKeywords.add_stmt, orig_statement);
	}

	@Override
	protected String generate_parameters() throws Exception {
		return "(" + this.get_orig_statement().generate_code() + ")";
	}

}
