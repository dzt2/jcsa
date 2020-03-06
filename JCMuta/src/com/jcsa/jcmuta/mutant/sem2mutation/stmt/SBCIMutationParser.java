package com.jcsa.jcmuta.mutant.sem2mutation.stmt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmuta.MutaOperator;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class SBCIMutationParser extends SemanticMutationParser {

	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		return this.get_prev_statement(ast_mutation.get_location());
	}
	
	/**
	 * find the looping statement where the mutation is performed
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private AstStatement get_loop_statement(AstMutation ast_mutation) throws Exception {
		AstNode location = ast_mutation.get_location();
		while(location != null) {
			if(location instanceof AstForStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement) {
				return (AstStatement) location;
			}
			else { location = location.get_parent(); }
		}
		throw new IllegalArgumentException("Invalid location");
	}
	
	/**
	 * get the conditional statement of the looping
	 * @param cir_tree
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirIfStatement get_condition_statement(CirTree cir_tree, AstMutation ast_mutation) throws Exception {
		AstStatement ast_statement = this.get_loop_statement(ast_mutation);
		return (CirIfStatement) this.get_cir_node(ast_statement, CirIfStatement.class);
	}
	
	/**
	 * equal_with(condition, true) not_equal(condition, 0|Nullptr)
	 * @param expression
	 * @param sem_mutation
	 * @return
	 * @throws Exception
	 */
	private SemanticAssertion get_constraint(CirExpression expression, SemanticMutation sem_mutation) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(data_type)) {
			return sem_mutation.get_assertions().equal_with(expression, Boolean.TRUE);
		}
		else if(CTypeAnalyzer.is_number(data_type)) {
			return sem_mutation.get_assertions().not_equals(expression, Long.valueOf(0));
		}
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			return sem_mutation.get_assertions().not_equals(expression, Nullptr);
		}
		else throw new IllegalArgumentException("Invalid data_type");
	}
	
	/**
	 * get the statements within the looping location
	 * @param cir_tree
	 * @param loop_statement
	 * @return
	 * @throws Exception
	 */
	private Set<CirStatement> collect_statements_in(CirTree cir_tree, AstMutation ast_mutation) throws Exception {
		AstNode location = this.get_loop_statement(ast_mutation);
		Set<CirStatement> cir_statements = new HashSet<CirStatement>();
		Queue<AstNode> ast_queue = new LinkedList<AstNode>();
		
		ast_queue.add(location);
		while(!ast_queue.isEmpty()) {
			AstNode ast_node = ast_queue.poll();
			for(int k = 0; k < ast_node.number_of_children(); k++) {
				AstNode ast_child = ast_node.get_child(k);
				if(ast_child != null) ast_queue.add(ast_child);
			}
			
			AstCirPair range = this.get_cir_range(ast_node);
			if(range != null && range.executional()) {
				cir_statements.add(range.get_beg_statement());
				cir_statements.add(range.get_end_statement());
			}
		}
		
		
		return cir_statements;
	}
	
	/* statement traversal */
	/**
	 * perform deep first traversal from the current execution until the end is reached
	 * @param cur_execution
	 * @param end_execution
	 * @param visited_set
	 * @throws Exception
	 */
	private void deep_first_traversal(CirExecution cur_execution, 
			CirExecution end_execution, Set<CirExecution> visited_set) throws Exception {
		if(!visited_set.contains(cur_execution) && cur_execution != end_execution) {
			visited_set.add(cur_execution);
			for(CirExecutionFlow execution_flow : cur_execution.get_ou_flows()) {
				deep_first_traversal(execution_flow.get_target(), end_execution, visited_set);
			}
		}
	}
	
	/**
	 * collect all the statements that can be reached from the beg-statement
	 * until the end statement is reached.
	 * @param beg_statement
	 * @param end_statement
	 * @return
	 * @throws Exception
	 */
	private Set<CirStatement> collect_reaching_set(CirStatement 
			beg_statement, CirIfStatement end_statement) throws Exception {
		CirFunctionCallGraph graph = beg_statement.get_tree().get_function_call_graph();
		CirExecution beg = graph.get_function(beg_statement).get_flow_graph().get_execution(beg_statement);
		CirExecution end = graph.get_function(end_statement).get_flow_graph().get_execution(end_statement);
		
		Set<CirExecution> reaching_set = new HashSet<CirExecution>();
		deep_first_traversal(beg, end, reaching_set); 
		
		Set<CirStatement> reach_set = new HashSet<CirStatement>();
		for(CirExecution execution : reaching_set) {
			reach_set.add(execution.get_statement());
		}
		return reach_set;
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		CirStatement beg_statement = this.get_prev_statement(ast_mutation.get_location());
		CirIfStatement end_statement = this.get_condition_statement(cir_tree, ast_mutation);
		
		/** determine the reaching set and previous statement set **/
		Set<CirStatement> loop_body = this.collect_statements_in(cir_tree, ast_mutation);
		Set<CirStatement> reach_set = this.collect_reaching_set(beg_statement, end_statement);
		Set<CirStatement> reaching_set = new HashSet<CirStatement>();
		for(CirStatement statement : reach_set) {
			if(loop_body.contains(statement)) reaching_set.add(statement);
		}
		Set<CirStatement> previous_set = new HashSet<CirStatement>();
		for(CirStatement statement : loop_body) {
			if(!reaching_set.contains(statement)) 
				previous_set.add(statement);
		}
		
		/** cover(stmt) ==> disactive(reach_set); **/
		List<SemanticAssertion> errors1 = new ArrayList<SemanticAssertion>();
		for(CirStatement statement : reaching_set) {
			if(!(statement instanceof CirTagStatement)) {
				errors1.add(sem_mutation.get_assertions().disactive(statement));
			}
		}
		if(!errors1.isEmpty()) { this.infect(errors1); }
		
		/** cover(stmt); condition = true; ==> disactive(prev_stmt) for break inserted **/
		if(ast_mutation.get_mutation_operator() == MutaOperator.ins_break) {
			List<SemanticAssertion> errors2 = new ArrayList<SemanticAssertion>();
			for(CirStatement statement : previous_set) {
				if(!(statement instanceof CirTagStatement)) {
					errors2.add(sem_mutation.get_assertions().disactive(statement));
				}
			}
			if(!errors2.isEmpty()) {
				SemanticAssertion constraint = 
						this.get_constraint(end_statement.get_condition(), sem_mutation);
				this.infect(constraint, errors2);
			}
		}
	}

}
