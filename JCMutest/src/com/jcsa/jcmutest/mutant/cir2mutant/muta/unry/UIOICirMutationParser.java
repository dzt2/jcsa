package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
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
	protected void generate_infections(CirMutations mutations, CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		CirReferExpression reference = (CirReferExpression) 
				this.get_cir_expression(cir_tree, mutation.get_location());
		SymbolExpression muta_expression;
		SymConstraint constraint = mutations.expression_constraint(statement, Boolean.TRUE, true);
		
		switch(mutation.get_operator()) {
		case insert_post_inc:
		{
			muta_expression = SymbolFactory.arith_add(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(mutations.state_error(reference, muta_expression), constraint);
			break;
		}
		case insert_post_dec:
		{
			muta_expression = SymbolFactory.arith_sub(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(mutations.state_error(reference, muta_expression), constraint);
			break;
		}
		case insert_prev_inc:
		{
			muta_expression = SymbolFactory.arith_add(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(mutations.state_error(reference, muta_expression), constraint);
			infections.put(mutations.expr_error(reference, muta_expression), constraint);
			break;
		}
		case insert_prev_dec:
		{
			muta_expression = SymbolFactory.arith_sub(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(mutations.state_error(reference, muta_expression), constraint);
			infections.put(mutations.expr_error(reference, muta_expression), constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
	}

}
