package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class SymFieldExpression extends SymExpression {

	protected SymFieldExpression(CType data_type) {
		super(data_type);
	}
	
	/**
	 * @return get the operator of dot
	 */
	public CPunctuator get_operator() { return CPunctuator.dot; }
	/**
	 * @return the expression as structure body
	 */
	public SymExpression get_body() { 
		return (SymExpression) this.get_child(0); 
	}
	/**
	 * @return the field to reference
	 */
	public SymField get_field() {
		return (SymField) this.get_child(1);
	}

	@Override
	protected SymNode new_self() {
		return new SymFieldExpression(this.get_data_type());
	}

	@Override
	protected String generate_code(boolean ast_style) throws Exception {
		return "(" + this.get_body().generate_code(ast_style) + ")." + 
				this.get_field().generate_code(ast_style);
	}
	
}
