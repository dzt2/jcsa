package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstance;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;

/**
 * Collection of symbolic instance states defined and evaluated at a point of program during testing 
 * (with respect to an edge in path), which is established as the annotation of CirExecutionEdge for 
 * analysis.
 * 
 * @author yukimula
 *
 */
public class SymInstanceStates {
	
	/* definitions */
	/** collection of states of unique symbolic instance evaluated **/
	private List<SymInstanceState> states;
	
	/* constructor */
	/**
	 * create an empty collection of symbolic instance state defined
	 */
	protected SymInstanceStates() {
		this.states = new ArrayList<SymInstanceState>();
	}
	
	/* getters */
	/**
	 * @return collection of states of unique symbolic instance evaluated
	 */
	public Iterable<SymInstanceState> get_states() { return this.states; }
	
	protected Iterable<SymInstanceState> get_states_copy() { 
		List<SymInstanceState> copy = new ArrayList<SymInstanceState>();
		for(SymInstanceState state : this.states) copy.add(state);
		return copy;
	}
	/**
	 * @return the number of states of unique symbolic instance evaluated
	 */
	public int size() { return this.states.size(); }
	/**
	 * @param instance
	 * @return the unique state w.r.t. input symbolic instance or newly created one if it does not exist
	 * @throws Exception
	 */
	protected SymInstanceState new_state(SymInstance instance) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			for(SymInstanceState state : this.states) {
				if(state.get_instance() == instance) {
					return state;
				}
			}
			
			SymInstanceState state = new SymInstanceState(instance);
			this.states.add(state);
			return state;
		}
	}
	
	/* setters */
	/**
	 * remove all the states from the collection
	 */
	protected void clear() { this.states.clear(); }
	/**
	 * reset states defined in this collection
	 */
	protected void reset() {
		for(SymInstanceState state : this.states) {
			state.reset();
		}
	}
	/**
	 * evaluate all the states defined in the collection
	 * @param cir_mutations
	 * @param contexts
	 * @throws Exception
	 */
	protected void evaluate(CirMutations cir_mutations, SymbolStateContexts contexts) throws Exception {
		for(SymInstanceState state : this.states) {
			state.evaluate(cir_mutations, contexts);
		}
	}
	
}
