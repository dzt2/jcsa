package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.refs;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadParser;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.SadInfection;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;
import com.jcsa.jcparse.lang.scope.CScope;

public class VRRPSadInfection extends SadInfection {
	
	private SadExpression get_parameter(AstNode location, String name) throws Exception {
		while(location != null) {
			if(location instanceof AstScopeNode) {
				CScope scope = ((AstScopeNode) location).get_scope();
				if(scope.has_name(name)) {
					CName cname = scope.get_name(name);
					if(cname instanceof CInstanceName) {
						String identifier = cname.get_name() + "#" + cname.get_scope().hashCode();
						CType data_type = ((CInstanceName) cname).get_instance().get_type();
						return SadFactory.id_expression(data_type, identifier);
					}
					else if(cname instanceof CParameterName) {
						String identifier = cname.get_name() + "#" + cname.get_scope().hashCode();
						CType data_type = ((CParameterName) cname).get_parameter().get_type();
						return SadFactory.id_expression(data_type, identifier);
					}
					else if(cname instanceof CEnumeratorName) {
						int value = ((CEnumeratorName) cname).get_enumerator().get_value();
						return SadFactory.constant(value);
					}
					else {
						throw new IllegalArgumentException("Unsupport: " + cname);
					}
				}
			}
			else {
				location = location.get_parent();
			}
		}
		return null;
	}
	
	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		AstExpression location = (AstExpression) mutation.get_location();
		CirExpression expression = this.find_result(tree, location);
		CirStatement statement = expression.statement_of();
		SadExpression parameter = this.get_parameter(
						location, mutation.get_parameter().toString());
		if(statement != null) {
			if(parameter != null) {
				SadExpression condition = SadFactory.not_equals(
						CBasicTypeImpl.bool_type, (SadExpression)
						SadParser.cir_parse(expression), parameter);
				SadAssertion constraint = SadFactory.assert_condition(statement, condition);
				SadAssertion state_error = SadFactory.set_expression(
						statement, expression, (SadExpression) parameter.clone());
				this.connect(reach_node, state_error, constraint);
			}
			else {
				throw new IllegalArgumentException("Undefined name: " + parameter);
			}
		}
	}

}
