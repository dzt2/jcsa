package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SadLabelStatement extends SadStatement {

	protected SadLabelStatement(CirNode source) {
		super(source);
	}

	@Override
	protected String generate_content() throws Exception {
		return "";
	}

	@Override
	protected SadNode clone_self() {
		return new SadLabelStatement(this.get_cir_source());
	}

}
