package com.jcsa.jcmutest.mutant.sym2mutant.muta.stmt;

import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.CirMutationParser;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;


public class SWDRCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, mutation.get_location());
	}

	private void while_to_do_while(CirTree cir_tree,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		/* constraint: true_branch execute at least 1 times */
		CirIfStatement if_statement = (CirIfStatement)
				this.get_cir_node(cir_tree, mutation.get_location(), CirIfStatement.class);
		CirExecution if_execution = cir_tree.get_localizer().get_execution(if_statement);
		CirExecution true_branch = null;
		for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				true_branch = flow.get_target();
				break;
			}
		}
		SymConstraint constraint = SymInstances.expr_constraint(
					true_branch.get_statement(), Boolean.TRUE, true);

		/* beg_stmt.before --> end_stmt */
		CirStatement beg_statement = this.get_beg_statement(cir_tree, mutation.get_location());
		CirStatement end_statement = this.get_end_statement(cir_tree, mutation.get_location());
		CirExecution beg_execution = cir_tree.get_localizer().get_execution(beg_statement);
		CirExecution end_execution = cir_tree.get_localizer().get_execution(end_statement);

		CirExecutionFlow orig_flow = beg_execution.get_in_flow(0);
		CirExecutionFlow muta_flow = CirExecutionFlow.virtual_flow(
				beg_execution.get_in_flow(0).get_source(), end_execution);
		SymStateError state_error = SymInstances.flow_error(orig_flow, muta_flow);

		infections.put(state_error, constraint);
	}

	private void do_while_to_while(CirTree cir_tree,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		CirStatement beg_statement = this.get_beg_statement(cir_tree, mutation.get_location());
		CirExecution beg_execution = cir_tree.get_localizer().get_execution(beg_statement);
		CirExecutionFlow orig_flow = beg_execution.get_in_flow(0);

		CirStatement end_statement = (CirStatement)
				this.get_cir_node(cir_tree, mutation.get_location(), CirIfStatement.class);
		CirExecution end_execution = cir_tree.get_localizer().get_execution(end_statement);
		CirExecutionFlow muta_flow = CirExecutionFlow.virtual_flow(beg_execution, end_execution);

		infections.put(SymInstances.flow_error(orig_flow, muta_flow), SymInstances.expr_constraint(beg_statement, Boolean.TRUE, true));
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		switch(mutation.get_operator()) {
		case while_to_do_while:	this.while_to_do_while(cir_tree, mutation, infections); break;
		case do_while_to_while:	this.do_while_to_while(cir_tree, mutation, infections); break;
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
	}

}
