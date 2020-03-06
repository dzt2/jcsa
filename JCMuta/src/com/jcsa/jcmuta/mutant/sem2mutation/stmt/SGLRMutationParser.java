package com.jcsa.jcmuta.mutant.sem2mutation.stmt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabel;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class SGLRMutationParser extends SemanticMutationParser {
	
	private static final int maximal_number = 32;
	
	private CirStatement get_statement_of(CirTree cir_tree, AstLabel label) throws Exception {
		AstGotoStatement goto_statement = (AstGotoStatement) label.get_parent();
		return (CirStatement) this.get_cir_node(goto_statement, CirGotoStatement.class);
	}
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		return this.get_statement_of(cir_tree, (AstLabel) ast_mutation.get_location());
	}
	
	/**
	 * collect the statements following the beg-statement
	 * @param beg
	 * @param size
	 * @return
	 * @throws Exception
	 */
	private Set<CirStatement> collect_following_statement(CirStatement beg, int size) throws Exception {
		/** 1. declarations **/
		CirExecution beg_execution = beg.get_tree().get_function_call_graph().
						get_function(beg).get_flow_graph().get_execution(beg);
		Queue<CirExecution> queue = new LinkedList<CirExecution>();
		Set<CirExecution> visited = new HashSet<CirExecution>();
		
		/** 2. initialize the BFS queue **/
		queue.add(beg_execution); visited.add(beg_execution);
		
		/** 3. brandth-first traversal **/
		while(!queue.isEmpty() && visited.size() <= size) {
			CirExecution execution = queue.poll();
			for(CirExecutionFlow execution_flow : execution.get_ou_flows()) {
				if(!visited.contains(execution_flow.get_target())) {
					visited.add(execution_flow.get_target());
					queue.add(execution_flow.get_target());
				}
			}
		}
		
		/** 4. extract the execution statements **/
		Set<CirStatement> statements = new HashSet<CirStatement>();
		for(CirExecution execution : visited) {
			statements.add(execution.get_statement());
		}
		return statements;
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		CirStatement source_statement = this.get_statement_of(cir_tree, (AstLabel) ast_mutation.get_location());
		CirStatement target_statement = this.get_statement_of(cir_tree, (AstLabel) ast_mutation.get_parameter());
		
		Set<CirStatement> source_followings = collect_following_statement(source_statement, maximal_number);
		Set<CirStatement> target_followings = collect_following_statement(target_statement, maximal_number);
		Set<CirStatement> intersection = new HashSet<CirStatement>();
		for(CirStatement statement : source_followings) {
			if(target_followings.contains(statement)) { intersection.add(statement); }
		}
		source_followings.removeAll(intersection); target_followings.removeAll(intersection);
		
		List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
		for(CirStatement statement : source_followings) {
			if(!(statement instanceof CirTagStatement)) {
				state_errors.add(sem_mutation.get_assertions().disactive(statement));
			}
		}
		for(CirStatement statement : target_followings) {
			if(!(statement instanceof CirTagStatement)) {
				state_errors.add(sem_mutation.get_assertions().active(statement));
			}
		}
		
		if(!state_errors.isEmpty()) {
			this.infect(state_errors);
		}
	}

}
