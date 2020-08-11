package com.jcsa.jcmutest.mutant.ast2mutant.extend;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * It is used to generate the coverage, weak, strong mutation for
 * a given syntactic mutation generated over the source code.
 * 
 * @author yukimuala
 *
 */
public abstract class AstMutationExtender {
	
	/* extension methods */
	/**
	 * @param mutation
	 * @return the mutation is killed once the statement where it is seeded is covered.
	 * @throws Exception
	 */
	public abstract AstMutation coverage_mutation(AstMutation mutation) throws Exception;
	/**
	 * @param mutation
	 * @return the mutation is killed once the state error is caused during testing.
	 * @throws Exception
	 */
	public abstract AstMutation weak_mutation(AstMutation mutation) throws Exception;
	/**
	 * @param mutation
	 * @return the mutation is killed iff. the given mutation can be killed, this will
	 * 		   provide a standard version of original mutation.
	 * @throws Exception
	 */
	public abstract AstMutation strong_mutation(AstMutation mutation) throws Exception;
	
	/* utility methods */
	/**
	 * @param mutation
	 * @return the expression in which the mutation occurs.
	 * @throws Exception
	 */
	protected AstExpression get_expression_of(AstMutation mutation) throws Exception {
		AstNode location = mutation.get_location();
		if(location instanceof AstExpression) {
			return CTypeAnalyzer.get_expression_of((AstExpression) location);
		}
		else if(location instanceof AstExpressionStatement) {
			if(((AstExpressionStatement) location).has_expression()) {
				return CTypeAnalyzer.get_expression_of(
						((AstExpressionStatement) location).get_expression());
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	/**
	 * @param statement
	 * @return empty statement or empty block
	 * @throws Exception
	 */
	protected boolean is_empty_statement(AstStatement statement) throws Exception {
		if(statement instanceof AstExpressionStatement) {
			return !((AstExpressionStatement) statement).has_expression();
		}
		else if(statement instanceof AstCompoundStatement) {
			return !((AstCompoundStatement) statement).has_statement_list();
		}
		else {
			return false;
		}
	}
	
	
}
