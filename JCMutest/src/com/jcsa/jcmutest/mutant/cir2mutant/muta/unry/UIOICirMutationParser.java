package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 
 * @author dzt2
 *
 */
public class UIOICirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymCondition, SymCondition> infections) throws Exception {
		CirReferExpression reference = (CirReferExpression) 
				this.get_cir_expression(cir_tree, mutation.get_location());
		SymbolExpression muta_expression;
		CirExecution execution = CirMutations.execution_of(statement);
		SymCondition constraint = CirMutations.cov_stmt(execution, 1);
		
		switch(mutation.get_operator()) {
		case insert_post_inc:
		{
			muta_expression = SymbolFactory.arith_add(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(CirMutations.mut_stat(reference, muta_expression), constraint);
			break;
		}
		case insert_post_dec:
		{
			muta_expression = SymbolFactory.arith_sub(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(CirMutations.mut_stat(reference, muta_expression), constraint);
			break;
		}
		case insert_prev_inc:
		{
			muta_expression = SymbolFactory.arith_add(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(CirMutations.mut_stat(reference, muta_expression), constraint);
			infections.put(CirMutations.mut_expr(reference, muta_expression), constraint);
			break;
		}
		case insert_prev_dec:
		{
			muta_expression = SymbolFactory.arith_sub(reference.get_data_type(), reference, Integer.valueOf(1));
			infections.put(CirMutations.mut_stat(reference, muta_expression), constraint);
			infections.put(CirMutations.mut_expr(reference, muta_expression), constraint);
			break;
		}
		default: 
		{
			throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		}
	}
	
}
