package com.jcsa.jcmutest.mutant.sec2mutant.lang.desc;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecDisjunctDescriptions extends SecDescriptions {

	public SecDisjunctDescriptions(CirStatement statement) throws Exception {
		super(statement, SecKeywords.disjunct);
	}

}
