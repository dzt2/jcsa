package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * field |-- {name: String}
 * @author yukimula
 *
 */
public class SedField extends SedNode {
	
	/* definition */
	/** the name of this field **/
	private String name;
	protected SedField(CirNode source, String name) {
		super(source);
		this.name = name;
	}
	
	/* getter */
	/**
	 * @return the name of this field
	 */
	public String get_name() { return this.name; }

	@Override
	protected SedNode copy_self() {
		return new SedField(this.get_source(), this.name);
	}
	@Override
	public String generate_code() throws Exception {
		return this.name;
	}
	
}
