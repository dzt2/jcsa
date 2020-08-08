package com.jcsa.jcparse.test.path.find;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.test.path.AstExecutionFlowType;
import com.jcsa.jcparse.test.path.AstExecutionNode;
import com.jcsa.jcparse.test.path.AstExecutionType;

/**
 * The range of execution path is defined by two nodes, in which one refers to
 * the first executional node in the sub-sequence of path and another refers to
 * the final executional node in the sub-sequence of the path, which is used as 
 * the solution of the path building process.
 * 
 * @author yukimula
 *
 */
class AstExecutionRange {
	
	/* attributes */
	/** the location in AST on which the range is defined **/
	protected AstNode ast_location;
	/** the first execution node in the range of the path **/
	protected AstExecutionNode beg_execution;
	/** the final execution node in the range of the path **/
	protected AstExecutionNode end_execution;
	/**
	 * create an empty range of executional path
	 */
	protected AstExecutionRange(AstNode ast_location) {
		this.ast_location = ast_location;
		this.beg_execution = null;
		this.end_execution = null;
	}
	
	/* getters */
	/**
	 * @return whether the path range is empty
	 */
	protected boolean is_empty() {
		return this.beg_execution == null;
	}
	/**
	 * initialize the path range as [beg_execution, beg_execution]
	 * @param beg_execution
	 * @throws Exception
	 */
	protected void append(AstExecutionNode beg_execution) throws Exception {
		if(beg_execution == null)
			throw new IllegalArgumentException("Invalid beg_execution: null");
		else if(this.beg_execution != null)
			throw new IllegalArgumentException("Unable to initialize range.");
		else {
			this.beg_execution = beg_execution;
			this.end_execution = beg_execution;
		}
	}
	/**
	 * append the execution at the end of the range
	 * @param flow_type
	 * @param execution
	 * @throws Exception
	 */
	protected void append(AstExecutionFlowType flow_type, 
			AstExecutionNode execution) throws Exception {
		if(flow_type == null)
			throw new IllegalArgumentException("Invalid flow_type: null");
		else if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(this.end_execution == null)
			throw new IllegalArgumentException("Invalid range accessed");
		else {
			this.end_execution.connect(flow_type, execution);
			this.end_execution = execution;
		}
	}
	/**
	 * connect this range with another range as specified
	 * @param flow_type
	 * @param range
	 * @throws Exception
	 */
	protected void append(AstExecutionFlowType flow_type, 
			AstExecutionRange range) throws Exception {
		if(flow_type == null)
			throw new IllegalArgumentException("Invalid flow_type: null");
		else if(range == null)
			throw new IllegalArgumentException("Invalid range as null");
		else if(range.beg_execution != null) {
			if(this.beg_execution != null) {
				this.end_execution.connect(flow_type, range.beg_execution);
				this.end_execution = range.end_execution;
			}
			else {
				this.beg_execution = range.beg_execution;
				this.end_execution = range.end_execution;
			}
		}
	}
	
	/* finder */
	/**
	 * @param type
	 * @param location
	 * @return the last node in the range of the path w.r.t. the type and location
	 */
	protected AstExecutionNode find(AstExecutionType type, AstNode location) {
		AstExecutionNode execution = this.end_execution;
		while(execution != null) {
			if(execution.get_unit().get_type() == type
				&& execution.get_unit().get_location() == location) {
				return execution;
			}
			else if(execution.has_in_flow()) {
				execution = execution.get_in_flow().get_source();
			}
			else {
				execution = null;
			}
		}
		return execution;
	}
	/**
	 * @param location
	 * @return the last node in the range of the path w.r.t. the location
	 */
	protected AstExecutionNode find(AstNode location) {
		AstExecutionNode execution = this.end_execution;
		while(execution != null) {
			if(execution.get_unit().get_location() == location) {
				return execution;
			}
			else if(execution.has_in_flow()) {
				execution = execution.get_in_flow().get_source();
			}
			else {
				execution = null;
			}
		}
		return execution;
	}
	
	@Override
	public String toString() {
		if(this.beg_execution == null) 
			return "[]";
		else
			return "[" + this.beg_execution + ", " + this.end_execution + "]";
	}
	
}
