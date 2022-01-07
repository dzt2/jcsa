package com.jcsa.jcmutest.mutant.sta2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
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

public class VTRPStateMutationParser extends StateMutationParser {
	
	/**
	 * @param location
	 * @param parameter
	 * @return the expression as the parameter to generate constraint
	 * @throws Exception
	 */
	private SymbolExpression get_parameter(AstNode location, Object parameter) throws Exception {
		if(parameter instanceof String) {
			String name = parameter.toString();
			while(location != null) {
				if(location instanceof AstScopeNode) {
					AstScopeNode scope_node = (AstScopeNode) location;
					CScope scope = scope_node.get_scope();
					if(scope.has_name(name)) {
						CName cname = scope.get_name(name);
						return SymbolFactory.identifier(cname);
					}
					else {
						throw new IllegalArgumentException("Undefined: " + name);
					}
				}
				else {
					location = location.get_parent();
				}
			}
			throw new IllegalArgumentException("Not in any scope.");
		}
		else {
			return SymbolFactory.sym_expression(parameter);
		}
	}
	
	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		/* determine the expression and declaration */
		CirExpression expression = this.get_cir_expression(mutation.get_location());
		SymbolExpression condition; CirExecution execution = this.get_r_execution();
		
		/* generate the state infection constraints */
		if(mutation.get_operator() == MutaOperator.trap_on_pos) {
			if(StateMutations.is_boolean(expression)) {
				condition = SymbolFactory.sym_condition(expression, true);
			}
			else {
				condition = SymbolFactory.greater_tn(expression, 0);
			}
		}
		else if(mutation.get_operator() == MutaOperator.trap_on_zro) {
			if(StateMutations.is_boolean(expression)) {
				condition = SymbolFactory.sym_condition(expression, false);
			}
			else {
				condition = SymbolFactory.equal_with(expression, 0);
			}
		}
		else if(mutation.get_operator() == MutaOperator.trap_on_neg) {
			if(StateMutations.is_boolean(expression)) {
				condition = SymbolFactory.sym_constant(Boolean.FALSE);
			}
			else {
				condition = SymbolFactory.smaller_tn(expression, 0);
			}
		}
		else if(mutation.get_operator() == MutaOperator.trap_on_dif) {
			SymbolExpression parameter = this.get_parameter(
					mutation.get_location(), mutation.get_parameter());
			SymbolExpression loperand, roperand;
			if(CirMutations.is_boolean(expression)) {
				loperand = SymbolFactory.sym_condition(expression, true);
				roperand = SymbolFactory.sym_condition(parameter, true);
			}
			else {
				loperand = SymbolFactory.sym_expression(expression);
				roperand = parameter;
			}
			condition = SymbolFactory.not_equals(loperand, roperand);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + mutation);
		}
		
		/* generate the constraint-error infection pairs */
		CirConditionState constraint = CirAbstractState.eva_cond(execution, condition, true);
		CirAbstErrorState init_error = CirAbstractState.set_trap(execution);
		this.put_infection_pair(constraint, init_error);
	}

}
