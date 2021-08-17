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
 * It models the RIP process of killing a mutant using symbolic tree.
 * 
 * @author yukimula
 *
 */
public class CirMutationTree {
	
	/* attributes */
	/** the mutation of which killability is defined by this tree **/
	private Mutant mutant;
	/** the root node denotes the coverage of program entry in Cir **/
	private CirMutationTreeNode root;
	/** the set of CIR-based mutations generated from the mutation **/
	private List<CirMutation> cir_mutations;
	/** the set of edges from infection condition to their initial errors **/
	private Collection<CirMutationTreeEdge> infection_edges;
	
	/* constructor */
	private CirMutationTree(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			this.mutant = mutant;
			this.root = new CirMutationTreeNode(this, CirAttribute.
					new_cover_count(this.find_entry_of(mutant), 1));
			this.cir_mutations = new ArrayList<CirMutation>();
			Iterable<CirMutation> cir_mutations = CirMutationParsers.parse(
					mutant.get_space().get_cir_tree(), mutant.get_mutation());
			if(cir_mutations != null) {
				for(CirMutation cir_mutation : cir_mutations) {
					this.cir_mutations.add(cir_mutation);
				}
			}
			this.infection_edges = new ArrayList<CirMutationTreeEdge>();
		}
	}
	/**
	 * @param mutant
	 * @return the program entry where the mutation is created
	 * @throws Exception
	 */
	private CirExecution find_entry_of(Mutant mutant) throws Exception {
		CirTree cir_tree = mutant.get_space().get_cir_tree();
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
		return main_function.get_flow_graph().get_entry();
	}
	
	/* construction */
	/**
	 * @param mutant
	 * @return create the mutation tree to describe mutant's killability using decidable path
	 * @throws Exception
	 */
	public static CirMutationTree new_tree(Mutant mutant) throws Exception {
		CirMutationTree tree = new CirMutationTree(mutant);
		CirMutationTreeUtil.construct(tree, null);
		return tree;
	}
	/**
	 * @param mutant
	 * @param dependence_graph
	 * @return create the mutation tree to describe mutant's killability using PDG
	 * @throws Exception
	 */
	public static CirMutationTree new_tree(Mutant mutant, CDependGraph dependence_graph) throws Exception {
		CirMutationTree tree = new CirMutationTree(mutant);
		CirMutationTreeUtil.construct(tree, dependence_graph);
		return tree;
	}
	/**
	 * @param mutant
	 * @param dependence_graph
	 * @return create the mutation tree to describe mutant's killability using dynamic analysis
	 * @throws Exception
	 */
	public static CirMutationTree new_tree(Mutant mutant, CStatePath state_path) throws Exception {
		CirMutationTree tree = new CirMutationTree(mutant);
		CirMutationTreeUtil.construct(tree, state_path);
		return tree;
	}
	
	/* getters */
	/**
	 * @return abstract syntactic tree of the mutation
	 */
	public AstTree get_ast_tree() { return this.mutant.get_space().get_ast_tree(); }
	/**
	 * @return C-intermediate representation of the mutant
	 */
	public CirTree get_cir_tree() { return this.mutant.get_space().get_cir_tree(); }
	/**
	 * @return the mutation of which killability is defined by this tree
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return whether the mutant is transformed to some CIR-based mutations
	 */
	public boolean has_cir_mutations() { return !this.cir_mutations.isEmpty(); }
	/**
	 * @return the set of CIR-based mutations generated from the mutation
	 */
	public Iterable<CirMutation> get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the set of edges from infection condition to their initial errors
	 */
	public Iterable<CirMutationTreeEdge> get_infection_edges() { return this.infection_edges; }
	/**
	 * @return the root node denotes the coverage of program entry in mutation
	 */
	public CirMutationTreeNode get_root() { return this.root; }
	/**
	 * @return the set of all the tree nodes in this model
	 */
	public Iterable<CirMutationTreeNode> get_nodes() {
		Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
		List<CirMutationTreeNode> records = new ArrayList<CirMutationTreeNode>();
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
	 * @return the set of leafs in the tree
	 */
	public Iterable<CirMutationTreeNode> get_leafs() {
		Set<CirMutationTreeNode> leafs = new HashSet<CirMutationTreeNode>();
		for(CirMutationTreeNode node : this.get_nodes()) {
			if(node.is_leaf()) {
				leafs.add(node);
			}
		}
		return leafs;
	}
	/**
	 * update the set of infection edges created in this tree
	 */
	protected void set_infection_edges() {
		this.infection_edges.clear();
		for(CirMutationTreeNode node : this.get_nodes()) {
			if(!node.is_root()) {
				CirMutationTreeEdge edge = node.get_in_edge();
				if(edge.get_edge_type() == CirMutationTreeFlow.infection) {
					this.infection_edges.add(edge);
				}
			}
		}
	}
	/**
	 * @param execution
	 * @param max_evaluation_times
	 * @return the set of infection edges of which acceptions are less than given times and w.r.t. the execution
	 * @throws Exception
	 */
	protected Iterable<CirMutationTreeEdge> find_infection_edges(
			CirExecution execution, int max_evaluation_times) throws Exception {
		if(execution == null) {
			return this.infection_edges;
		}
		else {
			List<CirMutationTreeEdge> edges = new ArrayList<CirMutationTreeEdge>();
			for(CirMutationTreeEdge infection_edge : this.infection_edges) {
				if(infection_edge.get_target().get_node_execution() == execution) {
					if(infection_edge.get_target().get_node_data().number_of_acceptable() < max_evaluation_times) {
						edges.add(infection_edge);
					}
				}
			}
			return edges;
		}
	}
	
	/* setters */
	/**
	 * clear the data state hold by each node in this tree
	 */
	public void clc_states() {
		for(CirMutationTreeNode node : this.get_nodes()) {
			node.get_node_data().clc();
		}
	}
	/**
	 * summarize the data state hold from every tree node
	 * @throws Exception
	 */
	public void sum_states() throws Exception {
		for(CirMutationTreeNode node : this.get_nodes()) {
			node.get_node_data().sum();
		}
	}
	/**
	 * recursively evaluate the node 
	 * @param node
	 * @param context
	 * @throws Exception
	 */
	private void do_evaluate(CirMutationTreeNode node, SymbolProcess context) throws Exception {
		Boolean result = node.get_node_data().add(context);
		if(result == null || result.booleanValue()) {
			for(CirMutationTreeEdge edge : node.get_ou_edges()) {
				this.do_evaluate(edge.get_target(), context);
			}
		}
	}
	/**
	 * @param execution
	 * @param max_evaluation_times
	 * @param context
	 * @throws Exception
	 */
	public void add_states(CirExecution execution, int max_evaluation_times, SymbolProcess context) throws Exception {
		/* I. fetch the state infection edges w.r.t. execution and evaluation times limit */
		Iterable<CirMutationTreeEdge> infection_edges = this.find_infection_edges(execution, max_evaluation_times);
		
		/* II. capture the pre_condition tree nodes reaching the collected infection edge */
		Collection<CirMutationTreeNode> pred_nodes = new HashSet<CirMutationTreeNode>();
		for(CirMutationTreeEdge infection_edge : infection_edges) {
			CirMutationTreeNode parent = infection_edge.get_source();
			while(parent != null) {
				pred_nodes.add(parent); 
				parent = parent.get_parent();
			}
		}
		for(CirMutationTreeNode pred_node : pred_nodes) {
			pred_node.get_node_data().add(null);
		}
		
		/* III. perform the propagation evaluation from the nodes of infection edges */
		for(CirMutationTreeEdge infection_edge : infection_edges) {
			this.do_evaluate(infection_edge.get_source(), context);
		}
	}
	
}
