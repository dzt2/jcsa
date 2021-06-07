package com.jcsa.jcmutest.mutant.sym2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

public class SymTrapError extends SymStateError {

	protected SymTrapError(CirExecution execution) throws IllegalArgumentException {
		super(SymInstanceType.trap_error, execution, execution.get_statement());
	}

	@Override
	protected String generate_code() throws Exception {
		return this.get_type() + ":" + this.get_execution();
	}

	@Override
	public Boolean validate(SymbolProcess contexts) throws Exception {
		return Boolean.TRUE;
	}

}
