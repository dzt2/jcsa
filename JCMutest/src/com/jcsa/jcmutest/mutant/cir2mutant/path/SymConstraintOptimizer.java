package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymBinaryExpression;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.lang.sym.SymNode;

/**
 * It implements the algorithm to optimize the SymConstraint used in mutation analysis.
 * 
 * @author yukimula
 *
 */
public class SymConstraintOptimizer {
	
	/* singleton mode */
	/** private constructor for singleton mode **/
	private SymConstraintOptimizer() { }
	/** the singleton instance of the optimizer used to optimize symbolic constraint **/
	public static final SymConstraintOptimizer optimizer = new SymConstraintOptimizer();
	
	/* symbolic expression normalization */
	/**
	 * @param expression
	 * @return whether the expression is logical AND
	 */
	private boolean is_conjunction(SymExpression expression) {
		if(expression instanceof SymBinaryExpression) {
			return ((SymBinaryExpression) expression).get_operator().get_operator() == COperator.logic_and;
		}
		else {
			return false;
		}
	}
	/**
	 * collect the conditions in the conjunction
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private boolean collect_conditions_in(SymExpression expression, Collection<SymExpression> conditions) throws Exception {
		if(expression instanceof SymConstant) {
			if(((SymConstant) expression).get_bool()) {
				return true;
			}
			else {
				conditions.clear();
				conditions.add(SymFactory.sym_constant(Boolean.FALSE));
				return false;
			}
		}
		else if(this.is_conjunction(expression)) {
			if(this.collect_conditions_in(((SymBinaryExpression) expression).get_loperand(), conditions)) {
				return this.collect_conditions_in(((SymBinaryExpression) expression).get_roperand(), conditions);
			}
			else {
				return false;
			}
		}
		else {
			conditions.add(SymFactory.sym_condition(expression, true));
			return true;
		}
	}
	/**
	 * @param cir_mutations
	 * @param constraint
	 * @return the set of constraints as elemental conditions in the constraint of source
	 * @throws Exception
	 */
	public Collection<SymConstraint> divide_in_constraints(CirMutations cir_mutations, SymConstraint constraint) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint as null");
		else {
			/* 1. collect the conditions in the conjunction of expression */
			Set<SymExpression> conditions = new HashSet<SymExpression>();
			this.collect_conditions_in(constraint.get_condition(), conditions);
			
			/* 2. generate the set of symbolic constraints w.r.t. the conditions */
			Set<SymConstraint> constraints = new HashSet<SymConstraint>();
			for(SymExpression condition : conditions) {
				constraints.add(cir_mutations.expression_constraint(
						constraint.get_statement(), condition, true));
			}
			constraints.add(cir_mutations.statement_constraint(constraint.get_statement(), 1));
			return constraints;
		}
	}
	
	/* path-aware constraint improvement */
	/**
	 * @param condition
	 * @param statement
	 * @return whether the symbolic expression in condition is defined in the statement
	 * @throws Exception
	 */
	private boolean is_defined(SymExpression condition, CirStatement statement) throws Exception {
		if(statement instanceof CirAssignStatement) {
			SymExpression definition = SymFactory.sym_expression(
					((CirAssignStatement) statement).get_lvalue());
			
			Queue<SymNode> queue = new LinkedList<SymNode>();
			queue.add(condition); SymNode sym_node;
			while(!queue.isEmpty()) {
				sym_node = queue.poll();
				for(SymNode child : sym_node.get_children()) {
					queue.add(child);
				}
				if(sym_node.equals(definition)) {
					return true;
				}
			}
			
			return false;
		}
		else {
			return false;
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @return whether the condition constains the reference of the execution
	 * @throws Exception
	 */
	private boolean is_statement_condition(CirExecution execution, SymExpression condition) throws Exception {
		SymExpression statement = SymFactory.sym_expression(execution);
		Queue<SymNode> queue = new LinkedList<SymNode>();
		queue.add(condition); SymNode sym_node;
		while(!queue.isEmpty()) {
			sym_node = queue.poll();
			for(SymNode child : sym_node.get_children()) {
				queue.add(child);
			}
			if(sym_node.equals(statement)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @param execution
	 * @param condition
	 * @return the statement point where the condition can reach to prior
	 * @throws Exception
	 */
	private CirExecution prev_reach_point(CirExecution execution, SymExpression condition) throws Exception {
		if(condition instanceof SymConstant) {
			return execution.get_graph().get_entry();
		}
		else if(this.is_statement_condition(execution, condition)) {
			return execution;
		}
		else {
			CirExecutionPath path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = path.get_reverse_edges();
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				CirExecution prev_node = edge.get_source();
				if(this.is_defined(condition, prev_node.get_statement())) {
					return edge.get_target();
				}
			}
			return path.get_source();
		}
	}
	/**
	 * @param cir_mutations
	 * @param constraint
	 * @return the constraint is improved to the statement where the condition can be evaluated earliest.
	 * @throws Exception
	 */
	public SymConstraint path_improvement(CirMutations cir_mutations, SymConstraint constraint) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations as null");
		else if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint as null");
		else {
			CirExecution execution = this.prev_reach_point(
					constraint.get_execution(), constraint.get_condition());
			return cir_mutations.expression_constraint(execution.
					get_statement(), constraint.get_condition(), true);
		}
	}
	
	/* constraint subsume based algorithm */
	/**
	 * generate the set of conditions subsuming the expression
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private void generate_subsume_set(SymExpression expression, Collection<SymExpression> conditions) throws Exception {
		if(expression instanceof SymBinaryExpression) {
			SymBinaryExpression bin_expression = (SymBinaryExpression) expression;
			COperator operator = ((SymBinaryExpression) expression).get_operator().get_operator();
			switch(operator) {
			case smaller_tn:
			{
				conditions.add(bin_expression);
				conditions.add(SymFactory.smaller_eq(bin_expression.get_loperand(), bin_expression.get_roperand()));
				conditions.add(SymFactory.not_equals(bin_expression.get_loperand(), bin_expression.get_roperand()));
				break;
			}
			case smaller_eq:
			{
				conditions.add(bin_expression);
				break;
			}
			case greater_tn:
			{
				conditions.add(SymFactory.smaller_tn(bin_expression.get_roperand(), bin_expression.get_loperand()));
				conditions.add(SymFactory.smaller_eq(bin_expression.get_roperand(), bin_expression.get_loperand()));
				conditions.add(SymFactory.not_equals(bin_expression.get_roperand(), bin_expression.get_loperand()));
				break;
			}
			case greater_eq:
			{
				conditions.add(SymFactory.smaller_eq(bin_expression.get_roperand(), bin_expression.get_loperand()));
				break;
			}
			case equal_with:
			{
				conditions.add(bin_expression);
				conditions.add(SymFactory.smaller_eq(bin_expression.get_loperand(), bin_expression.get_roperand()));
				conditions.add(SymFactory.smaller_eq(bin_expression.get_roperand(), bin_expression.get_loperand()));
				break;
			}
			case not_equals:
			{
				conditions.add(bin_expression);
				conditions.add(SymFactory.not_equals(bin_expression.get_roperand(), bin_expression.get_loperand()));
				conditions.add(SymFactory.smaller_tn(bin_expression.get_loperand(), bin_expression.get_roperand()));
				conditions.add(SymFactory.smaller_tn(bin_expression.get_roperand(), bin_expression.get_loperand()));
				break;
			}
			default:
			{
				conditions.add(expression);
				break;
			}
			}
		}
		else {
			conditions.add(expression);
		}
	}
	/**
	 * @param cir_mutations
	 * @param constraint
	 * @return the set of constraints subsumed from the original conditions (extended)
	 * @throws Exception
	 */
	public Collection<SymConstraint> subsume_constraints(CirMutations cir_mutations, SymConstraint constraint) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations as null");
		else if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint as null");
		else {
			Set<SymConstraint> constraints = new HashSet<SymConstraint>();
			Set<SymExpression> conditions = new HashSet<SymExpression>();
			this.generate_subsume_set(constraint.get_condition(), conditions);
			for(SymExpression condition : conditions) {
				constraints.add(cir_mutations.expression_constraint(constraint.get_statement(), condition, true));
			}
			return constraints;
		}
	}
	
}
