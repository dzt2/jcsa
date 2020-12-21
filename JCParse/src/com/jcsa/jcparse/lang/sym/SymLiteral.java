package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymLiteral extends SymBasicExpression {
	
	/* definitions */
	private String literal;
	private SymLiteral(CType data_type, String literal) throws IllegalArgumentException {
		super(data_type);
		if(literal == null)
			throw new IllegalArgumentException("Invalid literal: null");
		else
			this.literal = literal;
	}
	
	/**
	 * @return the String literal of this node
	 */
	public String get_literal() { return this.literal; }

	@Override
	protected SymNode construct() throws Exception {
		return new SymLiteral(this.get_data_type(), this.literal);
	}
	
	/**
	 * @param data_type
	 * @param literal
	 * @return string literal as symbolic node
	 * @throws Exception
	 */
	protected static SymLiteral create(CType data_type, String literal) throws Exception {
		return new SymLiteral(data_type, literal);
	}
	
}
