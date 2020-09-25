package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	SecUniqueError														<br>
 * 	|--	SecTrapError				trap()								<br>
 * 	|--	SecNoneError				none()								<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SecUniqueError extends SecStateError {

	public SecUniqueError(CirStatement statement, SecKeywords keyword) throws Exception {
		super(statement, keyword);
	}

	@Override
	public CirNode get_cir_location() {
		return this.get_statement().get_statement();
	}

	@Override
	protected String generate_content() throws Exception {
		return "()";
	}

}
