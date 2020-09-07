package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SelStatementError extends SelDescription {

	protected SelStatementError(CirStatement statement, SelKeywords 
			keyword, CirStatement orig_statement) throws Exception {
		super(statement, keyword);
		this.add_child(new SelStatement(orig_statement));
	}
	
	/**
	 * @return the statement being mutated
	 */
	public SelStatement get_orig_statement() {
		return (SelStatement) this.get_child(2);
	}
	
}
