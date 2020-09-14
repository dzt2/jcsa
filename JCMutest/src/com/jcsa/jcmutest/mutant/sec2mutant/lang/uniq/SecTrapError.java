package com.jcsa.jcmutest.mutant.sec2mutant.lang.uniq;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecTrapError extends SecUniqueError {

	public SecTrapError(CirStatement statement) throws Exception {
		super(statement, SecKeywords.trap);
	}

}
