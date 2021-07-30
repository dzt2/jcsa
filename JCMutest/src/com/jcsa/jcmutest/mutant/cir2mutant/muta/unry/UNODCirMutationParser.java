package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UNODCirMutationParser extends CirMutationParser {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}
	
	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		CirAttribute constraint; CirExecution execution = statement.execution_of();
		CirAttribute init_error; SymbolExpression condition, muta_value;
		
		switch(mutation.get_operator()) {
		case delete_arith_neg:
		{
			condition = SymbolFactory.not_equals(expression, Integer.valueOf(0));
			constraint = CirAttribute.new_constraint(execution, condition, true);
			muta_value = SymbolFactory.arith_neg(expression);
			init_error = CirAttribute.new_value_error(expression, muta_value);
			infections.put(init_error, constraint);
			break;
		}
		case delete_bitws_rsv:
		{
			constraint = CirAttribute.new_cover_count(execution, 1);
			muta_value = SymbolFactory.bitws_rsv(expression);
			init_error = CirAttribute.new_value_error(expression, muta_value);
			infections.put(init_error, constraint);
			break;
		}
		case delete_logic_not:
		{
			constraint = CirAttribute.new_cover_count(execution, 1);
			muta_value = SymbolFactory.logic_not(expression);
			init_error = CirAttribute.new_value_error(expression, muta_value);
			infections.put(init_error, constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation);
		}
	}
	
}
