package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.test.state.CStatePath;


/**
 * The execution state representing killing process of a mutation.
 * 
 * @author yukimula
 *
 */
public class CirStateTree {
	
	/* definitions */
	/** the mutation that the tree is going to kill **/
	private Mutant 				mutant;
	/** the set of cir-based mutations generated from mutant **/
	private Set<CirMutation>	cir_mutations;
	/** the root node for covering the program entry **/
	private CirStateNode 		root;
	/** the set of state nodes for covering faulty statements **/
	private Set<CirStateNode> 	mid_nodes;
	
	/* constructor */
	/**
	 * It creates an empty execution state tree for killing the mutation
	 * @param mutant
	 * @throws Exception
	 */
	private CirStateTree(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant as null");
		}
		else {
			this.mutant = mutant;
			this.cir_mutations = new HashSet<CirMutation>();
			try {
				for(CirMutation cir_mutation : CirMutations.parse(mutant)) {
					this.cir_mutations.add(cir_mutation);
				}
			}
			catch(Exception ex) { /* none of mutations created */ }
			this.root = new CirStateNode(this, this.find_program_entry(mutant));
			this.mid_nodes = new HashSet<CirStateNode>();
		}
	}
	/**
	 * @param mutant
	 * @return find the program entry of the mutation
	 * @throws Exception
	 */
	private CirExecution find_program_entry(Mutant mutant) throws Exception {
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
		
		if(main_function == null) {
			throw new IllegalArgumentException("Cannot found function entry");
		}
		else {
			return main_function.get_flow_graph().get_entry();
		}
	}
	
	/* getters */
	/**
	 * @return the mutant to generate symbolic execution state tree
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return abstract syntactic tree
	 */
	public AstTree get_ast_tree() { return this.mutant.get_space().get_ast_tree(); }
	/**
	 * @return C-intermediate representative
	 */
	public CirTree get_cir_tree() { return this.mutant.get_space().get_cir_tree(); }
	/**
	 * @return the root node
	 */
	public CirStateNode get_root() { return this.root; }
	/**
	 * @return whether there is cir_mutation generated from mutant
	 */
	public boolean has_cir_mutations() { return !this.cir_mutations.isEmpty(); }
	/**
	 * @return the set of cir-based mutations of the mutant
	 */
	public Iterable<CirMutation> get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the state nodes to annotate the reaching of faulty statement
	 */
	public Iterable<CirStateNode> get_mid_state_nodes() { return this.mid_nodes; }
	/**
	 * @return all the nodes created under the tree in BFS-traversal
	 */
	public Iterator<CirStateNode> get_nodes() { return this.root.get_post_nodes(); }
	/**
	 * update the state nodes to annotate the reaching of faulty statement
	 */
	protected void update_mid_state_nodes() {
		Iterator<CirStateNode> iterator = this.get_nodes();
		this.mid_nodes.clear();
		while(iterator.hasNext()) {
			CirStateNode node = iterator.next();
			if(node.get_type() == CirStateType.mid_condition) {
				this.mid_nodes.add(node);
			}
		}
	}
	
	/* creator */
	public static CirStateTree new_tree(Mutant mutant) throws Exception {
		CirStateTree tree = new CirStateTree(mutant);
		CirStateUtil.construct(tree, null);
		return tree;
	}
	public static CirStateTree new_tree(Mutant mutant, CDependGraph dependence_graph) throws Exception {
		CirStateTree tree = new CirStateTree(mutant);
		CirStateUtil.construct(tree, dependence_graph);
		return tree;
	}
	public static CirStateTree new_tree(Mutant mutant, CStatePath state_path) throws Exception {
		CirStateTree tree = new CirStateTree(mutant);
		CirStateUtil.construct(tree, state_path);
		return tree;
	}
	
}
