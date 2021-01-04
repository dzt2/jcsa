package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * <code>
 * 	SymbolBasicExpression		[<i>as leaf in SymbolExpression tree</i>]											<br>
 * 	|--	SymbolIdentifier		{name: String}																		<br>
 * 	|--	SymbolConstant			{constant: CConstant}																<br>
 * 	|--	SymbolLiteral			{literal: String}																	<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SymbolBasicExpression extends SymbolExpression {

	protected SymbolBasicExpression(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}

}
