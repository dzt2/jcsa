package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstance;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;

/**
 * It maintains the symbolic instance states annotated with the edges in a given execution path during testing.
 * 
 * @author yukimula
 *
 */
public class SymInstancePath {
	
	/* definitions */
	/** the execution path on which the symbolic analysis is done **/
	private CirExecutionPath execution_path;
	/** it is used to create concrete symbolic state in evaluation **/
	private CirMutations cir_mutations;
	/** mapping from symbolic instance to the unique global state it corresponds **/
	private Map<SymInstance, SymInstanceState> accumulate_states;
	/** mapping from each edge in execution path to unique local state it refers **/
	private Map<CirExecutionEdge, SymInstanceStates> local_states;
	
	/* constructor */
	/**
	 * create a execution path annotated with symbolic instances and their states
	 * @param path
	 * @param cir_mutations
	 * @throws Exception
	 */
	protected SymInstancePath(CirExecutionPath path, CirMutations cir_mutations) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations");
		else {
			this.execution_path = path;
			this.cir_mutations = cir_mutations;
			this.accumulate_states = new HashMap<SymInstance, SymInstanceState>();
			this.local_states = new HashMap<CirExecutionEdge, SymInstanceStates>();
		}
	}
	
	/* getters */
	/**
	 * @return it is used to create concrete symbolic state in evaluation
	 */
	public CirMutations get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the execution path on which the symbolic analysis is done
	 */
	public CirExecutionPath get_execution_path() { return this.execution_path; }
	/**
	 * @return the edges defined in execution path on which the symbolic analysis is done
	 */
	public Iterable<CirExecutionEdge> get_execution_edges() { return this.execution_path.get_edges(); }
	/**
	 * @param k
	 * @return the kth edge defined in execution path on which the symbolic analysis is done
	 * @throws IndexOutOfBoundsException
	 */
	public CirExecutionEdge get_execution_edge(int k) throws IndexOutOfBoundsException { return this.execution_path.get_edge(k); }
	/**
	 * @return the number of edges in execution path on which the symbolic analysis is done
	 */
	public int get_execution_length() { return this.execution_path.length(); }
	/**
	 * @param instance
	 * @return the global state w.r.t. symbolic instance during testing
	 * @throws Exception
	 */
	public SymInstanceState get_state(SymInstance instance) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			if(!this.accumulate_states.containsKey(instance)) {
				this.accumulate_states.put(instance, new SymInstanceState(instance));
			}
			return this.accumulate_states.get(instance);
		}
	}
	/**
	 * @param edge
	 * @return the set of symbolic instancec states evaluated and defined in given edge
	 * @throws Exception
	 */
	public SymInstanceStates get_state(CirExecutionEdge edge) throws Exception {
		if(edge == null || edge.get_path() != this.execution_path)
			throw new IllegalArgumentException("Invalid edge as null");
		else {
			if(!this.local_states.containsKey(edge)) {
				this.local_states.put(edge, new SymInstanceStates());
			}
			return this.local_states.get(edge);
		}
	}
	/**
	 * @param edge
	 * @param instance
	 * @return create a local state (along with its global version) in given edge
	 * @throws Exception
	 */
	public SymInstanceState get_state(CirExecutionEdge edge, SymInstance instance) throws Exception {
		if(edge == null || edge.get_path() != this.execution_path)
			throw new IllegalArgumentException("Invalid edge as null");
		else if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			this.get_state(instance);
			return this.get_state(edge).new_state(instance);
		}
	}
	
	/* setters */
	/**
	 * evaluate and update the states of instances defined in given edge and their global version 
	 * with the information provided in contexts as given.
	 * @param edge
	 * @param contexts 
	 * @throws Exception
	 */
	protected void evaluate(CirExecutionEdge edge, SymbolStateContexts contexts) throws Exception {
		if(edge == null || edge.get_path() != this.execution_path)
			throw new IllegalArgumentException("Invalid edge: null");
		else {
			SymInstanceStates edge_states = this.get_state(edge);
			edge_states.evaluate(this.cir_mutations, contexts);
			for(SymInstanceState edge_state : edge_states.get_states()) {
				this.get_state(edge_state.get_instance()).evaluate(this.cir_mutations, contexts);
			}
		}
	}
	
}
