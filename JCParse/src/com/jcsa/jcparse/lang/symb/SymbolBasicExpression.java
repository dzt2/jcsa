package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	<code>
 * 	|--	|--	SymbolBasicExpression		(<i>leaf expression used in the computation</i>)				<br>
 * 	|--	|--	|--	SymbolIdentifier		[get_name(): String]											<br>
 * 	|--	|--	|--	SymbolConstant			[get_constant(): CConstant; ...]								<br>
 * 	|--	|--	|--	SymbolLiteral			[get_literal(): String]											<br>
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
