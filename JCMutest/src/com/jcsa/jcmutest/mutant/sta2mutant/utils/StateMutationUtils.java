package com.jcsa.jcmutest.mutant.sta2mutant.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirDataErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirPathErrorState;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It provides the interfaces to operate on abstract execution states.
 * 
 * @author yukimula
 *
 */
public class StateMutationUtils {
	
	/**
	 * @param state		the state being normalized under the context
	 * @param context	the context in which the state is normalized
	 * @return			normalized version of the state in a context
	 * @throws Exception
	 */
	public static CirAbstractState normalize(CirAbstractState state, SymbolProcess context) throws Exception {
		return StateNormalization.normalize(state, context);
	}
	
	/**
	 * @param state		the state being evaluated under the context
	 * @param context	the context in which the state is evaluated
	 * @return			True (satisfied) False (non-satisfied) null (unknown)
	 * @throws Exception
	 */
	public static Boolean evaluate(CirAbstractState state, SymbolProcess context) throws Exception {
		return StateNormalization.evaluate(state, context);
	}
	
	/**
	 * It appends the set of abstract states subsumed by the input to the output set
	 * @param state		the state, from which the subsumption relations are inferred
	 * @param outputs	to preserve the subsumed states by the input during analysis
	 * @param context	CDependGraph | CirExecutionPath | CStatePath | otherwise
	 * @throws Exception
	 */
	public static void subsume(CirAbstractState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			state = state.normalize();
			if(state instanceof CirConditionState) {
				StateCondInference.infer((CirConditionState) state, outputs, context);
			}
			else if(state instanceof CirDataErrorState) {
				StateDataInference.infer((CirDataErrorState) state, outputs, context);
			}
			else if(state instanceof CirPathErrorState) {
				StatePathInference.infer((CirPathErrorState) state, outputs, context);
			}
			else {
				throw new IllegalArgumentException("Invalid: " + state);
			}
		}
	}
	
	/**
	 * It extends the state to the locally subsumed states using the static inference
	 * @param state		the state from which the states will be extended from inputs
	 * @param outputs	to preserve the states being extended from the input state
	 * @param loc_all	True {once} False {until fix-point algorithm}
	 * @throws Exception
	 */
	public static void extend(CirAbstractState state, Collection<CirAbstractState> outputs, boolean loc_all) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else if(loc_all) {
			StateAbstExtension.extend(state, outputs);
		}
		else {
			Queue<CirAbstractState> queue = new LinkedList<CirAbstractState>();
			Set<CirAbstractState> buffer = new HashSet<CirAbstractState>();
			queue.add(state.normalize());
			while(!queue.isEmpty()) {
				CirAbstractState parent = queue.poll();
				if(!outputs.contains(parent)) {
					outputs.add(parent);
					StateAbstExtension.extend(parent, buffer);
					for(CirAbstractState output : buffer) {
						queue.add(output);
					}
				}
			}
		}
	}
	
}
