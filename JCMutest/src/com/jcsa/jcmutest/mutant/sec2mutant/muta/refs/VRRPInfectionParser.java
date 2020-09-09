package com.jcsa.jcmutest.mutant.sec2mutant.muta.refs;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.ctype.CEnumerator;
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

public class VRRPInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_cir_value(this.location).statement_of();
	}
	
	private SymExpression get_muta_expression(AstNode location, String name) throws Exception {
		while(location != null) {
			if(location instanceof AstScopeNode) {
				CScope scope = ((AstScopeNode) location).get_scope();
				if(scope.has_name(name)) {
					CName cname = scope.get_name(name);
					name = cname.get_name() + "#" + cname.get_scope().hashCode();
					if(cname instanceof CInstanceName) {
						CInstance instance = ((CInstanceName) cname).get_instance();
						return SymFactory.new_identifier(instance.get_type(), name);
					}
					else if(cname instanceof CParameterName) {
						CInstance instance = ((CParameterName) cname).get_parameter();
						return SymFactory.new_identifier(instance.get_type(), name);
					}
					else if(cname instanceof CEnumeratorName) {
						CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
						return SymFactory.new_constant(Integer.valueOf(enumerator.get_value()));
					}
					else {
						throw new IllegalArgumentException("Invalid: " + name);
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
		throw new IllegalArgumentException("Not in definition scope");
	}
	
	@Override
	protected void generate_infections() throws Exception {
		CirExpression orig_expression = this.get_cir_value(this.location);
		SymExpression muta_expression = this.get_muta_expression(
				this.location, this.mutation.get_parameter().toString());
		SecConstraint constraint = SecFactory.assert_constraint(this.statement, 
				SymFactory.not_equals(orig_expression, muta_expression), true);
		SecDescription init_error = SecFactory.
				set_expression(this.statement, orig_expression, muta_expression);
		this.add_infection(constraint, init_error);
	}

}
