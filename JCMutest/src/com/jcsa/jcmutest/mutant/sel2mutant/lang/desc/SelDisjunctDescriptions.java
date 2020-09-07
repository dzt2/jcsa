package com.jcsa.jcmutest.mutant.sel2mutant.lang.desc;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SelDisjunctDescriptions extends SelDescriptions {

	public SelDisjunctDescriptions(CirStatement statement) throws Exception {
		super(statement, SelKeywords.disjunct);
	}

}
