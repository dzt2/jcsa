package com.jcsa.jcparse.parse.symbolic;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	<code>
 * 	|--	SymbolSpecialExpression																	<br>
 * 	|--	|--	SymbolCastExpression			(cast_expr --> {type_name} expression)				<br>
 * 	|--	|--	SymbolCallExpression			(call_expr --> expression argument_list)			<br>
 * 	|--	|--	SymbolConditionExpression		(cond_expr --> expr ? expr : expr)					<br>
 * 	|--	|--	SymbolInitializerList			(init_list --> {expr (, expr)*})					<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class SymbolSpecialExpression extends SymbolExpression {

	protected SymbolSpecialExpression(SymbolClass _class, CType type) throws Exception {
		super(_class, type);
	}

}
