package com.jcsa.jcmutest.mutant.sym2mutant.muta.trap;

import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymInstanceUtils;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

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
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		CType data_type = expression.get_data_type();
		if(data_type == null) data_type = CBasicTypeImpl.void_type;
		else data_type = CTypeAnalyzer.get_value_type(data_type);
		
		SymbolExpression condition; 
		switch(mutation.get_operator()) {
		case trap_on_pos:	
		{
			if(CTypeAnalyzer.is_boolean(data_type)) {
				condition = SymbolFactory.sym_condition(expression, true);
			}
			else {
				condition = SymbolFactory.greater_tn(expression, Integer.valueOf(0));
			}
			break;
		}
		case trap_on_zro:	
		{
			if(CTypeAnalyzer.is_boolean(data_type)) {
				condition = SymbolFactory.sym_condition(expression, false);
			}
			else {
				condition = SymbolFactory.equal_with(expression, Integer.valueOf(0));
			}
			break;
		}
		case trap_on_neg:	
		{
			if(CTypeAnalyzer.is_boolean(data_type)) {
				condition = SymbolFactory.sym_expression(Boolean.FALSE);
			}
			else {
				condition = SymbolFactory.smaller_tn(expression, Integer.valueOf(0));
			}
			break;
		}
		case trap_on_dif: 	condition = SymbolFactory.not_equals(expression, get_parameter(
										mutation.get_location(), mutation.get_parameter())); 	break;
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		
		SymConstraint constraint = SymInstanceUtils.expr_constraint(statement, condition, true);
		SymStateError state_error = SymInstanceUtils.trap_error(statement);
		infections.put(state_error, constraint);
	}

}
