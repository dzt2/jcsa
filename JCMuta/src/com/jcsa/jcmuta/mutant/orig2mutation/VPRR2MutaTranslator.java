package com.jcsa.jcmuta.mutant.orig2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CScope;

import __backup__.TextMutation;

public class VPRR2MutaTranslator implements Text2MutaTranslator {
	
	private CName find_name(AstNode location, String name) throws Exception {
		while(location != null) {
			if(location instanceof AstScopeNode) {
				CScope scope = ((AstScopeNode) location).get_scope();
				return scope.get_name(name);
			}
			else location = location.get_parent();
		}
		throw new IllegalArgumentException("Unable to find " + name);
	}
	
	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		expression = CTypeAnalyzer.get_expression_of(expression);
		String name = mutation.get_replace();
		CName cname = this.find_name(expression, name);
		return AstMutation.VRRP(expression, cname);
	}

}
