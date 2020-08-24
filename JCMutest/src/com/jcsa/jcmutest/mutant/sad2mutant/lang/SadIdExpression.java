package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SadIdExpression extends SadBasicExpression {

	/** the name of the identifier expression **/
	private String name;
	protected SadIdExpression(CirNode source, CType data_type, String name) {
		super(source, data_type);
		this.name = name;
	}
	
	/**
	 * @return the name of the identifier expression
	 */
	public String get_name() { return this.name; }

	@Override
	public String generate_code() throws Exception {
		return this.name;
	}

	@Override
	protected SadNode clone_self() {
		return new SadIdExpression(this.get_cir_source(), 
						this.get_data_type(), this.name);
	}
	
}
