package com.jcsa.jcmuta.mutant.sem2mutation.unry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class UIOIMutationParser extends SemanticMutationParser {

	/**
	 * get the location that the trapping really occurs.
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private AstExpression get_location(AstMutation ast_mutation) throws Exception {
		AstExpression expression = (AstExpression) ast_mutation.get_location();
		return CTypeAnalyzer.get_expression_of(expression);
	}
	
	/**
	 * get the expression representing the AST mutation
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirExpression get_cir_result(AstMutation ast_mutation) throws Exception {
		return this.get_result(this.get_location(ast_mutation));
	}
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		CirExpression expression = get_cir_result(ast_mutation);
		if(expression != null) return expression.statement_of();
		else return null;
	}
	
	/**
	 * whether the expression is defined in the statement
	 * @param expression
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	private boolean is_defined_in(CirExpression expression, CirStatement statement) throws Exception {
		if(statement instanceof CirAssignStatement) {
			CirExpression lvalue = ((CirAssignStatement) statement).get_lvalue();
			Queue<CirExpression> cir_queue = new LinkedList<CirExpression>(); 
			String key = lvalue.generate_code(); cir_queue.add(expression);
			
			while(!cir_queue.isEmpty()) {
				CirExpression expr = cir_queue.poll();
				if(expr.generate_code().equals(key)) { return true; }
				else {
					for(int k = 0; k < expr.number_of_children(); k++) {
						CirNode child = expr.get_child(k);
						if(child instanceof CirExpression) {
							cir_queue.add((CirExpression) child);
						}
					}
				}
			}
			return false;
		}
		else return false;
	}
	
	/**
	 * collect all the usage points in the statement
	 * @param expression
	 * @param statement
	 * @param usage_points
	 * @throws Exception
	 */
	private void collect_usage_points(CirExpression expression, CirNode location, Set<CirExpression> usage_points) throws Exception {
		if(location instanceof CirAssignStatement) {
			this.collect_usage_points(expression, ((CirAssignStatement) location).get_rvalue(), usage_points);
			this.collect_usage_points(expression, ((CirAssignStatement) location).get_lvalue(), usage_points);
			usage_points.remove(((CirAssignStatement) location).get_lvalue());
		}
		else if(location instanceof CirCallStatement) {
			this.collect_usage_points(expression, ((CirCallStatement) location).get_function(), usage_points);
			
			CirArgumentList arguments = ((CirCallStatement) location).get_arguments();
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				CirExpression argument = arguments.get_argument(k);
				this.collect_usage_points(expression, argument, usage_points);
			}
		}
		else if(location instanceof CirCaseStatement) {
			this.collect_usage_points(expression, ((CirCaseStatement) location).get_condition(), usage_points);
		}
		else if(location instanceof CirIfStatement) {
			this.collect_usage_points(expression, ((CirIfStatement) location).get_condition(), usage_points);
		}
		else if(location instanceof CirExpression) {
			Queue<CirNode> cir_queue = new LinkedList<CirNode>();
			cir_queue.add(location); String key = expression.generate_code();
			
			while(!cir_queue.isEmpty()) {
				CirNode cir_node = cir_queue.poll();
				
				if(key.equals(cir_node.generate_code())) {
					if(cir_node instanceof CirExpression)
						usage_points.add((CirExpression) cir_node);
				}
				
				for(int k = 0; k < cir_node.number_of_children(); k++) {
					cir_queue.add(cir_node.get_child(k));
				}
			}
		}
	}
	
	/**
	 * collect all the usage points since the statement until the point it is defined
	 * @param execution
	 * @param expression
	 * @param usage_points
	 * @param visited_set
	 * @throws Exception
	 */
	private void use_define_traversal(CirExecution execution, 
			CirExpression expression, Set<CirExpression> usage_points,
			Set<CirExecution> visited_set) throws Exception {
		if(!visited_set.contains(execution)) {
			visited_set.add(execution);
			CirStatement statement = execution.get_statement();
			this.collect_usage_points(expression, statement, usage_points);
			
			/** stop until the expression is defined **/
			if(this.is_defined_in(expression, statement)) return;
			
			/** deep-first traversal **/
			for(CirExecutionFlow flow : execution.get_ou_flows()) {
				CirExecution target;
				switch(flow.get_type()) {
				case retr_flow: target = null; break;
				case call_flow:
					target = execution.get_graph().get_function().get_graph().get_calling(flow).get_wait_execution();
					break;
				default: target = flow.get_target(); break;
				}
				
				if(target != null) {
					this.use_define_traversal(target, expression, usage_points, visited_set);
				}
			}
		}
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		CirStatement statement = expression.statement_of();
		CirExecution execution = statement.get_tree().get_function_call_graph().
				get_function(statement).get_flow_graph().get_execution(statement);
		
		/** collect all the usage points since the expression increased **/
		Set<CirExpression> usage_points = new HashSet<CirExpression>();
		// use_define_traversal(execution, expression, usage_points, new HashSet<CirExecution>());
		for(CirExecutionFlow flow : execution.get_ou_flows()) {
			this.use_define_traversal(flow.get_target(), 
					expression, usage_points, new HashSet<CirExecution>());
		}
		
		/** determine the difference and the real usage points **/
		int difference;
		switch(ast_mutation.get_mutation_operator()) {
		case insert_prev_inc:	difference = 1; usage_points.add(expression); break;
		case insert_prev_dec:	difference =-1; usage_points.add(expression); break;
		case insert_post_inc:	difference = 1; break;
		case insert_post_dec:	difference =-1; break;
		default: throw new IllegalArgumentException("Invalid mutation operator.");
		}
		
		/** construct the state errors in the mutation **/
		List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
		for(CirExpression usage_point : usage_points) {
			// state_errors.add(sem_mutation.get_assertions().mut_value(usage_point));
			state_errors.add(sem_mutation.get_assertions().diff_value(usage_point, difference));
		}
		if(!state_errors.isEmpty()) { this.infect(state_errors); }
	}
	

}
