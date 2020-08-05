package com.jcsa.jcparse.test.path.find;

import com.jcsa.jcparse.test.path.AstExecutionFlowType;
import com.jcsa.jcparse.test.path.AstExecutionNode;

class AstExecutionPathSolution {
	public AstExecutionNode beg;
	public AstExecutionNode end;
	public AstExecutionPathSolution() {
		this.beg = null;
		this.end = null;
	}
	public boolean executional() {
		return this.beg != null;
	}
	public void append(AstExecutionFlowType flow_type, 
			AstExecutionNode next) throws Exception {
		if(this.end == null) {
			this.beg = next;
			this.end = next;
		}
		else {
			this.end.connect(flow_type, next);
			this.end = next;
		}
	}
	public void append(AstExecutionFlowType flow_type, 
			AstExecutionPathSolution solution) throws Exception {
		if(this.beg == null) {
			this.beg = solution.beg;
			this.end = solution.end;
		}
		else {
			this.end.connect(flow_type, solution.beg);
			this.end = solution.end;
		}
	}
}
