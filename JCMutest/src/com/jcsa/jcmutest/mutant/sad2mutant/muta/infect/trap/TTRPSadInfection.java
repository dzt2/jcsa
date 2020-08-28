package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.trap;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.SadInfection;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * execute_on(statement, 1)
 * execute_on(statement, n)
 * trapping()
 * @author yukimula
 *
 */
public class TTRPSadInfection extends SadInfection {
	
	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		/* find the first statement in loop statement */
		AstNode location = mutation.get_location();
		CirStatement statement = (CirStatement) get_cir_nodes(
				tree, location, CirIfStatement.class).get(0);
		int time = ((Integer) mutation.get_parameter()).intValue();
		
		/* execute_on(statement, time) */
		SadAssertion constraint = SadFactory.assert_execution(statement, time);
		SadAssertion state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
	}
	
}
