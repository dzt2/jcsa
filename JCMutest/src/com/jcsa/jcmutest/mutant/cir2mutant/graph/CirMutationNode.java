package com.jcsa.jcmutest.mutant.cir2mutant.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutationUtils;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * The statement node contains the error propagation tree in local statement.
 * 
 * @author yukimula
 *
 */
public class CirMutationNode {
	
	/* definitions */
	/** the graph where the statement node is created **/
	private CirMutationGraph graph;
	/** the constraints for reaching the statement in the node **/
	private Set<CirConstraint> path_constraints;
	/** the execution node of statement in which the errors are caused **/
	private CirExecution execution;
	/** the error propagation tree in the local statement **/
	private CirMutationTree tree;
	/** the propagation edges to this statement node from others **/
	protected List<CirMutationEdge> in_edges;
	/** the propagation edges from this statement node to others **/
	protected List<CirMutationEdge> ou_edges;
	
	/* constructor */
	/**
	 * create an isolated 
	 * @param graph
	 * @param mutation
	 * @throws Exception
	 */
	protected CirMutationNode(CirMutationGraph graph, CirMutation mutation) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation as null");
		else {
			this.graph = graph;
			this.path_constraints = new HashSet<CirConstraint>();
			this.tree = new CirMutationTree(this, mutation);
			this.in_edges = new LinkedList<CirMutationEdge>();
			this.ou_edges = new LinkedList<CirMutationEdge>();
			this.execution = mutation.get_statement().get_tree().
					get_localizer().get_execution(mutation.get_statement());
		}
	}
	
	/* getters */
	/**
	 * @return the error propagation graph where the node is created
	 */
	public CirMutationGraph get_graph() { return this.graph; }
	/**
	 * @return the path constraints for reaching the statement
	 */
	public Iterable<CirConstraint> get_path_constraints() {
		return this.path_constraints;
	}
	/**
	 * @return the execution node of the statement where the error
	 * 	       propagation tree is created
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the statement where the error propagation tree is created
	 */
	public CirStatement get_statement() { return this.execution.get_statement(); }
	/**
	 * @return the error propagation tree in local statement node
	 */
	public CirMutationTree get_tree() { return this.tree; }
	/**
	 * @return the error propagation edges to this node from others
	 */
	public Iterable<CirMutationEdge> get_in_edges() { return this.in_edges; }
	/**
	 * @return the error propagation edges from this node to others
	 */
	public Iterable<CirMutationEdge> get_ou_edges() { return this.ou_edges; }
	public boolean is_root() { return this.in_edges.isEmpty(); }
	public boolean is_leaf() { return this.ou_edges.isEmpty(); }
	
	/* setters */
	/**
	 * build the error propagation tree in the local node, CALLED ONLY ONCE.
	 * @param dependence_graph
	 * @param cir_mutations
	 * @throws Exception
	 */
	protected void build_local_tree(CDependGraph dependence_graph, 
			CirMutations cir_mutations) throws Exception {
		if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph");
		else if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations");
		else {
			/* 1. generate the path constraints */
			this.path_constraints.clear();
			this.path_constraints.addAll(CirMutationUtils.get_common_path_constraints(
					this.get_statement(), dependence_graph, cir_mutations));
			
			/* 2. rebuild the cir-mutation tree */
			this.tree.clear_tree();
			Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
			queue.add(this.tree.get_root());
			while(!queue.isEmpty()) {
				CirMutationTreeNode parent = queue.poll();
				Iterable<CirMutation> children_mutations = CirMutationUtils.
						local_propagate(cir_mutations, parent.get_cir_mutation());
				for(CirMutation child_mutation : children_mutations) {
					CirMutationTreeNode child = parent.new_child(child_mutation);
					queue.add(child);
				}
			}
			
			/* 3. update the tree leafs in the statement node */
			this.tree.update_leafs();
		}
	}
	
}
