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
	
	/**
	 * @param source
	 * @return the body in the loop-statement
	 * @throws Exception
	 */
	private	AstStatement find_loop_body(AstNode source) throws Exception {
		if(source instanceof AstWhileStatement) {
			return ((AstWhileStatement) source).get_body();
		}
		else if(source instanceof AstDoWhileStatement) {
			return ((AstDoWhileStatement) source).get_body();
		}
		else if(source instanceof AstForStatement) {
			return ((AstForStatement) source).get_body();
		}
		else {
			throw new IllegalArgumentException("Invalid source: null");
		}
	}

	@Override
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(this.find_loop_body(mutation.get_location()));
	}

	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		int times = ((Integer) mutation.get_parameter()).intValue();
		this.put_infection(this.cov_time(times), this.trp_stmt());
	}

}
