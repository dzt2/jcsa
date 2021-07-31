package com.jcsa.jcmutest.mutant.cir2mutant.enco;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

public class CirAnnotations {
	
	/* basic supporting methods */
	/**
	 * recursively collect the symbolic references under the node
	 * @param node
	 * @param references to preserve the output references being collected
	 */
	private void get_symbol_references_in(SymbolNode node, Collection<SymbolExpression> references) {
		if(node.is_reference()) references.add((SymbolExpression) node);
		for(SymbolNode child : node.get_children()) {
			this.get_symbol_references_in(child, references);
		}
	}
	/**
	 * @param node
	 * @return the set of references defined in the node
	 */
	private Collection<SymbolExpression> get_symbol_references_in(SymbolNode node) {
		Set<SymbolExpression> references = new HashSet<SymbolExpression>();
		this.get_symbol_references_in(node, references); return references;
	}
	/**
	 * @param node
	 * @param references
	 * @return whether there is reference used in the node
	 */
	private boolean has_symbol_references_in(SymbolNode node, Collection<SymbolExpression> references) {
		if(references.isEmpty()) {
			return false;
		}
		else if(references.contains(node)) {
			return true;
		}
		else {
			for(SymbolNode child : node.get_children()) {
				if(this.has_symbol_references_in(child, references)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether any references is limited in the execution (IF|CASE|ASSIGN)
	 */
	private boolean has_symbol_references_in(CirExecution execution,  Collection<SymbolExpression> references) throws Exception {
		if(references.isEmpty()) {
			return false;
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirAssignStatement) {
				return references.contains(SymbolFactory.
						sym_expression(((CirAssignStatement) statement).get_lvalue()));
			}
			else if(statement instanceof CirIfStatement) {
				return this.has_symbol_references_in(SymbolFactory.sym_expression(
						((CirIfStatement) statement).get_condition()), references);
			}
			else if(statement instanceof CirCaseStatement) {
				return this.has_symbol_references_in(SymbolFactory.sym_expression(
						((CirCaseStatement) statement).get_condition()), references);
			}
			else {
				return false;
			}
		}
	}
	/**
	 * recursive collect the symbolic conditions defined in logical conjunction
	 * @param expression
	 * @param expressions
	 * @throws Exception
	 */
	private void get_symbol_conditions_in(SymbolExpression expression, Collection<SymbolExpression> expressions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				/* ignore TRUE since it is equivalent to cov_stmt(1) */
			}
			else {
				expressions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.logic_and) {
				this.get_symbol_conditions_in(((SymbolBinaryExpression) expression).get_loperand(), expressions);
				this.get_symbol_conditions_in(((SymbolBinaryExpression) expression).get_roperand(), expressions);
			}
			else {
				expressions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
		else {
			expressions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * @param condition
	 * @return the improved constraint-based symbolic condition on execution path for uniform
	 * @throws Exception
	 */
	private CirAnnotation improve_constraint_condition_on_path(CirAnnotation condition) throws Exception {
		/* coverage improved to the head of the first branch */
		if(condition.get_operator() == CirAnnotationType.cov_stmt) {
			/** declarations **/
			CirExecution execution = condition.get_execution();
			int times = ((SymbolConstant) condition.get_parameter()).get_int().intValue();
			
			/** improve until the first decidable branch to unify **/
			CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				CirStatement statement = edge.get_source().get_statement();
				if(statement instanceof CirIfStatement || statement instanceof CirCaseStatement) {
					return CirAnnotation.cov_stmt(edge.get_target(), times);
				}
			}
			return CirAnnotation.cov_stmt(prev_path.get_source(), times);
		}
		/* improve evaluation based on decidable path to available node */
		else if(condition.get_operator() == CirAnnotationType.eva_expr) {
			/** declarations **/
			CirExecution execution = condition.get_execution();
			SymbolExpression expression = condition.get_parameter();
			
			/** improve const condition to program entry **/
			if(expression instanceof SymbolConstant) {
				CirFunction function = execution.get_graph().get_function().get_graph().get_main_function();
				if(function == null) { function = execution.get_graph().get_function(); }
				execution = function.get_flow_graph().get_entry();
				expression = SymbolFactory.sym_constant(((SymbolConstant) expression).get_bool());
				return CirAnnotation.eva_expr(execution, expression);
			}
			/** improve non-const condition on decidbale path **/
			else {
				CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
				Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
				Collection<SymbolExpression> references = this.get_symbol_references_in(expression);
				while(iterator.hasNext()) {
					CirExecutionEdge edge = iterator.next();
					if(this.has_symbol_references_in(edge.get_source(), references)) {
						return CirAnnotation.eva_expr(edge.get_target(), expression);
					}
				}
				return CirAnnotation.eva_expr(prev_path.get_source(), expression);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + condition);
		}
	}
	
	/* extension algorithms */
	
	
	
}
