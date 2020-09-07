package com.jcsa.jcmutest.mutant.sel2mutant.lang.stmt;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SelSetStatementError extends SelStatementError {

	public SelSetStatementError(CirStatement statement, 
			CirStatement orig_statement,
			CirStatement muta_statement) throws Exception {
		super(statement, SelKeywords.set_stmt, orig_statement);
		this.add_child(new SelStatement(muta_statement));
	}
	
	/**
	 * @return the statement being executed following the execution of 
	 * 		   the original statement when it should NOT be.
	 */
	public SelStatement get_muta_statement() {
		return (SelStatement) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_statement().generate_code() +
				", " + this.get_muta_statement().generate_code() + ")";
	}
	
}
