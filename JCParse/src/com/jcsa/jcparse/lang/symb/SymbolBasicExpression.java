package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	The basic expression without children.
 * 	<br>
 * 	<code>
 * 	SymbolNode								[_class, source, parent, children]	<br>
 * 	|--	SymbolExpression					(typed evaluation unit) [data_type]	<br>
 * 	|--	|--	SymbolBasicExpression			(basic expression without child)	<br>
 * 	|--	|--	|--	SymbolIdentifier			[name: String, scope: Object]		<br>
 * 	|--	|--	|--	SymbolConstant				[constant: CConstant]				<br>
 * 	|--	|--	|--	SymbolLiteral				[literal: String]					<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class SymbolBasicExpression extends SymbolExpression {

	protected SymbolBasicExpression(SymbolClass _class, CType data_type) throws IllegalArgumentException {
		super(_class, data_type);
	}

}
