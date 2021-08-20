package com.jcsa.jcmutest.mutant.cir2mutant.cond;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
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
import com.jcsa.jcparse.lang.irlang.CirNode;
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
 * It is used to generate, concretize and summarize the annotations from 
 * CirAttribute or CirAnnotation themselves.
 * 
 * @author yukimula
 *
 */
public class CirAnnotationUtil {
	
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
	
	/* generation interfaces */
	/**
	 * generate the symbolic annotations representing the source attribute statically
	 * @param attribute		the attribute from which the symbolic annotations be generated
	 * @param annotations	the collection to contain the symbolic annotations be generated 
	 * @throws Exception
	 */
	public static void generate_annotations(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		util.generate_annotations_in(attribute, annotations);
	}
	/**
	 * @param attribute		the attribute from which the symbolic annotations be generated
	 * @param annotations	the collection to contain the symbolic annotations be generated 
	 * @throws Exception
	 */
	private void generate_annotations_in(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute as null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid annotations as null");
		}
		else if(attribute instanceof CirConstraint) {
			this.generate_annotations_in_constraints((CirConstraint) attribute, annotations);
		}
		else if(attribute instanceof CirCoverCount) {
			this.generate_annotations_in_cover_count((CirCoverCount) attribute, annotations);
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
	 * divide the constraint into conditions and generate their annotations in the set
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
	 * generate the coverage constraint to annotations using its execution times limit sequence
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
	 * generate the stmt_error annotations from block-error directly
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
	 * generate the stmt_error annotations in the flows-error directly
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_flows_error(CirFlowsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecutionFlow orig_flow = attribute.get_original_flow();
		CirExecutionFlow muta_flow = attribute.get_mutation_flow();
		if(orig_flow.get_target() != muta_flow.get_target()) {
			annotations.add(CirAnnotation.mut_flow(orig_flow, muta_flow));
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
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_traps_error(CirTrapsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution().get_graph().get_exit();
		annotations.add(CirAnnotation.trp_stmt(execution));
	}
	/**
	 * @param expression
	 * @param muta_expression
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_via_expr_error(CirExpression expression, 
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
		
		/* 2. compare the original and mutation part and generate good annotations here */
		if(orig_expression.equals(muta_expression)) {
			check_point = this.find_prior_checkpoint(execution, SymbolFactory.sym_expression(Boolean.FALSE));
			annotations.add(CirAnnotation.eva_expr(check_point, Boolean.FALSE, true)); 
			return;
		}
		else {
			annotations.add(CirAnnotation.set_expr(expression, muta_expression));
		}
		
		/* 3. difference by subtract, bitws_xor and extend subtract */
		if(CirMutation.is_numeric(expression) || CirMutation.is_pointer(expression)) {
			SymbolExpression sub_difference = CirAnnotation.sub(orig_expression, muta_expression);
			sub_difference = this.symbol_evaluate(sub_difference, null);
			annotations.add(CirAnnotation.sub_expr(expression, sub_difference));
		}
		if(CirMutation.is_numeric(expression)) {
			SymbolExpression ext_difference = CirAnnotation.ext(orig_expression, muta_expression);
			ext_difference = this.symbol_evaluate(ext_difference, null);
			annotations.add(CirAnnotation.ext_expr(expression, ext_difference));
		}
		if(CirMutation.is_integer(expression)) {
			SymbolExpression xor_difference = CirAnnotation.xor(orig_expression, muta_expression);
			xor_difference = this.symbol_evaluate(xor_difference, null);
			annotations.add(CirAnnotation.xor_expr(expression, xor_difference));
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_difer_error(CirDiferError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression muta_expression = attribute.get_parameter();
		this.generate_annotations_via_expr_error(expression, muta_expression, annotations);
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_value_error(CirValueError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression muta_expression = attribute.get_parameter();
		this.generate_annotations_via_expr_error(expression, muta_expression, annotations);
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_refer_error(CirReferError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression muta_expression = attribute.get_parameter();
		this.generate_annotations_via_expr_error(expression, muta_expression, annotations);
	}
	/**
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
	
	/* concretization methods */
	/**
	 * @param symbolic_annotations	the set of symbolic annotations from which the concrete ones be created
	 * @param concrete_annotations	the set of concrete annotations being generated from the symbolic ones.
	 * @throws Exception
	 */
	public static void concretize_annotations(Collection<CirAnnotation> symbolic_annotations,
			SymbolProcess context, Collection<CirAnnotation> concrete_annotations) throws Exception {
		for(CirAnnotation symbolic_annotation : symbolic_annotations) {
			util.concretize_annotations_in(symbolic_annotation, context, concrete_annotations);
		}
	}
	/**
	 * @param annotation	the symbolic annotation representing the original CirAttribute
	 * @param annotations	the collection to contain concrete annotations from the source
	 * @throws Exception
	 */
	private void concretize_annotations_in(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else {
			switch(annotation.get_operator()) {
			case cov_stmt:	this.concretize_annotations_in_cov_stmt(annotation, context, annotations);	break;
			case eva_expr:	this.concretize_annotations_in_eva_expr(annotation, context, annotations); 	break;
			case mut_stmt:	this.concretize_annotations_in_mut_stmt(annotation, context, annotations);	break;
			case mut_flow:	this.concretize_annotations_in_mut_flow(annotation, context, annotations); 	break;
			case trp_stmt:	this.concretize_annotations_in_trp_stmt(annotation, context, annotations); 	break;
			case mut_stat:	this.concretize_annotations_in_mut_stat(annotation, context, annotations);	break;
			case set_expr:	this.concretize_annotations_in_set_expr(annotation, context, annotations); 	break;
			case sub_expr:	this.concretize_annotations_in_sub_expr(annotation, context, annotations); 	break;
			case ext_expr:	this.concretize_annotations_in_ext_expr(annotation, context, annotations);  break;
			case xor_expr:	this.concretize_annotations_in_xor_expr(annotation, context, annotations); 	break;
			default:		throw new IllegalArgumentException("Unsupport: " + annotation);
			}
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_cov_stmt(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { }
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_eva_expr(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { }
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_mut_flow(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { }
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_mut_stmt(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { }
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_trp_stmt(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { }
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_mut_stat(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = annotation.get_execution();
		CirExpression expression = (CirExpression) annotation.get_location();
		SymbolExpression muta_expression = annotation.get_parameter();
		try {
			muta_expression = this.symbol_evaluate(muta_expression, context);
			if(muta_expression instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) muta_expression;
				if(CirMutation.is_boolean(expression)) {
					annotations.add(CirAnnotation.set_conc(expression, constant.get_bool()));
				}
				else if(CirMutation.is_integer(expression)) {
					annotations.add(CirAnnotation.set_conc(expression, constant.get_long()));
				}
				else if(CirMutation.is_numeric(expression)) {
					annotations.add(CirAnnotation.set_conc(expression, constant.get_double()));
				}
				else if(CirMutation.is_pointer(expression)) {
					annotations.add(CirAnnotation.set_conc(expression, constant.get_long()));
				}
				else {
					annotations.add(CirAnnotation.set_conc(expression, constant.get_constant()));
				}
			}
			else {
				annotations.add(CirAnnotation.chg_expr(expression));
			}
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution.get_graph().get_exit()));
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_set_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = annotation.get_execution();
		CirExpression expression = (CirExpression) annotation.get_location();
		SymbolExpression orig_expression = SymbolFactory.sym_expression(expression);
		SymbolExpression muta_expression = annotation.get_parameter();
		try {
			orig_expression = this.symbol_evaluate(orig_expression, context);
			muta_expression = this.symbol_evaluate(muta_expression, context);
			if(orig_expression.equals(muta_expression)) { /* no error occurs */ }
			else if(muta_expression instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) muta_expression;
				if(CirMutation.is_boolean(expression)) {
					annotations.add(CirAnnotation.set_conc(expression, constant.get_bool()));
				}
				else if(CirMutation.is_integer(expression)) {
					annotations.add(CirAnnotation.set_conc(expression, constant.get_long()));
				}
				else if(CirMutation.is_numeric(expression)) {
					annotations.add(CirAnnotation.set_conc(expression, constant.get_double()));
				}
				else if(CirMutation.is_pointer(expression)) {
					annotations.add(CirAnnotation.set_conc(expression, constant.get_long()));
				}
				else {
					annotations.add(CirAnnotation.set_conc(expression, constant.get_constant()));
				}
			}
			else {
				annotations.add(CirAnnotation.chg_expr(expression));
			}
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution.get_graph().get_exit()));
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_sub_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_location();
		SymbolExpression difference = annotation.get_parameter();
		try {
			difference = this.symbol_evaluate(difference, context);
			if(difference instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) difference;
				if(CirMutation.is_integer(expression)) {
					annotations.add(CirAnnotation.sub_conc(expression, constant.get_long()));
				}
				else if(CirMutation.is_numeric(expression)) {
					annotations.add(CirAnnotation.sub_conc(expression, constant.get_double()));
				}
				else if(CirMutation.is_pointer(expression)) {
					annotations.add(CirAnnotation.sub_conc(expression, constant.get_long()));
				}
				else {
					throw new IllegalArgumentException("Unsupport: " + expression.get_data_type());
				}
			}
		}
		catch(ArithmeticException ex) { }
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_ext_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_location();
		SymbolExpression difference = annotation.get_parameter();
		try {
			difference = this.symbol_evaluate(difference, context);
			if(difference instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) difference;
				if(CirMutation.is_integer(expression)) {
					annotations.add(CirAnnotation.ext_conc(expression, constant.get_long()));
				}
				else if(CirMutation.is_numeric(expression)) {
					annotations.add(CirAnnotation.ext_conc(expression, constant.get_double()));
				}
				else {
					throw new IllegalArgumentException("Unsupport: " + expression.get_data_type());
				}
			}
		}
		catch(ArithmeticException ex) { }
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_xor_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_location();
		SymbolExpression difference = annotation.get_parameter();
		try {
			difference = this.symbol_evaluate(difference, context);
			if(difference instanceof SymbolConstant) {
				SymbolConstant constant = (SymbolConstant) difference;
				if(CirMutation.is_integer(expression)) {
					annotations.add(CirAnnotation.xor_conc(expression, constant.get_long()));
				}
				else {
					throw new IllegalArgumentException("Unsupport: " + expression.get_data_type());
				}
			}
		}
		catch(ArithmeticException ex) { }
	}
	
	/* domain-abstract methods */
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	public static void summarized_annotations(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		util.summarized_annotations_in(concrete_annotations, abstract_annotations);
	}
	/**
	 * summarize the domain-based annotations from the given concrete annotations
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in(Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(concrete_annotations == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(abstract_annotations == null) {
			throw new IllegalArgumentException("Invalid abstract_annotations");
		}
		else {
			Map<CirAnnotationType, Collection<CirAnnotation>> maps = 
					new HashMap<CirAnnotationType, Collection<CirAnnotation>>();
			for(CirAnnotation concrete_annotation : concrete_annotations) {
				CirAnnotationType type = concrete_annotation.get_operator();
				if(!maps.containsKey(type)) {
					maps.put(type, new HashSet<CirAnnotation>());
				}
				maps.get(type).add(concrete_annotation);
			}
			
			for(CirAnnotationType type : maps.keySet()) {
				concrete_annotations = maps.get(type); CirNode location = null;
				for(CirAnnotation concrete_annotation : concrete_annotations) {
					location = concrete_annotation.get_location();
					break;
				}
				this.summarized_annotations_in(type, location, maps.get(type), abstract_annotations);
			}
		}
	}
	/**
	 * summarize the domain-based annotations from the concrete ones under the type
	 * @param type
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in(CirAnnotationType type, CirNode location,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(concrete_annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else if(abstract_annotations == null) {
			throw new IllegalArgumentException("Invalid abstract_annotations");
		}
		else if(!concrete_annotations.isEmpty()) {
			switch(type) {
			case trp_stmt:	
			{
				this.summarized_annotations_in_trp_stmt(concrete_annotations, abstract_annotations); 
				break;
			}
			case set_conc:
			{
				CirExpression expression = (CirExpression) location;
				if(CirMutation.is_boolean(expression)) {
					this.summarized_annotations_in_set_bool(concrete_annotations, abstract_annotations);
				}
				else if(CirMutation.is_integer(expression)) {
					this.summarized_annotations_in_set_numb(concrete_annotations, abstract_annotations);
				}
				else if(CirMutation.is_numeric(expression)) {
					this.summarized_annotations_in_set_real(concrete_annotations, abstract_annotations);
				}
				else if(CirMutation.is_pointer(expression)) {
					this.summarized_annotations_in_set_addr(concrete_annotations, abstract_annotations);
				}
				else {
					this.summarized_annotations_in_set_auto(concrete_annotations, abstract_annotations);
				}
				break;
			}
			case sub_conc:
			{
				CirExpression expression = (CirExpression) location;
				if(CirMutation.is_integer(expression)) {
					this.summarized_annotations_in_dif_numb(concrete_annotations, abstract_annotations);
				}
				else if(CirMutation.is_numeric(expression)) {
					this.summarized_annotations_in_dif_real(concrete_annotations, abstract_annotations);
				}
				else {
					this.summarized_annotations_in_dif_addr(concrete_annotations, abstract_annotations);
				}
				break;
			}
			case ext_conc:
			{
				CirExpression expression = (CirExpression) location;
				if(CirMutation.is_integer(expression)) {
					this.summarized_annotations_in_ext_numb(concrete_annotations, abstract_annotations);
				}
				else {
					this.summarized_annotations_in_ext_real(concrete_annotations, abstract_annotations);
				}
				break;
			}
			case xor_conc:
			{
				this.summarized_annotations_in_xor_numb(concrete_annotations, abstract_annotations);
				break;
			}
			default:		throw new IllegalArgumentException("Unsupport: " + type);
			}
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in_trp_stmt(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(concrete_annotations);
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in_set_bool(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage collection */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirExpression expression = null; SymbolConstant muta_constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			if(concrete_annotation.has_parameter()) {
				muta_constant = (SymbolConstant) concrete_annotation.get_parameter();
				if(muta_constant.get_bool()) {
					domains.add(CirAnnotationScope.get_true_scope());
				}
				else {
					domains.add(CirAnnotationScope.get_fals_scope());
				}
			}
		}
		
		/* domain summarization analysis */
		if(domains.contains(CirAnnotationScope.get_true_scope())) {
			if(domains.contains(CirAnnotationScope.get_fals_scope())) {			/* (T,F) */
				abstract_annotations.add(CirAnnotation.set_bool_scope(expression));
			}
			else {																/* (T) */
				abstract_annotations.add(CirAnnotation.set_true_scope(expression));
			}
		}
		else {
			if(domains.contains(CirAnnotationScope.get_fals_scope())) {			/* (F) */
				abstract_annotations.add(CirAnnotation.set_fals_scope(expression));
			}
			else {	/* none of value is contained in the coverage domain */	}
		}
		if(expression != null) abstract_annotations.add(CirAnnotation.set_bool_scope(expression));
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in_set_numb(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage collection */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirExpression expression = null; SymbolConstant muta_constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			if(concrete_annotation.has_parameter()) {
				muta_constant = (SymbolConstant) concrete_annotation.get_parameter();
				if(muta_constant.get_long() > 0) {
					domains.add(CirAnnotationScope.get_post_scope());
				}
				else if(muta_constant.get_long() < 0) {
					domains.add(CirAnnotationScope.get_negt_scope());
				}
				else {
					domains.add(CirAnnotationScope.get_zero_scope());
				}
			}
		}
		
		/* domain summarization analysis */
		if(domains.contains(CirAnnotationScope.get_post_scope())) {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) {
				if(domains.contains(CirAnnotationScope.get_zero_scope())) {		/* (+, 0, -) */
					abstract_annotations.add(CirAnnotation.set_numb_scope(expression));
				}
				else {															/* (+, -) */
					abstract_annotations.add(CirAnnotation.set_nzro_scope(expression));
				}
			}
			else {
				if(domains.contains(CirAnnotationScope.get_zero_scope())) {		/* (+, 0) */
					abstract_annotations.add(CirAnnotation.set_nneg_scope(expression));
				}
				else {															/* (+) */
					abstract_annotations.add(CirAnnotation.set_post_scope(expression));
					abstract_annotations.add(CirAnnotation.set_nneg_scope(expression));
					abstract_annotations.add(CirAnnotation.set_nzro_scope(expression));
				}
			}
		}
		else {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) {
				if(domains.contains(CirAnnotationScope.get_zero_scope())) {		/* (-, 0) */
					abstract_annotations.add(CirAnnotation.set_npos_scope(expression));
				}
				else {															/* (-) */
					abstract_annotations.add(CirAnnotation.set_negt_scope(expression));
					abstract_annotations.add(CirAnnotation.set_npos_scope(expression));
					abstract_annotations.add(CirAnnotation.set_nzro_scope(expression));
				}
			}
			else {
				if(domains.contains(CirAnnotationScope.get_zero_scope())) {		/* (0) */
					abstract_annotations.add(CirAnnotation.set_zero_scope(expression));
					abstract_annotations.add(CirAnnotation.set_npos_scope(expression));
					abstract_annotations.add(CirAnnotation.set_nneg_scope(expression));
				}
				else {	/* none of value is contained in the coverage domain */	}
			}
		}
		if(expression != null) abstract_annotations.add(CirAnnotation.set_numb_scope(expression));
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in_set_real(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage collection */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirExpression expression = null; SymbolConstant muta_constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			if(concrete_annotation.has_parameter()) {
				muta_constant = (SymbolConstant) concrete_annotation.get_parameter();
				if(muta_constant.get_double() > 0) {
					domains.add(CirAnnotationScope.get_post_scope());
				}
				else if(muta_constant.get_double() < 0) {
					domains.add(CirAnnotationScope.get_negt_scope());
				}
				else {
					domains.add(CirAnnotationScope.get_zero_scope());
				}
			}
		}
		
		/* domain summarization analysis */
		if(domains.contains(CirAnnotationScope.get_post_scope())) {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) {
				if(domains.contains(CirAnnotationScope.get_zero_scope())) {		/* (+, 0, -) */
					abstract_annotations.add(CirAnnotation.set_numb_scope(expression));
				}
				else {															/* (+, -) */
					abstract_annotations.add(CirAnnotation.set_nzro_scope(expression));
				}
			}
			else {
				if(domains.contains(CirAnnotationScope.get_zero_scope())) {		/* (+, 0) */
					abstract_annotations.add(CirAnnotation.set_nneg_scope(expression));
				}
				else {															/* (+) */
					abstract_annotations.add(CirAnnotation.set_post_scope(expression));
					abstract_annotations.add(CirAnnotation.set_nneg_scope(expression));
					abstract_annotations.add(CirAnnotation.set_nzro_scope(expression));
				}
			}
		}
		else {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) {
				if(domains.contains(CirAnnotationScope.get_zero_scope())) {		/* (-, 0) */
					abstract_annotations.add(CirAnnotation.set_npos_scope(expression));
				}
				else {															/* (-) */
					abstract_annotations.add(CirAnnotation.set_negt_scope(expression));
					abstract_annotations.add(CirAnnotation.set_npos_scope(expression));
					abstract_annotations.add(CirAnnotation.set_nzro_scope(expression));
				}
			}
			else {
				if(domains.contains(CirAnnotationScope.get_zero_scope())) {		/* (0) */
					abstract_annotations.add(CirAnnotation.set_zero_scope(expression));
					abstract_annotations.add(CirAnnotation.set_npos_scope(expression));
					abstract_annotations.add(CirAnnotation.set_nneg_scope(expression));
				}
				else {	/* none of value is contained in the coverage domain */	}
			}
		}
		if(expression != null) abstract_annotations.add(CirAnnotation.set_numb_scope(expression));
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in_set_addr(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage collection */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirExpression expression = null; SymbolConstant muta_constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			if(concrete_annotation.has_parameter()) {
				muta_constant = (SymbolConstant) concrete_annotation.get_parameter();
				if(muta_constant.get_long() == 0) {
					domains.add(CirAnnotationScope.get_null_scope());
				}
				else {
					domains.add(CirAnnotationScope.get_invp_scope());
				}
			}
		}
		
		/* domain summarization analysis */
		if(domains.contains(CirAnnotationScope.get_null_scope())) {
			if(domains.contains(CirAnnotationScope.get_invp_scope())) {
				abstract_annotations.add(CirAnnotation.set_addr_scope(expression));
			}
			else {
				abstract_annotations.add(CirAnnotation.set_null_scope(expression));
			}
		}
		else {
			if(domains.contains(CirAnnotationScope.get_invp_scope())) {
				abstract_annotations.add(CirAnnotation.set_invp_scope(expression));
			}
			else {	/* none of error is contained in the domains */	}
		}
		if(expression != null) abstract_annotations.add(CirAnnotation.set_addr_scope(expression));
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in_set_auto(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			CirExpression expression = (CirExpression) concrete_annotation.get_location();
			abstract_annotations.add(CirAnnotation.chg_expr(expression));
			break;
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in_dif_numb(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage analysis */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirExpression expression = null; SymbolConstant muta_constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			if(concrete_annotation.has_parameter()) {
				muta_constant = (SymbolConstant) concrete_annotation.get_parameter();
				if(muta_constant.get_long() > 0) {
					domains.add(CirAnnotationScope.get_post_scope());
				}
				else if(muta_constant.get_long() < 0) {
					domains.add(CirAnnotationScope.get_negt_scope());
				}
			}
		}
		
		/* domain summarization analysis */
		if(domains.contains(CirAnnotationScope.get_post_scope())) {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) { }
			else {
				abstract_annotations.add(CirAnnotation.inc_scope(expression));
			}
		}
		else {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) {
				abstract_annotations.add(CirAnnotation.dec_scope(expression));
			}
			else { }
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in_dif_real(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage analysis */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirExpression expression = null; SymbolConstant muta_constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			if(concrete_annotation.has_parameter()) {
				muta_constant = (SymbolConstant) concrete_annotation.get_parameter();
				if(muta_constant.get_double() > 0) {
					domains.add(CirAnnotationScope.get_post_scope());
				}
				else if(muta_constant.get_double() < 0) {
					domains.add(CirAnnotationScope.get_negt_scope());
				}
			}
		}
		
		/* domain summarization analysis */
		if(domains.contains(CirAnnotationScope.get_post_scope())) {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) { }
			else {
				abstract_annotations.add(CirAnnotation.inc_scope(expression));
			}
		}
		else {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) {
				abstract_annotations.add(CirAnnotation.dec_scope(expression));
			}
			else { }
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in_dif_addr(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage analysis */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirExpression expression = null; SymbolConstant muta_constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			if(concrete_annotation.has_parameter()) {
				muta_constant = (SymbolConstant) concrete_annotation.get_parameter();
				if(muta_constant.get_long() > 0) {
					domains.add(CirAnnotationScope.get_post_scope());
				}
				else if(muta_constant.get_long() < 0) {
					domains.add(CirAnnotationScope.get_negt_scope());
				}
			}
		}
		
		/* domain summarization analysis */
		if(domains.contains(CirAnnotationScope.get_post_scope())) {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) { }
			else {
				abstract_annotations.add(CirAnnotation.inc_scope(expression));
			}
		}
		else {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) {
				abstract_annotations.add(CirAnnotation.dec_scope(expression));
			}
			else { }
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in_ext_numb(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage analysis */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirExpression expression = null; SymbolConstant muta_constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			if(concrete_annotation.has_parameter()) {
				muta_constant = (SymbolConstant) concrete_annotation.get_parameter();
				if(muta_constant.get_long() > 0) {
					domains.add(CirAnnotationScope.get_post_scope());
				}
				else if(muta_constant.get_long() < 0) {
					domains.add(CirAnnotationScope.get_negt_scope());
				}
			}
		}
		
