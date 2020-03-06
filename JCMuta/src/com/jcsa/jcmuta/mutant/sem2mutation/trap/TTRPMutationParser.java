package com.jcsa.jcmuta.mutant.sem2mutation.trap;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class TTRPMutationParser extends SemanticMutationParser {
	
	/**
	 * get the loop statement being mutated
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private AstStatement get_loop_statement(AstMutation ast_mutation) throws Exception {
		AstNode location = ast_mutation.get_location();
		while(location != null) {
			if(location instanceof AstForStatement
				|| location instanceof AstWhileStatement
				|| location instanceof AstDoWhileStatement) {
				return (AstStatement) location;
			}
			else { location = location.get_parent(); }
		}
		throw new IllegalArgumentException("Unable to locate");
	}
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		AstStatement loop_statement = this.get_loop_statement(ast_mutation);
		return (CirStatement) this.get_cir_node(loop_statement, CirIfStatement.class);
	}

	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		AstStatement loop_statement = this.get_loop_statement(ast_mutation);
		CirIfStatement statement = (CirIfStatement) 
				this.get_cir_node(loop_statement, CirIfStatement.class);
		int times = (int) ast_mutation.get_parameter();
		
		SemanticAssertion constraint = sem_mutation.
				get_assertions().cover_for(statement.get_condition(), times);
		SemanticAssertion state_error = sem_mutation.get_assertions().trapping();
		this.infect(constraint, state_error);
	}

}
