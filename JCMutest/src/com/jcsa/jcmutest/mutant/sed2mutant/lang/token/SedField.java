package com.jcsa.jcmutest.mutant.sed2mutant.lang.token;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SedField extends SedToken {

	private String name;
	public SedField(CirNode cir_source, String name) {
		super(cir_source);
		this.name = name;
	}
	
	/**
	 * @return the name of the field
	 */
	public String get_name() { return this.name; }

	@Override
	protected SedNode clone_self() {
		return new SedField(this.get_cir_source(), this.name);
	}

	@Override
	public String generate_code() throws Exception {
		return this.name;
	}
	
}
