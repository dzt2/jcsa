package com.jcsa.jcmutest.mutant.txt2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

public class SBCRMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstStatement statement = (AstStatement) source.get_location();
		return statement;
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		switch(source.get_operator()) {
		case break_to_continue:	return "continue;";
		case continue_to_break:	return "break;";
		default: throw new IllegalArgumentException(source.toString());
		}
	}

}
