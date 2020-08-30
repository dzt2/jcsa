package com.jcsa.jcmutest.mutant.sed2mutant.lang.token;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

public abstract class SedToken extends SedNode {

	public SedToken(CirNode cir_source) {
		super(cir_source);
	}

}
