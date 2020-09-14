package com.jcsa.jcmutest.mutant.sec2mutant.muta.trap;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class CTRPInfectionParser extends SecInfectionParser {
	
	private AstSwitchStatement get_switch_statement(AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		while(location != null) {
			if(location instanceof AstSwitchStatement) {
				return (AstSwitchStatement) location;
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in switch-statement.");
	}
	
	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return (CirStatement) this.get_cir_node(
				this.get_switch_statement(mutation), 
				CirSaveAssignStatement.class);
	}
	
	private AstCaseStatement get_case_statement(AstMutation mutation) throws Exception {
		AstNode parameter = (AstNode) mutation.get_parameter();
		while(parameter != null) {
			if(parameter instanceof AstCaseStatement) {
				return (AstCaseStatement) parameter;
			}
			else {
				parameter = parameter.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in case-statement");
	}
	
	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		List<SecConstraint> constraints = new ArrayList<SecConstraint>();
		/* case_statement.condition == case_statement.condition */
		CirAssignStatement switch_statement = (CirAssignStatement) statement;
		AstCaseStatement case_statement = this.get_case_statement(mutation);
		SymExpression condition = this.sym_condition(COperator.equal_with, 
				switch_statement.get_rvalue(), case_statement.get_expression());
		constraints.add(this.get_constraint(condition, true));
		/* execute(case_statement.true_statement, 1) */
		CirCaseStatement target_statement = (CirCaseStatement) this.
				get_cir_node(case_statement, CirCaseStatement.class);
		CirExecution case_execution = this.get_execution(target_statement);
		CirExecution true_execution = null;
		for(CirExecutionFlow flow : case_execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				true_execution = flow.get_target();
				break;
			}
		}
		constraints.add(this.exe_constraint(true_execution.get_statement(), 1));
		/* add infection */
		SecConstraint constraint = this.conjunct(constraints);
		SecStateError init_error = this.trap_statement(statement);
		this.add_infection(constraint, init_error); return true;
	}

}
