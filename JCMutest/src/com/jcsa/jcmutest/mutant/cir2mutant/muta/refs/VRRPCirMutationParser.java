package com.jcsa.jcmutest.mutant.cir2mutant.muta.refs;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.ctype.CEnumerator;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class VRRPCirMutationParser extends CirMutationParser {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}
	
	private SymExpression get_muta_value(AstNode location, String parameter) throws Exception {
		while(location != null) {
			if(location instanceof AstScopeNode) {
				CScope scope = ((AstScopeNode) location).get_scope();
				if(scope.has_name(parameter)) {
					CName cname = scope.get_name(parameter);
					if(cname instanceof CInstanceName) {
						CInstance instance = ((CInstanceName) cname).get_instance();
						String name = cname.get_name() + "#" + cname.get_scope().hashCode();
						return SymFactory.identifier(instance.get_type(), name);
					}
					else if(cname instanceof CParameterName) {
						CInstance instance = ((CParameterName) cname).get_parameter();
						String name = cname.get_name() + "#" + cname.get_scope().hashCode();
						return SymFactory.identifier(instance.get_type(), name);
					}
					else if(cname instanceof CEnumeratorName) {
						CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
						return SymFactory.sym_expression(Integer.valueOf(enumerator.get_value()));
					}
					else {
						throw new IllegalArgumentException("Invalid name: " + parameter);
					}
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
	protected void generate_infections(CirMutations mutations, CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<CirStateError, CirConstraint> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		SymExpression muta_value = this.get_muta_value(mutation.get_location(), mutation.get_parameter().toString());
		
		SymExpression condition = SymFactory.not_equals(expression, muta_value);
		CirConstraint constraint = mutations.expression_constraint(statement, condition, true);
		
		CirStateError state_error;
		if(expression instanceof CirReferExpression && !(muta_value instanceof SymConstant)) {
			state_error = mutations.refer_error((CirReferExpression) expression, muta_value);
		}
		else {
			state_error = mutations.expr_error(expression, muta_value);
		}
		infections.put(state_error, constraint);
	}
	
}
