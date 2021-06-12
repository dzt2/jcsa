package com.jcsa.jcmutest.mutant.sym2mutant.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.jcsa.jcmutest.mutant.sym2mutant.cond.SymConditions;
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
 * It implements the construction and evaluation of local symbolic tree node.
 * 
 * @author yukimula
 *
 */
public class SymConditionUtil {
	
	/* singleton pattern */		private SymConditionUtil() { } 
	private static final SymConditionUtil util = new SymConditionUtil();
	
	/* data flow analysis */
	/**
	 * collect all the symbolic references under the node
	 * @param node
	 * @param references to preserve the reference s
	 */
	private void get_symbol_references_in(SymbolNode node, Collection<SymbolExpression> references) {
		if(node.is_reference()) {
			references.add((SymbolExpression) node);
		}
		for(SymbolNode child : node.get_children()) {
			this.get_symbol_references_in(child, references);
		}
	}
	/**
	 * @param node
	 * @return the set of symbolic references in the node
	 */
	private Collection<SymbolExpression> get_symbol_references_in(SymbolNode node) {
		Collection<SymbolExpression> references = new HashSet<SymbolExpression>();
		this.get_symbol_references_in(node, references);
		return references;
	}
	/**
	 * @param expression
	 * @param references
	 * @return whether any reference in set is used in the expression
	 */
	private boolean has_symbol_references_in(SymbolNode expression, Collection<SymbolExpression> references) {
		if(references.isEmpty()) {
			return false;
		}
		else if(references.contains(expression)) {
			return true;
		}
		else {
			for(SymbolNode child : expression.get_children()) {
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
	 * @return whether any references is defined or limited in the execution
	 */
	private boolean has_symbol_references_in(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
		if(references.isEmpty()) {
			return false;
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirIfStatement) {
				return this.has_symbol_references_in(SymbolFactory.sym_expression(
						((CirIfStatement) statement).get_condition()), references);
			}
			else if(statement instanceof CirCaseStatement) {
				return this.has_symbol_references_in(SymbolFactory.sym_expression(
						((CirCaseStatement) statement).get_condition()), references);
			}
			else if(statement instanceof CirAssignStatement) {
				return references.contains(SymbolFactory.sym_expression(((CirAssignStatement) statement).get_lvalue()));
			}
			else {
				return false;
			}
		}
	}
	
	/* constraint constructions */
	/**
	 * collect the conditions in the expression (assumed as logical AND)
	 * @param expression
	 * @param expressions to preserve the set of expressions in conjunction
	 * @throws Exception
	 */
	private void get_symbol_conditions_in(SymbolExpression expression, Collection<SymbolExpression> expressions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				/* ignore TRUE since it is equivalent to cov_stmt */
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
	 * @return the improved condition over the path to the right closest prefix node
	 * @throws Exception
	 */
	private SymCondition improve_symbol_constraint_on_path(SymCondition condition) throws Exception {
		/* coverage operator */
		if(condition.get_operator() == SymOperator.cov_stmt) {
			CirExecution execution = condition.get_execution();
			int times = ((SymbolConstant) condition.get_parameter()).get_int().intValue();
			
			CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = prev_path.get_reverse_edges();
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				CirStatement statement = edge.get_source().get_statement();
				if(statement instanceof CirIfStatement || statement instanceof CirCaseStatement) {
					return SymCondition.cov_stmt(edge.get_target(), times);
				}
			}
			return SymCondition.cov_stmt(prev_path.get_source(), times);
		}
		/* evaluation operator */
		else {
			/* declarations */
			CirExecution execution = condition.get_execution();
			SymbolExpression expression = condition.get_parameter();
			
			/* improved the program entry */
			if(expression instanceof SymbolConstant) {
				CirFunction function = execution.get_graph().get_function().get_graph().get_main_function();
				if(function == null) { function = execution.get_graph().get_function(); }
				execution = function.get_flow_graph().get_entry();
				expression = SymbolFactory.sym_constant(((SymbolConstant) expression).get_bool());
				return SymCondition.eva_expr(execution, expression);
			}
			/* improved to non-defined point */
			else {
				CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
				Iterator<CirExecutionEdge> iterator = prev_path.get_reverse_edges();
				Collection<SymbolExpression> references = this.get_symbol_references_in(expression);
				while(iterator.hasNext()) {
					CirExecutionEdge edge = iterator.next();
					if(this.has_symbol_references_in(edge.get_source(), references)) {
						return SymCondition.eva_expr(edge.get_target(), expression);
					}
				}
				return SymCondition.eva_expr(prev_path.get_source(), expression);
			}
		}
	}
	
	
	
	
	
	
	
	
	
}
