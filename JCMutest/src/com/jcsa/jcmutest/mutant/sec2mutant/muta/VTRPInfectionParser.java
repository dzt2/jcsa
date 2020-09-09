package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import com.jcsa.jcmutest.mutant.sec2mutant.apis.SecInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
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

public class VTRPInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_location() throws Exception {
		return this.cir_tree.get_localizer().
				end_statement(mutation.get_location());
	}
	
	private SymExpression get_muta_expression(AstNode location,
			Object parameter) throws Exception {
		if(parameter instanceof String) {
			while(location != null) {
				if(location instanceof AstScopeNode) {
					String name = location.toString();
					CScope scope = ((AstScopeNode) location).get_scope();
					if(scope.has_name(name)) {
						CName cname = scope.get_name(name);
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
							throw new IllegalArgumentException("Undefined: " + name);
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
			throw new IllegalArgumentException("Undefined: " + parameter);
		}
		else {
			return SymFactory.parse(parameter);
		}
	}
	
	@Override
	protected void generate_infections() throws Exception {
		CirExpression orig_expression = this.
				cir_tree.get_localizer().get_cir_value(mutation.get_location());
		SecConstraint constraint; SymExpression condition, muta_expression;
		/*SymExpression muta_expression = this.get_muta_expression(
				this.mutation.get_location(), this.mutation.get_parameter());*/
		
		switch(this.mutation.get_operator()) {
		case trap_on_pos:
		{
			condition = SymFactory.greater_tn(orig_expression, Integer.valueOf(0));
			break;
		}
		case trap_on_neg:
		{
			condition = SymFactory.smaller_tn(orig_expression, Integer.valueOf(0));
			break;
		}
		case trap_on_zro:
		{
			condition = SymFactory.equal_with(orig_expression, Integer.valueOf(0));
			break;
		}
		case trap_on_dif:
		{
			muta_expression = this.get_muta_expression(
					this.mutation.get_location(), this.mutation.get_parameter());
			condition = SymFactory.not_equals(orig_expression, muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(this.mutation.toString());
		}
		
		constraint = SecFactory.assert_constraint(statement, condition, true);
		SecDescription init_error = SecFactory.trap_statement(statement);
		this.add_infection(constraint, init_error);
	}

}
