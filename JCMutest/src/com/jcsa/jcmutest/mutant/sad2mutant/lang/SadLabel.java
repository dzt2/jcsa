package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * SadLabel |-- {execution: CirExecution}
 * 
 * @author yukimula
 *
 */
public class SadLabel extends SadToken {
	
	/** the executional node to which the label points **/
	private CirExecution execution;
	protected SadLabel(CirNode source, CirExecution execution) {
		super(source);
		this.execution = execution;
	}
	
	/**
	 * @return the executional node to which the label refers
	 */
	public CirExecution get_execution() {
		return this.execution;
	}
	
	@Override
	public String generate_code() throws Exception {
		return this.execution.toString();
	}
	
	@Override
	protected SadNode clone_self() {
		return new SadLabel(this.get_cir_source(), this.execution);
	}
	
}
