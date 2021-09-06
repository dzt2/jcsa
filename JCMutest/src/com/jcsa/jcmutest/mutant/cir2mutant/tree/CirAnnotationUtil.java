package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirInfection;
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
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;


/**
 * It implements the generation, concretization and summarization of CirAnnotation(s)
 * from CirAttribute or CirAnnotation itself.
 * 
 * @author yukimula
 *
 */
class CirAnnotationUtil {
	
	/* singleton */	/** constructors **/	private CirAnnotationUtil() {}
	private static final CirAnnotationUtil util = new CirAnnotationUtil();
	
	/* basic methods */
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
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private void generate_symbol_conditions_in(SymbolExpression expression, Collection<SymbolExpression> conditions) throws Exception {
		Set<SymbolExpression> sub_expressions = new HashSet<SymbolExpression>();
		this.get_symbol_conditions_in(expression, sub_expressions);
		for(SymbolExpression sub_expression : sub_expressions) {
			conditions.add(sub_expression);
			if(sub_expression instanceof SymbolBinaryExpression) {
				COperator operator = ((SymbolBinaryExpression) sub_expression).get_operator().get_operator();
				SymbolExpression loperand = ((SymbolBinaryExpression) sub_expression).get_loperand();
				SymbolExpression roperand = ((SymbolBinaryExpression) sub_expression).get_roperand();
				switch(operator) {
				case greater_tn:	/** (>, >=, !=) **/
				{
					conditions.add(SymbolFactory.greater_eq(loperand, roperand));
					conditions.add(SymbolFactory.not_equals(loperand, roperand));
					break;
				}
				case smaller_tn:	/** (<, <=, !=) **/
				{
					conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
					conditions.add(SymbolFactory.not_equals(loperand, roperand));
					break;
				}
				case equal_with:	/** (==, <=, >=) **/
				{
					conditions.add(SymbolFactory.greater_eq(loperand, roperand));
					conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
					break;
				}
				default:			
				{
					break;
				}
				}
			}
		}
	}
	/**
	 * @param expression
	 * @return the set of symbolic conditions divided and inferred from the expression
	 * @throws Exception
	 */
	private Collection<SymbolExpression> generate_symbolic_conditions(SymbolExpression expression) throws Exception {
		/* 1. simplify the input expression at first */
		expression = this.symbol_evaluate(expression, null);
		
		/* 2. generate sub_expressions from the input */
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.generate_symbol_conditions_in(expression, conditions);
		
		/* 3. simplify the final output expressions */
		Set<SymbolExpression> expressions = new HashSet<SymbolExpression>();
		for(SymbolExpression condition : conditions) {
			expressions.add(this.symbol_evaluate(condition, null));
		}
		
		/* 4. complement the expressions when it is empty */
		if(expressions.isEmpty()) {
			expressions.add(SymbolFactory.sym_constant(Boolean.TRUE));
		}
		return expressions;
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
	 * @param expression
	 * @param context
	 * @return symbolic evaluation
	 * @throws Exception
	 */
	private SymbolExpression symbol_evaluate(SymbolExpression expression, SymbolProcess context) throws Exception {
		return SymbolEvaluator.evaluate_on(expression, context);
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
	/**
	 * @param execution
	 * @param condition
	 * @return the annotation that can best describe the input condition at specified point
	 * @throws Exception
	 */
	private CirAnnotation get_condition_annotation(CirExecution check_point, SymbolExpression condition) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				return CirAnnotation.cov_stmt(check_point, 1);
			}
			else {
				return CirAnnotation.eva_expr(check_point, Boolean.FALSE, true);
			}
		}
		else {
			return CirAnnotation.eva_expr(check_point, condition, true);
		}
	}
	
	/* symbolic annotation generators */
	/**
	 * It generates the symbolic annotation(s) that represent the source attribute statically
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
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
	 * It generates the symbolic annotations of coverage counter using cov_stmt(1), cov_stmt(2) --> cov_stmt(n)
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
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
	/**
	 * It divides the constraint into sub_expression (conditions) and evaluate them at availabl point
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_constraints(CirConstraint attribute, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. capture the symbolic conditions required in constraint */
		CirExecution execution = attribute.get_execution(), check_point;
		Collection<SymbolExpression> conditions = this.
				generate_symbolic_conditions(attribute.get_condition());
		
		/* 2. determine the check_point and generate the annotations */
		for(SymbolExpression condition : conditions) {
			check_point = this.find_prior_checkpoint(execution, condition);
			annotations.add(this.get_condition_annotation(check_point, condition));
		}
	}
	/**
	 * It generates the statement error annotations from block-error attribute statically
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_block_error(CirBlockError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		if(attribute.is_executed()) {
			annotations.add(CirAnnotation.add_stmt(execution));
		}
		else {
			annotations.add(CirAnnotation.del_stmt(execution));
		}
	}
	/**
	 * It generates the flow error and statement errors from the flow extended
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_flows_error(CirFlowsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecutionFlow orig_flow = attribute.get_original_flow();
		CirExecutionFlow muta_flow = attribute.get_mutation_flow();
		
		if(orig_flow.get_target() != muta_flow.get_target()) {
			/* capture the statement error annotations from flow-error attribute */
			Map<Boolean, Collection<CirExecution>> results = this.
					get_add_del_executions(orig_flow.get_target(), muta_flow.get_target());
			for(Boolean result : results.keySet()) {
				for(CirExecution execution : results.get(result)) {
					if(execution.get_statement() instanceof CirTagStatement) {
						continue;
					}
					else if(result) {
						annotations.add(CirAnnotation.add_stmt(execution));
					}
					else {
						annotations.add(CirAnnotation.del_stmt(execution));
					}
				}
			}
			
			/* generate the flow-error annotations from */
			if(!annotations.isEmpty()) {
				annotations.add(CirAnnotation.mut_flow(orig_flow, muta_flow));
			}
		}
	}
	/**
	 * It directly maps the trap attribute to trap annotation.
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_traps_error(CirTrapsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution().get_graph().get_exit();
		annotations.add(CirAnnotation.trp_stmt(execution));
	}
	/**
	 * It generates the set_expr, sub_expr, xor_expr, ext_expr error annotations.
	 * @param expression
	 * @param muta_expression
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_exprs_error(CirExpression expression, 
			SymbolExpression muta_expression, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. initialization and computation */
		CirExecution execution = expression.execution_of(), check_point;
		SymbolExpression orig_expression = SymbolFactory.sym_expression(expression);
		orig_expression = this.symbol_evaluate(orig_expression, null);
		try {
			muta_expression = this.symbol_evaluate(muta_expression, null);
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution));
			return;
		}
		
		/* 2. compare the original and mutation part and generate FALSE */
		if(orig_expression.equals(muta_expression)) {
			SymbolExpression condition = SymbolFactory.sym_constant(Boolean.FALSE);
			check_point = this.find_prior_checkpoint(execution, condition);
			annotations.add(CirAnnotation.eva_expr(check_point, condition, true));
			return;		/** identical value will be ignored for further analysis **/
		}
		else {
			annotations.add(CirAnnotation.set_expr(expression, muta_expression));
		}
		
		/* 3. differential analysis between original and mutation values */
		SymbolExpression difference;
		if(CirInfection.is_numeric(expression) || CirInfection.is_pointer(expression)) {
			difference = CirAnnotationScope.sub_difference(orig_expression, muta_expression);
			difference = this.symbol_evaluate(difference, null);
			annotations.add(CirAnnotation.sub_expr(expression, difference));
		}
		if(CirInfection.is_numeric(expression)) {
			difference = CirAnnotationScope.ext_difference(orig_expression, muta_expression);
			difference = this.symbol_evaluate(difference, null);
			annotations.add(CirAnnotation.ext_expr(expression, difference));
		}
		if(CirInfection.is_integer(expression)) {
			difference = CirAnnotationScope.xor_difference(orig_expression, muta_expression);
			difference = this.symbol_evaluate(difference, null);
			annotations.add(CirAnnotation.xor_expr(expression, difference));
		}
	}
	/**
	 * It generates the symbolic annotations from weakly killed value error
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_difer_error(CirDiferError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression muta_expression = attribute.get_parameter();
		this.generate_annotations_in_exprs_error(expression, muta_expression, annotations);
	}
	/**
	 * It generates the symbolic annotations from value error (strongly kill)
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_value_error(CirValueError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression muta_expression = attribute.get_parameter();
		this.generate_annotations_in_exprs_error(expression, muta_expression, annotations);
	}
	/**
	 * It generates the symbolic annotations from reference error (strongly)
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_refer_error(CirReferError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression muta_expression = attribute.get_parameter();
		this.generate_annotations_in_exprs_error(expression, muta_expression, annotations);
	}
	/**
	 * It generates the annotations from the state mutation error.
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_state_error(CirStateError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression muta_expression = attribute.get_muta_expression();
		try {
			muta_expression = this.symbol_evaluate(muta_expression, null);
			annotations.add(CirAnnotation.mut_stat(expression, muta_expression));
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution.get_graph().get_exit()));
		}
	}
	/**
	 * It generates the symbolic annotation(s) that represent the source attribute statically
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	protected static void generate_annotations(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		util.generate_annotations_in(attribute, annotations);
	}
	
	/* concrete evaluation generators */
	/**
	 * It generates the concrete annotations from symbolic annotation using given context.
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @return whether the symbolic annotation is satisfied or not or unknown.
	 * @throws Exception
	 */
	private Boolean concretize_annotations_in(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid output specified");
		}
		else {
			/* syntax-directed translation for concretizing annotations */
			switch(annotation.get_operator()) {
			case cov_stmt:	return this.concretize_annotations_in_cov_stmt(annotation, context, annotations);
			case eva_expr:	return this.concretize_annotations_in_eva_expr(annotation, context, annotations);
			case mut_flow:	return this.concretize_annotations_in_mut_flow(annotation, context, annotations);
			case mut_stmt:	return this.concretize_annotations_in_mut_stmt(annotation, context, annotations);
			case mut_stat:	return this.concretize_annotations_in_mut_stat(annotation, context, annotations);
			case trp_stmt:	return this.concretize_annotations_in_trp_stmt(annotation, context, annotations);
			case set_expr:	return this.concretize_annotations_in_set_expr(annotation, context, annotations);
			case sub_expr:	return this.concretize_annotations_in_sub_expr(annotation, context, annotations);
			case ext_expr:	return this.concretize_annotations_in_ext_expr(annotation, context, annotations);
			case xor_expr:	return this.concretize_annotations_in_xor_expr(annotation, context, annotations);
			default:		throw new IllegalArgumentException("Unsupported: " + annotation);
			}
		}
	}
	/**
	 * It concretizes the cov_stmt annotation using constraint context
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private Boolean concretize_annotations_in_cov_stmt(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		if(context != null) {
			/* fetch the times of the execution being performed */
			SymbolExpression source = SymbolFactory.sym_expression(annotation.get_execution());
			SymbolExpression target = context.get_data_stack().load(source);
			int executed_times;
			if(target == null) {
				executed_times = 0;
			}
			else {
				executed_times = ((SymbolConstant) target).get_int().intValue();
			}
			
			/* determine whether the coverage requirement is achieved */
			int execution_times = ((SymbolConstant) annotation.get_parameter()).get_int();
			annotations.add(CirAnnotation.eva_expr(annotation.get_execution(), 
					Boolean.valueOf(execution_times <= executed_times), true));
			return Boolean.valueOf(execution_times <= executed_times);
		}
		else {
			return null;
		}
	}
	/**
	 * It concretizes the eva_expr annotation using constraint context
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private Boolean concretize_annotations_in_eva_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. determine whether the condition is satisfied in testing */
		SymbolExpression condition = annotation.get_parameter();
		try {
			condition = this.symbol_evaluate(condition, context);
		}
		catch(ArithmeticException ex) {
			condition = SymbolFactory.sym_constant(Boolean.TRUE);
		}
		
		/* 2. generate the annotations for concretization */
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				annotations.add(CirAnnotation.eva_expr(
						annotation.get_execution(), Boolean.TRUE, true));
			}
			else {
				annotations.add(CirAnnotation.eva_expr(
						annotation.get_execution(), Boolean.FALSE, true));
			}
			return ((SymbolConstant) condition).get_bool();
		}
		else {
			return null;
		}
	}
	/**
	 * It concretizes the mut_flow annotation dynamically
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private Boolean concretize_annotations_in_mut_flow(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. get the original and mutated target of flows */
		CirExecution orig_target = annotation.get_location().execution_of();
		CirExecution muta_target = (CirExecution) annotation.get_parameter().get_source();
		
		/* 2. concretize the statement error annotations */
		Collection<CirAnnotation> new_annotations = new HashSet<CirAnnotation>();
		if(orig_target != muta_target) {
			/* capture the statement error annotations from flow-error attribute */
			Map<Boolean, Collection<CirExecution>> results = this.
					get_add_del_executions(orig_target, muta_target);
			for(Boolean result : results.keySet()) {
				for(CirExecution execution : results.get(result)) {
					if(execution.get_statement() instanceof CirTagStatement) {
						continue;
					}
					else if(result) {
						new_annotations.add(CirAnnotation.add_stmt(execution));
					}
					else {
						new_annotations.add(CirAnnotation.del_stmt(execution));
					}
				}
			}
		}
		
		/* 3. generate the total annotations from concretization */
		if(new_annotations.isEmpty()) {
			return Boolean.FALSE;
		}
		else {
			for(CirAnnotation new_annotation : new_annotations) {
				annotations.add(new_annotation);
			}
			return Boolean.TRUE;
		}
	}
	/**
	 * It concretizes the mut_stmt annotation dynamically
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private Boolean concretize_annotations_in_mut_stmt(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = annotation.get_execution();
		if(execution.get_statement() instanceof CirTagStatement) {
			return Boolean.FALSE;
		}
		else {
			annotations.add(annotation);
			return Boolean.TRUE;
		}
	}
	/**
	 * It concretizes the mut_stat annotation using context information
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @return 
	 * @throws Exception
	 */
	private Boolean concretize_annotations_in_mut_stat(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		SymbolExpression muta_value = annotation.get_parameter();
		CirExecution execution = annotation.get_execution();
		try {
			muta_value = this.symbol_evaluate(muta_value, context);
			annotations.add(CirAnnotation.mut_stat((CirExpression) annotation.get_location(), muta_value));
			return Boolean.TRUE;
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution.get_graph().get_exit()));
			return Boolean.TRUE;
		}
	}
	/**
	 * It concretizes the trp_stmt annotation using context information
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @return
	 * @throws Exception
	 */
	private Boolean concretize_annotations_in_trp_stmt(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(annotation);
		return Boolean.TRUE;
	}
	/**
	 * It concretizes the set_expr annotation using context information
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @return
	 * @throws Exception
	 */
	private Boolean concretize_annotations_in_set_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = annotation.get_execution();
		CirExpression expression = (CirExpression) annotation.get_location();
		SymbolExpression orig_expression = SymbolFactory.sym_expression(expression);
		SymbolExpression muta_expression = annotation.get_parameter();
		
		try {
			orig_expression = this.symbol_evaluate(orig_expression, context);
			muta_expression = this.symbol_evaluate(muta_expression, context);
			
			if(orig_expression.equals(muta_expression)) { return Boolean.FALSE; }
			else if(muta_expression instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) muta_expression;
				if(CirInfection.is_boolean(expression)) {
					muta_expression = SymbolFactory.sym_constant(constant.get_bool());
				}
				else if(CirInfection.is_integer(expression)) {
					muta_expression = SymbolFactory.sym_constant(constant.get_long());
				}
				else if(CirInfection.is_numeric(expression)) {
					muta_expression = SymbolFactory.sym_constant(constant.get_double());
				}
				else if(CirInfection.is_pointer(expression)) {
					muta_expression = SymbolFactory.sym_constant(constant.get_long());
				}
				else {
					muta_expression = SymbolFactory.sym_constant(constant.get_constant());
				}
				annotations.add(CirAnnotation.set_expr(expression, muta_expression));
				return Boolean.TRUE;
			}
			else { return null;	/* undecidable determination */	}
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution.get_graph().get_exit()));
			return Boolean.TRUE;
		}
	}
	/**
	 * It concretizes the sub_expr annotation using context information
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @return
	 * @throws Exception
	 */
	private Boolean concretize_annotations_in_sub_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = annotation.get_execution();
		CirExpression expression = (CirExpression) annotation.get_location();
		SymbolExpression difference = annotation.get_parameter();
		
		try {
			difference = this.symbol_evaluate(difference, context);
			if(difference instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) difference;
				if(CirInfection.is_integer(expression)) {
					if(constant.get_long().longValue() == 0L) {
						return Boolean.FALSE;
					}
					else {
						difference = SymbolFactory.sym_constant(constant.get_long());
						annotations.add(CirAnnotation.sub_expr(expression, difference));
						return Boolean.TRUE;
					}
				}
				else if(CirInfection.is_numeric(expression)) {
					if(constant.get_double() == 0.0) {
						return Boolean.FALSE;
					}
					else {
						difference = SymbolFactory.sym_constant(constant.get_double());
						annotations.add(CirAnnotation.sub_expr(expression, difference));
						return Boolean.TRUE;
					}
					
				}
				else {
					if(constant.get_long().longValue() == 0L) {
						return Boolean.FALSE;
					}
					else {
						difference = SymbolFactory.sym_constant(constant.get_long());
						annotations.add(CirAnnotation.sub_expr(expression, difference));
						return Boolean.TRUE;
					}
				}
			}
			else { return null;	/* undecidable determination */ }
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution.get_graph().get_exit()));
			return Boolean.TRUE;
		}
	}
	/**
	 * It concretizes the ext_expr annotation using context information
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @return
	 * @throws Exception
	 */
	private Boolean concretize_annotations_in_ext_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = annotation.get_execution();
		CirExpression expression = (CirExpression) annotation.get_location();
		SymbolExpression difference = annotation.get_parameter();
		
		try {
			difference = this.symbol_evaluate(difference, context);
			if(difference instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) difference;
				if(CirInfection.is_integer(expression)) {
					if(constant.get_long().longValue() == 0L) {
						return Boolean.FALSE;
					}
					else {
						difference = SymbolFactory.sym_constant(constant.get_long());
						annotations.add(CirAnnotation.ext_expr(expression, difference));
						return Boolean.TRUE;
					}
				}
				else {
					if(constant.get_double() == 0.0) {
						return Boolean.FALSE;
					}
					else {
						difference = SymbolFactory.sym_constant(constant.get_double());
						annotations.add(CirAnnotation.ext_expr(expression, difference));
						return Boolean.TRUE;
					}
					
				}
			}
			else { return null;	/* undecidable determination */ }
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution.get_graph().get_exit()));
			return Boolean.TRUE;
		}
	}
	/**
	 * It concretizes the xor_expr annotation using context information
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @return
	 * @throws Exception
	 */
	private Boolean concretize_annotations_in_xor_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = annotation.get_execution();
		CirExpression expression = (CirExpression) annotation.get_location();
		SymbolExpression difference = annotation.get_parameter();
		
		try {
			difference = this.symbol_evaluate(difference, context);
			if(difference instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) difference;
				if(constant.get_long() == 0L) { 
					return Boolean.FALSE;
				}
				else {
					difference = SymbolFactory.sym_constant(constant.get_long());
					annotations.add(CirAnnotation.xor_expr(expression, difference));
					return Boolean.TRUE;
				}
			}
			else { return null;	/* undecidable determination */ }
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution.get_graph().get_exit()));
			return Boolean.TRUE;
		}
	}
	/**
	 * It generates the concrete annotations from symbolic annotation using given context.
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	protected static Boolean concretize_annotations(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		return util.concretize_annotations_in(annotation, context, annotations);
	}
	
	/* abstract summairzation generators */
	/**
	 * It captures the value domains hold by concrete-annotations when take them as boolean parameters
	 * @param concrete_annotations
	 * @return
	 * @throws Exception
	 */
	private Collection<SymbolExpression> capture_domains_in_boolean(Iterable<CirAnnotation> concrete_annotations) throws Exception {
		Set<SymbolExpression> domains = new HashSet<SymbolExpression>();
		for(CirAnnotation annotation : concrete_annotations) {
			SymbolExpression parameter = annotation.get_parameter();
			if(parameter instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) parameter;
				if(constant.get_bool()) {
					domains.add(CirAnnotationScope.get_true_scope());
				}
				else {
					domains.add(CirAnnotationScope.get_fals_scope());
				}
			}
		}
		return domains;
	}
	/**
	 * It captures the value domains hold by concrete-annotations when take them as integer parameters
	 * @param concrete_annotations
	 * @return
	 * @throws Exception
	 */
	private Collection<SymbolExpression> capture_domains_in_integer(Iterable<CirAnnotation> concrete_annotations) throws Exception {
		Set<SymbolExpression> domains = new HashSet<SymbolExpression>();
		for(CirAnnotation annotation : concrete_annotations) {
			SymbolExpression parameter = annotation.get_parameter();
			if(parameter instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) parameter;
				if(constant.get_long() > 0) {
					domains.add(CirAnnotationScope.get_post_scope());
				}
				else if(constant.get_long() < 0) {
					domains.add(CirAnnotationScope.get_negt_scope());
				}
				else {
					domains.add(CirAnnotationScope.get_zero_scope());
				}
			}
		}
		return domains;
	}
	/**
	 * It captures the value domains hold by concrete-annotations when take them as integer parameters
	 * @param concrete_annotations
	 * @return
	 * @throws Exception
	 */
	private Collection<SymbolExpression> capture_domains_in_double(Iterable<CirAnnotation> concrete_annotations) throws Exception {
		Set<SymbolExpression> domains = new HashSet<SymbolExpression>();
		for(CirAnnotation annotation : concrete_annotations) {
			SymbolExpression parameter = annotation.get_parameter();
			if(parameter instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) parameter;
				if(constant.get_double() > 0) {
					domains.add(CirAnnotationScope.get_post_scope());
				}
				else if(constant.get_double() < 0) {
					domains.add(CirAnnotationScope.get_negt_scope());
				}
				else {
					domains.add(CirAnnotationScope.get_zero_scope());
				}
			}
		}
		return domains;
	}
	/**
	 * It captures the value domains hold by concrete-annotations when take them as integer parameters
	 * @param concrete_annotations
	 * @return
	 * @throws Exception
	 */
	private Collection<SymbolExpression> capture_domains_in_pointer(Iterable<CirAnnotation> concrete_annotations) throws Exception {
		Set<SymbolExpression> domains = new HashSet<SymbolExpression>();
		for(CirAnnotation annotation : concrete_annotations) {
			SymbolExpression parameter = annotation.get_parameter();
			if(parameter instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) parameter;
				if(constant.get_long() == 0L) {
					domains.add(CirAnnotationScope.get_null_scope());
				}
				else {
					domains.add(CirAnnotationScope.get_invp_scope());
				}
			}
		}
		return domains;
	}
	/**
	 * It summarizes the type-based expression's value domains from concrete annotations
	 * @param expression
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_value_domains_in(CirExpression expression, 
			Collection<CirAnnotation> concrete_annotations, Collection<SymbolExpression> scopes) throws Exception {
		Collection<SymbolExpression> domains;
		if(CirInfection.is_boolean(expression)) {
			domains = this.capture_domains_in_boolean(concrete_annotations);
			if(domains.contains(CirAnnotationScope.get_true_scope())) {
				if(domains.contains(CirAnnotationScope.get_fals_scope())) {
					scopes.add(CirAnnotationScope.get_bool_scope());
				}
				else {
					scopes.add(CirAnnotationScope.get_true_scope());
					scopes.add(CirAnnotationScope.get_bool_scope());
				}
			}
			else {
				if(domains.contains(CirAnnotationScope.get_fals_scope())) {
					scopes.add(CirAnnotationScope.get_fals_scope());
					scopes.add(CirAnnotationScope.get_bool_scope());
				}
				else { }
			}
		}
		else if(CirInfection.is_integer(expression)) {
			domains = this.capture_domains_in_integer(concrete_annotations);
			if(domains.contains(CirAnnotationScope.get_post_scope())) {
				if(domains.contains(CirAnnotationScope.get_negt_scope())) {
					if(domains.contains(CirAnnotationScope.get_zero_scope())) {			/** {+, -, 0} **/
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
					else {																/** {+, -} **/
						scopes.add(CirAnnotationScope.get_nzro_scope());
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
				}
				else {
					if(domains.contains(CirAnnotationScope.get_zero_scope())) {			/** {+, 0} **/
						scopes.add(CirAnnotationScope.get_nneg_scope());
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
					else {																/** {+} **/
						scopes.add(CirAnnotationScope.get_post_scope());
						scopes.add(CirAnnotationScope.get_nneg_scope());
						scopes.add(CirAnnotationScope.get_nzro_scope());
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
				}
			}
			else {
				if(domains.contains(CirAnnotationScope.get_negt_scope())) {
					if(domains.contains(CirAnnotationScope.get_zero_scope())) {			/** {-, 0} **/
						scopes.add(CirAnnotationScope.get_npos_scope());
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
					else {																/** {-} **/
						scopes.add(CirAnnotationScope.get_negt_scope());
						scopes.add(CirAnnotationScope.get_npos_scope());
						scopes.add(CirAnnotationScope.get_nzro_scope());
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
				}
				else {
					if(domains.contains(CirAnnotationScope.get_zero_scope())) {			/** {0} **/
						scopes.add(CirAnnotationScope.get_zero_scope());
						scopes.add(CirAnnotationScope.get_nneg_scope());
						scopes.add(CirAnnotationScope.get_npos_scope());
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
					else { /* no valid scope is incorporated in the analysis */ }
				}
			}
		}
		else if(CirInfection.is_numeric(expression)) {
			domains = this.capture_domains_in_double(concrete_annotations);
			if(domains.contains(CirAnnotationScope.get_post_scope())) {
				if(domains.contains(CirAnnotationScope.get_negt_scope())) {
					if(domains.contains(CirAnnotationScope.get_zero_scope())) {			/** {+, -, 0} **/
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
					else {																/** {+, -} **/
						scopes.add(CirAnnotationScope.get_nzro_scope());
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
				}
				else {
					if(domains.contains(CirAnnotationScope.get_zero_scope())) {			/** {+, 0} **/
						scopes.add(CirAnnotationScope.get_nneg_scope());
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
					else {																/** {+} **/
						scopes.add(CirAnnotationScope.get_post_scope());
						scopes.add(CirAnnotationScope.get_nneg_scope());
						scopes.add(CirAnnotationScope.get_nzro_scope());
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
				}
			}
			else {
				if(domains.contains(CirAnnotationScope.get_negt_scope())) {
					if(domains.contains(CirAnnotationScope.get_zero_scope())) {			/** {-, 0} **/
						scopes.add(CirAnnotationScope.get_npos_scope());
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
					else {																/** {-} **/
						scopes.add(CirAnnotationScope.get_negt_scope());
						scopes.add(CirAnnotationScope.get_npos_scope());
						scopes.add(CirAnnotationScope.get_nzro_scope());
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
				}
				else {
					if(domains.contains(CirAnnotationScope.get_zero_scope())) {			/** {0} **/
						scopes.add(CirAnnotationScope.get_zero_scope());
						scopes.add(CirAnnotationScope.get_nneg_scope());
						scopes.add(CirAnnotationScope.get_npos_scope());
						scopes.add(CirAnnotationScope.get_numb_scope());
					}
					else { /* no valid scope is incorporated in the analysis */ }
				}
			}
		}
		else if(CirInfection.is_pointer(expression)) {
			domains = this.capture_domains_in_pointer(concrete_annotations);
			if(domains.contains(CirAnnotationScope.get_null_scope())) {
				if(domains.contains(CirAnnotationScope.get_invp_scope())) {
					scopes.add(CirAnnotationScope.get_addr_scope());
				}
				else {
					scopes.add(CirAnnotationScope.get_addr_scope());
					scopes.add(CirAnnotationScope.get_null_scope());
				}
			}
			else {
				if(domains.contains(CirAnnotationScope.get_invp_scope())) {
					scopes.add(CirAnnotationScope.get_addr_scope());
					scopes.add(CirAnnotationScope.get_invp_scope());
				}
				else { /* no valid scope is incorporated in the analysis */ }
			}
		}
		else { /* no valid scope is incorporated in the analysis */ }
	}
	/**
	 * It generates the summarization of symbolic annotation using its concrete instances
	 * @param symbolic_annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in(CirAnnotation symbolic_annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(symbolic_annotation == null) {
			throw new IllegalArgumentException("Invalid symbolic_annotation: null");
		}
		else if(concrete_annotations == null) {
			throw new IllegalArgumentException("Invalid concrete_annotations: null");
		}
		else if(abstract_annotations == null) {
			throw new IllegalArgumentException("Invalid abstract_annotations: null");
		}
		else {
			switch(symbolic_annotation.get_operator()) {
			case cov_stmt:	
				this.summarize_annotations_in_cov_stmt(symbolic_annotation, concrete_annotations, abstract_annotations); 
				break;
			case eva_expr:	
				this.summarize_annotations_in_eva_expr(symbolic_annotation, concrete_annotations, abstract_annotations);
				break;
			case mut_stmt:	
				this.summarize_annotations_in_mut_stmt(symbolic_annotation, concrete_annotations, abstract_annotations);
				break;
			case mut_flow:	
				this.summarize_annotations_in_mut_flow(symbolic_annotation, concrete_annotations, abstract_annotations);
				break;
			case trp_stmt:	
				this.summarize_annotations_in_trp_stmt(symbolic_annotation, concrete_annotations, abstract_annotations);
				break;
			case mut_stat:	
				this.summarize_annotations_in_mut_stat(symbolic_annotation, concrete_annotations, abstract_annotations);
				break;
			case set_expr:	
				this.summarize_annotations_in_set_expr(symbolic_annotation, concrete_annotations, abstract_annotations);
				break;
			case sub_expr:	
				this.summarize_annotations_in_sub_expr(symbolic_annotation, concrete_annotations, abstract_annotations);
				break;
			case ext_expr:
				this.summarize_annotations_in_ext_expr(symbolic_annotation, concrete_annotations, abstract_annotations);
				break;
			case xor_expr:
				this.summarize_annotations_in_xor_expr(symbolic_annotation, concrete_annotations, abstract_annotations);
				break;
			default:		throw new IllegalArgumentException("Invalid annotation:" + symbolic_annotation);
			}
		}
	}
	/**
	 * It summarizes the cov_stmt annotation from the attribute using its records
	 * @param symbolic_annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_cov_stmt(CirAnnotation symbolic_annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.add(symbolic_annotation);
	}
	/**
	 * It summarizes the eva_expr annotation from its concrete instances 
	 * @param symbolic_annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_eva_expr(CirAnnotation symbolic_annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.add(symbolic_annotation);
	}
	/**
	 * It summarizes the mut_stmt annotation from concrete ones
	 * @param symbolic_annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_stmt(CirAnnotation symbolic_annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(!concrete_annotations.isEmpty())
			abstract_annotations.add(symbolic_annotation);
	}
	/**
	 * It summarizes the mut_stmt annotation from concrete ones
	 * @param symbolic_annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_flow(CirAnnotation symbolic_annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(concrete_annotations);
	}
	/**
	 * It summarizes the mut_stmt annotation from concrete ones
	 * @param symbolic_annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_trp_stmt(CirAnnotation symbolic_annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(concrete_annotations);
	}
	/**
	 * @param symbolic_annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_stat(CirAnnotation symbolic_annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* 1. incorporate trp_stmt if exception occurs in testing */
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			if(concrete_annotation.get_operator() == CirAnnotationType.trp_stmt) {
				abstract_annotations.add(concrete_annotation);
				return;
			}
		}
		
		/* 2. otherwise, incorporate the set-state annotation */
		abstract_annotations.add(symbolic_annotation);
		
		/* 3. summarize the value scopes from concrete ones */
		CirExpression expression = (CirExpression) symbolic_annotation.get_location();
		Collection<SymbolExpression> scopes = new HashSet<SymbolExpression>();
		this.summarize_value_domains_in(expression, concrete_annotations, scopes);
		for(SymbolExpression scope : scopes) {
			abstract_annotations.add(CirAnnotation.mut_stat(expression, scope));
		}
	}
	/**
	 * @param symbolic_annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_set_expr(CirAnnotation symbolic_annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* 1. incorporate trp_stmt if exception occurs in testing */
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			if(concrete_annotation.get_operator() == CirAnnotationType.trp_stmt) {
				abstract_annotations.add(concrete_annotation);
				return;
			}
		}
		
		/* 2. otherwise, incorporate the set-state annotation */
		abstract_annotations.add(symbolic_annotation);
		
		/* 3. summarize the value scopes from concrete ones */
		CirExpression expression = (CirExpression) symbolic_annotation.get_location();
		Collection<SymbolExpression> scopes = new HashSet<SymbolExpression>();
		this.summarize_value_domains_in(expression, concrete_annotations, scopes);
		for(SymbolExpression scope : scopes) {
			abstract_annotations.add(CirAnnotation.set_expr(expression, scope));
		}
	}
	/**
	 * @param symbolic_annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_sub_expr(CirAnnotation symbolic_annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* 1. incorporate trp_stmt if exception occurs in testing */
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			if(concrete_annotation.get_operator() == CirAnnotationType.trp_stmt) {
				abstract_annotations.add(concrete_annotation);
				return;
			}
		}
		
		/* 2. otherwise, incorporate the set-state annotation */
		abstract_annotations.add(symbolic_annotation);
		
		/* 3. summarize the value scopes from concrete ones */
		CirExpression expression = (CirExpression) symbolic_annotation.get_location();
		Collection<SymbolExpression> scopes = new HashSet<SymbolExpression>();
		this.summarize_value_domains_in(expression, concrete_annotations, scopes);
		for(SymbolExpression scope : scopes) {
			abstract_annotations.add(CirAnnotation.sub_expr(expression, scope));
		}
	}
	/**
	 * @param symbolic_annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_ext_expr(CirAnnotation symbolic_annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* 1. incorporate trp_stmt if exception occurs in testing */
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			if(concrete_annotation.get_operator() == CirAnnotationType.trp_stmt) {
				abstract_annotations.add(concrete_annotation);
				return;
			}
		}
		
		/* 2. otherwise, incorporate the set-state annotation */
		abstract_annotations.add(symbolic_annotation);
		
		/* 3. summarize the value scopes from concrete ones */
		CirExpression expression = (CirExpression) symbolic_annotation.get_location();
		Collection<SymbolExpression> scopes = new HashSet<SymbolExpression>();
		this.summarize_value_domains_in(expression, concrete_annotations, scopes);
		for(SymbolExpression scope : scopes) {
			abstract_annotations.add(CirAnnotation.ext_expr(expression, scope));
		}
	}
	/**
	 * @param symbolic_annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_xor_expr(CirAnnotation symbolic_annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* 1. incorporate trp_stmt if exception occurs in testing */
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			if(concrete_annotation.get_operator() == CirAnnotationType.trp_stmt) {
				abstract_annotations.add(concrete_annotation);
				return;
			}
		}
		
		/* 2. otherwise, incorporate the set-state annotation */
		abstract_annotations.add(symbolic_annotation);
		
		/* 3. summarize the value scopes from concrete ones */
		CirExpression expression = (CirExpression) symbolic_annotation.get_location();
		Collection<SymbolExpression> scopes = new HashSet<SymbolExpression>();
		this.summarize_value_domains_in(expression, concrete_annotations, scopes);
		for(SymbolExpression scope : scopes) {
			abstract_annotations.add(CirAnnotation.xor_expr(expression, scope));
		}
	}
	/**
	 * It summarizes the abstract annotations from input symbolic one with its concrete instances
	 * @param symbolic_annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	protected static void summarize_annotations(CirAnnotation symbolic_annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		util.summarize_annotations_in(symbolic_annotation, concrete_annotations, abstract_annotations);
	}
	
}
