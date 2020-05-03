package com.jcsa.jcmuta.mutant.error2mutation.infection;

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
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;

public class SBCIInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return PathConditions.find_cir_location(cir_tree, this.get_location(mutation));
	}
	
	/**
	 * get the IF-conditional statement with respect to the looping parent
	 * @param cir_tree
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirIfStatement find_conditional_statement(CirTree cir_tree, AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		while(location != null) {
			CirIfStatement if_statement;
			if(location instanceof AstWhileStatement) {
				if_statement = (CirIfStatement) cir_tree.get_cir_nodes(location, CirIfStatement.class);
				return if_statement;
			}
			else if(location instanceof AstDoWhileStatement) {
				if_statement = (CirIfStatement) cir_tree.get_cir_nodes(location, CirIfStatement.class);
				return if_statement;
			}
			else if(location instanceof AstForStatement) {
				if_statement = (CirIfStatement) cir_tree.get_cir_nodes(location, CirIfStatement.class);
				return if_statement;
			}
			else {
				location = location.get_parent();
			}
		}
		return null;	/* unable to find */
	}
	
	/**
	 * if-end statement
	 * @param cir_tree
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirTagStatement find_end_statement(CirTree cir_tree, AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		while(location != null) {
			if(location instanceof AstWhileStatement) {
				return (CirTagStatement) cir_tree.get_cir_nodes(location, CirIfEndStatement.class);
			}
			else if(location instanceof AstDoWhileStatement) {
				return (CirTagStatement) cir_tree.get_cir_nodes(location, CirIfEndStatement.class);
			}
			else if(location instanceof AstForStatement) {
				return (CirTagStatement) cir_tree.get_cir_nodes(location, CirIfEndStatement.class);
			}
			else {
				location = location.get_parent();
			}
		}
		return null;	/* unable to find */
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/* 1. get the loop statement and seeded point */
		CirStatement seed_statement = PathConditions.
								find_cir_location(cir_tree, this.get_location(mutation));
		CirIfStatement beg_statement = this.find_conditional_statement(cir_tree, mutation);
		CirTagStatement end_statement = this.find_end_statement(cir_tree, mutation);
		
		/* 2. invalid insertion */
		if(beg_statement == null) {
			output.put(graph.get_error_set().syntax_error(), new StateConstraints(true));
		}
		/* 3. insert break to prevent all the statements in block being not executed */
		else if(mutation.get_mutation_operator() == MutaOperator.ins_break) {
			Set<CirExecutionFlow> prev_flows = PathConditions.must_be_path(
					PathConditions.paths_of(beg_statement, seed_statement));
			Set<CirExecutionFlow> post_flows = PathConditions.must_be_path(
					PathConditions.paths_of(seed_statement, end_statement));
			
			Set<CirStatement> prev_statements = new HashSet<CirStatement>();
			Set<CirStatement> post_statements = new HashSet<CirStatement>();
			for(CirExecutionFlow flow : prev_flows) {
				prev_statements.add(flow.get_source().get_statement());
				prev_statements.add(flow.get_target().get_statement());
			}
			for(CirExecutionFlow flow : post_flows) {
				post_statements.add(flow.get_source().get_statement());
				post_statements.add(flow.get_target().get_statement());
			}
			post_statements.remove(seed_statement);
			
			/* condition == true --> prev_statements not executed */
			StateConstraints cond_constraints = new StateConstraints(true);
			cond_constraints.add_constraint(beg_statement, 
					this.get_sym_condition(beg_statement.get_condition(), true));
			for(CirStatement statement : prev_statements) {
				if(!(statement instanceof CirTagStatement)) {
					output.put(graph.get_error_set().not_execute(statement), cond_constraints);
				}
			}
			
			/* true --> post_statements not execute */
			StateConstraints true_constraint = new StateConstraints(true);
			for(CirStatement statement : post_statements) {
				if(!(statement instanceof CirTagStatement)) {
					output.put(graph.get_error_set().not_execute(statement), true_constraint);
				}
			}
		}
		/* 4. insert continue statement */
		else {
			Set<CirExecutionFlow> post_flows = PathConditions.must_be_path(
					PathConditions.paths_of(seed_statement, end_statement));
			Set<CirStatement> post_statements = new HashSet<CirStatement>();
			for(CirExecutionFlow flow : post_flows) {
				post_statements.add(flow.get_source().get_statement());
				post_statements.add(flow.get_target().get_statement());
			}
			
			/* true --> post_statements not execute */
			StateConstraints true_constraint = new StateConstraints(true);
			for(CirStatement statement : post_statements) {
				if(!(statement instanceof CirTagStatement)) {
					output.put(graph.get_error_set().not_execute(statement), true_constraint);
				}
			}
		}
	}

}
