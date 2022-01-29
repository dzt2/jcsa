package com.jcsa.jcmutest.mutant.sta2mutant.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirBlockErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirFlowsErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirPathErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirTrapsErrorState;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

/**
 * It implemnts the inference of state subsumption between CirPathErrorState.
 * 
 * @author yuimula
 *
 */
final class StatePathInference {
	
	/* singleton mode */ /** constructor **/ private StatePathInference() { }
	private static final StatePathInference inf = new StatePathInference();
	
	/* inferring subsumption relation for path-errors states */
	/**
	 * set_stmt(exec, bool) --> cov_stmt(exec) | lim_stmt(exec)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void inf_block_error(CirBlockErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		if(execution.get_statement() instanceof CirTagStatement) {
			/* no impacts being subsumed by this execution (none) */
		}
		else if(state.is_original_executed()) {
			outputs.add(CirAbstractState.cov_time(execution, 1));
		}
		else { /* none subsumptions on coverage from not-executed */ }
	}
	/**
	 * set_flow(source, orig, muta) --> set_stmt(xxx,xxx) | cov_stmt(source)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void inf_flows_error(CirFlowsErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		/* 1. declarations and initializations */
		CirExecutionPath orig_path = StateMutations.oublock_post_path(state.get_orig_target());
		CirExecutionPath muta_path = StateMutations.oublock_post_path(state.get_muta_target());
		
		/* 2. collect the execution points being executed in both paths */
		Collection<CirExecution> orig_executions = new HashSet<CirExecution>();
		Collection<CirExecution> muta_executions = new HashSet<CirExecution>();
		for(CirExecutionEdge edge : orig_path.get_edges()) {
			orig_executions.add(edge.get_target());
		}
		for(CirExecutionEdge edge : muta_path.get_edges()) {
			muta_executions.add(edge.get_target());
		}
		orig_executions.add(orig_path.get_source());
		muta_executions.add(muta_path.get_source());
		
		/* 3. collect the commonly execution points between the paths */
		Collection<CirExecution> common_executions = new HashSet<CirExecution>();
		for(CirExecution execution : orig_executions) {
			if(muta_executions.contains(execution)) {
				common_executions.add(execution);
			}
		}
		orig_executions.removeAll(common_executions);
		muta_executions.removeAll(common_executions);
		
		/* 4. generate the block error states subsumed by flow errors */
		for(CirExecution execution : orig_executions) {
			if(!(execution.get_statement() instanceof CirTagStatement))
				outputs.add(CirAbstractState.set_stmt(execution, false));
		}
		for(CirExecution execution : muta_executions) {
			if(!(execution.get_statement() instanceof CirTagStatement))
				outputs.add(CirAbstractState.set_stmt(execution, true));
		}
		outputs.add(CirAbstractState.cov_time(state.get_source_execution(), 1));
	}
	/**
	 * trp_stmt(exec) --> cov_stmt(exec)
	 * @param state
	 * @param outputs
	 * @throws Exception
	 */
	private void inf_traps_error(CirTrapsErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		CirExecution execution = state.get_execution();
		if(!(execution.get_statement() instanceof CirEndStatement)) {
			outputs.add(CirAbstractState.set_trap(execution.get_graph().get_exit()));
		}
		outputs.add(CirAbstractState.cov_time(execution, 1));
	}
	/**
	 * It infers the states directly subsumed by the state into the outputs
	 * @param state		the state from which the subsumption are inferred
	 * @param outputs	to preserve the set of states subsumed by inputs
	 * @throws Exception
	 */
	private void inf(CirPathErrorState state, Collection<CirAbstractState> outputs) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(state instanceof CirBlockErrorState) {
			this.inf_block_error((CirBlockErrorState) state, outputs);
		}
		else if(state instanceof CirFlowsErrorState) {
			this.inf_flows_error((CirFlowsErrorState) state, outputs);
		}
		else if(state instanceof CirTrapsErrorState) {
			this.inf_traps_error((CirTrapsErrorState) state, outputs);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + state);
		}
	}
	/**
	 * It infers the states directly subsumed by the state into the outputs
	 *@param state		the state from which the subsumption are inferred
	 * @param outputs	to preserve the set of states subsumed by inputs
	 * @param context	will not be used in this inference machine
	 * @throws Exception
	 */
	protected static void infer(CirPathErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			Set<CirAbstractState> buffer = new HashSet<CirAbstractState>();
			inf.inf(state, buffer);
			for(CirAbstractState output : buffer) {
				outputs.add(output.normalize());
			}
		}
	}
	
}
