package com.jcsa.jcmutest.mutant.cir2mutant.muta.trap;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * [x > 0 || x = 0 || x < 0] --> trap(statement)
 * @author dzt2
 *
 */
public class VTRPCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_end_statement(cir_tree, mutation.get_location());
	}
	
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
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymCondition, SymCondition> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		
		SymbolExpression condition; 
		switch(mutation.get_operator()) {
		case trap_on_pos:	
		{
			if(CirMutations.is_boolean(expression)) {
				condition = SymbolFactory.sym_condition(expression, true);
			}
			else {
				condition = SymbolFactory.greater_tn(expression, Integer.valueOf(0));
			}
			break;
		}
		case trap_on_zro:	
		{
			if(CirMutations.is_boolean(expression)) {
				condition = SymbolFactory.sym_condition(expression, false);
			}
			else {
				condition = SymbolFactory.equal_with(expression, Integer.valueOf(0));
			}
			break;
		}
		case trap_on_neg:	
		{
			if(CirMutations.is_boolean(expression)) {
				condition = SymbolFactory.sym_expression(Boolean.FALSE);
			}
			else {
				condition = SymbolFactory.smaller_tn(expression, Integer.valueOf(0));
			}
			break;
		}
		case trap_on_dif: 	
		{
			condition = SymbolFactory.not_equals(expression, get_parameter(
						mutation.get_location(), mutation.get_parameter())); 	
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		
		CirExecution execution = CirMutations.execution_of(statement);
		SymCondition constraint = CirMutations.eva_expr(execution, condition);
		SymCondition init_error = CirMutations.trp_stmt(execution);
		infections.put(init_error, constraint);
	}
	
}
