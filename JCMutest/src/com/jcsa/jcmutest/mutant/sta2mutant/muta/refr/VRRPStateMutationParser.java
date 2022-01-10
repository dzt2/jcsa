package com.jcsa.jcmutest.mutant.sta2mutant.muta.refr;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VRRPStateMutationParser extends StateMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}
	
	private SymbolExpression get_muta_value(AstNode location, String parameter) throws Exception {
		while(location != null) {
			if(location instanceof AstScopeNode) {
				CScope scope = ((AstScopeNode) location).get_scope();
				if(scope.has_name(parameter)) {
					CName cname = scope.get_name(parameter);
					return SymbolFactory.identifier(cname);
				}
				else {
					throw new IllegalArgumentException("Undefined: " + parameter);
				}
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in scope definition.");
	}
	
	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		SymbolExpression mvalue = this.get_muta_value(
				mutation.get_location(), mutation.get_parameter().toString());
		SymbolExpression condition = SymbolFactory.not_equals(expression, mvalue);
		CirExecution execution = expression.execution_of();
		CirConditionState constraint = CirAbstractState.eva_cond(execution, condition, true);
		this.put_infection_pair(constraint, CirAbstractState.set_expr(expression, mvalue));
	}

}
