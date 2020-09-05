package com.jcsa.jcmutest.sedlang.lang.serr;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcmutest.sedlang.lang.token.SedStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * set_stmt(orig_stmt, muta_stmt): orig_stmt is executed in the semantics of muta_stmt.
 * 
 * @author yukimula
 *
 */
public class SedSetStatementError extends SedStatementError {

	public SedSetStatementError(CirStatement statement, 
			CirStatement orig_statement,
			CirStatement muta_statement)
			throws Exception {
		super(statement, SedKeywords.set_stmt, orig_statement);
		this.add_child(new SedStatement(muta_statement));
	}
	
	public SedStatement get_muta_statement() {
		return (SedStatement) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_statement().generate_code() + 
				", " + this.get_muta_statement().generate_code() + ")";
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedSetStatementError(
				this.get_statement().get_cir_statement(),
				this.get_orig_statement().get_cir_statement(),
				this.get_muta_statement().get_cir_statement());
	}

}
