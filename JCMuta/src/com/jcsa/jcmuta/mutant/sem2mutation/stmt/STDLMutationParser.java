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
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class STDLMutationParser extends SemanticMutationParser {

	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		return this.get_prev_statement(ast_mutation.get_location());
	}
	
	/**
	 * get the statements within the looping location
	 * @param cir_tree
	 * @param loop_statement
	 * @return
	 * @throws Exception
	 */
	private Set<CirStatement> collect_statements_in(CirTree cir_tree, AstMutation ast_mutation) throws Exception {
		AstNode location = ast_mutation.get_location();
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
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		Set<CirStatement> statements = collect_statements_in(cir_tree, ast_mutation);
		List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
		
		for(CirStatement statement : statements) {
			if(!(statement instanceof CirTagStatement)) {
				state_errors.add(sem_mutation.get_assertions().disactive(statement));
			}
		}
		
		if(!state_errors.isEmpty()) {
			this.infect(state_errors);
		}
	}
	
}
