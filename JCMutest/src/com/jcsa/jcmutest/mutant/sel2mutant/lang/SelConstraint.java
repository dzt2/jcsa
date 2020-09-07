package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SelConstraint extends SelDescription {

	protected SelConstraint(CirStatement statement, SelKeywords keyword) throws Exception {
		super(statement, keyword);
	}

}
