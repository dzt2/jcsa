package com.jcsa.jcmutest.mutant.sec2mutant.lang.uniq;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecNoneError extends SecUniqueError {

	public SecNoneError(CirStatement statement) throws Exception {
		super(statement, SecKeywords.none);
	}

}
