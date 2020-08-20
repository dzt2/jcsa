package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;

public class SWDRMutationParser extends CirMutationParser {
	
	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		CirIfStatement if_statement = (CirIfStatement) this.get_cir_nodes(
				tree, source.get_location(), CirIfStatement.class).get(0);
		CirExpression expression = if_statement.get_condition();
		
		switch(source.get_operator()) {
		case while_to_do_while:
			targets.add(CirMutations.set_data_state(expression, true, 1)); break;
		case do_while_to_while:
			targets.add(CirMutations.trap_on_equal(expression, false)); break;
		default: throw new IllegalArgumentException("Invalid: " + source);
		}
	}
	
}
