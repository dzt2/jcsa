package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * literal	--> {String}
 * @author yukimula
 *
 */
public class SymLiteral extends SymBasicExpression {
	
	/**
	 * @param data_type
	 * @param literal
	 */
	protected SymLiteral(CType data_type, String literal) {
		super(data_type, literal);
	}
	
	/**
	 * @return content of the string literal
	 */
	public String get_literal() { return (String) this.get_token(); }

	@Override
	protected SymNode clone_self() {
		return new SymLiteral(this.get_data_type(), this.get_literal());
	}

	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		StringBuilder buffer = new StringBuilder();
		String literal = this.get_literal();
		buffer.append("\"");
		for(int k = 0; k < literal.length(); k++) {
			char ch = literal.charAt(k);
			switch(ch) {
			case '\b':		buffer.append("\\b");	break;
			case '\r':		buffer.append("\\r");	break;
			case '\t':		buffer.append("\\t");	break;
			case '\n':		buffer.append("\\n");	break;
			case '\\':		buffer.append("\\\\");	break;
			case '\'':		buffer.append("\\\'");	break;
			case '\"':		buffer.append("\\\"");	break;
			default:		buffer.append(ch); 		break;
			}
		}
		buffer.append("\"");
		return buffer.toString();
	}
	
}
