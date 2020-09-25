package com.jcsa.jcmutest.backups;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class TTRPStateMutationParser extends StateMutationParser {
	
	private CirIfStatement get_if_statement(AstMutation mutation) throws Exception {
		return (CirIfStatement) this.get_cir_node(mutation.get_location(), CirIfStatement.class);
	}
	
	private CirStatement get_true_branch(AstMutation mutation) throws Exception {
		CirIfStatement condition = this.get_if_statement(mutation);
		CirExecution execution = this.get_execution(condition);
		for(CirExecutionFlow flow : execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				return flow.get_target().get_statement();
			}
		}
		throw new IllegalArgumentException("No true branch found");
	}
	
	private int get_loop_times(AstMutation mutation) throws Exception {
		return ((Integer) mutation.get_parameter()).intValue();
	}
	
	@Override
	protected CirStatement find_beg_statement(AstMutation mutation) throws Exception {
		return this.get_true_branch(mutation);
	}

	@Override
	protected CirStatement find_end_statement(AstMutation mutation) throws Exception {
		return this.get_true_branch(mutation);
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		/* 0. declaration */
		List<SecConstraint> constraints = new ArrayList<SecConstraint>();
		CirIfStatement if_statement = this.get_if_statement(mutation);
		CirStatement true_statement = this.get_true_branch(mutation);
		int loop_times = this.get_loop_times(mutation);
		if(loop_times < 1) return false;
		
		/* 1. {if_statement.condition as true} */
		constraints.add(this.get_constraint(if_statement.get_condition(), true));
		/* 2. execute(true-statement, n) */
		constraints.add(this.exe_constraint(true_statement, loop_times));
		
		/* 3. add the infection pair to module */
		SecConstraint constraint = this.conjunct(constraints);
		SecStateError init_error = this.trap_statement(if_statement);
		this.add_infection(constraint, init_error);
		return true;
	}

}
