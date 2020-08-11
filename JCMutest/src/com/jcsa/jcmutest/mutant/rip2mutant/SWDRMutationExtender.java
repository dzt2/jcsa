package com.jcsa.jcmutest.mutant.rip2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class SWDRMutationExtender extends MutationExtender {

	@Override
	protected AstMutation coverage_mutation(AstMutation source) throws Exception {
		return this.coverage_at(source.get_location());
	}

	@Override
	protected AstMutation weak_mutation(AstMutation source) throws Exception {
		AstStatement statement = (AstStatement) source.get_location();
		
		AstExpression condition;
		if(statement instanceof AstWhileStatement) {
			condition = ((AstWhileStatement) statement).get_condition();
		}
		else if(statement instanceof AstDoWhileStatement) {
			condition = ((AstDoWhileStatement) statement).get_condition();
		}
		else {
			throw new IllegalArgumentException("Invalid: " + statement);
		}
		condition = CTypeAnalyzer.get_expression_of(condition);
		
		return AstMutations.trap_on_true(condition);
	}

	@Override
	protected AstMutation strong_mutation(AstMutation source) throws Exception {
		return source;
	}

}
