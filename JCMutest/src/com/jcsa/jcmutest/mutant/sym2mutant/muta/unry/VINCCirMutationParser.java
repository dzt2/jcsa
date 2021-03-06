package com.jcsa.jcmutest.mutant.sym2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.CirMutationParser;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VINCCirMutationParser extends CirMutationParser {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		SymbolExpression muta_value; 
		SymConstraint constraint = SymInstances.expr_constraint(statement, Boolean.TRUE, true);
		switch(mutation.get_operator()) {
		case inc_constant:
		{
			muta_value = SymbolFactory.arith_add(expression.get_data_type(), expression, mutation.get_parameter());
			break;
		}
		case mul_constant:
		{
			muta_value = SymbolFactory.arith_mul(expression.get_data_type(), expression, mutation.get_parameter());
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		infections.put(SymInstances.expr_error(expression, muta_value), constraint);
	}

}
