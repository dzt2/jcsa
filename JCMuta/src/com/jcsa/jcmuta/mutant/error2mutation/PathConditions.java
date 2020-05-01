package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.SymEvaluator;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;
import com.jcsa.jcparse.lang.symb.impl.StandardSymEvaluator;
import com.jcsa.jcparse.lopt.CirInstance;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceEdge;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceGraph;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceNode;

/**
 * Used to extract the path conditions to a specified statement.
 * 
 * @author yukimula
 *
 */
public class PathConditions {
	
	private static final Random random = new Random();
	private static final SymEvaluator evaluator = StandardSymEvaluator.new_evaluator();
	
	/**
	 * find the dominance node to which the statement refers to
	 * @param statement
	 * @param dgraph
	 * @return
	 * @throws Exception
	 */
	private static CDominanceNode find_dominance_node(
			CirStatement statement, CDominanceGraph dgraph) throws Exception {
		/* get the execution of the statement in program static graph */
		CirExecution execution = statement.get_tree().get_function_call_graph().
				get_function(statement).get_flow_graph().get_execution(statement);
		
		/* get the dominance nodes w.r.t. the statement of execution */
		List<CDominanceNode> matched_set = new ArrayList<CDominanceNode>();
		for(CDominanceNode node : dgraph.get_nodes()) {
			CirInstance instance = node.get_instance();
			if(instance instanceof CirInstanceNode) {
				if(((CirInstanceNode) instance).get_execution() == execution) {
					matched_set.add(node);
				}
			}
		}
		
		/* get a random node from the dominance graph w.r.t. the statement */
		if(!matched_set.isEmpty()) {
			int index = random.nextInt(matched_set.size());
			for(CDominanceNode node : matched_set) {
				if(index-- <= 0) return node;
			}
			throw new RuntimeException("Out of bounds!");
		}
		
		/* none of nodes matching with the statement */	return null;
	}
	
	/**
	 * get the flow path from entry to the 
	 * @param statement
	 * @param dgraph
	 * @return
	 * @throws Exception
	 */
	private static Iterable<CirExecutionFlow> find_flow_path(CDominanceNode source) throws Exception {
		List<CirExecutionFlow> flows = new ArrayList<CirExecutionFlow>();
		while(source != null) {
			CirInstance instance = source.get_instance();
			
			if(instance instanceof CirInstanceEdge) {
				CirExecutionFlow flow = ((CirInstanceEdge) instance).get_flow();
				if(flow != null) {
					switch(flow.get_type()) {
					case true_flow:
					case fals_flow: flows.add(flow);
					default: break;
					}
				}
			}
			
			if(source.get_in_degree() > 0)
				source = source.get_in_node(0);
			else source = null;
		}
		return flows;
	}
	
	/**
	 * translate the source to boolean condition
	 * @param source
	 * @param value
	 * @return
	 * @throws Exception
	 */
	private static SymExpression get_condition_of(SymExpression source, boolean value) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(source.get_data_type());
		
		SymExpression condition;
		if(CTypeAnalyzer.is_boolean(data_type)) {
			condition = source;
		}
		else if(CTypeAnalyzer.is_number(data_type)) {
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
						COperator.not_equals, source, SymFactory.new_constant(0L));
		}
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, source, SymFactory.new_address(StateError.NullPointer, data_type));
		}
		else {
			throw new IllegalArgumentException("Invalid data type");
		}
		
		if(!value) {
			condition = SymFactory.new_unary_expression(
				CBasicTypeImpl.bool_type, COperator.logic_not, condition);
		}
		
		return evaluator.evaluate(condition);
	}
	
	/**
	 * generate the path conditions w.r.t. the path being selected
	 * @param flows
	 * @return
	 * @throws Exception
	 */
	private static Iterable<StateConstraint> generate_path_condition(
			Iterable<CirExecutionFlow> flows) throws Exception {
		List<StateConstraint> conditions = new ArrayList<StateConstraint>();
		
		for(CirExecutionFlow flow : flows) {
			CirStatement statement = flow.get_source().get_statement();
			
			CirExpression expression; 
			if(statement instanceof CirIfStatement) {
				expression = ((CirIfStatement) statement).get_condition();
			}
			else {
				expression = ((CirCaseStatement) statement).get_condition();
			}
			boolean value = (flow.get_type() == CirExecutionFlowType.true_flow);
			
			SymExpression condition = SymFactory.parse(expression);
			condition = get_condition_of(condition, value);
			
			conditions.add(new StateConstraint(statement, condition));
		}
		
		return conditions;
	}
	
	/**
	 * get the constraints of the path to the statement
	 * @param statement
	 * @param dgraph dominance graph used to generate path conditions
	 * @return
	 * @throws Exception
	 */
	public static Iterable<StateConstraint> path_constraints(
			CirStatement statement, CDominanceGraph dgraph) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(dgraph == null)
			throw new IllegalArgumentException("Invalid dgraph: null");
		else {
			CDominanceNode source = find_dominance_node(statement, dgraph);
			if(source != null) {
				Iterable<CirExecutionFlow> flows_path = find_flow_path(source);
				return generate_path_condition(flows_path);
			}
			else {
				/** unreachable path condition **/
				List<StateConstraint> constraints = new ArrayList<StateConstraint>();
				constraints.add(new StateConstraint(statement, SymFactory.new_constant(false)));
				return constraints;
			}
		}
	}
	
}
