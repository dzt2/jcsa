package com.jcsa.jcmutest.mutant.sel2mutant.lang.desc;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SelConjunctDescriptions extends SelDescriptions {

	public SelConjunctDescriptions(CirStatement statement) throws Exception {
		super(statement, SelKeywords.conjunct);
	}

}
