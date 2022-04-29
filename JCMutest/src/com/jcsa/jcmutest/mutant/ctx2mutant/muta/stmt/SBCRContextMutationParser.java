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
	protected AstCirNode localize(AstMutation mutation) throws Exception {
		return this.find_ast_location(mutation.get_location());
	}
	
	/**
	 * It localizes to the end of loop statement
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	AstCirNode	loc_end_statement(AstNode source) throws Exception {
		while(source != null) {
			if(source instanceof AstForStatement || 
					source instanceof AstWhileStatement || 
					source instanceof AstDoWhileStatement) {
				return this.find_ast_location(source);
			}
			else {
				source = source.get_parent();
			}
		}
		throw new IllegalArgumentException("Unable to localize...");
	}
	
	/**
	 * @param source
	 * @return the condition where the source is localized (continue)
	 * @throws Exception
	 */
	private	AstCirNode	loc_loop_condition(AstNode source) throws Exception {
		while(source != null) {
			if(source instanceof AstForStatement) {
				return this.find_ast_location(((AstForStatement) source).get_condition());
			}
			else if(source instanceof AstWhileStatement) {
				return this.find_ast_location(((AstWhileStatement) source).get_condition());
			}
			else if(source instanceof AstDoWhileStatement) {
				return this.find_ast_location(((AstDoWhileStatement) source).get_condition());
			}
			else {
				source = source.get_parent();
			}
		}
		throw new IllegalArgumentException("Unable to localize...");
	}
	
	@Override
	protected void generate(AstCirNode location, AstMutation mutation) throws Exception {
		AstCirNode orig_next, muta_next;
		if(mutation.get_operator() == MutaOperator.break_to_continue) {
			orig_next = this.loc_end_statement(mutation.get_location());
			muta_next = this.loc_loop_condition(mutation.get_location());
		}
		else {
			muta_next = this.loc_end_statement(mutation.get_location());
			orig_next = this.loc_loop_condition(mutation.get_location());
		}
		this.put_infection(this.eva_cond(Boolean.TRUE), this.set_flow(orig_next, muta_next));
	}

}
