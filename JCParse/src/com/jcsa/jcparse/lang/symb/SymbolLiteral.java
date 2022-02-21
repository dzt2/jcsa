package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;

public class SymbolLiteral extends SymbolBasicExpression {
	
	/** used to create array type **/
	private static final CTypeFactory t_factory = new CTypeFactory();
	
	/** the content of this string literal **/
	private String literal;
	
	/**
	 * @param type		array(char, length)
	 * @param literal	the content of this string literal
	 * @throws Exception
	 */
	private SymbolLiteral(CType type, String literal) throws Exception {
		super(SymbolClass.literal, type);
		if(literal == null) {
			throw new IllegalArgumentException("Invalid literal: null");
		}
		else {
			this.literal = literal;
		}
	}
	
	/**
	 * @return the content of this string literal
	 */
	public String get_literal() { return this.literal; }

	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolLiteral(this.get_data_type(), this.literal);
	}

	@Override
	protected String get_code(boolean simplified) throws Exception {
		return "\"" + this.literal + "\"";
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param literal	the content of this string literal
	 * @return
	 * @throws Exception
	 */
	protected static SymbolLiteral create(String literal) throws Exception {
		if(literal == null) {
			throw new IllegalArgumentException("Invalid literal: null");
		}
		else {
			CType type;
			if(literal.length() == 0) {
				type = t_factory.get_pointer_type(CBasicTypeImpl.char_type);
			}
			else {
				type = t_factory.get_array_type(CBasicTypeImpl.char_type, literal.length());
			}
			return new SymbolLiteral(type, literal);
		}
	}
	
}
