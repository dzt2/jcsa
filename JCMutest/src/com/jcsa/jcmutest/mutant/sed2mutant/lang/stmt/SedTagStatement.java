package com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * label:
 * @author dzt2
 *
 */
public class SedTagStatement extends SedStatement {

	public SedTagStatement(CirNode cir_source) {
		super(cir_source);
	}

	@Override
	protected String generate_content() throws Exception {
		return "#TAG";
	}

	@Override
	protected SedNode clone_self() {
		return new SedTagStatement(this.get_cir_source());
	}

}
