package com.jcsa.jcmutest.mutant.sym2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.CirMutationParser;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UIOICirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		CirReferExpression reference = (CirReferExpression)
				this.get_cir_expression(cir_tree, mutation.get_location());
		SymbolExpression muta_expression;
		SymConstraint constraint = SymInstances.expr_constraint(statement, Boolean.TRUE, true);

		switch(mutation.get_operator()) {
		case insert_post_inc:
		{
			muta_expression = SymbolFactory.arith_add(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(SymInstances.stat_error(reference, muta_expression), constraint);
			break;
		}
		case insert_post_dec:
		{
			muta_expression = SymbolFactory.arith_sub(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(SymInstances.stat_error(reference, muta_expression), constraint);
			break;
		}
		case insert_prev_inc:
		{
			muta_expression = SymbolFactory.arith_add(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(SymInstances.stat_error(reference, muta_expression), constraint);
			infections.put(SymInstances.expr_error(reference, muta_expression), constraint);
			break;
		}
		case insert_prev_dec:
		{
			muta_expression = SymbolFactory.arith_sub(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(SymInstances.stat_error(reference, muta_expression), constraint);
			infections.put(SymInstances.expr_error(reference, muta_expression), constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
	}

}
