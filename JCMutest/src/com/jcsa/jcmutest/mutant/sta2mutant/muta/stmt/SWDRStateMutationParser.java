package com.jcsa.jcmutest.mutant.sta2mutant.muta.stmt;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParser;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SWDRStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_beg_statement(mutation.get_location());
	}
	
	private void while_to_do_while(AstMutation mutation) throws Exception {
		/* constraint is not execute while body */
		AstWhileStatement location = (AstWhileStatement) mutation.get_location();
		CirIfStatement if_statement = 
				(CirIfStatement) this.get_cir_node(location, CirIfStatement.class);
		CirExecution if_execution = if_statement.execution_of(), tbranch = null;
		for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				tbranch = flow.get_target();
				break;
			}
		}
		CirConditionState constraint = CirAbstractState.lim_time(tbranch, 0);
		
		/* state error is to execute while body */
		CirExpression condition = if_statement.get_condition();
		CirAbstErrorState init_error = CirAbstractState.set_expr(condition, true);
		this.put_infection_pair(constraint, init_error);
	}
	
	private void do_while_to_while(AstMutation mutation) throws Exception {
		/* constraint is to evaluate false-condition */
		AstDoWhileStatement location = (AstDoWhileStatement) mutation.get_location();
		CirIfStatement if_statement = 
				(CirIfStatement) this.get_cir_node(location, CirIfStatement.class);
		CirExpression condition = if_statement.get_condition();
		CirConditionState constraint = CirAbstractState.
				eva_cond(this.get_r_execution(), condition, false);
		
		/* state error is to NOT execute the body */
		CirStatement beg_statement = this.get_beg_statement(location);
		CirStatement end_statement = this.get_end_statement(location);
		CirExecution beg_execution = beg_statement.execution_of();
		CirExecution end_execution = end_statement.execution_of();
		CirExecutionFlow orig_flow = beg_execution.get_in_flow(0);
		CirExecutionFlow muta_flow = CirExecutionFlow.
					virtual_flow(orig_flow.get_source(), end_execution);
		CirAbstErrorState init_error = CirAbstractState.set_flow(orig_flow, muta_flow);
		this.put_infection_pair(constraint, init_error);
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		if(mutation.get_operator() == MutaOperator.while_to_do_while) {
			this.while_to_do_while(mutation);
		}
		else if(mutation.get_operator() == MutaOperator.do_while_to_while) {
			this.do_while_to_while(mutation);
		}
		else {
			throw new IllegalArgumentException(mutation.toString());
		}
	}

}
