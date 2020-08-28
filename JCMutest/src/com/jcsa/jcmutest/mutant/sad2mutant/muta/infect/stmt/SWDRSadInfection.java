package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.stmt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.SadInfection;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class SWDRSadInfection extends SadInfection {
	
	/**
	 * collect all the statements in AST node
	 * @param tree
	 * @param location
	 * @param statements
	 * @throws Exception
	 */
	private void collect_statements(CirTree tree, AstNode location,
			Collection<CirStatement> statements) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range.executional()) {
			statements.add(range.get_beg_statement());
			statements.add(range.get_end_statement());
		}
		for(int k = 0; k < location.number_of_children(); k++) {
			this.collect_statements(tree, location.get_child(k), statements);
		}
	}
	
	/**
	 * {condition as false}
	 * ==> del_stmt(body)
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void while_to_do_while(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstWhileStatement location = (AstWhileStatement) mutation.get_location();
		CirIfStatement if_statement = (CirIfStatement) 
				this.get_cir_nodes(tree, location, CirIfStatement.class).get(0);
		
		SadExpression condition = this.condition_of(if_statement.get_condition(), false);
		SadAssertion constraint = SadFactory.assert_condition(if_statement, condition);
		
		HashSet<CirStatement> statements = new HashSet<CirStatement>();
		this.collect_statements(tree, location.get_body(), statements);
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		for(CirStatement statement : statements) {
			if(!(statement instanceof CirTagStatement)) {
				state_errors.add(SadFactory.del_statement(statement));
			}
		}
		SadAssertion state_error = SadFactory.conjunct(if_statement, state_errors);
		this.connect(reach_node, state_error, constraint);
	}
	
	/**
	 * {condition as false}
	 * ==> execute(body, 1)
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void do_while_to_while(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstDoWhileStatement location = (AstDoWhileStatement) mutation.get_location();
		CirIfStatement if_statement = (CirIfStatement) 
					this.get_cir_nodes(tree, location, CirIfStatement.class).get(0);
		
		SadExpression condition = this.condition_of(if_statement.get_condition(), false);
		SadAssertion constraint = SadFactory.assert_condition(if_statement, condition);
		
		HashSet<CirStatement> statements = new HashSet<CirStatement>();
		this.collect_statements(tree, location.get_body(), statements);
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		for(CirStatement statement : statements) {
			if(!(statement instanceof CirTagStatement)) {
				state_errors.add(SadFactory.assert_execution(statement, 1));
			}
		}
		SadAssertion state_error = SadFactory.conjunct(if_statement, state_errors);
		this.connect(reach_node, state_error, constraint);
	}
	
	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		switch(mutation.get_operator()) {
		case while_to_do_while: this.while_to_do_while(tree, mutation, reach_node); break;
		case do_while_to_while: this.do_while_to_while(tree, mutation, reach_node); break;
		default: throw new IllegalArgumentException("Unsupport: " + mutation.toString());
		}
	}

}
