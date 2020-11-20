package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import com.jcsa.jcparse.flwa.symbol.CStateContexts;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

public class SymTrapError extends SymStateError {

	protected SymTrapError(CirExecution execution) throws IllegalArgumentException {
		super(SymInstanceType.trap_error, execution, execution.get_statement());
	}

	@Override
	protected String generate_code() throws Exception {
		return this.get_type() + ":" + this.get_execution();
	}

	@Override
	public Boolean validate(CStateContexts contexts) throws Exception {
		return Boolean.TRUE;
	}

}
