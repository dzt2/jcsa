package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymFieldExpression extends SymExpression {

	protected SymFieldExpression(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}
	
	public SymExpression get_body() {
		return (SymExpression) this.get_child(0);
	}
	
	public SymField get_field() {
		return (SymField) this.get_child(1);
	}
	
	@Override
	protected SymNode construct() throws Exception {
		return new SymFieldExpression(this.get_data_type());
	}
	
	@Override
	public String generate_code() throws Exception {
		return "(" + this.get_body().generate_code() + 
				")." + this.get_field().generate_code();
	}

}
