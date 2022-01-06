package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
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
			AstMutation mutation, Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirReferExpression reference = (CirReferExpression)
				this.get_cir_expression(cir_tree, mutation.get_location());
		SymbolExpression muta_expression;
		CirAttribute constraint = CirAttribute.new_cover_count(statement.execution_of(), 1);

		switch(mutation.get_operator()) {
		case insert_post_inc:
		{
			muta_expression = SymbolFactory.arith_add(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(CirAttribute.new_state_error(reference, muta_expression), constraint); break;
		}
		case insert_post_dec:
		{
			muta_expression = SymbolFactory.arith_sub(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(CirAttribute.new_state_error(reference, muta_expression), constraint); break;
		}
		case insert_prev_inc:
		{
			muta_expression = SymbolFactory.arith_add(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(CirAttribute.new_state_error(reference, muta_expression), constraint);
			infections.put(CirAttribute.new_value_error(reference, muta_expression), constraint);
			break;
		}
		case insert_prev_dec:
		{
			muta_expression = SymbolFactory.arith_sub(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(CirAttribute.new_state_error(reference, muta_expression), constraint);
			infections.put(CirAttribute.new_value_error(reference, muta_expression), constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
	}

}
