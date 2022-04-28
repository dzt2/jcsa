package com.jcsa.jcmutest.mutant.ctx2mutant.muta.refr;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class VRRPContextMutationParser extends ContextMutationParser {
	
	private SymbolExpression get_muta_value(AstNode location, String parameter) throws Exception {
		while(location != null) {
			if(location instanceof AstScopeNode) {
				CScope scope = ((AstScopeNode) location).get_scope();
				if(scope.has_name(parameter)) {
					CName cname = scope.get_name(parameter);
					return SymbolFactory.sym_expression(cname);
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
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}

	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		SymbolExpression orig_value = SymbolFactory.sym_expression(mutation.get_location());
		SymbolExpression muta_value = this.get_muta_value(mutation.get_location(), mutation.get_parameter().toString());
		this.put_infection(this.
				eva_cond(SymbolFactory.not_equals(orig_value, muta_value), false), this.set_expr(orig_value, muta_value));
	}

}