		/* domain summarization analysis */
		if(domains.contains(CirAnnotationScope.get_post_scope())) {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) { }
			else {
				abstract_annotations.add(CirAnnotation.ext_scope(expression));
			}
		}
		else {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) {
				abstract_annotations.add(CirAnnotation.shk_scope(expression));
			}
			else { }
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in_ext_real(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage analysis */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirExpression expression = null; SymbolConstant muta_constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			if(concrete_annotation.has_parameter()) {
				muta_constant = (SymbolConstant) concrete_annotation.get_parameter();
				if(muta_constant.get_double() > 0) {
					domains.add(CirAnnotationScope.get_post_scope());
				}
				else if(muta_constant.get_double() < 0) {
					domains.add(CirAnnotationScope.get_negt_scope());
				}
			}
		}
		
		/* domain summarization analysis */
		if(domains.contains(CirAnnotationScope.get_post_scope())) {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) { }
			else {
				abstract_annotations.add(CirAnnotation.ext_scope(expression));
			}
		}
		else {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) {
				abstract_annotations.add(CirAnnotation.shk_scope(expression));
			}
			else { }
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarized_annotations_in_xor_numb(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* domain coverage analysis */
		Collection<SymbolExpression> domains = new HashSet<SymbolExpression>();
		CirExpression expression = null; SymbolConstant muta_constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			if(concrete_annotation.has_parameter()) {
				muta_constant = (SymbolConstant) concrete_annotation.get_parameter();
				if(muta_constant.get_long() > 0) {
					domains.add(CirAnnotationScope.get_post_scope());
				}
				else if(muta_constant.get_long() < 0) {
					domains.add(CirAnnotationScope.get_negt_scope());
				}
			}
		}
		
		/* domain summarization analysis */
		if(domains.contains(CirAnnotationScope.get_post_scope())) {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) { }
			else {
				abstract_annotations.add(CirAnnotation.xor_post(expression));
			}
		}
		else {
			if(domains.contains(CirAnnotationScope.get_negt_scope())) {
				abstract_annotations.add(CirAnnotation.xor_negt(expression));
			}
			else { }
		}
	}
	
}
