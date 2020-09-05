package com.jcsa.jcmutest.selang.lang.serr;

import com.jcsa.jcmutest.selang.SedKeywords;
import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedTrpStatementError extends SedStatementError {
	
	public SedTrpStatementError(CirStatement statement, CirStatement orig_statement)
			throws Exception {
		super(statement, SedKeywords.trp_stmt, orig_statement);
	}
	
	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_statement().generate_code() + ")";
	}
	
	@Override
	protected SedNode construct() throws Exception {
		return new SedTrpStatementError(
				this.get_statement().get_cir_statement(),
				this.get_orig_statement().get_cir_statement());
	}
	
}
