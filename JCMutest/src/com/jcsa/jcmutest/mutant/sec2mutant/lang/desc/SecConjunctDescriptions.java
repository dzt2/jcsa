package com.jcsa.jcmutest.mutant.sec2mutant.lang.desc;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecConjunctDescriptions extends SecDescriptions {

	public SecConjunctDescriptions(CirStatement statement) throws Exception {
		super(statement, SecKeywords.conjunct);
	}

}
