package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymLiteral extends SymBasicExpression {
	
	/* definitions */
	private String literal;
	protected SymLiteral(CType data_type, String literal) throws IllegalArgumentException {
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

	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("\"");
		for(int k = 0; k < literal.length(); k++) {
			char ch = literal.charAt(k);
			switch(ch) {
			case '\b':	buffer.append("\\b");	break;
			case '\f':	buffer.append("\\f");	break;
			case '\n':	buffer.append("\\n");	break;
			case '\r':	buffer.append("\\r");	break;
			case '\t':	buffer.append("\\t");	break;
			case '\\':	buffer.append("\\\\");	break;
			case '\'':	buffer.append("\\\'");	break;
			case '\"':	buffer.append("\\\"");	break;
			case '\0':	buffer.append("\\0");	break;
			default:	buffer.append(ch); 		break;
			}
		}
		buffer.append("\"");
		return buffer.toString();
	}
	
}
