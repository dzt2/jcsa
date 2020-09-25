package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecTrapError extends SecUniqueError {

	public SecTrapError(CirStatement statement) throws Exception {
		super(statement, SecKeywords.trap);
	}

}
