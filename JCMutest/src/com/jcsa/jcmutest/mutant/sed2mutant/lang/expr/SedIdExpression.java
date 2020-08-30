package com.jcsa.jcmutest.mutant.sed2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SedIdExpression extends SedBasicExpression {
	
	private String name;
	public SedIdExpression(CirNode cir_source, CType data_type, String name) {
		super(cir_source, data_type);
		this.name = name;
	}
	
	/**
	 * @return the name of the identifier expression
	 */
	public String get_name() { return this.name; }
	
	@Override
	protected SedNode clone_self() {
		return new SedIdExpression(this.get_cir_source(), 
						this.get_data_type(), this.name);
	}
	
	@Override
	public String generate_code() throws Exception {
		return this.name;
	}
	
}
