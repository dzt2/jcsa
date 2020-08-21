package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

public class SedLabel extends SedNode {
	
	/* definitions */
	/** the executional node to which the label points **/
	private CirExecution execution;
	protected SedLabel(CirNode source, CirExecution execution) {
		super(source);
		this.execution = execution;
	}
	/**
	 * @return the executional node to which the label points
	 */
	public CirExecution get_execution() {
		return this.execution;
	}
	
	/* implementations */
	@Override
	protected SedNode copy_self() {
		return new SedLabel(this.get_source(), this.execution);
	}
	@Override
	public String generate_code() throws Exception {
		if(this.execution != null)
			return "<" + this.execution + ">";
		else
			return "<?>";
	}
	
}
