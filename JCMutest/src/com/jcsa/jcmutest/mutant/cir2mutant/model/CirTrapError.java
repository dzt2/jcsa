package com.jcsa.jcmutest.mutant.cir2mutant.model;

import com.jcsa.jcmutest.mutant.cir2mutant.CirErrorType;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>trap_on(statement)</code>: an exception is thrown at the statement, and
 * the program is forcedly terminated because the exception causes an unexpected 
 * behavior of program under test.<br>
 * 
 * @author yukimula
 *
 */
public class CirTrapError extends CirStateError {

	protected CirTrapError(CirStatement statement) throws Exception {
		super(CirErrorType.trap_error, statement);
	}

	@Override
	protected String generate_code() throws Exception {
		return "";
	}

}
