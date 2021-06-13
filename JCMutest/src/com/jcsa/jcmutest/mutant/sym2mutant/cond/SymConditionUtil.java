package com.jcsa.jcmutest.mutant.sym2mutant.cond;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
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
 * Used to construct tree from symbolic instance under evaluation context.
 * 
 * @author yukimula
 *
 */
class SymConditionUtil {
	
	/* single mode */	/** construct **/ private SymConditionUtil() { }
	private static final SymConditionUtil util = new SymConditionUtil();
	
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
	private SymCondition improve_constraint_condition_on_path(SymCondition condition) throws Exception {
		/* coverage improved to the head of the first branch */
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
					return SymCondition.cov_stmt(edge.get_target(), times);
				}
			}
			return SymCondition.cov_stmt(prev_path.get_source(), times);
		}
		/* improve evaluation based on decidable path to available node */
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
				return SymCondition.eva_expr(execution, expression);
			}
			/** improve non-const condition on decidbale path **/
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
		else {
			throw new IllegalArgumentException("Invalid: " + condition);
		}
	}
	
	/* recursive extension method in local instance */
	/**
	 * extend on the 
	 * @param tree
	 * @throws Exception
	 */
	protected static void construct_tree(SymConditionTree tree) throws Exception {
		util.recursive_extend_on_node(tree.get_root());
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void recursive_extend_on_node(SymConditionNode source) throws Exception {
		switch(source.get_condition().get_operator()) {
		case cov_stmt:	this.extend_on_cov_stmt(source); break;
		case eva_expr:	this.extend_on_eva_expr(source); break;
		case add_stmt:	this.extend_on_add_stmt(source); break;
		case del_stmt:	this.extend_on_del_stmt(source); break;
		case trp_stmt:	this.extend_on_trp_stmt(source); break;
		case mut_flow:	this.extend_on_mut_flow(source); break;
		case mut_expr:	
		case mut_refr:	
		case mut_stat:	this.extend_on_mut_expr(source); break;
		case set_bool:	this.extend_on_set_bool(source); break;
		case set_true:	this.extend_on_set_true(source); break;
		case set_fals:	this.extend_on_set_fals(source); break;
		case chg_bool:	this.extend_on_chg_bool(source); break;
		case set_numb:	this.extend_on_set_numb(source); break;
		case set_post:	this.extend_on_set_post(source); break;
		case set_negt:	this.extend_on_set_negt(source); break;
		case set_zero:	this.extend_on_set_zero(source); break;
		case set_npos:	this.extend_on_set_npos(source); break;
		case set_nneg:	this.extend_on_set_nneg(source); break;
		case set_nzro:	this.extend_on_set_nzro(source); break;
		case inc_scop:	this.extend_on_inc_scop(source); break;
		case dec_scop:	this.extend_on_dec_scop(source); break;
		case ext_scop:	this.extend_on_ext_scop(source); break;
		case shk_scop:	this.extend_on_shk_scop(source); break;
		case chg_numb:	this.extend_on_chg_numb(source); break;
		case set_addr:	this.extend_on_set_addr(source); break;
		case set_null:	this.extend_on_set_null(source); break;
		case set_invp:	this.extend_on_set_invp(source); break;
		case chg_addr:	this.extend_on_chg_addr(source); break;
		case set_auto:	this.extend_on_set_auto(source); break;
		case chg_auto:	this.extend_on_chg_auto(source); break;
		default:	throw new IllegalArgumentException(source.get_condition().toString());
		}
	}
	
	/* basic construction */
	/**
	 * cov(S, N) --> cov(iS, N) --> cov(iS, N - 1) --> ... --> cov(iS, 1)
	 * @param source
	 * @throws Exception
	 */
	private void extend_on_cov_stmt(SymConditionNode source) throws Exception {
		/* cov(S, N) --> cov(iS, N) */
		source = source.add_child(this.improve_constraint_condition_on_path(source.get_condition()));
		
		/* generate smaller looping times */
		int times = ((SymbolConstant) source.get_condition().get_parameter()).get_int();
		List<Integer> loop_times = new ArrayList<Integer>();
		for(int k = 1; k < times; k = k * 2) { loop_times.add(Integer.valueOf(k)); }
		
		/* cov(iS, N) --> cov(iS, N - 1) */
		CirExecution execution = source.get_condition().get_execution();
		for(int k = loop_times.size() - 1; k >= 0; k--) {
			int loop_time = loop_times.get(k).intValue();
			source = source.add_child(SymCondition.cov_stmt(execution, loop_time));
		}
	}
	/**
	 * eva(S, E) --> {eva(iS, iE)}+ --> {eva(iS, siE)}* --> cov(S, 1)
	 * @param source
	 * @throws Exception
	 */
	private void extend_on_eva_expr(SymConditionNode source) throws Exception {
		/* declarations */
		CirExecution execution = source.get_condition().get_execution();
		SymbolExpression expression = source.get_condition().get_parameter();
		Collection<SymbolExpression> expressions = new HashSet<SymbolExpression>();
		Collection<SymConditionNode> children = new HashSet<SymConditionNode>();
		
		/* divide into sub-expressions for conjunction case */
		this.get_symbol_conditions_in(expression, expressions);
		for(SymbolExpression sub_expression : expressions) {
			SymCondition next_condition = SymCondition.eva_expr(execution, sub_expression);
			SymCondition improved = this.improve_constraint_condition_on_path(next_condition);
			children.add(source.add_child(improved));
		}
		if(children.contains(source)) { children.remove(source); }	/* remove invalid case */
		
		/* source --> cov(S, 1) */
		if(children.isEmpty()) {
			this.recursive_extend_on_node(source.add_child(SymCondition.cov_stmt(execution, 1)));
		}
		/* source --> child --> subsumed* --> cov */
		else {
			for(SymConditionNode child : children) {
				execution = child.get_condition().get_execution();
				expression = child.get_condition().get_parameter();
				if(expression instanceof SymbolBinaryExpression) {
					COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
					SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
					SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
					if(operator == COperator.smaller_tn) {
						this.recursive_extend_on_node(child.add_child(SymCondition.eva_expr(
								execution, SymbolFactory.not_equals(loperand, roperand))));
						this.recursive_extend_on_node(child.add_child(SymCondition.eva_expr(
								execution, SymbolFactory.smaller_eq(loperand, roperand))));
					}
					else if(operator == COperator.greater_tn) {
						this.recursive_extend_on_node(child.add_child(SymCondition.eva_expr(
								execution, SymbolFactory.not_equals(loperand, roperand))));
						this.recursive_extend_on_node(child.add_child(SymCondition.eva_expr(
								execution, SymbolFactory.greater_eq(loperand, roperand))));
					}
					else if(operator == COperator.equal_with) {
						this.recursive_extend_on_node(child.add_child(SymCondition.eva_expr(
								execution, SymbolFactory.smaller_eq(loperand, roperand))));
						this.recursive_extend_on_node(child.add_child(SymCondition.eva_expr(
								execution, SymbolFactory.greater_eq(loperand, roperand))));
					}
					else {
						this.recursive_extend_on_node(child.add_child(SymCondition.cov_stmt(execution, 1)));
					}
				}
				else {
					this.recursive_extend_on_node(child.add_child(SymCondition.cov_stmt(execution, 1)));
				}
			}
		}
	}
	private void extend_on_add_stmt(SymConditionNode source) throws Exception { }
	private void extend_on_del_stmt(SymConditionNode source) throws Exception { }
	private void extend_on_trp_stmt(SymConditionNode source) throws Exception { }
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_on_mut_flow(SymConditionNode source) throws Exception {
		/* determine the original and mutated target execution */
		CirExecution orig_target = SymCondition.execution_of(source.get_condition().get_location());
		CirExecution muta_target = (CirExecution) source.get_condition().get_parameter().get_source();
		
		/* compute the statements being added or deleted in testing */
		Collection<CirExecution> add_executions = new HashSet<CirExecution>();
		Collection<CirExecution> del_executions = new HashSet<CirExecution>();
		CirExecutionPath orig_path = CirExecutionPathFinder.finder.df_extend(orig_target);
		CirExecutionPath muta_path = CirExecutionPathFinder.finder.df_extend(muta_target);
		for(CirExecutionEdge edge : muta_path.get_edges()) { add_executions.add(edge.get_source()); }
		for(CirExecutionEdge edge : orig_path.get_edges()) { del_executions.add(edge.get_source()); }
		add_executions.add(muta_path.get_target()); del_executions.add(orig_path.get_target());
		
		/* removed the common part for corrections */
		Collection<CirExecution> com_executions = new HashSet<CirExecution>();
		for(CirExecution execution : add_executions) {
			if(del_executions.contains(execution)) {
				com_executions.add(execution);
			}
		}
		add_executions.removeAll(com_executions);
		del_executions.removeAll(com_executions);
		
		/* extending to add_stmt | del_stmt */
		for(CirExecution add_execution : add_executions) {
			this.recursive_extend_on_node(source.add_child(SymCondition.add_stmt(add_execution)));
		}
		for(CirExecution del_execution : del_executions) {
			this.recursive_extend_on_node(source.add_child(SymCondition.del_stmt(del_execution)));
		}
	}
	/**
	 * --> set_bool|set_auto|set_addr|set_numb based on type
	 * @param source
	 * @throws Exception
	 */
	private void extend_on_mut_expr(SymConditionNode source) throws Exception {
		/* declarations */
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		SymbolExpression muta_value = source.get_condition().get_parameter();
		
		/* categorization based on type */
		if(SymCondition.is_boolean(expression)) {
			this.recursive_extend_on_node(source.add_child(SymCondition.set_bool(expression, muta_value)));
		}
		else if(SymCondition.is_numeric(expression)) {
			this.recursive_extend_on_node(source.add_child(SymCondition.set_numb(expression, muta_value)));
		}
		else if(SymCondition.is_address(expression)) {
			this.recursive_extend_on_node(source.add_child(SymCondition.set_addr(expression, muta_value)));
		}
		else {
			this.recursive_extend_on_node(source.add_child(SymCondition.set_auto(expression, muta_value)));
		}
	}
	
	/* boolean construction */
	/**
	 * set_bool --> {set_true|set_fals} --> chg_bool
	 * @param source
	 * @throws Exception
	 */
	private void extend_on_set_bool(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		SymbolExpression muta_value = source.get_condition().get_parameter().evaluate(null);
		if(muta_value instanceof SymbolConstant) {
			if(((SymbolConstant) muta_value).get_bool()) {
				this.recursive_extend_on_node(source.add_child(SymCondition.set_true(expression)));
			}
			else {
				this.recursive_extend_on_node(source.add_child(SymCondition.set_fals(expression)));
			}
		}
		else {
			this.recursive_extend_on_node(source.add_child(SymCondition.chg_bool(expression)));
		}
	}
	/**
	 * set_true --> chg_bool
	 * @param source
	 * @throws Exception
	 */
	private void extend_on_set_true(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.chg_bool(expression)));
	}
	/**
	 * set_fals --> chg_bool
	 * @param source
	 * @throws Exception
	 */
	private void extend_on_set_fals(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.chg_bool(expression)));
	}
	private void extend_on_chg_bool(SymConditionNode source) throws Exception { }
	
	/* numeric construction */
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_on_set_numb(SymConditionNode source) throws Exception {
		CirExecution execution = source.get_condition().get_execution();
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		SymbolExpression orig_value = SymbolFactory.sym_expression(expression).evaluate(null);
		SymbolExpression muta_value = source.get_condition().get_parameter().evaluate(null);
		SymbolExpression difference = SymbolFactory.
				arith_sub(expression.get_data_type(), muta_value, orig_value).evaluate(null);
		
		/* difference checking... */
		if(difference instanceof SymbolConstant) {
			Object diff_number = ((SymbolConstant) difference).get_number();
			if(diff_number instanceof Long) {
				if(((Long) diff_number).longValue() > 0) {
					this.recursive_extend_on_node(source.add_child(SymCondition.inc_scop(expression)));
				}
				else if(((Long) diff_number).longValue() < 0) {
					this.recursive_extend_on_node(source.add_child(SymCondition.dec_scop(expression)));
				}
				else {
					this.recursive_extend_on_node(source.add_child(SymCondition.eva_expr(execution, Boolean.FALSE)));
					return;
				}
			}
			else if(diff_number instanceof Double) {
				if(((Double) diff_number).doubleValue() > 0) {
					this.recursive_extend_on_node(source.add_child(SymCondition.inc_scop(expression)));
				}
				else if(((Double) diff_number).doubleValue() < 0) {
					this.recursive_extend_on_node(source.add_child(SymCondition.dec_scop(expression)));
				}
				else {
					this.recursive_extend_on_node(source.add_child(SymCondition.eva_expr(execution, Boolean.FALSE)));
					return;
				}
			}
		}
		
		/* two constant */
		if(orig_value instanceof SymbolConstant) {
			if(muta_value instanceof SymbolConstant) {
				Object xnumber = ((SymbolConstant) orig_value).get_number();
				Object ynumber = ((SymbolConstant) muta_value).get_number();
				if(xnumber instanceof Long) {
					long x = ((Long) xnumber).longValue();
					if(ynumber instanceof Long) {
						long y = ((Long) ynumber).longValue();
						if(Math.abs(y) > Math.abs(x)) {
							this.recursive_extend_on_node(source.add_child(SymCondition.ext_scop(expression)));
						}
						else if(Math.abs(y) < Math.abs(x)) {
							this.recursive_extend_on_node(source.add_child(SymCondition.shk_scop(expression)));
						}
					}
					else {
						double y = ((Double) ynumber).doubleValue();
						if(Math.abs(y) > Math.abs(x)) {
							this.recursive_extend_on_node(source.add_child(SymCondition.ext_scop(expression)));
						}
						else if(Math.abs(y) < Math.abs(x)) {
							this.recursive_extend_on_node(source.add_child(SymCondition.shk_scop(expression)));
						}
					}
				}
				else {
					double x = ((Double) xnumber).doubleValue();
					if(ynumber instanceof Long) {
						long y = ((Long) ynumber).longValue();
						if(Math.abs(y) > Math.abs(x)) {
							this.recursive_extend_on_node(source.add_child(SymCondition.ext_scop(expression)));
						}
						else if(Math.abs(y) < Math.abs(x)) {
							this.recursive_extend_on_node(source.add_child(SymCondition.shk_scop(expression)));
						}
					}
					else {
						double y = ((Double) ynumber).doubleValue();
						if(Math.abs(y) > Math.abs(x)) {
							this.recursive_extend_on_node(source.add_child(SymCondition.ext_scop(expression)));
						}
						else if(Math.abs(y) < Math.abs(x)) {
							this.recursive_extend_on_node(source.add_child(SymCondition.shk_scop(expression)));
						}
					}
				}
			}
		}
		
		/* muta-constant */
		if(muta_value instanceof SymbolConstant) {
			Object ynumber = ((SymbolConstant) muta_value).get_number();
			if(ynumber instanceof Long) {
				long y = ((Long) ynumber).longValue();
				if(y > 0) {
					this.recursive_extend_on_node(source.add_child(SymCondition.set_post(expression)));
				}
				else if(y < 0) {
					this.recursive_extend_on_node(source.add_child(SymCondition.set_negt(expression)));
				}
				else {
					this.recursive_extend_on_node(source.add_child(SymCondition.set_zero(expression)));
				}
			}
			else {
				double y = ((Double) ynumber).doubleValue();
				if(y > 0) {
					this.recursive_extend_on_node(source.add_child(SymCondition.set_post(expression)));
				}
				else if(y < 0) {
					this.recursive_extend_on_node(source.add_child(SymCondition.set_negt(expression)));
				}
				else {
					this.recursive_extend_on_node(source.add_child(SymCondition.set_zero(expression)));
				}
			}
		}
		
		if(source.is_leaf()) { this.recursive_extend_on_node(source.add_child(SymCondition.chg_numb(expression))); }
	}
	private void extend_on_set_post(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.set_nneg(expression)));
		this.recursive_extend_on_node(source.add_child(SymCondition.set_nzro(expression)));
	}
	private void extend_on_set_negt(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.set_npos(expression)));
		this.recursive_extend_on_node(source.add_child(SymCondition.set_nzro(expression)));
	}
	private void extend_on_set_zero(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.set_npos(expression)));
		this.recursive_extend_on_node(source.add_child(SymCondition.set_nneg(expression)));
	}
	private void extend_on_set_npos(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.chg_numb(expression)));
	}
	private void extend_on_set_nneg(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.chg_numb(expression)));
	}
	private void extend_on_set_nzro(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.chg_numb(expression)));
	}
	private void extend_on_inc_scop(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.chg_numb(expression)));
	}
	private void extend_on_ext_scop(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.chg_numb(expression)));
	}
	private void extend_on_shk_scop(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.chg_numb(expression)));
	}
	private void extend_on_dec_scop(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.chg_numb(expression)));
	}
	
	/* address construction */
	private void extend_on_set_addr(SymConditionNode source) throws Exception {
		CirExecution execution = source.get_condition().get_execution();
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		SymbolExpression orig_value = SymbolFactory.sym_expression(expression).evaluate(null);
		SymbolExpression muta_value = source.get_condition().get_parameter().evaluate(null);
		SymbolExpression difference = SymbolFactory.
				arith_sub(expression.get_data_type(), muta_value, orig_value).evaluate(null);
		
		if(difference instanceof SymbolConstant) {
			long dvalue = ((SymbolConstant) difference).get_long().longValue();
			if(dvalue > 0) {
				this.recursive_extend_on_node(source.add_child(SymCondition.inc_scop(expression)));
			}
			else if(dvalue < 0) {
				this.recursive_extend_on_node(source.add_child(SymCondition.dec_scop(expression)));
			}
			else {
				this.recursive_extend_on_node(source.add_child(SymCondition.eva_expr(execution, Boolean.FALSE)));
				return;
			}
		}
		
		if(muta_value instanceof SymbolConstant) {
			long mvalue = ((SymbolConstant) muta_value).get_long().longValue();
			if(mvalue == 0) {
				this.recursive_extend_on_node(source.add_child(SymCondition.set_null(expression)));
			}
			else {
				this.recursive_extend_on_node(source.add_child(SymCondition.set_invp(expression)));
			}
		}
		else {
			this.recursive_extend_on_node(source.add_child(SymCondition.set_invp(expression)));
		}
	}
	private void extend_on_set_null(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.chg_addr(expression)));
	}
	private void extend_on_set_invp(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.chg_addr(expression)));
	}
	private void extend_on_set_auto(SymConditionNode source) throws Exception {
		CirExpression expression = (CirExpression) source.get_condition().get_location();
		this.recursive_extend_on_node(source.add_child(SymCondition.chg_auto(expression)));
	}
	private void extend_on_chg_auto(SymConditionNode source) throws Exception { }
	private void extend_on_chg_numb(SymConditionNode source) throws Exception { }
	private void extend_on_chg_addr(SymConditionNode source) throws Exception { }
	
}
