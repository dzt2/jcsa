package com.jcsa.jcmutest.mutant.cir2mutant.muta.refs;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VRRPCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
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
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		SymbolExpression muta_value = this.get_muta_value(mutation.get_location(), mutation.get_parameter().toString());

		SymbolExpression condition = SymbolFactory.not_equals(expression, muta_value);
		CirAttribute constraint =
				CirAttribute.new_constraint(statement.execution_of(), condition, true);

		CirAttribute state_error;
		if(expression instanceof CirReferExpression && !(muta_value instanceof SymbolConstant)) {
			state_error = CirAttribute.new_refer_error(expression, muta_value);
		}
		else {
			state_error = CirAttribute.new_value_error(expression, muta_value);
		}
		infections.put(state_error, constraint);
	}

}
