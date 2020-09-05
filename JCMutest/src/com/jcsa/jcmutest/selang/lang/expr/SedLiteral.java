package com.jcsa.jcmutest.selang.lang.expr;

import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class SedLiteral extends SedBasicExpression {
	
	private String literal;
	public SedLiteral(CirExpression cir_expression, 
			CType data_type, String literal) throws Exception {
		super(cir_expression, data_type);
		if(literal == null)
			throw new IllegalArgumentException("Invalid literal");
		else
			this.literal = literal;
	}
	
	/**
	 * @return the literal of the node
	 */
	public String get_literal() { return this.literal; }

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

	@Override
	protected SedNode construct() throws Exception {
		return new SedLiteral(this.get_cir_expression(), 
				this.get_data_type(), this.literal);
	}
	
}
