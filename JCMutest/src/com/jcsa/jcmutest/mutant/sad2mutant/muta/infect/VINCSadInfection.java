package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadInfection;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class VINCSadInfection extends SadInfection {

	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		CirExpression expression = this.find_result(tree, mutation.get_location());
		CirStatement statement = expression.statement_of();
		Object parameter = mutation.get_parameter(); SadExpression operand;
		if(parameter instanceof Long) {
			operand = SadFactory.constant(((Long) parameter).longValue());
		}
		else {
			operand = SadFactory.constant(((Double) parameter).doubleValue());
		}
		
		switch(mutation.get_operator()) {
		case inc_constant:
		{
			this.connect(reach_node, SadFactory.add_operand(statement, expression, COperator.arith_add, operand));
			break;
		}
		case mul_constant:
		{
			this.connect(reach_node, SadFactory.add_operand(statement, expression, COperator.arith_mul, operand));
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation);
		}
	}

}
