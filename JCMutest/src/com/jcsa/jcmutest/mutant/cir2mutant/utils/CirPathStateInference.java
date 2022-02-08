package com.jcsa.jcmutest.mutant.cir2mutant.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBlockErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirFlowsErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirPathErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirTrapsErrorState;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

/**
 * It implements the inference of subsume-analysis on path-error state.
 * 
 * @author yukimula
 *
 */
final class CirPathStateInference {
	
	/* singleton mode */ /** constructor **/ private CirPathStateInference() {}
	static final CirPathStateInference inference = new CirPathStateInference();
	
	/* subsume-analysis inference algorithms */
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void inf_blc_error(CirBlockErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
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
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void inf_flw_error(CirFlowsErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		/* 1. declarations and initializations */
		CirExecutionPath orig_path = CirMutations.oublock_post_path(state.get_orig_target());
		CirExecutionPath muta_path = CirMutations.oublock_post_path(state.get_muta_target());
		
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
				outputs.add(CirAbstractState.mut_stmt(execution, false));
		}
		for(CirExecution execution : muta_executions) {
			if(!(execution.get_statement() instanceof CirTagStatement))
				outputs.add(CirAbstractState.mut_stmt(execution, true));
		}
		outputs.add(CirAbstractState.cov_time(state.get_execution(), 1));
	}
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void inf_trp_error(CirTrapsErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception { }
	/**
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	private void inf(CirPathErrorState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(state instanceof CirBlockErrorState) {
			this.inf_blc_error((CirBlockErrorState) state, outputs, context);
		}
		else if(state instanceof CirFlowsErrorState) {
			this.inf_flw_error((CirFlowsErrorState) state, outputs, context);
		}
		else if(state instanceof CirTrapsErrorState) {
			this.inf_trp_error((CirTrapsErrorState) state, outputs, context);
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
			inference.inf(state, buffer, context);
			for(CirAbstractState output : buffer) {
				outputs.add(output.normalize());
			}
		}
	}
	
}
