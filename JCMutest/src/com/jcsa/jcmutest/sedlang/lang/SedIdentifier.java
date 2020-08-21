package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * identifier |-- {name: String}
 * 
 * @author yukimula
 *
 */
public class SedIdentifier extends SedBasicExpression {
	
	/** the name of the identifier expression **/
	private String name;
	protected SedIdentifier(CirNode source, CType data_type, String name) {
		super(source, data_type);
		this.name = name;
	}
	
	/* getter */
	/**
	 * @return the name of the identifier expression
	 */
	public String get_name() { return this.name; }
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedIdentifier(this.get_source(), 
				  this.get_data_type(), this.name);
	}
	@Override
	public String generate_code() throws Exception {
		return this.name;
	}
	
}
