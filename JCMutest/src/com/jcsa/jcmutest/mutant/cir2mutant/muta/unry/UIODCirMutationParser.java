package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
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
			AstMutation mutation, Map<CirAttribute, CirAttribute> infections) throws Exception {
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

			CirAttribute constraint = CirAttribute.new_cover_count(inc_statement.execution_of(), 1);
			CirAttribute init_error = CirAttribute.new_value_error(expression, SymbolFactory.sym_expression(loperand));
			infections.put(init_error, constraint); break;
		}
		default:
		{
			throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		}
	}

}
