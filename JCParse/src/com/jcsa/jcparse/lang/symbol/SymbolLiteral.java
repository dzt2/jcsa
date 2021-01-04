package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;

/**
 * literal ==> {literal: String}
 * @author yukimula
 *
 */
public class SymbolLiteral extends SymbolBasicExpression {
	
	/** content of string literal **/
	private String literal;
	
	/**
	 * construct a literal node w.r.t. String content
	 * @param data_type
	 * @param literal
	 * @throws IllegalArgumentException
	 */
	private SymbolLiteral(CType data_type, String literal) throws IllegalArgumentException {
		super(data_type);
		if(literal == null)
			throw new IllegalArgumentException("Invalid literal: null");
		else 
			this.literal = literal;
	}
	
	/**
	 * @return content of string literal
	 */
	public String get_literal() { return this.literal; }
	
	@Override
	protected SymbolNode construct() throws Exception {
		return new SymbolLiteral(this.get_data_type(), this.get_literal());
	}
	
	private static final CTypeFactory tfactory = new CTypeFactory();
	
	/**
	 * @param literal
	 * @return literal |-- {literal: String}
	 * @throws Exception
	 */
	protected static SymbolLiteral create(String literal) throws Exception {
		int length = literal.length() + 1;
		CType data_type = tfactory.get_array_type(CBasicTypeImpl.char_type, length);
		return new SymbolLiteral(data_type, literal);
	}
	
}
