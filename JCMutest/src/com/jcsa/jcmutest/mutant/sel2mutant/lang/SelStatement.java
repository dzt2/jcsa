package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SelStatement extends SelToken {
	
	private CirExecution execution;
	protected SelStatement(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement");
		else {
			this.execution = statement.get_tree().
					get_function_call_graph().
					get_function(statement).
					get_flow_graph().
					get_execution(statement);
		}
	}
	
	/**
	 * @return the executional node where the node points to
	 */
	public CirExecution get_execution() { return this.execution; }
	
	/**
	 * @return the statement in IR code to which this node refers
	 */
	public CirStatement get_statement() { return this.execution.get_statement(); }
	

	@Override
	public String generate_code() throws Exception {
		return this.execution.toString();
	}

}
