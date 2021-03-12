package com.jcsa.jcmutest.mutant.cir2mutant._back_;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * The symbolic instance graph as feature model for prediction and evaluation.
 * 
 * @author yukimula
 *
 */
public class SymInstanceGraph {
	
	/* definitions */
	/** the mutation that the instance graph describes **/
	private Mutant mutant;
	/** the library used to generate symbolic instance **/
	private CirMutations cir_mutations;
	/** the collection of symbolic nodes created in this graph **/
	private List<SymInstanceNode> nodes;
	/**
	 * create an empty graph to describe the structural features hold by the mutant
	 * @param mutant
	 * @throws Exception
	 */
	private SymInstanceGraph(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.mutant = mutant;
			this.cir_mutations = new CirMutations(mutant.get_space().get_cir_tree());
			this.nodes = new ArrayList<SymInstanceNode>();
			this.clear();
		}
	}
	
	/* getters */
	/**
	 * @return the mutant being described by this model
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the syntactic mutation defined by the mutant of this graph
	 */
	public AstMutation get_mutation() { return this.mutant.get_mutation(); }
	/**
	 * @return the library used to generate symbolic instance
	 */
	public CirMutations get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the number of nodes created in this graph
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * @return the root node w.r.t. the main function entry in the graph
	 */
	public SymInstanceNode get_root() { return this.nodes.get(0); }
	/**
	 * @return the collection of nodes created in this graph
	 */
	public Iterable<SymInstanceNode> get_nodes() { return this.nodes; }
	/**
	 * @return the set of nodes w.r.t. mutated statement of the cir-mutations
	 */
	public Iterable<SymInstanceNode> get_mutated_nodes() { 
		List<SymInstanceNode> mutated_nodes = new ArrayList<SymInstanceNode>();
		for(SymInstanceNode node : this.nodes) {
			if(node.get_type() == SymInstanceNodeType.muta_node) {
				mutated_nodes.add(node);
			}
		}
		return mutated_nodes;
	}
	
	/* setters */
	/**
	 * remove all the nodes, edges in the graph and regenerate the root node
	 */
	private void clear() throws Exception {
		/* remove the original nodes in the graph */
		for(SymInstanceNode node : this.nodes) {
			node.delete();
		}
		this.nodes.clear(); 
		
		/* regenerate the root of the node */
		CirTree cir_tree = this.mutant.get_space().get_cir_tree();
		CirFunction main = cir_tree.get_function_call_graph().get_main_function();
		CirExecution main_entry = main.get_flow_graph().get_entry();
		this.new_node(SymInstanceNodeType.path_node, main_entry, null);
	}
	/**
	 * create a new isolated node in the graph w.r.t. the execution
	 * @param execution
	 * @return
	 * @throws Exception
	 */
	protected SymInstanceNode new_node(SymInstanceNodeType type, CirExecution 
			execution, SymStateError state_error) throws Exception {
		SymInstanceNode node = new SymInstanceNode(this, type, execution, state_error);
		this.nodes.add(node); return node;
	}
	
	/* builder */
	/**
	 * create a symbolic instance graph with specified distance of error propagation
	 * @param dependence_graph used to generate control and data dependence for reaching or propagation
	 * @param mutant the mutation as the source of the symbolic instance graph
	 * @param maximal_distance the maximal distance of error propagation from the enclosing expression where the mutation is seeded
	 * @return the symbolic instance graph built from the mutant
	 * @throws Exception
	 */
	public static SymInstanceGraph new_graph(CDependGraph dependence_graph, Mutant mutant, int maximal_distance) throws Exception {
		SymInstanceGraph sym_graph = new SymInstanceGraph(mutant);
		SymInstanceBuilder.builder.generate_reaching_paths(dependence_graph, sym_graph);
		SymInstanceBuilder.builder.propagate(dependence_graph, sym_graph, maximal_distance);
		return sym_graph;
	}
	
	/* evaluation */
	/**
	 * Perform dynamic evaluation on nodes and edges in the graph
	 * @param state_path
	 * @throws Exception
	 */
	public void evaluate(CStatePath state_path) throws Exception {
		SymInstanceEvaluator.evaluator.dynamic_evaluate(this, state_path);
	}
	/**
	 * Perform static evaluation on nodes and edges in the graph
	 * @throws Exception
	 */
	public void evaluate() throws Exception {
		SymInstanceEvaluator.evaluator.static_evaluate(this);
	}
	/**
	 * @return the set of reachable paths from the root of the node 
	 * @throws Exception
	 */
	public Collection<List<SymInstanceEdge>> select_reachable_paths() throws Exception {
		return SymInstanceEvaluator.evaluator.select_reachable_paths(this);
	}
	
}
