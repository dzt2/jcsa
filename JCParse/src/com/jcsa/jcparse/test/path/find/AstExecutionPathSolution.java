package com.jcsa.jcparse.test.path.find;

import com.jcsa.jcparse.test.path.AstExecutionFlowType;
import com.jcsa.jcparse.test.path.AstExecutionNode;

class AstExecutionPathSolution {
	public AstExecutionNode beg;
	public AstExecutionNode end;
	public AstExecutionPathSolution(AstExecutionNode beg) {
		this.beg = beg;
		this.end = beg;
	}
	public void append(AstExecutionFlowType flow_type, 
			AstExecutionNode next) throws Exception {
		this.end.connect(flow_type, next);
		this.end = next;
	}
	public void append(AstExecutionFlowType flow_type, 
			AstExecutionPathSolution solution) throws Exception {
		this.end.connect(flow_type, solution.beg);
		this.end = solution.end;
	}
}
