package com.jcsa.jcmutest.mutant.cir2mutant.error;

import com.jcsa.jcmutest.mutant.cir2mutant.CirErrorType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * trap_error(statement) requires an exception being thrown at the statement
 * which forces the program being terminated.
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
		CirStatement statement = this.get_statement();
		CirExecution execution = statement.get_tree().
				get_localizer().get_execution(statement);
		return this.get_type() + "::" + execution.toString();
	}

}
