package com.jcsa.jcmutest.mutant.ctx2mutant.muta.stmt;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextState;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class SWDRContextMutationParser extends ContextMutationParser {

	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}

	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		AstStatement loop_statement = (AstStatement) mutation.get_location();
		SymbolExpression condition; AstCirNode loop_body; boolean muta_exec;
		if(loop_statement instanceof AstDoWhileStatement) {
			condition = SymbolFactory.sym_condition(((AstDoWhileStatement) loop_statement).get_condition(), false);
			loop_body = this.get_location(((AstDoWhileStatement) loop_statement).get_body()); muta_exec = false;
		}
		else {
			condition = SymbolFactory.sym_condition(((AstWhileStatement) loop_statement).get_condition(), false);
			loop_body = this.get_location(((AstWhileStatement) loop_statement).get_body()); muta_exec = true;
		}
		this.put_infection(this.mus_cond(condition), AstContextState.mut_stmt(loop_body, muta_exec));
	}

}
