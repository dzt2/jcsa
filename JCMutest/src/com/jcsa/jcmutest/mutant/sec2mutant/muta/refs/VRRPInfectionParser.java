package com.jcsa.jcmutest.mutant.sec2mutant.muta.refs;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.ctype.CEnumerator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class VRRPInfectionParser extends SecInfectionParser {
	
	private SymExpression get_muta_expression(AstMutation mutation) throws Exception {
		Object parameter = mutation.get_parameter();
		AstNode location = mutation.get_location();
		if(parameter instanceof String) {
			while(location != null) {
				if(location instanceof AstScopeNode) {
					String name = parameter.toString();
					CScope scope = ((AstScopeNode) location).get_scope();
					if(scope.has_name(name)) {
						CName cname = scope.get_name(name);
						if(cname instanceof CInstanceName) {
							CInstance instance = ((CInstanceName) cname).get_instance();
							return SymFactory.new_identifier(instance.get_type(), cname.get_name() + "#" + scope.hashCode());
						}
						else if(cname instanceof CParameterName) {
							CInstance instance = ((CParameterName) cname).get_parameter();
							return SymFactory.new_identifier(instance.get_type(), cname.get_name() + "#" + scope.hashCode());
						}
						else if(cname instanceof CEnumeratorName) {
							CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
							return SymFactory.new_constant(Integer.valueOf(enumerator.get_value()));
						}
						else {
							throw new UnsupportedOperationException("Undefined: " + name);
						}
					}
					else {
						throw new UnsupportedOperationException("Undefined: " + name);
					}
				}
				else {
					location = location.get_parent();
				}
			}
			throw new UnsupportedOperationException("Not in definition block");
		}
		else {
			return SymFactory.parse(parameter);
		}
	}
	
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_cir_expression(mutation.get_location()).statement_of();
	}

	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		SecConstraint constraint; SecDescription init_error;
		CirExpression orig_expression = this.get_cir_expression(mutation.get_location());
		SymExpression muta_expression = this.get_muta_expression(mutation);
		
		constraint = this.get_constraint(this.sym_condition(COperator.
				not_equals, orig_expression, muta_expression), true);
		init_error = this.set_expression(orig_expression, muta_expression);
		this.add_infection(constraint, init_error); return true;
	}

}
