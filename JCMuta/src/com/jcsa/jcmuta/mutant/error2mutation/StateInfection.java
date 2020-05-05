package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceGraph;

/**
 * Used to create infection of state errors.
 * 
 * @author yukimula
 *
 */
public abstract class StateInfection {
	
	/* parameters */
	/** number of bit that cannot mask the others **/
	protected static final int min_bitwise = 1;
	/** number of bit that will mask all the others **/
	protected static final int max_bitwise = 32;
	
	/* attributes */
	/** whether to optimize constraint **/
	private boolean opt_constraint;
	/** to generate random integer **/
	private Random random;
	
	/* constructor */
	/**
	 * create a state infection machine without optimizing constraints
	 */
	public StateInfection() { this.opt_constraint = false; random = new Random(); }
	
	/* parameter API method */
	/**
	 * open the optimization of symbolic constraint
	 */
	public void open_optimize_constraint() { this.opt_constraint = true; }
	/**
	 * close the optimization of symbolic constraint
	 */
	public void close_optimize_constraint() { this.opt_constraint = false; }
	
	/* parsing methods */
	/**
	 * get the statement where the fault is seeded
	 * @param cir_tree
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected abstract CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception;
	/**
	 * generate the infections of {state_error, constraints} directly caused by the mutation
	 * @param cir_tree
	 * @param mutation
	 * @param graph
	 * @return
	 * @throws Exception
	 */
	protected abstract void get_infections(CirTree cir_tree, AstMutation mutation, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception;
	/**
	 * generate the infection subgraph from specified mutation
	 * @param cir_tree
	 * @param mutation
	 * @param dgraph
	 * @return
	 * @throws Exception
	 */
	public StateErrorGraph parse(CirTree cir_tree, AstMutation mutation, CDominanceGraph dgraph) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(dgraph == null)
			throw new IllegalArgumentException("No dominance provided");
		else {
			/* initialization */
			CirStatement statement = this.get_location(cir_tree, mutation);
			if(statement == null) return null; /* unreachable nodes in graph */
			StateErrorGraph graph = new StateErrorGraph();
			
			/* entry --> {path_constraints} --> reach_node */
			StateConstraints path_constraints = PathConditions.path_constraints(statement, dgraph);
			StateErrorNode reach_node = graph.new_node(graph.get_error_set().execute(statement));
			graph.get_beg_node().propagate(reach_node, path_constraints);
			
			/* reach_node -- {constraints} --> infect_node+ */
			Map<StateError, StateConstraints> infections = new HashMap<StateError, StateConstraints>();
			this.get_infections(cir_tree, mutation, graph, infections);
			for(StateError error : infections.keySet()) {
				StateConstraints constraints = infections.get(error);
				StateErrorNode target = graph.new_node(error);
				reach_node.propagate(target, constraints);
			}
			
			/* return the state error graph */	return graph;
		}
	}
	
	/* tool methods */
	/**
	 * get the representative location where the fault is seeded
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected AstNode get_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}
	/**
	 * get the true location representing the original source node
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected AstNode get_location(AstNode location) throws Exception {
		if(location instanceof AstConstExpression) {
			return this.get_location(((AstConstExpression) location).get_expression());
		}
		else if(location instanceof AstParanthExpression) {
			return this.get_location(((AstParanthExpression) location).get_sub_expression());
		}
		else if(location instanceof AstInitializer) {
			AstInitializer initializer = (AstInitializer) location;
			if(initializer.is_body())
				return this.get_location(initializer.get_body());
			else return this.get_location(initializer.get_expression());
		}
		else {
			return location;
		}
	}
	/**
	 * get the begining statement in the range of source node
	 * @param cir_tree
	 * @param node
	 * @return
	 * @throws Exception
	 */
	protected CirStatement get_beg_statement(CirTree cir_tree, AstNode node) throws Exception {
		if(cir_tree.has_cir_range(node)) {
			AstCirPair range = cir_tree.get_cir_range(node);
			return range.get_beg_statement();
		}
		else return null;
	}
	/**
	 * get the final statement in the range of source node
	 * @param cir_tree
	 * @param node
	 * @return
	 * @throws Exception
	 */
	protected CirStatement get_end_statement(CirTree cir_tree, AstNode node) throws Exception {
		if(cir_tree.has_cir_range(node)) {
			AstCirPair range = cir_tree.get_cir_range(node);
			return range.get_end_statement();
		}
		else return null;
	}
	/**
	 * get the result that represents the source node in code
	 * @param cir_tree
	 * @param node
	 * @return
	 * @throws Exception
	 */
	protected CirExpression get_result_of(CirTree cir_tree, AstNode node) throws Exception {
		if(cir_tree.has_cir_range(node)) {
			AstCirPair range = cir_tree.get_cir_range(node);
			return range.get_result();
		}
		else return null;
	}
	/**
	 * get the reachable node representing covering faulty statement
	 * @param graph
	 * @return
	 * @throws Exception
	 */
	protected StateErrorNode get_reach_node(StateErrorGraph graph) throws Exception {
		for(StateErrorEdge edge : graph.get_beg_node().get_ou_edges())
			return edge.get_target();
		return null;
	}
	/**
	 * add the constraint to the tail of the constraints sequence
	 * @param constraints
	 * @param constraint
	 * @throws Exception
	 */
	protected void add_constraint(StateConstraints constraints, 
			CirStatement statement, SymExpression constraint) throws Exception {
		StateEvaluation.add_constraint(constraints, statement, constraint, this.opt_constraint);
	}
	/**
	 * get all the statements within the AST node range
	 * @param cir_tree
	 * @param node
	 * @return
	 * @throws Exception
	 */
	protected Set<CirStatement> collect_statements_in(CirTree cir_tree, AstNode node) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		Set<CirStatement> statements = new HashSet<CirStatement>();
		
