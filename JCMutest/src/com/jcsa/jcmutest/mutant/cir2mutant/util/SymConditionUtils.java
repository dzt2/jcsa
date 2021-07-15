package com.jcsa.jcmutest.mutant.cir2mutant.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.base.SymConditions;
import com.jcsa.jcmutest.mutant.cir2mutant.base.SymOperator;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolNode;

/**
 * It implements the extension and inference on symbolic condition
 * 
 * @author yukimula
 *
 */
public class SymConditionUtils {
	
	/* single */ /** constructors **/ private SymConditionUtils() { }
	private static SymConditionUtils utils = new SymConditionUtils();
	
	/* basic methods */
	/**
	 * collect all the symbolic references in the node subtree
	 * @param node
	 * @param references
	 */
	private void get_symbolic_references_in(SymbolNode node, Collection<SymbolExpression> references) {
		/* append the reference node */
		if(node.is_reference()) {
			references.add((SymbolExpression) node);
		}
		/* recursively collecting in */
		for(SymbolNode child : node.get_children()) {
			this.get_symbolic_references_in(child, references);
		}
	}
	/**
	 * @param node
	 * @return all the symbolic references in the subtree of node 
	 */
	private Collection<SymbolExpression> get_symbolic_references_in(SymbolNode node) {
		Collection<SymbolExpression> references = new HashSet<SymbolExpression>();
		this.get_symbolic_references_in(node, references); return references;
	}
	/**
	 * @param node
	 * @param references
	 * @return true if any reference in the node is contained in the references set.
	 */
	private boolean has_symbolic_references_in(SymbolNode node, Collection<SymbolExpression> references) {
		if(references == null || references.isEmpty()) {
			return false;	/* no reference is used in the node */
		}
		else if(references.contains(node)) { return true; }
		else {
			for(SymbolNode child : node.get_children()) {
				if(this.has_symbolic_references_in(child, references)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether any references is defined or filtered in the statement of the execution node
	 * @throws Exception
	 */
	private boolean has_symbolic_references_in(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
		if(references == null || references.isEmpty()) {
			return false;	/* no reference is used in the node */
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirAssignStatement) {
				return references.contains(SymbolFactory.sym_expression(
						((CirAssignStatement) statement).get_lvalue()));
			}
			else if(statement instanceof CirIfStatement) {
				return this.has_symbolic_references_in(SymbolFactory.sym_expression(
						((CirIfStatement) statement).get_condition()), references);
			}
			else if(statement instanceof CirCaseStatement) {
				return this.has_symbolic_references_in(SymbolFactory.sym_expression(
						((CirCaseStatement) statement).get_condition()), references);
			}
			else {
				return false;
			}
		}
	}
	/**
	 * collect the symbolic conditions in the conjunction of the source condition
	 * @param condition
	 * @param conditions
	 * @throws Exception
	 */
	private void get_symbolic_conditions_in(SymbolExpression condition, Collection<SymbolExpression> conditions) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				/* ignore the TRUE constant */
			}
			else {
				conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			if(operator == COperator.logic_and) {
				this.get_symbolic_conditions_in(loperand, conditions);
				this.get_symbolic_conditions_in(roperand, conditions);
			}
			else {
				conditions.add(SymbolFactory.sym_condition(condition, true));
			}
		}
		else {
			conditions.add(SymbolFactory.sym_condition(condition, true));
		}
	}
	/**
	 * @param condition
	 * @return the execution point to which the condition {cov_stmt|eva_expr} can be improved toward
	 * @throws Exception
	 */
	private SymCondition improve_condition_on_path(SymCondition condition) throws Exception {
		if(condition.get_operator() == SymOperator.cov_stmt) {
			/** declarations **/
			CirExecution execution = condition.get_execution();
			int times = ((SymbolConstant) condition.get_parameter()).get_int().intValue();
			
			/** improve until the first decidable branch to unify **/
			CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = prev_path.get_reverse_edges();
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				CirStatement statement = edge.get_source().get_statement();
				if(statement instanceof CirIfStatement || statement instanceof CirCaseStatement) {
					return SymConditions.cov_stmt(edge.get_target(), times);
				}
			}
			return SymConditions.cov_stmt(prev_path.get_source(), times);
		}
		else if(condition.get_operator() == SymOperator.eva_expr) {
			/** declarations **/
			CirExecution execution = condition.get_execution();
			SymbolExpression expression = condition.get_parameter();
			
			/** improve const condition to program entry **/
			if(expression instanceof SymbolConstant) {
				CirFunction function = execution.get_graph().get_function().get_graph().get_main_function();
				if(function == null) { function = execution.get_graph().get_function(); }
				execution = function.get_flow_graph().get_entry();
				expression = SymbolFactory.sym_constant(((SymbolConstant) expression).get_bool());
				return SymConditions.eva_expr(execution, Boolean.FALSE, true);
			}
			/** improve non-const condition on decidable path **/
			else {
				CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
				Iterator<CirExecutionEdge> iterator = prev_path.get_reverse_edges();
				Collection<SymbolExpression> references = this.get_symbolic_references_in(expression);
				while(iterator.hasNext()) {
					CirExecutionEdge edge = iterator.next();
					if(this.has_symbolic_references_in(edge.get_source(), references)) {
						return SymConditions.eva_expr(edge.get_target(), condition, true);
					}
				}
				return SymConditions.eva_expr(prev_path.get_source(), condition, true);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid condition: null");
		}
	}
	
	/* local extension methods */
	
	
	
	
	
	
	
	
	
	
	
}
