package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.ctype.CEnumerator;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;
import com.jcsa.jcparse.lang.scope.CScope;

public class VRRPMutationParser extends CirMutationParser {
	
	/**
	 * @param location
	 * @param name
	 * @return the declaration of the name in scope of the location
	 * @throws Exception
	 */
	private CName find_cname(AstNode location, String name) throws Exception {
		while(location != null) {
			if(location instanceof AstScopeNode) {
				CScope scope = ((AstScopeNode) location).get_scope();
				if(scope.has_name(name)) {
					return scope.get_name(name);
				}
				else {
					return null;
				}
			}
			else {
				location = location.get_parent();
			}
		}
		return null;
	}
	
	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		CirExpression expression = this.get_use_point(tree, source.get_location());
		CName cname = this.find_cname(source.get_location(), source.get_parameter().toString());
		
		if(cname instanceof CInstanceName
			|| cname instanceof CParameterName) {
			String name = cname.get_name() + "#" + cname.get_scope().hashCode();
			targets.add(CirMutations.set_expression(expression, name));
		}
		else if(cname instanceof CEnumeratorName) {
			CEnumerator enumerator = ((CEnumeratorName) cname).get_enumerator();
			targets.add(CirMutations.set_expression(expression, enumerator.get_value()));
		}
		else {
			throw new IllegalArgumentException("Invalid name: " + source.get_parameter());
		}
	}

}
