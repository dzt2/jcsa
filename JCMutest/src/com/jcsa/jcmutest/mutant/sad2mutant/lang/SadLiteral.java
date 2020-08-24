package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * literal |-- {literal: String}
 * @author yukimula
 *
 */
public class SadLiteral extends SadBasicExpression {
	
	/** the literal of the node **/
	private String literal;
	protected SadLiteral(CirNode source, CType data_type, String literal) {
		super(source, data_type);
		this.literal = literal;
	}
	
	/**
	 * @return the literal of the string node
	 */
	public String get_literal() {
		return this.literal;
	}
	
	@Override
	public String generate_code() throws Exception {
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < this.literal.length(); k++) {
			char ch = this.literal.charAt(k);
			switch(ch) {
			case '\b': buffer.append("\\b"); 	break;
			case '\f': buffer.append("\\f"); 	break;
			case '\n': buffer.append("\\n"); 	break;
			case '\r': buffer.append("\\r"); 	break;
			case '\t': buffer.append("\\t"); 	break;
			case '\\': buffer.append("\\\\"); 	break;
			case '\'': buffer.append("\\'"); 	break;
			case '\"': buffer.append("\\\""); 	break;
			default: buffer.append(ch); break;
			}
		}
		return buffer.toString();
	}
	
	@Override
	protected SadNode clone_self() {
		return new SadLiteral(this.get_cir_source(), 
				this.get_data_type(), this.literal);
	}
	
}
