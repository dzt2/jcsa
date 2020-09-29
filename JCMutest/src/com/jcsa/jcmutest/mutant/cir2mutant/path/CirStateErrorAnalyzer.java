package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.state.CStateContexts;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

public class CirStateErrorAnalyzer {
	
	/* definitions */
	/** it provides contextual information to optimize **/
	private CStateContexts contexts;
	/** used to create constraint and state errors in the analyzer **/
	private CirMutations cir_mutations;
	/** mapping from statement to the source mutations among it **/
	private Map<CirStatement, Collection<CirMutation>> source_mutations;
	/** mapping from source mutation to those extended from it without
	 *  using any contextual information **/
	private Map<CirMutation, Collection<CirMutation>> abstract_mutations;
	/** mapping from source mutation to those extended from it by using
	 *  contextual information during testing **/
	private Map<CirMutation, List<Collection<CirMutation>>> concrete_mutations;
	
	/* constructor & singleton */
	private CirStateErrorAnalyzer(CirMutations cir_mutations) throws Exception {
		this.cir_mutations = new CirMutations(cir_mutations.get_cir_tree());
		this.source_mutations = new HashMap<CirStatement, Collection<CirMutation>>();
		this.abstract_mutations = new HashMap<CirMutation, Collection<CirMutation>>();
		this.concrete_mutations = new HashMap<CirMutation, List<Collection<CirMutation>>>();
		
		/* record the source mutation with the statement it belongs to */
		for(CirMutation source_mutation : cir_mutations.get_mutations()) {
			CirStatement statement = source_mutation.get_statement();
			if(!this.source_mutations.containsKey(statement)) {
				this.source_mutations.put(statement, new ArrayList<CirMutation>());
			}
			this.source_mutations.get(statement).add(source_mutation);
			
			Collection<CirMutation> all_mutations = CirLocalPropagation.
						local_propagate(this.cir_mutations, source_mutation);
			all_mutations = new ArrayList<CirMutation>(all_mutations);
			this.abstract_mutations.put(source_mutation, all_mutations);
			this.concrete_mutations.put(source_mutation, new ArrayList<Collection<CirMutation>>());
		}
	}
	
	/* state update iteration */
	/**
	 * @param mutations
	 * @return the concrete mutations optimized from the input ones using current contexts
	 * @throws Exception
	 */
	private Collection<CirMutation> optimize_all(Iterable<CirMutation> mutations) throws Exception {
		List<CirMutation> results = new ArrayList<CirMutation>();
		for(CirMutation mutation : mutations) {
			results.add(this.cir_mutations.optimize(mutation, contexts));
		}
		return results;
	}
	/**
	 * generate the concrete versions of the abstract mutations propagated from the source
	 * and append them into the concrete maps
	 * @param source_mutation
	 * @throws Exception
	 */
	private void generate_concrete_mutations(CirMutation source_mutation) throws Exception {
		Collection<CirMutation> abs_mutations = this.abstract_mutations.get(source_mutation);
		Collection<CirMutation> con_mutations = this.optimize_all(abs_mutations);
		this.concrete_mutations.get(source_mutation).add(con_mutations);
	}
	/**
	 * proceed one state node in path
	 * @param state_node
	 * @throws Exception
	 */
	private void update_one(CStateNode state_node) throws Exception {
		System.out.print("\t\tStart from " + state_node.get_execution() + " ");
		this.contexts.accumulate(state_node);
		System.out.print("until ");
		CirStatement statement = state_node.get_statement();
		if(this.source_mutations.containsKey(statement)) {
			Collection<CirMutation> local_mutations = this.source_mutations.get(statement);
			for(CirMutation local_mutation : local_mutations) {
				this.generate_concrete_mutations(local_mutation);
			}
		}
		System.out.println("FINISHED.");
	}
	/**
	 * construct the concrete state mutations in testing w.r.t. state path
	 * @param state_path
	 * @throws Exception
	 */
	private void update_all(CStatePath state_path) throws Exception {
		if(state_path == null) {
			this.contexts = null;
			for(CirStatement statement : this.source_mutations.keySet()) {
				Collection<CirMutation> mutations = this.source_mutations.get(statement);
				for(CirMutation source_mutation : mutations) {
					this.generate_concrete_mutations(source_mutation);
				}
			}
		}
		else {
			this.contexts = new CStateContexts();
			for(CStateNode state_node : state_path.get_nodes()) {
				this.update_one(state_node);
			}
			this.contexts = null;
		}
	}
	
	/* API */
	/**
	 * @param cir_mutations
	 * @param state_path
	 * @return Perform path analysis with contextual information provided by state path
	 * @throws Exception
	 */
	public static Map<CirMutation, List<Collection<CirMutation>>> analyze(
			CirMutations cir_mutations, CStatePath state_path) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else {
			CirStateErrorAnalyzer analyzer = new CirStateErrorAnalyzer(cir_mutations);
			analyzer.update_all(state_path);
			return analyzer.concrete_mutations;
		}
	}
	/**
	 * @param cir_mutations
	 * @param state_path
	 * @return Perform path analysis with contextual information provided by state path
	 * @throws Exception
	 */
	public static Map<CirMutation, List<Collection<CirMutation>>> analyze(
			CirMutations cir_mutations) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else {
			CirStateErrorAnalyzer analyzer = new CirStateErrorAnalyzer(cir_mutations);
			analyzer.update_all(null);
			return analyzer.concrete_mutations;
		}
	}
	
}
