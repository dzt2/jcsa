package com.jcsa.jcmutest.backups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependPredicate;
import com.jcsa.jcparse.flwa.depend.CDependReference;
import com.jcsa.jcparse.flwa.depend.CDependType;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;


/**
 * It is used to construct the error propagation between statements, including:
 * (1) condition --> statement;
 * (2) call_stmt.argument --> parameter;
 * (3) retr_stmt.lvalue --> wait_expr;
 * (4) assign.lvalue --> stmt.usages;
 * 
 * @author yukimula
 *
 */
public class SecStatementPropagators {
	
	private SecErrorPropagator condition_t_propagator = new SecConditionTPropagator();
	private SecErrorPropagator condition_f_propagator = new SecConditionTPropagator();
	private SecErrorPropagator define_usage_propagator = new SecDefineUsePropagator();
	private SecStatementPropagators() {}
	private static final SecStatementPropagators propagators = new SecStatementPropagators();
	
	/**
	 * @param dependence_graph
	 * @param statement
	 * @return the dependence nodes w.r.t. the instances of the statement
	 * @throws Exception
	 */
	private Iterable<CDependNode> dependence_nodes(
			CDependGraph dependence_graph,
			CirStatement statement) throws Exception {
		List<CDependNode> depend_nodes = new ArrayList<CDependNode>();
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		if(dependence_graph.get_program_graph().has_instances_of(execution)) {
			Iterable<CirInstanceNode> instances = dependence_graph.
					get_program_graph().get_instances_of(execution);
			for(CirInstanceNode instance : instances) {
				if(dependence_graph.has_node(instance)) {
					depend_nodes.add(dependence_graph.get_node(instance));
				}
			}
		}
		return depend_nodes;
	}
	
	/**
	 * @param dependence_graph
	 * @param condition
	 * @return true --> statements in true branch && false --> statements in false branch
	 * @throws Exception
	 */
	private Map<Boolean, Set<CirStatement>> get_branches(
			CDependGraph dependence_graph,
			CirExpression condition) throws Exception {
		Map<Boolean, Set<CirStatement>> branches = new HashMap<Boolean, Set<CirStatement>>();
		branches.put(Boolean.TRUE, new HashSet<CirStatement>());
		branches.put(Boolean.FALSE, new HashSet<CirStatement>());
		Iterable<CDependNode> depend_nodes = this.
				dependence_nodes(dependence_graph, condition.statement_of());
		for(CDependNode depend_node : depend_nodes) {
			for(CDependEdge edge : depend_node.get_in_edges()) {
				if(edge.get_type() == CDependType.predicate_depend) {
					CDependPredicate predicate = (CDependPredicate) edge.get_element();
					CirStatement statement = edge.get_source().get_statement();
					if(predicate.get_predicate_value()) {
						branches.get(Boolean.TRUE).add(statement);
					}
					else {
						branches.get(Boolean.FALSE).add(statement);
					}
				}
			}
		}
		return branches;
	}
	
	/**
	 * @param error
	 * @param dependence_graph
	 * @return mapping from target errors to the constraints for infecting them
	 * @throws Exception
	 */
	private Map<SecStateError, SecConstraint> propagate_on_condition(
			SecExpressionError error, CDependGraph dependence_graph) throws Exception {
		CirExpression condition = error.get_orig_expression().get_expression().get_cir_source();
		Map<Boolean, Set<CirStatement>> branches = this.get_branches(dependence_graph, condition);
		Map<SecStateError, SecConstraint> results = new HashMap<SecStateError, SecConstraint>();
		
		for(CirStatement true_statement : branches.get(Boolean.TRUE)) {
			Map<SecStateError, SecConstraint> buffer = this.
					condition_t_propagator.propagate(true_statement, true_statement, error);
			for(SecStateError target_error : buffer.keySet()) {
				SecConstraint constraint = buffer.get(target_error);
				results.put(target_error, constraint);
			}
		}
		
		for(CirStatement false_statement : branches.get(Boolean.FALSE)) {
			Map<SecStateError, SecConstraint> buffer = this.
					condition_f_propagator.propagate(false_statement, false_statement, error);
			for(SecStateError target_error : buffer.keySet()) {
				SecConstraint constraint = buffer.get(target_error);
				results.put(target_error, constraint);
			}
		}
		
		return results;
	}
	
