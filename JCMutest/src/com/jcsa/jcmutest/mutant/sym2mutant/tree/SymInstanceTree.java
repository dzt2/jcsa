package com.jcsa.jcmutest.mutant.sym2mutant.tree;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymInstanceTreeUtils;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymInstanceUtils;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * Create a symbolic instance tree for killing a particular mutation.
 * 
 * @author yukimula
 *
 */
public class SymInstanceTree {
	
	/* definitions */
	private CirTree cir_tree;
	private Mutant mutant;
	private SymInstanceTreeNode root;
	public SymInstanceTree(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.cir_tree = mutant.get_space().get_cir_tree();
			this.mutant = mutant;
			this.root = this.new_root();
		}
	}
	private SymInstanceTreeNode new_root() throws Exception {
		CirFunction main_function = this.cir_tree.get_function_call_graph().get_main_function();
		if(main_function == null) {
			AstNode ast_location = this.mutant.get_mutation().get_location();
			ast_location = ast_location.get_tree().function_of(ast_location);
			for(CirFunction function : this.cir_tree.get_function_call_graph().get_functions()) {
				if(function.get_definition().get_ast_source() == ast_location) {
					main_function = function; break;
				}
			}
		}
		return new SymInstanceTreeNode(this, SymInstanceUtils.stmt_constraint(
				main_function.get_flow_graph().get_entry(), COperator.greater_eq, 1));
	}
	
	/* getters */
	/**
	 * @return mutation to be killed under the symbolic tree
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the C-intermediate representation where the mutation is defined
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @return the root node of the symbolic tree 
	 */
	public SymInstanceTreeNode get_root() { return this.root; }
	
	/* creators */
	/**
	 * @param mutant
	 * @param propagation_distance maximal distance for error propagation
	 * @param dependence_graph
	 * @return
	 * @throws Exception
	 */
	public static SymInstanceTree new_tree(Mutant mutant, int propagation_distance, CDependGraph dependence_graph) throws Exception {
		return SymInstanceTreeUtils.utils.build_sym_instance_tree(mutant, propagation_distance, dependence_graph);
	}
	

}
