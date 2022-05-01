package com.jcsa.jcmutest.mutant.ctx2mutant.muta.stmt;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextStates;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class SWDRContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		AstNode loop_statement = mutation.get_location(), body, condition;
		if(loop_statement instanceof AstWhileStatement) {
			body = ((AstWhileStatement) loop_statement).get_body();
			condition = ((AstWhileStatement) loop_statement).get_condition();
			this.put_infection(this.mus_cond(SymbolFactory.sym_condition(condition, false)), 
							AstContextStates.set_stmt(this.find_ast_location(body), true));
		}
		else {
			body = ((AstDoWhileStatement) loop_statement).get_body();
			condition = ((AstDoWhileStatement) loop_statement).get_condition();
			this.put_infection(this.mus_cond(SymbolFactory.sym_condition(condition, false)), 
							AstContextStates.set_stmt(this.find_ast_location(body), false));
		}
	}

}
