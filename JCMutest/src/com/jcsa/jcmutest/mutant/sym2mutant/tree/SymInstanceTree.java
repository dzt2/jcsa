package com.jcsa.jcmutest.mutant.sym2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.test.state.CStatePath;


public class SymInstanceTree {
	
	/* definitions */
	private Mutant mutant;
	private SymInstanceTreeNode root;
	private SymInstanceTree(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.mutant = mutant;
			this.root = new SymInstanceTreeNode(this, this.new_root_instance());
		}
	}
	private SymInstance new_root_instance() throws Exception {
		CirTree cir_tree = mutant.get_space().get_cir_tree();
		CirFunction main_function = cir_tree.get_function_call_graph().get_main_function();
		if(main_function == null)  {
			AstNode location = mutant.get_mutation().get_location();
			AstFunctionDefinition definition = location.get_tree().function_of(location);
			for(CirFunction function : cir_tree.get_function_call_graph().get_functions()) {
				if(function.get_definition().get_ast_source() == definition) {
					main_function = function;
					break;
				}
			}
		}
		return SymInstances.expr_constraint(main_function.get_flow_graph().get_entry(), Boolean.TRUE, true);
	}
	public static SymInstanceTree new_tree(Mutant mutant, int distance, CDependGraph dependence_graph) throws Exception {
		SymInstanceTree tree = new SymInstanceTree(mutant);
		SymInstanceTrees.construct_sym_tree(tree, distance, dependence_graph);
		return tree;
	}
	
	/* getters */
	/**
	 * @return the mutation being killed
	 */
	public Mutant get_mutant() { return this.mutant; }
	public CirTree get_cir_tree() { return this.mutant.get_space().get_cir_tree(); }
	/**
	 * @return the root node of the tree
	 */
	public SymInstanceTreeNode get_root() { return this.root; }
	
	/* evaluation */
	private void clc_status(SymInstanceTreeNode node) {
		node.clc_status();
		for(SymInstanceTreeEdge edge : node.get_ou_edges()) {
			this.clc_status(edge);
		}
	}
	private void clc_status(SymInstanceTreeEdge edge) {
		edge.clc_status();
		this.clc_status(edge.get_target());
	}
	/**
	 * clear the accumulated status from the tree nodes and edges.
	 */
	public void clc_status() {
		this.clc_status(this.root);
	}
	/**
	 * static evaluation
	 * @throws Exception
	 */
	public void evaluate() throws Exception {
		SymInstanceTrees.static_evaluate(this);
	}
	/**
	 * dynamic evaluation
	 * @param path
	 * @throws Exception
	 */
	public void evaluate(CStatePath path) throws Exception {
		SymInstanceTrees.dynamic_evaluate(this, path);
	}
	
	/* inference */
	private void get_infection_edges(SymInstanceTreeNode node, Collection<SymInstanceTreeEdge> edges) {
		for(SymInstanceTreeEdge edge : node.get_ou_edges()) {
			if(edge.is_infection_edge()) {
				edges.add(edge);
			}
			else {
				this.get_infection_edges(edge.get_target(), edges);
			}
		}
	}
	/**
	 * @return the set of edges referring to the infection stage.
	 */
	public Collection<SymInstanceTreeEdge> get_infection_edges() {
		Collection<SymInstanceTreeEdge> edges = new HashSet<SymInstanceTreeEdge>();
		this.get_infection_edges(this.root, edges);
		return edges;
	}
	private void get_reachable_paths(SymInstanceTreeNode node, Collection<List<SymInstanceTreeEdge>> paths) {
		if(node.get_status().is_acceptable()) {
			for(SymInstanceTreeEdge edge : node.get_ou_edges()) {
				this.get_reachable_paths(edge, paths);
			}
		}
		else {
			paths.add(node.get_prev_path());
		}
	}
	private void get_reachable_paths(SymInstanceTreeEdge edge, Collection<List<SymInstanceTreeEdge>> paths) {
		if(edge.get_status().is_acceptable()) {
			this.get_reachable_paths(edge.get_target(), paths);
		}
		else {
			paths.add(edge.get_prev_path());
		}
	}
	/**
	 * @return the set of paths from root until the reachable edge or node
	 */
	public Collection<List<SymInstanceTreeEdge>> get_reachable_paths() {
		Collection<List<SymInstanceTreeEdge>> paths = new ArrayList<List<SymInstanceTreeEdge>>();
		this.get_reachable_paths(this.root, paths);
		return paths;
	}
	
}
