package com.jcsa.jcmutest.mutant.ast2mutant.extend.trap;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.extend.AstMutationExtender;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class STRPMutationExtender extends AstMutationExtender {

	@Override
	public AstMutation coverage_mutation(AstMutation mutation) throws Exception {
		AstStatement statement = (AstStatement) mutation.get_location();
		if(statement instanceof AstBreakStatement
			|| statement instanceof AstContinueStatement
			|| statement instanceof AstGotoStatement
			|| statement instanceof AstLabeledStatement
			|| statement instanceof AstCaseStatement
			|| statement instanceof AstDefaultStatement) {
			return AstMutations.trap_on_statement(statement);
		}
		else if(statement instanceof AstReturnStatement) {
			if(((AstReturnStatement) statement).has_expression()) {
				AstExpression expression = 
						((AstReturnStatement) statement).get_expression();
				expression = CTypeAnalyzer.get_expression_of(expression);
				return AstMutations.trap_on_expression(expression);
			}
			else {
				return AstMutations.trap_on_statement(statement);
			}
		}
		else if(statement instanceof AstDeclarationStatement) {
			return AstMutations.trap_on_statement(statement);
		}
		else if(statement instanceof AstExpressionStatement) {
			if(((AstExpressionStatement) statement).has_expression()) {
				AstExpression expression = ((AstExpressionStatement) statement).get_expression();
				return AstMutations.trap_on_expression(CTypeAnalyzer.get_expression_of(expression));
			}
			else {
				return AstMutations.trap_on_statement(statement);
			}
		}
		else if(statement instanceof AstCompoundStatement) {
			if(((AstCompoundStatement) statement).has_statement_list()) {
				AstStatementList list = 
						((AstCompoundStatement) statement).get_statement_list();
				return AstMutations.trap_on_statement(list.get_statement(0));
			}
			else {
				return AstMutations.trap_on_statement(statement);
			}
		}
		else if(statement instanceof AstIfStatement) {
			AstExpression expression = ((AstIfStatement) statement).get_condition();
			return AstMutations.trap_on_expression(CTypeAnalyzer.get_expression_of(expression));
		}
		else if(statement instanceof AstSwitchStatement) {
			AstExpression expression = ((AstSwitchStatement) statement).get_condition();
			return AstMutations.trap_on_expression(CTypeAnalyzer.get_expression_of(expression));
		}
		else if(statement instanceof AstWhileStatement) {
			AstExpression expression = ((AstWhileStatement) statement).get_condition();
			return AstMutations.trap_on_expression(CTypeAnalyzer.get_expression_of(expression));
		}
		else if(statement instanceof AstDoWhileStatement) {
			if(this.is_empty_statement(((AstDoWhileStatement) statement).get_body())) {
				AstExpression expression = ((AstDoWhileStatement) statement).get_condition();
				return AstMutations.trap_on_expression(CTypeAnalyzer.get_expression_of(expression));
			}
			else {
				AstStatement body = ((AstDoWhileStatement) statement).get_body();
				if(body instanceof AstCompoundStatement) {
					body = ((AstCompoundStatement) body).get_statement_list().get_statement(0);
				}
				return this.coverage_mutation(AstMutations.trap_on_statement(body));
			}
		}
		else if(statement instanceof AstForStatement) {
			AstForStatement for_statement = (AstForStatement) statement;
			if(!this.is_empty_statement(for_statement.get_condition())) {
				AstExpression expression = for_statement.get_condition().get_expression();
				return AstMutations.
						trap_on_expression(CTypeAnalyzer.get_expression_of(expression));
			}
			else {
				AstStatement body = for_statement.get_body();
				if(body instanceof AstCompoundStatement) {
					if(((AstCompoundStatement) body).has_statement_list()) {
						body = ((AstCompoundStatement) body).
								get_statement_list().get_statement(0);
					}
				}
				return this.coverage_mutation(AstMutations.trap_on_statement(body));
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + statement);
		}
	}

	@Override
	public AstMutation weak_mutation(AstMutation mutation) throws Exception {
		return this.coverage_mutation(mutation);
	}

	@Override
	public AstMutation strong_mutation(AstMutation mutation) throws Exception {
		return this.coverage_mutation(mutation);
	}

}
