package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * trap_on_true(condition)|trap_on_statement(body)
 * trap_for_time(loop_statement, loop_time)
 * trap_for_time(loop_statement, loop_time)
 *
 * @author yukimula
 *
 */
public class TTRPMutationExtension extends MutationExtension {

	@Override
	protected AstMutation cover(AstMutation source) throws Exception {
		AstNode location = source.get_location();
		if(location instanceof AstWhileStatement) {
			AstWhileStatement statement = (AstWhileStatement) location;
			AstExpression condition = statement.get_condition();
			condition = CTypeAnalyzer.get_expression_of(condition);
			return AstMutations.trap_on_true(condition);
		}
		else if(location instanceof AstDoWhileStatement) {
			AstDoWhileStatement statement = (AstDoWhileStatement) location;
			AstExpression condition = statement.get_condition();
			condition = CTypeAnalyzer.get_expression_of(condition);
			return AstMutations.trap_on_true(condition);
		}
		else {
			AstForStatement statement = (AstForStatement) location;
			if(statement.get_condition().has_expression()) {
				AstExpression condition =
							statement.get_condition().get_expression();
				condition = CTypeAnalyzer.get_expression_of(condition);
				return AstMutations.trap_on_true(condition);
			}
			else {
				return this.coverage_mutation(statement.get_body());
			}
		}
	}

	@Override
	protected AstMutation weak(AstMutation source) throws Exception {
		AstStatement loop_statement = (AstStatement) source.get_location();
		int loop_times = ((Integer) source.get_parameter()).intValue();
		return AstMutations.trap_for_time(loop_statement, loop_times);
	}

	@Override
	protected AstMutation strong(AstMutation source) throws Exception {
		return this.weak(source);
	}

}
