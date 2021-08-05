package com.jcsa.jcparse.lang.irlang.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirIdentifier;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionBody;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;

/**
 * To construct the function graph and flow graph based on the C-IR code provided.
 *
 * @author yukimula
 *
 */
class CirFunctionBuilder {

	private CirFunctionCallGraph graph;
	private static final CirFunctionBuilder builder = new CirFunctionBuilder();
	private CirFunctionBuilder() { }

	private void open(CirTree cir_tree) throws Exception {
		this.graph = new CirFunctionCallGraph(cir_tree);
	}
	private void create_nodes() throws Exception {
		Iterable<CirFunction> functions = graph.get_functions();
		for(CirFunction function : functions) {
			CirFunctionDefinition definition = function.get_definition();
			CirFunctionBody function_body = definition.get_body();
			CirExecutionFlowGraph flow_graph = function.get_flow_graph();

			for(int k = 0; k < function_body.number_of_statements(); k++) {
				CirStatement statement = function_body.get_statement(k);
				flow_graph.new_execution(statement);
			}
		}
	}
	private void create_edge(CirExecutionFlowGraph flow_graph, CirFunctionBody function_body, CirExecution execution) throws Exception {
		CirStatement statement = execution.get_statement();
		if(statement instanceof CirAssignStatement) {
			int index = statement.get_child_index() + 1;
			CirStatement next_statement = function_body.get_statement(index);
			CirExecution next_execution = flow_graph.get_execution(next_statement);
			execution.link_to(next_execution, CirExecutionFlowType.next_flow);
		}
		else if(statement instanceof CirGotoStatement) {
			int next_id = ((CirGotoStatement) statement).get_label().get_target_node_id();
			if(next_id < 0) {
				System.out.println(statement.get_ast_source().get_code());
			}
			CirStatement next_statement = (CirStatement) function_body.get_tree().get_node(next_id);
			CirExecution next_execution = flow_graph.get_execution(next_statement);
			execution.link_to(next_execution, CirExecutionFlowType.next_flow);
		}
		else if(statement instanceof CirIfStatement) {
			int true_id = ((CirIfStatement) statement).get_true_label().get_target_node_id();
			int fals_id = ((CirIfStatement) statement).get_false_label().get_target_node_id();
			/* for debug
			if(true_id < 0)
				System.out.println("ERROR at " + statement.get_ast_source().get_code());
			*/
			CirStatement true_statement = (CirStatement) function_body.get_tree().get_node(true_id);
			CirStatement fals_statement = (CirStatement) function_body.get_tree().get_node(fals_id);
			CirExecution true_execution = flow_graph.get_execution(true_statement);
			CirExecution fals_execution = flow_graph.get_execution(fals_statement);
			execution.link_to(true_execution, CirExecutionFlowType.true_flow);
			execution.link_to(fals_execution, CirExecutionFlowType.fals_flow);
		}
		else if(statement instanceof CirCaseStatement) {
			int true_id = statement.get_child_index() + 1;
			int fals_id = ((CirCaseStatement) statement).get_false_label().get_target_node_id();
			CirStatement true_statement = function_body.get_statement(true_id);
			CirStatement fals_statement = (CirStatement) function_body.get_tree().get_node(fals_id);
			CirExecution true_execution = flow_graph.get_execution(true_statement);
			CirExecution fals_execution = flow_graph.get_execution(fals_statement);
			execution.link_to(true_execution, CirExecutionFlowType.true_flow);
			execution.link_to(fals_execution, CirExecutionFlowType.fals_flow);
		}
		else if(statement instanceof CirCallStatement) {
			CirFunctionCallGraph fun_graph = this.graph;
			CirExpression fun_expr = ((CirCallStatement) statement).get_function();

			CirFunction callee_function = null;
			if(fun_expr instanceof CirIdentifier) {
				String function_name = ((CirIdentifier) fun_expr).get_cname().get_name();
				if(fun_graph.has_function(function_name)) {
					callee_function = fun_graph.get_function(function_name);
				}
				else {
					// throw new IllegalArgumentException("Unable to find: " + function_name);
				}
			}

			int wait_id = statement.get_child_index() + 1;
			CirWaitAssignStatement wait_statement = (CirWaitAssignStatement) function_body.get_child(wait_id);
			CirExecution wait_execution = flow_graph.get_execution(wait_statement);

			/* external function called: call -->[skip_flow]--> wait */
			if(callee_function == null) {
				execution.link_to(wait_execution, CirExecutionFlowType.skip_flow);
			}
			/* user defined function called:
			 * 		call -->[call_flow]--> callee.entry
			 * 		callee.exits -->[retr_flow]--> wait
			 * */
			else {
				CirExecution callee_beg = callee_function.get_flow_graph().get_entry();
				CirExecution callee_end = callee_function.get_flow_graph().get_exit();
				CirExecutionFlow call_flow = execution.link_to(callee_beg, CirExecutionFlowType.call_flow);
				CirExecutionFlow retr_flow = callee_end.link_to(wait_execution, CirExecutionFlowType.retr_flow);
				fun_graph.call(call_flow, retr_flow);
			}
		}
		else if(statement instanceof CirTagStatement) {
			if(!(statement instanceof CirEndStatement)) {
				int index = statement.get_child_index() + 1;
				CirStatement next_statement = function_body.get_statement(index);
				CirExecution next_execution = flow_graph.get_execution(next_statement);
				execution.link_to(next_execution, CirExecutionFlowType.next_flow);
			}
		}
		else throw new IllegalArgumentException("unsupport: " + statement.getClass().getSimpleName());
	}
	private void create_edges() throws Exception {
		Iterable<CirFunction> functions = graph.get_functions();
		for(CirFunction function : functions) {
			CirExecutionFlowGraph flow_graph = function.get_flow_graph();
			CirFunctionDefinition definition = function.get_definition();
			CirFunctionBody function_body = definition.get_body();

			Iterable<CirExecution> executions = flow_graph.get_executions();
			for(CirExecution execution : executions) {
				this.create_edge(flow_graph, function_body, execution);
			}
		}
	}
	/**
	 * collect all the statement executions that can be reached from the entry of the flow graph
	 * based on the structure rather than the program semantics. The newly reached nodes will be
	 * pushed into the reaching set and update it.
	 * @param flow_graph
	 * @param reach_set
	 * @throws Exception
	 */
	private void create_reach_in_function(CirExecutionFlowGraph flow_graph, Set<CirExecution> reach_set) throws Exception {
		/* 1. declarations and get the entry of the flow graph */
		CirExecution entry = flow_graph.get_entry(), statement, next;
		Iterable<CirExecutionFlow> flows; Queue<CirExecution> queue;

		/* 2. traverse from the entry of the graph if not visited before */
		if(!reach_set.contains(entry)) {
			/* 2.A. initialize the queue for using the BFS algorithm */
			queue = new LinkedList<>();
			queue.add(entry); reach_set.add(entry);
			entry.set_reachable(true);

			/* 2.B. traverse all the nodes that can be reached in BFS */
			while(!queue.isEmpty()) {
				/* 2.B.1 get the next statement being traversed
				 * and its output flows */
				statement = queue.poll(); flows = statement.get_ou_flows();

				/* 2.B.2 traverse every node reached from the node being
				 * analyzed currently */
				for(CirExecutionFlow flow : flows) {
					/* (1) determine whether the next node can be reached */
					switch(flow.get_type()) {

					// case-A. when calling another function, traverse recursively
					case call_flow: {
						// a. get the calling where the flow is defined
						CirFunctionCall call = this.graph.get_calling(flow);

						// b. get the graph of the function being called
						CirExecutionFlowGraph callee_graph = call.get_callee().get_flow_graph();

						// c. recursively solving the reaching set of the callee graph
						this.create_reach_in_function(callee_graph, reach_set);

						// d. if the function's exit can be reached, set the waiting
						// statement execution as reachable from the callee's exit.
						if(callee_graph.get_exit().is_reachable())
							next = call.get_wait_execution();
						// e. otherwise, waiting statement execution cannot be reached
						else next = null;

						break;
					}

					// case-B. when reaching the exit of function, stop traversal
					case retr_flow: next = null; break;

					// case-C. for any internal flow in function, traverse the next
					default: next = flow.get_target(); break;

					}/* end of switch */

					/* (2) push the next node into queue if it is reached */
					if(next != null && !reach_set.contains(next)) {
						queue.add(next); reach_set.add(next);
						next.set_reachable(true);
					}

				}	/* end of for */

			}	/* end of while */
		}
	}
	/**
	 * Update the reaching set by traversing flow graph in function graph by one iteration.
	 * It returns true if the reaching set has been updated during the proceed.
	 * @param graph
	 * @param reach_set
	 * @return
	 * @throws Exception
	 */
	private boolean create_reach_in_functions(CirFunctionCallGraph graph, Set<CirExecution> reach_set) throws Exception {
		int size = reach_set.size(); reach_set.clear();

		Iterable<CirFunction> functions = graph.get_functions();
		for(CirFunction function : functions) {
			this.create_reach_in_function(
					function.get_flow_graph(), reach_set);
		}

		return size != reach_set.size();
	}
	/**
	 * fix-point algorithm to solve the reaching set of statements in flow graph
	 * @throws Exception
	 */
	private void create_reach() throws Exception {
		/* 1. fix-point algorithm to solve the reaching-sets */
		Set<CirExecution> reach_set = new HashSet<>();
		while(this.create_reach_in_functions(graph, reach_set)) {}

		/* 2. update the reaching set index in flow graph */
		Iterable<CirFunction> functions = graph.get_functions();
		for(CirFunction function : functions)
			function.get_flow_graph().update_reachable_set();
	}
	private void build() throws Exception {
		this.create_nodes();
		this.create_edges();
		this.create_reach();
	}
	private void close() { this.graph = null; }

	/**
	 * construct the function call graph and the execution flow graph for each function defined
	 * in the source code.
	 * @param cir_tree
	 * @return
	 * @throws Exception
	 */
	protected static CirFunctionCallGraph build(CirTree cir_tree) throws Exception {
		builder.open(cir_tree);
		builder.build();
		CirFunctionCallGraph graph = builder.graph;
		builder.close();
		return graph;
	}

}
