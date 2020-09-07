package com.jcsa.jcmutest.mutant.sel2mutant.lang.stmt;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.desc.SelDescription;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SelStatementError extends SelDescription {

	public SelStatementError(CirStatement statement, SelKeywords 
			keyword, CirStatement orig_statement) throws Exception {
		super(statement, keyword);
		this.add_child(new SelStatement(orig_statement));
	}
	
	/**
	 * @return the original statement being mutated
	 */
	public SelStatement get_orig_statement() {
		return (SelStatement) this.get_child(2);
	}
	
}
