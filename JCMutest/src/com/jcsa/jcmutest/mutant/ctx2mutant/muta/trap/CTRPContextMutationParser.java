package com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CTRPContextMutationParser extends ContextMutationParser {
	
	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		AstNode source = mutation.get_location();
		while(source != null) {
			if(source instanceof AstSwitchStatement) {
				return this.find_ast_location(source);
			}
			else {
				source = source.get_parent();
			}
		}
		return null;
	}
	
	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		AstExpression swit_condition = (AstExpression) mutation.get_location();
		AstExpression case_condition = (AstExpression) mutation.get_parameter();
		SymbolExpression condition = 
						SymbolFactory.equal_with(swit_condition, case_condition);
		this.put_infection(this.eva_cond(condition), this.trp_stmt());
	}
	
}
