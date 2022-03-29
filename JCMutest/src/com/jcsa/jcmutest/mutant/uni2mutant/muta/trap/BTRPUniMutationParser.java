package com.jcsa.jcmutest.mutant.uni2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstErrorState;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniConditionState;
import com.jcsa.jcmutest.mutant.uni2mutant.muta.UniMutationParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class BTRPUniMutationParser extends UniMutationParser {

	@Override
	protected CirStatement get_reach_node(AstMutation mutation) throws Exception {
		return this.loc_end_statement(mutation.get_location());
	}

	@Override
	protected void generate_infection_map(CirStatement statement, AstMutation mutation) throws Exception {
		/* 1. derive the expression being mutated by trapping */
		CirExpression expression = this.loc_use_expression(
				mutation.get_location(), CirExpression.class);
		boolean value; SymbolExpression condition;
		switch(mutation.get_operator()) {
		case trap_on_true:	value = true;	break;
		case trap_on_false:	value = false;	break;
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		
		/* 2. generates the symbolic constraint for trapping */
		if(expression != null) {
			condition = SymbolFactory.sym_condition(expression, value);
			UniConditionState i_state = this.eva_need(statement, condition);
			UniAbstErrorState p_state = this.trp_stmt(statement);
			this.put_infection_pair(i_state, p_state);
		}
	}

}
