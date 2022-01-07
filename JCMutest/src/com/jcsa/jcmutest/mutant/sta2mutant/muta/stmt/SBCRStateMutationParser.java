package com.jcsa.jcmutest.mutant.sta2mutant.muta.stmt;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SBCRStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_beg_statement(mutation.get_location());
	}
	
	/**
	 * @param location
	 * @return the loop statement in which the break is transformed to continue;
	 * @throws Exception
	 */
	private AstStatement find_loop_statement(AstNode location) throws Exception {
		while(location != null) {
			if(location instanceof AstDoWhileStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstForStatement) {
				return (AstStatement) location;
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in loop-structure");
	}
	
	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		/* determine the source location and the next stop */
		CirExecution source = this.get_r_execution();
		CirExecutionFlow orig_flow = source.get_ou_flow(0);
		AstStatement loop_statement = this.find_loop_statement(mutation.get_location());
		
		/* determine the transformed next statement in loop */
		CirStatement next_statement;
		if(mutation.get_operator() == MutaOperator.break_to_continue) {
			next_statement = (CirStatement) this.get_cir_node(loop_statement, CirIfStatement.class);
		}
		else if(mutation.get_operator() == MutaOperator.continue_to_break) {
			next_statement = (CirStatement) this.get_cir_node(loop_statement, CirIfEndStatement.class);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + mutation.toString());
		}
		CirExecution target = next_statement.execution_of();
		
		/* generate the constraint-error infection pair */
		if(target != orig_flow.get_target()) {
			CirExecutionFlow muta_flow = CirExecutionFlow.virtual_flow(source, target);
			CirConditionState constriant = CirAbstractState.cov_time(source, 1);
			CirAbstErrorState init_error = CirAbstractState.set_flow(orig_flow, muta_flow);
			this.put_infection_pair(constriant, init_error);
		}
	}

}
