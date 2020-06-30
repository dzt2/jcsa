package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * basic_expression |-- identifier | constant | literal
 * @author yukimula
 *
 */
public abstract class SymBasicExpression extends SymExpression {

	protected SymBasicExpression(CType data_type, Object token) {
		super(data_type, token);
	}

}
