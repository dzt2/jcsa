package com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.ContextMutationParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CTRPContextMutationParser extends ContextMutationParser {
	
	private	AstSwitchStatement find_switch_statement(AstNode source) throws Exception {
		while(source != null) {
			if(source instanceof AstSwitchStatement) {
				return (AstSwitchStatement) source;
			}
			else {
				source = source.get_parent();
			}
		}
		throw new IllegalArgumentException("Unable to locate switch");
	}
	
	private AstCaseStatement find_case_statement(AstNode source) throws Exception {
		while(source != null) {
			if(source instanceof AstCaseStatement) {
				return (AstCaseStatement) source;
			}
			else {
				source = source.get_parent();
			}
		}
		throw new IllegalArgumentException("Unable to locate case");
	}

	@Override
	protected AstCirNode find_reach_location(AstMutation mutation) throws Exception {
		return this.get_location(mutation.get_location());
	}

	@Override
	protected void parse_infection_set(AstCirNode location, AstMutation mutation) throws Exception {
		/* generate the infection condition */
		AstSwitchStatement switch_statement; 
		AstCaseStatement case_statement;
		switch_statement = this.find_switch_statement(mutation.get_location());
		case_statement = this.find_case_statement((AstNode) mutation.get_parameter());
		SymbolExpression condition = SymbolFactory.equal_with(
				switch_statement.get_condition(), case_statement.get_expression());
		
		/* generate the infection pairs */
		this.put_infection(this.eva_cond(condition), this.mut_trap());
	}

}
