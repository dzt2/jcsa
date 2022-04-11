package com.jcsa.jcmutest.mutant.txt2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstLabel;

public class SGLRMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstLabel label = (AstLabel) source.get_location();
		return label;
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstLabel label = (AstLabel) source.get_parameter();
		return label.get_name();
	}

}
