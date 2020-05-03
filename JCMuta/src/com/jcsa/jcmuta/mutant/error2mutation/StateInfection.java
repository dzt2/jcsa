package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymEvaluator;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;
import com.jcsa.jcparse.lang.symb.impl.StandardSymEvaluator;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceGraph;

/**
 * Used to create infection of state errors.
 * 
 * @author yukimula
 *
 */
public abstract class StateInfection {
	
	/** to represent the value of any integer **/
	protected static final String AnyInteger = "AnyInt";
	
	/* common tool-kit */
	/** data type factory **/
	protected static final CTypeFactory tfactory = new CTypeFactory();
	/** used to evaluate and optimize symbolic constraint before added to constraints **/
	private static final SymEvaluator evaluator = StandardSymEvaluator.new_evaluator();
	
	/* attributes */
	/** whether to optimize constraint **/
	private boolean opt_constraint;
	
	/* constructor */
	/**
	 * create a state infection machine without optimizing constraints
	 */
	public StateInfection() { this.opt_constraint = false; }
	
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
	 * return condition == value as boolean condition way
	 * @param source
	 * @param value
	 * @return
	 * @throws Exception
	 */
	protected SymExpression get_sym_condition(CirExpression source, boolean value) throws Exception {
		SymExpression operand = SymFactory.parse(source);
		CType type = CTypeAnalyzer.get_value_type(source.get_data_type());
		
		if(value) {
			/* operand */
			if(CTypeAnalyzer.is_boolean(type)) {
				return operand;
			}
			/* operand != 0 */
			else if(CTypeAnalyzer.is_number(type)) {
				return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
						COperator.not_equals, operand, SymFactory.new_constant(0L));
			}
			/* operand != null */
			else if(CTypeAnalyzer.is_pointer(type)) {
				return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
						COperator.not_equals, operand, 
						SymFactory.new_address(StateError.NullPointer, type));
			}
			else {
				throw new IllegalArgumentException("Invalid: " + type);
			}
		}
		else {
			/* !operand */
			if(CTypeAnalyzer.is_boolean(type)) {
				return SymFactory.new_unary_expression(
						CBasicTypeImpl.bool_type, COperator.logic_not, operand);
			}
			/* operand == 0 */
			else if(CTypeAnalyzer.is_number(type)) {
				return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
						COperator.equal_with, operand, SymFactory.new_constant(0L));
			}
			/* operand == null */
			else if(CTypeAnalyzer.is_pointer(type)) {
				return SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
						COperator.equal_with, operand, 
						SymFactory.new_address(StateError.NullPointer, type));
			}
			else {
				throw new IllegalArgumentException("Invalid: " + type);
			}
		}
	}
	/**
	 * generate the optimized or standardized symbolic constrant
	 * @param constraint
	 * @return
	 * @throws Exception
	 */
	protected SymExpression derive_sym_constraint(SymExpression constraint) throws Exception {
		if(this.opt_constraint)
			return evaluator.evaluate(constraint);
		else
			return constraint; // get original constraint
	}
	/**
	 * get all the statements within the code range of AST source node
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
			
			CirStatement beg = this.get_beg_statement(cir_tree, parent);
			CirStatement end = this.get_end_statement(cir_tree, parent);
			if(beg != null) statements.add(beg);
			if(end != null) statements.add(end);
			
			for(int k = 0; k < parent.number_of_children(); k++) 
				queue.add(parent.get_child(k));
		}
		
		return statements;
	}
	/**
	 * bool | long | double
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	protected Object get_constant_value(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return constant.get_bool();
		case c_char:
		case c_uchar:
		{
			return Long.valueOf(constant.get_char().charValue());
		}
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
		{
			return Long.valueOf(constant.get_integer().intValue());
		}
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return constant.get_long();
		case c_float:
		{
			return Double.valueOf(constant.get_float().doubleValue());
		}
		case c_double:
		case c_ldouble:	return constant.get_double();
		default: throw new IllegalArgumentException("Invalid: " + constant);
		}
	}
	
}
