package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	<code>
 * 	SymbolNode								[_class, source, parent, children]	<br>
 * 	|--	SymbolExpression					(typed evaluation unit) [data_type]	<br>
 * 	|--	|--	SymbolSpecialExpression												<br>
 * 	|--	|--	|--	SymbolCastExpression		(cast_expr --> {type_name} expr)	<br>
 * 	|--	|--	|--	SymbolInitializerList		(seq_list --> (expression+))		<br>
 * 	|--	|--	|--	SymbolCallExpression		(call_expr --> expr seq_list)		<br>
 * 	|--	|--	|--	SymbolIfElseExpression		(cond_expr --> expr ? expr : expr)	<br>
 * 	|--	|--	|--	SymbolFieldExpression		(field_expr --> expr.field)			<br>
 * 	|--	|--	|--	SymbolExpressionList		(expr_list --> (expr (, expr)+))	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class SymbolSpecialExpression extends SymbolExpression {

	protected SymbolSpecialExpression(SymbolClass _class, CType data_type) throws IllegalArgumentException {
		super(_class, data_type);
	}

}
