package com.jcsa.jcmutest.mutant.sym2mutant.muta.stmt;

import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.CirMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SGLRCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, mutation.get_location());
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		CirExecution source = cir_tree.get_localizer().get_execution(statement);
		CirExecutionFlow orig_flow = source.get_ou_flow(0);
		
		CirStatement next_statement = this.get_beg_statement(cir_tree, (AstNode) mutation.get_parameter());
		CirExecution target = cir_tree.get_localizer().get_execution(next_statement);
		CirExecutionFlow muta_flow = CirExecutionFlow.virtual_flow(source, target);
		
		infections.put(SymInstances.flow_error(orig_flow, muta_flow), SymInstances.expr_constraint(statement, Boolean.TRUE, true));
	}

}
