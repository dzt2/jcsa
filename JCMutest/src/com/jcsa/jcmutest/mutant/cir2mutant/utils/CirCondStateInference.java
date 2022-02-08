package com.jcsa.jcmutest.mutant.cir2mutant.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConstraintState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirCoverTimesState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirSyMutationState;
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
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It implements the inference of subsume-analysis for CirConditionState
 * @author yukimula
 *
 */
final class CirCondStateInference {
	
	/* singleton mode */ /** constructor **/ private CirCondStateInference() {}
	static final CirCondStateInference inference = new CirCondStateInference();
	
	/* basic methods to support logical-based subsumption inference */
	/**
	 * It determines the next execution times smaller than the given limit
	 * @param max_times maximal/minimal times for statement being executed
	 * @return			the times smaller than the given limit or 1 as top
	 */
	private int get_smaller_maximal_times(int max_times) {
		int times = 1;
		while(times < max_times) { times = times * 2; }
		return times / 2;
	}
	/**
	 * It recursively derives the sub_conditions in the expression when taking
	 * the expression as a logical conjunctive form.
	 * @param expression		the expression in which conditions are derived
	 * @param sub_conditions	to preserve the sets of sub_conditions derived
	 * @throws Exception
	 */
	private void derive_conditions_in_conjunction(SymbolExpression expression,
				Collection<SymbolExpression> sub_conditions) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(sub_conditions == null) {
			throw new IllegalArgumentException("Invalid sub_conditions: null");
		}
		else if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				/* True operand is ignored from the conjunctive expression */
			}
			else {		
				sub_conditions.clear();		/* clear and take only a False */
				sub_conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.logic_and) {	/* logical conjunctive */
				this.derive_conditions_in_conjunction(loperand, sub_conditions);
				this.derive_conditions_in_conjunction(roperand, sub_conditions);
			}
			else {				/* a normal condition is directly appended */
				sub_conditions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
		else {					/* a normal condition is directly appended */
			sub_conditions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * It recursively derives the conditions subsumed by the expression
	 * @param expression		the expression from which the subsumed conditions are derived
	 * @param sub_conditions	to preserve set of conditions directly subsumed by expression
	 * @throws Exception
	 */
	private void derive_subsummed_conditions_from(SymbolExpression expression,
				Collection<SymbolExpression> sub_conditions) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(sub_conditions == null) {
			throw new IllegalArgumentException("Invalid sub_conditions: null");
		}
		else if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				/* TRUE condition does not subsume any other conditions */
			}
			else {
				sub_conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.logic_and) {
				this.derive_conditions_in_conjunction(expression, sub_conditions);
			}
			else if(operator == COperator.equal_with) {
				sub_conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				sub_conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
			}
			else if(operator == COperator.greater_tn) {
				sub_conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				sub_conditions.add(SymbolFactory.not_equals(loperand, roperand));
			}
			else if(operator == COperator.smaller_tn) {
				sub_conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
				sub_conditions.add(SymbolFactory.not_equals(loperand, roperand));
			}
			else {
				sub_conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
		}
		else {
			sub_conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
		}
	}
	/**
	 * @param expression
	 * @return the set of conditions directly subsumed by the input expression
	 * @throws Exception
	 */
	private Collection<SymbolExpression> derive_subsummed_conditions(SymbolExpression expression) throws Exception {
		Set<SymbolExpression> sub_conditions = new HashSet<SymbolExpression>();
		this.derive_subsummed_conditions_from(expression, sub_conditions);
		return sub_conditions;
	}
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
			CirExecutionPath prev_path = CirMutations.oublock_prev_path(target);
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
			
			/* II. otherwise, return the calling-flows */ 
			CirExecution entry = target.get_graph().get_entry();
			if(entry.get_in_degree() == 1) {
				return entry.get_in_flow(0);
			}
			else {
				return null;
			}
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
	
	/* inference methods */
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void inf_cov_times(CirCoverTimesState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state.is_reach_coverage()) {
			/* 1. determine the execution point and reachability times */
			CirExecution execution = state.get_execution();
			int times = this.get_smaller_maximal_times(state.get_executed_times());
			
			/* 2. in case that times >= 1, resort to the smaller times */
			if(times >= 1) {
				outputs.add(CirAbstractState.cov_time(execution, times));
			}
			/* 3. otherwise, it needs to connect to the prior branch flow */
			else {
				/* 3.1. derive the prior execution flow and extract source  */
				CirExecutionFlow prior_flow = this.find_control_flow(context, execution);
				if(prior_flow == null) { return; /* none more prior flow depended on */ }
				
				/* 3.2. derive the conditional statement and its predicate */
				CirStatement source_statement = prior_flow.get_source().get_statement();
				CirExpression condition;
				if(source_statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) source_statement).get_condition();
				}
				else if(source_statement instanceof CirCaseStatement) {
					condition = ((CirCaseStatement) source_statement).get_condition();
				}
				else {
					condition = null;
				}
				execution = source_statement.execution_of();
				
				/* 3.3. determine the next subsumed states using flow-type */
				if(prior_flow.get_type() == CirExecutionFlowType.true_flow) {
					outputs.add(CirAbstractState.eva_need(execution, condition));
				}
				else if(prior_flow.get_type() == CirExecutionFlowType.fals_flow) {
					outputs.add(CirAbstractState.eva_need(execution, SymbolFactory.sym_condition(condition, false)));
				}
				else {
					outputs.add(CirAbstractState.cov_time(execution, 1));
				}
			}
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void inf_constrain(CirConstraintState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		/* 1. declarations and subsumed conditions getter */
		CirExecution execution = state.get_execution();
		Collection<SymbolExpression> subsumed_conditions = this.
				derive_subsummed_conditions(state.get_condition());
		
		/* 2. in case that the constraint must be satisfied */
		if(state.is_must_constrain()) {
			for(SymbolExpression condition : subsumed_conditions) {
				outputs.add(CirAbstractState.eva_must(execution, condition));
			}
			outputs.add(CirAbstractState.eva_need(execution, state.get_condition()));
		}
		/* 3. in case that the constraint needs to be achieved */
		else {
			if(subsumed_conditions.isEmpty()) {
				outputs.add(CirAbstractState.cov_time(execution, 1));
			}
			else {
				for(SymbolExpression condition : subsumed_conditions) {
					outputs.add(CirAbstractState.eva_need(execution, condition));
				}
			}
			
			SymbolExpression condition = state.get_condition();
			if(condition instanceof SymbolConstant) {
				boolean value = ((SymbolConstant) condition).get_bool();
				if(!value) {
					execution = execution.get_graph().get_entry();
					outputs.add(CirAbstractState.eva_need(execution, Boolean.FALSE));
				}
			}
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void inf_sy_mutant(CirSyMutationState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		outputs.add(CirAbstractState.cov_time(state.get_execution(), 1));
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void inf(CirConditionState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(state instanceof CirCoverTimesState) {
			this.inf_cov_times((CirCoverTimesState) state, outputs, context);
		}
		else if(state instanceof CirConstraintState) {
			this.inf_constrain((CirConstraintState) state, outputs, context);
		}
		else if(state instanceof CirSyMutationState) {
			this.inf_sy_mutant((CirSyMutationState) state, outputs, context);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + state);
		}
	}
	/**
	 * It infers the states directly subsumed by the input state as conditional
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	protected static void infer(CirConditionState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			Set<CirAbstractState> buffer = new HashSet<CirAbstractState>();
			inference.inf(state, buffer, context);
			for(CirAbstractState output : buffer) {
				outputs.add(output.normalize());
			}
		}
	}
	
}
