package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
 * It implements the generation, concretize and summarization of CirAnnotation
 * from CirAttribute or themselves simply.
 * 
 * @author yukimula
 *
 */
final class CirAnnotationUtil {
	
	/* singleton */	/** constructor **/ private CirAnnotationUtil() { }
	private static final CirAnnotationUtil util = new CirAnnotationUtil();
	
	/* basic methods */
	/**
	 * It recursively collects the sub_conditions from the input condition when taking it as a conjunctive expression.
	 * @param condition
	 * @param conditions
	 * @throws Exception
	 */
	private void get_conditions_in_conjunct(SymbolExpression condition, Collection<SymbolExpression> conditions) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				/* true operand is ignored from definition of conjunction */
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
				this.get_conditions_in_conjunct(loperand, conditions);
				this.get_conditions_in_conjunct(roperand, conditions);
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
	 * @return the set of sub_conditions defined in the input condition when taking it as the conjunctive expressions.
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_conditions_in_conjunct(SymbolExpression condition) throws Exception {
		Collection<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.get_conditions_in_conjunct(condition, conditions); return conditions;
	}
	/**
	 * It recursively collects the references defined in the symbolic node
	 * @param node
	 * @param references
	 * @throws Exception
	 */
	private void get_references_in_symbolic(SymbolNode node, Collection<SymbolExpression> references) {
		if(node.is_reference()) { 
			references.add((SymbolExpression) node); 
		}
		for(SymbolNode child : node.get_children()) {
			this.get_references_in_symbolic(child, references);
		}
	}
	/**
	 * @param node
	 * @return the set of references defined in the symbolic node
	 */
	private Collection<SymbolExpression> get_references_in_symbolic(SymbolNode node) {
		Collection<SymbolExpression> references = new HashSet<SymbolExpression>();
		this.get_references_in_symbolic(node, references); return references;
	}
	/**
	 * @param node
	 * @param references
	 * @return whether any reference in input collection is used in the symbolic node
	 */
	private boolean has_references_in_symbolic(SymbolNode node, Collection<SymbolExpression> references) {
		if(node == null) {
			return false;
		}
		else if(references == null || references.isEmpty()) {
			return false;
		}
		else if(references.contains(node)) {
			return true;
		}
		else {
			for(SymbolNode child : node.get_children()) {
				if(this.has_references_in_symbolic(child, references)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether any references are used in the statement of execution
	 * @throws Exception
	 */
	private boolean has_references_in_execution(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(references == null || references.isEmpty()) {
			return false;
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirAssignStatement) {
				return references.contains(SymbolFactory.sym_expression(
						((CirAssignStatement) statement).get_lvalue()));
			}
			else if(statement instanceof CirIfStatement) {
				SymbolExpression condition = SymbolFactory.sym_expression(((CirIfStatement) statement).get_condition());
				return this.has_references_in_symbolic(condition, references);
			}
			else if(statement instanceof CirCaseStatement) {
				SymbolExpression condition = SymbolFactory.sym_expression(((CirCaseStatement) statement).get_condition());
				return this.has_references_in_symbolic(condition, references);
			}
			else {
				return false;
			}
		}
	}
	/**
	 * @param max_times
	 * @return [1, 2, 4, 8, 16, ..., max_times]
	 */
	private List<Integer> get_execution_times_from(int max_times) {
		List<Integer> execution_times = new ArrayList<Integer>();
		for(int times = 1; times < max_times; times = times * 2) {
			execution_times.add(Integer.valueOf(times));
		}
		return execution_times;
	}
	/**
	 * @param execution
	 * @param expression
	 * @return it determines the best-prior check-point to evaluate the expression
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
			Collection<SymbolExpression> references = this.get_references_in_symbolic(expression);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(this.has_references_in_execution(edge.get_source(), references)) {
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
	private Map<Boolean, Collection<CirExecution>>	find_add_del_executions(CirExecution orig_target, CirExecution muta_target) throws Exception {
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
	
	/* automatic extension in tree annotation structure */
	/**
	 * It recursively extend the annotation from the source node.
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_in(CirAnnotationNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			CirAnnotation annotation = source.get_annotation();
			switch(annotation.get_value_type()) {
			case cov_stmt:	this.extend_annotations_in_cov_stmt(source); break;
			case eva_expr:	this.extend_annotations_in_eva_expr(source); break;
			case mut_stmt:	this.extend_annotations_in_mut_stmt(source); break;
			case trp_stmt:	this.extend_annotations_in_trp_stmt(source); break;
			case mut_expr:	this.extend_annotations_in_mut_expr(source); break;
			case sub_diff:	this.extend_annotations_in_sub_diff(source); break;
			case ext_diff:	this.extend_annotations_in_ext_diff(source); break;
			case xor_diff:	this.extend_annotations_in_xor_diff(source); break;
			default:		throw new IllegalArgumentException("Unsupport: " + annotation);
			}
		}
	}
	/**
	 * cov(E, N) --> cov(E, N/2) --> ... --> cov(E, 1)
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_in_cov_stmt(CirAnnotationNode source) throws Exception {
		/* 1. fetch the execution point and running times */
		CirExecution execution = source.get_annotation().get_execution();
		int max_times = ((SymbolConstant) source.get_annotation().get_symb_value()).get_int().intValue();
		List<Integer> execution_times = this.get_execution_times_from(max_times);
		
		/* 2. extend the cov_stmt until time as one */
		for(int k = execution_times.size() - 1; k >= 0; k--) {
			int time = execution_times.get(k).intValue();
			source = source.connect(CirAnnotation.cov_stmt(execution, time));
		}
	}
	/**
	 * eva_expr(S, E) --> eva_expr(S, E') --> ... --> cov_stmt(S, 1)
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_in_eva_expr(CirAnnotationNode source) throws Exception {
		/* 1. fetch the execution point and symbolic condition */
		CirExecution execution = source.get_annotation().get_execution();
		SymbolExpression condition = source.get_annotation().get_symb_value();
		
		/* 2. subsumption analysis from the symbolic condition */
		if(condition instanceof SymbolConstant) {
			source = source.connect(CirAnnotation.cov_stmt(execution, 1));
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			
			if(op == COperator.greater_tn) {
				condition = SymbolFactory.greater_eq(loperand, roperand);
				this.extend_annotations_in(source.connect(CirAnnotation.eva_expr(execution, condition)));
				condition = SymbolFactory.not_equals(loperand, roperand);
				this.extend_annotations_in(source.connect(CirAnnotation.eva_expr(execution, condition)));
			}
			else if(op == COperator.smaller_tn) {
				condition = SymbolFactory.smaller_eq(loperand, roperand);
				this.extend_annotations_in(source.connect(CirAnnotation.eva_expr(execution, condition)));
				condition = SymbolFactory.not_equals(loperand, roperand);
				this.extend_annotations_in(source.connect(CirAnnotation.eva_expr(execution, condition)));
			}
			else if(op == COperator.equal_with) {
				condition = SymbolFactory.greater_eq(loperand, roperand);
				this.extend_annotations_in(source.connect(CirAnnotation.eva_expr(execution, condition)));
				condition = SymbolFactory.smaller_eq(loperand, roperand);
				this.extend_annotations_in(source.connect(CirAnnotation.eva_expr(execution, condition)));
			}
			else {
				execution = this.find_prior_checkpoint(
						execution, SymbolFactory.sym_constant(Boolean.TRUE));
				source = source.connect(CirAnnotation.cov_stmt(execution, 1));
			}
		}
		else {
			execution = this.find_prior_checkpoint(
					execution, SymbolFactory.sym_constant(Boolean.TRUE));
			source = source.connect(CirAnnotation.cov_stmt(execution, 1));
		}
	}
	/**
	 * mut_stmt --> cov_stmt
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_in_mut_stmt(CirAnnotationNode source) throws Exception {
		CirExecution execution = source.get_annotation().get_execution();
		execution = this.find_prior_checkpoint(execution, SymbolFactory.sym_constant(Boolean.TRUE));
		source.connect(CirAnnotation.cov_stmt(execution, 1));
	}
	/**
	 * trp_stmt
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_in_trp_stmt(CirAnnotationNode source) throws Exception { }
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_in_mut_expr(CirAnnotationNode source) throws Exception {
		/* 1. fetch the expression and the mutated value */
		CirExecution execution = source.get_annotation().get_execution();
		CirExpression expression = (CirExpression) source.get_annotation().get_store_unit();
		SymbolExpression mutated_value = source.get_annotation().get_symb_value();
		
		/* 2. constant --> scopes in numeric */
		if(mutated_value instanceof SymbolConstant) {
			List<SymbolExpression> values = new ArrayList<SymbolExpression>();
			Collection<SymbolExpression> scopes = new HashSet<SymbolExpression>();
			values.add(mutated_value);
			CirValueScope.get_scopes_in(expression, values, scopes);
			
			for(SymbolExpression scope : scopes) {
				this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, scope)));
			}
		}
		/* 3. abstract value scope extension */
		else if(mutated_value == CirValueScope.true_value 
				|| mutated_value == CirValueScope.fals_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.bool_value)));
		}
		else if(mutated_value == CirValueScope.post_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.nneg_value)));
			this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.nzro_value)));
		}
		else if(mutated_value == CirValueScope.negt_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.npos_value)));
			this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.nzro_value)));
		}
		else if(mutated_value == CirValueScope.zero_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.nneg_value)));
			this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.npos_value)));
		}
		else if(mutated_value == CirValueScope.npos_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.numb_value)));
		}
		else if(mutated_value == CirValueScope.nneg_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.numb_value)));
		}
		else if(mutated_value == CirValueScope.nzro_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.numb_value)));
		}
		else if(mutated_value == CirValueScope.null_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.addr_value)));
		}
		else if(mutated_value == CirValueScope.nnul_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.addr_value)));
		}
		else if(mutated_value == CirValueScope.bool_value 
				|| mutated_value == CirValueScope.numb_value 
				|| mutated_value == CirValueScope.addr_value) {
			execution = this.find_prior_checkpoint(execution, 
					SymbolFactory.sym_constant(Boolean.TRUE));
			source.connect(CirAnnotation.cov_stmt(execution, 1));
		}
		/* 4. otherwise, extend to coverage */
		else {
			if(CirMutations.is_boolean(expression)) {
				this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.bool_value)));
			}
			else if(CirMutations.is_usigned(expression)) {
				this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.npos_value)));
			}
			else if(CirMutations.is_numeric(expression)) {
				this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.numb_value)));
			}
			else if(CirMutations.is_address(expression)) {
				this.extend_annotations_in(source.connect(CirAnnotation.mut_expr(expression, CirValueScope.addr_value)));
			}
			else {
				execution = this.find_prior_checkpoint(execution, 
						SymbolFactory.sym_constant(Boolean.TRUE));
				source.connect(CirAnnotation.cov_stmt(execution, 1));
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_in_sub_diff(CirAnnotationNode source) throws Exception {
		/* 1. fetch the expression and the mutated value */
		CirExecution execution = source.get_annotation().get_execution();
		CirExpression expression = (CirExpression) source.get_annotation().get_store_unit();
		SymbolExpression difference = source.get_annotation().get_symb_value();
		
		/* 2. constant to given scope anyway */
		if(difference instanceof SymbolConstant) {
			double value = ((SymbolConstant) difference).get_double();
			if(value > 0) {
				this.extend_annotations_in(source.connect(CirAnnotation.sub_diff(expression, CirValueScope.post_value)));
			}
			else if(value < 0) {
				this.extend_annotations_in(source.connect(CirAnnotation.sub_diff(expression, CirValueScope.negt_value)));
			}
			else {
				execution = this.find_prior_checkpoint(execution, SymbolFactory.sym_constant(Boolean.FALSE));
				this.extend_annotations_in(source.connect(CirAnnotation.eva_expr(execution, Boolean.FALSE)));
			}
		}
		else if(difference == CirValueScope.post_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.sub_diff(expression, CirValueScope.nneg_value)));
		}
		else if(difference == CirValueScope.negt_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.sub_diff(expression, CirValueScope.npos_value)));
		}
		else if(difference == CirValueScope.npos_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.sub_diff(expression, CirValueScope.nzro_value)));
		}
		else if(difference == CirValueScope.nneg_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.sub_diff(expression, CirValueScope.nzro_value)));
		}
		else if(difference == CirValueScope.nzro_value) {
			execution = this.find_prior_checkpoint(execution, 
					SymbolFactory.sym_constant(Boolean.TRUE));
			source.connect(CirAnnotation.cov_stmt(execution, 1));
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_in_ext_diff(CirAnnotationNode source) throws Exception {
		/* 1. fetch the expression and the mutated value */
		CirExecution execution = source.get_annotation().get_execution();
		CirExpression expression = (CirExpression) source.get_annotation().get_store_unit();
		SymbolExpression difference = source.get_annotation().get_symb_value();
		
		/* 2. constant to given scope anyway */
		if(difference instanceof SymbolConstant) {
			double value = ((SymbolConstant) difference).get_double();
			if(value > 0) {
				this.extend_annotations_in(source.connect(CirAnnotation.ext_diff(expression, CirValueScope.post_value)));
			}
			else if(value < 0) {
				this.extend_annotations_in(source.connect(CirAnnotation.ext_diff(expression, CirValueScope.negt_value)));
			}
			else {
				execution = this.find_prior_checkpoint(execution, SymbolFactory.sym_constant(Boolean.FALSE));
				this.extend_annotations_in(source.connect(CirAnnotation.eva_expr(execution, Boolean.FALSE)));
			}
		}
		else if(difference == CirValueScope.post_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.ext_diff(expression, CirValueScope.nneg_value)));
		}
		else if(difference == CirValueScope.negt_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.ext_diff(expression, CirValueScope.npos_value)));
		}
		else if(difference == CirValueScope.npos_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.ext_diff(expression, CirValueScope.nzro_value)));
		}
		else if(difference == CirValueScope.nneg_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.ext_diff(expression, CirValueScope.nzro_value)));
		}
		else if(difference == CirValueScope.nzro_value) {
			execution = this.find_prior_checkpoint(execution, 
					SymbolFactory.sym_constant(Boolean.TRUE));
			source.connect(CirAnnotation.cov_stmt(execution, 1));
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_in_xor_diff(CirAnnotationNode source) throws Exception {
		/* 1. fetch the expression and the mutated value */
		CirExecution execution = source.get_annotation().get_execution();
		CirExpression expression = (CirExpression) source.get_annotation().get_store_unit();
		SymbolExpression difference = source.get_annotation().get_symb_value();
		
		/* 2. constant to given scope anyway */
		if(difference instanceof SymbolConstant) {
			long value = ((SymbolConstant) difference).get_long();
			if(value > 0) {
				this.extend_annotations_in(source.connect(CirAnnotation.xor_diff(expression, CirValueScope.post_value)));
			}
			else if(value < 0) {
				this.extend_annotations_in(source.connect(CirAnnotation.xor_diff(expression, CirValueScope.negt_value)));
			}
			else {
				execution = this.find_prior_checkpoint(execution, SymbolFactory.sym_constant(Boolean.FALSE));
				this.extend_annotations_in(source.connect(CirAnnotation.eva_expr(execution, Boolean.FALSE)));
			}
		}
		else if(difference == CirValueScope.post_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.xor_diff(expression, CirValueScope.nneg_value)));
		}
		else if(difference == CirValueScope.negt_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.xor_diff(expression, CirValueScope.npos_value)));
		}
		else if(difference == CirValueScope.npos_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.xor_diff(expression, CirValueScope.nzro_value)));
		}
		else if(difference == CirValueScope.nneg_value) {
			this.extend_annotations_in(source.connect(CirAnnotation.xor_diff(expression, CirValueScope.nzro_value)));
		}
		else if(difference == CirValueScope.nzro_value) {
			execution = this.find_prior_checkpoint(execution, 
					SymbolFactory.sym_constant(Boolean.TRUE));
			source.connect(CirAnnotation.cov_stmt(execution, 1));
		}
	}
	/**
	 * It extends the annotations from the source node in given tree context.
	 * @param source
	 * @throws Exception
	 */
	protected static void extend_annotations(CirAnnotationNode source) throws Exception {
		util.extend_annotations_in(source);
	}
	
	/* generation from CirAttribute to CirAnnotation* */
	private void generate_annotations_in_cover_count(CirCoverCount attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		int execution_time = attribute.get_coverage_count();
		annotations.add(CirAnnotation.cov_stmt(execution, execution_time));
	}
	private void generate_annotations_in_constraints(CirConstraint attribute, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. fetch the execution and the constraint */
		CirExecution execution = attribute.get_execution(), check_point;
		SymbolExpression constraint = attribute.get_condition();
		constraint = CirValueScope.safe_evaluate(constraint, null);
		Collection<SymbolExpression> conditions = this.get_conditions_in_conjunct(constraint);
		
		/* 2. generate the eva_expr annotations from */
		for(SymbolExpression condition : conditions) {
			check_point = this.find_prior_checkpoint(execution, condition);
			annotations.add(CirAnnotation.eva_expr(check_point, condition));
		}
		
		/* 3. generate the coverage requirement needed */
		if(conditions.isEmpty()) {
			SymbolExpression condition = SymbolFactory.sym_constant(Boolean.TRUE);
			check_point = this.find_prior_checkpoint(execution, condition);
			annotations.add(CirAnnotation.cov_stmt(check_point, 1));
		}
	}
	private void generate_annotations_in_flows_error(CirFlowsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. determine of which statements should be executed or not */
		CirExecution orig_target = attribute.get_original_flow().get_target();
		CirExecution muta_target = attribute.get_mutation_flow().get_target();
		Map<Boolean, Collection<CirExecution>> maps = this.find_add_del_executions(orig_target, muta_target);
		
		/* 2. generate the statement mutation annotations into */
		for(Boolean result : maps.keySet()) {
			for(CirExecution execution : maps.get(result)) {
				if(execution.get_statement() instanceof CirTagStatement) {
					continue;
				}
				else {
					annotations.add(CirAnnotation.mut_stmt(execution, result));
				}
			}
		}
	}
	private void generate_annotations_in_block_error(CirBlockError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		if(execution.get_statement() instanceof CirTagStatement) {
			return;
		}
		else {
			annotations.add(CirAnnotation.mut_stmt(execution, attribute.is_executed()));
		}
	}
	private void generate_annotations_in_traps_error(CirTrapsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(CirAnnotation.trp_stmt(attribute.get_execution()));
	}
	private void generate_annotations_in_exprs_error(CirExpression expression, 
			Object value, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. initialize the original and mutated annotation */
		CirExecution execution = expression.execution_of();
		CirAnnotation orig_annotation, muta_annotation;
		if(CirMutations.is_assigned(expression)) {
			CirAssignStatement stmt = (CirAssignStatement) expression.get_parent();
			orig_annotation = CirAnnotation.mut_expr(expression, stmt.get_rvalue());
		}
		else {
			orig_annotation = CirAnnotation.mut_expr(expression, expression);
		}
		muta_annotation = CirAnnotation.mut_expr(expression, value);
		SymbolExpression orig_value = orig_annotation.get_symb_value();
		SymbolExpression muta_value = muta_annotation.get_symb_value();
		
		/* 2. trapping if the mutated or original results are exception */
		if(orig_value == CirValueScope.expt_value || muta_value == CirValueScope.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution)); 
			return;
		}
		
		/* 3. compare the original and mutated value to determine false */
		if(orig_value.equals(muta_value)) {
			SymbolExpression condition = SymbolFactory.sym_constant(Boolean.FALSE);
			execution = this.find_prior_checkpoint(execution, condition);
			annotations.add(CirAnnotation.eva_expr(execution, condition));
			return;
		}
		annotations.add(muta_annotation);
		
		/* 4. difference generation will follow */
		SymbolExpression difference; 
		if(CirMutations.is_numeric(expression) || CirMutations.is_address(expression)) {
			difference = CirValueScope.sub_difference(orig_value, muta_value);
			annotations.add(CirAnnotation.sub_diff(expression, difference));
		}
		if(CirMutations.is_numeric(expression)) {
			difference = CirValueScope.ext_difference(orig_value, muta_value);
			annotations.add(CirAnnotation.ext_diff(expression, difference));
		}
		if(CirMutations.is_integer(expression)) {
			difference = CirValueScope.xor_difference(orig_value, muta_value);
			annotations.add(CirAnnotation.xor_diff(expression, difference));
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
	 * It generates the symbolic annotations from the input attribute
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	protected static void generate_annotations(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		util.generate_annotations_in(attribute, annotations);
	}
	
	/* concretization from CirAnnotation to themselves under context */
	private void concretize_annotations_in_cov_stmt(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* ignore the concretization of path condition based annotation */
	}
	private void concretize_annotations_in_eva_expr(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* ignore the concretization of path condition based annotation */
	}
	private void concretize_annotations_in_mut_stmt(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* ignore the concretization of statement based annotation */
	}
	private void concretize_annotations_in_trp_stmt(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* ignore the concretization of statement based annotation */
	}
	private void concretize_annotations_in_mut_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		SymbolExpression value = annotation.get_symb_value();
		value = CirValueScope.safe_evaluate(value, context);
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		annotations.add(CirAnnotation.mut_expr(expression, value));
	}
	private void concretize_annotations_in_sub_diff(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		SymbolExpression value = annotation.get_symb_value();
		value = CirValueScope.safe_evaluate(value, context);
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		annotations.add(CirAnnotation.sub_diff(expression, value));
	}
	private void concretize_annotations_in_ext_diff(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		SymbolExpression value = annotation.get_symb_value();
		value = CirValueScope.safe_evaluate(value, context);
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		annotations.add(CirAnnotation.ext_diff(expression, value));
	}
	private void concretize_annotations_in_xor_diff(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		SymbolExpression value = annotation.get_symb_value();
		value = CirValueScope.safe_evaluate(value, context);
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		annotations.add(CirAnnotation.xor_diff(expression, value));
	}
	private void concretize_annotations_in(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else {
			switch(annotation.get_value_type()) {
			case cov_stmt:	this.concretize_annotations_in_cov_stmt(annotation, context, annotations); break;
			case eva_expr:	this.concretize_annotations_in_eva_expr(annotation, context, annotations); break;
			case mut_stmt:	this.concretize_annotations_in_mut_stmt(annotation, context, annotations); break;
			case trp_stmt:	this.concretize_annotations_in_trp_stmt(annotation, context, annotations); break;
			case mut_expr:	this.concretize_annotations_in_mut_expr(annotation, context, annotations); break;
			case sub_diff:	this.concretize_annotations_in_sub_diff(annotation, context, annotations); break;
			case ext_diff:	this.concretize_annotations_in_ext_diff(annotation, context, annotations); break;
			case xor_diff:	this.concretize_annotations_in_xor_diff(annotation, context, annotations); break;
			default:		throw new IllegalArgumentException("Unsupport annotation: " + annotation);
			}
		}
	}
	/**
	 * It concretizes the annotation to concrete values using the context.
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	protected static void concretize_annotations(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		util.concretize_annotations_in(annotation, context, annotations);
	}
	
	/* summarization from concrete annotations to CirAnnotation */
	private void summarize_annotations_in_cov_stmt(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		abs_annotations.add(annotation);
	}
	private void summarize_annotations_in_eva_expr(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		abs_annotations.add(annotation);
	}
	private void summarize_annotations_in_mut_stmt(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		abs_annotations.add(annotation);
	}
	private void summarize_annotations_in_trp_stmt(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		abs_annotations.add(annotation);
	}
	private void summarize_annotations_in_mut_expr(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		/* 1. fecth the expression and concrete values */
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		CirExecution execution = annotation.get_execution();
		Collection<SymbolExpression> values = new HashSet<SymbolExpression>();
		for(CirAnnotation con_annotation : con_annotations) {
			SymbolExpression value = con_annotation.get_symb_value();
			if(value == CirValueScope.expt_value) {
				abs_annotations.add(CirAnnotation.trp_stmt(execution));
				return;
			}
			else if(value instanceof SymbolConstant) {
				values.add(value);
			}
		}
		
		/* 2. extract the abstract domains from concrete values */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirValueScope.get_scopes_in(expression, values, domains);
		for(SymbolExpression domain : domains) {
			abs_annotations.add(CirAnnotation.mut_expr(expression, domain));
		}
		abs_annotations.add(annotation);
	}
	private void summarize_annotations_in_sub_diff(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		/* 1. fecth the expression and concrete values */
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		CirExecution execution = annotation.get_execution();
		Collection<SymbolExpression> values = new HashSet<SymbolExpression>();
		for(CirAnnotation con_annotation : con_annotations) {
			SymbolExpression value = con_annotation.get_symb_value();
			if(value == CirValueScope.expt_value) {
				abs_annotations.add(CirAnnotation.trp_stmt(execution));
				return;
			}
			else if(value instanceof SymbolConstant) {
				values.add(value);
			}
		}
		
		/* 2. extract the abstract domains from concrete values */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirValueScope.get_scopes_in(expression, values, domains);
		for(SymbolExpression domain : domains) {
			abs_annotations.add(CirAnnotation.sub_diff(expression, domain));
		}
	}
	private void summarize_annotations_in_ext_diff(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		/* 1. fecth the expression and concrete values */
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		CirExecution execution = annotation.get_execution();
		Collection<SymbolExpression> values = new HashSet<SymbolExpression>();
		for(CirAnnotation con_annotation : con_annotations) {
			SymbolExpression value = con_annotation.get_symb_value();
			if(value == CirValueScope.expt_value) {
				abs_annotations.add(CirAnnotation.trp_stmt(execution));
				return;
			}
			else if(value instanceof SymbolConstant) {
				values.add(value);
			}
		}
		
		/* 2. extract the abstract domains from concrete values */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirValueScope.get_scopes_in(expression, values, domains);
		for(SymbolExpression domain : domains) {
			abs_annotations.add(CirAnnotation.ext_diff(expression, domain));
		}
	}
	private void summarize_annotations_in_xor_diff(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		/* 1. fecth the expression and concrete values */
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		CirExecution execution = annotation.get_execution();
		Collection<SymbolExpression> values = new HashSet<SymbolExpression>();
		for(CirAnnotation con_annotation : con_annotations) {
			SymbolExpression value = con_annotation.get_symb_value();
			if(value == CirValueScope.expt_value) {
				abs_annotations.add(CirAnnotation.trp_stmt(execution));
				return;
			}
			else if(value instanceof SymbolConstant) {
				values.add(value);
			}
		}
		
		/* 2. extract the abstract domains from concrete values */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirValueScope.get_scopes_in(expression, values, domains);
		for(SymbolExpression domain : domains) {
			abs_annotations.add(CirAnnotation.xor_diff(expression, domain));
		}
	}
	private void summarize_annotations_in(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else if(con_annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else if(abs_annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else {
			switch(annotation.get_value_type()) {
			case cov_stmt:	this.summarize_annotations_in_cov_stmt(annotation, con_annotations, abs_annotations); break;
			case eva_expr:	this.summarize_annotations_in_eva_expr(annotation, con_annotations, abs_annotations); break;
			case mut_stmt:	this.summarize_annotations_in_mut_stmt(annotation, con_annotations, abs_annotations); break;
			case trp_stmt:	this.summarize_annotations_in_trp_stmt(annotation, con_annotations, abs_annotations); break;
			case mut_expr:	this.summarize_annotations_in_mut_expr(annotation, con_annotations, abs_annotations); break;
			case sub_diff:	this.summarize_annotations_in_sub_diff(annotation, con_annotations, abs_annotations); break;
			case ext_diff:	this.summarize_annotations_in_ext_diff(annotation, con_annotations, abs_annotations); break;
			case xor_diff:	this.summarize_annotations_in_xor_diff(annotation, con_annotations, abs_annotations); break;
			default:		throw new IllegalArgumentException("Unsupport annotation: " + annotation);
			}
		}
	}
	/**
	 * It summarizes the annotations from the symbolic and concrete ones.
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	protected static void summarize_annotations(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		util.summarize_annotations_in(annotation, con_annotations, abs_annotations);
	}
	
}
