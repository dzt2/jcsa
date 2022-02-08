package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class UIODCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		/* determine the incremental statement */
		CirAssignStatement inc_statement = (CirAssignStatement) this.
				get_cir_node(mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression ovalue = (CirComputeExpression) inc_statement.get_rvalue();
		CirExpression mvalue = ovalue.get_operand(0);
		CirExecution execution = inc_statement.execution_of();
		
		/* generate constraint-error infection */
		CirConditionState constraint = CirAbstractState.cov_time(execution, 1);
		CirAbstErrorState init_error = CirAbstractState.set_expr(ovalue, mvalue);
		this.put_infection_pair(constraint, init_error);
	}

}
