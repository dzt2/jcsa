package com.jcsa.jcmutest.mutant.cir2mutant.muta.stmt;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class STDLCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_beg_statement(mutation.get_location());
	}
	
	/**
	 * @param statement
	 * @param location
	 * @return whether the statement in the range of the location
	 * @throws Exception
	 */
	private boolean in_location(CirStatement statement, AstNode location) throws Exception {
		if(statement.get_ast_source() != null) {
			AstNode ast_source = statement.get_ast_source();
			while(ast_source != null) {
				if(ast_source == location)
					return true;
				else
					ast_source = ast_source.get_parent();
			}
			return false;
		}
		else {
			return false;
		}
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		CirStatement beg_statement = this.get_beg_statement(mutation.get_location());
		CirStatement end_statement = this.get_end_statement(mutation.get_location());
		CirExecution beg_execution = beg_statement.execution_of();
		CirExecution end_execution = end_statement.execution_of();
		
		if(this.in_location(beg_statement, mutation.get_location())) {
			beg_execution = beg_execution.get_in_flow(0).get_source();
		}
		if(this.in_location(end_statement, mutation.get_location())) {
			end_execution = end_execution.get_ou_flow(0).get_target();
		}
		CirExecutionFlow orig_flow = beg_execution.get_ou_flow(0);
		CirExecutionFlow muta_flow = CirExecutionFlow.virtual_flow(beg_execution, end_execution);
		
		CirConditionState constraint = CirAbstractState.cov_time(beg_execution, 1);
		CirAbstErrorState init_error = CirAbstractState.mut_flow(orig_flow, muta_flow);
		this.put_infection_pair(constraint, init_error);
	}

}
