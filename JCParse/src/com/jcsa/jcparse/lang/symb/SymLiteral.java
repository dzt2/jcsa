package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * literal ==> string
 * @author yukimula
 *
 */
public class SymLiteral extends SymBasicExpression {

	protected SymLiteral(CType data_type, String literal) throws IllegalArgumentException {
		super(data_type, literal);
		if(literal == null)
			throw new IllegalArgumentException("Invalid literal: null");
	}
	
	/**
	 * get the literal that the node represents
	 * @return
	 */
	public String get_literal() { return (String) this.value; }
	
	@Override
	public String toString() {
		return "@<" + this.get_literal() + ">";
	}
	
}
