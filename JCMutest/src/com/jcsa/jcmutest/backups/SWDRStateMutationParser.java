package com.jcsa.jcmutest.backups;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SWDRStateMutationParser extends StateMutationParser {
	
	private CirStatement pror_to_loop(AstNode location) throws Exception {
		CirStatement statement = this.get_beg_statement(location);
		CirExecution execution = this.get_execution(statement);
		for(CirExecutionFlow flow : execution.get_in_flows()) {
			return flow.get_source().get_statement();
		}
		return null;
	}
	
	@Override
	protected CirStatement find_beg_statement(AstMutation mutation) throws Exception {
		return this.pror_to_loop(mutation.get_location());
	}
	
	@Override
	protected CirStatement find_end_statement(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}
	
	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		CirStatement source = this.pror_to_loop(mutation.get_location());
		CirIfStatement if_statement = (CirIfStatement) this.
				get_cir_node(mutation.get_location(), CirIfStatement.class);
		CirExecution if_execution = this.get_execution(if_statement);
		
		SecConstraint constraint; CirStatement target;
		constraint = this.get_constraint(if_statement.get_condition(), false);
		if(mutation.get_operator() == MutaOperator.while_to_do_while) {
			target = null;
			for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
				if(flow.get_type() == CirExecutionFlowType.true_flow) {
					target = flow.get_target().get_statement();
					break;
				}
			}
		}
		else {
			target = if_statement;
		}
		SecStateError init_error = SecFactory.set_statement(source, target);
		
		return this.add_infection(constraint, init_error);
	}
	
}
