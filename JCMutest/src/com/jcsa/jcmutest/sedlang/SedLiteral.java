package com.jcsa.jcmutest.sedlang;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * literal |-- {String}
 * @author yukimula
 *
 */
public class SedLiteral extends SedBasicExpression {
	
	/* definition */
	/** the literal that this node defines **/
	private String literal;
	protected SedLiteral(CirNode source, CType data_type, String literal) {
		super(source, data_type);
		this.literal = literal;
	}
	
	/* getters */
	/**
	 * @return the literal that this node defines
	 */
	public String get_literal() { return this.literal; }

	@Override
	protected SedNode copy_self() {
		return new SedLiteral(this.get_source(), this.get_data_type(), this.literal);
	}
	
	@Override
	protected String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < this.literal.length(); k++) {
			switch(literal.charAt(k)) {
			case '\f':	buffer.append("\\f"); break;
			case '\r':	buffer.append("\\r"); break;
			case '\t':	buffer.append("\\t"); break;
			case '\b':	buffer.append("\\b"); break;
			case '\n':	buffer.append("\\n"); break;
			case '\\':	buffer.append("\\\\"); break;
			case '\'':	buffer.append("\\'"); break;
			case '\"':	buffer.append("\\\""); break;
			case '\0':	buffer.append("\\0"); break;
			default: buffer.append(literal.charAt(k)); break;
			}
		}
		return buffer.toString();
	}
	
	
}
