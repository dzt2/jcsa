package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SelDisjunctDescriptions extends SelDescriptions {

	protected SelDisjunctDescriptions(CirStatement statement) throws Exception {
		super(statement, SelKeywords.disjunct);
	}

}
