package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymLiteral extends SymBasicExpression {
	
	private String literal;
	
	protected SymLiteral(CType data_type, String literal) {
		super(data_type);
		this.literal = literal;
	}
	
	/**
	 * @return the text of the string literal
	 */
	public String get_literal() {
		return this.literal;
	}

	@Override
	protected SymNode new_self() {
		return new SymLiteral(this.get_data_type(), this.literal);
	}

	@Override
	protected String generate_code(boolean ast_style) throws Exception {
		StringBuilder buffer = new StringBuilder();
		buffer.append("\"");
		for(int k = 0; k < this.literal.length(); k++) {
			char ch = this.literal.charAt(k);
			switch(ch) {
			case '\b':	buffer.append("\\b");	break;
			case '\t':	buffer.append("\\t");	break;
			case '\n':	buffer.append("\\n");	break;
			case '\r':	buffer.append("\\r");	break;
			case '\0':	buffer.append("\\0");	break;
			default:	buffer.append(ch);		break;
			}
		}
		buffer.append("\"");
		return buffer.toString();
	}
	
}
