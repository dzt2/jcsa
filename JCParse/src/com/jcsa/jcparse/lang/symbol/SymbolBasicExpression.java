package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	<code>
 * 	|--	|--	SymbolBasicExpression			(basic expression as the leaf node)					<br>
 * 	|--	|--	|--	SymbolIdentifier			[name: String; scope: Object; identifier: String]	<br>
 * 	|--	|--	|--	SymbolConstant				[constant: CConstant]								<br>
 * 	|--	|--	|--	SymbolLiteral				[literal: String]									<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class SymbolBasicExpression extends SymbolExpression {

	protected SymbolBasicExpression(SymbolClass _class, CType type) throws Exception {
		super(_class, type);
	}

}
