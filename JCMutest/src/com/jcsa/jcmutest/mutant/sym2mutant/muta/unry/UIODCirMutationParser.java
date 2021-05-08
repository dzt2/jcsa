package com.jcsa.jcmutest.mutant.sym2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.CirMutationParser;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;


public class UIODCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_end_statement(cir_tree, mutation.get_location());
	}
	
	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		switch(mutation.get_operator()) {
		case delete_prev_inc:
		case delete_prev_dec:
		case delete_post_inc:
		case delete_post_dec:
		{
			CirAssignStatement inc_statement = (CirAssignStatement) this.
					get_cir_node(cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
			CirComputeExpression expression = (CirComputeExpression) inc_statement.get_rvalue();
			CirExpression loperand = expression.get_operand(0);
			SymConstraint constraint = SymInstances.expr_constraint(inc_statement, Boolean.TRUE, true);
			SymStateError state_error = SymInstances.expr_error(expression, SymbolFactory.sym_expression(loperand));
			infections.put(state_error, constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
	}

}
