package com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.program.AstCirNode;

public class TTRPContextMutationParser extends ContextMutationParser {
	
	private	AstStatement	find_loop_body(AstMutation mutation) throws Exception {
		AstNode statement = mutation.get_location();
		if(statement instanceof AstWhileStatement) {
			statement = ((AstWhileStatement) statement).get_body();
		}
		else if(statement instanceof AstDoWhileStatement) {
			statement = ((AstDoWhileStatement) statement).get_body();
		}
		else {
			statement = ((AstForStatement) statement).get_body();
		}
		return (AstStatement) statement;
	}
	
	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(this.find_loop_body(mutation));
	}

	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		int times = ((Integer) mutation.get_parameter()).intValue();
		this.put_infection(this.cov_time(times, Integer.MAX_VALUE), this.trp_stmt());
	}

}
