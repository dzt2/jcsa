package com.jcsa.jcparse.lang.irlang.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionBody;

/**
 * The program flow graph to describe the execution of statements in one single function
 * which is defined in the source code. For those that are used and not defined in source
 * file, there is no execution flow graph to describe it but using a "skip_flow" from the
 * calling statement to the waiting statement directly, which means the transition skips
 * from thousands of statements and may not reach the final point (probabilistic).
 * 
 * @author yukimula
 *
 */
public class CirExecutionFlowGraph {
	
	private CirFunction function;
	private List<CirExecution> executions;
	private Map<CirStatement, CirExecution> index;
	private List<CirExecution> reach_set;
	/**
	 * create an execution flow graph for the statements in the function
	 * @param function
	 * @throws Exception
	 */
	protected CirExecutionFlowGraph(CirFunction function) throws Exception {
		if(function == null)
			throw new IllegalArgumentException("invalid function");
		else {
			this.function = function;
			this.executions = new ArrayList<CirExecution>();
			this.index = new HashMap<CirStatement, CirExecution>();
			this.reach_set = new ArrayList<CirExecution>();
			this.init_entry_and_exit();
		}
	}
	private void init_entry_and_exit() throws Exception {
		CirFunctionBody body = function.get_definition().get_body();
		CirBegStatement beg = (CirBegStatement) body.get_statement(0);
		CirEndStatement end = (CirEndStatement) body.get_statement(body.number_of_statements() - 1);
		this.new_execution(end); this.new_execution(beg);
	}
	
	/* getters */
	/**
	 * get the function of the flow graph
	 * @return
	 */
	public CirFunction get_function() { return this.function; }
	/**
	 * get the number of the statement executions in the graph
	 * @return
	 */
	public int size() { return this.executions.size(); }
	/**
	 * get the statement executions in the flow graph
	 * @return
	 */
	public Iterable<CirExecution> get_executions() { return this.executions; }
	/**
	 * get the set of statements that can be reached from the entry of the graph.
	 * @return
	 */
	public Iterable<CirExecution> get_reachable_executions() { return this.reach_set; }
	/**
	 * get the kth statement execution in the graph
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecution get_execution(int k) throws IndexOutOfBoundsException {
		return this.executions.get(k);
	}
	/**
	 * whether there is an execution of the statement in the function's flow graph.
	 * @param statement
	 * @return
	 */
	public boolean has_execution(CirStatement statement) { return index.containsKey(statement); }
	/**
	 * get the execution of the statement in the function's flow graph
	 * @param statement
	 * @return
	 * @throws IllegalArgumentException
	 */
	public CirExecution get_execution(CirStatement statement) throws IllegalArgumentException {
		if(this.index.containsKey(statement)) return index.get(statement);
		else throw new IllegalArgumentException("undefined: " + statement.get_node_id());
	}
	/**
	 * get the entry of the function
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecution get_entry() throws IndexOutOfBoundsException { return this.executions.get(1); }
	/**
	 * get the exits of the function
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecution get_exit() throws IndexOutOfBoundsException { return this.get_execution(0); }
	
	/* setters */
	/**
	 * create the execution for the statement in the function's flow graph or
	 * return the existing one.
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	protected CirExecution new_execution(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("invalid statement: null");
		else if(index.containsKey(statement)) return index.get(statement);
		else {
			CirExecution execution = new CirExecution(this, executions.size(), statement);
			executions.add(execution); index.put(statement, execution); return execution;
		}
	}
	/**
	 * update the set of statements that can be reached from the entry of the graph
	 */
	protected void update_reachable_set() {
		this.reach_set.clear();
		for(CirExecution execution : this.executions) {
			if(execution.is_reachable())
				this.reach_set.add(execution);
		}
	}
}
