package com.jcsa.jcmutest.mutant.cir2mutant.muta.trap;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * execute(statement) --> trap(statement)
 * 
 * @author yukimula
 *
 */
public class STRPCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, mutation.get_location());
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymCondition, SymCondition> infections) throws Exception {
		CirExecution execution = CirMutations.execution_of(statement);
		SymCondition constraint = CirMutations.cov_stmt(execution, 1);
		SymCondition init_error = CirMutations.trp_stmt(execution);
		infections.put(init_error, constraint);
	}
	
}
