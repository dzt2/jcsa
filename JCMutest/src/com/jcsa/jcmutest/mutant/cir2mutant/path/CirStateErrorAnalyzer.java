package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
	/** used to create constraint and state errors **/
	private CirMutations cir_mutations;
	/** mapping from statement to the mutations among it **/
	private Map<CirStatement, Collection<CirMutation>> index;
	/** mapping from mutation to those generated from it
	 * 	including generated by optimize or propagate_in
	 *  using the contextual information at the statement **/
	private Map<CirMutation, Collection<CirMutation>> errors;
	
	/* constructor */
	/**
	 * prepare an analyzer for parsing the state errors for mutations
	 * created from the input library, in which this.cir_mutatinos is
	 * independence from the input cir_mutations.
	 * @param cir_mutations
	 * @throws Exception
	 */
	private CirStateErrorAnalyzer(CirMutations cir_mutations) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations");
		else {
			this.cir_mutations = new CirMutations(cir_mutations.get_cir_tree());
			this.index = new HashMap<CirStatement, Collection<CirMutation>>();
			this.errors = new HashMap<CirMutation, Collection<CirMutation>>();
			for(CirMutation cir_mutation : cir_mutations.get_mutations()) {
				CirStatement statement = cir_mutation.get_statement();
				if(!this.index.containsKey(statement)) {
					this.index.put(statement, new ArrayList<CirMutation>());
				}
				this.index.get(statement).add(cir_mutation);
				this.errors.put(cir_mutation, new HashSet<CirMutation>());
			}
		}
	}
	
	/* state update iteration */
	private void generate_errors(CirMutation mutation) throws Exception {
		Collection<CirMutation> err_mutations = 
				this.cir_mutations.propagate_in(mutation, contexts);
		this.errors.get(mutation).addAll(err_mutations);
	}
	private void update(CStateNode state_node) throws Exception {
		System.out.print("\t\tStart from " + state_node.get_execution() + " ");
		this.contexts.accumulate(state_node);
		System.out.print("until ");
		CirStatement statement = state_node.get_statement();
		if(this.index.containsKey(statement)) {
			Collection<CirMutation> local_mutations = this.index.get(statement);
			for(CirMutation local_mutation : local_mutations) {
				this.generate_errors(local_mutation);
			}
		}
		System.out.println("FINISHED.");
	}
	private void update_all(CStatePath state_path) throws Exception {
		this.contexts = new CStateContexts();
		for(CStateNode state_node : state_path.get_nodes()) {
			this.update(state_node);
		}
		this.contexts = null;
	}
	
	/* public API approach */
	/**
	 * @param cir_mutations
	 * @return the errors generated from each mutation in input library
	 * @throws Exception
	 */
	public static Map<CirMutation, Collection<CirMutation>> solve_errors(
			CirMutations cir_mutations) throws Exception {
		CirStateErrorAnalyzer analyzer = new CirStateErrorAnalyzer(cir_mutations);
		analyzer.contexts = null;
		for(CirMutation cir_mutation : analyzer.errors.keySet()) {
			analyzer.generate_errors(cir_mutation);
		}
		return analyzer.errors;
	}
	/**
	 * @param cir_mutations
	 * @param state_path
	 * @return the errors generated from each mutation in input library under dynamic analysis
	 * @throws Exception
	 */
	public static Map<CirMutation, Collection<CirMutation>> solve_errors(
			CirMutations cir_mutations, CStatePath state_path) throws Exception {
		if(state_path == null) {
			return solve_errors(cir_mutations);
		}
		else {
			CirStateErrorAnalyzer analyzer = new CirStateErrorAnalyzer(cir_mutations);
			analyzer.update_all(state_path);
			return analyzer.errors;
		}
	}
	
}