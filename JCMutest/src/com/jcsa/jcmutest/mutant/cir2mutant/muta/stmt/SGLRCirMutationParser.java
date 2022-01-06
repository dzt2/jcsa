package com.jcsa.jcmutest.mutant.cir2mutant.muta.stmt;

import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
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
			AstMutation mutation, Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirExecution source = cir_tree.get_localizer().get_execution(statement);
		CirExecutionFlow orig_flow = source.get_ou_flow(0);

		CirStatement next_statement = this.get_beg_statement(cir_tree, (AstNode) mutation.get_parameter());
		CirExecution target = cir_tree.get_localizer().get_execution(next_statement);
		CirExecutionFlow muta_flow = CirExecutionFlow.virtual_flow(source, target);

		CirAttribute constraint = CirAttribute.new_cover_count(statement.execution_of(), 1);
		CirAttribute init_error = CirAttribute.new_flows_error(orig_flow, muta_flow);
		infections.put(init_error, constraint);
	}

}
