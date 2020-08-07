package com.jcsa.jcparse.test.path.find;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.test.path.AstExecutionFlowType;
import com.jcsa.jcparse.test.path.AstExecutionNode;
import com.jcsa.jcparse.test.path.AstExecutionType;
import com.jcsa.jcparse.test.path.AstExecutionUnit;

/**
 * Used to preserve the data of solving path part of a given AST node
 * from the instrumental path data.
 * 
 * @author yukimula
 *
 */
class InstrumentPathSolution {
	
	/* attributes */
	/** the AST node that starts path part **/
	protected AstNode prev_node;
	/** the AST node as the start of next path part **/
	protected AstNode next_node;
	/** the first statement being executed **/
	protected AstExecutionNode beg_execution;
	/** the final statement being executed **/
	protected AstExecutionNode end_execution;
	
	/* constructor */
	/**
	 * create a solution w.r.t. the given location
	 * @param location
	 */
	protected InstrumentPathSolution(AstNode location) throws IllegalArgumentException {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else {
			this.prev_node = location;
			this.next_node = null;
			this.beg_execution = null;
			this.end_execution = null;
		}
	}
	
	/* setters */
	/**
	 * set the execution node as the beginning of the path range in the solution
	 * @param beg_execution
	 * @throws IllegalArgumentException
	 */
	protected void append(AstExecutionNode beg_execution) throws IllegalArgumentException {
		if(this.beg_execution != null || this.end_execution != null)
			throw new IllegalArgumentException("Invalid beg_execution: null");
		else {
			this.beg_execution = beg_execution;
			this.end_execution = beg_execution;
		}
	}
	/**
	 * append the execution node at the end of the path range in this solution
	 * @param flow_type
	 * @param execution
	 * @throws IllegalArgumentException
	 */
	protected void append(AstExecutionFlowType flow_type, AstExecutionNode execution) throws IllegalArgumentException {
		if(this.beg_execution == null || this.end_execution == null)
			throw new IllegalArgumentException("Invalid access: no range is set");
		else {
			this.end_execution.connect(flow_type, execution);
			this.end_execution = execution;
		}
	}
	/**
	 * @return whether the path range of the solution is empty without any nodes
	 */
	protected boolean is_empty() { return this.beg_execution == null; }
	/**
	 * append the execution range at the tail of this path range of the solution
	 * @param flow_type
	 * @param solution
	 */
	protected void append(AstExecutionFlowType flow_type, InstrumentPathSolution solution) throws IllegalArgumentException {
		if(this.beg_execution == null || this.end_execution == null)
			throw new IllegalArgumentException("Invalid access: no range is set");
		else if(!solution.is_empty()) {
			this.end_execution.connect(flow_type, solution.beg_execution);
			this.end_execution = solution.end_execution;
		}
	}
	/**
	 * @param type
	 * @param location
	 * @return the last execution node to the end of the path range in this solution w.r.t. the input arguments
	 * @throws IllegalArgumentException
	 */
	protected AstExecutionNode last_node(AstExecutionType type, AstNode location) {
		AstExecutionNode last_node = this.end_execution;
		while(last_node != null) {
			AstExecutionUnit unit = last_node.get_unit();
			if(unit.get_type() == type && unit.get_location() == location) {
				return last_node;
			}
			else if(last_node.has_in_flow()) {
				last_node = last_node.get_in_flow().get_source();
			}
			else {
				last_node = null;
			}
		}
		return last_node;
	}
	/**
	 * @param location
	 * @return the last execution node to the end of the path range in this solution w.r.t. the input arguments
	 */
	protected AstExecutionNode last_node(AstNode location) {
		AstExecutionNode last_node = this.end_execution;
		while(last_node != null) {
			AstExecutionUnit unit = last_node.get_unit();
			if(unit.get_location() == location) {
				return last_node;
			}
			else if(last_node.has_in_flow()) {
				last_node = last_node.get_in_flow().get_source();
			}
			else {
				last_node = null;
			}
		}
		return last_node;
	}
	/**
	 * @param next_node set the AST-node as the input of the next solution of solving algorithm
	 */
	protected void set_next_node(AstNode next_node) { this.next_node = next_node; }
	/**
	 * @return whether there is a AST node as the input of the next solution in traversal path.
	 */
	protected boolean has_next_node() { return this.next_node != null; }
	/**
	 * @return the solution that represents the range of the next AST-node in traversal path
	 * @throws IllegalArgumentException
	 */
	protected InstrumentPathSolution next_solution() throws IllegalArgumentException {
		if(this.next_node == null)
			throw new IllegalArgumentException("No more next-node in current solution");
		else {
			InstrumentPathSolution solution = new InstrumentPathSolution(this.next_node);
			solution.beg_execution = this.beg_execution;
			solution.end_execution = this.end_execution;
			return solution;
		}
	}
	
}
