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
	 * @return the tree root
	 */
	public CirMutationTreeNode get_root() { return this.root; }
	/**
	 * @return the set of infection nodes
	 */
	public Iterable<CirMutationTreeEdge> get_infection_edges() {
		Queue<CirMutationTreeNode> queue = new LinkedList<>();
		queue.add(this.root);
		List<CirMutationTreeEdge> edges = new ArrayList<CirMutationTreeEdge>();
		while(!queue.isEmpty()) {
			CirMutationTreeNode node = queue.poll();
			for(CirMutationTreeEdge edge : node.get_ou_edges()) {
				if(edge.get_edge_type() == CirMutationTreeFlow.infect) {
					edges.add(edge);
				}
				queue.add(edge.get_target());
			}
		}
		return edges;
	}
	/**
	 * @return the set of cir-mutations generated from the source mutant
	 */
	public Iterable<CirMutation> get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return whether there exists cir-mutation corresponding to the mutant
	 */
	public boolean has_cir_mutations() { return !this.cir_mutations.isEmpty(); }
	public Collection<CirMutationTreeNode> get_nodes() {
		Queue<CirMutationTreeNode> queue = new LinkedList<>();
		queue.add(this.root);
		Set<CirMutationTreeNode> nodes = new HashSet<CirMutationTreeNode>();
		while(!queue.isEmpty()) {
			CirMutationTreeNode node = queue.poll();
			nodes.add(node);
			for(CirMutationTreeEdge edge : node.get_ou_edges()) {
				queue.add(edge.get_target());
			}
		}
		return nodes;
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
		CirMutationTreeUtil.util.construct_mutation_tree_in(tree, null);
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
		CirMutationTreeUtil.util.construct_mutation_tree_in(tree, dependence_graph);
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
		CirMutationTreeUtil.util.construct_mutation_tree_in(tree, state_path);
		return tree;
	}

	/* evaluation */
	/**
	 * clear all the status in the tree nodes
	 * @throws Exception
	 */
	public void initialize_status() throws Exception {
		Queue<CirMutationTreeNode> queue = new LinkedList<>();
		queue.add(this.root);
		while(!queue.isEmpty()) {
			CirMutationTreeNode node = queue.poll();
			node.get_status().clc();
			for(CirMutationTreeEdge edge : node.get_ou_edges()) {
				queue.add(edge.get_target());
			}
		}
	}
	/**
	 * evaluate using context at the mutated location
	 * @param context
	 * @throws Exception
	 */
	public void evaluate(SymbolProcess context) throws Exception {
		CirMutationTreeUtil.util.evaluate_at(this, context);
	}
	/**
	 * context-insensitive evaluation from infection nodes
	 * @throws Exception
	 */
	public void evaluate() throws Exception { this.evaluate(null); }
	/**
	 * summarize all the nodes in the tree
	 * @throws Exception
	 */
	public void summarize_all() throws Exception {
		Queue<CirMutationTreeNode> queue = new LinkedList<>();
		queue.add(this.root);
		while(!queue.isEmpty()) {
			CirMutationTreeNode node = queue.poll();
			node.get_status().sum();
			for(CirMutationTreeEdge edge : node.get_ou_edges()) {
				queue.add(edge.get_target());
			}
		}
	}
	
}
