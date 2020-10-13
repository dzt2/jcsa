package com.jcsa.jcmutest.mutant.cir2mutant.muta.trap;

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
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

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
	private SymExpression get_parameter(AstNode location, Object parameter) throws Exception {
		if(parameter instanceof String) {
			String name = parameter.toString();
			while(location != null) {
				if(location instanceof AstScopeNode) {
					AstScopeNode scope_node = (AstScopeNode) location;
					CScope scope = scope_node.get_scope();
					if(scope.has_name(name)) {
						CName cname = scope.get_name(name);
						if(cname instanceof CInstanceName) {
							CInstance instance = ((CInstanceName) cname).get_instance();
							String identifier = cname.get_name() + "#" + scope.hashCode();
							return SymFactory.new_identifier(instance.get_type(), identifier);
						}
						else if(cname instanceof CParameterName) {
							CInstance instance = ((CParameterName) cname).get_parameter();
							String identifier = cname.get_name() + "#" + scope.hashCode();
							return SymFactory.new_identifier(instance.get_type(), identifier);
						}
						else if(cname instanceof CEnumeratorName) {
							CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
							return SymFactory.parse(Integer.valueOf(enumerator.get_value()));
						}
						else {
							throw new IllegalArgumentException(cname.getClass().getSimpleName());
						}
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
			return SymFactory.parse(parameter);
		}
	}
	
	@Override
	protected void generate_infections(CirMutations mutations, CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<CirStateError, CirConstraint> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		
		SymExpression condition;
		switch(mutation.get_operator()) {
		case trap_on_pos:	condition = SymFactory.greater_tn(expression, Integer.valueOf(0)); break;
		case trap_on_zro:	condition = SymFactory.equal_with(expression, Integer.valueOf(0)); break;
		case trap_on_neg:	condition = SymFactory.smaller_tn(expression, Integer.valueOf(0)); break;
		case trap_on_dif: 	condition = SymFactory.not_equals(expression, get_parameter(
										mutation.get_location(), mutation.get_parameter())); 	break;
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		
		CirConstraint constraint = mutations.expression_constraint(statement, condition, true);
		CirStateError state_error = mutations.trap_error(statement);
		infections.put(state_error, constraint);
	}

}
