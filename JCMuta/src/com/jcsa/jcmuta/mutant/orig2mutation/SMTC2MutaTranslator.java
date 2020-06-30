package com.jcsa.jcmuta.mutant.orig2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

import __backup__.TextMutation;

public class SMTC2MutaTranslator implements Text2MutaTranslator {

	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstStatement location = 
				(AstStatement) mutation.get_origin().get_parent();
		String replace = mutation.get_replace();
		int beg = replace.indexOf('(');
		int end = replace.indexOf(')');
		int times = Integer.parseInt(replace.substring(beg + 1, end).strip());
		return AstMutation.TTRP(location, times);
	}

}
