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
 * It provides interfaces to detect whether a mutant can be killed
 * by a specific test input, and the reasons to interpret why that
 * mutant is not killed by that test.
 * 
 * @author yukimula
 *
 */
public class CirMutationDetection {
	
	/* definitions */
	/** the graph of which mutations will be analyzed **/
	private CirMutationGraph mutation_graph;
	/** mapping from statement to the nodes in that statement,
	 * 	which inform the system to analyze these nodes when the
	 * 	state flow reaches the target statement in testing. **/
	private Map<CirStatement, Collection<CirMutationNode>> nodes;
	/** mapping from each node to their detection levels **/
	private Map<CirMutationNode, List<CirDetectionLevel>> detections;
	
	/* constructor */
	/**
	 * construct the detection machine for analyzing the state error graph
	 * @param mutation_graph
	 * @throws Exception
	 */
	private CirMutationDetection(CirMutationGraph mutation_graph) throws Exception {
		if(mutation_graph == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else {
			this.mutation_graph = mutation_graph;
			this.nodes = new HashMap<CirStatement, Collection<CirMutationNode>>();
			this.detections = new HashMap<CirMutationNode, List<CirDetectionLevel>>();
			for(CirMutationNode node : this.mutation_graph.get_nodes()) {
				CirStatement statement = node.get_statement();
				if(!this.nodes.containsKey(statement)) {
					this.nodes.put(statement, new ArrayList<CirMutationNode>());
				}
				this.nodes.get(statement).add(node);
				this.detections.put(node, new ArrayList<CirDetectionLevel>());
			}
		}
	}
	
	/* state update algorithms */
	/**
	 * It clears the old records in detection maps.
	 */
	private void initialize() {
		for(CirMutationNode node : this.detections.keySet()) {
			this.detections.get(node).clear();
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
						this.detections.get(local_node).add(CirDetectionLevel.pass_mutation);
					}
					else {
						this.detections.get(local_node).add(CirDetectionLevel.non_influence);
					}
				}
				else {
					this.detections.get(local_node).add(CirDetectionLevel.not_satisfied);
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
		for(CirMutationNode node : this.detections.keySet()) {
			if(node.validate_constraint(null)) {
				if(node.validate_state_error(null)) {
					this.detections.get(node).add(CirDetectionLevel.pass_mutation);
				}
				else {
					this.detections.get(node).add(CirDetectionLevel.non_influence);
				}
			}
			else {
				this.detections.get(node).add(CirDetectionLevel.not_satisfied);
			}
		}
	}
	/**
	 * @return mapping from mutation to the detection summary result {maps from detection level to their counter in testing,
	 * 		   each of which refers to one execution of the faulty statement during analysis}
	 * @throws Exception
	 */
	private Map<CirMutationNode, Map<CirDetectionLevel, Integer>> get_summary() throws Exception {
		Map<CirMutationNode, Map<CirDetectionLevel, Integer>> summary = 
				new HashMap<CirMutationNode, Map<CirDetectionLevel, Integer>>();
		
		for(CirMutationNode mutation_node : this.detections.keySet()) {
			Collection<CirDetectionLevel> levels = this.detections.get(mutation_node);
			
			Map<CirDetectionLevel, Integer> counter = new HashMap<CirDetectionLevel, Integer>();
			counter.put(CirDetectionLevel.not_executed,  Integer.valueOf(0));
			counter.put(CirDetectionLevel.not_satisfied, Integer.valueOf(0));
			counter.put(CirDetectionLevel.non_influence, Integer.valueOf(0));
			counter.put(CirDetectionLevel.pass_mutation, Integer.valueOf(0));
			
			if(levels.isEmpty()) {
				counter.put(CirDetectionLevel.not_executed, counter.get(CirDetectionLevel.not_executed) + 1);
			}
			else {
				for(CirDetectionLevel level : levels) counter.put(level, counter.get(level) + 1);
			}
			summary.put(mutation_node, counter);
		}
		
		return summary;
	}
	/**
	 * @param state_path
	 * @return counter of detection level for each execution of the faulty statement during testing
	 * @throws Exception
	 */
	public Map<CirMutationNode, Map<CirDetectionLevel, Integer>> detection_analysis(CStatePath state_path) throws Exception {
		this.initialize();
		if(state_path == null) {
			this.update_no_path();
		}
		else {
			this.update_path(state_path);
		}
		return this.get_summary();
	}
	
	/* factory methods */
	/**
	 * @param cir_mutations
	 * @param cir_mutation
	 * @param dominance_graph
	 * @return create the detection analyzer w.r.t. the cir-mutation graph with path constraints 
	 * @throws Exception
	 */
	public static CirMutationDetection new_detector(CirMutations cir_mutations, 
			CirMutation cir_mutation, CDominanceGraph dominance_graph) throws Exception {
		CirMutationGraph graph = CirMutationGraph.parse(cir_mutations, cir_mutation, dominance_graph);
		return new CirMutationDetection(graph);
	}
	/**
	 * @param cir_mutations
	 * @param cir_mutation
	 * @return create the detection analyzer w.r.t. the cir-mutation graph without path constraints 
	 * @throws Exception
	 */
	public static CirMutationDetection new_detector(CirMutations cir_mutations,
			CirMutation cir_mutation) throws Exception {
		CirMutationGraph graph = CirMutationGraph.parse(cir_mutations, cir_mutation);
		return new CirMutationDetection(graph);
	}
	
}
