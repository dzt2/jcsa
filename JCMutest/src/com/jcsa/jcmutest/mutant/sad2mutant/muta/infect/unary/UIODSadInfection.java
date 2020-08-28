package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.unary;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.SadInfection;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UIODSadInfection extends SadInfection {
	
	/**
	 * x = x + 1; ==> x = x;
	 * 
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void delete_inc(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		AstNode location = mutation.get_location();
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_stmt.get_rvalue();
		SadAssertion state_error = SadFactory.add_operand(inc_stmt, 
				inc_expression, COperator.arith_add, SadFactory.constant(-1));
		this.connect(reach_node, state_error);
	}
	/**
	 * x = x - 1; ==> x = x;
	 * @param tree
	 * @param mutation
	 * @param reach_node
	 * @throws Exception
	 */
	private void delete_dec(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		AstNode location = mutation.get_location();
		CirAssignStatement inc_stmt = (CirAssignStatement) get_cir_nodes(
					tree, location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_stmt.get_rvalue();
		SadAssertion state_error = SadFactory.add_operand(inc_stmt, 
				inc_expression, COperator.arith_add, SadFactory.constant(1));
		this.connect(reach_node, state_error);
	}
	
	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		switch(mutation.get_operator()) {
		case delete_prev_inc:	
		case delete_post_inc:	this.delete_inc(tree, mutation, reach_node); break;
		case delete_prev_dec:
		case delete_post_dec:	this.delete_dec(tree, mutation, reach_node); break;
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.toString());
		}
	}

}
