package com.jcsa.jcparse.flwa.context;

import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCall;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;

/**
 * The calling context based instance graph to describe the execution of statement
 * or flows in the context of a function being called in the calling tree as given
 *
 * @author yukimula
 *
 */
public class CirCallContextInstanceGraph extends CirInstanceGraph {

	/* constructor */
	private CirFunctionCallTree call_tree;
	private CirCallContextInstanceGraph(CirFunctionCallTree call_tree) throws Exception {
		super(call_tree.get_root().get_function().get_graph().get_cir_tree());
		this.call_tree = call_tree;
	}

	/* getters */
	/**
	 * get the function calling tree of which node is taken as the context
	 * in which the instance of statements or flows are created in graph.
	 * @return
	 */
	public CirFunctionCallTree get_call_tree() { return this.call_tree; }
	/**
	 * get all the call context nodes created within the function calling tree
	 * @return
	 */
	public Iterable<CirFunctionCallTreeNode> get_call_contexts() { return call_tree.get_nodes(); }
	/**
	 * whether there is an instance of statement being executed referring to the given context in this graph
	 * @param context
	 * @param execution
	 * @return
	 */
	public boolean has_execution(CirFunctionCallTreeNode context, CirExecution execution) {
		return this.has_instance(context, execution);
	}
	/**
	 * get the instance of the statement being executed in the speicfied calling context
	 * @param context
	 * @param execution
	 * @return
	 * @throws Exception
	 */
	public CirInstanceNode get_execution(CirFunctionCallTreeNode context, CirExecution execution) throws Exception {
		return this.get_instance(context, execution);
	}
	/**
	 * get the instance of the entry statement of the function under given context.
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public CirInstanceNode get_entry_execution(CirFunctionCallTreeNode context) throws Exception {
		return this.get_instance(context, context.get_function().get_flow_graph().get_entry());
	}
	/**
	 * get the instance of the exit statement of the function under given context.
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public CirInstanceNode get_exit_execution(CirFunctionCallTreeNode context) throws Exception {
		return this.get_instance(context, context.get_function().get_flow_graph().get_exit());
	}

	/* builder */
	/**
	 * create all the nodes within the context
	 * @param context
	 * @throws Exception
	 */
	private void create_nodes(CirFunctionCallTreeNode context) throws Exception {
		Iterable<CirExecution> executions = context.
				get_function().get_flow_graph().get_executions();
		for(CirExecution execution : executions) {
			if(execution.is_reachable()) this.new_node(context, execution);
		}
	}
	private void create_nodes() throws Exception {
		for(CirFunctionCallTreeNode context : this.call_tree.get_nodes()) {
			this.create_nodes(context);
		}
	}
	private void create_edge(CirInstanceNode source) throws Exception {
		/* 1. declarations */
		CirFunctionCallTreeNode source_context = (CirFunctionCallTreeNode) source.get_context();
		CirExecution source_execution = source.get_execution(); CirFunctionCall calling;
		Iterable<CirExecutionFlow> ou_flows = source_execution.get_ou_flows();
		CirFunctionCallTreeNode target_context; CirExecution target_execution;
		CirExecutionFlow source_target_flow;
		CirFunctionCallGraph call_graph = this.get_cir_tree().get_function_call_graph();
		Iterable<CirFunctionCallTreeNode> children; CirFunctionCall parent_call;

		/* 2. create output edge for each output flow from source execution */
		for(CirExecutionFlow ou_flow : ou_flows) {
			/* A. determine the target and flow for creating instance */
			switch(ou_flow.get_type()) {

			// (1) create the flow instance if function expands in this
			// 	   point or directly skip flow to target.
			case call_flow:
			{
				calling = call_graph.get_calling(ou_flow);
				children = source_context.get_children();

				target_context = source_context;
				target_execution = calling.get_wait_execution();
				source_target_flow = null;

				for(CirFunctionCallTreeNode child : children) {
					if(child.get_context() == calling) {
						target_context = child;
						target_execution = ou_flow.get_target();
						source_target_flow = ou_flow;
						break;
					}
				}
			}
			break;

			// (2) create the flow instance if function returns at this
			//	   this point or ignore it otherwise.
			case retr_flow:
			{
				calling = call_graph.get_calling(ou_flow);
				parent_call = source_context.get_context();

				target_context = null;
				target_execution = null;
				source_target_flow = null;

				if(calling == parent_call) {
					target_context = source_context.get_parent();
					target_execution = ou_flow.get_target();
					source_target_flow = ou_flow;
				}
			}
			break;

			// (3) create the instance for any internal flows in graph
			default:
			{
				target_context = source_context;
				target_execution = ou_flow.get_target();
				source_target_flow = ou_flow;
			}
			break;

			}	/* end of switch */

			/* B. create an edge from the source to the target */
			if(target_context != null) {
				CirInstanceNode target = this.get_instance(target_context, target_execution);
				if(source_target_flow != null) {
					this.new_edge(source, target, source_context, source_target_flow);
				}
				else {
					this.new_edge(source, target, source_context);
				}
			}

		}	/* end of for */
	}
	private void create_edges(CirFunctionCallTreeNode context) throws Exception {
		Iterable<CirExecution> executions = context.
				get_function().get_flow_graph().get_executions();
		for(CirExecution execution : executions) {
			if(execution.is_reachable()) {
				CirInstanceNode source = this.get_instance(context, execution);
				this.create_edge(source);
			}
		}
	}
	private void create_edges() throws Exception {
		for(CirFunctionCallTreeNode context : this.call_tree.get_nodes()) {
			this.create_edges(context);
		}
	}
	public static CirCallContextInstanceGraph graph(CirFunction root_function,
			CirFunctionCallPathType type, int maximal_depth) throws Exception {
		CirFunctionCallTree tree = CirFunctionCallTree.tree(root_function, type, maximal_depth);
		CirCallContextInstanceGraph graph = new CirCallContextInstanceGraph(tree);
		graph.create_nodes(); graph.create_edges(); graph.update_heads_and_tails(); return graph;
	}

}
