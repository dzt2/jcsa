package com.jcsa.jcmutest.mutant.sta2mutant.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirBixorErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirBlockErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirFlowsErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirIncreErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirLimitTimesState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirMConstrainState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirNConstrainState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirReachTimesState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirTrapsErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirValueErrorState;
import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependPredicate;
import com.jcsa.jcparse.flwa.depend.CDependType;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It implements the subsumption inference between CirAbstractState(s) across
 * the level of C-intermediate representative locations.
 * 
 * @author yukimula
 *
 */
public final class StateCrossInference {
	
	/* singleton mode */ /** constructor **/ private StateCrossInference() { }
	private static final StateCrossInference cinfer = new StateCrossInference();
	
	/* path control dependence flow finder */
	/**
	 * @param target
	 * @return the flow that controls the execution of the input target point
	 * @throws Exception
	 */
	private CirExecutionFlow find_control_flow_in_nul(CirExecution target) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			/* I. find the closest selective flow in decidable previous path */
			CirExecutionPath prev_path = StateMutations.oublock_prev_path(target);
			Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
			while(iterator.hasNext()) {
				CirExecutionFlow flow = iterator.next().get_flow();
				if(flow.get_type() == CirExecutionFlowType.true_flow
					|| flow.get_type() == CirExecutionFlowType.fals_flow
					|| flow.get_type() == CirExecutionFlowType.skip_flow
					|| flow.get_type() == CirExecutionFlowType.call_flow) {
					return flow;
				}
				else if(flow.get_type() == CirExecutionFlowType.retr_flow) {
					CirExecution wait = flow.get_target();
					CirExecution call = wait.get_graph().get_execution(wait.get_id() - 1);
					return call.get_ou_flow(0);
				}
				else { /* ignore the non-selective flow in decidable path */ }
			}
			
