package com.jcsa.jcmutest.mutant.sta2mutant.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It infers the direct subsumed abstract states by a given state any way.
 * 
 * @author yukimula
 *
 */
public class CirStateSubsumer {
	
	/* singleton mode */ /** constructor **/ private CirStateSubsumer() { }
	private static final CirStateSubsumer subsumer = new CirStateSubsumer();
	
	/* local static subsumption inference */
	/**
	 * It implements the local inference of static subsumption from source to a (set of) target state(s) or empty 
	 * if no more states in local execution are directly subsumed by this one.
	 * 
	 * @param input_state	the input state to be analyzed to obtain its subsuming set
	 * @param output_states	the collection to preserve the states directly subsumed by the input state
	 * @throws Exception
	 */
	private void linf_iter(CirAbstractState input_state, Collection<CirAbstractState> output_states) throws Exception {
		if(input_state == null) {
			throw new IllegalArgumentException("Invalid input_state: null");
		}
		else if(output_states == null) {
			throw new IllegalArgumentException("Invalid output_states: null");
		}
		else {
			/* initialization */
			output_states.clear(); input_state = input_state.normalize();
			
			/* syntax-directed inference */
			if(input_state instanceof CirReachTimesState) {
				this.linf_reach_times((CirReachTimesState) input_state, output_states);
			}
			else if(input_state instanceof CirLimitTimesState) {
				this.linf_limit_times((CirLimitTimesState) input_state, output_states);
			}
			else if(input_state instanceof CirNConstrainState) {
				this.linf_n_constrain((CirNConstrainState) input_state, output_states);
			}
			else if(input_state instanceof CirMConstrainState) {
				this.linf_m_constrain((CirMConstrainState) input_state, output_states);
			}
			else if(input_state instanceof CirBlockErrorState) {
				this.linf_block_error((CirBlockErrorState) input_state, output_states);
			}
			else if(input_state instanceof CirFlowsErrorState) {
				this.linf_flows_error((CirFlowsErrorState) input_state, output_states);
			}
			else if(input_state instanceof CirTrapsErrorState) {
				this.linf_traps_error((CirTrapsErrorState) input_state, output_states);
			}
			else if(input_state instanceof CirValueErrorState) {
				
			}
			else if(input_state instanceof CirIncreErrorState) {
				
			}
			else if(input_state instanceof CirBixorErrorState) {
				
			}
			else {
				throw new IllegalArgumentException("Invalid: " + input_state);
			}
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void linf_reach_times(CirReachTimesState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		int limits = state.get_minimal_times(), times = 1;
		while(times < limits) { times = times * 2; }
		times = times / 2;
		if(times <= 1) {
			outputs.add(CirAbstractState.eva_cond(execution, Boolean.TRUE, true));
		}
		else {
			outputs.add(CirAbstractState.cov_time(execution, times));
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void linf_limit_times(CirLimitTimesState state, Collection<CirAbstractState> outputs) throws Exception { }
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void linf_m_constrain(CirMConstrainState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		SymbolExpression condition = state.get_condition();
		outputs.add(CirAbstractState.eva_cond(execution, condition, true));
	}
	/**
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private void find_subsumed_conditions(SymbolExpression expression, Collection<SymbolExpression> conditions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				/* none of condition is subsumed by TRUE */
			}
			else {
				conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			
			if(operator == COperator.logic_and) {
				this.find_subsumed_conditions(loperand, conditions);
				this.find_subsumed_conditions(roperand, conditions);
			}
			else if(operator == COperator.greater_tn) {
				conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				conditions.add(SymbolFactory.not_equals(loperand, roperand));
			}
			else if(operator == COperator.smaller_tn) {
				conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
				conditions.add(SymbolFactory.not_equals(loperand, roperand));
			}
			else if(operator == COperator.equal_with) {
				conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
			}
			else {
				conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
		}
		else {
			conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void linf_n_constrain(CirNConstrainState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		SymbolExpression condition = state.get_condition();
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.find_subsumed_conditions(condition, conditions);
		for(SymbolExpression sub_condition : conditions) {
			outputs.add(CirAbstractState.eva_cond(execution, sub_condition, true));
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void linf_block_error(CirBlockErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		if(state.is_original_executed())
			outputs.add(CirAbstractState.cov_time(execution, 1));
		else 
			outputs.add(CirAbstractState.lim_time(execution, 0));
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void linf_flows_error(CirFlowsErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecutionPath orig_path = CirExecutionPathFinder.finder.df_extend(state.get_orig_target());
		CirExecutionPath muta_path = CirExecutionPathFinder.finder.df_extend(state.get_muta_target());
		Collection<CirExecution> orig_executions = new HashSet<CirExecution>();
		Collection<CirExecution> muta_executions = new HashSet<CirExecution>();
		for(CirExecutionEdge edge : orig_path.get_edges()) orig_executions.add(edge.get_target());
		for(CirExecutionEdge edge : muta_path.get_edges()) muta_executions.add(edge.get_target());
		orig_executions.add(orig_path.get_source()); muta_executions.add(muta_path.get_source());
		
		Collection<CirExecution> common_executions = new HashSet<CirExecution>();
		for(CirExecution execution : orig_executions) {
			if(muta_executions.contains(execution)) {
				common_executions.add(execution);
			}
		}
		orig_executions.removeAll(common_executions);
		muta_executions.removeAll(common_executions);
		
		for(CirExecution execution : orig_executions) {
			if(execution.get_statement() instanceof CirTagStatement) {
				continue;
			}
			else {
				outputs.add(CirAbstractState.set_stmt(execution, false));
			}
		}
		for(CirExecution execution : muta_executions) {
			if(execution.get_statement() instanceof CirTagStatement) {
				continue;
			}
			else {
				outputs.add(CirAbstractState.set_stmt(execution, true));
			}
		}
	}
	/**
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void linf_traps_error(CirTrapsErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		outputs.add(CirAbstractState.cov_time(state.get_execution(), 1));
	}
	
	
}
