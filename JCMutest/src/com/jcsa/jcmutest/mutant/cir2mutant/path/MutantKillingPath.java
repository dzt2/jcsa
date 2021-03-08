package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;

/**
 * It describes an execution path annotated with symbolic conditions required to be satisfied, such that
 * the target mutant in the path can be killed and cause observable failures in final outputs.<br>
 * <br>
 * 
 * The model of a killing-path for mutation testing is described to include following attributes as:<br>
 * 
 * 	(1)	execution_path: CirExecutionPath that describes the (virtual) execution path from program entry
 * 		to the mutated statement, where the target mutant is injected.<br>
 * 	(2)	target_mutant: the target mutation as test objective for being killed during testing.<br>
 * 	
 * 	(3)	For each execution edge in the path, the MutantKillingState is annotated in the CirExecutionEdge
 * 		such that the instances (constraint or state-error) along with annotations can be recored.	
 * 
 * @author yukimula
 *
 */
public class MutantKillingPath {
	
	/* definitions */
	/** the execution path on which the mutant is killed **/
	private CirExecutionPath execution_path;
	/** the target mutant as test objective for being killed **/
	private Mutant target_mutant;
	
	/* constructor */
	protected MutantKillingPath(CirExecutionPath execution_path, Mutant target_mutant) throws Exception {
		if(execution_path == null)
			throw new IllegalArgumentException("Invalid execution_path: null");
		else if(target_mutant == null)
			throw new IllegalArgumentException("Invalid target_mutant as null");
		else {
			this.execution_path = execution_path;
			this.target_mutant = target_mutant;
			for(CirExecutionEdge edge : execution_path.get_edges()) {
				edge.set_annotation(new MutantKillingState(this, edge));
			}
		}
	}
	/**
	 * @param mutant
	 * @param location
	 * @param sym_path
	 * @param dependence_graph
	 * @return the set of mutant killing paths for killing target mutant in specified location
	 * 		   with a specified series of abstract constraints in symbolic path via dependence
	 * 		   analysis as provided.
	 * @throws Exception
	 */
	public static Iterable<MutantKillingPath> killing_paths(Mutant mutant, CirExecution location,
			List<SymInstanceEdge> sym_path, CDependGraph dependence_graph, 
			CirMutations cir_mutations) throws Exception {
		MutantKillingBuilder.builder.cir_mutations = cir_mutations;
		return MutantKillingBuilder.muta_paths(mutant, location, sym_path, dependence_graph);
	}
	
	/* getters */
	public CirExecutionPath get_execution_path() { return this.execution_path; }
	public Mutant get_target_mutant() { return this.target_mutant; }
	public int length() { return this.execution_path.length(); }
	public Iterable<CirExecutionEdge> get_execution_edges() { return this.execution_path.get_edges(); }
	/**
	 * @param k
	 * @return abstract state w.r.t. the kth edge in the path
	 * @throws Exception
	 */
	public MutantKillingState get_state(int k) throws Exception {
		return (MutantKillingState) this.execution_path.get_edge(k).get_annotation();
	}
	
	/* building methods */
	
	
	
}
