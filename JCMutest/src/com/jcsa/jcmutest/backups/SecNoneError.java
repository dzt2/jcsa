package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecNoneError extends SecUniqueError {

	public SecNoneError(CirStatement statement) throws Exception {
		super(statement, SecKeywords.none);
	}

}
