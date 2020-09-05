package com.jcsa.jcmutest.selang.muta;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
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

public class VRRPInfectParser extends SedInfectParser {
	
	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return cir_tree.get_localizer().beg_statement(mutation.get_location());
	}
	
	private CName find_cname(AstNode location, String name) throws Exception {
		while(location != null) {
			if(location instanceof AstScopeNode) {
				CScope scope = ((AstScopeNode) location).get_scope();
				if(scope.has_name(name)) {
					return scope.get_name(name);
				}
				else {
					throw new IllegalArgumentException("Undefined: " + name);
				}
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Undefined: " + name);
	}
	
	private SedExpression get_cname_expression(CName cname) throws Exception {
		if(cname instanceof CInstanceName) {
			CInstance instance = ((CInstanceName) cname).get_instance();
			String name = cname.get_name() + "#" + cname.get_scope().hashCode();
			return SedFactory.id_expression(instance.get_type(), name);
		}
		else if(cname instanceof CParameterName) {
			CInstance instance = ((CParameterName) cname).get_parameter();
			String name = cname.get_name() + "#" + cname.get_scope().hashCode();
			return SedFactory.id_expression(instance.get_type(), name);
		}
		else if(cname instanceof CEnumeratorName) {
			CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
			return (SedExpression) 
					SedFactory.fetch(Integer.valueOf(enumerator.get_value()));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + cname);
		}
	}
	
	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, 
			AstMutation mutation, SedInfection infection) throws Exception {
		CirExpression expression = 
				cir_tree.get_localizer().get_cir_value(mutation.get_location());
		SedExpression parameter = this.get_cname_expression(this.find_cname(
				mutation.get_location(), mutation.get_parameter().toString()));
		
		SedDescription constraint, init_error;
		if(expression != null) {
			constraint = SedFactory.condition_constraint(statement, 
					SedFactory.not_equals(expression, parameter), true);
			init_error = SedFactory.mut_expression(statement, expression, parameter);
			infection.add_infection_pair(constraint, init_error);
		}
	}
	
}
