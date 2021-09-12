package com.jcsa.jcmutest.mutant.cir2mutant.cond;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBlockError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirCoverCount;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirDiferError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirFlowsError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirReferError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirTrapsError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirValueError;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolNode;

/**
 * It implements the generation, concretization and summarization of annotations
 * from CirAttribute or themselves.
 * 
 * @author yukimula
 *
 */
public class CirAnnotationUtils {
	
	/* singleton */	/** constructor **/	private CirAnnotationUtils() {}
	private static CirAnnotationUtils utils = new CirAnnotationUtils();
	
	/* symbolic condition division */
	/**
	 * It captures the sub-conditions within the input when taking it as the conjunctive expression
	 * @param condition
	 * @param conditions
	 * @throws Exception
	 */
	private void div_conditions_in_conjunct(SymbolExpression condition, Collection<SymbolExpression> conditions) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				/* ignore the true element in conjunction */
			}
			else {
				conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			if(op == COperator.logic_and) {
				this.div_conditions_in_conjunct(loperand, conditions);
				this.div_conditions_in_conjunct(roperand, conditions);
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
	 * It generates the set of conditions being subsumed from the input condition
	 * @param condition
	 * @param conditions
	 * @throws Exception
	 */
	private void div_conditions_by_subsumed(SymbolExpression condition, Collection<SymbolExpression> conditions) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				/* ignore the true part in subsumed conjunction */
			}
			else {
				conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			
			conditions.add(SymbolFactory.sym_condition(condition, true));
			if(op == COperator.greater_tn) {
				conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				conditions.add(SymbolFactory.not_equals(loperand, roperand));
			}
			else if(op == COperator.smaller_tn) {
				conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
				conditions.add(SymbolFactory.not_equals(loperand, roperand));
			}
			else if(op == COperator.equal_with) {
				conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
			}
		}
		else {
			conditions.add(SymbolFactory.sym_condition(condition, true));
		}
	}
	/**
	 * It captures the sub-conditions in the input and generates its subsumed conditions.
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_conditions_in(SymbolExpression condition) throws Exception {
		condition = CirValueScope.evaluate(condition, null);
		Set<SymbolExpression> sub_conditions = new HashSet<SymbolExpression>();
		this.div_conditions_in_conjunct(condition, sub_conditions);
		
		Set<SymbolExpression> sum_conditions = new HashSet<SymbolExpression>();
		for(SymbolExpression sub_condition : sub_conditions) {
			this.div_conditions_by_subsumed(sub_condition, sum_conditions);
		}
		return sum_conditions;
	}
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
		Set<SymbolExpression> references = new HashSet<>();
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
	
	/* reference based check-point */
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
	 * @param execution
	 * @param expression
	 * @return find the previous check-point where the expression should be evaluated
	 * @throws Exception
	 */
	private CirExecution find_prior_checkpoint(CirExecution execution, SymbolExpression expression) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
				Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
				while(iterator.hasNext()) {
					CirExecutionEdge edge = iterator.next();
					switch(edge.get_type()) {
					case true_flow:	return edge.get_target();
					case fals_flow:	return edge.get_target();
					default:		break;
					}
				}
				return prev_path.get_source();
			}
			else {
				return execution.get_graph().get_entry();
			}
		}
		else {
			CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
			Collection<SymbolExpression> references = this.get_symbol_references_in(expression);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(this.has_symbol_references_in(edge.get_source(), references)) {
					return edge.get_target();
				}
			}
			return prev_path.get_source();
		}
	}
	/**
	 * true --> add_executions; false --> del_executions;
	 * @param orig_target
	 * @param muta_target
	 * @return
	 * @throws Exception
	 */
	private Map<Boolean, Collection<CirExecution>>	get_add_del_executions(CirExecution orig_target, CirExecution muta_target) throws Exception {
		if(orig_target == null) {
			throw new IllegalArgumentException("Invalid orig_target: null");
		}
		else if(muta_target == null) {
			throw new IllegalArgumentException("Invalid muta_target: null");
		}
		else {
			/* compute the statements being added or deleted in testing */
			Collection<CirExecution> add_executions = new HashSet<>();
			Collection<CirExecution> del_executions = new HashSet<>();
			CirExecutionPath orig_path = CirExecutionPathFinder.finder.df_extend(orig_target);
			CirExecutionPath muta_path = CirExecutionPathFinder.finder.df_extend(muta_target);
			for(CirExecutionEdge edge : muta_path.get_edges()) { add_executions.add(edge.get_source()); }
			for(CirExecutionEdge edge : orig_path.get_edges()) { del_executions.add(edge.get_source()); }
			add_executions.add(muta_path.get_target()); del_executions.add(orig_path.get_target());

			/* removed the common part for corrections */
			Collection<CirExecution> com_executions = new HashSet<>();
			for(CirExecution execution : add_executions) {
				if(del_executions.contains(execution)) {
					com_executions.add(execution);
				}
			}
			add_executions.removeAll(com_executions);
			del_executions.removeAll(com_executions);

			/* construct mapping from true|false to collections */
			Map<Boolean, Collection<CirExecution>> results =
					new HashMap<>();
			results.put(Boolean.TRUE, add_executions);
			results.put(Boolean.FALSE, del_executions);
			return results;
		}
	}
	
	/* generation implementation */
	private void generate_annotations_in_constraints(CirConstraint attribute, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. capture the symbolic conditions required in constraint */
		CirExecution execution = attribute.get_execution(), check_point;
		Collection<SymbolExpression> conditions = this.get_conditions_in(attribute.get_condition());
		
		/* 2. generate the eva_expr annotations from the conditions */
		for(SymbolExpression condition : conditions) {
			check_point = this.find_prior_checkpoint(execution, condition);
			annotations.add(CirAnnotation.eva_expr(check_point, condition));
		}
		
		/* 3. insert the coverage requirement when it is necessary. */
		if(conditions.isEmpty()) {
			SymbolExpression condition = SymbolFactory.sym_constant(Boolean.TRUE);
			check_point = this.find_prior_checkpoint(execution, condition);
			annotations.add(CirAnnotation.cov_stmt(check_point, 1));
		}
	}
	private void generate_annotations_in_cover_count(CirCoverCount attribute, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. declarations */
		CirExecution execution = attribute.get_execution();
		int max_execution_times = attribute.get_coverage_count();
		List<Integer> execution_times = new ArrayList<Integer>();
		
		/* 2. capture the set of execution times for analysis */
		for(int k = 1; k < max_execution_times; k = k * 2) {
			execution_times.add(Integer.valueOf(k));
		}
		execution_times.add(Integer.valueOf(max_execution_times));
		
		/* 3. generate the annotations w.r.t. coverage statement */
		for(Integer execution_time : execution_times) {
			annotations.add(CirAnnotation.cov_stmt(execution, execution_time));
		}
	}
	private void generate_annotations_in_block_error(CirBlockError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		if(execution.get_statement() instanceof CirTagStatement) {
			return;
		}
		else if(attribute.is_executed()) {
			annotations.add(CirAnnotation.ori_stmt(execution, false));
			annotations.add(CirAnnotation.mut_stmt(execution, true));
		}
		else {
			annotations.add(CirAnnotation.ori_stmt(execution, true));
			annotations.add(CirAnnotation.mut_stmt(execution, false));
		}
	}
	private void generate_annotations_in_flows_error(CirFlowsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecutionFlow orig_flow = attribute.get_original_flow();
		CirExecutionFlow muta_flow = attribute.get_mutation_flow();
		
		if(orig_flow.get_target() != muta_flow.get_target()) {
			/* capture the statement error annotations from flow-error attribute */
			Map<Boolean, Collection<CirExecution>> results = this.
					get_add_del_executions(orig_flow.get_target(), muta_flow.get_target());
			
			/* generate the statement error annotations */
			int counter = 0;
			for(Boolean result : results.keySet()) {
				for(CirExecution execution : results.get(result)) {
					if(execution.get_statement() instanceof CirTagStatement) {
						continue;
					}
					else if(result) {
						annotations.add(CirAnnotation.ori_stmt(execution, false));
						annotations.add(CirAnnotation.mut_stmt(execution, true));
						counter++;
					}
					else {
						annotations.add(CirAnnotation.ori_stmt(execution, true));
						annotations.add(CirAnnotation.mut_stmt(execution, false));
						counter++;
					}
				}
			}
			
			/* generate the flow-based error annotation */
			if(counter > 0) {
				annotations.add(CirAnnotation.ori_flow(orig_flow));
				annotations.add(CirAnnotation.mut_flow(muta_flow));
			}
		}
	}
	private void generate_annotations_in_exprs_error(CirExpression expression, 
			SymbolExpression muta_value, 
			Collection<CirAnnotation> annotations) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			/* 1. determine the original and mutated values */
			CirExecution execution = expression.execution_of();
			SymbolExpression orig_value;
			if(CirMutations.is_assigned(expression)) {
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				orig_value = SymbolFactory.sym_expression(statement.get_rvalue());
			}
			else {
				orig_value = SymbolFactory.sym_expression(expression);
			}
			if(CirMutations.is_boolean(expression)) {
				orig_value = SymbolFactory.sym_condition(orig_value, true);
				muta_value = SymbolFactory.sym_condition(muta_value, true);
			}
			orig_value = CirValueScope.evaluate(orig_value, null);
			muta_value = CirValueScope.evaluate(muta_value, null);
			if(muta_value == CirValueScope.expt_value) {
				annotations.add(CirAnnotation.trp_stmt(execution));
				return;
			}
			
			/* 2. compare the original and mutated value */
			if(muta_value.equals(orig_value)) { /* no error occurs */ return; }
			/* 3. otherwise, generate original and mutation values */
			else {
				annotations.add(CirAnnotation.ori_expr(expression));
				annotations.add(CirAnnotation.mut_expr(expression, muta_value));
			}
			
			/* 4. difference-related annotations generated */
			if(CirMutations.is_boolean(expression) || CirMutations.is_numeric(expression) || CirMutations.is_address(expression)) {
				annotations.add(CirAnnotation.cmp_diff(expression, muta_value));
			}
			if(CirMutations.is_numeric(expression) || CirMutations.is_address(expression)) {
				annotations.add(CirAnnotation.sub_diff(expression, muta_value));
			}
			if(CirMutations.is_numeric(expression)) {
				annotations.add(CirAnnotation.ext_diff(expression, muta_value));
			}
			if(CirMutations.is_integer(expression)) {
				annotations.add(CirAnnotation.xor_diff(expression, muta_value));
			}
		}
	}
	private void generate_annotations_in_difer_error(CirDiferError attribute, Collection<CirAnnotation> annotations) throws Exception {
		this.generate_annotations_in_exprs_error(attribute.get_orig_expression(), attribute.get_muta_expression(), annotations);
	}
	private void generate_annotations_in_value_error(CirValueError attribute, Collection<CirAnnotation> annotations) throws Exception {
		this.generate_annotations_in_exprs_error(attribute.get_orig_expression(), attribute.get_muta_expression(), annotations);
	}
	private void generate_annotations_in_refer_error(CirReferError attribute, Collection<CirAnnotation> annotations) throws Exception {
		this.generate_annotations_in_exprs_error(attribute.get_orig_expression(), attribute.get_muta_expression(), annotations);
	}
	private void generate_annotations_in_state_error(CirStateError attribute, Collection<CirAnnotation> annotations) throws Exception {
		this.generate_annotations_in_exprs_error(attribute.get_orig_expression(), attribute.get_muta_expression(), annotations);
	}
	private void generate_annotations_in_traps_error(CirTrapsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(CirAnnotation.trp_stmt(attribute.get_execution()));
	}
	private void generate_annotations_in(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute as null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else if(attribute instanceof CirCoverCount) {
			this.generate_annotations_in_cover_count((CirCoverCount) attribute, annotations);
		}
		else if(attribute instanceof CirConstraint) {
			this.generate_annotations_in_constraints((CirConstraint) attribute, annotations);
		}
		else if(attribute instanceof CirBlockError) {
			this.generate_annotations_in_block_error((CirBlockError) attribute, annotations);
		}
		else if(attribute instanceof CirFlowsError) {
			this.generate_annotations_in_flows_error((CirFlowsError) attribute, annotations);
		}
		else if(attribute instanceof CirTrapsError) {
			this.generate_annotations_in_traps_error((CirTrapsError) attribute, annotations);
		}
		else if(attribute instanceof CirDiferError) {
			this.generate_annotations_in_difer_error((CirDiferError) attribute, annotations);
		}
		else if(attribute instanceof CirValueError) {
			this.generate_annotations_in_value_error((CirValueError) attribute, annotations);
		}
		else if(attribute instanceof CirReferError) {
			this.generate_annotations_in_refer_error((CirReferError) attribute, annotations);
		}
		else if(attribute instanceof CirStateError) {
			this.generate_annotations_in_state_error((CirStateError) attribute, annotations);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + attribute);
		}
	}
	/**
	 * It generates the symbolic annotations representing the input attribute
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	public static void generate_annotations(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		utils.generate_annotations_in(attribute, annotations);
	}
	
}