		queue.add(node);
		while(!queue.isEmpty()) {
			AstNode parent = queue.poll();
			for(int k = 0; k < parent.number_of_children(); k++)
				queue.add(parent.get_child(k));
			
			if(cir_tree.has_cir_range(parent)) {
				AstCirPair range = cir_tree.get_cir_range(parent);
				if(range.executional()) {
					statements.add(range.get_beg_statement());
					statements.add(range.get_end_statement());
				}
			}
		}
		
		return statements;
	}
	/**
	 * perform complete evaluation --> set_numb | set_bool | set_addr
	 * @param expression
	 * @param replace
	 * @param graph
	 * @param output
	 * @return
	 * @throws Exception
	 */
	protected boolean complete_evaluate(CirExpression expression, SymExpression replace, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object orig_constant = StateEvaluation.get_constant_value(expression);
		Object muta_constant = StateEvaluation.get_constant_value(replace);
		
		if(muta_constant.toString().equals(orig_constant.toString())) { 
			/** equivalent mutant being detected **/ 	return true;
		}
		else if(muta_constant instanceof Boolean) {
			output.put(
					graph.get_error_set().set_bool(expression, ((Boolean) muta_constant).booleanValue()), 
					StateEvaluation.get_conjunctions());
			return true;	/** set_bool for boolean constant **/
		}
		else if(muta_constant instanceof Long) {
			output.put(
					graph.get_error_set().set_numb(expression, ((Long) muta_constant).longValue()), 
					StateEvaluation.get_conjunctions());
			return true; 	/** set_numb for numeric constant **/
		}
		else if(muta_constant instanceof Double) {
			output.put(
					graph.get_error_set().set_numb(expression, ((Double) muta_constant).doubleValue()), 
					StateEvaluation.get_conjunctions());
			return true; 	/** set_numb for numeric constant **/
		}
		else if(muta_constant instanceof String) {
			output.put(
					graph.get_error_set().set_addr(expression, muta_constant.toString()), 
					StateEvaluation.get_conjunctions());
			return true; 	/** set_addr for address constant **/
		}
		else {
			/** undecidable for complete evaluation **/	return false;
		}
	}
	/**
	 * get the random value of address
	 * @return
	 */
	protected long random_address() {
		return 1 + Math.abs(this.random.nextLong()) % (1024 * 8);
	}
	/**
	 * get the number bit-1 in the bitwise sequence
	 * @param value
	 * @return
	 */
	private int number_of_bit1(long value) {
		int number = 0;
		while(value != 0) {
			if(value % 2 == 1) {
				number = number + 1;
			}
			value = (value >> 1);
		}
		return number;
	}
	/**
	 * whether only few of bit-1 in the number
	 * @param value
	 * @return
	 */
	protected boolean few_bits_number(long value) {
		return this.number_of_bit1(value) <= min_bitwise;
	}
	/**
	 * whether almost every bit-1 in the number
	 * @param value
	 * @return
	 */
	protected boolean big_bits_number(long value) {
		return this.number_of_bit1(value) >= max_bitwise;
	}
	
}
