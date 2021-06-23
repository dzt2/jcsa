package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.base.SymConditions;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;


/**
 * @author yukimula
 */
public class UIODCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_end_statement(cir_tree, mutation.get_location());
	}
	
	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymCondition, SymCondition> infections) throws Exception {
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
			
			CirExecution execution = SymConditions.execution_of(inc_statement);
			SymCondition constraint = SymConditions.cov_stmt(execution, 1);
			SymCondition init_error = SymConditions.mut_expr(expression, loperand);
			infections.put(init_error, constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
	}
	
}
