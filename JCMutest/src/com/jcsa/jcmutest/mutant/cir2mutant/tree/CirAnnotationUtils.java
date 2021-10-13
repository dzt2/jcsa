package com.jcsa.jcmutest.mutant.cir2mutant.tree;

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
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirKillMutant;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirReferError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirTrapsError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirValueError;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
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
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It implements the generation, concretization and summarization of CirAnnotation.
 * 
 * @author yukimula
 *
 */
final class CirAnnotationUtils {
	
	/* singleton mode */ /** constructor **/ private CirAnnotationUtils() {}
	private static final CirAnnotationUtils utils = new CirAnnotationUtils();
	
	/* basic methods */
	/**
	 * It collects the set of sub-conditions from input expression into the output collection
	 * @param condition
	 * @param conditions
	 * @throws Exception
	 */
	private void get_conditions_in(SymbolExpression condition, Collection<SymbolExpression> conditions) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				/* the true operand will be ignored from the conjunctive */
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
				this.get_conditions_in(loperand, conditions);
				this.get_conditions_in(roperand, conditions);
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
	 * @return the set of sub-conditions from input expression
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_conditions_in(SymbolExpression condition) throws Exception {
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.get_conditions_in(condition, conditions); return conditions;
	}
	/**
	 * It collects the set of reference expressions within the symbolic node as input
	 * @param node
	 * @param references
	 * @throws Exception
	 */
	private void get_references_in(SymbolNode node, Collection<SymbolExpression> references) throws Exception {
		if(node.is_reference()) {
			references.add((SymbolExpression) node);
		}
		for(SymbolNode child : node.get_children()) {
			this.get_references_in(child, references);
		}
	}
	/**
	 * @param node
	 * @return the set of reference expressions within the symbolic input node
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_references_in(SymbolNode node) throws Exception {
		Set<SymbolExpression> references = new HashSet<SymbolExpression>();
		this.get_references_in(node, references); return references;
	}
	/**
	 * @param node
	 * @param references
	 * @return whether any reference is preserved in the input symbolic node
	 * @throws Exception
	 */
	private boolean has_references_in(SymbolNode node, Collection<SymbolExpression> references) throws Exception {
		if(references.contains(node)) {
			return true;
		}
		else {
			for(SymbolNode child : node.get_children()) {
				if(this.has_references_in(child, references)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether any reference is defined in the execution point
	 * @throws Exception
	 */
	private boolean has_references_in(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
		if(execution == null) {
			return false;
		}
		else if(references == null || references.isEmpty()) {
			return false;
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirAssignStatement) {
				SymbolExpression reference = SymbolFactory.sym_expression(
						((CirAssignStatement) statement).get_lvalue());
				return references.contains(reference);
			}
			else if(statement instanceof CirIfStatement) {
				SymbolExpression condition = SymbolFactory.sym_expression(
						((CirIfStatement) statement).get_condition());
				return this.has_references_in(condition, references);
			}
			else if(statement instanceof CirCaseStatement) {
				SymbolExpression condition = SymbolFactory.sym_expression(
						((CirCaseStatement) statement).get_condition());
				return this.has_references_in(condition, references);
			}
			else {
				return false;
			}
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @return the prior point where the condition could be evaluated before the given point
	 * @throws Exception
	 */
	private CirExecution get_prior_checkpoint(CirExecution execution, Object condition) throws Exception {
		/* generate the symbolic expression for analysis */
		SymbolExpression expression = SymbolFactory.sym_expression(condition);
		expression = CirMutations.safe_evaluate(expression, null);
		
		/* A. in constant case, return the prior decidable point */
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				CirExecutionPath path = CirExecutionPathFinder.finder.db_extend(execution);
				Iterator<CirExecutionEdge> iterator = path.get_iterator(true);
				while(iterator.hasNext()) {
					CirExecutionEdge edge = iterator.next();
					CirStatement statement = edge.get_target().get_statement();
					if(statement instanceof CirIfStatement
						|| statement instanceof CirCaseStatement) {
						return edge.get_target();
					}
				}
				return path.get_source();
			}
			else {
				return execution.get_graph().get_entry();
			}
		}
		/* B. otherwise, move backward until the definition node is found */
		else {
			Collection<SymbolExpression> references = this.get_references_in(expression);
			CirExecutionPath path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = path.get_iterator(true);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(this.has_references_in(edge.get_source(), references)) {
					return edge.get_target();
				}
			}
			return path.get_source();
		}
		
	}
	/**
	 * @param orig_target
	 * @param muta_target
	 * @return true --> add_executions; false --> del_executions;
	 * @throws Exception
	 */
	private Map<Boolean, Collection<CirExecution>> infer_flag_executions(CirExecution orig_target, CirExecution muta_target) throws Exception {
		if(orig_target == null) {
			throw new IllegalArgumentException("Invalid orig_target: null");
		}
		else if(muta_target == null) {
			throw new IllegalArgumentException("Invalid muta_target: null");
		}
		else {
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

			/* construct mapping from true|false to collections */
			Map<Boolean, Collection<CirExecution>> results =
					new HashMap<Boolean, Collection<CirExecution>>();
			results.put(Boolean.TRUE, add_executions);
			results.put(Boolean.FALSE, del_executions);
			return results;
		}
	}
	/**
	 * @param max_exec_time
	 * @return [1, 2, 4, 8, ..., max_exec_time]
	 */
	private List<Integer> get_execution_times_util(int max_exec_time) {
		List<Integer> times = new ArrayList<Integer>();
		for(int k = 1; k < max_exec_time; k = k * 2) {
			times.add(Integer.valueOf(k));
		}
		times.add(Integer.valueOf(max_exec_time));
		return times;
	}
	/**
	 * @param max_exec_time
	 * @return the maximal executing times greater than the input parameter
	 */
	private int find_max_execution_time(int max_exec_time) {
		int counter = 1;
		while(counter < max_exec_time) {
			counter = counter * 2;
		}
		return counter / 2;
	}
	
	/* generation */
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_from_cover_count(CirCoverCount attribute, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. get the execution and executing times for generation */
		CirExecution execution = attribute.get_execution();
		int max_times = ((SymbolConstant) attribute.get_parameter()).get_int();
		annotations.add(CirAnnotation.cov_stmt(execution, max_times));
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_from_constraints(CirConstraint attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution(), check_point;
		SymbolExpression condition = attribute.get_condition().evaluate(null);
		check_point = this.get_prior_checkpoint(execution, condition);
		annotations.add(CirAnnotation.eva_expr(check_point, condition));
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_from_block_error(CirBlockError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		if(attribute.is_executed()) {
			annotations.add(CirAnnotation.mut_stmt(execution, 
					SymbolFactory.sym_constant(Boolean.TRUE)));
		}
		else {
			annotations.add(CirAnnotation.mut_stmt(execution, 
					SymbolFactory.sym_constant(Boolean.FALSE)));
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_from_flows_error(CirFlowsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(CirAnnotation.mut_flow(attribute.get_original_flow(), attribute.get_mutation_flow()));
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_from_traps_error(CirTrapsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(CirAnnotation.trp_stmt(attribute.get_execution()));
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_from_kill_mutant(CirKillMutant attribute, Collection<CirAnnotation> annotations) throws Exception { }
	/**
	 * It generates annotations for expression-based error attribute class.
	 * @param expression
	 * @param value
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_from_exprs_error(CirExpression expression, Object value, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. generates the original and mutation value from the inputs */
		CirAnnotation orig_annotation, muta_annotation;
		if(CirMutations.is_assigned(expression)) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			orig_annotation = CirAnnotation.mut_stat(expression, statement.get_rvalue());
			muta_annotation = CirAnnotation.mut_stat(expression, value);
		}
		else {
			orig_annotation = CirAnnotation.mut_expr(expression, expression);
			muta_annotation = CirAnnotation.mut_expr(expression, value);
		}
		CirExecution execution = expression.execution_of(), check_point;
		
		/* 2. initialize the annotation generation mode from output values */
		if(orig_annotation.get_symb_value() == CirMutations.expt_value
			|| muta_annotation.get_symb_value() == CirMutations.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution)); 
			return;
		}
		else if(orig_annotation.get_symb_value().equals(muta_annotation.get_symb_value())) {
			check_point = this.get_prior_checkpoint(execution, Boolean.FALSE);
			annotations.add(CirAnnotation.eva_expr(check_point, Boolean.FALSE));
			return;
		}
		else {
			annotations.add(muta_annotation);
		}
		
		/* 3. difference based annotations generation */
		SymbolExpression orig_value = orig_annotation.get_symb_value();
		SymbolExpression muta_value = muta_annotation.get_symb_value();
		SymbolExpression difference;
		if(CirMutations.is_numeric(expression)) {
			difference = CirAnnotationValue.ext_differ(orig_value, muta_value);
			annotations.add(CirAnnotation.dif_asub(expression, difference));
		}
		if(CirMutations.is_numeric(expression) || CirMutations.is_address(expression)) {
			difference = CirAnnotationValue.sub_differ(orig_value, muta_value);
			annotations.add(CirAnnotation.dif_rsub(expression, difference));
		}
		if(CirMutations.is_integer(expression)) {
			difference = CirAnnotationValue.xor_differ(orig_value, muta_value);
			annotations.add(CirAnnotation.dif_exor(expression, difference));
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_from_difer_error(CirDiferError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = attribute.get_orig_expression();
		SymbolExpression value = attribute.get_muta_expression();
		this.generate_annotations_from_exprs_error(expression, value, annotations);
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_from_value_error(CirValueError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = attribute.get_orig_expression();
		SymbolExpression value = attribute.get_muta_expression();
		this.generate_annotations_from_exprs_error(expression, value, annotations);
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_from_refer_error(CirReferError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = attribute.get_orig_expression();
		SymbolExpression value = attribute.get_muta_expression();
		this.generate_annotations_from_exprs_error(expression, value, annotations);
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_from_state_error(CirStateError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = attribute.get_orig_expression();
		SymbolExpression value = attribute.get_muta_expression();
		this.generate_annotations_from_exprs_error(expression, value, annotations);
	}
	/**
	 * It generates the symbolic annotations representing the input attribute
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_from_attribute(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else if(attribute instanceof CirCoverCount) {
			this.generate_annotations_from_cover_count((CirCoverCount) attribute, annotations);
		}
		else if(attribute instanceof CirConstraint) {
			this.generate_annotations_from_constraints((CirConstraint) attribute, annotations);
		}
		else if(attribute instanceof CirBlockError) {
			this.generate_annotations_from_block_error((CirBlockError) attribute, annotations);
		}
		else if(attribute instanceof CirFlowsError) {
			this.generate_annotations_from_flows_error((CirFlowsError) attribute, annotations);
		}
		else if(attribute instanceof CirTrapsError) {
			this.generate_annotations_from_traps_error((CirTrapsError) attribute, annotations);
		}
		else if(attribute instanceof CirDiferError) {
			this.generate_annotations_from_difer_error((CirDiferError) attribute, annotations);
		}
		else if(attribute instanceof CirValueError) {
			this.generate_annotations_from_value_error((CirValueError) attribute, annotations);
		}
		else if(attribute instanceof CirReferError) {
			this.generate_annotations_from_refer_error((CirReferError) attribute, annotations);
		}
		else if(attribute instanceof CirStateError) {
			this.generate_annotations_from_state_error((CirStateError) attribute, annotations);
		}
		else if(attribute instanceof CirKillMutant) {
			this.generate_annotations_from_kill_mutant((CirKillMutant) attribute, annotations);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + attribute);
		}
	}
	
	/* concretized */
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_from_cov_stmt(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { 
		/* not to concretize for this class of symbolic annotation */ 
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_from_eva_expr(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { 
		/* not to concretize for this class of symbolic annotation */ 
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_from_mut_flow(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { 
		/* not to concretize for this class of symbolic annotation */ 
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_from_trp_stmt(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { 
		/* not to concretize for this class of symbolic annotation */ 
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_from_mut_stmt(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { 
		CirExecution execution = annotation.get_execution();
		SymbolExpression value = annotation.get_symb_value();
		value = CirMutations.safe_evaluate(value, context);
		annotations.add(CirAnnotation.mut_stmt(execution, value));
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_from_mut_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { 
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = annotation.get_symb_value();
		value = CirMutations.safe_evaluate(value, context);
		CirExecution execution = annotation.get_execution();
		if(value == CirMutations.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution));
		}
		else {
			annotations.add(CirAnnotation.mut_expr(expression, value));
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_from_mut_stat(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { 
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = annotation.get_symb_value();
		value = CirMutations.safe_evaluate(value, context);
		CirExecution execution = annotation.get_execution();
		if(value == CirMutations.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution));
		}
		else {
			annotations.add(CirAnnotation.mut_stat(expression, value));
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_from_dif_asub(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { 
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = annotation.get_symb_value();
		value = CirMutations.safe_evaluate(value, context);
		CirExecution execution = annotation.get_execution();
		if(value == CirMutations.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution));
		}
		else {
			annotations.add(CirAnnotation.dif_asub(expression, value));
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_from_dif_rsub(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { 
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = annotation.get_symb_value();
		value = CirMutations.safe_evaluate(value, context);
		CirExecution execution = annotation.get_execution();
		if(value == CirMutations.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution));
		}
		else {
			annotations.add(CirAnnotation.dif_rsub(expression, value));
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_from_dif_exor(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { 
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = annotation.get_symb_value();
		value = CirMutations.safe_evaluate(value, context);
		CirExecution execution = annotation.get_execution();
		if(value == CirMutations.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution));
		}
		else {
			annotations.add(CirAnnotation.dif_exor(expression, value));
		}
	}
	/**
	 * It generates the concrete version of the symbolic annotation using the input context
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_from(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else {
			switch(annotation.get_logic_type()) {
			case cov_stmt:	this.concretize_annotations_from_cov_stmt(annotation, context, annotations); break;
			case eva_expr:	this.concretize_annotations_from_eva_expr(annotation, context, annotations); break;
			case trp_stmt:	this.concretize_annotations_from_trp_stmt(annotation, context, annotations); break;
			case mut_flow:	this.concretize_annotations_from_mut_flow(annotation, context, annotations); break;
			case mut_stmt:	this.concretize_annotations_from_mut_stmt(annotation, context, annotations); break;
			case mut_expr:	this.concretize_annotations_from_mut_expr(annotation, context, annotations); break;
			case mut_stat:	this.concretize_annotations_from_mut_stat(annotation, context, annotations); break;
			case dif_asub:	this.concretize_annotations_from_dif_asub(annotation, context, annotations); break;
			case dif_rsub:	this.concretize_annotations_from_dif_rsub(annotation, context, annotations); break;
			case dif_exor:	this.concretize_annotations_from_dif_exor(annotation, context, annotations); break;
			default:		throw new IllegalArgumentException("Unsupported: " + annotation.toString());
			}
		}
	}
	
	/* summarization */
	/**
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_from_cov_stmt(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		/* it infers the minimal limit of the symbolic annotations */
		CirExecution execution = annotation.get_execution();
		int max_time = ((SymbolConstant) annotation.get_symb_value()).get_int();
		List<Integer> execution_times = this.get_execution_times_util(max_time);
		
		/* it generates the execution coverage requirement annotations */
		for(Integer execution_time : execution_times) {
			abs_annotations.add(CirAnnotation.cov_stmt(execution, execution_time));
		}
	}
	/**
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_from_eva_expr(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		/* 1. it gets the check_point and sub-conditions */
		CirExecution execution = annotation.get_execution(), check_point;
		SymbolExpression condition = annotation.get_symb_value();
		Collection<SymbolExpression> sub_conditions = this.get_conditions_in(condition);
		
		/* 2. generate the eva_expr annotations from sub_conditions */
		for(SymbolExpression sub_condition : sub_conditions) {
			check_point = this.get_prior_checkpoint(execution, sub_condition);
			abs_annotations.add(CirAnnotation.eva_expr(check_point, sub_condition));
		}
		
		/* 3. coverage requirement is summarized when no condition is needed */
		if(sub_conditions.isEmpty()) {
			check_point = this.get_prior_checkpoint(execution, Boolean.TRUE);
			abs_annotations.add(CirAnnotation.cov_stmt(check_point, 1));
		}
	}
	/**
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_from_mut_flow(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		CirExecution orig_target = annotation.get_store_unit().execution_of();
		CirExecution muta_target = (CirExecution) annotation.get_symb_value().get_source();
		Map<Boolean, Collection<CirExecution>> results = this.infer_flag_executions(orig_target, muta_target);
		
		int counter = 0;
		for(Boolean result : results.keySet()) {
			for(CirExecution execution : results.get(result)) {
				if(execution.get_statement() instanceof CirTagStatement) {
					continue;
				}
				else {
					abs_annotations.add(CirAnnotation.mut_stmt(execution, 
							SymbolFactory.sym_constant(result))); counter++;
				}
			}
		}
		
		if(counter > 0) {
			abs_annotations.add(annotation);
		}
	}
	/**
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_from_trp_stmt(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		abs_annotations.add(CirAnnotation.trp_stmt(annotation.get_execution()));
	}
	/**
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_from_mut_stmt(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		abs_annotations.add(annotation);
	}
	/**
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_from_mut_expr(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		/* 1. get the original expression and mutation value */
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression muta_value = annotation.get_symb_value();
		Set<SymbolExpression> conc_values = new HashSet<SymbolExpression>();
		
		/* 2. collect the concrete values or return when except occurs */
		for(CirAnnotation con_annotation : con_annotations) {
			if(con_annotation.get_logic_type() == CirAnnotationClass.trp_stmt) {
				abs_annotations.add(con_annotation);
				return;
			}
			else {
				conc_values.add(con_annotation.get_symb_value());
			}
		}
		
		/* 3. generate the abstract value scopes from concrete values */
		Collection<SymbolExpression> scopes = CirAnnotationValue.sum_scopes_in(
				expression, CirAnnotationValue.get_scopes_in(expression, conc_values));
		for(SymbolExpression scope : scopes) {
			abs_annotations.add(CirAnnotation.mut_expr(expression, scope));
		}
		abs_annotations.add(CirAnnotation.mut_expr(expression, muta_value));
	}
	/**
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_from_mut_stat(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		/* 1. get the original expression and mutation value */
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression muta_value = annotation.get_symb_value();
		Set<SymbolExpression> conc_values = new HashSet<SymbolExpression>();
		
		/* 2. collect the concrete values or return when except occurs */
		for(CirAnnotation con_annotation : con_annotations) {
			if(con_annotation.get_logic_type() == CirAnnotationClass.trp_stmt) {
				abs_annotations.add(con_annotation);
				return;
			}
			else {
				conc_values.add(con_annotation.get_symb_value());
			}
		}
		
		/* 3. generate the abstract value scopes from concrete values */
		Collection<SymbolExpression> scopes = CirAnnotationValue.sum_scopes_in(
				expression, CirAnnotationValue.get_scopes_in(expression, conc_values));
		for(SymbolExpression scope : scopes) {
			abs_annotations.add(CirAnnotation.mut_stat(expression, scope));
		}
		abs_annotations.add(CirAnnotation.mut_stat(expression, muta_value));
	}
	/**
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_from_dif_asub(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		/* 1. get the original expression and mutation value */
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Set<SymbolExpression> conc_values = new HashSet<SymbolExpression>();
		
		/* 2. collect the concrete values or return when except occurs */
		for(CirAnnotation con_annotation : con_annotations) {
			if(con_annotation.get_logic_type() == CirAnnotationClass.trp_stmt) {
				abs_annotations.add(con_annotation);
				return;
			}
			else {
				conc_values.add(con_annotation.get_symb_value());
			}
		}
		
		/* 3. generate the abstract value scopes from concrete values */
		Collection<SymbolExpression> scopes = CirAnnotationValue.sum_scopes_in(
				expression, CirAnnotationValue.get_scopes_in(expression, conc_values));
		if(scopes.contains(CirAnnotationValue.numb_value)) {
			scopes.remove(CirAnnotationValue.numb_value);
		}
		for(SymbolExpression scope : scopes) {
			abs_annotations.add(CirAnnotation.dif_asub(expression, scope));
		}
	}
	/**
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_from_dif_rsub(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		/* 1. get the original expression and mutation value */
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Set<SymbolExpression> conc_values = new HashSet<SymbolExpression>();
		
		/* 2. collect the concrete values or return when except occurs */
		for(CirAnnotation con_annotation : con_annotations) {
			if(con_annotation.get_logic_type() == CirAnnotationClass.trp_stmt) {
				abs_annotations.add(con_annotation);
				return;
			}
			else {
				conc_values.add(con_annotation.get_symb_value());
			}
		}
		
		/* 3. generate the abstract value scopes from concrete values */
		Collection<SymbolExpression> scopes = CirAnnotationValue.sum_scopes_in(
				expression, CirAnnotationValue.get_scopes_in(expression, conc_values));
		scopes.remove(CirAnnotationValue.numb_value);
		if(scopes.contains(CirAnnotationValue.numb_value)) {
			scopes.remove(CirAnnotationValue.numb_value);
		}
		else if(scopes.contains(CirAnnotationValue.addr_value)) {
			scopes.remove(CirAnnotationValue.addr_value);
		}
		for(SymbolExpression scope : scopes) {
			abs_annotations.add(CirAnnotation.dif_rsub(expression, scope));
		}
	}
	/**
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_from_dif_exor(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		/* 1. get the original expression and mutation value */
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Set<SymbolExpression> conc_values = new HashSet<SymbolExpression>();
		
		/* 2. collect the concrete values or return when except occurs */
		for(CirAnnotation con_annotation : con_annotations) {
			if(con_annotation.get_logic_type() == CirAnnotationClass.trp_stmt) {
				abs_annotations.add(con_annotation);
				return;
			}
			else {
				conc_values.add(con_annotation.get_symb_value());
			}
		}
		
		/* 3. generate the abstract value scopes from concrete values */
		Collection<SymbolExpression> scopes = CirAnnotationValue.sum_scopes_in(
				expression, CirAnnotationValue.get_scopes_in(expression, conc_values));
		if(scopes.contains(CirAnnotationValue.numb_value)) {
			scopes.remove(CirAnnotationValue.numb_value);
		}
		for(SymbolExpression scope : scopes) {
			abs_annotations.add(CirAnnotation.dif_exor(expression, scope));
		}
	}
	/**
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_from(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation as null");
		}
		else {
			switch(annotation.get_logic_type()) {
			case cov_stmt:	this.summarize_annotations_from_cov_stmt(annotation, con_annotations, abs_annotations); break;
			case eva_expr:	this.summarize_annotations_from_eva_expr(annotation, con_annotations, abs_annotations); break;
			case trp_stmt:	this.summarize_annotations_from_trp_stmt(annotation, con_annotations, abs_annotations); break;
			case mut_flow:	this.summarize_annotations_from_mut_flow(annotation, con_annotations, abs_annotations); break;
			case mut_stmt:	this.summarize_annotations_from_mut_stmt(annotation, con_annotations, abs_annotations); break;
			case mut_expr:	this.summarize_annotations_from_mut_expr(annotation, con_annotations, abs_annotations); break;
			case mut_stat:	this.summarize_annotations_from_mut_stat(annotation, con_annotations, abs_annotations); break;
			case dif_asub:	this.summarize_annotations_from_dif_asub(annotation, con_annotations, abs_annotations); break;
			case dif_rsub:	this.summarize_annotations_from_dif_rsub(annotation, con_annotations, abs_annotations); break;
			case dif_exor:	this.summarize_annotations_from_dif_exor(annotation, con_annotations, abs_annotations); break;
			default:		throw new IllegalArgumentException("Unsupported: " + annotation.toString());
			}
		}
	}
	
	/* extension */
	/**
	 * cov_stmt(E, N) --> ... --> cov_stmt(E, 4) --> cov_stmt(E, 2) --> cov_stmt(E, 1)
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_cov_stmt(CirAnnotationNode source) throws Exception {
		/* 1. get the execution and executing times at first */
		CirExecution execution = source.get_annotation().get_execution();
		int max_time = ((SymbolConstant) source.get_annotation().get_symb_value()).get_int();
		max_time = this.find_max_execution_time(max_time);
		
		/* 2. link the node to the corresponding smaller execution time */
		if(max_time > 0)
			this.extend_annotations_from(source.new_child(CirAnnotation.cov_stmt(execution, max_time)));
	}
	/**
	 * eva_expr(E, expr) --> eva_expr(E, expr')+ --> cov_stmt(E, 1)
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_eva_expr(CirAnnotationNode source) throws Exception {
		/* 1. fetch the attributes from the annotation of the source */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution(), check_point;
		SymbolExpression condition = annotation.get_symb_value();
		
		/* 2. syntax-directed extension from the symbolic condition */
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				this.extend_annotations_from(source.new_child(CirAnnotation.cov_stmt(execution, 1)));
			}
			else {
				condition = SymbolFactory.sym_constant(Boolean.TRUE);
				check_point = this.get_prior_checkpoint(execution, condition);
				this.extend_annotations_from(source.new_child(CirAnnotation.cov_stmt(check_point, 1)));
			}
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			if(op == COperator.smaller_tn) {
				condition = SymbolFactory.smaller_eq(loperand, roperand);
				check_point = this.get_prior_checkpoint(execution, condition);
				this.extend_annotations_from(source.new_child(CirAnnotation.eva_expr(check_point, condition)));
				
				condition = SymbolFactory.not_equals(loperand, roperand);
				check_point = this.get_prior_checkpoint(execution, condition);
				this.extend_annotations_from(source.new_child(CirAnnotation.eva_expr(check_point, condition)));
			}
			else if(op == COperator.greater_tn) {
				condition = SymbolFactory.greater_eq(loperand, roperand);
				check_point = this.get_prior_checkpoint(execution, condition);
				this.extend_annotations_from(source.new_child(CirAnnotation.eva_expr(check_point, condition)));
				
				condition = SymbolFactory.not_equals(loperand, roperand);
				check_point = this.get_prior_checkpoint(execution, condition);
				this.extend_annotations_from(source.new_child(CirAnnotation.eva_expr(check_point, condition)));
			}
			else if(op == COperator.equal_with) {
				condition = SymbolFactory.smaller_eq(loperand, roperand);
				check_point = this.get_prior_checkpoint(execution, condition);
				this.extend_annotations_from(source.new_child(CirAnnotation.eva_expr(check_point, condition)));
				
				condition = SymbolFactory.greater_eq(loperand, roperand);
				check_point = this.get_prior_checkpoint(execution, condition);
				this.extend_annotations_from(source.new_child(CirAnnotation.eva_expr(check_point, condition)));
			}
			else {
				condition = SymbolFactory.sym_constant(Boolean.TRUE);
				check_point = this.get_prior_checkpoint(execution, condition);
				this.extend_annotations_from(source.new_child(CirAnnotation.cov_stmt(check_point, 1)));
			}
		}
		else {
			condition = SymbolFactory.sym_constant(Boolean.TRUE);
			check_point = this.get_prior_checkpoint(execution, condition);
			this.extend_annotations_from(source.new_child(CirAnnotation.cov_stmt(check_point, 1)));
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_trp_stmt(CirAnnotationNode source) throws Exception {
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution();
		execution = this.get_prior_checkpoint(execution, Boolean.TRUE);
		this.extend_annotations_from(source.new_child(CirAnnotation.cov_stmt(execution, 1)));
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_mut_stmt(CirAnnotationNode source) throws Exception {
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution();
		execution = this.get_prior_checkpoint(execution, Boolean.TRUE);
		this.extend_annotations_from(source.new_child(CirAnnotation.cov_stmt(execution, 1)));
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_mut_flow(CirAnnotationNode source) throws Exception {
		CirAnnotation annotation = source.get_annotation();
		CirExecution orig_target = annotation.get_store_unit().execution_of();
		CirExecution muta_target = (CirExecution) annotation.get_symb_value().get_source();
		Map<Boolean, Collection<CirExecution>> results = this.infer_flag_executions(orig_target, muta_target);
		
		for(Boolean result : results.keySet()) {
			for(CirExecution execution : results.get(result)) {
				if(execution.get_statement() instanceof CirTagStatement) {
					continue;
				}
				else {
					this.extend_annotations_from(source.new_child(CirAnnotation.
							mut_stmt(execution, SymbolFactory.sym_constant(result))));
				}
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_mut_expr(CirAnnotationNode source) throws Exception {
		/* 1. fetch the execution and its expression and value */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution(), check_point;
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression muta_value = annotation.get_symb_value();
		Collection<SymbolExpression> scopes = CirAnnotationValue.nex_scopes_in(expression, muta_value);
		
		/* 2. when no subsumed value is shown, directly subsuming the coverage */
		if(scopes.isEmpty()) {
			check_point = this.get_prior_checkpoint(execution, Boolean.TRUE);
			this.extend_annotations_from(source.new_child(CirAnnotation.cov_stmt(check_point, 1)));
		}
		/* 3. otherwise, subsume the directly scopes from the value in mut_expr */
		else {
			for(SymbolExpression scope : scopes) {
				this.extend_annotations_from(source.new_child(CirAnnotation.mut_expr(expression, scope)));
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_mut_stat(CirAnnotationNode source) throws Exception {
		/* 1. fetch the execution and its expression and value */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution(), check_point;
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression muta_value = annotation.get_symb_value();
		Collection<SymbolExpression> scopes = CirAnnotationValue.nex_scopes_in(expression, muta_value);
		
		/* 2. when no subsumed value is shown, directly subsuming the coverage */
		if(scopes.isEmpty()) {
			check_point = this.get_prior_checkpoint(execution, Boolean.TRUE);
			this.extend_annotations_from(source.new_child(CirAnnotation.cov_stmt(check_point, 1)));
		}
		/* 3. otherwise, subsume the directly scopes from the value in mut_expr */
		else {
			for(SymbolExpression scope : scopes) {
				this.extend_annotations_from(source.new_child(CirAnnotation.mut_stat(expression, scope)));
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_dif_asub(CirAnnotationNode source) throws Exception {
		/* 1. fetch the execution and its expression and value */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution(), check_point;
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression muta_value = annotation.get_symb_value();
		Collection<SymbolExpression> scopes = CirAnnotationValue.nex_scopes_in(expression, muta_value);
		
		/* 2. when no subsumed value is shown, directly subsuming the coverage */
		if(scopes.isEmpty()) {
			check_point = this.get_prior_checkpoint(execution, Boolean.TRUE);
			this.extend_annotations_from(source.new_child(CirAnnotation.cov_stmt(check_point, 1)));
		}
		/* 3. otherwise, subsume the directly scopes from the value in mut_expr */
		else {
			for(SymbolExpression scope : scopes) {
				this.extend_annotations_from(source.new_child(CirAnnotation.dif_asub(expression, scope)));
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_dif_rsub(CirAnnotationNode source) throws Exception {
		/* 1. fetch the execution and its expression and value */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution(), check_point;
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression muta_value = annotation.get_symb_value();
		Collection<SymbolExpression> scopes = CirAnnotationValue.nex_scopes_in(expression, muta_value);
		
		/* 2. when no subsumed value is shown, directly subsuming the coverage */
		if(scopes.isEmpty()) {
			check_point = this.get_prior_checkpoint(execution, Boolean.TRUE);
			this.extend_annotations_from(source.new_child(CirAnnotation.cov_stmt(check_point, 1)));
		}
		/* 3. otherwise, subsume the directly scopes from the value in mut_expr */
		else {
			for(SymbolExpression scope : scopes) {
				this.extend_annotations_from(source.new_child(CirAnnotation.dif_rsub(expression, scope)));
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_dif_exor(CirAnnotationNode source) throws Exception {
		/* 1. fetch the execution and its expression and value */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution(), check_point;
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression muta_value = annotation.get_symb_value();
		Collection<SymbolExpression> scopes = CirAnnotationValue.nex_scopes_in(expression, muta_value);
		
		/* 2. when no subsumed value is shown, directly subsuming the coverage */
		if(scopes.isEmpty()) {
			check_point = this.get_prior_checkpoint(execution, Boolean.TRUE);
			this.extend_annotations_from(source.new_child(CirAnnotation.cov_stmt(check_point, 1)));
		}
		/* 3. otherwise, subsume the directly scopes from the value in mut_expr */
		else {
			for(SymbolExpression scope : scopes) {
				this.extend_annotations_from(source.new_child(CirAnnotation.dif_exor(expression, scope)));
			}
		}
	}
	/**
	 * It recursively extends annotation from the input source node in tree.
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from(CirAnnotationNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			CirAnnotation annotation = source.get_annotation();
			switch(annotation.get_logic_type()) {
			case cov_stmt:	this.extend_annotations_from_cov_stmt(source); break;
			case eva_expr:	this.extend_annotations_from_eva_expr(source); break;
			case trp_stmt:	this.extend_annotations_from_trp_stmt(source); break;
			case mut_flow:	this.extend_annotations_from_mut_flow(source); break;
			case mut_stmt:	this.extend_annotations_from_mut_stmt(source); break;
			case mut_expr:	this.extend_annotations_from_mut_expr(source); break;
			case mut_stat:	this.extend_annotations_from_mut_stat(source); break;
			case dif_asub:	this.extend_annotations_from_dif_asub(source); break;
			case dif_rsub:	this.extend_annotations_from_dif_rsub(source); break;
			case dif_exor:	this.extend_annotations_from_dif_exor(source); break;
			default:		throw new IllegalArgumentException("Unsupported: " + annotation.toString());
			}
		}
	}
	
	/* interfaces */
	protected static void generate_annotations(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		utils.generate_annotations_from_attribute(attribute, annotations);
	}
	protected static void concretize_annotations(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		utils.concretize_annotations_from(annotation, context, annotations);
	}
	protected static void summarize_annotations(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		utils.summarize_annotations_from(annotation, con_annotations, abs_annotations);
	}
	protected static void extend_annotations(CirAnnotationNode source) throws Exception {
		utils.extend_annotations_from(source);
	}
	
}
