package com.jcsa.jcmuta.mutant.orig2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

import __backup__.TextMutation;

public class SSWM2MutaTranslator implements Text2MutaTranslator {
	
	private AstSwitchStatement get_location(TextMutation mutation) throws Exception {
		AstNode location = mutation.get_origin();
		while(location != null) {
			if(location instanceof AstSwitchStatement) {
				return (AstSwitchStatement) location;
			}
			else location = location.get_parent();
		}
		throw new IllegalArgumentException("Invalid location: null");
	}
	
	private String extract_parameter(TextMutation mutation) throws Exception {
		String replace = mutation.get_replace();
		int beg = replace.lastIndexOf(',');
		int end = replace.lastIndexOf(')');
		return replace.substring(beg + 1, end).strip();
	}
	
	private AstCaseStatement find_case_statement(AstStatement statement, String parameter) throws Exception {
		if(statement instanceof AstCompoundStatement) {
			if(((AstCompoundStatement) statement).has_statement_list()) {
				AstStatementList list = ((AstCompoundStatement) statement).get_statement_list();
				for(int k = 0; k < list.number_of_statements(); k++) {
					AstCaseStatement solution = this.find_case_statement(list.get_statement(k), parameter);
					if(solution != null) return solution;
				}
				return null;
			}
			else return null;
		}
		else if(statement instanceof AstIfStatement) {
			AstCaseStatement solution;
			solution = this.find_case_statement(((AstIfStatement) statement).get_true_branch(), parameter);
			
			if(solution != null) return solution;
			else if(((AstIfStatement) statement).has_else()) {
				solution = this.find_case_statement(((AstIfStatement) statement).get_false_branch(), parameter);
				return solution;
			}
			else return null;
		}
		else if(statement instanceof AstWhileStatement) {
			return this.find_case_statement(((AstWhileStatement) statement).get_body(), parameter);
		}
		else if(statement instanceof AstDoWhileStatement) {
			return this.find_case_statement(((AstDoWhileStatement) statement).get_body(), parameter);
		}
		else if(statement instanceof AstForStatement) {
			return this.find_case_statement(((AstForStatement) statement).get_body(), parameter);
		}
		else if(statement instanceof AstCaseStatement) {
			if(((AstCaseStatement) statement).get_expression().get_code().equals(parameter)) {
				return (AstCaseStatement) statement;
			}
			else return null;
		}
		else return null;
	}
	
	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstSwitchStatement statement = this.get_location(mutation);
		String parameter = this.extract_parameter(mutation);
		AstCaseStatement replace = this.find_case_statement(statement, parameter);
		return AstMutation.CTRP(statement, replace);
	}

}
