package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * field_expression	|--	expression . field
 * @author yukimula
 *
 */
public class SymFieldExpression extends SymExpression {
	
	/**
	 * @param data_type
	 */
	protected SymFieldExpression(CType data_type) {
		super(data_type, CPunctuator.dot);
	}
	
	/**
	 * @return token as .
	 */
	public CPunctuator get_operator() { return (CPunctuator) this.get_token(); }
	/**
	 * @return body of the expression
	 */
	public SymExpression get_body() { return (SymExpression) this.get_child(0); }
	/**
	 * @return field to determine the bias
	 */
	public SymField get_field() { return (SymField) this.get_child(1); }

	@Override
	protected SymNode clone_self() {
		return new SymFieldExpression(this.get_data_type());
	}

	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		return "(" + this.get_body().generate_code(ast_code) + 
				")." + this.get_field().generate_code(ast_code);
	}
	
}
