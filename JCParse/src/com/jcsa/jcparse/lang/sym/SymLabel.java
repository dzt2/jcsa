package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	label |-- {execution: CirExecution}
 * 	
 * 	@author yukimula
 *
 */
public class SymLabel extends SymUnit {
	
	/* definition */
	private CirExecution execution;
	protected SymLabel(CirExecution execution) throws IllegalArgumentException {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else 
			this.execution = execution;
	}
	
	/**
	 * @return the executional node to which the label refers
	 */
	public CirExecution get_execution() { return this.execution; }
	
	/**
	 * @return the statement in which the label is pointed
	 */
	public CirStatement get_statement() { return this.execution.get_statement(); }

	@Override
	protected SymNode construct() throws Exception {
		return new SymLabel(this.execution);
	}

	@Override
	public String generate_code() throws Exception {
		return this.execution.toString();
	}

}
