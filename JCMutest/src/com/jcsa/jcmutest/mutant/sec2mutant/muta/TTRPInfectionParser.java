package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sec2mutant.apis.SecInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class TTRPInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_location() throws Exception {
		AstNode location = this.mutation.get_location();
		while(location != null) {
			if(location instanceof AstDoWhileStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstForStatement) {
				return (CirStatement) cir_tree.get_cir_nodes(
							location, CirIfStatement.class);
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException(this.mutation.toString());
	}

	@Override
	protected void generate_infections() throws Exception {
		/* getters */
		CirIfStatement if_statement = (CirIfStatement) statement;
		CirExpression condition = if_statement.get_condition();
		int times = ((Integer) mutation.get_parameter()).intValue();
		
		/* find the statements in true branch */
		CirExecution if_execution = if_statement.get_tree().
				get_localizer().get_execution(if_statement);
		CirExecution true_branch = null;
		for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				true_branch = flow.get_target(); break;
			}
		}
		CirStatement true_statement = true_branch.get_statement();
		
		/* condition == true and execute(true_statement, times) ==> trap() */
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		constraints.add(SecFactory.assert_constraint(if_statement, condition, true));
		constraints.add(SecFactory.execute_constraint(true_statement, times));
		SecDescription init_error = SecFactory.trap_statement(true_statement);
		add_infection(SecFactory.conjunct(if_statement, constraints), init_error);
	}

}
