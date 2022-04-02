package com.jcsa.jcmutest.mutant.cod2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

public class SWDRMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstStatement statement = (AstStatement) source.get_location();
		return statement;
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		if(location instanceof AstWhileStatement) {
			AstWhileStatement statement = (AstWhileStatement) location;
			String condition = statement.get_condition().generate_code();
			String body = statement.get_body().generate_code();
			return "do " + body + " while(" + condition + ");";
		}
		else if(location instanceof AstDoWhileStatement) {
			AstDoWhileStatement statement = (AstDoWhileStatement) location;
			String condition = statement.get_condition().generate_code();
			String body = statement.get_body().generate_code();
			return "while(" + condition + ") " + body;
		}
		else {
			throw new IllegalArgumentException(location.toString());
		}
	}

}
