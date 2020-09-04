package com.jcsa.jcmutest.sedlang.lang.token;

import com.jcsa.jcmutest.sedlang.lang.SedNode;

/**
 * field ==> {name: String}
 * @author yukimula
 *
 */
public class SedField extends SedToken {
	
	private String name;
	public SedField(String name) throws IllegalArgumentException {
		if(name == null || name.isBlank())
			throw new IllegalArgumentException("Invalid name");
		else {
			this.name = name;
		}
	}
	
	/**
	 * @return the name of the field
	 */
	public String get_name() { return this.name; }

	@Override
	public String generate_code() throws Exception {
		return this.name;
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedField(this.name);
	}

}
