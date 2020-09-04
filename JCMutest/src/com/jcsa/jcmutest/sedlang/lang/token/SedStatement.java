package com.jcsa.jcmutest.sedlang.lang.token;

import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedStatement extends SedToken {
	
	private CirExecution cir_execution;
	public SedStatement(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement");
		else {
			this.cir_execution = statement.get_tree().get_function_call_graph().
					get_function(statement).get_flow_graph().get_execution(statement);
		}
	}
	
	/**
	 * @return the executional node of the statement
	 */
	public CirExecution get_cir_execution() { return this.cir_execution; }
	
	/**
	 * @return the statement to which this node points
	 */
	public CirStatement get_cir_statement() { return this.cir_execution.get_statement(); }
	
	@Override
	public String generate_code() throws Exception {
		return this.cir_execution.toString();
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedStatement(this.get_cir_statement());
	}

}
