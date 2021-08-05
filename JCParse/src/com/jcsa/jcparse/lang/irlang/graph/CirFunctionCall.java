package com.jcsa.jcparse.lang.irlang.graph;

import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;

public class CirFunctionCall {

	private CirFunction caller, callee;
	private CirExecutionFlow call_flow;
	private CirExecutionFlow retr_flow;
	private CirExecution call_execution;
	private CirExecution wait_execution;

	protected CirFunctionCall(CirExecutionFlow call_flow,
			CirExecutionFlow retr_flow) throws IllegalArgumentException {
		if(call_flow == null || call_flow.get_type() != CirExecutionFlowType.call_flow)
			throw new IllegalArgumentException("invalid call_flow as null");
		else if(retr_flow == null || retr_flow.get_type() != CirExecutionFlowType.retr_flow)
			throw new IllegalArgumentException("invalid retr_flow as null");
		else {
			this.call_flow = call_flow; this.retr_flow = retr_flow;
			this.call_execution = call_flow.get_source();
			this.wait_execution = retr_flow.get_target();
			this.caller = this.call_execution.get_graph().get_function();
			this.callee = call_flow.get_target().get_graph().get_function();
		}
	}

	/* getters */
	/**
	 * get the function that calls another
	 * @return
	 */
	public CirFunction get_caller() { return this.caller; }
	/**
	 * get the function that is called
	 * @return
	 */
	public CirFunction get_callee() { return this.callee; }
	/**
	 * get the execution of the calling statement
	 * @return
	 */
	public CirExecution get_call_execution() { return this.call_execution; }
	/**
	 * get the execution of the waiting statement
	 * @return
	 */
	public CirExecution get_wait_execution() { return this.wait_execution; }
	/**
	 * get the calling statement
	 * @return
	 */
	public CirCallStatement get_call_statement() {
		return (CirCallStatement) call_execution.get_statement();
	}
	/**
	 * get the waiting statement
	 * @return
	 */
	public CirWaitAssignStatement get_wait_statement() {
		return (CirWaitAssignStatement) wait_execution.get_statement();
	}
	/**
	 * get the calling flow
	 * @return
	 */
	public CirExecutionFlow get_call_flow() { return call_flow; }
	/**
	 * get the returning flow
	 * @return
	 */
	public CirExecutionFlow get_retr_flow() { return retr_flow; }

}
