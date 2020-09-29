package com.jcsa.jcmutest.mutant.cir2mutant.struct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcparse.flwa.dominate.CDominanceGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.state.CStateContexts;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It is used to detect the cir-mutation error propagation via CirMutationGraph
 * generated from one single cir-mutation of some mutant.
 * 
 * @author yukimula
 *
 */
public class CirMutationDetector {
	
	/** the target mutation is not reached during testing **/
	public static final char UnreachedMutation = 'r';
	/** the current state cannot satisfy the target constraint **/
	public static final char InvalidConstraint = 'c';
	/** the current state cannot infect the target state error **/
	public static final char InvalidStateError = 'e';
	/** the current state can satisfy the constraint and infect
	 * 	a target state error as given in the target mutation. **/
	public static final char ObservedMutation = 'p';
	
	/** the graph of which mutations will be analyzed **/
	private CirMutationGraph mutation_graph;
	/** mapping from statement to the nodes in that statement,
	 * 	which inform the system to analyze these nodes when the
	 * 	state flow reaches the target statement in testing. **/
	private Map<CirStatement, Collection<CirMutationNode>> nodes;
	/** mapping from each node under analysis  **/
	private Map<CirMutationNode, List<Character>> execute_records;
	
	/* constructor */
	private CirMutationDetector(CirMutationGraph mutation_graph) throws Exception {
		if(mutation_graph == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else {
			this.mutation_graph = mutation_graph;
			this.nodes = new HashMap<CirStatement, Collection<CirMutationNode>>();
			this.execute_records = new HashMap<CirMutationNode, List<Character>>();
			for(CirMutationNode node : this.mutation_graph.get_nodes()) {
				CirStatement statement = node.get_statement();
				if(!this.nodes.containsKey(statement)) {
					this.nodes.put(statement, new ArrayList<CirMutationNode>());
				}
				this.nodes.get(statement).add(node);
				this.execute_records.put(node, new ArrayList<Character>());
			}
		}
	}
	
	/* initializer */
	/**
	 * It clears the old records and build for new ones. 
	 */
	private void initialize() {
		for(CirMutationNode node : this.execute_records.keySet()) {
			this.execute_records.get(node).clear();
		}
	}
	/**
	 * accumulate the state and update the state in the specified node
	 * @param contexts
	 * @param state_node
	 * @throws Exception
	 */
	private void update_node(CStateContexts contexts, CStateNode state_node) throws Exception {
		contexts.accumulate(state_node);
		CirStatement statement = state_node.get_statement();
		if(this.nodes.containsKey(statement)) {
			Collection<CirMutationNode> local_nodes = this.nodes.get(statement);
			for(CirMutationNode local_node : local_nodes) {
				if(local_node.validate_constraint(contexts)) {
					if(local_node.validate_state_error(contexts)) {
						this.execute_records.get(local_node).add(ObservedMutation);
					}
					else {
						this.execute_records.get(local_node).add(InvalidStateError);
					}
				}
				else {
					this.execute_records.get(local_node).add(InvalidConstraint);
				}
			}
		}
	}
	/**
	 * perform path analysis on mutations in the program
	 * @param state_path
	 * @throws Exception
	 */
	private void update_path(CStatePath state_path) throws Exception {
		CStateContexts contexts = new CStateContexts();
		for(CStateNode state_node : state_path.get_nodes()) {
			this.update_node(contexts, state_node);
		}
	}
	/**
	 * evaluate the mutation without contextual information
	 * @throws Exception
	 */
	private void update_no_path() throws Exception {
		for(CirMutationNode node : this.execute_records.keySet()) {
			if(node.validate_constraint(null)) {
				if(node.validate_state_error(null)) {
					this.execute_records.get(node).add(ObservedMutation);
				}
				else {
					this.execute_records.get(node).add(InvalidStateError);
				}
			}
			else {
				this.execute_records.get(node).add(InvalidConstraint);
			}
		}
	}
	/**
	 * @return int[4] := { unreached, invalid_constraint, invalid_state_error, pass_mutation }
	 */
	private Map<CirMutation, int[]> get_summary_result() {
		Map<CirMutation, int[]> summary = new HashMap<CirMutation, int[]>();
		for(CirMutationNode node : this.execute_records.keySet()) {
			List<Character> list = this.execute_records.get(node);
			int[] result = new int[] { 0, 0, 0, 0 };
			if(list.isEmpty()) {
				result[0]++;
			}
			else {
				for(Character value : list) {
					switch(value) {
					case InvalidConstraint:		result[1]++;	break;
					case InvalidStateError:		result[2]++;	break;
					case ObservedMutation:		result[3]++;	break;
					default: 									break;
					}
				}
			}
			summary.put(node.get_mutation(), result);
		}
		return summary;
	}
	
	/**
	 * @param state_path
	 * @return check the validity of each constraint and errors for killing the mutation.
	 * @throws Exception
	 */
	public Map<CirMutation, int[]> detect_in(CStatePath state_path) throws Exception {
		this.initialize();
		if(state_path == null) {
			this.update_no_path();
		}
		else {
			this.update_path(state_path);
		}
		return this.get_summary_result();
	}
	
	/* factory methods */
	/**
	 * create the detection analyzer for error propagation analysis for target mutation
	 * @param cir_mutations
	 * @param cir_mutation
	 * @param dominance_graph
	 * @return
	 * @throws Exception
	 */
	public static CirMutationDetector new_detector(CirMutations cir_mutations, 
			CirMutation cir_mutation, CDominanceGraph dominance_graph) throws Exception {
		CirMutationGraph graph = CirMutationGraph.parse(cir_mutations, cir_mutation, dominance_graph);
		return new CirMutationDetector(graph);
	}
	/**
	 * create the detection analyzer for error propagation analysis for target mutation
	 * @param cir_mutations
	 * @param cir_mutation
	 * @param dominance_graph
	 * @return
	 * @throws Exception
	 */
	public static CirMutationDetector new_detector(CirMutations cir_mutations, 
			CirMutation cir_mutation) throws Exception {
		CirMutationGraph graph = CirMutationGraph.parse(cir_mutations, cir_mutation, null);
		return new CirMutationDetector(graph);
	}
	
}
