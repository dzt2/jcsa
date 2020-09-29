package com.jcsa.jcmutest.mutant.cir2mutant.struct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcparse.flwa.dominate.CDominanceGraph;
import com.jcsa.jcparse.flwa.dominate.CDominanceNode;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It is used to generate the path constraints via dominance graph analysis.
 * 
 * @author yukimula
 *
 */
public class CirPathConstraints {
	
	/**
	 * @param dominance_graph
	 * @param instance
	 * @return the constraints for reaching the instance of target statement
	 * @throws Exception
	 */
	public static List<CirConstraint> path_constraints(
			CDominanceGraph dominance_graph, CirInstanceNode instance,
			CirMutations cir_mutations) throws Exception {
		List<CirConstraint> constraints = new ArrayList<CirConstraint>();
		if(dominance_graph.has_node(instance)) {
			CDominanceNode dominance_node = dominance_graph.get_node(instance);
			List<CirExecutionFlow> flows = dominance_node.get_dominance_path();
			for(CirExecutionFlow flow : flows) {
				switch(flow.get_type()) {
				case true_flow:
				{
					CirStatement if_statement = flow.get_source().get_statement();
					CirExpression condition;
					if(if_statement instanceof CirIfStatement) {
						condition = ((CirIfStatement) if_statement).get_condition();
					}
					else if(if_statement instanceof CirCaseStatement) {
						condition = ((CirCaseStatement) if_statement).get_condition();
					}
					else {
						throw new IllegalArgumentException(if_statement.generate_code(true));
					}
					constraints.add(cir_mutations.expression_constraint(if_statement, condition, true));
					break;
				}
				case fals_flow:
				{
					CirStatement if_statement = flow.get_source().get_statement();
					CirExpression condition;
					if(if_statement instanceof CirIfStatement) {
						condition = ((CirIfStatement) if_statement).get_condition();
					}
					else if(if_statement instanceof CirCaseStatement) {
						condition = ((CirCaseStatement) if_statement).get_condition();
					}
					else {
						throw new IllegalArgumentException(if_statement.generate_code(true));
					}
					constraints.add(cir_mutations.expression_constraint(if_statement, condition, false));
					break;
				}
				case call_flow:
				{
					CirStatement call_statement = flow.get_source().get_statement();
					constraints.add(cir_mutations.expression_constraint(call_statement, Boolean.TRUE, true));
					break;
				}
				case retr_flow:
				{
					CirStatement wait_statement = flow.get_target().get_statement();
					constraints.add(cir_mutations.expression_constraint(wait_statement, Boolean.TRUE, true));
					break;
				}
				default: break;
				}
			}
		}
		return constraints;
	}
	
	/**
	 * @param dominance_graph
	 * @param statement
	 * @param cir_mutations
	 * @return common constraints shared between different paths leading to the statement
	 * @throws Exception
	 */
	public static Set<CirConstraint> common_path_constraints(CDominanceGraph dominance_graph,
			CirStatement statement, CirMutations cir_mutations) throws Exception {
		Set<CirConstraint> common_constraints = new HashSet<CirConstraint>(); boolean first = true;
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		Set<CirConstraint> removed_constraints = new HashSet<CirConstraint>();
		
		if(dominance_graph.get_instance_graph().has_instances_of(execution)) {
			for(CirInstanceNode instance : dominance_graph.get_instance_graph().get_instances_of(execution)) {
				List<CirConstraint> constraints = path_constraints(dominance_graph, instance, cir_mutations);
				if(first) {
					first = false;
					common_constraints.addAll(constraints);
				}
				else {
					removed_constraints.clear();
					for(CirConstraint constraint : common_constraints) {
						if(!constraints.contains(constraint)) {
							removed_constraints.add(constraint);
						}
					}
					common_constraints.removeAll(removed_constraints);
				}
			}
		}
		return common_constraints;
	}
	
}
