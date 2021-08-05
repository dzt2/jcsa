package com.jcsa.jcmutest.mutant.txt2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

public class STRPMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstStatement statement = (AstStatement) source.get_location();
		return statement;
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstStatement statement = (AstStatement) source.get_location();
		if(statement instanceof AstExpressionStatement) {
			if(((AstExpressionStatement) statement).has_expression()) {
				return "jcm_trap(), " + statement.generate_code();
			}
			else {
				return "jcm_trap();";
			}
		}
		else if(statement instanceof AstDeclarationStatement) {
			return "jcm_trap(); " + statement.generate_code();
		}
		else if(statement instanceof AstCompoundStatement) {
			String code = "{ jcm_trap(); ";
			if(((AstCompoundStatement) statement).has_statement_list()) {
				code += ((AstCompoundStatement) statement).
						get_statement_list().generate_code();
			}
			code += " }";
			return code;
		}
		else if(statement instanceof AstBreakStatement
				|| statement instanceof AstContinueStatement
				|| statement instanceof AstReturnStatement
				|| statement instanceof AstGotoStatement) {
			return "{ jcm_trap(); " + statement.generate_code() + " }";
		}
		else if(statement instanceof AstLabeledStatement
				|| statement instanceof AstCaseStatement
				|| statement instanceof AstDefaultStatement) {
			return statement.generate_code() + " jcm_trap(); ";
		}
		else {
			return "{ jcm_trap(); " + statement.generate_code() + " }";
		}
	}

}
