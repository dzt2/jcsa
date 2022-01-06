package com.jcsa.jcmutest.mutant.cir2mutant.muta.refs;

import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VBRPCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		SymbolExpression muta_value; CirAttribute constraint;
		CirExecution execution = statement.execution_of();
		switch(mutation.get_operator()) {
		case set_true:
		{
			constraint = CirAttribute.new_constraint(execution, expression, false);
			muta_value = SymbolFactory.sym_expression(Boolean.TRUE);
			infections.put(CirAttribute.new_value_error(expression, muta_value), constraint);
			break;
		}
		case set_false:
		{
			constraint = CirAttribute.new_constraint(execution, expression, true);
			muta_value = SymbolFactory.sym_expression(Boolean.FALSE);
			infections.put(CirAttribute.new_value_error(expression, muta_value), constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
	}

}
