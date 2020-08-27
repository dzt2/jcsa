package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadInfection;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UIORSadInfection extends SadInfection {
	
	/* ++x */
	/**
	 * 	x := x + 1 ==> x := x - 1 {use_point}
	 * 	{true}
	 * 	add_operand(x + 1, -2)
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void prev_inc_to_prev_dec(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		/* getters */
		AstNode location = mutation.get_location();
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		CirExpression ref_expression = inc_stmt.get_rvalue();
		
		/* true ==> add_operand(ref_expression, +, -2) */
		SadAssertion state_error = SadFactory.add_operand(inc_stmt, 
				ref_expression, COperator.arith_add, SadFactory.constant(-2));
		this.connect(reach_node, state_error);
	}
	/**
	 * x := x + 1 {use} ==> x := x + 1 {use - 1}
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void prev_inc_to_post_inc(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstNode location = mutation.get_location();
		CirExpression use_expression = this.find_result(tree, location);
		CirStatement use_stmt = use_expression.statement_of();
		
		/* true ==> add_operand(use_expression, +, -1) */
		if(use_stmt != null) {
			SadAssertion state_error = SadFactory.add_operand(use_stmt, 
					use_expression, COperator.arith_add, SadFactory.constant(-1));
			this.connect(reach_node, state_error);
		}
	}
	/**
	 * x := x + 1 {use} ==> x := x - 1 {use - 1}
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void prev_inc_to_post_dec(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		/* getters */
		AstNode location = mutation.get_location();
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		CirExpression ref_expression = inc_stmt.get_rvalue();
		CirExpression use_expression = this.find_result(tree, location);
		CirStatement use_stmt = use_expression.statement_of();
		
		/* true ==> add_operand(use_expression, +, -1) && add_operand(ref_expression, +, -2) */
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		if(use_stmt != null) {
			state_errors.add(SadFactory.add_operand(use_stmt, use_expression, 
							COperator.arith_add, SadFactory.constant(-1)));
		}
		state_errors.add(SadFactory.add_operand(inc_stmt, 
				ref_expression, COperator.arith_add, SadFactory.constant(-2)));
		
		/* graph connections */
		SadAssertion state_error = SadFactory.conjunct(inc_stmt, state_errors);
		this.connect(reach_node, state_error);
	}
	
	/* --x */
	/**
	 * 	x := x - 1 {use} ==> x := x + 1 {use}
	 * 	{true}
	 * 	add_operand(x - 1, +, 2)
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void prev_dec_to_prev_inc(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		/* getters */
		AstNode location = mutation.get_location();
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		CirExpression ref_expression = inc_stmt.get_rvalue();
		
		/* true ==> add_operand(ref_expression, +, -2) */
		SadAssertion state_error = SadFactory.add_operand(inc_stmt, 
				ref_expression, COperator.arith_add, SadFactory.constant(2));
		this.connect(reach_node, state_error);
	}
	/**
	 * x := x - 1 {use} ==> x := x - 1 {use + 1}
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void prev_dec_to_post_dec(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstNode location = mutation.get_location();
		CirExpression use_expression = this.find_result(tree, location);
		CirStatement use_stmt = use_expression.statement_of();
		
		/* true ==> add_operand(use_expression, +, -1) */
		if(use_stmt != null) {
			SadAssertion state_error = SadFactory.add_operand(use_stmt, 
					use_expression, COperator.arith_add, SadFactory.constant(1));
			this.connect(reach_node, state_error);
		}
	}
	/**
	 * x := x - 1 {use} ==> x := x + 1 {use + 1}
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void prev_dec_to_post_inc(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		/* getters */
		AstNode location = mutation.get_location();
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		CirExpression ref_expression = inc_stmt.get_rvalue();
		CirExpression use_expression = this.find_result(tree, location);
		CirStatement use_stmt = use_expression.statement_of();
		
		/* true ==> add_operand(use_expression, +, 1) && add_operand(ref_expression, +, 2) */
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		if(use_stmt != null) {
			state_errors.add(SadFactory.add_operand(use_stmt, use_expression, 
							COperator.arith_add, SadFactory.constant(1)));
		}
		state_errors.add(SadFactory.add_operand(inc_stmt, 
				ref_expression, COperator.arith_add, SadFactory.constant(2)));
		
		/* graph connections */
		SadAssertion state_error = SadFactory.conjunct(inc_stmt, state_errors);
		this.connect(reach_node, state_error);
	}
	
	/* x++ */
	/**
	 * t = x; {use}
	 * x = x + 1;	==> x = x - 1;
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void post_inc_to_post_dec(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstNode location = mutation.get_location();
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_stmt.get_rvalue();
		
		SadAssertion state_error = SadFactory.add_operand(inc_stmt, 
				inc_expression, COperator.arith_add, SadFactory.constant(-2));
		this.connect(reach_node, state_error);
	}
	/**
	 * t = x; {use}	==> t = x; {use + 1}
	 * x = x + 1;	==> x = x + 1;
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void post_inc_to_prev_inc(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstNode location = mutation.get_location();
		CirExpression use_expression = this.find_result(tree, location);
		CirStatement use_stmt = use_expression.statement_of();
		
		if(use_stmt != null) {
			SadAssertion state_error = SadFactory.add_operand(use_stmt, 
					use_expression, COperator.arith_add, SadFactory.constant(1));
			this.connect(reach_node, state_error);
		}
	}
	/**
	 * t = x; {use}	==> t = x; {use - 1}
	 * x = x + 1;	==> x = x - 1;
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void post_inc_to_prev_dec(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstNode location = mutation.get_location();
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_stmt.get_rvalue();
		CirExpression use_expression = this.find_result(tree, location);
		CirStatement use_stmt = use_expression.statement_of();
		
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		if(use_stmt != null) {
			state_errors.add(SadFactory.add_operand(use_stmt, use_expression, 
					COperator.arith_add, SadFactory.constant(-1)));
		}
		state_errors.add(SadFactory.add_operand(inc_stmt, 
				inc_expression, COperator.arith_add, SadFactory.constant(-2)));
		
		this.connect(reach_node, SadFactory.conjunct(inc_stmt, state_errors));
	}
	
	/* x-- */
	/**
	 * t = x; {use}
	 * x = x - 1;	==> x = x + 1;
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void post_dec_to_post_inc(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstNode location = mutation.get_location();
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_stmt.get_rvalue();
		
		SadAssertion state_error = SadFactory.add_operand(inc_stmt, 
				inc_expression, COperator.arith_add, SadFactory.constant(2));
		this.connect(reach_node, state_error);
	}
	/**
	 * t = x; {use}	==> t = x; {use + 1}
	 * x = x - 1;	==> x = x - 1;
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void post_dec_to_prev_dec(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstNode location = mutation.get_location();
		CirExpression use_expression = this.find_result(tree, location);
		CirStatement use_stmt = use_expression.statement_of();
		
		if(use_stmt != null) {
			SadAssertion state_error = SadFactory.add_operand(use_stmt, 
					use_expression, COperator.arith_add, SadFactory.constant(1));
			this.connect(reach_node, state_error);
		}
	}
	/**
	 * t = x; {use}	==> t = x; {use + 1}
	 * x = x - 1;	==> x = x + 1;
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void post_dec_to_prev_inc(CirTree tree, AstMutation 
			mutation, SadVertex reach_node) throws Exception {
		AstNode location = mutation.get_location();
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_stmt.get_rvalue();
		CirExpression use_expression = this.find_result(tree, location);
		CirStatement use_stmt = use_expression.statement_of();
		
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		if(use_stmt != null) {
			state_errors.add(SadFactory.add_operand(use_stmt, use_expression, 
					COperator.arith_add, SadFactory.constant(1)));
		}
		state_errors.add(SadFactory.add_operand(inc_stmt, 
				inc_expression, COperator.arith_add, SadFactory.constant(2)));
		
		this.connect(reach_node, SadFactory.conjunct(inc_stmt, state_errors));
	}
	
	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		switch(mutation.get_operator()) {
		case prev_inc_to_prev_dec:	this.prev_inc_to_prev_dec(tree, mutation, reach_node); break;
		case prev_inc_to_post_inc:	this.prev_inc_to_post_inc(tree, mutation, reach_node); break;
		case prev_inc_to_post_dec:	this.prev_inc_to_post_dec(tree, mutation, reach_node); break;
		case prev_dec_to_prev_inc:	this.prev_dec_to_prev_inc(tree, mutation, reach_node); break;
		case prev_dec_to_post_dec:	this.prev_dec_to_post_dec(tree, mutation, reach_node); break;
		case prev_dec_to_post_inc:	this.prev_dec_to_post_inc(tree, mutation, reach_node); break;
		case post_inc_to_post_dec:	this.post_inc_to_post_dec(tree, mutation, reach_node); break;
		case post_inc_to_prev_inc:	this.post_inc_to_prev_inc(tree, mutation, reach_node); break;
		case post_inc_to_prev_dec:	this.post_inc_to_prev_dec(tree, mutation, reach_node); break;
		case post_dec_to_post_inc:	this.post_dec_to_post_inc(tree, mutation, reach_node); break;
		case post_dec_to_prev_dec:	this.post_dec_to_prev_dec(tree, mutation, reach_node); break;
		case post_dec_to_prev_inc:	this.post_dec_to_prev_inc(tree, mutation, reach_node); break;
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.toString());
		}
	}

}
