package com.jcsa.jcmutest.mutant.cir2mutant.rtree;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationFlowType;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * The statement node in error-propagation graph.
 * 
 * @author yukimula
 *
 */
public class CirMutationNode {
	
	/* definitions */
	/** the graph where the node is created **/
	private CirMutationGraph graph;
	/** the statement where the state error occurs **/
	private CirStatement statement;
	/** path constraints required for covering the faulty statement **/
	private Set<CirConstraint> constraints;
	/** it maintains the state error and propagation structure in the node **/
	private CirMutationTree mutation_tree;
	protected List<CirMutationEdge> in_edges;
	protected List<CirMutationEdge> ou_edges;
	
	/**
	 * create the node of statement in the graph
	 * @param graph
	 * @param statement
	 * @throws Exception
	 */
	protected CirMutationNode(CirMutationGraph graph, CirMutation root_mutation) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(root_mutation == null || root_mutation.get_statement() == null)
			throw new IllegalArgumentException("Invalid root_mutation: null");
		else {
			this.graph = graph;
			this.statement = root_mutation.get_statement();
			this.constraints = new HashSet<CirConstraint>();
			this.mutation_tree = new CirMutationTree(this, root_mutation);
			this.in_edges = new LinkedList<CirMutationEdge>();
			this.ou_edges = new LinkedList<CirMutationEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the graph where the node is created
	 */
	public CirMutationGraph get_graph() { return this.graph; }
	/**
	 * @return the statement where the state error occurs
	 */
	public CirStatement get_statement() { return this.statement; }
	/**
	 * @return path constraints required for covering the faulty statement 
	 */
	public Iterable<CirConstraint> get_constraints() { return this.constraints; }
	/**
	 * @return it maintains the state error and propagation structure in the node
	 */
	public CirMutationTree get_mutation_tree() { return this.mutation_tree; }
	/**
	 * @return error propagation edges from tree node in this statement to the others
	 */
	public Iterable<CirMutationEdge> get_in_edges() { return this.in_edges; }
	/**
	 * @return error propagation edges to tree node in this statement from the others
	 */
	public Iterable<CirMutationEdge> get_ou_edges() { return this.ou_edges; }
	/**
	 * update the path constraints in this node
	 * @param dependence_graph
	 * @throws Exception
	 */
	protected void set_path_constraints(CDependGraph dependence_graph) throws Exception {
		Iterable<CirConstraint> common_constraints = CirMutationGraphUtils.
				common_path_constraints(dependence_graph, this.statement, this.graph.get_cir_mutations());
		this.constraints.clear();
		for(CirConstraint constraint : common_constraints) {
			this.constraints.add(constraint);
		}
	}
	/**
	 * rebuild the inner mutation propagation tree
	 * @throws Exception
	 * @return the set of leafs in the tree
	 */
	protected Iterable<CirMutationTreeNode> build_inner_tree() throws Exception {
		Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
		queue.add(this.mutation_tree.get_root());
		while(!queue.isEmpty()) {
			while(!queue.isEmpty()) {
				CirMutationTreeNode tree_node = queue.poll();
				Map<CirMutation, CirMutationFlowType> next_mutations = CirMutationGraphUtils.
						propagate_one(this.graph.get_cir_mutations(), tree_node.get_mutation());
				for(CirMutation next_mutation : next_mutations.keySet()) {
					CirMutationTreeNode child = tree_node.get_child(next_mutation);
					queue.add(child);
				}
			}
		}
		
		List<CirMutationTreeNode> leafs = new LinkedList<CirMutationTreeNode>();
		queue.add(this.mutation_tree.get_root());
		while(!queue.isEmpty()) {
			CirMutationTreeNode tree_node = queue.poll();
			if(tree_node.is_leaf()) {
				leafs.add(tree_node);
			}
			else {
				for(CirMutationTreeNode child : tree_node.get_children()) {
					queue.add(child);
				}
			}
		}
		return leafs;
	}
	
}
