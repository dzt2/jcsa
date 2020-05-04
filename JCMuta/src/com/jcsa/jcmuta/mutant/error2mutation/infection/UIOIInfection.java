package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.PathConditions;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;

public class UIOIInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	/**
	 * get all the paths from source to the end of the function where it is injected
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private Collection<List<CirExecutionFlow>> get_following_paths(CirStatement source) throws Exception {
		CirStatement target = source.get_tree().get_function_call_graph().
				get_function(source).get_flow_graph().get_exit().get_statement();
		return PathConditions.paths_of(source, target);
	}
	
	/**
	 * get the list of statements in the sequence of path being provided
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private List<CirStatement> get_statement_path(List<CirExecutionFlow> path) throws Exception {
		List<CirStatement> sequence = new ArrayList<CirStatement>();
		sequence.add(path.get(0).get_source().get_statement());
		
		for(CirExecutionFlow flow : path) {
			switch(flow.get_type()) {
			case call_flow:
			case retr_flow: break;
			default: sequence.add(flow.get_target().get_statement()); break;
			}
		}
		
		return sequence;
	}
	
	/**
	 * get all the usage points w.r.t. the given code of expression definition
	 * @param expression
	 * @param key
	 * @param use_points
	 * @throws Exception
	 */
	private void collect_use_points_in(CirExpression expression, 
			String key, Set<CirExpression> use_points) throws Exception {
		if(expression instanceof CirReferExpression && 
				expression.generate_code().equals(key)) use_points.add(expression);
		
		if(expression instanceof CirNameExpression) {
			
		}
		else if(expression instanceof CirDeferExpression) {
			this.collect_use_points_in(((CirDeferExpression) expression).get_address(), key, use_points);
		}
		else if(expression instanceof CirFieldExpression) {
			this.collect_use_points_in(((CirFieldExpression) expression).get_body(), key, use_points);
		}
		else if(expression instanceof CirAddressExpression) {
			this.collect_use_points_in(((CirAddressExpression) expression).get_operand(), key, use_points);
		}
		else if(expression instanceof CirCastExpression) {
			this.collect_use_points_in(((CirCastExpression) expression).get_operand(), key, use_points);
		}
		else if(expression instanceof CirInitializerBody) {
			CirInitializerBody body = (CirInitializerBody) expression;
			for(int k = 0; k < body.number_of_elements(); k++) {
				this.collect_use_points_in(body.get_element(k), key, use_points);
			}
		}
		else if(expression instanceof CirWaitExpression) {
			this.collect_use_points_in(((CirWaitExpression) expression).get_function(), key, use_points);
		}
		else if(expression instanceof CirComputeExpression) {
			CirComputeExpression expr = (CirComputeExpression) expression;
			for(int k = 0; k < expr.number_of_operand(); k++) {
				this.collect_use_points_in(expr.get_operand(k), key, use_points);
			}
		}
		
	}
	
	/**
	 * whether the reference is redefined in the statement
	 * @param statement
	 * @param reference
	 * @return
	 * @throws Exception
	 */
	private boolean is_defined_by(CirStatement statement, CirExpression reference) throws Exception {
		if(statement instanceof CirAssignStatement) {
			CirExpression expression = ((CirAssignStatement) statement).get_lvalue();
			String key = expression.generate_code();
			
			Queue<CirNode> queue = new LinkedList<CirNode>(); queue.add(reference);
			while(!queue.isEmpty()) {
				CirNode node = queue.poll();
				if(node.generate_code().equals(key)) return true;
			}
			return false;
		}
		else {
			return false;
		}
	}
	
	/**
	 * get the set of usage points in program path as given
	 * @param path
	 * @param reference
	 * @return
	 * @throws Exception
	 */
	private Set<CirExpression> collect_use_points_in_path(List<CirExecutionFlow> path, CirExpression reference) throws Exception {
		Set<CirExpression> use_points = new HashSet<CirExpression>();
		String key = reference.generate_code();
		List<CirStatement> sequence = this.get_statement_path(path);
		
		for(CirStatement statement : sequence) {
			if(statement instanceof CirAssignStatement) {
				this.collect_use_points_in(((CirAssignStatement) statement).get_rvalue(), key, use_points);
				if(this.is_defined_by(statement, reference)) break;	// stop to traverse the path
			}
			else if(statement instanceof CirCallStatement) {
				this.collect_use_points_in(((CirCallStatement) statement).get_function(), key, use_points);
				CirArgumentList arguments = ((CirCallStatement) statement).get_arguments();
				for(int k = 0; k < arguments.number_of_arguments(); k++) {
					this.collect_use_points_in(arguments.get_argument(k), key, use_points);
				}
			}
			else if(statement instanceof CirIfStatement) {
				this.collect_use_points_in(((CirIfStatement) statement).get_condition(), key, use_points);
			}
			else if(statement instanceof CirCaseStatement) {
				this.collect_use_points_in(((CirCaseStatement) statement).get_condition(), key, use_points);
			}
		}
		
		return use_points;
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = this.get_result_of(cir_tree, this.get_location(mutation));
		CirStatement source = expression.statement_of();
		Collection<List<CirExecutionFlow>> paths = this.get_following_paths(source);
		
		Set<CirExpression> use_points = new HashSet<CirExpression>();
		for(List<CirExecutionFlow> path : paths) {
			Set<CirExpression> buffer = this.collect_use_points_in_path(path, expression);
			use_points.addAll(buffer);
		}
		
		long difference;
		switch(mutation.get_mutation_operator()) {
		case insert_prev_inc: difference = 1; break;
		case insert_post_inc: difference = 1; use_points.remove(expression); break;
		case insert_prev_dec: difference =-1; break;
		case insert_post_dec: difference =-1; use_points.remove(expression); break;
		default: throw new IllegalArgumentException(mutation.get_mutation_operator().toString());
		}
		
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		for(CirExpression use_point : use_points) {
			output.put(graph.get_error_set().dif_numb(use_point, difference), constraints);
		}
	}

}
