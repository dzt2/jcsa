package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadInfection;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	execute_on(faulty_statement)
 * 	assert#faulty_statement:
 * 	trapping()
 * 	@author yukimula
 *
 */
public class BTRPSadInfection extends SadInfection {

	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		/* declarations and getters */
		AstNode location = mutation.get_location();
		CirStatement statement = this.find_beg_stmt(tree, location);
		CirExpression expression = this.find_result(tree, location);
		
		/* assert#statement: condition */
		boolean value;
		switch(mutation.get_operator()) {
		case trap_on_true:		value = true;	break;
		case trap_on_false:		value = false;	break;
		default: throw new IllegalArgumentException("Invalid: " + mutation.toString());
		}
		
		/* execute(statement, 1) --> assert(condition, true|false) --> trapping() */
		SadAssertion constraint = SadFactory.assert_condition(
						statement, this.condition_of(expression, value));
		SadAssertion state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
	}

}