			/* II. otherwise, return the function entry output flow */
			CirExecution entry = target.get_graph().get_entry();
			return entry.get_ou_flow(0);
		}
	}
	/**
	 * @param context
	 * @param target
	 * @return the flow that controls the execution of the input target point
	 * @throws Exception
	 */
	private CirExecutionFlow find_control_flow_in_dpg(CDependGraph context, CirExecution target) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			CirInstanceGraph instance_graph = context.get_program_graph();
			if(instance_graph.has_instances_of(target)) {
				for(CirInstanceNode instance_node : instance_graph.get_instances_of(target)) {
					if(context.has_node(instance_node)) {
						CDependNode dependence_node = context.get_node(instance_node);
						for(CDependEdge dependence_edge : dependence_node.get_ou_edges()) {
							CDependType dependence_type = dependence_edge.get_type();
							if(dependence_type == CDependType.predicate_depend) {
								CDependPredicate element = (CDependPredicate) dependence_edge.get_element();
								CirExecution if_execution = dependence_edge.get_target().get_execution();
								for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
									if(element.get_predicate_value() && flow.get_type() == CirExecutionFlowType.true_flow) {
										return flow;
									}
									else if(!element.get_predicate_value() && flow.get_type() == CirExecutionFlowType.fals_flow) {
										return flow;
									}
								}
								throw new IllegalArgumentException("Failed to find flow: " + dependence_edge.get_target());
							}
							else if(dependence_type == CDependType.stmt_call_depend) {
								CirExecution call = dependence_edge.get_target().get_execution();
								return call.get_ou_flow(0);
							}
							else if(dependence_type == CDependType.stmt_exit_depend) {
								CirExecution wait = dependence_edge.get_target().get_execution();
								CirExecution call = wait.get_graph().get_execution(wait.get_id() - 1);
								return call.get_ou_flow(0);
							}
							else {
								/* not a control dependence edge */	continue;
							}
						}
					}
				}
			}
			return this.find_control_flow_in_nul(target);
		}
	}
	/**
	 * @param context
	 * @param target
	 * @return the flow that controls the execution of the input target point
	 * @throws Exception
	 */
	private CirExecutionFlow find_control_flow_in_exp(CirExecutionPath context, CirExecution target) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			/* I. accumulate the execution flows until the target */
			Stack<CirExecutionFlow> stack = new Stack<CirExecutionFlow>();
			boolean found = false;
			for(CirExecutionEdge edge : context.get_edges()) {
				stack.push(edge.get_flow());
				if(edge.get_target() == target) {
					found = true; break;
				}
			}
			
			/* II. unable to find the selective flows */
			if(!found) {
				return this.find_control_flow_in_nul(target);
			}
			/* III. to identify the selective flow in stack */
			else {
				while(!stack.isEmpty()) {
					CirExecutionFlow flow = stack.pop();
					if(flow.get_type() == CirExecutionFlowType.true_flow
							|| flow.get_type() == CirExecutionFlowType.fals_flow
							|| flow.get_type() == CirExecutionFlowType.skip_flow
							|| flow.get_type() == CirExecutionFlowType.call_flow) {
							return flow;
						}
						else if(flow.get_type() == CirExecutionFlowType.retr_flow) {
							CirExecution wait = flow.get_target();
							CirExecution call = wait.get_graph().get_execution(wait.get_id() - 1);
							return call.get_ou_flow(0);
						}
						else { /* ignore the non-selective flow in decidable path */ }
				}
				return this.find_control_flow_in_nul(target);
			}
		}
	}
	/**
	 * @param context
	 * @param target
	 * @return the flow that controls the execution of the input target point
	 * @throws Exception
	 */
	private CirExecutionFlow find_control_flow_in_stp(CStatePath context, CirExecution target) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			/* I. accumulate the execution flows until the target */
			Stack<CirExecutionFlow> stack = new Stack<CirExecutionFlow>();
			boolean found = false;
			for(CStateNode state_node : context.get_nodes()) {
				CirExecution execution = state_node.get_execution();
				if(execution.get_in_degree() == 1) {
					stack.push(execution.get_in_flow(0));
				}
				if(execution == target) { break; }
				if(execution.get_ou_degree() == 1) {
					stack.push(execution.get_ou_flow(0));
				}
			}
			
			/* II. unable to find the selective flows */
			if(!found) {
				return this.find_control_flow_in_nul(target);
			}
			/* III. to identify the selective flow in stack */
			else {
				while(!stack.isEmpty()) {
					CirExecutionFlow flow = stack.pop();
					if(flow.get_type() == CirExecutionFlowType.true_flow
							|| flow.get_type() == CirExecutionFlowType.fals_flow
							|| flow.get_type() == CirExecutionFlowType.skip_flow
							|| flow.get_type() == CirExecutionFlowType.call_flow) {
							return flow;
						}
						else if(flow.get_type() == CirExecutionFlowType.retr_flow) {
							CirExecution wait = flow.get_target();
							CirExecution call = wait.get_graph().get_execution(wait.get_id() - 1);
							return call.get_ou_flow(0);
						}
						else { /* ignore the non-selective flow in decidable path */ }
				}
				return this.find_control_flow_in_nul(target);
			}
		}
	}
	/**
	 * @param context	CDependGraph | CStatePath | CirExecutionPath | null
	 * @param target
	 * @return the flow that controls the execution of the input target point
	 * @throws Exception
	 */
	private CirExecutionFlow find_control_flow(Object context, CirExecution target) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(context == null) {
			return this.find_control_flow_in_nul(target);
		}
		else if(context instanceof CDependGraph) {
			return this.find_control_flow_in_dpg((CDependGraph) context, target);
		}
		else if(context instanceof CirExecutionPath) {
			return this.find_control_flow_in_exp((CirExecutionPath) context, target);
		}
		else if(context instanceof CStatePath) {
			return this.find_control_flow_in_stp((CStatePath) context, target);
		}
		else {
			return this.find_control_flow_in_nul(target);
		}
	}
	
	/* cross-statement */
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void cinf_reach_times(CirReachTimesState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		CirExecution execution = state.get_execution();
		int minimal_times = state.get_minimal_times();
		if(minimal_times == 1) {
			CirExecutionFlow flow = this.find_control_flow(context, execution);
			CirExecution source_execution = flow.get_source();
			
			CirStatement if_statement = flow.get_source().get_statement();
			CirExpression condition;
			if(if_statement instanceof CirIfStatement) {
				condition = ((CirIfStatement) if_statement).get_condition();
			}
			else if(if_statement instanceof CirCaseStatement) {
				condition = ((CirCaseStatement) if_statement).get_condition();
			}
			else { condition = null; }
			
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				outputs.add(CirAbstractState.eva_cond(source_execution, condition, true));
			}
			else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
				outputs.add(CirAbstractState.eva_cond(source_execution, condition, false));
			}
			else {
				outputs.add(CirAbstractState.cov_time(source_execution, 1));
			}
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void cinf_limit_times(CirLimitTimesState state, Collection<CirAbstractState> outputs, Object context) throws Exception { }
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void cinf_n_constrain(CirNConstrainState state, Collection<CirAbstractState> outputs, Object context) throws Exception { 
		CirExecution execution = state.get_execution();
		SymbolExpression condition = state.get_condition();
		if(condition instanceof SymbolConstant) {
			boolean value = ((SymbolConstant) condition).get_bool();
			if(!value) {
				execution = execution.get_graph().get_entry();
				outputs.add(CirAbstractState.eva_cond(execution, Boolean.FALSE, true));
			}
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void cinf_m_constrain(CirMConstrainState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		CirExecution execution = state.get_execution();
		SymbolExpression condition = state.get_condition();
		if(condition instanceof SymbolConstant) {
			boolean value = ((SymbolConstant) condition).get_bool();
			if(!value) {
				execution = execution.get_graph().get_entry();
				outputs.add(CirAbstractState.eva_cond(execution, Boolean.FALSE, true));
			}
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void cinf_block_error(CirBlockErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception { }
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void cinf_flows_error(CirFlowsErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception { }
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void cinf_traps_error(CirTrapsErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception { }
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void cinf_value_error(CirValueErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state.is_defined_point()) { /* TODO add definition point here */ }
		else { StateValueInference.value_infer(state, outputs); }
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void cinf_incre_error(CirIncreErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state.is_defined_point()) { /* TODO add definition point here */ }
		else { StateValueInference.value_infer(state, outputs); }
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void cinf_bixor_error(CirBixorErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state.is_defined_point()) { /* TODO add definition point here */ }
		else { StateValueInference.value_infer(state, outputs); }
	}
	
	/* interfaces */
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void cinf(CirAbstractState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(state instanceof CirLimitTimesState) {
			this.cinf_limit_times((CirLimitTimesState) state, outputs, context);
		}
		else if(state instanceof CirReachTimesState) {
			this.cinf_reach_times((CirReachTimesState) state, outputs, context);
		}
		else if(state instanceof CirNConstrainState) {
			this.cinf_n_constrain((CirNConstrainState) state, outputs, context);
		}
		else if(state instanceof CirMConstrainState) {
			this.cinf_m_constrain((CirMConstrainState) state, outputs, context);
		}
		else if(state instanceof CirBlockErrorState) {
			this.cinf_block_error((CirBlockErrorState) state, outputs, context);
		}
		else if(state instanceof CirFlowsErrorState) {
			this.cinf_flows_error((CirFlowsErrorState) state, outputs, context);
		}
		else if(state instanceof CirTrapsErrorState) {
			this.cinf_traps_error((CirTrapsErrorState) state, outputs, context);
		}
		else if(state instanceof CirValueErrorState) {
			this.cinf_value_error((CirValueErrorState) state, outputs, context);
		}
		else if(state instanceof CirIncreErrorState) {
			this.cinf_incre_error((CirIncreErrorState) state, outputs, context);
		}
		else if(state instanceof CirBixorErrorState) {
			this.cinf_bixor_error((CirBixorErrorState) state, outputs, context);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + state.toString());
		}
	}
	/**
	 * It generates the states directly subsumed by the input state under the given context
	 * @param state		the source state from which the subsumed states are inferred
	 * @param context	CDependGraph | CStatePath | CirExecutionPath | null
	 * @return			the set of states directly subsumed by the input state
	 * @throws Exception
	 */
	public static Collection<CirAbstractState> cross_subsume(CirAbstractState state, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else {
			state = state.normalize();
			Set<CirAbstractState> outputs = new HashSet<CirAbstractState>();
			cinfer.cinf(state, outputs, context);
			Set<CirAbstractState> noutput = new HashSet<CirAbstractState>();
			for(CirAbstractState output : outputs) {
				noutput.add(output.normalize());
			}
			return noutput;
		}
	}
	
}
