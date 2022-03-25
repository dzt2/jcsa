package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

/**
 * It represents a string literal in symbolic evaluation.
 * 
 * @author yukimula
 *
 */
public class SymbolLiteral extends SymbolBasicExpression {
	
	/** the text literal included in this node **/
	private String literal;
	
	/**
	 * It creates a string literal node with specified text
	 * @param data_type
	 * @param literal
	 * @throws IllegalArgumentException
	 */
	private SymbolLiteral(CType data_type, String literal) throws IllegalArgumentException {
		super(SymbolClass.literal, data_type);
		if(literal == null) {
			throw new IllegalArgumentException("Invalid literal: null");
		}
		else {
			this.literal = literal;
		}
	}
	
	/**
	 * @return the text literal included in the node
	 */
	public String get_literal() { return this.literal; }

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
	
	/**
	 * @param literal
	 * @return	It creates a string literal node with specified text
	 * @throws IllegalArgumentException
	 */
	protected static SymbolLiteral create(String literal) throws Exception {
		if(literal == null) {
			throw new IllegalArgumentException("Invalid literal: null");
		}
		else {
			CType type = SymbolFactory.type_factory.get_array_type(CBasicTypeImpl.char_type, literal.length() + 1);
			return new SymbolLiteral(type, literal);
		}
	}
	
}
