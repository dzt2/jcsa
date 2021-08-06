package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParsers;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * feature tree of mutant
 *
 * @author yukimula
 *
 */
public class CirMutationTree {

	/* attributes */
	/** mutant to describe the feature tree **/
	private Mutant mutant;
	/** the root node of cov_stmt(program.entry) **/
	private CirMutationTreeNode root;
	/** the set of cir-based mutations in the mutant **/
	private List<CirMutation> cir_mutations;
	/** the set of state infection edges from condition to initial error **/
	private Collection<CirMutationTreeEdge> infection_edges;

	/* constructor */
	/**
	 * @param mutant
	 * @throws Exception
	 */
	private CirMutationTree(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			this.mutant = mutant;
			this.root = this.new_root();
			this.cir_mutations = new ArrayList<>();
			Iterable<CirMutation> cir_mutations = CirMutationParsers.parse(
					this.get_cir_tree(), this.mutant.get_mutation());
			if(cir_mutations != null) {
				for(CirMutation cir_mutation : cir_mutations) {
					this.cir_mutations.add(cir_mutation);
				}
			}
			this.infection_edges = null;	/* wait for construction */
		}
	}
	private CirMutationTreeNode new_root() throws Exception {
		CirTree cir_tree = this.get_cir_tree();
		CirFunction main_function = cir_tree.get_function_call_graph().get_main_function();
		if(main_function == null) {
			AstNode ast_location = mutant.get_mutation().get_location();
			while(ast_location != null) {
				if(ast_location instanceof AstFunctionDefinition) {
					Iterable<CirNode> cir_defs = cir_tree.get_localizer().
							get_cir_nodes(ast_location, CirFunctionDefinition.class);
					CirNode cir_def = cir_defs.iterator().next();
					for(CirFunction function : cir_tree.get_function_call_graph().get_functions()) {
						if(function.get_definition() == cir_def) {
							main_function = function;
							break;
						}
					}
					break;
				}
				else {
					ast_location = ast_location.get_parent();
				}
			}
		}
		CirExecution execution = main_function.get_flow_graph().get_entry();
		return new CirMutationTreeNode(this, CirMutationTreeType.precondition,
				CirAttribute.new_cover_count(execution, 1));
	}

	/* getters */
	/**
	 * @return mutant of the feature tree in CIR-mutation terms
	 */
	public Mutant 	get_mutant() { return this.mutant; }
	/**
	 * @return AST
	 */
	public AstTree	get_ast_tree() { return this.mutant.get_space().get_ast_tree(); }
	/**
	 * @return CIR
	 */
	public CirTree	get_cir_tree() { return this.mutant.get_space().get_cir_tree(); }
	/**
	 * @return whether there exist cir-mutation(s) w.r.t. the mutant in program
	 */
	public boolean has_cir_mutations() { return !this.cir_mutations.isEmpty(); }
	/**
	 * @return the set of CirMutation(s) w.r.t. the mutant in CIR-program
	 */
	public Iterable<CirMutation> get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the tree root
	 */
	public CirMutationTreeNode get_root() { return this.root; }
	/**
	 * @return whether there exist infection edge from constraint to initial error
	 */ 
	public boolean has_infection_edges() { return !this.infection_edges.isEmpty(); }
	/**
	 * @return the set of edges from state infection constraint to initial error
	 */
	public Iterable<CirMutationTreeEdge> get_infection_edges() { return this.infection_edges; }
	/**
	 * @return the set of all the tree nodes in this model
	 */
	public Iterable<CirMutationTreeNode> get_nodes() {
		Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
		Set<CirMutationTreeNode> records = new HashSet<CirMutationTreeNode>();
		queue.add(this.root);
		while(!queue.isEmpty()) {
			CirMutationTreeNode node = queue.poll();
			records.add(node);
			for(CirMutationTreeEdge edge : node.get_ou_edges()) {
				queue.add(edge.get_target());
			}
		}
		return records;
	}
	/**
	 * @param execution
	 * @param max_infected_times how many times the selected nodes being infected at most
	 * @return get the set of state infection edges w.r.t. the given execution
	 */
	public Iterable<CirMutationTreeEdge> get_infection_edges(
			CirExecution execution, int max_infected_times) {
		List<CirMutationTreeEdge> edges = new ArrayList<CirMutationTreeEdge>();
		for(CirMutationTreeEdge infection_edge : this.infection_edges) {
			CirAttribute init_error = infection_edge.get_target().get_attribute();
			if(init_error.get_execution() == execution || execution == null) {
				CirMutationTreeStatus status = infection_edge.get_target().get_status();
				if(status.number_of_acceptable() < max_infected_times) {
					edges.add(infection_edge);
				}
			}
		}
		return edges;
	}
	
	/* construction */
	/**
	 * construct a static cir-based mutation tree for mutant
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public static CirMutationTree new_tree(Mutant mutant) throws Exception {
		CirMutationTree tree = new CirMutationTree(mutant);
		tree.infection_edges = CirMutationTreeUtil.util.construct_mutation_tree_in(tree, null);
		return tree;
	}
	/**
	 * construct a static cir-based mutation tree for mutant using dependence model
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public static CirMutationTree new_tree(Mutant mutant,
			CDependGraph dependence_graph) throws Exception {
		CirMutationTree tree = new CirMutationTree(mutant);
		tree.infection_edges = CirMutationTreeUtil.util.construct_mutation_tree_in(tree, dependence_graph);
		return tree;
	}
	/**
	 * construct a dynamic cir-based mutation tree for mutant using state-path
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public static CirMutationTree new_tree(Mutant mutant,
			CStatePath state_path) throws Exception {
		CirMutationTree tree = new CirMutationTree(mutant);
		tree.infection_edges = CirMutationTreeUtil.util.construct_mutation_tree_in(tree, state_path);
		return tree;
	}
	
	/* evaluation */
	/**
	 * initialize the status for each node in the tree model
	 */
	public void initialize_status() {
		for(CirMutationTreeNode node : this.get_nodes()) {
			node.get_status().clc();
		}
	}
	/**
	 * summarize the status for all the nodes in the tree model
	 * @throws Exception
	 */
	public void summarize_status() throws Exception {
		for(CirMutationTreeNode node : this.get_nodes()) {
			node.get_status().sum();
		}
	}
	/**
	 * perform down-load evaluation from a given node
	 * @param context
	 * @throws Exception
	 */
	private void do_down_evaluation(CirMutationTreeNode node, SymbolProcess context) throws Exception {
		Boolean result = node.get_status().add(context);
		if(result == null || result.booleanValue()) {
			for(CirMutationTreeEdge edge : node.get_ou_edges()) {
				this.do_down_evaluation(edge.get_target(), context);
			}
		}
	}
	/**
	 * @param execution				where the infection should occur
	 * @param max_infected_times	maximal times to infect the node
	 * @param context				contextual evidence to evaluate
	 * @return						the number of nodes being evaluated
	 * @throws Exception
	 */
	public int evaluate_status(CirExecution execution, int max_infected_times, SymbolProcess context) throws Exception {
		/* 1. declarations */
		Iterable<CirMutationTreeEdge> infection_edges = 
				this.get_infection_edges(execution, max_infected_times);
		int evaluation_counter = 0;
		
		/* 2. capture previous nodes on the edges */
		Set<CirMutationTreeNode> prev_nodes = new HashSet<CirMutationTreeNode>();
		for(CirMutationTreeEdge infection_edge : infection_edges) {
			CirMutationTreeNode parent = infection_edge.get_source().get_parent();
			while(parent != null) {
				prev_nodes.add(parent);
				parent = parent.get_parent();
			}
		}
		for(CirMutationTreeNode parent : prev_nodes) {
			parent.get_status().add(null); evaluation_counter++;
		}
		
		/* 3. perform propagation analysis from */
		for(CirMutationTreeEdge infection_edge : infection_edges) {
			this.do_down_evaluation(infection_edge.get_source(), context);
			evaluation_counter++;
		}
		return evaluation_counter;
	}
	
}
