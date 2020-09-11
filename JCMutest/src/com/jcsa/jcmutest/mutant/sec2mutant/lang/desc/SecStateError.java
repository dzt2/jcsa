package com.jcsa.jcmutest.mutant.sec2mutant.lang.desc;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class SecStateError extends SecDescription {

	public SecStateError(CirStatement statement, SecKeywords keyword) throws Exception {
		super(statement, keyword);
	}

}
