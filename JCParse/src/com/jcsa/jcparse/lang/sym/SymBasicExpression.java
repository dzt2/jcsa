package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	|--	|--	SymBasicExpression
 * 	|--	|--	|--	SymIdentifier		{name: String}
 * 	|--	|--	|--	SymConstant			{constant: CConstant}
 * 	|--	|--	|--	SymLiteral			{literal: String}
 * @author yukimula
 *
 */
public abstract class SymBasicExpression extends SymExpression {

	protected SymBasicExpression(CType data_type) {
		super(data_type);
	}

}
