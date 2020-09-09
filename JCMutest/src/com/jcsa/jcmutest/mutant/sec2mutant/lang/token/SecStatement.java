package com.jcsa.jcmutest.mutant.sec2mutant.lang.token;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecStatement extends SecToken {
	
	private CirExecution execution;
	public SecStatement(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement");
		else {
			this.execution = statement.get_tree().
					get_localizer().get_execution(statement);
		}
	}
	
	public CirExecution get_execution() { return this.execution; }
	
	public CirStatement get_statement() { return this.execution.get_statement(); }

	@Override
	public String generate_code() throws Exception {
		return this.execution.toString();
	}
	
}
