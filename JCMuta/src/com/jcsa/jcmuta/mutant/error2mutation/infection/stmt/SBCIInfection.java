package com.jcsa.jcmuta.mutant.error2mutation.infection.stmt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmuta.MutaOperator;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.PathConditions;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;

public class SBCIInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return PathConditions.find_cir_location(cir_tree, this.get_location(mutation));
	}
	
	/**
	 * get the looping structure in which the mutant is seeded
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private AstStatement get_loop_statement(AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		while(location != null) {
			if(location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement
				|| location instanceof AstForStatement)
				return (AstStatement) location;
			else location = location.get_parent();
		}
		return null;
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		AstStatement loop_statement = this.get_loop_statement(mutation);
		/* syntax error */
		if(loop_statement == null) {
			output.put(graph.get_error_set().syntax_error(), new StateConstraints(true));
		}
		/* valid mutation */
		else {
			/** 1. get the key point in the program for analysis **/
			Set<CirStatement> statements_in_loop = this.collect_statements_in(cir_tree, loop_statement);
			/*CirIfStatement beg_statement = 
					(CirIfStatement) cir_tree.get_cir_nodes(loop_statement, CirIfStatement.class).get(0);*/
			CirIfEndStatement end_statement = 
					(CirIfEndStatement) cir_tree.get_cir_nodes(loop_statement, CirIfEndStatement.class).get(0);
			CirStatement seed_statement = this.get_location(cir_tree, mutation);
			
			/** 2. in case of inserting continue **/
			if(mutation.get_mutation_operator() == MutaOperator.ins_continue) {
				Set<CirExecutionFlow> seed_end_paths = PathConditions.might_be_path(
						PathConditions.paths_of(seed_statement, end_statement));
				Set<CirStatement> seed_end_statements = new HashSet<CirStatement>();
				for(CirExecutionFlow flow : seed_end_paths) {
					CirStatement source = flow.get_source().get_statement();
					CirStatement target = flow.get_target().get_statement();
					if(statements_in_loop.contains(source)) seed_end_statements.add(source);
					if(statements_in_loop.contains(target)) seed_end_statements.add(target);
				}
				for(CirStatement statement : seed_end_statements) {
					if(!(statement instanceof CirTagStatement))
						output.put(graph.get_error_set().not_execute(statement), new StateConstraints(true));
				}
			}
			/** 3. in case of break is inserted, remove all the statements in loop **/
			else {
				for(CirStatement statement : statements_in_loop) {
					if(!(statement instanceof CirTagStatement))
						output.put(graph.get_error_set().not_execute(statement), new StateConstraints(true));
				}
			}
		}
	}

}
