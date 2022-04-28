package com.jcsa.jcmutest.mutant.cir2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class CTRPCirMutationParser extends CirMutationParser {
	
	/**
	 * @param location
	 * @return the switch-statement where the location is seeded
	 * @throws Exception
	 */
	private AstSwitchStatement get_switch_statement(AstNode location) throws Exception {
		while(location != null) {
			if(location instanceof AstSwitchStatement) {
				return (AstSwitchStatement) location;
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in switch-statement");
	}

	/**
	 * @param location
	 * @return the case-statement where the location is seeded
	 * @throws Exception
	 */
	private AstCaseStatement get_case_statement(AstNode location) throws Exception {
		while(location != null) {
			if(location instanceof AstCaseStatement) {
				return (AstCaseStatement) location;
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in case-statement");
	}
	
	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		AstNode statement = this.get_switch_statement(mutation.get_location());
		return (CirStatement) this.get_cir_node(statement, CirSaveAssignStatement.class);
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		/* determine the condition and location being evaluated */
		AstSwitchStatement switch_statement = this.get_switch_statement(mutation.get_location());
		AstCaseStatement case_statement = this.get_case_statement((AstNode) mutation.get_parameter());
		CirAssignStatement c_head = (CirAssignStatement) this.get_cir_node(switch_statement, CirSaveAssignStatement.class);
		CirCaseStatement c_case = (CirCaseStatement) this.get_cir_node(case_statement, CirCaseStatement.class);
		
		/* generate the constraint and initial error infections */
		CirExecution execution = c_head.execution_of();
		CirConditionState constraint = CirAbstractState.eva_need(execution, c_case.get_condition());
		CirAbstErrorState init_error = CirAbstractState.trp_stmt(execution);
		this.put_infection_pair(constraint, init_error);
	}

}