	/**
	 * @param dependence_graph
	 * @param reference
	 * @return the set of usage points from the definition reference
	 * @throws Exception
	 */
	private Set<CirExpression> get_usage_points(CDependGraph dependence_graph, CirExpression reference) throws Exception {
		Set<CirExpression> usage_points = new HashSet<CirExpression>();
		Iterable<CDependNode> depend_nodes = this.dependence_nodes(dependence_graph, reference.statement_of());
		for(CDependNode depend_node : depend_nodes) {
			for(CDependEdge edge : depend_node.get_in_edges()) {
				switch(edge.get_type()) {
				case use_defin_depend:
				case param_arg_depend:
				case wait_retr_depend:
				{
					CDependReference element = (CDependReference) edge.get_element();
					if(element.get_def() == reference) {
						if(element.get_use().statement_of() != null)
							usage_points.add(element.get_use());
					}
				}
				default: break;
				}
			}
		}
		return usage_points;
	}
	
	/**
	 * @param error
	 * @param dependence_graph
	 * @return 
	 * @throws Exception
	 */
	private Map<SecStateError, SecConstraint> propagate_on_define_use(
			SecReferenceError error, CDependGraph dependence_graph) throws Exception {
		CirExpression reference = error.get_orig_reference().get_expression().get_cir_source();
		Set<CirExpression> usage_points = this.get_usage_points(dependence_graph, reference);
		Map<SecStateError, SecConstraint> results = new HashMap<SecStateError, SecConstraint>();
		for(CirExpression use_expression : usage_points) {
			Map<SecStateError, SecConstraint> buffer = this.define_usage_propagator.propagate(
					use_expression.statement_of(), use_expression, error);
			for(SecStateError target_error : buffer.keySet()) {
				SecConstraint constraint = buffer.get(target_error);
				results.put(target_error, constraint);
			}
		}
		return results;
	}
	
	/**
	 * @param source
	 * @param contexts
	 * @param dependence_graph
	 * @return 
	 * @throws Exception
	 */
	private Iterable<SecStateEdge> propagate_on(SecStateNode source, CDependGraph dependence_graph) throws Exception {
		if(source == null || !source.is_state_error())
			throw new IllegalArgumentException("Not a state error: " + source);
		else {
			SecStateError error = source.get_state_error();
			Map<SecStateError, SecConstraint> results; SecStateEdgeType edge_type;
			if(error instanceof SecExpressionError) {
				CirExpression condition = ((SecExpressionError) error).get_orig_expression().get_expression().get_cir_source();
				if(condition.get_parent() instanceof CirIfStatement || condition.get_parent() instanceof CirCaseStatement) {
					results = this.propagate_on_condition((SecExpressionError) error, dependence_graph);
					edge_type = SecStateEdgeType.control;
				}
				else {
					results = null; edge_type = null;
				}
			}
			else if(error instanceof SecReferenceError) {
				CirExpression reference = ((SecReferenceError) error).
						get_orig_reference().get_expression().get_cir_source();
				CirStatement statement = reference.statement_of();
				if(statement instanceof CirReturnAssignStatement) {
					edge_type = SecStateEdgeType.def_use;
				}
				else if(statement instanceof CirAssignStatement) {
					edge_type = SecStateEdgeType.def_use;
				}
				else if(statement instanceof CirCallStatement) {
					edge_type = SecStateEdgeType.def_use;
				}
				else {
					results = null; edge_type = null;
				}
				
				if(edge_type != null) {
					results = this.propagate_on_define_use((SecReferenceError) error, dependence_graph);
				}
				else {
					results = null;
				}
			}
			else {
				results = null; edge_type = null;
			}
			
			if(results != null) {
				for(SecStateError target_error : results.keySet()) {
					SecConstraint constraint = results.get(target_error);
					SecStateNode target = source.get_graph().new_node(target_error);
					source.link_to(edge_type, target, constraint);
				}
			}
			return source.get_ou_edges();
		}
		
	}
	
	/**
	 * @param source
	 * @param contexts
	 * @param dependence_graph
	 * @return
	 * @throws Exception
	 */
	protected List<SecStateNode> propagate_from(SecStateNode source, CDependGraph dependence_graph) throws Exception {
		Iterable<SecStateEdge> ou_edges = this.propagate_on(source, dependence_graph);
		List<SecStateNode> next_nodes = new ArrayList<SecStateNode>();
		for(SecStateEdge edge : ou_edges) {
			next_nodes.add(edge.get_target());
		}
		return next_nodes;
	}
	
	/**
	 * @param source
	 * @param dependence_graph
	 * @return 
	 * @throws Exception
	 */
	public static List<SecStateNode> propagate(SecStateNode source, CDependGraph dependence_graph) throws Exception {
		return propagators.propagate_from(source, dependence_graph);
	}
	
}
