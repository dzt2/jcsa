package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymCallExpression extends SymExpression {

	protected SymCallExpression(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}
	
	public SymExpression get_function() {
		return (SymExpression) this.get_child(0);
	}
	
	public SymArgumentList get_argument_list() {
		return (SymArgumentList) this.get_child(1);
	}

	@Override
	protected SymNode construct() throws Exception {
		return new SymCallExpression(this.get_data_type());
	}

	@Override
	public String generate_code() throws Exception {
		return this.get_function().generate_code() + 
				this.get_argument_list().generate_code();
	}
	

}
