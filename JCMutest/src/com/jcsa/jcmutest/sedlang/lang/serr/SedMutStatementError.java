package com.jcsa.jcmutest.sedlang.lang.serr;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcmutest.sedlang.lang.token.SedStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * mut_stmt(orig_stmt, muta_stmt): the muta_stmt is executed following the
 * execution of the orig_stmt even though it should NOT be.
 * @author yukimula
 *
 */
public class SedMutStatementError extends SedStatementError {

	public SedMutStatementError(CirStatement statement, 
			CirStatement orig_statement,
			CirStatement muta_statement)
			throws Exception {
		super(statement, SedKeywords.mut_stmt, orig_statement);
		this.add_child(new SedStatement(muta_statement));
	}
	
	public SedStatement get_muta_statement() {
		return (SedStatement) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return this.get_orig_statement().generate_code() + 
				", " + this.get_muta_statement().generate_code();
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedMutStatementError(
				this.get_statement().get_cir_statement(),
				this.get_orig_statement().get_cir_statement(),
				this.get_muta_statement().get_cir_statement());
	}

}
