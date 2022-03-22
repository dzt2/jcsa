package com.jcsa.jcparse.lang.symbol;

import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

/**
 * SymbolLiteral				[literal: String]
 * 
 * @author yukimula
 *
 */
public class SymbolLiteral extends SymbolBasicExpression {
	
	/** the literal of this symbolic node **/
	private String literal;
	
	/**
	 * It creates a string literal
	 * @param _class
	 * @param type
	 * @throws Exception
	 */
	private SymbolLiteral(CType type, String literal) throws Exception {
		super(SymbolClass.string_literal, type);
		if(literal == null) {
			throw new IllegalArgumentException("invalid literal");
		}
		this.literal = literal;
	}
	
	/**
	 * @return the literal of the string node
	 */
	public String get_literal() { return this.literal; }
	
	/**
	 * It creates a node representing the string-literal
	 * @param literal
	 * @return
	 * @throws Exception
	 */
	protected static SymbolLiteral create(String literal) throws Exception {
		if(literal == null) {
			throw new IllegalArgumentException("Invalid literal: null");
		}
		else {
			CType type = SymbolFactory.type_factory.get_array_type(
					CBasicTypeImpl.char_type, literal.length() + 1);
			return new SymbolLiteral(type, literal);
		}
	}

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolLiteral(this.get_data_type(), this.literal);
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("\"");
		for(int k = 0; k < this.literal.length(); k++) {
			char ch = this.literal.charAt(k);
			switch(ch) {
			case '\b':	buffer.append("\\b");	break;
			case '\t':	buffer.append("\\t");	break;
			case '\n':	buffer.append("\\n");	break;
			case '\f':	buffer.append("\\f");	break;
			case '\r':	buffer.append("\\r");	break;
			case '\\':	buffer.append("\\\\");	break;
			case '\'':	buffer.append("\\\'");	break;
			case '\"':	buffer.append("\\\"");	break;
			default:	buffer.append(ch);		break;
			}
		}
		buffer.append("\"");
		return buffer.toString();
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	@Override
	protected SymbolExpression symb_replace(Map<SymbolExpression, SymbolExpression> name_value_map) throws Exception {
		return this;
	}
	
}
