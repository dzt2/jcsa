package com.jcsa.jcmutest.mutant.ctx2mutant.muta.stmt;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.program.AstCirNode;

public class SBCRContextMutationParser extends ContextMutationParser {
	
	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}
	
	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		AstNode goto_statement = mutation.get_location();
		AstNode loop_statement = goto_statement.get_parent();
		AstCirNode original_next = null, mutation_next = null;
		while(loop_statement != null) {
			if(loop_statement instanceof AstForStatement) {
				if(mutation.get_operator() == MutaOperator.break_to_continue) {
					original_next = this.get_location(loop_statement);
					mutation_next = this.get_location(((AstForStatement) loop_statement).get_condition());
				}
				else {
					mutation_next = this.get_location(loop_statement);
					original_next = this.get_location(((AstForStatement) loop_statement).get_condition());
				}
				break;
			}
			else if(loop_statement instanceof AstWhileStatement) {
				if(mutation.get_operator() == MutaOperator.break_to_continue) {
					original_next = this.get_location(loop_statement);
					mutation_next = this.get_location(((AstWhileStatement) loop_statement).get_condition());
				}
				else {
					mutation_next = this.get_location(loop_statement);
					original_next = this.get_location(((AstWhileStatement) loop_statement).get_condition());
				}
				break;
			}
			else if(loop_statement instanceof AstDoWhileStatement) {
				if(mutation.get_operator() == MutaOperator.break_to_continue) {
					original_next = this.get_location(loop_statement);
					mutation_next = this.get_location(((AstDoWhileStatement) loop_statement).get_condition());
				}
				else {
					mutation_next = this.get_location(loop_statement);
					original_next = this.get_location(((AstDoWhileStatement) loop_statement).get_condition());
				}
				break;
			}
			else {
				loop_statement = loop_statement.get_parent();
			}
		}
		this.put_infection(this.cov_time(1, Integer.MAX_VALUE), this.set_flow(original_next, mutation_next));
	}
	
}
